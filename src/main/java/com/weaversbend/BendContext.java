package com.weaversbend;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.kosmx.playerAnim.core.util.Pair;
import dev.kosmx.playerAnim.impl.Helper;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import dev.kosmx.playerAnim.impl.animation.IBendHelper;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Как Emotecraft (playerAnimator + bendy-lib) сгибает игрока:
 * <ul>
 *   <li>у каждой части модели (руки, ноги, торс) куб получает «bend»-мутатор bendy-lib,
 *       и эмоция включает на нём изгиб (локоть, колено, поясница);</li>
 *   <li>{@code ModelPart.copyFrom} (bendy-lib) копирует состояние изгиба — но только в кубы,
 *       у которых такой мутатор тоже зарегистрирован;</li>
 *   <li>изгиб «body» дополнительно поворачивает всё, что выше пояса (голова, руки),
 *       вокруг точки на высоте 6px — это делается поворотом PoseStack.</li>
 * </ul>
 * Модели одежды Weaver's Paradise — свои {@code ModelPart}, которые копируют позу
 * с модели игрока, но без мутатора, поэтому раньше не сгибались. Здесь — общая логика,
 * которую миксины применяют к ним.
 */
public final class BendContext {
    /** Та же высота оси поворота торса, что и в {@link IBendHelper#rotateMatrixStack}. */
    private static final float BODY_PIVOT = 0.375F;

    /** Сущность, которую сейчас рисует LivingEntityRenderer (рендер идёт в одном потоке). */
    @Nullable
    private static LivingEntity current;

    private BendContext() {
    }

    /** @return предыдущая сущность, чтобы восстановить её в {@link #exit}. */
    @Nullable
    public static LivingEntity enter(LivingEntity entity) {
        LivingEntity previous = current;
        current = entity;
        return previous;
    }

    public static void exit(@Nullable LivingEntity previous) {
        current = previous;
    }

    /** Регистрирует на кубе части такой же мутатор, как у соответствующей части игрока. */
    public static void initBend(ModelPart part, Direction direction) {
        IBendHelper.INSTANCE.initBend(part, direction);
    }

    @Nullable
    private static AnimationApplier activeAnimation() {
        if (!Helper.isBendEnabled() || !(current instanceof IAnimatedPlayer player)) {
            return null;
        }
        AnimationApplier animation = player.playerAnimator_getAnimation();
        return animation != null && animation.isActive() ? animation : null;
    }

    /** Изгиб «body», которым playerAnimator поворачивает верхние части (руки, голову). */
    @Nullable
    public static Pair<Float, Float> bodyBend() {
        AnimationApplier animation = activeAnimation();
        return animation == null ? null : animation.getBend("body");
    }

    /** Полный изгиб верхней половины торса: «torso» + «body», как у куба тела игрока. */
    @Nullable
    public static Pair<Float, Float> chestBend() {
        AnimationApplier animation = activeAnimation();
        if (animation == null) {
            return null;
        }
        Pair<Float, Float> torso = animation.getBend("torso");
        Pair<Float, Float> body = animation.getBend("body");
        return new Pair<>(torso.getLeft() + body.getLeft(), torso.getRight() + body.getRight());
    }

    public static void applyBend(PoseStack poseStack, Pair<Float, Float> bend) {
        IBendHelper.rotateMatrixStack(poseStack, bend);
    }

    /** Обратное к {@link #applyBend}: снимает поворот, который playerAnimator навесил на весь слой. */
    public static void undoBend(PoseStack poseStack, Pair<Float, Float> bend) {
        float axisAngle = -bend.getLeft();
        Vector3f axis = new Vector3f((float) Math.cos(axisAngle), 0.0F, (float) Math.sin(axisAngle));
        poseStack.translate(0.0F, BODY_PIVOT, 0.0F);
        poseStack.mulPose(new Quaternionf().rotateAxis(-bend.getRight(), axis));
        poseStack.translate(0.0F, -BODY_PIVOT, 0.0F);
    }
}
