package com.lowdragmc.shimmer.neoforge.core.mixins.rubidium;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.lowdragmc.shimmer.client.light.LightManager;
import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import net.minecraft.resources.ResourceLocation;
import org.embeddedt.embeddium.impl.gl.shader.ShaderLoader;
import org.embeddedt.embeddium.impl.gl.shader.ShaderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * @author KilaBash
 * @date 2022/05/28
 * @implNote ShaderLoaderMixin
 */
@Mixin(value = ShaderLoader.class,remap = false)
public abstract class ShaderLoaderMixin {

    @ModifyExpressionValue(method = "loadShader",
            at = @At(value = "INVOKE", target = "Lorg/embeddedt/embeddium/impl/gl/shader/ShaderParser;parseShader(Ljava/lang/String;Lorg/embeddedt/embeddium/impl/gl/shader/ShaderConstants;)Ljava/lang/String;"))
    private static String transformShader(String shader, ShaderType type, ResourceLocation name) {
        if (name.getPath().contains("block_layer_opaque")) {
            if (type == ShaderType.FRAGMENT) {
                shader = PostProcessing.embeddiumBloomMRTFSHInjection(shader);
            }
            if (type == ShaderType.VERTEX) {
                shader = LightManager.embeddiumVVSHInjection(shader);
            }
        }
        return shader;
    }
}
