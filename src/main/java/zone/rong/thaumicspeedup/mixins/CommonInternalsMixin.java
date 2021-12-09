package zone.rong.thaumicspeedup.mixins;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import thaumcraft.api.internal.CommonInternals;

@Mixin(value = CommonInternals.class, remap = false)
public class CommonInternalsMixin {

    /**
     * @author Rongmario
     * @reason Eliminate ItemStack#copy + Use NBTTagCompound's native hashCode implementation
     */
    @Overwrite
    public static int generateUniqueItemstackId(ItemStack stack) {
        NBTTagCompound tag = stack.serializeNBT();
        tag.removeTag("Count");
        return tag.hashCode();
    }

    /**
     * @author Rongmario
     * @reason Eliminate ItemStack#copy + Use NBTTagCompound's native hashCode implementation
     */
    @Overwrite
    public static int generateUniqueItemstackIdStripped(ItemStack stack) {
        NBTTagCompound tag = stack.serializeNBT();
        tag.removeTag("Count");
        tag.removeTag("tag");
        return tag.hashCode();
    }

}
