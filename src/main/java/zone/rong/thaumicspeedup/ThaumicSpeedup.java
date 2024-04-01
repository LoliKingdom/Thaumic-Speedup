package zone.rong.thaumicspeedup;

import com.google.common.base.Stopwatch;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectEventProxy;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.internal.CommonInternals;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Mod(modid = Tags.MODID, name = Tags.MODNAME, version = Tags.VERSION, dependencies = "required:thaumcraft;required:persistency")
@Mod.EventBusSubscriber(modid = Tags.MODID)
public class ThaumicSpeedup {

    public static final Logger LOGGER = LogManager.getLogger("ThaumicSpeedup");
    public static AspectEventProxy PROXY_INSTANCE = new AspectEventProxy();

    public static volatile boolean persistentAspectsCache = true;
    public static Thread aspectsThread;

    public static HashMap<Integer, AspectList> lateObjectTags;
    private static long nextSave = -1L;

    @Mod.EventHandler
    public void construct(FMLConstructionEvent event) {
        if ((boolean) Launch.blackboard.getOrDefault("ConsistentLoad", false)) {
            File aspectsCache = new File((File) Launch.blackboard.get("CachesFolderFile"), "thaumicspeedup/aspects_cache.bin");
            if (aspectsCache.isFile() && aspectsCache.exists() && aspectsCache.length() > 0L) {
                aspectsThread = new Thread(() -> {
                    try {
                        ThaumicSpeedup.LOGGER.info("Offloading aspects deserialization...");
                        Stopwatch stopwatch = Stopwatch.createStarted();
                        FileInputStream fileStream = new FileInputStream(aspectsCache);
                        ObjectInputStream objectStream = new ObjectInputStream(fileStream);
                        Int2ObjectMap<Object2IntMap<String>> objectTags = (Int2ObjectMap<Object2IntMap<String>>) objectStream.readObject();
                        objectTags.forEach((i, m) -> {
                            AspectList aspectList = new AspectList();
                            m.forEach((aspect, value) -> aspectList.aspects.put(Aspect.getAspect(aspect), value));
                            CommonInternals.objectTags.put(i, aspectList);
                        });
                        objectStream.close();
                        fileStream.close();
                        ThaumicSpeedup.LOGGER.info("Aspects deserialization complete! Taken {}.", stopwatch.stop());
                        ThaumicSpeedup.lateObjectTags = new HashMap<>();
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                        persistentAspectsCache = false;
                    }
                }, "ThaumicSpeedup/AspectThread");
                aspectsThread.start();
            } else {
                persistentAspectsCache = false;
            }
        }
    }

    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {
        if (ThaumicSpeedupConfig.saveInterval > 0) {
            nextSave = System.currentTimeMillis() + (ThaumicSpeedupConfig.saveInterval * 1000L);
        }
    }

    @SubscribeEvent
    public static void tick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) return;
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) return;
        long now = System.currentTimeMillis();
        if (nextSave > 0 && nextSave < now) {
            nextSave = now + (ThaumicSpeedupConfig.saveInterval * 1000L);
            if (lateObjectTags.isEmpty()) {
                LOGGER.info(Tags.MODNAME + " found 0 new item aspects to save.");
                return;
            }
            LOGGER.info(Tags.MODNAME + " found " + lateObjectTags.size() + " new item aspects to save.");
            File aspectsCache = new File((File) Launch.blackboard.get("CachesFolderFile"), "thaumicspeedup/aspects_cache.bin");
            if (aspectsCache.isFile() && aspectsCache.exists() && aspectsCache.length() > 0L) {
                new Thread(() -> {
                    try {
                        Stopwatch stopwatch = Stopwatch.createStarted();
                        FileOutputStream fileOutputStream = new FileOutputStream(aspectsCache);
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                        Int2ObjectMap<Object2IntMap<String>> objectTags = new Int2ObjectOpenHashMap<>();
                        lateObjectTags.forEach((k, v) -> objectTags.put(k, v.aspects.entrySet().stream().collect(Object2IntArrayMap::new, (m, av) -> m.put(av.getKey().getTag(), av.getValue()), Map::putAll)));
                        objectOutputStream.writeObject(objectTags);
                        fileOutputStream.close();
                        objectOutputStream.close();
                        ThaumicSpeedup.LOGGER.info("Aspects serialization complete! Taken {}.", stopwatch.stop());
                        ThaumicSpeedup.lateObjectTags.clear();
                    } catch (IOException e) {
                        ThaumicSpeedup.LOGGER.error("Aspects serialization failed!");
                        e.printStackTrace();
                    }
                }, "ThaumicSpeedup/AspectThread").start();
            }
        }
    }

}
