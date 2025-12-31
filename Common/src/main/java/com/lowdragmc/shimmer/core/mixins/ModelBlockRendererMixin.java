package com.lowdragmc.shimmer.core.mixins;

import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import com.lowdragmc.shimmer.core.IBakedQuad;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author KilaBash
 * @date 2022/05/31
 * @implNote ModelBlockRendererMixin, reglowstone.pngcode uv2 for bloom info
 */
@Mixin(ModelBlockRenderer.class)
public abstract class ModelBlockRendererMixin {
    @Redirect(method = "putQuadData", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;putBulkData(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lnet/minecraft/client/renderer/block/model/BakedQuad;[FFFFF[IIZ)V"))
    private void injectPutQuadData(VertexConsumer instance, PoseStack.Pose pose, BakedQuad quad, float[] brightness, float red, float green, float blue, float alpha, int[] lightmap, int packedOverlay, boolean readAlpha) {
        if (((IBakedQuad)quad).isBloom() || PostProcessing.isBlockBloom()) {
            for (int i = 0; i < lightmap.length; i++) {
                lightmap[i] |= 0x1000100; // 0xf000f0 -> 0x1f001f0
            }
        }
        instance.putBulkData(pose, quad, brightness, red, green, blue, alpha, lightmap, packedOverlay, readAlpha);
    }
}
