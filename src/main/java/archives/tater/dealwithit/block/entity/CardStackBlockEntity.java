package archives.tater.dealwithit.block.entity;

import archives.tater.dealwithit.DealWithIt;
import archives.tater.dealwithit.block.CardStackBlock;
import archives.tater.dealwithit.component.CardComponent;
import archives.tater.dealwithit.registry.DealWithItBlockEntities;
import archives.tater.dealwithit.registry.DealWithItComponents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static archives.tater.dealwithit.Util.toShuffledList;
import static java.lang.Math.ceilDiv;
import static net.minecraft.util.Mth.clamp;

public class CardStackBlockEntity extends BlockEntity {

    public static final int FULL_HEIGHT = 64;
    private final List<CardInstance> cards = new LinkedList<>();

    private static final Codec<List<CardInstance>> CARDS_CODEC = CardInstance.CODEC.listOf();

    public CardStackBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(DealWithItBlockEntities.CARD_STACK, worldPosition, blockState);
    }

    public boolean pushCard(ItemStack stack, float angle, boolean flip) {
        var instance = CardInstance.fromStack(stack, angle, flip);
        if (instance == null) return false;

        cards.add(instance);
        markUpdated();
        updateHeight();

        return true;
    }

    public ItemStack popCard(boolean flip) {
        if (cards.isEmpty()) return ItemStack.EMPTY;

        var instance = cards.removeLast();
        markUpdated();
        updateHeight();

        return instance.toStack(flip);
    }

    public List<CardInstance> getCards() {
        return cards;
    }

    public void setCards(Collection<CardInstance> cards) {
        this.cards.clear();
        this.cards.addAll(cards);
    }

    public static List<CardInstance> getCards(ItemStack stack, float angle, boolean flip, RandomSource random) {
        var single = CardInstance.fromStack(stack, angle, flip);
        if (single != null) return List.of(single);

        var deck = stack.get(DealWithItComponents.DECK_CONTENTS);
        if (deck != null) return deck.cards().object2IntEntrySet().stream()
                .<CardInstance>mapMulti((entry, yield) -> {
                    var instance = new CardInstance(new CardComponent(deck.deck(), entry.getKey()), angle, !flip);
                    for (int i = 0; i < entry.getIntValue(); i++) yield.accept(instance);
                })
                .collect(toShuffledList(random));

        return List.of();
    }

    public static int getHeight(int count) {
        return clamp(ceilDiv(count * 16, FULL_HEIGHT), 1, 16);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        var level = getLevel();
        if (level == null) return;

        for (var instance : cards)
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), instance.toStack(false));
    }

    private void updateHeight() {
        getLevel().setBlockAndUpdate(getBlockPos(), cards.isEmpty() ? Blocks.AIR.defaultBlockState() : getBlockState().setValue(CardStackBlock.HEIGHT, getHeight(cards.size())));
    }

    private void markUpdated() {
        setChanged();
        getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        output.store("cards", CARDS_CODEC, cards);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        cards.clear();
        input.read("cards", CARDS_CODEC).ifPresent(cards::addAll);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public CompoundTag getUpdateTag(final HolderLookup.Provider registries) {
        try (var reporter = new ProblemReporter.ScopedCollector(problemPath(), DealWithIt.LOGGER)) {
            var output = TagValueOutput.createWithContext(reporter, registries);
            saveAdditional(output);
            return output.buildResult();
        }
    }

    public record CardInstance(CardComponent card, float angle, boolean faceDown) {
        public static final Codec<CardInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                CardComponent.MAP_CODEC.forGetter(CardInstance::card),
                Codec.FLOAT.fieldOf("angle").forGetter(CardInstance::angle),
                Codec.BOOL.fieldOf("face_down").forGetter(CardInstance::faceDown)
        ).apply(instance, CardInstance::new));

        public ItemStack toStack(boolean flip) {
            var stack = CardComponent.createStack(card);
            if (flip ^ faceDown) stack.set(DealWithItComponents.FACE_DOWN, Unit.INSTANCE);
            return stack;
        }

        public static @Nullable CardInstance fromStack(ItemStack stack, float angle, boolean flip) {
            var card = stack.get(DealWithItComponents.CARD);
            if (card == null) return null;
            return new CardInstance(card, angle, flip ^ stack.has(DealWithItComponents.FACE_DOWN));
        }
    }
}
