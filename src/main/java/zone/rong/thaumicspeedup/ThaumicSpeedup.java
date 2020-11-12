package zone.rong.thaumicspeedup;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

@Mod(modid = "thaumicspeedup", name = "Thaumic Speedup", version = "2.0", dependencies = "required:thaumcraft")
public class ThaumicSpeedup {

    public static ThreadLocal<Collection<IRecipe>> RECIPES;

    public static Logger LOGGER;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER = event.getModLog();
    }

}
