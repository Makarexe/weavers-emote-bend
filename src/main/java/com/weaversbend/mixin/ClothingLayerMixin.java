package com.weaversbend.mixin;

import dev.kosmx.playerAnim.impl.IUpperPartHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xox.labvorty.weaversparadise.renderers.layer.CottonSkirtRenderLayer;
import xox.labvorty.weaversparadise.renderers.layer.HandWarmersRenderLayer;
import xox.labvorty.weaversparadise.renderers.layer.PantsRenderLayer;
import xox.labvorty.weaversparadise.renderers.layer.ThighHighsRenderLayer;
import xox.labvorty.weaversparadise.renderers.layer.UpperwearRenderLayer;

/**
 * playerAnimator по умолчанию считает любой RenderLayer «верхним» и целиком поворачивает его
 * вместе с изгибом торса — из-за этого штаны, гольфы и юбка улетали вслед за грудью.
 * Помечаем слои одежды как «нижние» (так же playerAnimator поступает с бронёй), а рукава
 * поворачиваем сами в миксинах моделей.
 * Шапки (HatRenderLayer) не трогаем — голова должна поворачиваться целиком.
 */
@Mixin({
        UpperwearRenderLayer.class,
        PantsRenderLayer.class,
        ThighHighsRenderLayer.class,
        HandWarmersRenderLayer.class,
        CottonSkirtRenderLayer.class
})
public class ClothingLayerMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void weaversbend$markLowerLayer(CallbackInfo ci) {
        ((IUpperPartHelper) this).setUpperPart(false);
    }
}
