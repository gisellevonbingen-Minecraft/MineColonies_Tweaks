package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.minecolonies.api.entity.ai.util.StudyItem;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingLibrary;
import com.minecolonies.core.colony.jobs.JobStudent;
import com.minecolonies.core.entity.ai.basic.AbstractEntityAISkill;
import com.minecolonies.core.entity.ai.citizen.student.EntityAIStudy;

import net.minecraftforge.registries.ForgeRegistries;
import steve_gall.minecolonies_tweaks.core.common.init.MCTweaksBuildingModules;

@Mixin(value = EntityAIStudy.class, remap = false)
public abstract class EntityAIStudyMixin extends AbstractEntityAISkill<JobStudent, BuildingLibrary>
{
	protected EntityAIStudyMixin(@NotNull JobStudent job)
	{
		super(job);
	}

	@WrapOperation(method = "study", remap = false, at = @At(value = "INVOKE", target = "com/minecolonies/core/colony/buildings/workerbuildings/BuildingLibrary.getStudyItems", remap = false))
	private List<StudyItem> study_getAllStudyItems(BuildingLibrary building, Operation<List<StudyItem>> operation)
	{
		var module = this.building.getModule(MCTweaksBuildingModules.STUDY_ITEM_BLACKLIST);
		var items = operation.call(building);

		if (module == null)
		{
			return items;
		}

		var map = items.stream().collect(Collectors.toMap(i -> ForgeRegistries.ITEMS.getKey(i.getItem()), i -> i));

		for (var id : module.getIds())
		{
			map.remove(id);
		}

		return new ArrayList<>(map.values());
	}

}
