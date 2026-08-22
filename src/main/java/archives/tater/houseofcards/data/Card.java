package archives.tater.houseofcards.data;

import archives.tater.houseofcards.registry.HouseOfCardsComponents;
import archives.tater.houseofcards.registry.HouseOfCardsRegistries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record Card(Component description) {
    public static final Codec<Card> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ComponentSerialization.CODEC.fieldOf("description").forGetter(Card::description)
    ).apply(instance, Card::new));

    public static final Codec<Holder<Card>> CODEC = RegistryFixedCodec.create(HouseOfCardsRegistries.CARD);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Card>> STREAM_CODEC = ByteBufCodecs.holderRegistry(HouseOfCardsRegistries.CARD);

    public static void flip(ItemStack stack, Player player) {
        if (stack.has(HouseOfCardsComponents.FACE_DOWN))
            stack.remove(HouseOfCardsComponents.FACE_DOWN);
        else
            stack.set(HouseOfCardsComponents.FACE_DOWN, Unit.INSTANCE);

        player.playSound(SoundEvents.BOOK_PAGE_TURN);
    }
}
