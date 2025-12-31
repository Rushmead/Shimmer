package com.lowdragmc.shimmer.fabric.core.mixins;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author HypherionSA
 * @date 2022/06/09
 */
@Mixin(ResourceLocation.class)
public class ResourceLocationMixin {

    /***
     * Work around, or hack if you will, for fabric not supporting modded shaders
     * @param string
     */
    @Inject(method = "withDefaultNamespace(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;", at = @At(value = "HEAD"), cancellable = true)
    private static void decomposeInject(String location, CallbackInfoReturnable<ResourceLocation> cir) {
        if (location.startsWith("shaders") && location.contains("shimmer:")) {
            cir.setReturnValue(ResourceLocation.fromNamespaceAndPath("shimmer",  location.replace("shimmer:", "")));
        }
//        ResourceLocation.fromNamespaceAndPath("minecraft", location);
    }

}
