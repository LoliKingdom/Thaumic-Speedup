package zone.rong.thaumicspeedup;

import com.google.common.base.Stopwatch;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ResourceLocation;
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
import java.util.Set;

@Mod(
    modid = Tags.MOD_ID,
    name = Tags.MOD_NAME,
    version = Tags.VERSION,
    dependencies = "required:thaumcraft;required:persistency;required-after:mixinbooter"
)
public class ThaumicSpeedup {

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);
    public static final AspectEventProxy PROXY_INSTANCE = new AspectEventProxy();

    public static volatile boolean persistentAspectsCache = true;
    public static Thread aspectsThread;

    public static ThreadLocal<Set<ResourceLocation>> craftingRegistryKeys;

    @Mod.EventHandler
    public void construct(FMLConstructionEvent event) {
        if (!((boolean) Launch.blackboard.getOrDefault("ConsistentLoad", false))) {
            return;
        }
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
                    ThaumicSpeedup.LOGGER.error("Failed to deserialize the aspects cache", e);
                    persistentAspectsCache = false;
                }
            }, "ThaumicSpeedup/AspectThread-0").start();
        } else {
            persistentAspectsCache = false;
        }
    }

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        if (aspectsThread != null) {
            try {
                aspectsThread.join();
            } catch (InterruptedException e) {
                ThaumicSpeedup.LOGGER.error("Interrupted while waiting for aspect registration to finish", e);
            }
        }
        // The crafting-recipe reverse index is only needed for the startup aspect-tag pass.
        // Drop it once loading is done; forItem rebuilds it lazily for any runtime lookups.
        ThaumcraftRecipeIndex.clear();
    }

}
