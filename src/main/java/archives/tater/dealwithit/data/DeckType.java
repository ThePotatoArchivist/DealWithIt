package archives.tater.dealwithit.data;

import archives.tater.dealwithit.registry.DealWithItRegistries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.codec.RegistryCodecs;

public record DeckType(
        CardSet cards
) {
    public static final Codec<DeckType> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CardSet.CODEC.fieldOf("cards").forGetter(DeckType::cards)
    ).apply(instance, DeckType::new));

    public static final Codec<Holder<DeckType>> CODEC = RegistryCodecs.holder(DealWithItRegistries.DECK_TYPE);

    public int size() {
        return cards.count();
    }
}
