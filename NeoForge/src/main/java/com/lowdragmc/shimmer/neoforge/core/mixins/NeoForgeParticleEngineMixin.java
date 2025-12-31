package com.lowdragmc.shimmer.neoforge.core.mixins;

import com.google.common.collect.Maps;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.lowdragmc.shimmer.TracedGLState;
import com.lowdragmc.shimmer.client.postprocessing.IPostParticleType;
import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import com.lowdragmc.shimmer.core.IParticleDescription;
import com.lowdragmc.shimmer.core.IParticleEngine;
import com.lowdragmc.shimmer.core.mixins.ShimmerMixinPlugin;
import com.lowdragmc.shimmer.platform.Services;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleDescription;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;

/**
 * @author KilaBash
 * @date 2022/05/02
 * @implNote ParticleEngineMixin, inject particle postprocessing
 */
@Mixin(ParticleEngine.class)
public abstract class NeoForgeParticleEngineMixin implements IParticleEngine {
    @Shadow
    @Nullable
    protected abstract <T extends ParticleOptions> Particle makeParticle(T pParticleData, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed);

    @Shadow
    public abstract void add(Particle pEffect);

    @Shadow
    @Final
    private Map<ParticleRenderType, Queue<Particle>> particles;

    private final Map<ResourceLocation, String> PARTICLE_EFFECT = Maps.newHashMap();

    @Nullable
    public Particle createPostParticle(PostProcessing postProcessing, ParticleOptions pParticleData, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
        Particle particle = makeParticle(pParticleData, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        if (particle != null) {
            particle = Services.PLATFORM.createPostParticle(particle, postProcessing);
            add(particle);
            return particle;
        } else {
            return null;
        }
    }

    @ModifyReceiver(method = "render(Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/culling/Frustum;Ljava/util/function/Predicate;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/particle/ParticleRenderType;begin(Lcom/mojang/blaze3d/vertex/Tesselator;Lnet/minecraft/client/renderer/texture/TextureManager;)Lcom/mojang/blaze3d/vertex/BufferBuilder;"))
    private ParticleRenderType injectRenderPre(ParticleRenderType instance, Tesselator tesselator, TextureManager textureManager) {
        if (instance instanceof IPostParticleType && this.particles.get(instance).size() > 0) {
            PostProcessing postProcessing = ((IPostParticleType) instance).getPost();
            postProcessing.getPostTarget(false).bindWrite(false);
            postProcessing.hasParticle();
        }
        return instance;
    }


    @Inject(method = "render(Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/culling/Frustum;Ljava/util/function/Predicate;)V",
            at = @At(value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/BufferBuilder;build()Lcom/mojang/blaze3d/vertex/MeshData;",
                    shift = At.Shift.AFTER))
    private void injectRenderPost(CallbackInfo ci) {
        RenderTarget mainRenderTarget = Minecraft.getInstance().getMainRenderTarget();
        if (TracedGLState.bindFrameBuffer != mainRenderTarget.frameBufferId){
            mainRenderTarget.bindWrite(false);
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/culling/Frustum;Ljava/util/function/Predicate;)V",
            at = @At(value = "RETURN"), remap = false)
    private void injectRenderReturn(LightTexture arg, Camera arg2, float f, Frustum frustum, Predicate<ParticleRenderType> renderTypePredicate, CallbackInfo ci) {
        for (PostProcessing postProcessing : PostProcessing.values()) {
            postProcessing.renderParticlePost();
        }
    }

    @ModifyExpressionValue(method = "loadParticleDescription",
        at= @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleDescription;fromJson(Lcom/google/gson/JsonObject;)Lnet/minecraft/client/particle/ParticleDescription;"))
    private ParticleDescription injectLoad(ParticleDescription particleDescription,ResourceLocation registryName, Resource resource){
        if (particleDescription instanceof IParticleDescription description && description.getEffect() != null) {
            PARTICLE_EFFECT.put(registryName, description.getEffect());
        }
        return particleDescription;
    }

    @Inject(method = "reload", at = @At(value = "HEAD"))
    private void injectReload(PreparableReloadListener.PreparationBarrier $$0, ResourceManager $$1, ProfilerFiller $$2, ProfilerFiller $$3, Executor $$4, Executor $$5, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        PARTICLE_EFFECT.clear();
    }

    @Inject(method = "createParticle", at = @At(value = "HEAD"), cancellable = true)
    private void injectCreateParticle(ParticleOptions particleOptions, double x, double y, double z, double sx, double sy, double sz, CallbackInfoReturnable<Particle> cir) {
        ResourceLocation name = BuiltInRegistries.PARTICLE_TYPE.getKey(particleOptions.getType());
        if (!ShimmerMixinPlugin.IS_OPT_LOAD) {
            if (PARTICLE_EFFECT.containsKey(name)) {
                PostProcessing postProcessing = PostProcessing.getPost(PARTICLE_EFFECT.get(name));
                if (postProcessing != null) {
                    cir.setReturnValue(createPostParticle(postProcessing, particleOptions, x, y, z, sx, sy, sz));
                }
            } else if (PostProcessing.BLOOM_PARTICLE.contains(name)) {
                cir.setReturnValue(createPostParticle(PostProcessing.getBlockBloom(), particleOptions, x, y, z, sx, sy, sz));
            }
        }
    }
}

