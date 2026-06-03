package ganymedes01.etfuturum.api;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.gtnewhorizon.gtnhlib.eventbus.Phase;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.core.handlers.client.ArmorSoundEventHandler;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import roadhog360.hogutils.api.event.BlockItemIterateEvent;
import roadhog360.hogutils.api.hogtags.helpers.ItemTags;

import java.util.Map;
import java.util.Set;

@EventBusSubscriber(phase = Phase.INIT)
public class ArmorSoundsRegistry {
	private static final Object2ObjectOpenHashMap<String, String> TAG_TO_SOUND_MAP = new Object2ObjectOpenHashMap<>();

	public static final String GENERIC_EQUIP_SOUND = Tags.MC_ASSET_VER + ":item.armor.equip_generic";

	public static final String TURTLE_HELMET_EQUIP_SOUND = Tags.MC_ASSET_VER + ":item.armor.equip_turtle_helmet";
	public static final String ELYTRA_EQUIP_SOUND = Tags.MC_ASSET_VER + ":item.armor.equip_elytra";

	public static final String LEATHER_EQUIP_SOUND = Tags.MC_ASSET_VER + ":item.armor.equip_leather";
	public static final String COPPER_EQUIP_SOUND = Tags.MC_ASSET_VER + ":item.armor.equip_copper";
	public static final String GOLD_EQUIP_SOUND = Tags.MC_ASSET_VER + ":item.armor.equip_gold";
	public static final String CHAINMAIL_EQUIP_SOUND = Tags.MC_ASSET_VER + ":item.armor.equip_chain";
	public static final String IRON_EQUIP_SOUND = Tags.MC_ASSET_VER + ":item.armor.equip_iron";
	public static final String DIAMOND_EQUIP_SOUND = Tags.MC_ASSET_VER + ":item.armor.equip_diamond";
	public static final String NETHERITE_EQUIP_SOUND = Tags.MC_ASSET_VER + ":item.armor.equip_netherite";

	private static final Set<Pair<String[], String>> DEFAULT_CONDITIONS = new ObjectOpenHashSet<>();
	static {
		DEFAULT_CONDITIONS.add(Pair.of(new String[]{"leather", "sleeping", "padding", "padded", "wool", "robe"}, Tags.MOD_ID + ":leather_equip_sound"));

		DEFAULT_CONDITIONS.add(Pair.of(new String[]{"chain", "cultist"}, Tags.MOD_ID + ":chainmail_equip_sound"));

		DEFAULT_CONDITIONS.add(Pair.of(new String[]{"tin", "copper"}, Tags.MOD_ID + ":copper_equip_sound"));

		DEFAULT_CONDITIONS.add(Pair.of(new String[]{"gold", "silver", "platinum", "alloy", "angmallen", "carmot", "efrine",
				"electrum", "hepatizon", "midasium", "orichalcum", "oureclase"}, Tags.MOD_ID + ":gold_equip_sound"));

		DEFAULT_CONDITIONS.add(Pair.of(new String[]{"iron", "steel", "brass", "bronze", "titanium", "lead", "nickel", "nickle",
				"thaumium", "invar", "solar", "prometheum", "amordrine", "celenegil", "ceruclase", "desh", "inolashite", "kalendrite",
				"vyroxeres", "mythril", "mithril", "adamantium"}, Tags.MOD_ID + ":iron_equip_sound"));

		DEFAULT_CONDITIONS.add(Pair.of(new String[]{
				"diamond", "amethyst", "atlarus", "desichalkos", "eximite", "void"}, Tags.MOD_ID + ":diamond_equip_sound"));

		DEFAULT_CONDITIONS.add(Pair.of(new String[]{
				"netherite", "endium", "enderite", "haderoth", "heavyblaze", "vulcanite", "fortress", "ignatius", "sanguinite", "tartarite",
				"stellar"
		}, Tags.MOD_ID + ":netherite_equip_sound"));


		DEFAULT_CONDITIONS.add(Pair.of(new String[]{"wood"}, Tags.MOD_ID + ":turtle_helmet_equip_sound"));

		DEFAULT_CONDITIONS.add(Pair.of(new String[]{"hazmat"}, Tags.MOD_ID + ":elytra_equip_sound"));
	}

	@ApiStatus.AvailableSince("3.0.0")
	public static void registerSound(String tag, String sound) {
		TAG_TO_SOUND_MAP.put(tag, sound);
	}

	@Nullable
	@ApiStatus.AvailableSince("3.0.0")
	public static String getSoundForTag(String tag) {
		return TAG_TO_SOUND_MAP.get(tag);
	}


	@Nullable
	@ApiStatus.AvailableSince("3.0.0")
	public static String getEquipSound(Item item, int meta) {
		if(!ItemTags.getTags(item, meta).isEmpty()) {
			for (Map.Entry<String, String> entry : TAG_TO_SOUND_MAP.entrySet()) {
				if (ItemTags.hasTag(item, meta, entry.getKey())) {
					return entry.getValue();
				}
			}
		}
		return null;
	}

	@Nullable
	@ApiStatus.AvailableSince("3.0.0")
	public static String getEquipSound(@Nullable ItemStack stack) {
		if(stack == null) return null;
		return getEquipSound(stack.getItem(), stack.getItemDamage());
	}

	public static void init() {
		registerSound(Tags.MOD_ID + ":generic_equip_sound", GENERIC_EQUIP_SOUND);

		registerSound(Tags.MOD_ID + ":turtle_helmet_sound", TURTLE_HELMET_EQUIP_SOUND);
		registerSound(Tags.MOD_ID + ":elytra_equip_sound", ELYTRA_EQUIP_SOUND);

		registerSound(Tags.MOD_ID + ":leather_equip_sound", LEATHER_EQUIP_SOUND);
		registerSound(Tags.MOD_ID + ":copper_equip_sound", COPPER_EQUIP_SOUND);
		registerSound(Tags.MOD_ID + ":gold_equip_sound", GOLD_EQUIP_SOUND);
		registerSound(Tags.MOD_ID + ":chainmail_equip_sound", CHAINMAIL_EQUIP_SOUND);
		registerSound(Tags.MOD_ID + ":iron_equip_sound", IRON_EQUIP_SOUND);
		registerSound(Tags.MOD_ID + ":diamond_equip_sound", DIAMOND_EQUIP_SOUND);
		registerSound(Tags.MOD_ID + ":netherite_equip_sound", NETHERITE_EQUIP_SOUND);

		ItemTags.addTags(ModItems.ELYTRA.get(), Tags.MOD_ID + ":elytra_equip_sound");
	}

	@SubscribeEvent
	public static void registerDefaults(BlockItemIterateEvent.ItemRegister.Init event) {
		boolean checkEquip = event.objToRegister instanceof ItemArmor
				|| event.namespaceID.contains("skull") || event.namespaceID.contains("head") || event.namespaceID.contains("pumpkin");
		if(checkEquip && getEquipSound(event.objToRegister, OreDictionary.WILDCARD_VALUE) == null) {
			for(Pair<String[], String> condition : DEFAULT_CONDITIONS) {
				for(String nameCheck : condition.first()) {
					if(event.namespaceID.toLowerCase().contains(nameCheck)) {
						ItemTags.addTags(event.objToRegister, condition.second());
						return;
					}
				}
			}
			ItemTags.addTags(event.objToRegister, Tags.MOD_ID + ":generic_equip_sound");
		}
	}

	@EventBusSubscriber.Condition
	public static boolean condition() {
		return ArmorSoundEventHandler.condition();
	}
}
