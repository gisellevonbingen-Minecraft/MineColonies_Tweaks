package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.research.ILocalResearch;
import com.minecolonies.core.research.LocalResearchTree;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import steve_gall.minecolonies_tweaks.core.common.inventory.InventoryUtils2;
import steve_gall.minecolonies_tweaks.core.common.research.LocalResearchTreeExtension;

@Mixin(value = LocalResearchTree.class, remap = false)
public abstract class LocalResearchTreeMixin implements LocalResearchTreeExtension
{
	@Shadow(remap = false)
	private Map<ResourceLocation, Map<ResourceLocation, ILocalResearch>> researchTree;
	@Shadow(remap = false)
	private Map<ResourceLocation, ILocalResearch> inProgress;
	@Shadow(remap = false)
	private Set<ResourceLocation> isComplete;
	@Shadow(remap = false)
	private Set<ResourceLocation> maxLevelResearchCompleted;

	@Shadow(remap = false)
	abstract void resetEffects(IColony colony);

	@Unique
	private IBuilding minecolonies_tweaks$building = null;

	@WrapOperation(method = "attemptBeginResearch", remap = false, at = @At(value = "NEW", target = "net/neoforged/neoforge/items/wrapper/InvWrapper", remap = false))
	private InvWrapper newInvWrapper(Container inv, Operation<InvWrapper> operation)
	{
		var original = operation.call(inv);

		if (this.minecolonies_tweaks$building != null)
		{
			var level = this.minecolonies_tweaks$building.getColony().getWorld();

			if (level != null)
			{
				return InventoryUtils2.wrapWithBuilding(original, level, this.minecolonies_tweaks$building.getPosition());
			}

		}

		return original;
	}

	@Override
	public void minecolonies_tweaks$setBuilding(IBuilding building)
	{
		this.minecolonies_tweaks$building = building;
	}

	@Override
	public IBuilding minecolonies_tweaks$getBuilding()
	{
		return this.minecolonies_tweaks$building;
	}

	@Override
	public void minecolonies_tweaks$reset(IColony colony, ResourceLocation branchId)
	{
		var values = this.researchTree.get(branchId);

		if (values == null)
		{
			return;
		}

		for (var research : new ArrayList<>(values.keySet()))
		{
			values.remove(research);
			this.inProgress.remove(research);
			this.isComplete.remove(research);
		}

		this.maxLevelResearchCompleted.remove(branchId);

		this.minecolonies_tweaks$onResearchChanged(colony);
	}

	@Override
	public void minecolonies_tweaks$resetAll(IColony colony)
	{
		this.researchTree.values().forEach(Map::clear);
		this.inProgress.clear();
		this.isComplete.clear();
		this.maxLevelResearchCompleted.clear();

		this.minecolonies_tweaks$onResearchChanged(colony);
	}

	private void minecolonies_tweaks$onResearchChanged(IColony colony)
	{
		this.resetEffects(colony);
		colony.getResearchManager().markDirty();
		colony.getBuildingManager().markBuildingsDirty();

		for (var citizen : colony.getCitizenManager().getCitizens())
		{
			citizen.applyResearchEffects();
		}

	}

}
