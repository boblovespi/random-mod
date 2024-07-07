package boblovespi.randommod.common.block;

import boblovespi.randommod.RandomMod;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BambooBasket extends BlockWithEntity
{
	private static final VoxelShape OUTLINE_SHAPE = createCuboidShape(1, 0, 1, 15, 2, 15);

	public BambooBasket(Settings settings)
	{
		super(settings);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state)
	{
		return BlockRenderType.MODEL;
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
	{
		if (!state.isOf(newState.getBlock()))
		{
			var be = world.getBlockEntity(pos);
			if (be instanceof BambooBasketBE basket)
				ItemScatterer.spawn(world, pos, basket.getStacks());

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		var be = world.getBlockEntity(pos);
		if (be instanceof BambooBasketBE basket)
		{
			var stack = player.getStackInHand(hand);
			if (!world.isClient)
			{
				var hitPos = hit.getPos().subtract(pos.ofCenter());
				var dist = Math.abs(hitPos.x) + Math.abs(hitPos.z);
				var index = 0;
				if (hitPos.x < 0)
					index |= 0b10;
				if (hitPos.z < 0)
					index |= 0b01;
				if (dist < 3 / 16f)
					index = 4;
				var result = basket.interact(player, stack, index);
				//				RandomMod.LOGGER.info("hit ({}, {}), result index: {}", hitPos.x, hitPos.z, index);
				if (result)
					return ActionResult.SUCCESS;
			}
			return ActionResult.CONSUME;
		}
		return ActionResult.PASS;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return OUTLINE_SHAPE;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new BambooBasketBE(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
	{
		if (world.isClient)
			return null;
		return checkType(type, RandomMod.BAMBOO_BASKET_BE, BambooBasketBE::tick);
	}
}
