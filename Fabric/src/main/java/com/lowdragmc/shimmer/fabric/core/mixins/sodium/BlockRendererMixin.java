package com.lowdragmc.shimmer.fabric.core.mixins.sodium;

import com.llamalad7.mixinextras.sugar.Local;
import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import com.lowdragmc.shimmer.core.IBakedQuad;
import net.caffeinemc.mods.sodium.client.model.light.LightMode;
import net.caffeinemc.mods.sodium.client.model.light.data.QuadLightData;
import net.caffeinemc.mods.sodium.client.render.frapi.mesh.MutableQuadViewImpl;
import net.caffeinemc.mods.sodium.client.render.frapi.render.AbstractBlockRenderContext;
import net.fabricmc.fabric.api.renderer.v1.material.ShadeMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author KilaBash
 * @date 2022/05/31
 * @implNote ModelBlockRendererMixin, reglowstone.pngcode uv2 for bloom info
 */
@Mixin(value = AbstractBlockRenderContext.class)
public abstract class BlockRendererMixin {

    @Inject(method = "shadeQuad(Lnet/caffeinemc/mods/sodium/client/render/frapi/mesh/MutableQuadViewImpl;Lnet/caffeinemc/mods/sodium/client/model/light/LightMode;ZLnet/fabricmc/fabric/api/renderer/v1/material/ShadeMode;)V", at = @At(value = "INVOKE", target="Lnet/caffeinemc/mods/sodium/client/model/light/LightPipeline;calculate(Lnet/caffeinemc/mods/sodium/client/model/quad/ModelQuadView;Lnet/minecraft/core/BlockPos;Lnet/caffeinemc/mods/sodium/client/model/light/data/QuadLightData;Lnet/minecraft/core/Direction;Lnet/minecraft/core/Direction;ZZ)V"))
    private void reCalculateBloomLight(MutableQuadViewImpl quad, LightMode lightMode, boolean emissive, ShadeMode shadeMode, CallbackInfo ci, @Local QuadLightData data) {
        if ((quad instanceof IBakedQuad bloomQuad && bloomQuad.isBloom()) || PostProcessing.isBlockBloom()){
            int[] lm = data.lm;
            for (int index = 0; index < lm.length; index++) {
                lm[index] = lm[index] | 0x10000100;
            }
        }
    }
}
