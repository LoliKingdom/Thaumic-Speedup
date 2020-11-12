package zone.rong.thaumicspeedup;

import org.spongepowered.asm.mixin.Mixins;
import zone.rong.mixinbooter.MixinLoader;

@MixinLoader
public class ThaumicSpeedupMixinLoader {

    {
        Mixins.addConfiguration("mixins.thaumicspeedup.json");
    }

}
