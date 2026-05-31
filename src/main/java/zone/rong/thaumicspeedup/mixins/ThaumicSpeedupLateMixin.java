package zone.rong.thaumicspeedup.mixins;

import com.google.common.collect.Lists;
import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;
import zone.rong.thaumicspeedup.Tags;

import java.util.Collections;
import java.util.List;

/**
 * @author ZZZank
 */
public class ThaumicSpeedupLateMixin implements ILateMixinLoader {

    @Override
    public List<String> getMixinConfigs() {
        if (Loader.isModLoaded("betterwithmods")) {
            return Lists.newArrayList("mixins." + Tags.MOD_ID + ".json", "mixins." + Tags.MOD_ID + ".bwm.json");
        }
        return Collections.singletonList("mixins." + Tags.MOD_ID + ".json");
    }

}
