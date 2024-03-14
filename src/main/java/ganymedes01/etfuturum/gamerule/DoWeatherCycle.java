package ganymedes01.etfuturum.gamerule;

import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class DoWeatherCycle {

	public static final DoWeatherCycle INSTANCE = new DoWeatherCycle();

	public static final String GAMERULE_NAME = "doWeatherCycle";
	public static final String DEFAULT_VALUE = "true";
	
	public boolean isWorldTickInProgress;
	public boolean isCommandInProgress;

	public boolean canCancelWeatherChange(GameRules gameRules) {
		return isWorldTickInProgress && !isCommandInProgress && !gameRules.getGameRuleBooleanValue(GAMERULE_NAME);
	}

	public static void registerGamerule(World world) {
		 if(!world.isRemote && !world.getGameRules().hasRule(GAMERULE_NAME)) {
			 world.getGameRules().addGameRule(GAMERULE_NAME, DEFAULT_VALUE);
		 }
	}

}
