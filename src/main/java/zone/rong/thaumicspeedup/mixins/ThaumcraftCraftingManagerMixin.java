package zone.rong.thaumicspeedup.mixins;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.internal.CommonInternals;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import zone.rong.thaumicspeedup.ThaumicSpeedup;

@Mixin(ThaumcraftCraftingManager.class)
public class ThaumcraftCraftingManagerMixin {

	@Inject(method = "generateTags(Lnet/minecraft/item/ItemStack;)Lthaumcraft/api/aspects/AspectList;", at = @At("RETURN"), remap = false)
	private static void captureLateObjectTags(ItemStack is, CallbackInfoReturnable<AspectList> cir) {
		if (ThaumicSpeedup.lateObjectTags != null) {
			ThaumicSpeedup.lateObjectTags.put(CommonInternals.generateUniqueItemstackId(is), cir.getReturnValue());
			ThaumicSpeedup.LOGGER.debug("Captured late object tag for item: " + is.getItem().getRegistryName());
		}
	}

}
