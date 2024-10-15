package zone.rong.thaumicspeedup.mixins.thaumcraft;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.api.crafting.IThaumcraftRecipe;
import thaumcraft.api.internal.CommonInternals;
import zone.rong.thaumicspeedup.ConcurrentHashMapTypedMap;

import java.util.HashMap;

@Mixin(value = CommonInternals.class, remap = false)
public class CommonInternalsMixin {

    @Shadow public static HashMap<String, ResourceLocation> jsonLocs;
    @Shadow public static HashMap<ResourceLocation, IThaumcraftRecipe> craftingRecipeCatalog;
    @Shadow public static HashMap<ResourceLocation, Object> craftingRecipeCatalogFake;
    @Shadow public static HashMap<Object, Integer> warpMap;
    @Shadow public static HashMap<String, ItemStack> seedList;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void injectInStaticBlock(CallbackInfo ci) {
        jsonLocs = new ConcurrentHashMapTypedMap<>();
        craftingRecipeCatalog = new ConcurrentHashMapTypedMap<>();
        craftingRecipeCatalogFake = new ConcurrentHashMapTypedMap<>();
        warpMap = new ConcurrentHashMapTypedMap<>();
        seedList = new ConcurrentHashMapTypedMap<>();
    }

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
