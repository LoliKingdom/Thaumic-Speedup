package zone.rong.thaumicspeedup;

import com.google.common.base.Stopwatch;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectEventProxy;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.internal.CommonInternals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

@Mod(modid = "thaumicspeedup", name = "Thaumic Speedup", version = "3.0", dependencies = "required:thaumcraft;required:persistency")
public class ThaumicSpeedup {

    public static final Logger LOGGER = LogManager.getLogger("ThaumicSpeedup");
    public static final AspectEventProxy PROXY_INSTANCE = new AspectEventProxy();

    public static volatile boolean persistentAspectsCache = true;
    public static Thread aspectsThread;

    @Mod.EventHandler
    public void construct(FMLConstructionEvent event) {
        if ((boolean) Launch.blackboard.getOrDefault("ConsistentLoad", false)) {
            File aspectsCache = new File((File) Launch.blackboard.get("CachesFolderFile"), "thaumicspeedup/aspects_cache.bin");
            if (aspectsCache.isFile() && aspectsCache.exists() && aspectsCache.length() > 0L) {
                new Thread(() -> {
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
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                        persistentAspectsCache = false;
                    }
                }, "ThaumicSpeedup/AspectThread-0").start();
            } else {
                persistentAspectsCache = false;
            }
        }
    }

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        if (aspectsThread != null) {
            try {
                aspectsThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
