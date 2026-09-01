package archives.tater.dealwithit.block;

import archives.tater.dealwithit.block.entity.CardStackBlockEntity;
import archives.tater.dealwithit.block.entity.CardStackBlockEntity.WorldCardInstance;
import archives.tater.dealwithit.component.CardInstance;
import archives.tater.dealwithit.component.CardStack;
import archives.tater.dealwithit.component.DeckContents;
import archives.tater.dealwithit.data.CardSet;
import archives.tater.dealwithit.registry.DealWithItBlocks;
import archives.tater.dealwithit.registry.DealWithItComponents;
import archives.tater.dealwithit.registry.DealWithItSounds;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jspecify.annotations.Nullable;

import java.util.List;

import static archives.tater.dealwithit.Util.toShuffledList;

public class CardStackBlock extends BaseEntityBlock {
    public static final IntegerProperty HEIGHT = IntegerProperty.create("height", 1, 16);

    private static final VoxelShape[] SHAPES = Block.boxes(15, height -> Block.column(16, 0, height + 1));

    public CardStackBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(HEIGHT, 1));
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof CardStackBlockEntity blockEntity)) return InteractionResult.PASS;

        if (hand == InteractionHand.MAIN_HAND && itemStack.isEmpty()) {
            var stack = blockEntity.popCard(player.isSecondaryUseActive());
            if (stack.isEmpty()) return InteractionResult.FAIL;

            player.setItemInHand(hand, stack);
            level.playSound(player, pos, DealWithItSounds.CARD_STACK_PICKUP, SoundSource.BLOCKS);

            return InteractionResult.SUCCESS;
        }

        var contents = itemStack.get(DealWithItComponents.DECK_CONTENTS);
        if (contents != null) {
            var cards = contents.mutableCards();
            var anyRemoved = blockEntity.removeIf(instance -> DeckContents.tryInsert(instance.card(), contents.deck(), cards));
            if (!anyRemoved) return InteractionResult.FAIL;

            itemStack.set(DealWithItComponents.DECK_CONTENTS, contents.withCards(cards));
            level.playSound(player, pos, DealWithItSounds.CARD_BOX_INSERT, SoundSource.BLOCKS);

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return Block.isFaceFull(level.getBlockState(pos.below()).getCollisionShape(level, pos.below()), Direction.UP);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        return directionToNeighbour == Direction.DOWN && !canSurvive(state, level, pos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
    }

    public static boolean placeStack(Player player, UseOnContext useContext) {
        var stack = useContext.getItemInHand();
        var context = new BlockPlaceContext(useContext);
        if (!context.canPlace()) return false;

        var initialState = DealWithItBlocks.CARD_STACK.getStateForPlacement(context);
        if (initialState == null) return false;
        if (!initialState.canSurvive(context.getLevel(), context.getClickedPos())) return false; // height shouldn't affect placement

        var cards = extractCards(stack, player, useContext.getHand(), player.isSecondaryUseActive(), player.getRandom());
        if (cards.isEmpty()) return false;

        var state = initialState.setValue(HEIGHT, CardStackBlockEntity.getHeight(cards.size()));

        if (!context.getLevel().isClientSide()) {
            context.getLevel().setBlock(context.getClickedPos(), state, Block.UPDATE_ALL_IMMEDIATE);
            if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof CardStackBlockEntity blockEntity) {
                var angle = player.getYHeadRot();
                blockEntity.setCards(cards.stream().map(card -> new WorldCardInstance(card, angle)));
            }
        }

        context.getLevel().playSound(player, context.getClickedPos(), cards.size() < 8 || !stack.has(DealWithItComponents.DECK_CONTENTS) ? DealWithItSounds.CARD_STACK_PLACE : DealWithItSounds.CARD_STACK_SHUFFLE, SoundSource.BLOCKS);

        return true;
    }

    public static boolean addToStack(Player player, UseOnContext context, CardStackBlockEntity blockEntity) {
        var stack = context.getItemInHand();
        var cards = extractCards(stack, player, context.getHand(), player.isSecondaryUseActive(), player.getRandom());
        if (cards.isEmpty()) return false;

        if (!context.getLevel().isClientSide()) {
            var angle = player.getYHeadRot();
            blockEntity.pushCards(cards.stream().map(card -> new WorldCardInstance(card, angle)));
        }

        context.getLevel().playSound(player, context.getClickedPos(), DealWithItSounds.CARD_STACK_PLACE, SoundSource.BLOCKS);

        return true;
    }

    public static List<CardInstance> extractCards(ItemStack stack, Player player, InteractionHand hand, boolean secondaryActive, RandomSource random) {
        var single = stack.get(DealWithItComponents.CARD);
        if (single != null) {
            stack.consume(1, player);
            return List.of(secondaryActive ? single.flipped() : single);
        }

        var deck = stack.get(DealWithItComponents.DECK_CONTENTS);
        if (deck != null) {
            stack.set(DealWithItComponents.DECK_CONTENTS, deck.withCards(CardSet.EMPTY));
            return deck.cards().stream()
                    .map(card -> new CardInstance(deck.deck(), card, !secondaryActive))
                    .collect(toShuffledList(random));
        }

        var cardStack = stack.get(DealWithItComponents.CARD_STACK);
        if (cardStack != null) {
            var card = CardStack.pop(stack, player, hand);
            if (card == null) return List.of();
            return List.of(secondaryActive ? card.flipped() : card);
        }

        return List.of();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HEIGHT);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(HEIGHT) - 1];
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new CardStackBlockEntity(worldPosition, blockState);
    }
}
