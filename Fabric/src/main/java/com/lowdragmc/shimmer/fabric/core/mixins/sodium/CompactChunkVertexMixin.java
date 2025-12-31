package com.lowdragmc.shimmer.fabric.core.mixins.sodium;

import com.llamalad7.mixinextras.sugar.Local;
import net.caffeinemc.mods.sodium.client.render.chunk.vertex.format.ChunkVertexEncoder;
import net.caffeinemc.mods.sodium.client.render.chunk.vertex.format.impl.CompactChunkVertex;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = CompactChunkVertex.class, remap = false)
public abstract class CompactChunkVertexMixin {

    @Redirect(method = "lambda$getEncoder$0", at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/vertex/format/impl/CompactChunkVertex;packLightAndData(III)I", ordinal = 0))
    private static int injectMaterialForBloom(int light, int material, int section, @Local ChunkVertexEncoder.Vertex vertex) {
        if ((vertex.light & 0x100) != 0) {
            material |= (0x01 << 4);
        }
        if ((light & 0x100) != 0) {
            light = 15 | 15 << 4;
        }
        return (light & '\uffff') | (material & 255) << 16 | (section & 255) << 24;
    }
}
