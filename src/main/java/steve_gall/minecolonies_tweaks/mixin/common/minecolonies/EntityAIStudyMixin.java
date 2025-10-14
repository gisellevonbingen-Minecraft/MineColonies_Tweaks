package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingLibrary;
import com.minecolonies.core.colony.jobs.JobStudent;
import com.minecolonies.core.datalistener.StudyItemListener.StudyItem;
import com.minecolonies.core.entity.ai.workers.AbstractEntityAISkill;
import com.minecolonies.core.entity.ai.workers.education.EntityAIStudy;

import net.minecraft.resources.ResourceLocation;
import steve_gall.minecolonies_tweaks.core.common.init.MCTweaksBuildingModules;

@Mixin(value = EntityAIStudy.class, remap = false)
public abstract class EntityAIStudyMixin extends AbstractEntityAISkill<JobStudent, BuildingLibrary>
{
	protected EntityAIStudyMixin(@NotNull JobStudent job)
	{
		super(job);
	}

	@WrapOperation(method = "study", remap = false, at = @At(value = "INVOKE", target = "com/minecolonies/core/datalistener/StudyItemListener.getAllStudyItems", remap = false))
	private Map<ResourceLocation, StudyItem> study_getAllStudyItems(Operation<Map<ResourceLocation, StudyItem>> operation)
	{
		var module = this.building.getModule(MCTweaksBuildingModules.STUDY_ITEM_BLACKLIST);
		var items = operation.call();

		if (module == null)
		{
			return items;
		}

		items = new HashMap<>(items);

		for (var id : module.getIds())
		{
			items.remove(id);
		}

		return items;
	}

}
