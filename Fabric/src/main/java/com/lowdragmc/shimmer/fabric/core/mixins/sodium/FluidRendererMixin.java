package com.lowdragmc.shimmer.fabric.core.mixins.sodium;

import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import net.caffeinemc.mods.sodium.client.model.color.ColorProvider;
import net.caffeinemc.mods.sodium.client.model.light.LightPipeline;
import net.caffeinemc.mods.sodium.client.model.light.data.QuadLightData;
import net.caffeinemc.mods.sodium.client.model.quad.ModelQuadViewMutable;
import net.caffeinemc.mods.sodium.client.model.quad.properties.ModelQuadFacing;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.DefaultFluidRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.FluidRenderer;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.FluidState;
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
@Mixin(DefaultFluidRenderer.class)
public abstract class FluidRendererMixin {
    @Shadow(remap = false) @Final private QuadLightData quadLightData;

     @Inject(method = "updateQuad", at = @At(value = "RETURN"), remap = false)
    private void injectRender(ModelQuadViewMutable quad, LevelSlice level, BlockPos pos, LightPipeline lighter, Direction dir, ModelQuadFacing facing, float brightness, ColorProvider<FluidState> colorProvider, FluidState fluidState, CallbackInfo ci) {
        if (PostProcessing.isFluidBloom()) {
            var lm = this.quadLightData.lm;
            for (int index = 0; index < lm.length; index++) {
                lm[index] = lm[index] | 0x10000100;
            }
        }
    }
}
