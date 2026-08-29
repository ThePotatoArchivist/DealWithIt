package archives.tater.dealwithit.registry;

import archives.tater.dealwithit.DealWithIt;

import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public interface DealWithItDataPacks {

    Identifier PLAYING_CARDS = register("playing_cards", true);
    Identifier UNO = register("uno", true);
    Identifier NERTZ = register("nertz", false);

    private static Identifier register(String path, boolean defaultEnabled) {
        var id = DealWithIt.id(path);
        ResourceLoader.registerBuiltinPack(
                id,
                FabricLoader.getInstance().getModContainer(DealWithIt.MOD_ID).orElseThrow(),
                Component.translatable(id.toLanguageKey("dataPack", "name")),
                defaultEnabled ? PackActivationType.DEFAULT_ENABLED : PackActivationType.NORMAL
        );
        return id;
    }

    static void init() {

    }
}
