package zone.rong.thaumicspeedup.mixins;

import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;
import zone.rong.thaumicspeedup.Tags;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ZZZank
 */
public class ThaumicSpeedupLateMixin implements ILateMixinLoader {

    @Override
    public List<String> getMixinConfigs() {
        return Arrays.stream(Tags.MIXIN_CONFIGS.split(" "))
            .filter(Loader::isModLoaded)
            .map(mod -> String.format(Tags.MIXIN_CONFIG_TEMPLAE, mod))
            .collect(Collectors.toList());
    }
}
