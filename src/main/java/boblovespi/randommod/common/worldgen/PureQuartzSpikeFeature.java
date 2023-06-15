package boblovespi.randommod.common.worldgen;

import boblovespi.randommod.RandomMod;
import com.mojang.serialization.Codec;
import net.minecraft.block.AmethystClusterBlock;
import net.minecraft.block.BlockState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class PureQuartzSpikeFeature extends Feature<DefaultFeatureConfig>
{
	public PureQuartzSpikeFeature(Codec<DefaultFeatureConfig> configCodec)
	{
		super(configCodec);
	}

	@Override
	public boolean place(FeatureContext<DefaultFeatureConfig> context)
	{
		var world = context.getWorld();
		var origin = context.getOrigin();
		var mut = origin.mutableCopy();
		var random = context.getRandom();

		int height = random.range(12, 30);
		int radius = random.range(3, 6);

		boolean shouldPlace = false;
		layerCheck:
		for (int negY = 0; negY < height / 2; negY++) // check we are not making it floating
		{
			for (int x = -2; x < 2; x++)
			{
				for (int z = -2; z < 2; z++)
				{
					mut.set(origin, x, -negY, z);
					if (world.getBlockState(mut).isIn(BlockTags.REPLACEABLE))
						continue layerCheck;
				}
			}
			shouldPlace = true;
			break;
		}
		if (!shouldPlace)
			return false;

		var stone = RandomMod.PEGMATITE.getDefaultState();
		var quartz = RandomMod.PURE_QUARTZ_BLOCK.getDefaultState();
		var bud = RandomMod.BUDDING_PURE_QUARTZ.getDefaultState();
		var crystal = RandomMod.PURE_QUARTZ_CLUSTER.getDefaultState().with(AmethystClusterBlock.FACING, Direction.UP);

		float currentRadius = radius;
		float radiusChange = (currentRadius + 1) / height;
		radius += 1;

		for (int y = 0; y < height; y++)
		{
			for (int x = -radius; x <= radius; x++)
			{
				for (int z = -radius; z <= radius; z++)
				{
					mut.set(origin, x, y, z);
					if (within(x, z, currentRadius - 2.2f)) // hollow
						//noinspection UnnecessaryContinue
						continue;
					else if (within(x, z, currentRadius - 1.1f)) // quartz, inner
						placeQuartz(world, mut, quartz, bud, random);
					else if (within(x, z, currentRadius)) // stone
						setBlockState(world, mut, stone);
					else if (within(x, z, currentRadius + 0.6f))  // quartz and stone
						if (random.nextFloat() <= 0.3f)
							placeQuartz(world, mut, quartz, bud, random);
						else
							setBlockState(world, mut, stone);
					else if (within(x, z, currentRadius + 1.2f)) // quartz, outer
						if (!world.getBlockState(mut.down()).isIn(BlockTags.REPLACEABLE) && random.nextFloat() <= 0.3f)
							setBlockState(world, mut, quartz);
						else if (world.getBlockState(mut.down()).isOf(RandomMod.PURE_QUARTZ_BLOCK) && random.nextFloat() <= 0.7f)
							setBlockState(world, mut, crystal);
				}
			}
			currentRadius -= radiusChange;
		}

		for (int negY = 0; negY < height / 2; negY++) // make not floating
		{
			shouldPlace = false;
			layerCheck:
			for (int x = -2; x < 2; x++)
			{
				for (int z = -2; z < 2; z++)
				{
					mut.set(origin, x, -negY, z);
					if (world.getBlockState(mut).isIn(BlockTags.REPLACEABLE))
						shouldPlace = true;
					if (shouldPlace)
						break layerCheck;
				}
			}
			if (!shouldPlace)
				break;
			for (int x = -radius; x <= radius; x++)
			{
				for (int z = -radius; z <= radius; z++)
				{
					mut.set(origin, x, -negY, z);
					if (world.getBlockState(mut).isIn(BlockTags.REPLACEABLE) && within(x, z, radius))
						setBlockState(world, mut, stone);
				}
			}
		}

		return true;
	}

	private void placeQuartz(StructureWorldAccess world, BlockPos pos, BlockState quartz, BlockState bud, RandomGenerator r)
	{
		if (r.nextFloat() <= 0.1f)
			setBlockState(world, pos, bud);
		else
			setBlockState(world, pos, quartz);
	}

	private boolean within(float x, float z, float r)
	{
		float norm = 1.3f;
		return Math.pow(Math.abs(x), norm) + Math.pow(Math.abs(z), norm) < Math.pow(MathHelper.clamp(r, 0, 100), norm);
	}
}
