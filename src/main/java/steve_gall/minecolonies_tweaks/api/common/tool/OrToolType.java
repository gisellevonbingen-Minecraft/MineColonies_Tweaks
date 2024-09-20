package steve_gall.minecolonies_tweaks.api.common.tool;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import com.minecolonies.api.equipment.registry.EquipmentTypeEntry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class OrToolType extends CustomToolType
{
	private final List<Supplier<EquipmentTypeEntry>> toolTypes;

	public OrToolType(ResourceLocation name, Collection<Supplier<EquipmentTypeEntry>> toolTypes)
	{
		super(name);

		this.toolTypes = toolTypes.stream().toList();
	}

	public List<Supplier<EquipmentTypeEntry>> getToolTypes()
	{
		return this.toolTypes;
	}

	@Override
	public int getToolLevel(@NotNull ItemStack stack)
	{
		for (var supplier : this.getToolTypes())
		{
			var toolType = supplier.get();
			var level = toolType.getMiningLevel(stack);

			if (level > -1)
			{
				return level;
			}

		}

		return super.getToolLevel(stack);
	}

	@Override
	public boolean isTool(@NotNull ItemStack stack)
	{
		for (var supplier : this.getToolTypes())
		{
			var toolType = supplier.get();

			if (toolType.checkIsEquipment(stack))
			{
				return true;
			}

		}

		return super.isTool(stack);
	}

}
