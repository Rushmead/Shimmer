package com.lowdragmc.shimmer.neoforge.core.mixins.rubidium;

import com.lowdragmc.shimmer.client.light.LightManager;
import org.embeddedt.embeddium.impl.gl.shader.GlProgram;
import org.embeddedt.embeddium.impl.render.chunk.ShaderChunkRenderer;
import org.embeddedt.embeddium.impl.render.chunk.shader.ChunkShaderInterface;
import org.embeddedt.embeddium.impl.render.chunk.shader.ChunkShaderOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author KilaBash
 * @date 2022/05/31
 * @implNote ShaderChunkRenderer
 */
@Mixin(ShaderChunkRenderer.class)
public abstract class ShaderChunkRendererMixin {

    @Inject(method = "createShader", at = @At(value = "RETURN"), remap = false)
    private void injectLoadShader(String path, ChunkShaderOptions options, CallbackInfoReturnable<GlProgram<ChunkShaderInterface>> cir) {
        LightManager.INSTANCE.bindRbProgram(cir.getReturnValue().handle());
    }
}
