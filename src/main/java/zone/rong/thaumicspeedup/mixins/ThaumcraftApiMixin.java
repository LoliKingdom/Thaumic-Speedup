package zone.rong.thaumicspeedup.mixins;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;

import java.util.concurrent.ConcurrentHashMap;

@Mixin(ThaumcraftApi.class)
public class ThaumcraftApiMixin {

    @Inject(method = "registerObjectTag(Lnet/minecraft/item/ItemStack;Lthaumcraft/api/aspects/AspectList;)V", at = @At("HEAD"), remap = false, cancellable = true)
    private static void disallowEmptyStacks(ItemStack item, AspectList aspects, CallbackInfo ci) {
        if (item == null || item.isEmpty()) {
            ci.cancel();
        }
    }

    @Inject(method = "registerComplexObjectTag(Lnet/minecraft/item/ItemStack;Lthaumcraft/api/aspects/AspectList;)V", at = @At("HEAD"), remap = false, cancellable = true)
    private static void disallowComplexEmptyStacks(ItemStack item, AspectList aspects, CallbackInfo ci) {
        if (item == null || item.isEmpty()) {
            ci.cancel();
        }
    }

    @Redirect(method = "exists", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/ConcurrentHashMap;get(Ljava/lang/Object;)Ljava/lang/Object;", remap = false), remap = false)
    private static Object retrieve(ConcurrentHashMap concurrentHashMap, Object key, ItemStack stack) {
        return concurrentHashMap.get(stack.serializeNBT().hashCode());
    }

}
