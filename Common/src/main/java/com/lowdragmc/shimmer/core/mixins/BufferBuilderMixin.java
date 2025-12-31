package com.lowdragmc.shimmer.core.mixins;

import com.lowdragmc.shimmer.client.HSBBufferBuilder;
import com.lowdragmc.shimmer.client.auxiliaryScreen.HsbColorWidget;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BufferBuilder.class)
public abstract class BufferBuilderMixin implements HSBBufferBuilder {
    @Shadow
    protected abstract long beginElement(VertexFormatElement element);

    @Override
    public VertexConsumer shimmer$setHSB(float h, float s, float b, float alpha) {
        //TODO: Not the problem!
        long l = beginElement(HsbColorWidget.HSB_Alpha);
        if (l != -1L) {
            MemoryUtil.memPutFloat(l, h);
            MemoryUtil.memPutFloat(l + 4L, s);
            MemoryUtil.memPutFloat(l + 8L, b);
            MemoryUtil.memPutFloat(l + 12L, alpha);
        }
        return (VertexConsumer) this;
    }
}
