package zone.rong.thaumicspeedup.mixins;

import com.google.common.base.Stopwatch;
import com.google.common.io.Files;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import thaumcraft.api.aspects.AspectRegistryEvent;
import thaumcraft.api.internal.CommonInternals;
import thaumcraft.common.config.ConfigAspects;
import zone.rong.thaumicspeedup.ConcurrentHashMapTypedMap;
import zone.rong.thaumicspeedup.ThaumicSpeedup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Mixin(value = ConfigAspects.class, remap = false)
public abstract class ConfigAspectsMixin {

    @Shadow private static void registerItemAspects() { throw new AssertionError(); }
    @Shadow private static void registerEntityAspects() { throw new AssertionError(); }

    /**
     * @author Rongmario
     * @reason Offload to an async thread if ConsistentLoad is false
     */
    @Overwrite
    public static void postInit() {
        if (!(boolean) Launch.blackboard.getOrDefault("ConsistentLoad", false) || !ThaumicSpeedup.persistentAspectsCache) {
            ThaumicSpeedup.aspectsThread = new Thread(() -> {
                Stopwatch stopwatch = Stopwatch.createStarted();
                ThaumicSpeedup.LOGGER.info("Offloading aspects registration...");
                ThaumicSpeedup.craftingRegistryKeys = ThreadLocal.withInitial(() -> ObjectSets.unmodifiable(new ObjectOpenHashSet<>(CraftingManager.REGISTRY.getKeys())));
                CommonInternals.objectTags.clear();
                registerItemAspects();
                registerEntityAspects();
                AspectRegistryEvent are = new AspectRegistryEvent();
                are.register = ThaumicSpeedup.PROXY_INSTANCE;
                MinecraftForge.EVENT_BUS.post(are);
                ThaumicSpeedup.LOGGER.info("Aspects registration complete! Taken {}, now writing to disk.", stopwatch.stop());
                try {
                    stopwatch.start();
                    File tempAspectsCache = File.createTempFile("thaumicspeedup-aspects_cache", null);
                    tempAspectsCache.deleteOnExit();
                    FileOutputStream fileOutputStream = new FileOutputStream(tempAspectsCache);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                    Int2ObjectMap<Object2IntMap<String>> objectTags = new Int2ObjectOpenHashMap<>();
                    CommonInternals.objectTags.forEach((k, v) -> objectTags.put(k, v.aspects.entrySet().stream().collect(Object2IntArrayMap::new, (m, av) -> m.put(av.getKey().getTag(), av.getValue()), Map::putAll)));
                    objectOutputStream.writeObject(objectTags);
                    fileOutputStream.close();
                    objectOutputStream.close();
                    File thaumicSpeedupFolder = new File((File) Launch.blackboard.get("CachesFolderFile"), "thaumicspeedup");
                    thaumicSpeedupFolder.mkdirs();
                    File aspectsCache = new File(thaumicSpeedupFolder, "aspects_cache.bin");
                    aspectsCache.createNewFile();
                    Files.move(tempAspectsCache, aspectsCache);
                    ThaumicSpeedup.LOGGER.info("Aspects serialization complete! Taken {}.", stopwatch.stop());
                } catch (IOException e) {
                    ThaumicSpeedup.LOGGER.error("Aspects serialization failed!");
                    e.printStackTrace();
                }
                ThaumicSpeedup.craftingRegistryKeys = null;
                CommonInternals.jsonLocs = new HashMap<>(CommonInternals.jsonLocs);
                CommonInternals.craftingRecipeCatalog = new HashMap<>(CommonInternals.craftingRecipeCatalog);
                CommonInternals.craftingRecipeCatalogFake = new HashMap<>(CommonInternals.craftingRecipeCatalogFake);
                CommonInternals.warpMap = new HashMap<>(CommonInternals.warpMap);
                CommonInternals.seedList = new HashMap<>(CommonInternals.seedList);
            }, "ThaumicSpeedup/AspectThread");
            ThaumicSpeedup.aspectsThread.start();
        }
    }

}
