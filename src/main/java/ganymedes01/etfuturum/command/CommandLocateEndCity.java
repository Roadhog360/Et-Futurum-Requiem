package ganymedes01.etfuturum.command;

import ganymedes01.etfuturum.world.end.dimension.ChunkProviderEFREnd;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import java.util.Random;

public class CommandLocateEndCity extends CommandBase {

	@Override
	public String getCommandName() {
		return "locate_endcity";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/locate_endcity";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		World world = sender.getEntityWorld();
		if (world.provider.dimensionId != 1) {
			sender.addChatMessage(new ChatComponentText("You must be in the End to locate an End City."));
			return;
		}

		IChunkProvider provider = world.getChunkProvider();
		if (provider instanceof net.minecraft.world.gen.ChunkProviderServer) {
			provider = ((net.minecraft.world.gen.ChunkProviderServer) provider).currentChunkProvider;
		}

		ChunkProviderEFREnd endProvider = null;
		if (provider instanceof ChunkProviderEFREnd) {
			endProvider = (ChunkProviderEFREnd) provider;
		} else {
			sender.addChatMessage(new ChatComponentText("The Et Futurum End provider is not active."));
			return;
		}

		int playerChunkX = MathHelper.floor_double(sender.getPlayerCoordinates().posX / 16.0D);
		int playerChunkZ = MathHelper.floor_double(sender.getPlayerCoordinates().posZ / 16.0D);

		sender.addChatMessage(new ChatComponentText("Searching for nearest End City..."));

		int nearestDistance = Integer.MAX_VALUE;
		int nearestX = 0;
		int nearestZ = 0;
		boolean found = false;

		// Scan a 100x100 region of the SPACING grid around the player
		int SPACING = 20;
		int SEPARATION = 11;
		int SALT = 10387313;

		int gridCenterXR = playerChunkX / SPACING;
		int gridCenterZR = playerChunkZ / SPACING;

		for (int dx = -50; dx <= 50; dx++) {
			for (int dz = -50; dz <= 50; dz++) {
				int gridX = gridCenterXR + dx;
				int gridZ = gridCenterZR + dz;

				Random random = world.setRandomSeed(gridX, gridZ, SALT);

				int chunkX = gridX * SPACING;
				int chunkZ = gridZ * SPACING;

				chunkX += (random.nextInt(SPACING - SEPARATION) + random.nextInt(SPACING - SEPARATION)) / 2;
				chunkZ += (random.nextInt(SPACING - SEPARATION) + random.nextInt(SPACING - SEPARATION)) / 2;

				if (ganymedes01.etfuturum.world.end.gen.MapGenEndCity.isValidCityAt(endProvider, chunkX, chunkZ, world.getSeed())) {
					int blockX = chunkX * 16 + 8;
					int blockZ = chunkZ * 16 + 8;
					
					int distSq = (chunkX - playerChunkX) * (chunkX - playerChunkX) + (chunkZ - playerChunkZ) * (chunkZ - playerChunkZ);
					if (distSq < nearestDistance) {
						nearestDistance = distSq;
						nearestX = blockX;
						nearestZ = blockZ;
						found = true;
					}
				}
			}
		}

		if (found) {
			sender.addChatMessage(new ChatComponentText("Nearest End City located at X: " + nearestX + ", Z: " + nearestZ));
		} else {
			sender.addChatMessage(new ChatComponentText("Could not find an End City within search radius."));
		}
	}
}
