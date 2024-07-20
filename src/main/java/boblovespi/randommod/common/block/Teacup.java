package boblovespi.randommod.common.block;

import net.minecraft.block.*;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Teacup extends Block
{
	private static final VoxelShape OUTLINE_SHAPE = createCuboidShape(5.5f, 0, 5.5f, 10.5f, 5, 10.5f);

	public Teacup(AbstractBlock.Settings settings)
	{
		super(settings);
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
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
	{
		return Block.sideCoversSmallSquare(world, pos.down(), Direction.UP);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return OUTLINE_SHAPE;
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options)
	{
		var nbt = stack.getSubNbt("tea");
		var contents = nbt == null ? "empty" : nbt.getString("contents");
		var time = nbt == null ? 0 : nbt.getInt("time");
		contents = CopperKettle.Contents.fromString(contents).asString();
		tooltip.add(Text.translatable("boblovespirandommod.teas." + contents).append(Text.translatable("boblovespirandommod.teas.duration", time)).formatted(Formatting.GRAY));
	}
}
