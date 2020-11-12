package zone.rong.thaumicspeedup.mixins;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.internal.CommonInternals;
import thaumcraft.common.config.*;
import zone.rong.thaumicspeedup.ThaumicSpeedup;

import java.io.*;
import java.util.Collection;

@Mixin(Thaumcraft.class)
public class ThaumcraftMixin {

    @Inject(method = "postInit", at = @At("HEAD"), remap = false, cancellable = true)
    private void redirectProxyPostInit(FMLPostInitializationEvent event, CallbackInfo ci) {
        ConfigEntities.postInitEntitySpawns();
        final Collection<IRecipe> recipes = ForgeRegistries.RECIPES.getValues();
        new Thread(() -> {
            long time = System.currentTimeMillis();
            ThaumicSpeedup.LOGGER.info("Offloading aspects registration...");
            ThaumicSpeedup.RECIPES = ThreadLocal.withInitial(() -> recipes);
            CommonInternals.objectTags.clear();
            try {
                File parent = new File(Loader.instance().getConfigDir(), "/thaumicspeedup");
                parent.mkdir();
                File file = new File(parent, "/cache.lock");
                if (file.createNewFile() || file.length() <= 0L) {
                    ConfigAspects.postInit();
                    write(file);
                } else {
                    FileInputStream fileStream = new FileInputStream(file);
                    ObjectInputStream objectStream = new ObjectInputStream(fileStream);
                    Int2ObjectMap<Object2IntMap<String>> store = (Int2ObjectMap<Object2IntMap<String>>) objectStream.readObject();
                    objectStream.close();
                    fileStream.close();
                    store.forEach((i, o2i) -> {
                        AspectList list = new AspectList();
                        o2i.forEach((s, j) -> list.add(Aspect.getAspect(s), j));
                        CommonInternals.objectTags.put(i, list);
                    });
                    /*
                    long previousHash = objectStream.readLong();
                    ThaumicSpeedup.LOGGER.info("Previous Hash: {}, Current Hash: {}", previousHash, currentHash);
                    if (previousHash == currentHash) {
                        Int2ObjectMap<Object2IntMap<String>> store = (Int2ObjectMap<Object2IntMap<String>>) objectStream.readObject();
                        objectStream.close();
                        fileStream.close();
                        store.forEach((i, o2i) -> {
                            AspectList list = new AspectList();
                            o2i.forEach((s, j) -> list.add(Aspect.getAspect(s), j));
                            CommonInternals.objectTags.put(i, list);
                        });
                    } else {
                        objectStream.close();
                        fileStream.close();
                        ConfigAspects.postInit();
                        write(file);
                    }
                     */
                }
                ConfigRecipes.postAspects();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            ThaumicSpeedup.LOGGER.info("Offloading complete! Taken {}ms", System.currentTimeMillis() - time);
        }, "ThaumicSpeedup/AspectThread-0").start();
        ModConfig.postInitLoot();
        ModConfig.postInitMisc();
        ConfigRecipes.compileGroups();
        ConfigResearch.postInit();
        ci.cancel();
    }

    private static void write(File file) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        // objectOutputStream.writeLong(CraftingManager.REGISTRY.getKeys().hashCode());
        Int2ObjectMap<Object2IntMap<String>> store = new Int2ObjectOpenHashMap<>();
        CommonInternals.objectTags.forEach((i, list) -> {
            Object2IntMap<String> map = store.computeIfAbsent(i, k -> new Object2IntOpenHashMap<>());
            list.aspects.forEach((a, j) -> map.put(a.getTag(), j));
        });
        objectOutputStream.writeObject(store);
        fileOutputStream.close();
        objectOutputStream.close();
    }

}
