package zone.rong.thaumicspeedup;

import net.minecraftforge.fml.common.Loader;
import org.spongepowered.asm.mixin.Mixins;
import zone.rong.mixinbooter.MixinLoader;

@MixinLoader
public class ThaumicSpeedupMixinLoader {

    {
        Mixins.addConfiguration("tmp/mixins.thaumicspeedup.json");
        if (Loader.isModLoaded("betterwithmods")) {
            Mixins.addConfiguration("tmp/mixins.thaumicspeedup_bwmcompat.json");
        }
    }

}
