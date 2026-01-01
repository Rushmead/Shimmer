package com.lowdragmc.shimmer.neoforge.compat.oculus;

import com.lowdragmc.shimmer.neoforge.core.mixins.iris.GlResourceAccessor;
import net.irisshaders.iris.gl.framebuffer.GlFramebuffer;
import net.minecraft.client.Minecraft;

public class GBufferMainRenderTarget {
    private GBufferMainRenderTarget() {
        throw new RuntimeException("don't construct utility class");
    }

    public static GlFramebuffer lastGBufferUsedFrameBuffer;

    public static void autoBind() {
        if (lastGBufferUsedFrameBuffer != null) {
            if (((GlResourceAccessor) lastGBufferUsedFrameBuffer).getIsValid()) {
                lastGBufferUsedFrameBuffer.bind();
                return;
            } else {
                lastGBufferUsedFrameBuffer = null;
            }
        }
        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
    }

    public static int getID() {
        if (lastGBufferUsedFrameBuffer != null) {
            if (((GlResourceAccessor) lastGBufferUsedFrameBuffer).getIsValid()) {
                return lastGBufferUsedFrameBuffer.getColorAttachment(0);
            } else {
                lastGBufferUsedFrameBuffer = null;
            }
        }
        return Minecraft.getInstance().getMainRenderTarget().getColorTextureId();
    }
}
