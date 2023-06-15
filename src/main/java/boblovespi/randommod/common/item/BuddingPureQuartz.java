package boblovespi.randommod.common.item;

import boblovespi.randommod.RandomMod;
import net.minecraft.block.*;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomGenerator;

public class BuddingPureQuartz extends AmethystBlock
{
	public BuddingPureQuartz(Settings settings)
	{
		super(settings);
	}

	public static boolean canGrowIn(BlockState state)
	{
		return state.isAir() || state.isOf(Blocks.WATER) && state.getFluidState().getLevel() == 8;
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random)
	{
		if (random.nextInt(7) == 0)
		{
			var direction = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
			var blockPos = pos.offset(direction);
			var blockState = world.getBlockState(blockPos);
			Block block = null;

			if (canGrowIn(blockState))
				block = RandomMod.SMALL_PURE_QUARTZ_BUD;
			else if (blockState.isOf(RandomMod.SMALL_PURE_QUARTZ_BUD) && blockState.get(AmethystClusterBlock.FACING) == direction)
				block = RandomMod.MEDIUM_PURE_QUARTZ_BUD;
			else if (blockState.isOf(RandomMod.MEDIUM_PURE_QUARTZ_BUD) && blockState.get(AmethystClusterBlock.FACING) == direction)
				block = RandomMod.LARGE_PURE_QUARTZ_BUD;
			else if (blockState.isOf(RandomMod.LARGE_PURE_QUARTZ_BUD) && blockState.get(AmethystClusterBlock.FACING) == direction)
				block = RandomMod.PURE_QUARTZ_CLUSTER;

			if (block != null)
			{
				var newState = block.getDefaultState().with(AmethystClusterBlock.FACING, direction)
									   .with(AmethystClusterBlock.WATERLOGGED, blockState.getFluidState().getFluid() == Fluids.WATER);
				world.setBlockState(blockPos, newState);
			}
		}
	}
}
