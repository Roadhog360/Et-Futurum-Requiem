package ganymedes01.etfuturum.spectator;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import ganymedes01.etfuturum.api.spectator.ISpectatorInfo;
import ganymedes01.etfuturum.api.spectator.SpectatorUtils;
import ganymedes01.etfuturum.configuration.configs.ConfigMixins;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldSettings;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

@Deprecated
public class SpectatorMode {
	@Deprecated
	public static final IEntitySelector EXCEPT_SPECTATING = SpectatorUtils.EXCEPT_SPECTATING;
	@Deprecated
	public static final Map<EntityPlayer, Entity> SPECTATING_ENTITIES = new FakeSpectatorMap();
	@Deprecated
	public static final WorldSettings.GameType SPECTATOR_GAMETYPE = SpectatorUtils.SPECTATOR_GAMETYPE;

	@EventBusSubscriber.Condition
	public static boolean condition() {
		return ConfigMixins.enableSpectatorMode;
	}

	protected SpectatorMode() {
	}

	public static void init() {
	}

	@Deprecated
	public static boolean isSpectator(EntityPlayer player) {
        return SpectatorUtils.isSpectator(player);
    }

	@Deprecated
	public static boolean canSpectatorSelect(TileEntity te) {
		return SpectatorUtils.canSpectatorSelectTileEntity(te);
	}

	// Skeletal dummy map in case any mods actually checked for spectating entity data.
	private static class FakeSpectatorMap extends AbstractMap<EntityPlayer, Entity> {

		@Override
		public @NotNull Set<Entry<EntityPlayer, Entity>> entrySet() {
			throw new RuntimeException("Deprecated. If you were using this, open an issue report and I will find a way to fix this.");
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public Entity get(Object key) {
			if(key instanceof ISpectatorInfo info) {
				return info.etfu$spectatingEntity();
			}
			return null;
		}

		@Override
		public boolean containsKey(Object key) {
			if(key instanceof ISpectatorInfo info) {
				return info.etfu$spectatingEntity() != null;
			}
			return false;
		}

		@Override
		public boolean containsValue(Object value) {
			return false;
		}
	}
}
