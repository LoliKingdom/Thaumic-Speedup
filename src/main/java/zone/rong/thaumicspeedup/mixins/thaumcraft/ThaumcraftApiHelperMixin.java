package zone.rong.thaumicspeedup.mixins.thaumcraft;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import thaumcraft.api.ThaumcraftApiHelper;

import java.util.List;

@Mixin(value = ThaumcraftApiHelper.class, remap = false)
public class ThaumcraftApiHelperMixin {

    @Unique private static String[] oreNames;

    /**
     * @author Rongmario
     * @reason A little optimization to not retrieve a new array every time a wildcard entry is dealt with + cache trimmed entry
     */
    @Overwrite
    public static List<ItemStack> getOresWithWildCards(String oreDict) {
        oreDict = oreDict.trim();
        if (oreDict.endsWith("*")) {
            final ObjectArrayList<ItemStack> ores = new ObjectArrayList<>();
            if (oreNames == null) {
                oreNames = OreDictionary.getOreNames();
            }
            String wildcard = oreDict.replaceAll("\\*", "");
            for (String ore : oreNames) {
                if (ore.startsWith(wildcard)) {
                    ores.addAll(OreDictionary.getOres(ore, false));
                }
            }
            return ores;
        } else {
            return OreDictionary.getOres(oreDict, false);
        }
    }

}
