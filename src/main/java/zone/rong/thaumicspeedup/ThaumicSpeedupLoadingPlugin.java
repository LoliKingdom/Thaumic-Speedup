package zone.rong.thaumicspeedup;

import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;


@IFMLLoadingPlugin.SortingIndex(10000)
public class ThaumicSpeedupLoadingPlugin implements IFMLLoadingPlugin {
    
    public ThaumicSpeedupLoadingPlugin() {
        try {
            File jarLocation = new File("./mods/".concat(Config.fileName));
            if (!jarLocation.exists()) {
                throw new FileNotFoundException("You need to have Thaumcraft installed! Or if you have, please change /config/thaumicspeedup.cfg's string to it's jar file name!");
            }
            loadModJar(jarLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.thaumicspeedup.json");
    }

    private void loadModJar(File jar) throws Exception {
        ((LaunchClassLoader) this.getClass().getClassLoader()).addURL(jar.toURI().toURL());
        CoreModManager.getReparseableCoremods().add(jar.getName());
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