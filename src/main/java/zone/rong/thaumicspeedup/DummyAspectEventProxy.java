package zone.rong.thaumicspeedup;

import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.AspectEventProxy;
import thaumcraft.api.aspects.AspectList;

public class DummyAspectEventProxy extends AspectEventProxy {

    // This class is just to send the event for any mods that might listen to it for inserting Entity aspects, and such.

    @Override
    public void registerObjectTag(ItemStack item, AspectList aspects) {
    }

    @Override
    public void registerObjectTag(String oreDict, AspectList aspects) {
    }

    @Override
    public void registerComplexObjectTag(ItemStack item, AspectList aspects) {
    }

    @Override
    public void registerComplexObjectTag(String oreDict, AspectList aspects) {
    }

}
