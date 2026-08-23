package archives.tater.dealwithit.block;

import archives.tater.dealwithit.block.entity.CardStackBlockEntity;
import archives.tater.dealwithit.component.DeckContents;
import archives.tater.dealwithit.registry.DealWithItBlocks;
import archives.tater.dealwithit.registry.DealWithItComponents;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.jspecify.annotations.Nullable;

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

            return InteractionResult.SUCCESS;
        }

        var contents = itemStack.get(DealWithItComponents.DECK_CONTENTS);
        if (contents != null) {
            var cards = new Object2IntOpenHashMap<>(contents.cards());
            var anyRemoved = blockEntity.removeIf(instance -> DeckContents.tryInsert(instance.card(), contents.deck(), cards) );
            if (!anyRemoved) return InteractionResult.FAIL;

            itemStack.set(DealWithItComponents.DECK_CONTENTS, contents.withCards(cards));
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return Block.isFaceFull(level.getBlockState(pos.below()).getCollisionShape(level, pos.below()), Direction.UP);
    }

    public static boolean place(Player player, InteractionHand hand, ItemStack stack, BlockHitResult hitResult) {
        var cards = CardStackBlockEntity.getCards(stack, player.getYHeadRot(), player.isSecondaryUseActive(), player.getRandom());
        if (cards.isEmpty()) return false;

        var context = new BlockPlaceContext(player, hand, stack, hitResult);
        if (!context.canPlace()) return false;

        var state = DealWithItBlocks.CARD_STACK.defaultBlockState().setValue(HEIGHT, CardStackBlockEntity.getHeight(cards.size()));
        if (!context.getLevel().setBlock(context.getClickedPos(), state, Block.UPDATE_ALL_IMMEDIATE)) return false;

        if (!(player.level().getBlockEntity(context.getClickedPos()) instanceof CardStackBlockEntity blockEntity)) return true;

        blockEntity.setCards(cards);

        var deck = stack.get(DealWithItComponents.DECK_CONTENTS);
        if (deck != null)
            stack.set(DealWithItComponents.DECK_CONTENTS, deck.withCards(Object2IntMaps.emptyMap()));
        else
            stack.consume(1, player);

        return true;
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
