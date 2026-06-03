package ganymedes01.etfuturum.client.renderer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;

public class GlowingRenderHelper {

	public static int getEntityTeamColor(Entity entity) {
		int color = 0xFFFFFF;

		Team team = null;
		if (entity instanceof EntityLivingBase) {
			team = ((EntityLivingBase) entity).getTeam();
		}
		if (!(team instanceof ScorePlayerTeam) && entity.worldObj != null) {
			Scoreboard scoreboard = entity.worldObj.getScoreboard();
			team = scoreboard.getPlayersTeam(entity.getCommandSenderName());
			if (!(team instanceof ScorePlayerTeam) && entity.getUniqueID() != null) {
				team = scoreboard.getPlayersTeam(entity.getUniqueID().toString());
			}
		}

		if (team instanceof ScorePlayerTeam) {
			String prefix = ((ScorePlayerTeam) team).getColorPrefix();
			if (prefix != null && !prefix.isEmpty()) {
				int code = extractFormatColor(prefix);
				if (code >= 0) {
					color = code;
				}
			}
		}

		return color;
	}

	private static int extractFormatColor(String text) {
		int lastColor = -1;
		int len = text.length();

		for (int i = 0; i < len - 1; i++) {
			if (text.charAt(i) == '\u00a7') {
				int code = resolveColorCode(text.charAt(i + 1));
				if (code >= 0) {
					lastColor = code;
				}
			}
		}

		return lastColor;
	}

	private static int resolveColorCode(char code) {
		switch (Character.toLowerCase(code)) {
			case '0': return 0x000000;
			case '1': return 0x0000AA;
			case '2': return 0x00AA00;
			case '3': return 0x00AAAA;
			case '4': return 0xAA0000;
			case '5': return 0xAA00AA;
			case '6': return 0xFFAA00;
			case '7': return 0xAAAAAA;
			case '8': return 0x555555;
			case '9': return 0x5555FF;
			case 'a': return 0x55FF55;
			case 'b': return 0x55FFFF;
			case 'c': return 0xFF5555;
			case 'd': return 0xFF55FF;
			case 'e': return 0xFFFF55;
			case 'f': return 0xFFFFFF;
			default: return -1;
		}
	}
}
