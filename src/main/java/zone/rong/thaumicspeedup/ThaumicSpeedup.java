package zone.rong.thaumicspeedup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

@Mod(modid = "thaumicspeedup", name = "Thaumic Speedup", version = "1.3", dependencies = "required:thaumcraft")
public class ThaumicSpeedup {

    public static final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static ThreadLocal<Collection<IRecipe>> RECIPES;

    public static Logger LOGGER;

    public static boolean isTFCLoaded;
    public static boolean isCELoaded;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER = event.getModLog();
        isTFCLoaded = Loader.isModLoaded("tfc");
        isCELoaded = Loader.isModLoaded("gregtech");
    }

}
