package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.client.renderer.block.BlockRenderers;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.entities.EntityNewBoat;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockHoney extends BaseEFRBlock {

	private IIcon bottomIcon;
	private IIcon topIcon;

	public BlockHoney() {
		super(Material.clay);
		setNames("honey_block");
		setStepSound(ModSounds.soundHoneyBlock);
	}

	/**
	 * TODO
	 * showJumpParticles and showSlideParticles use a packet in modern. I really don't see any reason to implement a packet for this.
	 * That's why watching other entities slide on honey won't have particles. Keeping this here for reference.
	 * Also items often fall off of the honey block on the clientside
	 */


	@Override
	public void onFallenUpon(World world, int x, int y, int z, Entity entity, float fallDistance) {
		world.playSoundAtEntity(entity, Tags.MC_ASSET_VER + ":block.honey_block.slide", 1.0F, 1.0F);
		showJumpParticles(entity);
		entity.fallDistance *= 0.2F;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		if (this.isSlidingDown(entity, x, y, z)) {
//          this.maybeDoSlideAchievement(entity, x, y, z);
			this.doSlideMovement(entity);
			this.maybeDoSlideEffects(world, entity);
		}
		if (entity.onGround) {
			entity.motionX *= 0.4D;
			entity.motionZ *= 0.4D;
		}
		super.onEntityCollidedWithBlock(world, x, y, z, entity);
	}

	private boolean isSlidingDown(Entity p_54009_, int x, int y, int z) {
		if (p_54009_.onGround || p_54009_.posY > (double) y + 0.9375D - 1.0E-7D || p_54009_.motionY >= -0.08D) {
			return false;
		} else {
			double d0 = Math.abs(x + 0.5D - p_54009_.posX);
			double d1 = Math.abs(z + 0.5D - p_54009_.posZ);
			double d2 = 0.4375D + (p_54009_.width * 0.5D);
			return d0 + 1.0E-7D > d2 || d1 + 1.0E-7D > d2;
		}
	}

//  private void maybeDoSlideAchievement(Entity p_53992_, int x, int y, int z) {
//      if (p_53992_ instanceof ServerPlayer && p_53992_.level().getGameTime() % 20L == 0L) {
//          CriteriaTriggers.HONEY_BLOCK_SLIDE.trigger((ServerPlayer)p_53992_, p_53992_.level().getBlockState(p_53993_));
//      }
//  }

	private void doSlideMovement(Entity p_54020_) {
		if (p_54020_.motionY < -0.13D) {
			double d0 = -0.05D / p_54020_.motionY;
			p_54020_.motionX *= d0;
			p_54020_.motionZ *= d0;
		}
		p_54020_.motionY = -0.05D;
		p_54020_.fallDistance = 0;
	}

	private void maybeDoSlideEffects(World world, Entity entity) {
		if (doesEntityDoHoneyBlockSlideEffects(entity)) {
			if (world.rand.nextInt(5) == 0) {
				world.playSoundAtEntity(entity, Tags.MC_ASSET_VER + ":block.honey_block.slide", 1.0F, 1.0F);
				showSlideParticles(entity);
			}
		}

	}

	public static void showSlideParticles(Entity p_53987_) {
		showParticles(p_53987_, 5);
	}

	public static void showJumpParticles(Entity p_54011_) {
		showParticles(p_54011_, 10);
	}

	private static void showParticles(Entity p_53989_, int p_53990_) {
		if (p_53989_.worldObj.isRemote) {
			for (int i = 0; i < p_53990_; ++i) {
				p_53989_.worldObj.spawnParticle("blockcrack_" + Block.getIdFromBlock(ModBlocks.HONEY_BLOCK.get()) + "_0",
						p_53989_.posX, p_53989_.posY - p_53989_.height, p_53989_.posZ, 0, 0, 0);
			}
		}
	}

	private static boolean doesEntityDoHoneyBlockSlideEffects(Entity p_54013_) {
		return p_54013_ instanceof EntityLivingBase || p_54013_ instanceof EntityMinecart || p_54013_ instanceof EntityTNTPrimed
				|| p_54013_ instanceof EntityBoat || p_54013_ instanceof EntityNewBoat;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		float f = 0.0625F;
		return AxisAlignedBB.getBoundingBox(x + f, y + f, z + f, x + 1 - f, y + 1 - f, z + 1 - f);
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return side == 0 ? bottomIcon : side == 1 ? topIcon : blockIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		this.blockIcon = reg.registerIcon(getTextureName() + "_side");
		this.topIcon = reg.registerIcon(getTextureName() + "_top");
		this.bottomIcon = reg.registerIcon(getTextureName() + "_bottom");
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public int getRenderType() {
		return BlockRenderers.HONEY_BLOCK.getRenderId();
	}
}
