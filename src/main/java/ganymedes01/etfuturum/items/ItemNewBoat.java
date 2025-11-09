package ganymedes01.etfuturum.items;

import com.google.common.collect.Maps;
import ganymedes01.etfuturum.entities.EntityNewBoat;
import ganymedes01.etfuturum.entities.EntityNewBoatWithChest;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ItemNewBoat extends BaseItem {

    @Deprecated
    private EntityNewBoat.Type type;
	private final boolean isChest;

	private final String name;
	private final String domain;

	public static final Map<String, BoatInfo> BOAT_INFO = Maps.newHashMap();

	@Deprecated
	public ItemNewBoat(EntityNewBoat.Type type, boolean isChest) {
		super(type.getName() + (isChest ? "_chest_boat" : "_boat"));
		this.type = type;
		this.name = type.getName();
		this.domain = "minecraft";
		this.isChest = isChest;
		BOAT_INFO.put("minecraft:" + type.getName().toLowerCase() + (isChest ? "_chest" : ""),
				new BoatInfo(new ItemStack(this), () -> Item.getItemFromBlock(Blocks.planks), type.ordinal(), false));
		setMaxStackSize(1);
	}

	public ItemNewBoat(String domain, String name, Supplier<Item> plank, int plankMeta, boolean isChest, boolean isRaft) {
		super(name + (isChest ? "_chest" : "") + (isRaft ? "_raft" : "_boat"));
		this.name = name;
		this.domain = domain;
		this.isChest = isChest;
		BOAT_INFO.put(domain + ":" + name + (isChest ? "_chest" : ""), new BoatInfo(new ItemStack(this), plank, plankMeta, isRaft));
		setMaxStackSize(1);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) {
		float f = 1.0F;
		float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
		float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
		double d0 = player.prevPosX + (player.posX - player.prevPosX) * (double) f;
		double d1 = player.prevPosY + (player.posY - player.prevPosY) * (double) f + 1.62D - (double) player.yOffset;
		double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) f;
		Vec3 vec3 = Vec3.createVectorHelper(d0, d1, d2);
		float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
		float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
		float f5 = -MathHelper.cos(-f1 * 0.017453292F);
		float f6 = MathHelper.sin(-f1 * 0.017453292F);
		float f7 = f4 * f5;
		float f8 = f3 * f5;
		double d3 = 5.0D;
		Vec3 vec31 = vec3.addVector((double) f7 * d3, (double) f6 * d3, (double) f8 * d3);
		MovingObjectPosition movingobjectposition = worldIn.rayTraceBlocks(vec3, vec31, true);

		if (movingobjectposition == null) {
			return itemStackIn;
		}
		Vec3 vec32 = player.getLook(f);
		boolean flag = false;
		float f9 = 1.0F;
		List<Entity> list = worldIn.getEntitiesWithinAABBExcludingEntity(player, player.boundingBox.addCoord(vec32.xCoord * d3, vec32.yCoord * d3, vec32.zCoord * d3).expand(f9, f9, f9));
		int i;

		for (i = 0; i < list.size(); ++i) {
			Entity entity = list.get(i);

			if (entity.canBeCollidedWith()) {
				float f10 = entity.getCollisionBorderSize();
				AxisAlignedBB axisalignedbb = entity.boundingBox.expand(f10, f10, f10);

				if (axisalignedbb.isVecInside(vec3)) {
					flag = true;
				}
			}
		}

		if (flag) {
			return itemStackIn;
		}
		if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
			i = movingobjectposition.blockX;
			int j = movingobjectposition.blockY;
			int k = movingobjectposition.blockZ;

			if (worldIn.getBlock(i, j, k) == Blocks.snow_layer) {
				--j;
			}

			EntityNewBoat entityboat;
			if (isChest) {
				entityboat = new EntityNewBoatWithChest(worldIn);
			} else {
				entityboat = new EntityNewBoat(worldIn);
			}
			boolean isWater = worldIn.getBlock(i, j, k).getMaterial() == Material.water;
			entityboat.setPositionAndRotation(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord + (isWater ? -0.12 : 0), movingobjectposition.hitVec.zCoord, player.rotationYaw, 0);
			entityboat.motionX = entityboat.motionY = entityboat.motionZ = 0;
			entityboat.setBoatType(domain, name);
			if (itemStackIn.hasDisplayName()) {
				entityboat.setBoatName(itemStackIn.getDisplayName());
			}

			if (!worldIn.getCollidingBoundingBoxes(entityboat, entityboat.boundingBox.expand(-0.1D, -0.1D, -0.1D)).isEmpty()) {
				return itemStackIn;
			}

			if (!worldIn.isRemote) {
				worldIn.spawnEntityInWorld(entityboat);
			}

			if (!player.capabilities.isCreativeMode) {
				--itemStackIn.stackSize;
			}
		}

		return itemStackIn;
	}

	public class BoatInfo {
		private final ItemStack boatItem;
		private final Supplier<Item> plank;
		private final int plankMeta;
		private final boolean raft;

		public BoatInfo(ItemStack boatItem, Supplier<Item> plank, int plankMeta, boolean raft) {
			this.boatItem = boatItem;
			this.plank = plank;
			this.plankMeta = plankMeta;
			this.raft = raft;
		}

		public ItemStack getBoatItem() {
			return boatItem.copy();
		}

		public ItemStack getPlank() {
			return new ItemStack(plank.get(), 1, plankMeta);
		}

		public boolean isRaft() {
			return raft;
		}
	}
}
