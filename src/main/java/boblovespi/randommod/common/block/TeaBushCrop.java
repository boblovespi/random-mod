package boblovespi.randommod.common.block;

import boblovespi.randommod.RandomMod;
import net.minecraft.block.*;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class TeaBushCrop extends CropBlock
{
	public static final IntProperty AGE = Properties.AGE_1;
	private static final VoxelShape[] SHAPE = new VoxelShape[] {Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 6.0, 11.0), Block.createCuboidShape(5.0, 0.0, 5.0,
			11.0, 10.0, 11.0)};

	public TeaBushCrop(AbstractBlock.Settings settings)
	{
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return SHAPE[this.getAge(state)];
	}

	@Override
	protected IntProperty getAgeProperty()
	{
		return AGE;
	}

	@Override
	public int getMaxAge()
	{
		return 2;
	}

	@Override
	public BlockState withAge(int age)
	{
		return age == 2 ? RandomMod.TEA_BUSH.getDefaultState().with(TeaBush.AGE, 0) : super.withAge(age);
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random)
	{
		if (random.nextInt(3) != 0)
			super.randomTick(state, world, pos, random);
	}

	@Override
	protected int getGrowthAmount(World world)
	{
		return 1;
	}

	@Override
	protected ItemConvertible getSeedsItem()
	{
		return RandomMod.TEA_BUSH_CROP;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(AGE);
	}
}
