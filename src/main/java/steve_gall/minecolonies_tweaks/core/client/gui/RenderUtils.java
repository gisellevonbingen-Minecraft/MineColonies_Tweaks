package steve_gall.minecolonies_tweaks.core.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

public class RenderUtils
{
	public static final int FLUID_TEXTURE_SIZE = 16;

	public static void renderFluid(PoseStack poseStack, int width, int height, FluidStack fluidStack)
	{
		renderFluid(poseStack, width, height, fluidStack, fluidStack.getAmount());
	}

	public static void renderFluid(PoseStack poseStack, int width, int height, FluidStack fluidStack, int capacity)
	{
		var fluid = fluidStack.getFluid();

		if (fluid.isSame(Fluids.EMPTY))
		{
			return;
		}

		var renderProperties = IClientFluidTypeExtensions.of(fluidStack.getFluid());
		var stillTexture = renderProperties.getStillTexture(fluidStack);
		var stillSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTexture);

		if (stillSprite == null)
		{
			return;
		}

		var tintColor = renderProperties.getTintColor(fluidStack);
		var scachedHeight = height * (fluidStack.getAmount() / Math.max(capacity, 1.0D));
		renderTiledSprite(poseStack, width, height, tintColor, (int) scachedHeight, stillSprite);
	}

	public static void renderTiledSprite(PoseStack poseStack, int tiledWidth, int tiledHeight, int color, int scachedHeight, TextureAtlasSprite sprite)
	{
		RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
		var matrix = poseStack.last().pose();
		setShaderColor(color);

		var xTileCount = tiledWidth / FLUID_TEXTURE_SIZE;
		var xRemainder = tiledWidth - (xTileCount * FLUID_TEXTURE_SIZE);
		var yTileCount = scachedHeight / FLUID_TEXTURE_SIZE;
		var yRemainder = scachedHeight - (yTileCount * FLUID_TEXTURE_SIZE);

		for (var xTile = 0; xTile <= xTileCount; xTile++)
		{
			for (var yTile = 0; yTile <= yTileCount; yTile++)
			{
				var width = (xTile == xTileCount) ? xRemainder : FLUID_TEXTURE_SIZE;
				var height = (yTile == yTileCount) ? yRemainder : FLUID_TEXTURE_SIZE;

				if (width > 0 && height > 0)
				{
					var x = xTile * FLUID_TEXTURE_SIZE;
					var y = tiledHeight - ((yTile + 1) * FLUID_TEXTURE_SIZE);
					var maskTop = FLUID_TEXTURE_SIZE - height;
					var maskRight = FLUID_TEXTURE_SIZE - width;
					renderTextureWithMasking(matrix, x, y, sprite, maskTop, maskRight, 100);
				}

			}

		}

		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}

	public static void setShaderColor(int color)
	{
		var a = ((color >> 0x18) & 0xFF) / 255.0F;
		var r = ((color >> 0x10) & 0xFF) / 255.0F;
		var g = ((color >> 0x08) & 0xFF) / 255.0F;
		var b = ((color >> 0x00) & 0xFF) / 255.0F;
		RenderSystem.setShaderColor(r, g, b, a);
	}

	public static void renderTextureWithMasking(Matrix4f matrix, float xCoord, float yCoord, TextureAtlasSprite textureSprite, long maskTop, long maskRight, float zLevel)
	{
		var f = 16.0F;
		var u0 = textureSprite.getU0();
		var u1 = textureSprite.getU1();
		var v0 = textureSprite.getV0();
		var v1 = textureSprite.getV1();
		u1 = u1 - (maskRight / f * (u1 - u0));
		v1 = v1 - (maskTop / f * (v1 - v0));

		RenderSystem.setShader(GameRenderer::getPositionTexShader);

		var tessellator = Tesselator.getInstance();
		var bufferBuilder = tessellator.getBuilder();
		bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		bufferBuilder.vertex(matrix, xCoord, yCoord + f, zLevel).uv(u0, v1).endVertex();
		bufferBuilder.vertex(matrix, xCoord + f - maskRight, yCoord + f, zLevel).uv(u1, v1).endVertex();
		bufferBuilder.vertex(matrix, xCoord + f - maskRight, yCoord + maskTop, zLevel).uv(u1, v0).endVertex();
		bufferBuilder.vertex(matrix, xCoord, yCoord + maskTop, zLevel).uv(u0, v0).endVertex();
		tessellator.end();
	}

	private RenderUtils()
	{

	}

}
