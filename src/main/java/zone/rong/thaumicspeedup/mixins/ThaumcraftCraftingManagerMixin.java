package zone.rong.thaumicspeedup.mixins;

import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import zone.rong.thaumicspeedup.ThaumicSpeedup;

import java.util.Set;

@Mixin(ThaumcraftCraftingManager.class)
public class ThaumcraftCraftingManagerMixin {

	@Redirect(method = "generateTagsFromCraftingRecipes", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/registry/RegistryNamespaced;getKeys()Ljava/util/Set;"))
	private static Set<ResourceLocation> getThreadSafeRegistry(RegistryNamespaced<ResourceLocation, IRecipe> instance) {
		return ThaumicSpeedup.craftingRegistryKeys == null
			? CraftingManager.REGISTRY.getKeys()
			: ThaumicSpeedup.craftingRegistryKeys.get();
	}
}
