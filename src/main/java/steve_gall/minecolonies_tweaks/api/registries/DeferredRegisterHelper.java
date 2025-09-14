package steve_gall.minecolonies_tweaks.api.registries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import com.minecolonies.api.colony.guardtype.GuardType;
import com.minecolonies.api.colony.jobs.registry.JobEntry;
import com.minecolonies.api.crafting.registry.CraftingType;
import com.minecolonies.api.crafting.registry.RecipeTypeEntry;
import com.minecolonies.api.equipment.registry.EquipmentTypeEntry;
import com.minecolonies.api.sounds.EventType;
import com.minecolonies.api.util.Tuple;
import com.minecolonies.api.util.constant.Constants;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class DeferredRegisterHelper
{
	public static DeferredRegister<BuildingEntry> buildings(String modid)
	{
		return DeferredRegister.create(new ResourceLocation(Constants.MOD_ID, "buildings"), modid);
	}

	public static RegistryObject<BuildingEntry> registerBuilding(DeferredRegister<BuildingEntry> register, String name, Consumer<BuildingEntry.Builder> consumer)
	{
		var rl = register.createTagKey(name).location();
		return register.register(name, () ->
		{
			var builder = new BuildingEntry.Builder();
			builder.setRegistryName(rl);

			consumer.accept(builder);
			return builder.createBuildingEntry();
		});
	}

	public static DeferredRegister<GuardType> guardTypes(String modid)
	{
		return DeferredRegister.create(new ResourceLocation(Constants.MOD_ID, "guardtypes"), modid);
	}

	public static RegistryObject<GuardType> registerGuardType(DeferredRegister<GuardType> register, RegistryObject<JobEntry> jobEntry, Consumer<GuardType.Builder> consumer)
	{
		return registerGuardType(register, jobEntry.getId().getPath(), jobEntry, consumer);
	}

	public static RegistryObject<GuardType> registerGuardType(DeferredRegister<GuardType> register, String name, Supplier<JobEntry> jobEntry, Consumer<GuardType.Builder> consumer)
	{
		var rl = register.createTagKey(name).location();
		return register.register(name, () ->
		{
			var builder = new GuardType.Builder();
			builder.setJobTranslationKey(rl.getNamespace() + ".job." + name);
			builder.setButtonTranslationKey(rl.getNamespace() + ".gui.workerhuts." + name);
			builder.setJobEntry(jobEntry);
			builder.setRegistryName(rl);

			consumer.accept(builder);
			return builder.createGuardType();
		});
	}

	public static DeferredRegister<JobEntry> jobs(String modid)
	{
		return DeferredRegister.create(new ResourceLocation(Constants.MOD_ID, "jobs"), modid);
	}

	public static RegistryObject<JobEntry> registerJobEntry(DeferredRegister<JobEntry> register, String name, Consumer<JobEntry.Builder> consumer)
	{
		var rl = register.createTagKey(name).location();
		return register.register(name, () ->
		{
			var builder = new JobEntry.Builder();
			builder.setRegistryName(rl);

			consumer.accept(builder);
			return builder.createJobEntry();
		});

	}

	public static Map<EventType, List<Tuple<SoundEvent, SoundEvent>>> registerJobSoundEvents(DeferredRegister<SoundEvent> register, String name)
	{
		var namespace = register.createTagKey(name).location().getNamespace();
		var map = new HashMap<EventType, List<Tuple<SoundEvent, SoundEvent>>>();

		for (var event : EventType.values())
		{
			var individualSounds = new ArrayList<Tuple<SoundEvent, SoundEvent>>();

			for (var i = 1; i <= 4; i++)
			{
				var prefix = "citizen." + name;
				var suffix = i + "." + event.getId();

				var maleSoundEvent = SoundEvent.createVariableRangeEvent(new ResourceLocation(namespace, prefix + ".male" + suffix));
				var femaleSoundEvent = SoundEvent.createVariableRangeEvent(new ResourceLocation(namespace, prefix + ".female" + suffix));

				register.register(maleSoundEvent.getLocation().getPath(), () -> maleSoundEvent);
				register.register(femaleSoundEvent.getLocation().getPath(), () -> femaleSoundEvent);
				individualSounds.add(new Tuple<>(maleSoundEvent, femaleSoundEvent));
			}

			map.put(event, individualSounds);
		}

		return map;
	}

	public static DeferredRegister<CraftingType> craftingTypes(String modid)
	{
		return DeferredRegister.create(new ResourceLocation(Constants.MOD_ID, "craftingtypes"), modid);
	}

	public static <T extends CraftingType> RegistryObject<T> registerCraftingType(DeferredRegister<CraftingType> register, String name, Function<ResourceLocation, T> func)
	{
		var id = register.createTagKey(name).location();
		return register.register(name, () -> func.apply(id));
	}

	public static DeferredRegister<RecipeTypeEntry> recipeTypeEntries(String modid)
	{
		return DeferredRegister.create(new ResourceLocation(Constants.MOD_ID, "recipetypeentries"), modid);
	}

	public static RegistryObject<RecipeTypeEntry> registerRecipeTypeEntry(DeferredRegister<RecipeTypeEntry> register, String name, BiConsumer<ResourceLocation, RecipeTypeEntry.Builder> consumer)
	{
		var id = register.createTagKey(name).location();
		return register.register(name, () ->
		{
			var builder = new RecipeTypeEntry.Builder();
			builder.setRegistryName(id);
			consumer.accept(id, builder);
			return builder.createRecipeTypeEntry();
		});

	}

	public static DeferredRegister<EquipmentTypeEntry> equipmentTypes(String modid)
	{
		return DeferredRegister.create(new ResourceLocation(Constants.MOD_ID, "equipmenttypes"), modid);
	}

	private DeferredRegisterHelper()
	{

	}

}
