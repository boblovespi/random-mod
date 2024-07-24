package boblovespi.randommod.common.block;

import boblovespi.randommod.RandomMod;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Teapot extends BlockWithEntity
{
	private static final VoxelShape OUTLINE_SHAPE_OPEN = VoxelShapes.union(createCuboidShape(3, 0, 3, 13, 1, 13), createCuboidShape(5, 1, 5, 11, 5, 11),
			createCuboidShape(4, 5, 4, 12, 8, 12));
	private static final VoxelShape OUTLINE_SHAPE_CLOSED = VoxelShapes.union(OUTLINE_SHAPE_OPEN, createCuboidShape(5, 8, 5, 11, 9, 11),
			createCuboidShape(7, 9, 7, 9, 10, 9));
	public static BooleanProperty OPEN = Properties.OPEN;

	public Teapot(Settings settings)
	{
		super(settings);
		setDefaultState(getDefaultState().with(OPEN, false));
	}

	@Override
	public BlockRenderType getRenderType(BlockState state)
	{
		return BlockRenderType.MODEL;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos,
												BlockPos neighborPos)
	{
		if (!state.canPlaceAt(world, pos))
			return Blocks.AIR.getDefaultState();
		return state;
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if (world.isClient)
			return ActionResult.SUCCESS;
		if (state.get(OPEN) && !player.getStackInHand(hand).isEmpty())
			world.getBlockEntity(pos, RandomMod.TEAPOT_BE).ifPresent(t -> t.addItem(player, player.getStackInHand(hand)));
		else
			world.setBlockState(pos, state.cycle(OPEN));
		return ActionResult.CONSUME;
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
	{
		return Block.sideCoversSmallSquare(world, pos.down(), Direction.UP);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return state.get(OPEN) ? OUTLINE_SHAPE_OPEN : OUTLINE_SHAPE_CLOSED;
	}

	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state)
	{
		var teapotO = world.getBlockEntity(pos, RandomMod.TEAPOT_BE);
		if (teapotO.isPresent())
		{
			var teapot = teapotO.get();
			var nbt = new NbtCompound();
			teapot.writeNbt(nbt);
			var stack = super.getPickStack(world, pos, state);
			stack.setNbt(nbt);
			return stack;
		}
		return super.getPickStack(world, pos, state);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(OPEN);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options)
	{
		var nbt = stack.getSubNbt("teapot");
		var teaType = nbt == null ? "empty" : nbt.getString("teaType");
		var amount = nbt == null ? 0 : nbt.getInt("amount");
		tooltip.add(Text.literal(teaType + ": " + amount + " cups [TODO: REPLACE WITH LOCALIZATION]").formatted(Formatting.GRAY));
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new TeapotBE(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
	{
		if (world.isClient)
			return null;
		return checkType(type, RandomMod.TEAPOT_BE, TeapotBE::tick);
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
	{
		if (stack.getSubNbt("teapot") != null)
			world.getBlockEntity(pos, RandomMod.TEAPOT_BE).ifPresent(t -> t.readNbt(stack.getSubNbt("teapot")));
	}
}
