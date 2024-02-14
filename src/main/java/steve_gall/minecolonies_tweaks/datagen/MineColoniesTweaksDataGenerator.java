package steve_gall.minecolonies_tweaks.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import steve_gall.minecolonies_tweaks.common.MineColoniesTweaks;

@EventBusSubscriber(modid = MineColoniesTweaks.MOD_ID, bus = Bus.MOD)
public class MineColoniesTweaksDataGenerator
{
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event)
	{
		DataGenerator gen = event.getGenerator();
		@SuppressWarnings("unused")
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
	}

	private MineColoniesTweaksDataGenerator()
	{

	}

}
