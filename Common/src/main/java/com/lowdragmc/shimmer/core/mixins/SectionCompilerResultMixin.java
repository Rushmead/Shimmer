package com.lowdragmc.shimmer.core.mixins;

import com.google.common.collect.ImmutableList;
import com.lowdragmc.shimmer.client.light.ColorPointLight;
import com.lowdragmc.shimmer.core.ISectionCompilerResults;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(SectionCompiler.Results.class)
public class SectionCompilerResultMixin implements ISectionCompilerResults {
    @Unique
    ImmutableList.Builder<ColorPointLight> shimmer$lights = new ImmutableList.Builder<>();
    @Override
    public List<ColorPointLight> getShimmerLights() {
        return shimmer$lights.build();
    }

    @Override
    public void addShimmerLight(ColorPointLight light) {
        shimmer$lights.add(light);
    }

}
