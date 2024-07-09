package boblovespi.randommod.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
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

public class Teapot extends Block
{
	private static final VoxelShape OUTLINE_SHAPE_OPEN = VoxelShapes.union(createCuboidShape(3, 0, 3, 13, 1, 13),
			createCuboidShape(5, 1, 5, 11, 5, 11),
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
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if (world.isClient)
			return ActionResult.SUCCESS;
		world.setBlockState(pos, state.cycle(OPEN));
		return ActionResult.CONSUME;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(OPEN);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world,
												BlockPos pos, BlockPos neighborPos)
	{
		if (!state.canPlaceAt(world, pos))
			return Blocks.AIR.getDefaultState();
		return state;
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
}
