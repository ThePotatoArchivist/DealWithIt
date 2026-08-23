package archives.tater.dealwithit.registry;

import archives.tater.dealwithit.DealWithIt;
import archives.tater.dealwithit.component.DeckContents;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

import static net.minecraft.util.Util.makeDescriptionId;

public interface DealWithItCreativeTabs {

    CreativeModeTab CARDS = register(DealWithIt.id("cards"), builder -> builder
            .icon(Items.PAPER::getDefaultInstance)
            .displayItems((parameters, output) -> {
                parameters.holders().lookupOrThrow(DealWithItRegistries.DECK).listElements().forEach(deck ->
                        output.accept(DeckContents.createStack(deck)));
            })
    );

    private static CreativeModeTab register(Identifier id, Consumer<CreativeModeTab.Builder> init) {
        var builder = FabricCreativeModeTab.builder();
        builder.title(Component.translatable(makeDescriptionId("itemGroup", id)));
        init.accept(builder);
        return Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, id, builder.build());
    }

    static void init() {

    }
}
