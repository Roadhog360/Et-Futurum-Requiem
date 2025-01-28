package ganymedes01.etfuturum.tileentities;

import ganymedes01.etfuturum.core.utils.helpers.BlockPos;
import ganymedes01.etfuturum.world.end.gen.WorldGenEndGateway;
import ganymedes01.etfuturum.world.end.gen.WorldGenEndIsland;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roadhog360.hogutils.api.utils.GenericUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class TileEntityGateway extends TileEntity {
	private static final Logger LOG = LogManager.getLogger();
	private long age = 0L;
	private int teleportCooldown = 0;
	private BlockPos exitPortal;
	private boolean exactTeleport;

	@Override
	public void writeToNBT(NBTTagCompound p_189515_1_) {
		super.writeToNBT(p_189515_1_);
		write(p_189515_1_);
	}

	private NBTTagCompound write(NBTTagCompound p_189515_1_) {
		super.writeToNBT(p_189515_1_);
		p_189515_1_.setLong("Age", this.age);

		if (this.exitPortal != null) {
			NBTTagCompound coords = new NBTTagCompound();
			coords.setInteger("X", exitPortal.getX());
			coords.setInteger("Y", exitPortal.getY());
			coords.setInteger("Z", exitPortal.getZ());
			p_189515_1_.setTag("ExitPortal", coords);
		}

		if (this.exactTeleport) {
			p_189515_1_.setBoolean("ExactTeleport", this.exactTeleport);
		}
		return p_189515_1_;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.age = compound.getLong("Age");

		if (compound.hasKey("ExitPortal")) {
			NBTTagCompound coords = compound.getCompoundTag("ExitPortal");
			this.exitPortal = new BlockPos(coords.getInteger("X"), coords.getInteger("Y"), coords.getInteger("Z"));
		}
		this.exactTeleport = compound.getBoolean("ExactTeleport");
	}

	@Override
	public double getMaxRenderDistanceSquared() {
		return 65536.0D;
	}

	@Override
	public void updateEntity() {
		boolean flag = this.isSpawning();
		boolean flag1 = this.isCoolingDown();
		++this.age;

		if (flag1) {
			--this.teleportCooldown;
		} else if (!this.worldObj.isRemote) {
			List<Entity> list = this.worldObj.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1));

			if (!list.isEmpty()) {
				Entity entity = list.get(0);
				if (entity instanceof EntityEnderPearl) {
					EntityEnderPearl pearl = (EntityEnderPearl) entity;
					entity = pearl.getThrower();
					pearl.setPosition(xCoord + .5D, yCoord + .5D, zCoord + .5D);
					pearl.setDead();
				}
				this.teleportEntity(entity);
			}
		}

		if (flag != this.isSpawning() || flag1 != this.isCoolingDown()) {
			this.markDirty();
		}
	}

	public boolean isSpawning() {
		return this.age < 200L;
	}

	public boolean isCoolingDown() {
		return this.teleportCooldown > 0;
	}

	public float getSpawnPercent() {
		return MathHelper.clamp_float((float) this.age / 200.0F, 0.0F, 1.0F);
	}

	public float getCooldownPercent() {
		return 1.0F - MathHelper.clamp_float((float) this.teleportCooldown / 20.0F, 0.0F, 1.0F);
	}

	@Override
	public Packet getDescriptionPacket() {
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 8, this.getUpdateTag());
	}

	public NBTTagCompound getUpdateTag() {
		return this.write(new NBTTagCompound());
	}

	public void triggerCooldown() {
		if (!this.worldObj.isRemote) {
			this.teleportCooldown = 20;
			this.worldObj.addBlockEvent(xCoord, yCoord, zCoord, this.getBlockType(), 1, 0);
			this.markDirty();
		}
	}

	@Override
	public boolean receiveClientEvent(int id, int type) {
		if (id == 1) {
			this.teleportCooldown = 20;
			return true;
		}
		return super.receiveClientEvent(id, type);
	}

	public void teleportEntity(Entity entityIn) {
		if (!this.worldObj.isRemote && !this.isCoolingDown()) {
			this.teleportCooldown = 100;

			if (this.exitPortal == null && this.worldObj.provider instanceof WorldProviderEnd) {
				this.findExitPortal();
			}

			if (this.exitPortal != null) {
				BlockPos blockpos = this.exactTeleport ? this.exitPortal : this.findExitPosition();

				if (entityIn instanceof EntityLivingBase) {
					((EntityLivingBase) entityIn).setPositionAndUpdate((double) blockpos.getX() + 0.5D, (double) blockpos.getY() + 0.5D, (double) blockpos.getZ() + 0.5D);
				} else {
					entityIn.setPosition((double) blockpos.getX() + 0.5D, (double) blockpos.getY() + 0.5D, (double) blockpos.getZ() + 0.5D);
				}
				entityIn.worldObj.updateEntityWithOptionalForce(entityIn, false);
			}

			this.triggerCooldown();
		}
	}

	private BlockPos findExitPosition() {
		BlockPos blockpos = findHighestBlock(this.worldObj, this.exitPortal, 5, false);
		LOG.debug("Best exit position for portal at " + this.exitPortal + " is " + blockpos);
		return blockpos.up();
	}

	private void findExitPortal() {
		Vec3 vec3d = (Vec3.createVectorHelper(xCoord, 0.0D, zCoord)).normalize();
		Vec3 vec3d1 = Vec3.createVectorHelper(vec3d.xCoord * 1024.0D, vec3d.yCoord * 1024.0D, vec3d.zCoord * 1024.0D);

		for (int i = 16; getChunk(this.worldObj, vec3d1).getTopFilledSegment() > 0 && i-- > 0; vec3d1 = vec3d1.addVector(vec3d.xCoord * -16.0D, vec3d.yCoord * -16.0D, vec3d.zCoord * -16.0D)) {
			LOG.debug("Skipping backwards past nonempty chunk at " + vec3d1);
		}

		for (int j = 16; getChunk(this.worldObj, vec3d1).getTopFilledSegment() == 0 && j-- > 0; vec3d1 = vec3d1.addVector(vec3d.xCoord * 16.0D, vec3d.yCoord * 16.0D, vec3d.zCoord * 16.0D)) {
			LOG.debug("Skipping forward past empty chunk at " + vec3d1);
		}

		LOG.debug("Found chunk at " + vec3d1);
		Chunk chunk = getChunk(this.worldObj, vec3d1);
		this.exitPortal = findSpawnpointInChunk(chunk);

		if (this.exitPortal == null) {
			this.exitPortal = new BlockPos(vec3d1.xCoord + 0.5D, 75.0D, vec3d1.zCoord + 0.5D);
			LOG.debug("Failed to find suitable block, settling on " + this.exitPortal);
			(new WorldGenEndIsland()).generate(this.worldObj, new Random(this.exitPortal.toLong()), exitPortal.getX(), exitPortal.getY(), exitPortal.getZ());
		} else {
			LOG.debug("Found block at " + this.exitPortal);
		}

		this.exitPortal = findHighestBlock(this.worldObj, this.exitPortal, 16, true);
		LOG.debug("Creating portal at " + this.exitPortal);
		this.exitPortal = this.exitPortal.up(10);
		this.createExitPortal(this.exitPortal);
		this.markDirty();
	}

	private static BlockPos findHighestBlock(World p_184308_0_, BlockPos p_184308_1_, int p_184308_2_, boolean p_184308_3_) {
		BlockPos blockpos = null;

		for (int i = -p_184308_2_; i <= p_184308_2_; ++i) {
			for (int j = -p_184308_2_; j <= p_184308_2_; ++j) {
				if (i != 0 || j != 0 || p_184308_3_) {
					for (int k = 255; k > (blockpos == null ? 0 : blockpos.getY()); --k) {
						Block iblockstate = p_184308_0_.getBlock(p_184308_1_.getX() + i, k, p_184308_1_.getZ() + j);

						if (iblockstate.isBlockNormalCube() && (p_184308_3_ || iblockstate != Blocks.bedrock)) {
							blockpos = new BlockPos(p_184308_1_.getX() + i, k, p_184308_1_.getZ() + j);
							break;
						}
					}
				}
			}
		}

		return blockpos == null ? p_184308_1_ : blockpos;
	}

	private static Chunk getChunk(World worldIn, Vec3 vec3) {
		return worldIn.getChunkFromChunkCoords(MathHelper.floor_double(vec3.xCoord / 16.0D), MathHelper.floor_double(vec3.zCoord / 16.0D));
	}

	@Nullable
	private static BlockPos findSpawnpointInChunk(Chunk chunkIn) {
		int i = chunkIn.getTopFilledSegment() + 16 - 1;
		int xfrom = chunkIn.xPosition * 16;
		int yfrom = 30;
		int zfrom = chunkIn.zPosition * 16;
		int xto = chunkIn.xPosition * 16 + 16 - 1;
		int yto = i;
		int zto = chunkIn.zPosition * 16 + 16 - 1;
		BlockPos blockpos2 = null;
		double d0 = 0.0D;

		for (int x = xfrom; x <= xto; x++) {
			for (int y = yfrom; y <= yto; y++) {
				for (int z = zfrom; z <= zto; z++) {
					if (chunkIn.worldObj.getBlock(x, y, z) == Blocks.end_stone &&
							!chunkIn.worldObj.getBlock(x, y + 1, z).isNormalCube() && !chunkIn.worldObj.getBlock(x, y + 2, z).isNormalCube()) {
						double dis1 = x + 0.5D;
						double dis2 = y + 0.5D;
						double dis3 = z + 0.5D;
						double d1 = dis1 * dis1 + dis1 * dis2 + dis3 * dis3;

						if (blockpos2 == null || d1 < d0) {
							blockpos2 = new BlockPos(x, y, z);
							d0 = d1;
						}
					}
				}
			}
		}

		return blockpos2;
	}

	private void createExitPortal(BlockPos posIn) {
		new WorldGenEndGateway().generate(this.worldObj, new Random(), posIn.getX(), posIn.getY(), posIn.getZ());
		TileEntity tileentity = this.worldObj.getTileEntity(posIn.getX(), posIn.getY(), posIn.getZ());

		if (tileentity instanceof TileEntityGateway) {
			TileEntityGateway tileentityendgateway = (TileEntityGateway) tileentity;
			tileentityendgateway.exitPortal = new BlockPos(xCoord, yCoord, zCoord);
			tileentityendgateway.markDirty();
		} else {
			LOG.warn("Couldn't save exit portal at " + posIn);
		}
	}

	public boolean shouldRenderFace(EnumFacing p_184313_1_) {
		return this.getBlockType().shouldSideBeRendered(this.worldObj, xCoord, yCoord, zCoord, p_184313_1_.ordinal());
	}

	public int getParticleAmount() {
		int i = 0;

		for (EnumFacing enumfacing : GenericUtils.Constants.ENUM_FACING_VALUES) {
			if (this.shouldRenderFace(enumfacing))
				i++;
		}

		return i;
	}
}
