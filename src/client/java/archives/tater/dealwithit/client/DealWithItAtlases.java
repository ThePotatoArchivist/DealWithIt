package archives.tater.dealwithit.client;

import archives.tater.dealwithit.DealWithIt;

import net.fabricmc.fabric.api.client.rendering.v1.AtlasRegistry;

import net.minecraft.client.renderer.SpriteMapper;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.sprite.AtlasManager.AtlasConfig;
import net.minecraft.resources.Identifier;

public interface DealWithItAtlases {
    AtlasConfig CARDS = register("cards");
    RenderType CARDS_RENDER_TYPE = RenderTypes.itemCutout(CARDS.textureId());

    SpriteMapper CARD_MAPPER = new SpriteMapper(CARDS.textureId(), DealWithIt.MOD_ID + "/card");
    SpriteMapper DECK_BACK_MAPPER = new SpriteMapper(CARDS.textureId(), DealWithIt.MOD_ID + "/deck_back");

    private static AtlasConfig register(Identifier id) {
        var config = new AtlasConfig(AtlasRegistry.generateTextureLocation(id), id, false);
        AtlasRegistry.register(config);
        return config;
    }

    private static AtlasConfig register(String path) {
        return register(DealWithIt.id(path));
    }

    static void init() {

    }
}
