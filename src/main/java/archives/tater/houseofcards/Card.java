package archives.tater.houseofcards;

import archives.tater.houseofcards.registry.HouseOfCardsRegistries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.function.Consumer;

public record Card(Component description) implements TooltipProvider {
    public static final Codec<Card> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ComponentSerialization.CODEC.fieldOf("description").forGetter(Card::description)
    ).apply(instance, Card::new));

    public static final Codec<Holder<Card>> CODEC = RegistryFixedCodec.create(HouseOfCardsRegistries.CARD);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Card>> STREAM_CODEC = ByteBufCodecs.holderRegistry(HouseOfCardsRegistries.CARD);

    public static final Codec<HolderSet<Card>> SET_CODEC = RegistryCodecs.homogeneousList(HouseOfCardsRegistries.CARD, true);
    public static final StreamCodec<RegistryFriendlyByteBuf, HolderSet<Card>> SET_STREAM_CODEC = ByteBufCodecs.holderSet(HouseOfCardsRegistries.CARD);

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        consumer.accept(description);
    }
}
