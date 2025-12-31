package com.lowdragmc.shimmer.fabric.core.mixins.sodium;

import com.google.common.collect.ImmutableList;
import com.lowdragmc.shimmer.client.light.ColorPointLight;
import com.lowdragmc.shimmer.client.light.LightManager;
import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import com.lowdragmc.shimmer.core.IRenderSection;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSection;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildContext;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildOutput;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderTask;
import net.caffeinemc.mods.sodium.client.util.task.CancellationToken;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author KilaBash
 * @date 2022/05/28
 * @implNote ChunkRenderRebuildTaskMixin
 */
@Mixin(ChunkBuilderMeshingTask.class)
public abstract class ChunkBuilderMeshingTaskMixin extends ChunkBuilderTask<ChunkBuildOutput> {
    @Unique
    ImmutableList.Builder<ColorPointLight> shimmer$lights;

    public ChunkBuilderMeshingTaskMixin(RenderSection render, int time, Vector3dc absoluteCameraPos) {
        super(render, time, absoluteCameraPos);
    }

    @Redirect(method = "execute(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lnet/caffeinemc/mods/sodium/client/util/task/CancellationToken;)Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
            at = @At(value = "INVOKE",
                    target = "Lnet/caffeinemc/mods/sodium/client/world/LevelSlice;getBlockState(III)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState injectChunkCompileAddLight(LevelSlice instance, int blockX, int blockY, int blockZ) {
        var blockState = instance.getBlockState(blockX, blockY, blockZ);
        if (!blockState.isAir()) {
            var fluidState = blockState.getFluidState();
            if (LightManager.INSTANCE.isBlockHasLight(blockState.getBlock(), fluidState)) {
                var light = LightManager.INSTANCE.getBlockStateLight(instance, new BlockPos(blockX, blockY, blockZ), blockState, fluidState);
                if (light != null) shimmer$lights.add(light);
            }
            PostProcessing.setupBloom(blockState, fluidState);
        }
        return blockState;
    }

    @Inject(method = "execute(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lnet/caffeinemc/mods/sodium/client/util/task/CancellationToken;)Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;", at = @At("HEAD"), remap = false)
    private void injectChunkCompilePre(ChunkBuildContext buildContext, CancellationToken cancellationToken, CallbackInfoReturnable<ChunkBuildOutput> cir) {
        shimmer$lights = ImmutableList.builder();
    }

    @Inject(method = "execute(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lnet/caffeinemc/mods/sodium/client/util/task/CancellationToken;)Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;", at = @At("RETURN"), remap = false)
    private void injectChunkCompilePost(ChunkBuildContext buildContext, CancellationToken cancellationToken, CallbackInfoReturnable<ChunkBuildOutput> cir) {
        if (this.render instanceof IRenderSection shimmerRenderChunk) {
            shimmerRenderChunk.setShimmerLights(shimmer$lights.build());
        }
        shimmer$lights = null;
        PostProcessing.cleanBloom();
    }
}
