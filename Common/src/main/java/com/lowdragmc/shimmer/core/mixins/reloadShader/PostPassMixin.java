package com.lowdragmc.shimmer.core.mixins.reloadShader;

import com.lowdragmc.shimmer.client.shader.ReloadShaderManager;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.IOException;


@Mixin(PostPass.class)
public abstract class PostPassMixin {

    @Redirect(method = "<init>", at = @At(value = "NEW", target = "Lnet/minecraft/client/renderer/EffectInstance;"))
    private EffectInstance redirectEffectInstance(ResourceProvider resourceProvider, String name) throws IOException {
        return ReloadShaderManager.backupNewEffectInstance(resourceProvider,name);
    }

}