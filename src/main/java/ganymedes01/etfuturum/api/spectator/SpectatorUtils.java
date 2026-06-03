package ganymedes01.etfuturum.api.spectator;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.core.utils.helpers.SafeEnumHelperClient;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.common.util.FakePlayer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import roadhog360.hogutils.api.hogtags.helpers.BlockTags;

@ApiStatus.AvailableSince("3.0.0")
public class SpectatorUtils {
    @ApiStatus.AvailableSince("3.0.0")
    public static final IEntitySelector EXCEPT_SPECTATING;
    @ApiStatus.AvailableSince("3.0.0")
    public static final WorldSettings.GameType SPECTATOR_GAMETYPE;

    static {
        EXCEPT_SPECTATING = p_82704_1_ -> !isSpectator(p_82704_1_);
        SPECTATOR_GAMETYPE = SafeEnumHelperClient.addGameType("spectator", 3, "Spectator");
    }

    @ApiStatus.AvailableSince("3.0.0")
    public static boolean isSpectator(Entity entity) {
        if(entity instanceof EntityPlayerMP player) {
            if(!(player instanceof FakePlayer) && player.worldObj != null) {
                return player.theItemInWorldManager.getGameType() == SpectatorUtils.SPECTATOR_GAMETYPE;
            }
        } else if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            if(FMLClientHandler.instance().getWorldClient() != null && FMLClientHandler.instance().getClientPlayerEntity() == entity) {
                return FMLClientHandler.instance().getClient().playerController.currentGameType == SpectatorUtils.SPECTATOR_GAMETYPE;
            }
        }
        return false;
    }

    @ApiStatus.AvailableSince("3.0.0")
    public static boolean wasSpectator(Entity entity) {
        return entity instanceof ISpectatorInfo info && info.etfu$wasSpectator();
    }

    @ApiStatus.AvailableSince("3.0.0")
    @Nullable
    public static Entity getSpectatingEntity(Entity entity) {
        return entity instanceof ISpectatorInfo info ? info.etfu$spectatingEntity() : null;
    }

    @ApiStatus.AvailableSince("3.0.0")
    public static boolean canSpectatorSelectTileEntity(TileEntity te) {
        return (te instanceof IInventory || te instanceof TileEntityEnderChest)
                && !BlockTags.hasTag(te.getBlockType(), te.getBlockMetadata(), Tags.MOD_ID + ":spectators_cannot_interact");
    }
}
