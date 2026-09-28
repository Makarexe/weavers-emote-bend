package com.weaversbend;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

/**
 * Аддон целиком состоит из клиентских миксинов (см. weaversbend.mixins.json),
 * класс нужен только чтобы NeoForge увидел мод.
 */
@Mod(value = WeaversBend.MOD_ID, dist = Dist.CLIENT)
public final class WeaversBend {
    public static final String MOD_ID = "weaversbend";
}
