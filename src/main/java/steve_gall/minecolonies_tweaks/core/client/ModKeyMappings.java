package steve_gall.minecolonies_tweaks.core.client;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.util.Lazy;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;

public class ModKeyMappings
{
	public static final String CATEGORY = "key." + MineColoniesTweaks.MOD_ID + ".categories.general";

	public static final Lazy<KeyMapping> RESOURCESCROLL_BOOK = Lazy.of(() -> new KeyMapping("key." + MineColoniesTweaks.MOD_ID + ".resourcescroll_book", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), CATEGORY));

	public static void register(RegisterKeyMappingsEvent event)
	{
		event.register(RESOURCESCROLL_BOOK.get());
	}

	private ModKeyMappings()
	{

	}

}
