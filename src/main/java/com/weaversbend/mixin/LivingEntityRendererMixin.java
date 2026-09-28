package com.weaversbend.mixin;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import com.weaversbend.BendContext;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Запоминает, чья модель сейчас рисуется: модели одежды WP сами сущность не получают. */
@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    private static final String RENDER =
            "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V";

    @Inject(method = RENDER, at = @At("HEAD"))
    private void weaversbend$enter(LivingEntity entity, float yaw, float partialTicks, PoseStack poseStack,
                                   MultiBufferSource buffer, int light, CallbackInfo ci,
                                   @Share("previous") LocalRef<LivingEntity> previous) {
        previous.set(BendContext.enter(entity));
    }

    @Inject(method = RENDER, at = @At("RETURN"))
    private void weaversbend$exit(LivingEntity entity, float yaw, float partialTicks, PoseStack poseStack,
                                  MultiBufferSource buffer, int light, CallbackInfo ci,
                                  @Share("previous") LocalRef<LivingEntity> previous) {
        BendContext.exit(previous.get());
    }
}
