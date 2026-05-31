package zone.rong.thaumicspeedup.mixins.betterwithmods;

import betterwithmods.module.compat.thaumcraft.Thaumcraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.spongepowered.asm.mixin.*;
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

	/**
	 * @author Rongmario
	 * @reason Use AspectRegistryEvent for aspect registration/replacement purposes
	 */
	@Overwrite
	public void postInit(FMLPostInitializationEvent event) { }

	@Unique
	@SubscribeEvent
	public void thaumicspeedup$registerAspects(AspectRegistryEvent event) {
		this.registerAspectOverrides();
		this.registerAspects();
		registerAnvilRecipeAspects();
	}

}
