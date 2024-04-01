package zone.rong.thaumicspeedup;

import net.minecraftforge.common.config.Config;

@Config(modid = Tags.MODID)
public class ThaumicSpeedupConfig {

    @Config.Comment("Save interval for item aspects calculated at runtime (in seconds). Make it -1 if you don't want to save periodically, although I wouldn't recommend it.")
    public static int saveInterval = 300;

}
