package ganymedes01.etfuturum.core.handlers.client;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import ganymedes01.etfuturum.blocks.BlockBubbleColumn;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import org.jetbrains.annotations.Nullable;

public class BubbleColumnSoundEventHandler {
	private BubbleColumnSoundEventHandler() {};
	public static final BubbleColumnSoundEventHandler INSTANCE = new BubbleColumnSoundEventHandler();

	private BlockBubbleColumn column;

	@SubscribeEvent
	public void postPlayerTick(TickEvent.PlayerTickEvent e) {
		if (e.player == FMLClientHandler.instance().getClientPlayerEntity()) {
			EntityPlayer player = e.player;

//			if(!player.isInWater() && column == null) return;

			if (player.ticksExisted % 3 == 0) { // Sample only every 3rd tick for performance reasons
				BlockBubbleColumn newColumn = sampleBubbleColumn(player);
				if (newColumn != column) {
					if (column != null && newColumn == null) {
						// Second condition prevents sound from being played when player transfers directly from one column to another
						// Keeping this note here in case modern changes this, so I can remove that check
						String sound = column.getExitNoise(player);
						if (sound != null) {
							player.playSound(sound, 1, 1);
						}
					}
					if(newColumn != null && column == null) {
						// Second condition prevents sound from being played when player transfers directly from one column to another
						// Keeping this note here in case modern changes this, so I can remove that check
						String sound = newColumn.getEnterNoise(player);
						if (sound != null) {
							player.playSound(sound, 1, 1);
						}
					}
					column = newColumn;
				}
			}
		}
	}
	
	private @Nullable BlockBubbleColumn sampleBubbleColumn(Entity entity) {
		for (int i = 0; i < 4; ++i)
		{
			double f;
			double f2 = switch (i) {
				case 0 -> {
					f = entity.boundingBox.minX;
					yield entity.boundingBox.minZ;
				}
				case 1 -> {
					f = entity.boundingBox.maxX;
					yield entity.boundingBox.minZ;
				}
				case 2 -> {
					f = entity.boundingBox.minX;
					yield entity.boundingBox.maxZ;
				}
				default -> {
					f = entity.boundingBox.maxX;
					yield entity.boundingBox.maxZ;
				}
			};
			int j = MathHelper.floor_double(f);
			int k = MathHelper.floor_double(entity.boundingBox.minY);
			int l = MathHelper.floor_double(f2);

			if (entity.worldObj.getBlock(j, k, l) instanceof BlockBubbleColumn column)
			{
				return column;
			}
		}

		return null;
	}

	public BlockBubbleColumn getCurrentColumn() {
		return column;
	}
}
