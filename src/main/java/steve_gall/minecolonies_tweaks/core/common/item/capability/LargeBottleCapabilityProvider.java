package steve_gall.minecolonies_tweaks.core.common.item.capability;

import com.minecolonies.api.items.ModItems;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public class LargeBottleCapabilityProvider implements IFluidHandlerItem
{
	private ItemStack container;

	public LargeBottleCapabilityProvider(ItemStack container)
	{
		this.container = container;
	}

	@Override
	public ItemStack getContainer()
	{
		return this.container;
	}

	public FluidStack getFluid()
	{
		if (this.container.is(ModItems.large_empty_bottle))
		{
			return FluidStack.EMPTY;
		}
		else if (this.container.is(ModItems.large_water_bottle))
		{
			return new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME);
		}
		else if (this.container.is(ModItems.large_milk_bottle) && NeoForgeMod.MILK.isBound())
		{
			return new FluidStack(NeoForgeMod.MILK.get(), FluidType.BUCKET_VOLUME);
		}
		else
		{
			return FluidStack.EMPTY;
		}
	}

	public boolean canFillFluidType(FluidStack resource)
	{
		return resource.is(Fluids.WATER) || (NeoForgeMod.MILK.isBound() && resource.is(NeoForgeMod.MILK.get()));
	}

	public void setFluid(FluidStack resource)
	{
		if (resource.isEmpty())
		{
			this.container = ModItems.large_empty_bottle.getDefaultInstance();
		}
		else if (resource.getFluid().isSame(Fluids.WATER))
		{
			this.container = ModItems.large_water_bottle.getDefaultInstance();
		}
		else if (NeoForgeMod.MILK.isBound() && resource.getFluid().isSame(NeoForgeMod.MILK.get()))
		{
			this.container = ModItems.large_milk_bottle.getDefaultInstance();
		}

	}

	@Override
	public int getTanks()
	{
		return 1;
	}

	@Override
	public int getTankCapacity(int tank)
	{
		return FluidType.BUCKET_VOLUME;
	}

	@Override
	public FluidStack getFluidInTank(int tank)
	{
		return this.getFluid();
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack resource)
	{
		return this.canFillFluidType(resource);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action)
	{
		if (this.container.getCount() != 1 || resource.getAmount() < FluidType.BUCKET_VOLUME || !this.container.is(ModItems.large_empty_bottle) || !this.canFillFluidType(resource))
		{
			return 0;
		}

		if (action.execute())
		{
			this.setFluid(resource);
		}

		return FluidType.BUCKET_VOLUME;
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action)
	{
		if (this.container.getCount() != 1 || resource.getAmount() < FluidType.BUCKET_VOLUME)
		{
			return FluidStack.EMPTY;
		}

		var fluidStack = this.getFluid();

		if (!fluidStack.isEmpty() && FluidStack.isSameFluidSameComponents(fluidStack, resource))
		{
			if (action.execute())
			{
				this.setFluid(FluidStack.EMPTY);
			}

			return fluidStack;
		}

		return FluidStack.EMPTY;
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action)
	{
		if (this.container.getCount() != 1 || maxDrain < FluidType.BUCKET_VOLUME)
		{
			return FluidStack.EMPTY;
		}

		var fluidStack = this.getFluid();

		if (!fluidStack.isEmpty())
		{
			if (action.execute())
			{
				this.setFluid(FluidStack.EMPTY);
			}

			return fluidStack;
		}

		return FluidStack.EMPTY;
	}

}
