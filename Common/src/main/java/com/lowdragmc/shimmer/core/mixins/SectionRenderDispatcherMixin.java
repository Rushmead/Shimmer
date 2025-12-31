package com.lowdragmc.shimmer.core.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.lowdragmc.shimmer.client.light.ColorPointLight;
import com.lowdragmc.shimmer.core.IRenderSection;
import com.lowdragmc.shimmer.core.ISectionCompilerResults;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mixin(targets = "net.minecraft.client.renderer.chunk.SectionRenderDispatcher$RenderSection$RebuildTask")
public class SectionRenderDispatcherMixin {
    @Shadow(aliases = {"this$1", "field_20839"})
    @Final
    SectionRenderDispatcher.RenderSection this$1;

    @Inject(
            method = "doTask(Lnet/minecraft/client/renderer/SectionBufferBuilderPack;)Ljava/util/concurrent/CompletableFuture;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/SectionRenderDispatcher$RenderSection;updateGlobalBlockEntities(Ljava/util/Collection;)V")
    )
    public void afterCompile(SectionBufferBuilderPack sectionBufferBuilderPack, CallbackInfoReturnable<CompletableFuture<SectionRenderDispatcher.SectionTaskResult>> cir,
                             @Local SectionCompiler.Results results){
        List<ColorPointLight> shimmerLights = ((ISectionCompilerResults) (Object) results).getShimmerLights();
        if(this$1 instanceof IRenderSection iRenderSection){
            iRenderSection.setShimmerLights(shimmerLights);
        }
    }

}
