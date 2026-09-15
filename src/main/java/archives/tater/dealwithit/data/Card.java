package archives.tater.dealwithit.data;

import archives.tater.dealwithit.registry.DealWithItRegistries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record Card(Component description) {
    public static final Codec<Card> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ComponentSerialization.CODEC.fieldOf("description").forGetter(Card::description)
    ).apply(instance, Card::new));

    public static final Codec<Holder<Card>> CODEC = RegistryCodecs.holder(DealWithItRegistries.CARD);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Card>> STREAM_CODEC = ByteBufCodecs.holderRegistry(DealWithItRegistries.CARD);
}
