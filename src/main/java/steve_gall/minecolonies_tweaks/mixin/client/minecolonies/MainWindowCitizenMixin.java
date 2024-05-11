package steve_gall.minecolonies_tweaks.mixin.client.minecolonies;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.minecolonies.api.colony.ICitizenDataView;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.entity.citizen.Skill;
import com.minecolonies.core.client.gui.citizen.MainWindowCitizen;
import com.minecolonies.core.network.messages.server.colony.citizen.AdjustSkillCitizenMessage;

import net.minecraft.client.gui.screens.Screen;

@Mixin(value = MainWindowCitizen.class, remap = false)
public abstract class MainWindowCitizenMixin
{
	@Redirect(method = "onButtonClicked", remap = false, at = @At(value = "NEW", target = "com/minecolonies/core/network/messages/server/colony/citizen/AdjustSkillCitizenMessage"))
	private AdjustSkillCitizenMessage onButtonClicked(IColony colony, @NotNull ICitizenDataView citizenDataView, int quantity, Skill skill)
	{
		if (Screen.hasControlDown())
		{
			quantity *= 2;
		}

		if (Screen.hasShiftDown())
		{
			quantity *= 5;
		}

		return new AdjustSkillCitizenMessage(colony, citizenDataView, quantity, skill);
	}

}
