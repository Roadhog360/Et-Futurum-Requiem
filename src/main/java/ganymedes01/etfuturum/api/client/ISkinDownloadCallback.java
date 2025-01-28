package ganymedes01.etfuturum.api.client;

import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.SkinManager.SkinAvailableCallback;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
@Deprecated
public interface ISkinDownloadCallback extends SkinAvailableCallback {

	/// Gets called when a 1.8 style skin is downloaded
	///
	/// No longer used. Kept around to not break mods that might call this.
	/// Not sure what it was ever used for, since it's not implemented on anything.
	/// MCP name: `onSkinAvailable`
	@Override
	@Deprecated
	void func_152121_a(Type skinType, ResourceLocation resourceLocation);
}