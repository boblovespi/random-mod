package boblovespi.randommod.common.block;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import static net.minecraft.state.property.Properties.HORIZONTAL_FACING;
import static net.minecraft.state.property.Properties.WATERLOGGED;

public class CopperSink extends HorizontalFacingBlock
{
	public static final BooleanProperty FILLED = BooleanProperty.of("filled");
	protected static final VoxelShape[] OUTLINE_SHAPE = new VoxelShape[] {
			VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(),
			VoxelShapes.union(createCuboidShape(2, 10, 4, 14, 16, 14)), BooleanBiFunction.ONLY_FIRST), // south
			VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(),
					VoxelShapes.union(createCuboidShape(2, 10, 2, 12, 16, 14)), BooleanBiFunction.ONLY_FIRST), // west
			VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(),
					VoxelShapes.union(createCuboidShape(2, 10, 2, 14, 16, 12)), BooleanBiFunction.ONLY_FIRST), // north
			VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(),
					VoxelShapes.union(createCuboidShape(4, 10, 2, 14, 16, 14)), BooleanBiFunction.ONLY_FIRST) // esat
	};

	public CopperSink(Settings settings)
	{
		super(settings);
		setDefaultState(getDefaultState().with(FILLED, false));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		return this.getDefaultState().with(HORIZONTAL_FACING, ctx.getPlayerFacing().getOpposite());
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(FILLED, HORIZONTAL_FACING);
	}

	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		var stack = player.getStackInHand(hand);
		var filled = state.get(FILLED);
		if (!world.isClient)
		{
			if (!filled)
			{
				if (world.getFluidState(pos.down()).isEqualAndStill(Fluids.WATER) && world.getBlockState(pos.down()).getBlock() instanceof FluidDrainable drainable)
				{
					world.setBlockState(pos, state.with(FILLED, true));
					drainable.tryDrainFluid(world, pos.down(), world.getBlockState(pos.down()));
				}
				else if (stack.getItem() == Items.WATER_BUCKET)
					useItem(state, world, pos, player, hand, stack, new ItemStack(Items.BUCKET), true, SoundEvents.ITEM_BUCKET_EMPTY);
			}
			else
			{
				if (stack.getItem() == Items.BUCKET)
					useItem(state, world, pos, player, hand, stack, new ItemStack(Items.WATER_BUCKET), false, SoundEvents.ITEM_BUCKET_FILL);
			}
		}
		return ActionResult.success(world.isClient);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return OUTLINE_SHAPE[state.get(FACING).getHorizontal()];
	}

	private void useItem(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, ItemStack newStack, boolean fill,
						 SoundEvent sound)
	{
		Item item = stack.getItem();
		player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, newStack));
		player.incrementStat(Stats.USE_CAULDRON);
		player.incrementStat(Stats.USED.getOrCreateStat(item));
		world.setBlockState(pos, state.with(FILLED, fill));
		world.playSound(null, pos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F);
		world.emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);
	}
}
