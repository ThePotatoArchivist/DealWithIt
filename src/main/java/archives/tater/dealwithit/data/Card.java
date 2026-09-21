package archives.tater.dealwithit.data;

import archives.tater.dealwithit.registry.DealWithItRegistries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;

public record Card(Component description) {
    public static final Codec<Card> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ComponentSerialization.CODEC.fieldOf("description").forGetter(Card::description)
    ).apply(instance, Card::new));

    public static final Codec<Holder<Card>> CODEC = RegistryFixedCodec.create(DealWithItRegistries.CARD);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Card>> STREAM_CODEC = ByteBufCodecs.holderRegistry(DealWithItRegistries.CARD);
}
