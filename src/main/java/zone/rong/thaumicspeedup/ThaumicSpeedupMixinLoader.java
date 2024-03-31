package zone.rong.thaumicspeedup;

import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.ArrayList;
import java.util.List;

public class ThaumicSpeedupMixinLoader implements ILateMixinLoader {

    @Override
    public List<String> getMixinConfigs() {
        ArrayList<String> configs = new ArrayList<>();
        configs.add("mixins.thaumicspeedup.json");
        if (Loader.isModLoaded("betterwithmods")) {
            configs.add("mixins.thaumicspeedup_bwmcompat.json");
        }
        return configs;
    }

}
