package com.lowdragmc.shimmer.core;

import com.lowdragmc.shimmer.client.light.ColorPointLight;

import java.util.List;

public interface ISectionCompilerResults {
    List<ColorPointLight> getShimmerLights();
    void addShimmerLight(ColorPointLight light);
}
