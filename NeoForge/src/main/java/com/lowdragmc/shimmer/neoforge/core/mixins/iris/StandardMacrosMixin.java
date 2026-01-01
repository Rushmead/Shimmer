package com.lowdragmc.shimmer.neoforge.core.mixins.iris;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.sugar.Local;
import com.lowdragmc.shimmer.ShimmerConstants;
import net.irisshaders.iris.gl.shader.StandardMacros;
import net.irisshaders.iris.helpers.StringPair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = StandardMacros.class, remap = false)
public abstract class StandardMacrosMixin {

    @Shadow
    private static void define(List<StringPair> list, String s) {
    }

    @Inject(method = "createStandardEnvironmentDefines",
            at = @At(value = "INVOKE", ordinal = 1,
                    target = "Lnet/irisshaders/iris/gl/shader/StandardMacros;define(Ljava/util/List;Ljava/lang/String;Ljava/lang/String;)V")
    )
    private static void injectMacro(CallbackInfoReturnable<ImmutableList<StringPair>> cir, @Local ArrayList<StringPair> list) {
        define(list, ShimmerConstants.SHIMMER_IDENTIFIER_MACRO);
    }
}
