package steve_gall.minecolonies_tweaks.core.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
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
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks)
	{
		this.renderBackground(pose);

		super.render(pose, mouseX, mouseY, partialTicks);

		this.renderTooltip(pose, mouseX, mouseY);
	}

	@Override
	protected void renderBg(PoseStack pose, float partialTicks, int mouseX, int mouseY)
	{
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		this.blit(pose, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
	}

}
