package ganymedes01.etfuturum.api.spectator;

import net.minecraft.entity.Entity;

public interface ISpectatorInfo {
    boolean etfu$isSpectator();
    boolean etfu$wasSpectator();
    Entity etfu$spectatingEntity();
}
