package zone.rong.thaumicspeedup.mixins.bwm;

import betterwithmods.module.compat.thaumcraft.Thaumcraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.api.aspects.AspectRegistryEvent;

@Pseudo
@Mixin(value = Thaumcraft.class, remap = false)
public abstract class ThaumcraftMixin {

	@Shadow public static void registerAnvilRecipeAspects() {
		throw new AssertionError();
	}

	@Shadow public abstract void registerAspectOverrides();
	@Shadow public abstract void registerAspects();

	@Inject(method = "<init>", at = @At("RETURN"))
	private void registerEventListener(CallbackInfo ci) {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Inject(method = "postInit", at = @At("HEAD"), cancellable = true)
	private void disablePostInit(FMLPostInitializationEvent event, CallbackInfo ci) {
		ci.cancel();
	}

	@SubscribeEvent
	public void registerAspects(AspectRegistryEvent event) {
		this.registerAspectOverrides();
		this.registerAspects();
		registerAnvilRecipeAspects();
	}
}
