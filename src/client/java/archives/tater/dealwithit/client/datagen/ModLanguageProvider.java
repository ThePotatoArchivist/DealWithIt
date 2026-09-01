package archives.tater.dealwithit.client.datagen;

import archives.tater.dealwithit.component.DeckContents;
import archives.tater.dealwithit.registry.DealWithItCreativeTabs;
import archives.tater.dealwithit.registry.DealWithItDataPacks;
import archives.tater.dealwithit.registry.DealWithItItems;
import archives.tater.dealwithit.registry.DealWithItSounds;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;

import java.util.concurrent.CompletableFuture;

public class ModLanguageProvider extends FabricLanguageProvider {
    public ModLanguageProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    private static void addPack(TranslationBuilder translationBuilder, Identifier id, String name, String description) {
        translationBuilder.add(id.toLanguageKey("dataPack", "name"), name);
        translationBuilder.add(id.toLanguageKey("dataPack", "description"), description);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
        DeckProviders.PlayingCards.PLAYING_CARDS.generateTranslations(translationBuilder);
        DeckProviders.PlayingCards.JOKERS.generateTranslations(translationBuilder);
        DeckProviders.PlayingCards.COLORED.generateTranslations(translationBuilder);
        DeckProviders.PlayingCards.COLORED_JOKERS.generateTranslations(translationBuilder);
        DeckProviders.UNO.generateTranslations(translationBuilder);

        addPack(translationBuilder, DealWithItDataPacks.PLAYING_CARDS, "Playing Cards", "Standard 52-card deck");
        addPack(translationBuilder, DealWithItDataPacks.JOKERS, "Jokers", "54-card deck with 2 Jokers");
        addPack(translationBuilder, DealWithItDataPacks.COLORED, "Colored", "Colored decks");
        addPack(translationBuilder, DealWithItDataPacks.COLORED_JOKERS, "Colored Jokers", "Colored decks with Jokers");
        addPack(translationBuilder, DealWithItDataPacks.UNO, "Uno", "Regular Uno");

        translationBuilder.add(DealWithItItems.CARD, "Card");
        translationBuilder.add(DealWithItItems.CARD_STACK, "Card Stack");
        translationBuilder.add(DealWithItItems.CARD_BOX, "Card Box");
        translationBuilder.add(DealWithItItems.BLANK_CARD_BOX, "Blank Card Box");
        translationBuilder.add(DeckContents.FILL, "%s/%s");

        translationBuilder.add(((TranslatableContents) DealWithItCreativeTabs.CARDS.getDisplayName().getContents()).getKey(), "Deal With It");

        translationBuilder.add(DealWithItSounds.CARD_STACK_SHUFFLE, "Cards shuffled");
        translationBuilder.add(DealWithItSounds.CARD_STACK_PLACE, "Card placed");
        translationBuilder.add(DealWithItSounds.CARD_STACK_PICKUP, "Card taken");
        translationBuilder.add(DealWithItSounds.CARD_BOX_INSERT, "Cards collected");
        translationBuilder.add(DealWithItSounds.CARD_BOX_INSERT_FAIL, "Card box full");
        translationBuilder.add(DealWithItSounds.CARD_FLIP, "Cards flips");
    }
}
