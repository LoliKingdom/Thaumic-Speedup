package zone.rong.thaumicspeedup;

import net.minecraftforge.fml.common.Loader;
import org.spongepowered.asm.mixin.Mixins;
import zone.rong.mixinbooter.MixinLoader;

@MixinLoader
public class ThaumicSpeedupMixinLoader {

    {
        Mixins.addConfiguration("mixins.thaumicspeedup.json");
        if (Loader.isModLoaded("betterwithmods")) {
            Mixins.addConfiguration("mixins.thaumicspeedup_bwmcompat.json");
        }
    }

}
