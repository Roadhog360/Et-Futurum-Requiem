package ganymedes01.etfuturum.gamerule;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class RandomTickSpeed {

	public static final RandomTickSpeed INSTANCE = new RandomTickSpeed();

	public static final String GAMERULE_NAME = "randomTickSpeed";
	public static final String DEFAULT_VALUE = "3";

	public int getRandomTickSpeed(GameRules gameRulesInstance) {
		return Integer.parseInt(StringUtils.defaultIfEmpty(gameRulesInstance.getGameRuleStringValue(GAMERULE_NAME), DEFAULT_VALUE));
	}

	public static void registerGamerule(World world) {
		if (!world.isRemote && !world.getGameRules().hasRule(GAMERULE_NAME)) {
			world.getGameRules().addGameRule(GAMERULE_NAME, DEFAULT_VALUE);
		}
	}
}
