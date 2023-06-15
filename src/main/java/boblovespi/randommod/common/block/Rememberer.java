package boblovespi.randommod.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneDiodeBlock;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.BlockView;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import static net.minecraft.state.property.Properties.POWER;

public class Rememberer extends RedstoneDiodeBlock
{
	// Properties:
	// POWER is the input/output (data) level
	// POWERED is whether the write signal is high or not
	public Rememberer(Settings settings)
	{
		super(settings);
		setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(POWER, 0).with(POWERED, Boolean.FALSE));
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random)
	{
		if (writeIsHigh(world, pos, state))
		{
			world.setBlockState(pos, state.with(POWER, getInputLevel(world, pos, state)), Block.NOTIFY_LISTENERS);
		}
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction)
	{
		return state.get(FACING) == direction ? state.get(POWER) : 0;
	}

	@Override
	protected void checkOutputLevel(World world, BlockPos pos, BlockState state)
	{
		var writeHigh = writeIsHigh(world, pos, state);
		if (writeHigh)
		{
			int currentPower = state.get(POWER);
			int newPower = getInputLevel(world, pos, state);
			if (currentPower != newPower && !world.getBlockTickScheduler().willTick(pos, this))
			{
				TickPriority tickPriority = TickPriority.HIGH;
				if (isTargetNotAligned(world, pos, state))
				{
					tickPriority = TickPriority.EXTREMELY_HIGH;
				}
				else if (currentPower > 0)
				{
					tickPriority = TickPriority.VERY_HIGH;
				}
				world.scheduleBlockTick(pos, this, getUpdateDelayInternal(state), tickPriority);
			}
		}
		world.setBlockState(pos, state.with(POWERED, writeHigh), Block.NOTIFY_LISTENERS);
	}

	@Override
	protected int getUpdateDelayInternal(BlockState state)
	{
		return 2;
	}

	private boolean writeIsHigh(WorldView world, BlockPos pos, BlockState state)
	{
		return getMaxInputLevelSides(world, pos, state) != 0;
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, RandomGenerator random)
	{
		if (state.get(POWER) > 0)
		{
			Direction direction = state.get(FACING);
			double d = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
			double e = pos.getY() + 0.4 + (random.nextDouble() - 0.5) * 0.2;
			double f = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
			float g = -5.0F;
			if (random.nextBoolean())
				g = 1;

			g /= 16.0F;
			double h = g * direction.getOffsetX();
			double i = g * direction.getOffsetZ();
			world.addParticle(DustParticleEffect.DEFAULT, d + h, e, f + i, 0.0, 0.0, 0.0);
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(FACING, POWERED, POWER);
	}
}
