package com.lowdragmc.shimmer.neoforge.core.mixins.rubidium;

import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.FluidState;
import org.embeddedt.embeddium.impl.model.color.ColorProvider;
import org.embeddedt.embeddium.impl.model.light.LightPipeline;
import org.embeddedt.embeddium.impl.model.light.data.QuadLightData;
import org.embeddedt.embeddium.impl.model.quad.ModelQuadView;
import org.embeddedt.embeddium.impl.render.chunk.compile.pipeline.FluidRenderer;
import org.embeddedt.embeddium.impl.world.WorldSlice;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author KilaBash
 * @date 2022/06/19
 * @implNote FluidRendererMixin, hook fluid bloom
 */
@Mixin(FluidRenderer.class)
public abstract class FluidRendererMixin {
    @Shadow(remap = false) @Final private QuadLightData quadLightData;

    @Inject(method = "updateQuad", at = @At(value = "RETURN"), remap = false)
    private void injectRender(ModelQuadView quad, WorldSlice world, BlockPos pos, LightPipeline lighter, Direction dir, float brightness, ColorProvider<FluidState> colorProvider, FluidState fluidState, CallbackInfo ci) {
        if (PostProcessing.isFluidBloom()) {
//             0xf000f0 -> 0x1f001f0
//            Arrays.fill(this.quadLightData.lm, 0x1000100);
            var lm = this.quadLightData.lm;
            for (int index = 0; index < lm.length; index++) {
                lm[index] = lm[index] | 0x10000100;
            }
        }
    }
}
