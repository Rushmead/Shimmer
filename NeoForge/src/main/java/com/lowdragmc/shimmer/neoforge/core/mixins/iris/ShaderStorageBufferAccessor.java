package com.lowdragmc.shimmer.neoforge.core.mixins.iris;

import net.irisshaders.iris.gl.buffer.BuiltShaderStorageInfo;
import net.irisshaders.iris.gl.buffer.ShaderStorageBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = ShaderStorageBuffer.class, remap = false)
public interface ShaderStorageBufferAccessor {
    @Invoker void callDestroy();
    @Accessor
    BuiltShaderStorageInfo getInfo();
}
