package ganymedes01.etfuturum.potion;

public class PotionGlowing extends ModPotions {

	public PotionGlowing(String name, int id, boolean isBad, int color) {
		super(name, id, isBad, color);
		setPotionName("potion." + name);
	}

	@Override
	public boolean hasPacket() {
		return true;
	}
}
