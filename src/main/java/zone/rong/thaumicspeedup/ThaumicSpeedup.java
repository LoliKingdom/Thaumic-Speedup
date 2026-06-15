package zone.rong.thaumicspeedup;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thaumcraft.api.aspects.AspectEventProxy;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION,
        dependencies = "required:thaumcraft;required-after:mixinbooter")
public class ThaumicSpeedup {

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);
    public static final AspectEventProxy PROXY_INSTANCE = new AspectEventProxy();

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        // The crafting-recipe reverse index is only needed for the startup aspect-tag pass
        // Drop it once loading is done as forItem rebuilds it lazily for any runtime lookups
        ThaumcraftRecipeIndex.clear();
    }

}
