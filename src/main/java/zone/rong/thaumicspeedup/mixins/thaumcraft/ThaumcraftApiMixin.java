package zone.rong.thaumicspeedup.mixins.thaumcraft;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectEventProxy;
import thaumcraft.api.aspects.AspectList;
import zone.rong.thaumicspeedup.ThaumicSpeedup;

@Mixin(value = ThaumcraftApi.class, remap = false)
public class ThaumcraftApiMixin {

    @Redirect(
        method = "*",
        at = @At(
            value = "NEW",
            target = "Lthaumcraft/api/aspects/AspectEventProxy;<init>()V"
        )
    )
    private static AspectEventProxy useProxyInstanceSingleton() {
        return ThaumicSpeedup.PROXY_INSTANCE;
    }

    @Inject(method = "registerObjectTag(Lnet/minecraft/item/ItemStack;Lthaumcraft/api/aspects/AspectList;)V", at = @At("HEAD"), cancellable = true)
    private static void disallowEmptyStacks(ItemStack item, AspectList aspects, CallbackInfo ci) {
        if (item == null || item.isEmpty()) {
            ci.cancel();
        }
    }

    @Inject(method = "registerComplexObjectTag(Lnet/minecraft/item/ItemStack;Lthaumcraft/api/aspects/AspectList;)V", at = @At("HEAD"), cancellable = true)
    private static void disallowComplexEmptyStacks(ItemStack item, AspectList aspects, CallbackInfo ci) {
        if (item == null || item.isEmpty()) {
            ci.cancel();
        }
    }
}
