package boblovespi.randommod.common.block;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;

import static net.minecraft.state.property.Properties.HORIZONTAL_FACING;

public class CopperKettle extends HorizontalFacingBlock
{
	private static final VoxelShape OUTLINE_SHAPE = VoxelShapes.union(createCuboidShape(4, 0, 4, 12, 6, 12), createCuboidShape(5, 6, 5, 11, 7, 11));
	public static EnumProperty<Contents> CONTENTS = EnumProperty.of("contents", Contents.class);
	public static BooleanProperty LEGS = BooleanProperty.of("legs");
	public static IntProperty BOIL_LEVEL = IntProperty.of("boil_level", 0, 4);

	public CopperKettle(Settings settings)
	{
		super(settings);
		setDefaultState(getDefaultState().with(CONTENTS, Contents.EMPTY).with(LEGS, false));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world,
												BlockPos pos, BlockPos neighborPos)
	{
		if (!canPlaceAt(state, world, pos))
			return Blocks.AIR.getDefaultState();

		var legs = world.getBlockState(pos.down()).isIn(BlockTags.CAMPFIRES);
		return state.with(LEGS, legs);
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify)
	{
		if (!state.isOf(oldState.getBlock()))
			updateBoilingState(state, world, pos);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		var stack = player.getStackInHand(hand);
		var filled = state.get(CONTENTS) != Contents.EMPTY;
		if (!world.isClient)
		{
			if (!filled)
			{
				if (stack.getItem() == Items.WATER_BUCKET)
					useItem(state, world, pos, player, hand, stack, new ItemStack(Items.BUCKET), true, SoundEvents.ITEM_BUCKET_EMPTY);
			}
			else if (stack.getItem() == Items.BUCKET)
				useItem(state, world, pos, player, hand, stack, new ItemStack(Items.WATER_BUCKET), false, SoundEvents.ITEM_BUCKET_FILL);
		}
		return ActionResult.success(world.isClient);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
	{
		return Block.sideCoversSmallSquare(world, pos.down(), Direction.UP) || world.getBlockState(pos.down()).isIn(BlockTags.CAMPFIRES);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return OUTLINE_SHAPE;
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random)
	{
		updateBoilingState(state, world, pos);
	}

	private void updateBoilingState(BlockState state, World world, BlockPos pos)
	{
		// Logic:
		// empty -> nothing
		// filled + fire -> boiling
		// boiling + fire -> empty
		// boiling -> filled (if it doesn't empty first)
		var contents = state.get(CONTENTS);
		if (contents == Contents.EMPTY)
			return;
		var fire = world.getBlockState(pos.down());
		var boilLevel = state.get(BOIL_LEVEL);
		if (contents == Contents.FILLED)
		{
			// TODO: replace with custom tag for things that cause boiling (campfire, fire, copper brazer?)
			if (fire.isIn(BlockTags.CAMPFIRES) && fire.get(Properties.LIT))
			{
				if (boilLevel == 4)
					world.setBlockState(pos, state.with(CONTENTS, Contents.BOILING).with(BOIL_LEVEL, 2));
				else
					world.setBlockState(pos, state.with(BOIL_LEVEL, boilLevel + 1), NOTIFY_LISTENERS);
				world.scheduleBlockTick(pos, this, 60);
			}
			else
				world.setBlockState(pos, state.with(BOIL_LEVEL, 0), NOTIFY_LISTENERS);
		}
		if (contents == Contents.BOILING)
		{
			// TODO: replace with custom tag for things that cause boiling (campfire, fire, copper brazer?)
			if (fire.isIn(BlockTags.CAMPFIRES) && fire.get(Properties.LIT))
			{
				if (boilLevel == 4)
					world.setBlockState(pos, state.with(CONTENTS, Contents.EMPTY).with(BOIL_LEVEL, 0));
				else
					world.setBlockState(pos, state.with(BOIL_LEVEL, boilLevel + 1), NOTIFY_LISTENERS);
				world.scheduleBlockTick(pos, this, 200);
			}
			else
			{
				if (boilLevel > 0)
				{
					world.setBlockState(pos, state.with(BOIL_LEVEL, boilLevel - 1), NOTIFY_LISTENERS);
					world.scheduleBlockTick(pos, this, 200);
				}
				else
				{
					world.setBlockState(pos, state.with(CONTENTS, Contents.FILLED).with(BOIL_LEVEL, 0));
					world.scheduleBlockTick(pos, this, 60);
				}
			}
		}
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, RandomGenerator random)
	{
		if (state.get(CONTENTS) == Contents.BOILING)
		{
			float f = random.nextFloat();
			// TODO: custom particles
			world.addParticle(ParticleTypes.CLOUD, pos.getX() + 0.5, pos.getY() + 0.4, pos.getZ() + 0.5, 0.0, 0.1, 0.0);
			if (f < 0.17F)
			{
				// TODO: custom sounds
				world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_SMOKER_SMOKE, SoundCategory.BLOCKS,
						1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
			}

		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		var nbt = ctx.getStack().getNbt();
		var contents = Contents.fromString(nbt == null ? "empty" : nbt.getString("contents"));
		var dir = ctx.getPlayerFacing().getOpposite();
		var legs = ctx.getWorld().getBlockState(ctx.getBlockPos().down()).isIn(BlockTags.CAMPFIRES);
		var boilLevel = contents == Contents.BOILING ? 2 : 0;
		return getDefaultState().with(CONTENTS, contents).with(HORIZONTAL_FACING, dir).with(LEGS, legs).with(BOIL_LEVEL, boilLevel);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(CONTENTS, HORIZONTAL_FACING, LEGS, BOIL_LEVEL);
	}

	private void useItem(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, ItemStack newStack, boolean fill,
						 SoundEvent sound)
	{
		var item = stack.getItem();
		var newState = state.with(CONTENTS, fill ? Contents.FILLED : Contents.EMPTY);
		player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, newStack));
		player.incrementStat(Stats.USE_CAULDRON);
		player.incrementStat(Stats.USED.getOrCreateStat(item));
		world.setBlockState(pos, newState);
		if (fill)
			updateBoilingState(newState, world, pos);
		world.playSound(null, pos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F);
		world.emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);
	}

	public enum Contents implements StringIdentifiable
	{
		EMPTY, FILLED, BOILING;

		public static Contents fromString(String contents)
		{
			// @formatter:off
			return switch (contents)
			{
				case "empty" -> EMPTY;
				case "filled" -> FILLED;
				case "boiling" -> BOILING;
				default -> EMPTY;
			};
			// @formatter:on
		}

		@Override
		public String asString()
		{
			// @formatter:off
			return switch (this)
			{
				case EMPTY -> "empty";
				case FILLED -> "filled";
				case BOILING -> "boiling";
			};
			// @formatter:on
		}
	}
}
