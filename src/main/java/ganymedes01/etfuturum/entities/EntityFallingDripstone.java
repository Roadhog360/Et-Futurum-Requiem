package ganymedes01.etfuturum.entities;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.blocks.BlockPointedDripstone;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public class EntityFallingDripstone extends EntityFallingBlock implements IEntityAdditionalSpawnData {

	private int fallHurtMax = 40;
	private float fallHurtAmount = 4.0F;
	private boolean hurtEntities = true;
	private int count = 1;

	public EntityFallingDripstone(World world) {
		super(world);
	}

	public EntityFallingDripstone(World world, double x, double y, double z, int meta, int count) {
		super(world, x, y, z, ModBlocks.POINTED_DRIPSTONE.get(), meta);
		this.count = count;
		this.fallHurtAmount = Math.max(count, 6); // 1 HP per piece (min 6) per block fallen
		// The entity is anchored to the topmost block and spans `count` blocks downward,
		// so yOffset and size must be updated before setPosition recomputes the bounding box.
		this.yOffset = count - 0.5F;
		this.setSize(0.98F, count);
		this.setPosition(x, y, z);
	}

	public int getCount() {
		return count;
	}

	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		++this.field_145812_b; // fallTime
		this.motionY -= 0.04D;
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		this.motionX *= 0.98D;
		this.motionY *= 0.98D;
		this.motionZ *= 0.98D;

		if (this.worldObj.isRemote) return;

		int x = MathHelper.floor_double(this.posX);
		int y = MathHelper.floor_double(this.posY);
		int z = MathHelper.floor_double(this.posZ);

		if (this.onGround) {
			this.motionX *= 0.7D;
			this.motionZ *= 0.7D;
			this.motionY *= -0.5D;
			dropItems();
			this.setDead();
		} else if ((this.field_145812_b > 100 && (y < 1 || y > 256)) || this.field_145812_b > 600) {
			dropItems();
			this.setDead();
		}
	}

	private void dropItems() {
		if (!this.field_145813_c) return; // shouldDropItem
		Block block = ModBlocks.POINTED_DRIPSTONE.get();
		this.entityDropItem(new ItemStack(block, this.count, block.damageDropped(this.field_145814_a)), 0.5F);
	}

	@Override
	protected void fall(float distance) {
		if (!this.hurtEntities) return;
		int blocks = MathHelper.ceiling_float_int(distance - 1.0F);
		if (blocks <= 0) return;

		float baseDmg = Math.min(blocks * this.fallHurtAmount, this.fallHurtMax);
		List<Entity> entities = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox);
		for (Entity entity : entities) {
			float dmg = baseDmg;
			if (entity instanceof EntityLivingBase) {
				ItemStack helmet = ((EntityLivingBase) entity).getEquipmentInSlot(4);
				if (helmet != null) {
					dmg *= 0.75F; // helmet reduces damage by 1/4
					helmet.damageItem(2, (EntityLivingBase) entity); // 2× durability cost
				}
			}
			entity.attackEntityFrom(BlockPointedDripstone.STALACTITE_DAMAGE, dmg);
		}
	}

	@Override
	public void writeSpawnData(ByteBuf buf) {
		buf.writeInt(this.count);
	}

	@Override
	public void readSpawnData(ByteBuf buf) {
		this.count = buf.readInt();
		this.yOffset = count - 0.5F;
		this.setSize(0.98F, this.count);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		tag.setBoolean("HurtEntities", this.hurtEntities);
		tag.setFloat("FallHurtAmount", this.fallHurtAmount);
		tag.setInteger("FallHurtMax", this.fallHurtMax);
		tag.setInteger("Count", this.count);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		this.hurtEntities = tag.getBoolean("HurtEntities");
		this.fallHurtMax = tag.getInteger("FallHurtMax");
		this.count = tag.getInteger("Count");
		this.fallHurtAmount = Math.max(this.count, 6); // always derived from count
		this.yOffset = count - 0.5F;
		this.setSize(0.98F, this.count);
	}
}
