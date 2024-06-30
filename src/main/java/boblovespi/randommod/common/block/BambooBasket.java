package boblovespi.randommod.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class BambooBasket extends Block
{
	private static final VoxelShape OUTLINE_SHAPE = createCuboidShape(1, 0, 1, 15, 2, 15);

	public BambooBasket(Settings settings)
	{
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return OUTLINE_SHAPE;
	}
}
