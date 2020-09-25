package zone.rong.thaumicspeedup;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.util.Map;


@IFMLLoadingPlugin.SortingIndex(10000)
public class ThaumicSpeedupLoadingPlugin implements IFMLLoadingPlugin {
    
    public ThaumicSpeedupLoadingPlugin() {
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.thaumicspeedup.json");
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

}