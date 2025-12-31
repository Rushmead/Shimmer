package com.lowdragmc.shimmer.neoforge.core.mixins.oculus;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.lowdragmc.shimmer.comp.iris.ShaderpackInjection;
import net.irisshaders.iris.shaderpack.programs.ProgramSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Function;

@Mixin(ProgramSet.class)
public abstract class ProgramSetMixin {
    @ModifyExpressionValue(
            method = "readProgramSource(Lnet/irisshaders/iris/shaderpack/include/AbsolutePackPath;Ljava/util/function/Function;Ljava/lang/String;Lnet/irisshaders/iris/shaderpack/programs/ProgramSet;Lnet/irisshaders/iris/shaderpack/properties/ShaderProperties;Lnet/irisshaders/iris/gl/blending/BlendModeOverride;Z)Lnet/irisshaders/iris/shaderpack/programs/ProgramSource;",
            at = @At(value = "INVOKE",
                    target = "Ljava/util/function/Function;apply(Ljava/lang/Object;)Ljava/lang/Object;",
                    ordinal = 0)
            , remap = false)
    private static Object injectShaderpackVsh(Object value, @Local(argsOnly = true) String program){
        if (program.equals("gbuffers_terrain") && value instanceof String vsh) {
            return ShaderpackInjection.TERRAIN.injectTerrainVsh(vsh);
        }
        return value;
    }

    @ModifyExpressionValue(
            method = "readProgramSource(Lnet/irisshaders/iris/shaderpack/include/AbsolutePackPath;Ljava/util/function/Function;Ljava/lang/String;Lnet/irisshaders/iris/shaderpack/programs/ProgramSet;Lnet/irisshaders/iris/shaderpack/properties/ShaderProperties;Lnet/irisshaders/iris/gl/blending/BlendModeOverride;Z)Lnet/irisshaders/iris/shaderpack/programs/ProgramSource;",
            at = @At(value = "INVOKE",
                    target = "Ljava/util/function/Function;apply(Ljava/lang/Object;)Ljava/lang/Object;",
                    ordinal = 2)
            , remap = false)
    private static Object injectShaderpackFsh(Object value, @Local(argsOnly = true) String program){
        if (program.equals("gbuffers_terrain") && value instanceof String fsh) {
            return ShaderpackInjection.TERRAIN.injectTerrainFsh(fsh);
        }
        return value;
    }

}
