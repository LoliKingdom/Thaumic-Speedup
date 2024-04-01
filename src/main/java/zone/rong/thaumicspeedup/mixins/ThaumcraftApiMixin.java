package zone.rong.thaumicspeedup.mixins;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import zone.rong.thaumicspeedup.ThaumicSpeedup;

@Mixin(value = ThaumcraftApi.class, remap = false)
public class ThaumcraftApiMixin {

    /**
     * @author SumTingWong
     * @reason Thaumcraft deprecated this method in version 6 with an event in AspectEventProxy to replace it
     */
    @Deprecated
    @Overwrite
    public static void registerObjectTag(ItemStack item, AspectList aspects) {
        if (item == null || item.isEmpty()) return;
        ThaumicSpeedup.PROXY_INSTANCE.registerObjectTag(item, aspects);
    }

    /**
     * @author SumTingWong
     * @reason Thaumcraft deprecated this method in version 6 with an event in AspectEventProxy to replace it
     */
    @Deprecated
    @Overwrite
    public static void registerObjectTag(String oreDict, AspectList aspects) {
        ThaumicSpeedup.PROXY_INSTANCE.registerObjectTag(oreDict, aspects);
    }

    /**
     * @author SumTingWong
     * @reason Thaumcraft deprecated this method in version 6 with an event in AspectEventProxy to replace it
     */
    @Deprecated
    @Overwrite
    public static void registerComplexObjectTag(ItemStack item, AspectList aspects) {
        if (item == null || item.isEmpty()) return;
        ThaumicSpeedup.PROXY_INSTANCE.registerComplexObjectTag(item, aspects);
    }

    /**
     * @author SumTingWong
     * @reason Thaumcraft deprecated this method in version 6 with an event in AspectEventProxy to replace it
     */
    @Deprecated
    @Overwrite
    public static void registerComplexObjectTag(String oreDict, AspectList aspects) {
        ThaumicSpeedup.PROXY_INSTANCE.registerComplexObjectTag(oreDict, aspects);
    }

}
