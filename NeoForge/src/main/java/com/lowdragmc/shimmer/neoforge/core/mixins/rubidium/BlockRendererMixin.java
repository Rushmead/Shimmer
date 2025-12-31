package com.lowdragmc.shimmer.neoforge.core.mixins.rubidium;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import com.lowdragmc.shimmer.core.IBakedQuad;
import net.minecraft.core.Direction;
import org.embeddedt.embeddium.api.render.chunk.BlockRenderContext;
import org.embeddedt.embeddium.impl.model.light.data.QuadLightData;
import org.embeddedt.embeddium.impl.model.quad.BakedQuadView;
import org.embeddedt.embeddium.impl.render.chunk.compile.pipeline.BlockRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * @author KilaBash
 * @date 2022/05/31
 * @implNote ModelBlockRendererMixin, reglowstone.pngcode uv2 for bloom info
 */
@Mixin(value = BlockRenderer.class, remap = false)
public abstract class BlockRendererMixin {

    @ModifyReturnValue(method = "getVertexLight", at = @At("RETURN"))
    private QuadLightData reCalculateBloomLight(QuadLightData original, @Local(argsOnly = true) BakedQuadView quad) {
        if ((quad instanceof IBakedQuad bloomQuad && bloomQuad.isBloom()) || PostProcessing.isBlockBloom()){
            int[] lm = original.lm;
            for (int index = 0; index < lm.length; index++) {
                lm[index] = lm[index] | 0x10000100;
            }
        }
        return original;
    }
}
