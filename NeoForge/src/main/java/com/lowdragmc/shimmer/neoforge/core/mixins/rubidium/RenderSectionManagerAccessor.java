package com.lowdragmc.shimmer.neoforge.core.mixins.rubidium;

import org.embeddedt.embeddium.impl.render.chunk.RenderSectionManager;
import org.embeddedt.embeddium.impl.render.chunk.lists.SortedRenderLists;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author KilaBash
 * @date 2022/05/31
 * @implNote RenderSectionManagerAccessor
 */
@Mixin(RenderSectionManager.class)
public interface RenderSectionManagerAccessor {
    @Accessor(remap = false)
    SortedRenderLists getRenderLists();
}
