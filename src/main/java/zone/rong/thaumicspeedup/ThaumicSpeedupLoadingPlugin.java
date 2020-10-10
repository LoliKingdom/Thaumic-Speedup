package zone.rong.thaumicspeedup;

import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.commons.io.FileUtils;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.Map;


@IFMLLoadingPlugin.SortingIndex(10000)
public class ThaumicSpeedupLoadingPlugin implements IFMLLoadingPlugin {
    
    public ThaumicSpeedupLoadingPlugin() {
        try {
            File parent = new File("./config/", "/thaumicspeedup");
            parent.mkdir();
            File nameFile = new File(parent, "/thaumcraft_jar_name.txt");
            String thaumcraftName;
            if (nameFile.createNewFile() || nameFile.length() <= 0L) {
                thaumcraftName = "Thaumcraft-1.12.2-6.1.BETA26.jar";
                FileUtils.writeStringToFile(nameFile, thaumcraftName, Charset.defaultCharset());
            } else {
                thaumcraftName = FileUtils.readFileToString(nameFile, Charset.defaultCharset());
            }
            File jarLocation = new File("./mods/".concat(thaumcraftName));
            if (!jarLocation.exists()) {
                ThaumicSpeedup.LOGGER.fatal("You need to have Thaumcraft installed! Or if you have, please change /config/thaumicspeedup/thaumcraft_jar_name.txt's string to Thaumcraft's jar file name!");
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