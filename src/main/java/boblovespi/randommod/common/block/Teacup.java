package boblovespi.randommod.common.block;

import boblovespi.randommod.RandomMod;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Teacup extends BlockWithEntity
{
	private static final VoxelShape OUTLINE_SHAPE = createCuboidShape(5.5f, 0, 5.5f, 10.5f, 5, 10.5f);

	public Teacup(AbstractBlock.Settings settings)
	{
		super(settings);
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
		if (player.getStackInHand(hand).isEmpty())
		{
			world.getBlockEntity(pos, RandomMod.TEACUP_BE).ifPresent(t -> t.drink(player));
		}
		else
			world.getBlockEntity(pos, RandomMod.TEACUP_BE).ifPresent(t -> t.pour(player, player.getStackInHand(hand)));
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
		return OUTLINE_SHAPE;
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options)
	{
		var nbt = stack.getSubNbt("teacup");
		var contents = nbt == null ? "empty" : nbt.getString("teaType");
		var time = nbt == null ? 0 : nbt.getInt("time");
		tooltip.add(
				Text.translatable("boblovespirandommod.teas." + contents)/*.append(Text.translatable("boblovespirandommod.teas.duration", time))*/.formatted(
						Formatting.GRAY));
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new TeacupBE(pos, state);
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
	{
		if (stack.getSubNbt("teacup") != null)
			world.getBlockEntity(pos, RandomMod.TEACUP_BE).ifPresent(t -> t.readNbt(stack.getSubNbt("teacup")));
	}
}
