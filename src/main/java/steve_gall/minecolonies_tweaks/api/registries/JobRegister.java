package steve_gall.minecolonies_tweaks.api.registries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import com.minecolonies.api.colony.jobs.registry.JobEntry;
import com.minecolonies.api.sounds.EventType;
import com.minecolonies.api.sounds.ModSoundEvents;
import com.minecolonies.api.util.Tuple;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class JobRegister
{
	@NotNull
	private final DeferredRegister<JobEntry> jobs;
	@NotNull
	private final DeferredRegister<SoundEvent> soundEvents;

	@NotNull
	private final Map<String, Map<EventType, List<Tuple<SoundEvent, SoundEvent>>>> soundEventMap;

	public JobRegister(@NotNull String modid)
	{
		this.jobs = DeferredRegisterHelper.jobs(modid);
		this.soundEvents = DeferredRegister.create(Registries.SOUND_EVENT, modid);

		this.soundEventMap = new HashMap<>();
	}

	public void register(@NotNull IEventBus bus)
	{
		this.getJobs().register(bus);
		this.getSounds().register(bus);

		bus.addListener(this::onFMLCommonSetup);
	}

	public RegistryObject<JobEntry> register(@NotNull String name, @NotNull Consumer<JobEntry.Builder> consumer)
	{
		var job = DeferredRegisterHelper.registerJobEntry(this.getJobs(), name, consumer);
		var soundEvents = DeferredRegisterHelper.registerJobSoundEvents(this.getSounds(), name);
		this.soundEventMap.put(name, soundEvents);
		return job;
	}

	private void onFMLCommonSetup(FMLCommonSetupEvent event)
	{
		for (var obj : this.getJobs().getEntries())
		{
			var name = obj.getId().getPath();
			var soundEvents = this.soundEventMap.get(name);
			ModSoundEvents.CITIZEN_SOUND_EVENTS.put(name, soundEvents);
		}

	}

	@NotNull
	public DeferredRegister<JobEntry> getJobs()
	{
		return this.jobs;
	}

	@NotNull
	public DeferredRegister<SoundEvent> getSounds()
	{
		return this.soundEvents;
	}

}
