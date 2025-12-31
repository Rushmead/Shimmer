package com.lowdragmc.shimmer.client;

import com.mojang.blaze3d.vertex.VertexConsumer;

public interface HSBBufferBuilder {

    VertexConsumer shimmer$setHSB(float h, float s, float b, float alpha);

}
