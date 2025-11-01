package steve_gall.minecolonies_tweaks.mixin.client.minecolonies;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minecolonies.core.colony.crafting.ToolUsage;
import com.minecolonies.core.compatibility.jei.ToolRecipeCategory;

import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraftforge.fml.ModList;
import steve_gall.minecolonies_tweaks.api.common.tool.CustomToolType;

@Mixin(value = ToolRecipeCategory.class, remap = false)
public abstract class ToolRecipeCategoryMixin
{
	@Shadow(remap = false)
	private static int WIDTH;
	@Shadow(remap = false)
	private static int HEIGHT;
	@Shadow(remap = false)
	private static int SLOT_X;

	@Inject(method = "draw", remap = false, at = @At(value = "TAIL"))
	private void draw(ToolUsage recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics stack, double mouseX, double mouseY, CallbackInfo ci)
	{
		var customToolType = CustomToolType.find(recipe.tool());

		if (customToolType == null)
		{
			return;
		}

		var modId = customToolType.getName().getNamespace();
		var modContainer = ModList.get().getModContainerById(modId).orElse(null);

		var tooltip = new ArrayList<Component>();
		tooltip.add(customToolType.getDisplayName());

		var rawTooltip = customToolType.getTooltip().getString();

		if (!customToolType.getDefaultTooltipTranslationKey().equals(rawTooltip))
		{
			Arrays.stream(rawTooltip.split("\n")).map(Component::literal).forEach(tooltip::add);
		}

		tooltip.add(Component.literal(modContainer == null ? modId : modContainer.getModInfo().getDisplayName()).withStyle(ChatFormatting.BLUE).withStyle(ChatFormatting.ITALIC));

		var mc = Minecraft.getInstance();
		var lines = mc.font.getSplitter().splitLines(recipe.tool().getDisplayName(), SLOT_X - 4, Style.EMPTY);
		var width = 0;
		var height = lines.size() * mc.font.lineHeight;
		var x = 2;
		var y = HEIGHT - (36 + height) / 2 - 1;

		for (var line : lines)
		{
			width = Math.max(width, mc.font.width(Language.getInstance().getVisualOrder(line)));
		}

		if (new Rectangle(x, y, width, height).contains((int) mouseX, (int) mouseY))
		{
			stack.renderComponentTooltip(mc.font, tooltip, (int) mouseX, (int) mouseY);
		}

	}

}
