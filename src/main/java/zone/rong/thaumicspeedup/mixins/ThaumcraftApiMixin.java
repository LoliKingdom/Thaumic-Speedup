package zone.rong.thaumicspeedup.mixins;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import thaumcraft.api.ThaumcraftApi;

import java.util.concurrent.ConcurrentHashMap;

@Mixin(ThaumcraftApi.class)
public class ThaumcraftApiMixin {

    @Redirect(method = "exists", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/ConcurrentHashMap;get(Ljava/lang/Object;)Ljava/lang/Object;", remap = false), remap = false)
    private static Object retrieve(ConcurrentHashMap concurrentHashMap, Object key, ItemStack stack) {
        return concurrentHashMap.get(stack.serializeNBT().hashCode());
    }

}
