package steve_gall.minecolonies_tweaks.core.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.core.common.inventory.ResourceScrollBookInventoryMenu;

public class ResourceScrollBookInventoryScreen extends AbstractContainerScreen<ResourceScrollBookInventoryMenu>
{
	public static final ResourceLocation TEXTURE = MineColoniesTweaks.rl("textures/gui/resourcescroll_book_inventory.png");

	public ResourceScrollBookInventoryScreen(ResourceScrollBookInventoryMenu menu, Inventory inventory, Component title)
	{
		super(menu, inventory, title);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		this.renderBackground(graphics);

		super.render(graphics, mouseX, mouseY, partialTicks);

		this.renderTooltip(graphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY)
	{
		graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
	}

}
