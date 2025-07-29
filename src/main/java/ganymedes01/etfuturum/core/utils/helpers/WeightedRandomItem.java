package ganymedes01.etfuturum.core.utils.helpers;

import net.minecraft.util.WeightedRandom;

public class WeightedRandomItem<T> extends WeightedRandom.Item {

    public T data;

    public WeightedRandomItem(int weight, T data) {
        super(weight);
        this.data = data;
    }
}
