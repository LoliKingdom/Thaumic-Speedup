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

// output item id -> recipes index for ThaumcraftCraftingManagerMixin. Built from the
// craftingRegistryKeys snapshot when set (so it's safe on the async aspect thread), else
// the live registry. Rebuilt when the key set changes size, so it tracks later additions.
public final class ThaumcraftRecipeIndex {

    private static volatile Int2ObjectMap<List<IRecipe>> index;
    private static volatile int builtAtSize = -1;

    private ThaumcraftRecipeIndex() {}

    public static List<IRecipe> forItem(Item item) {
        Set<ResourceLocation> keys = currentKeys();
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
        return out != null ? out : Collections.<IRecipe>emptyList();
    }

    // Drops the in-memory index. forItem rebuilds it lazily if it is queried again.
    public static synchronized void clear() {
        index = null;
        builtAtSize = -1;
    }

    private static Set<ResourceLocation> currentKeys() {
        ThreadLocal<Set<ResourceLocation>> snapshot = ThaumicSpeedup.craftingRegistryKeys;
        return snapshot != null ? snapshot.get() : CraftingManager.REGISTRY.getKeys();
    }

    private static void build(Set<ResourceLocation> keys) {
        long t0 = System.nanoTime();
        Int2ObjectMap<List<IRecipe>> idx = new Int2ObjectOpenHashMap<List<IRecipe>>(4096);
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
                bucket = new ArrayList<IRecipe>(2);
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
