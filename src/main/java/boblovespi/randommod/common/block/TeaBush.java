package boblovespi.randommod.common.block;

import boblovespi.randommod.RandomMod;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;

public class TeaBush extends PlantBlock implements Fertilizable
{
	public static final IntProperty AGE = Properties.AGE_3;
	private static final VoxelShape SMALL_SHAPE = Block.createCuboidShape(2, 0, 2, 14, 12, 14);
	private static final VoxelShape LARGE_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 16, 16);

	public TeaBush(AbstractBlock.Settings settings)
	{
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(AGE, 1));
	}

	@Override
	public boolean hasRandomTicks(BlockState state)
	{
		return state.get(AGE) < 3;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(AGE);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		var age = state.get(AGE);
		var maxAge = age == 3;
		if (!maxAge && player.getStackInHand(hand).isOf(Items.BONE_MEAL))
			return ActionResult.PASS;
		else if (maxAge)
		{
			int dropCount = 1 + world.random.nextInt(2);
			dropStack(world, pos, new ItemStack(RandomMod.TEA_LEAF, dropCount));
			world.playSound(null, pos, SoundEvents.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES, SoundCategory.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
			BlockState blockState = state.with(AGE, 2);
			world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
			world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.create(player, blockState));
			return ActionResult.success(world.isClient);
		}
		else
			return ActionResult.PASS;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return state.get(AGE) < 2 ? SMALL_SHAPE : LARGE_SHAPE;
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random)
	{
		int i = state.get(AGE);
		if (i > 0 && i < 3 && random.nextInt(5) == 0 && world.getBaseLightLevel(pos.up(), 0) >= 9 && hasSpace(world, pos))
		{
			BlockState blockState = state.with(AGE, i + 1);
			world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
			world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.create(blockState));
		}
	}

	@Override
	public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state, boolean isClient)
	{
		return state.get(AGE) < 3 && state.get(AGE) > 0 /*&& hasSpace(world, pos)*/;
	}

	@Override
	public boolean canGrow(World world, RandomGenerator random, BlockPos pos, BlockState state)
	{
		return state.get(AGE) > 0 /*&& hasSpace(world, pos)*/;
	}

	@Override
	public void grow(ServerWorld world, RandomGenerator random, BlockPos pos, BlockState state)
	{
		if (!hasSpace(world, pos))
			return;
		int i = Math.min(3, state.get(AGE) + 1);
		world.setBlockState(pos, state.with(AGE, i), Block.NOTIFY_LISTENERS);
	}

	private boolean hasSpace(WorldView world, BlockPos pos)
	{
		for (var dir : Direction.Type.HORIZONTAL)
		{
			if (!world.isAir(pos.offset(dir)))
				return false;
		}
		return true;
	}
}
