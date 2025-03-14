package steve_gall.minecolonies_tweaks.mixin.client.minecolonies;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.ldtteam.blockui.Loader;
import com.ldtteam.blockui.PaneParams;
import com.ldtteam.blockui.controls.Button;
import com.ldtteam.blockui.views.View;
import com.minecolonies.api.colony.ICitizenDataView;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.entity.citizen.Skill;
import com.minecolonies.core.Network;
import com.minecolonies.core.client.gui.citizen.AbstractWindowCitizen;
import com.minecolonies.core.client.gui.citizen.MainWindowCitizen;
import com.minecolonies.core.network.messages.server.colony.citizen.AdjustSkillCitizenMessage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import steve_gall.minecolonies_tweaks.core.client.gui.ViewOverrideExtension;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;

@Mixin(value = MainWindowCitizen.class, remap = false)
public abstract class MainWindowCitizenMixin extends AbstractWindowCitizen implements ViewOverrideExtension
{
	@Unique
	private static final String SKILL_ALL_PLUS = "skill_all_up";
	@Unique
	private static final String SKILL_ALL_MINUS = "skill_all_down";
	@Unique
	private static final String SKILL_ALL_BTS = "skill_all_bts";

	@Shadow(remap = false)
	private ICitizenDataView citizen;
	@Shadow(remap = false)
	private int tick;

	public MainWindowCitizenMixin(ICitizenDataView citizen, String ui)
	{
		super(citizen, ui);
	}

	@Inject(method = "onButtonClicked", remap = false, at = @At(value = "HEAD"), cancellable = false)
	private void onButtonClicked(Button button, CallbackInfo ci)
	{
		if (button.getID().contains(SKILL_ALL_PLUS))
		{
			for (var skill : Skill.values())
			{
				Network.getNetwork().sendToServer(new AdjustSkillCitizenMessage(this.colony, this.citizen, this.getSkillQuantity(+1), skill));
			}

		}
		else if (button.getID().contains(SKILL_ALL_MINUS))
		{
			for (var skill : Skill.values())
			{
				Network.getNetwork().sendToServer(new AdjustSkillCitizenMessage(this.colony, this.citizen, this.getSkillQuantity(-1), skill));
			}

		}

	}

	@Redirect(method = "onButtonClicked", remap = false, at = @At(value = "NEW", target = "com/minecolonies/core/network/messages/server/colony/citizen/AdjustSkillCitizenMessage"))
	private AdjustSkillCitizenMessage onButtonClicked(IColony colony, @NotNull ICitizenDataView citizenDataView, int quantity, Skill skill)
	{
		return new AdjustSkillCitizenMessage(colony, citizenDataView, this.getSkillQuantity(quantity), skill);
	}

	@Unique
	private int getSkillQuantity(int quantity)
	{
		if (Screen.hasControlDown())
		{
			quantity *= 2;
		}

		if (Screen.hasShiftDown())
		{
			quantity *= 5;
		}

		return quantity;
	}

	@Inject(method = "onUpdate", remap = false, at = @At(value = "HEAD"), cancellable = false)
	private void onUpdateTail(CallbackInfo ci)
	{
		if (this.tick == 0)
		{
			var mc = Minecraft.getInstance();
			this.window.findPaneByID(SKILL_ALL_BTS).setVisible(mc.player.isCreative());
		}

	}

	@Override
	public void minecolonies_tweaks$onParse(View view, PaneParams params)
	{
		Loader.createFromXMLFile(MineColoniesTweaks.rl("gui/citizen/main.xml"), this);
	}

}
