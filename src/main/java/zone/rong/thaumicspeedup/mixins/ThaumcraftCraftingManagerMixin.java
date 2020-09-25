package zone.rong.thaumicspeedup.mixins;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.common.lib.crafting.ContainerFake;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import zone.rong.thaumicspeedup.ThaumicSpeedup;

import java.util.ArrayList;

@Mixin(ThaumcraftCraftingManager.class)
public class ThaumcraftCraftingManagerMixin {

    /**
     * @author Rongmario
     */
    @Redirect(method = "generateTagsFromRecipes", at = @At(value = "INVOKE", target = "Lthaumcraft/common/lib/crafting/ThaumcraftCraftingManager;generateTagsFromCraftingRecipes(Lnet/minecraft/item/ItemStack;Ljava/util/ArrayList;)Lthaumcraft/api/aspects/AspectList;"), remap = false)
    private static AspectList generateTagsFromRecipes(ItemStack stack, ArrayList<String> history) {
        return generateTagsFromCraftingRecipes(stack, history);
    }

    private static AspectList generateTagsFromCraftingRecipes(ItemStack stack, ArrayList<String> history) {
        AspectList ret = new AspectList();
        int value = Integer.MAX_VALUE;
        for (IRecipe recipe : ThaumicSpeedup.RECIPES.get()) {
            ItemStack output = recipe.getRecipeOutput();
            if (Item.getIdFromItem(output.getItem()) >= 0 && output.getItem() == stack.getItem()) {
                int idR = (output.getItemDamage() == 32767) ? 0 : output.getItemDamage();
                int idS = (stack.getItemDamage() == 32767) ? 0 : stack.getItemDamage();
                if (idR == idS) {
                    AspectList aspectList = getAspectsFromIngredients(recipe.getIngredients(), recipe.getRecipeOutput(), recipe, history);
                    if (recipe instanceof IArcaneRecipe) {
                        IArcaneRecipe ar = (IArcaneRecipe) recipe;
                        if (ar.getVis() > 0) {
                            aspectList.add(Aspect.MAGIC, (int) (Math.sqrt((1 + ((float) ar.getVis() / 2))) / recipe.getRecipeOutput().getCount()));
                        }
                    }
                    for (Aspect as : aspectList.getAspects()) {
                        if (aspectList.getAmount(as) <= 0) {
                            aspectList.remove(as);
                        }
                    }
                    if (aspectList.visSize() < value && aspectList.visSize() > 0) {
                        ret = aspectList;
                        value = aspectList.visSize();
                    }
                }
            }
        }
        return ret;
    }

    private static AspectList getAspectsFromIngredients(NonNullList<Ingredient> ingredients, ItemStack out, IRecipe recipe, ArrayList<String> history) {
        InventoryCrafting inv = new InventoryCrafting(new ContainerFake(), 3, 3);
        AspectList mid = new AspectList();
        AspectList finalReturn = new AspectList();
        int i = 0;
        for (Ingredient ingredient : ingredients) {
            final ItemStack[] stacks = ingredient.getMatchingStacks();
            if (stacks.length > 0) {
                ItemStack first = stacks[0];
                inv.setInventorySlotContents(i, first);
                AspectList aspects = ThaumcraftCraftingManager.getObjectTags(first, history);
                for (Aspect as : aspects.getAspects()) {
                    if (as != null) {
                        mid.add(as, aspects.getAmount(as));
                    }
                }
            }
            i++;
        }
        try {
            for (ItemStack ri : recipe.getRemainingItems(inv)) {
                if (!ri.isEmpty()) {
                    AspectList aspects = ThaumcraftCraftingManager.getObjectTags(ri, history);
                    for (Aspect as : aspects.getAspects()) {
                        mid.reduce(as, aspects.getAmount(as));
                    }
                }
            }
            for (Aspect as : mid.getAspects()) {
                if (as != null) {
                    float v = mid.getAmount(as) * 0.75F / out.getCount();
                    if (v < 1.0F && v > 0.75D) {
                        v = 1.0F;
                    }
                    if (v > 0) {
                        finalReturn.add(as, (int) v);
                    }
                }
            }
        } catch (Exception e) {
            // Silently retreat...
        }
        return finalReturn;
    }

}
