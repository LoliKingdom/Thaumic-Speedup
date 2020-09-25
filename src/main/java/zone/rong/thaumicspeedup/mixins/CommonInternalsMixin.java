package zone.rong.thaumicspeedup.mixins;

import com.google.common.base.Preconditions;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import thaumcraft.api.internal.CommonInternals;

@Mixin(CommonInternals.class)
public class CommonInternalsMixin {

    /**
     * @author Rongmario
     * @reason Eliminate ItemStack#copy + Use NBTTagCompound's native hashCode implementation
     */
    @Overwrite(remap = false)
    public static int generateUniqueItemstackId(ItemStack stack) {
        Preconditions.checkArgument(stack.getCount() == 1, "Aspects should only be registered on ItemStacks with a count of 1!");
        return stack.serializeNBT().hashCode();
    }

    /**
     * @author Rongmario
     * @reason Eliminate ItemStack#copy + Use NBTTagCompound's native hashCode implementation
     */
    @Overwrite(remap = false)
    public static int generateUniqueItemstackIdStripped(ItemStack stack) {
        Preconditions.checkArgument(stack.getCount() == 1, "Aspects should only be registered on ItemStacks with a count of 1!");
        stack.setTagCompound(null);
        return stack.serializeNBT().hashCode();
    }

}
