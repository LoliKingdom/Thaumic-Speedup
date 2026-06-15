package zone.rong.thaumicspeedup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

/**
 * Outputs item id => recipes index for {@link zone.rong.thaumicspeedup.mixins.thaumcraft.ThaumcraftCraftingManagerMixin}
 * replacing the per-call full-registry scan in {@link thaumcraft.common.lib.crafting.ThaumcraftCraftingManager#generateTagsFromCraftingRecipes(ItemStack, ArrayList<String>}.
 * <p>
 * Built from {@link CraftingManager#REGISTRY} and rebuilds when the key set changes size to track later additions
 */
public final class ThaumcraftRecipeIndex {

    private static volatile Int2ObjectMap<List<IRecipe>> index;
    private static volatile int builtAtSize = -1;

    private ThaumcraftRecipeIndex() {}

    public static List<IRecipe> forItem(Item item) {
        Set<ResourceLocation> keys = CraftingManager.REGISTRY.getKeys();
        int size = keys.size();
        Int2ObjectMap<List<IRecipe>> idx = index;
        if (idx == null || size != builtAtSize) {
            synchronized (ThaumcraftRecipeIndex.class) {
                if (index == null || size != builtAtSize) {
                    build(keys);
                }
                idx = index;
            }
        }
        List<IRecipe> out = idx.get(Item.getIdFromItem(item));
        return out != null ? out : Collections.emptyList();
    }

    // Drops the in-memory index. forItem rebuilds it lazily if it is queried again.
    public static synchronized void clear() {
        index = null;
        builtAtSize = -1;
    }

    private static void build(Set<ResourceLocation> keys) {
        long t0 = System.nanoTime();
        Int2ObjectMap<List<IRecipe>> idx = new Int2ObjectOpenHashMap<>(4096);
        for (ResourceLocation key : keys) {
            IRecipe recipe = CraftingManager.REGISTRY.getObject(key);
            if (recipe == null) {
                continue;
            }
            ItemStack output = recipe.getRecipeOutput();
            if (output == null || output.isEmpty()) {
                continue;
            }
            int id = Item.getIdFromItem(output.getItem());
            List<IRecipe> bucket = idx.get(id);
            if (bucket == null) {
                bucket = new ArrayList<>(2);
                idx.put(id, bucket);
            }
            bucket.add(recipe);
        }
        index = idx;
        builtAtSize = keys.size();
        ThaumicSpeedup.LOGGER.info("Built crafting-recipe reverse index: {} output items from {} recipe keys in {} ms",
            idx.size(), keys.size(), (System.nanoTime() - t0) / 1_000_000L);
    }
}
