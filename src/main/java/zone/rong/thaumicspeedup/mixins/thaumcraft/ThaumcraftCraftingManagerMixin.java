package zone.rong.thaumicspeedup.mixins.thaumcraft;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import zone.rong.thaumicspeedup.ThaumcraftRecipeIndex;
import zone.rong.thaumicspeedup.ThaumicSpeedup;

@Mixin(value = ThaumcraftCraftingManager.class, remap = false)
public abstract class ThaumcraftCraftingManagerMixin {

    @Shadow
    private static AspectList getAspectsFromIngredients(NonNullList<net.minecraft.item.crafting.Ingredient> ingredients,
                                                        ItemStack recipeOut, IRecipe recipe, ArrayList<String> history) {
        throw new AssertionError();
    }

    /**
     * @author Rongmario, obus-globus
     * @reason Replace the per-call full-registry scan with an output Item -> recipes index lookup; same keep predicate and per-recipe aspect computation as the original
     */
    @Overwrite
    private static AspectList generateTagsFromCraftingRecipes(ItemStack stack, ArrayList<String> history) {
        List<IRecipe> candidates = ThaumcraftRecipeIndex.forItem(stack.getItem());
        if (candidates.isEmpty()) {
            return null;
        }

        int idS = stack.getItemDamage() == 32767 ? 0 : stack.getItemDamage();
        AspectList ret = null;
        int value = Integer.MAX_VALUE;

        for (int i = 0, n = candidates.size(); i < n; i++) {
            IRecipe recipe = candidates.get(i);
            ItemStack out = recipe.getRecipeOutput();
            if (out == null || out.isEmpty()) {
                continue;
            }
            int idR = out.getItemDamage() == 32767 ? 0 : out.getItemDamage();
            if (idR != idS) {
                continue;
            }
            try {
                AspectList ph = getAspectsFromIngredients(recipe.getIngredients(), out, recipe, history);
                if (recipe instanceof IArcaneRecipe) {
                    IArcaneRecipe ar = (IArcaneRecipe) recipe;
                    if (ar.getVis() > 0) {
                        ph.add(Aspect.MAGIC, (int) (Math.sqrt(1 + (ar.getVis() / 2)) / out.getCount()));
                    }
                }
                ph.aspects.values().removeIf(a -> a <= 0);
                if (ph.visSize() < value && ph.visSize() > 0) {
                    ret = ph;
                    value = ph.visSize();
                }
            } catch (Exception e) {
                ThaumicSpeedup.LOGGER.error("Failed to generate aspect tags from a crafting recipe for {}", stack, e);
            }
        }
        return ret;
    }

}
