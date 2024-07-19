package steve_gall.minecolonies_tweaks.core.common.building.module;

import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import com.minecolonies.api.colony.jobs.registry.JobEntry;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.colony.buildings.modules.AbstractCraftingBuildingModule;
import com.minecolonies.core.colony.buildings.moduleviews.CraftingModuleView;

import net.minecraft.util.GsonHelper;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.core.common.config.MineColoniesTweaksConfigCommon;
import steve_gall.minecolonies_tweaks.core.common.util.GsonHelper2;

public class CustomCraftingModule extends AbstractCraftingBuildingModule.Custom
{
	public static void loadCustomCraftingModules()
	{
		try
		{
			var gson = new Gson();

			for (var raw : MineColoniesTweaksConfigCommon.INSTANCE.buildings.customCraftingModules.get())
			{
				var json = gson.fromJson(raw, JsonObject.class);
				var data = new CustomCraftingModule.Builder(json);
				data.buildingEntry().getModuleProducers().add(new BuildingEntry.ModuleProducer<>(data.name(), //
						() -> new CustomCraftingModule(data), //
						() -> CraftingModuleView::new));

				MineColoniesTweaks.LOGGER.info("CustomCraftingModule Added: " + data.name());
			}

		}
		catch (Exception e)
		{
			throw new RuntimeException("Exception during load CustomCraftingModule", e);
		}

	}

	private final String name;
	private final boolean forceVisible;

	public CustomCraftingModule(Builder builder)
	{
		super(builder.jobEntry);

		this.name = builder.name;
		this.forceVisible = builder.forceVisible;
	}

	@Override
	public @NotNull String getId()
	{
		return this.name;
	}

	@Override
	public boolean isVisible()
	{
		return this.forceVisible || super.isVisible();
	}

	public static class Builder
	{
		private final String name;
		private BuildingEntry buildingEntry;
		private JobEntry jobEntry;
		private boolean forceVisible;

		public Builder(JsonObject json)
		{
			this.name = GsonHelper.getAsString(json, "name");
			var buildinId = GsonHelper2.getAsResourceLocation(json, "buildingId", Constants.MOD_ID);
			var jobId = GsonHelper2.getAsResourceLocation(json, "jobId", Constants.MOD_ID);
			this.forceVisible = GsonHelper.getAsBoolean(json, "forceVisible", false);

			this.buildingEntry = IMinecoloniesAPI.getInstance().getBuildingRegistry().getValue(buildinId);

			if (this.buildingEntry == null)
			{
				throw new RuntimeException("BuildingEntry '" + buildinId + "' is not found during load CustomCraftingModule");
			}

			this.jobEntry = IMinecoloniesAPI.getInstance().getJobRegistry().getValue(jobId);

			if (this.jobEntry == null)
			{
				throw new RuntimeException("JobEntry '" + jobId + "' is not found during load CustomCraftingModule");
			}

		}

		public String name()
		{
			return this.name;
		}

		public Builder buildingEntry(BuildingEntry buildingEntry)
		{
			this.buildingEntry = buildingEntry;
			return this;
		}

		public BuildingEntry buildingEntry()
		{
			return this.buildingEntry;
		}

		public Builder jobEntry(JobEntry jobEntry)
		{
			this.jobEntry = jobEntry;
			return this;
		}

		public JobEntry jobEntry()
		{
			return this.jobEntry;
		}

		public Builder forceVisible(boolean forceVisible)
		{
			this.forceVisible = forceVisible;
			return this;
		}

		public boolean forceVisible()
		{
			return this.forceVisible;
		}

	}

}
