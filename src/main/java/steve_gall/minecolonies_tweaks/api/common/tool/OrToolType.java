package steve_gall.minecolonies_tweaks.api.common.tool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.constant.IToolType;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class OrToolType extends CustomToolType
{
	private final List<Supplier<IToolType>> toolTypes;

	public OrToolType(ResourceLocation name, Collection<Supplier<IToolType>> toolTypes)
	{
		super(name);

		this.toolTypes = new ArrayList<>(toolTypes);
	}

	@Override
	protected int getToolLevel(@NotNull ItemStack stack)
	{
		for (var supplier : this.toolTypes)
		{
			var toolType = supplier.get();
			var level = ItemStackUtils.getMiningLevel(stack, toolType);

			if (level > -1)
			{
				return level;
			}

		}

		return super.getToolLevel(stack);
	}

	@Override
	protected boolean isTool(@NotNull ItemStack stack)
	{
		for (var supplier : this.toolTypes)
		{
			var toolType = supplier.get();

			if (ItemStackUtils.isTool(stack, toolType))
			{
				return true;
			}

		}

		return super.isTool(stack);
	}

}
