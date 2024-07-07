package boblovespi.randommod.common.block;

import boblovespi.randommod.RandomMod;
import boblovespi.randommod.common.recipe.BasketDryingRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class BambooBasketBE extends BlockEntity
{
	private static final int NUM_STACKS = 5;
	private final DefaultedList<ItemStack> stacks = DefaultedList.ofSize(NUM_STACKS, ItemStack.EMPTY);
	private final int[] dryingTimes = new int[NUM_STACKS];

	private final RecipeManager.CachedCheck<Inventory, BasketDryingRecipe> recipeCache = RecipeManager.createCheck(RandomMod.BASKET_DRYING_RECIPE);

	public BambooBasketBE(BlockPos pos, BlockState state)
	{
		super(RandomMod.BAMBOO_BASKET_BE, pos, state);
	}

	public static void tick(World world, BlockPos pos, BlockState state, BambooBasketBE be)
	{
		for (int i = 0; i < NUM_STACKS; i++)
		{
			if (be.dryingTimes[i] > 0)
			{
				be.dryingTimes[i]--;
				if (be.dryingTimes[i] == 0)
				{
					// dried
					var tempInv = new SimpleInventory(be.stacks.get(i));
					var result = be.recipeCache.getRecipeFor(tempInv, world).map(r -> r.craft(tempInv, world.getRegistryManager())).orElse(be.stacks.get(i));
					be.stacks.set(i, result);
					world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
				}
				be.markDirty();
			}
		}
	}

	public DefaultedList<ItemStack> getStacks()
	{
		return stacks;
	}

	public boolean interact(PlayerEntity user, ItemStack item, int index)
	{
		if (removeItem(user, index))
			return true;
		else if (!item.isEmpty())
			return addItem(user, user.isCreative() ? item.copy() : item, index);
		else
			return false;
	}

	public boolean addItem(@Nullable Entity user, ItemStack item, int index)
	{
		if (stacks.get(index).isEmpty())
		{
			var recipe = recipeCache.getRecipeFor(new SimpleInventory(item), world);
			stacks.set(index, item.split(1));
			dryingTimes[index] = recipe.map(BasketDryingRecipe::getDryingTime).orElse(0);
			world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.getPos(), GameEvent.Context.create(user, this.getCachedState()));
			markDirty();
			world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
			return true;
		}
		return false;
	}

	public boolean removeItem(PlayerEntity user, int index)
	{
		if (stacks.get(index).isEmpty())
			return false;
		var success = user.getInventory().insertStack(stacks.get(index));
		if (!success)
			ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stacks.get(index));
		dryingTimes[index] = 0;
		world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.getPos(), GameEvent.Context.create(user, this.getCachedState()));
		markDirty();
		world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
		return true;
	}

	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		stacks.clear();
		Inventories.readNbt(nbt, stacks);
		if (nbt.contains("dryingTimes", NbtCompound.INT_ARRAY_TYPE))
		{
			var times = nbt.getIntArray("dryingTimes");
			System.arraycopy(times, 0, dryingTimes, 0, Math.min(times.length, NUM_STACKS));
		}
	}

	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		Inventories.writeNbt(nbt, stacks);
		nbt.putIntArray("dryingTimes", dryingTimes);
	}

	@Nullable
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket()
	{
		return BlockEntityUpdateS2CPacket.of(this);
	}

	@Override
	public NbtCompound toSyncedNbt()
	{
		var nbt = new NbtCompound();
		Inventories.writeNbt(nbt, stacks);
		return nbt;
	}
}
