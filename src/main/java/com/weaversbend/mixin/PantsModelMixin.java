package com.weaversbend.mixin;

import com.weaversbend.BendContext;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xox.labvorty.weaversparadise.model.PantsModel;

/** Штаны: колени и пояс. Ноги — «нижние» части, наклон торса их не поворачивает. */
@Mixin(PantsModel.class)
public class PantsModelMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void weaversbend$initBends(ModelPart root, CallbackInfo ci) {
        PantsModel<?> self = (PantsModel<?>) (Object) this;
        BendContext.initBend(self.Body, Direction.DOWN);
        BendContext.initBend(self.RightLeg, Direction.UP);
        BendContext.initBend(self.LeftLeg, Direction.UP);
    }
}
