package archives.tater.dealwithit.block.entity;

import archives.tater.dealwithit.DealWithIt;
import archives.tater.dealwithit.block.CardStackBlock;
import archives.tater.dealwithit.component.CardComponent;
import archives.tater.dealwithit.component.CardInstance;
import archives.tater.dealwithit.component.CardStack;
import archives.tater.dealwithit.registry.DealWithItBlockEntities;
import archives.tater.dealwithit.registry.DealWithItComponents;
import archives.tater.dealwithit.registry.DealWithItItems;

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

import org.jetbrains.annotations.UnmodifiableView;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import static archives.tater.dealwithit.Util.toShuffledList;
import static java.lang.Math.ceilDiv;
import static net.minecraft.util.Mth.clamp;

public class CardStackBlockEntity extends BlockEntity {

    public static final int FULL_HEIGHT = 128;
    private final List<WorldCardInstance> cards = new LinkedList<>();

    private static final Codec<List<WorldCardInstance>> CARDS_CODEC = WorldCardInstance.CODEC.listOf();

    public CardStackBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(DealWithItBlockEntities.CARD_STACK, worldPosition, blockState);
    }

    public boolean pushCard(ItemStack stack, float angle, boolean flip) {
        var instance = WorldCardInstance.fromStack(stack, angle, flip);
        if (instance == null) return false;

        pushCard(instance);

        return true;
    }

    public void pushCard(CardComponent card, boolean faceDown, float angle) {
        pushCard(new WorldCardInstance(card, angle, faceDown));
    }

    private void pushCard(WorldCardInstance instance) {
        cards.add(instance);
        markUpdated();
        updateHeight();
    }

    public ItemStack popCard(boolean flip) {
        if (cards.isEmpty()) return ItemStack.EMPTY;

        var instance = cards.removeLast();
        markUpdated();
        updateHeight();

        return instance.toStack(flip);
    }

    public @UnmodifiableView List<WorldCardInstance> getCards() {
        return cards;
    }

    public void setCards(Collection<WorldCardInstance> cards) {
        this.cards.clear();
        this.cards.addAll(cards);
        updateHeight();
        setChanged();
    }

    public boolean removeIf(Predicate<WorldCardInstance> condition) {
        if (!cards.removeIf(condition)) return false;
        updateHeight();
        setChanged();
        return true;
    }

    public static List<WorldCardInstance> getCards(ItemStack stack, float angle, boolean secondaryActive, RandomSource random) {
        var single = WorldCardInstance.fromStack(stack, angle, secondaryActive);
        if (single != null) return List.of(single);

        var deck = stack.get(DealWithItComponents.DECK_CONTENTS);
        if (deck != null) return deck.cards().stream()
                .map(card -> new WorldCardInstance(new CardComponent(deck.deck(), card), angle, !secondaryActive))
                .collect(toShuffledList(random));

        var cardStack = stack.get(DealWithItComponents.CARDS);
        if (cardStack != null && !cardStack.cards().isEmpty()) return secondaryActive
            ? cardStack.cards().stream().map(cardInstance -> new WorldCardInstance(cardInstance.card(), angle, cardInstance.faceDown())).toList()
            : List.of(new WorldCardInstance(cardStack.cards().getLast().card(), angle, cardStack.cards().getLast().faceDown()));

        return List.of();
    }

    public static int getHeight(int count) {
        return clamp(ceilDiv(count * 16, FULL_HEIGHT), 1, 16);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        var level = getLevel();
        if (level == null) return;

        var stack = DealWithItItems.CARD_STACK.getDefaultInstance();
        stack.set(DealWithItComponents.CARDS, new CardStack(cards.stream().map(WorldCardInstance::toStackEntry).toList()));
        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
    }

    public void updateHeight() {
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

    public record WorldCardInstance(CardComponent card, float angle, boolean faceDown) {
        public static final Codec<WorldCardInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                CardComponent.MAP_CODEC.forGetter(WorldCardInstance::card),
                Codec.FLOAT.fieldOf("angle").forGetter(WorldCardInstance::angle),
                Codec.BOOL.fieldOf("face_down").forGetter(WorldCardInstance::faceDown)
        ).apply(instance, WorldCardInstance::new));

        public CardInstance toStackEntry() {
            return new CardInstance(card, faceDown);
        }

        public ItemStack toStack(boolean flip) {
            var stack = CardComponent.createStack(card);
            if (flip ^ faceDown) stack.set(DealWithItComponents.FACE_DOWN, Unit.INSTANCE);
            return stack;
        }

        public static CardStackBlockEntity.@Nullable WorldCardInstance fromStack(ItemStack stack, float angle, boolean flip) {
            var card = stack.get(DealWithItComponents.CARD);
            if (card == null) return null;
            return new WorldCardInstance(card, angle, flip ^ stack.has(DealWithItComponents.FACE_DOWN));
        }
    }
}
