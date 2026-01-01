package com.lowdragmc.shimmer.fabric.core.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.lowdragmc.shimmer.client.light.ColorPointLight;
import com.lowdragmc.shimmer.client.light.LightManager;
import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import com.lowdragmc.shimmer.core.ISectionCompilerResults;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author KilaBash
 * @date 2022/05/02
 * @implNote RebuildTaskMixin, used to compile and save light info to the chunk.
 */
@Mixin(SectionCompiler.class)
public abstract class SectionCompilerMixin {
//    @SuppressWarnings("target") @Shadow(aliases = {"this$1", "f_112859_", "field_20839"}) @Final SectionRenderDispatcher.RenderSection this$1;

    @Redirect(method = "compile(Lnet/minecraft/core/SectionPos;Lnet/minecraft/client/renderer/chunk/RenderChunkRegion;Lcom/mojang/blaze3d/vertex/VertexSorting;Lnet/minecraft/client/renderer/SectionBufferBuilderPack;)Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/chunk/RenderChunkRegion;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState injectCompile(RenderChunkRegion instance, BlockPos pPos, @Local SectionCompiler.Results result) {
        BlockState blockstate = instance.getBlockState(pPos);
        FluidState fluidstate = blockstate.getFluidState();
        if (LightManager.INSTANCE.isBlockHasLight(blockstate.getBlock(), fluidstate)) {
            ColorPointLight light = LightManager.INSTANCE.getBlockStateLight(instance, pPos, blockstate, fluidstate);
            if (light != null) {
                ((ISectionCompilerResults) (Object) result).addShimmerLight(light);
            }
        }

        PostProcessing.setupBloom(blockstate, fluidstate);
        return blockstate;
    }


    @Inject(method = "compile", at = @At(value = "RETURN"))
    private void injectCompilePost(SectionPos sectionPos, RenderChunkRegion region, VertexSorting vertexSorting, SectionBufferBuilderPack sectionBufferBuilderPack, CallbackInfoReturnable<SectionCompiler.Results> cir) {
        PostProcessing.cleanBloom();
    }
}
