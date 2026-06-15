package zone.rong.thaumicspeedup.mixins.thaumcraft;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import thaumcraft.api.internal.CommonInternals;

@Mixin(value = CommonInternals.class, remap = false)
public class CommonInternalsMixin {

    @Unique
    private static int thaumicspeedup$id(ItemStack stack) {
        return Item.getIdFromItem(stack.getItem()) * 92821 + stack.getItemDamage();
    }

    /**
     * @author Rongmario
     * @reason Hash id, damage, tag directly, avoid copying ItemStack copy and creating NBT string per call
     */
    @Overwrite
    public static int generateUniqueItemstackId(ItemStack stack) {
        int hash = thaumicspeedup$id(stack);
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null) {
            hash = hash * 92821 + tag.hashCode();
        }
        return hash;
    }

    /**
     * @author Rongmario
     * @reason Hash id, damage, tag directly, avoid copying ItemStack copy
     */
    @Overwrite
    public static int generateUniqueItemstackIdStripped(ItemStack stack) {
        return thaumicspeedup$id(stack);
    }

}
