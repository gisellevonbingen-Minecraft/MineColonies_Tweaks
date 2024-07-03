package steve_gall.minecolonies_tweaks.core.client.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.ldtteam.blockui.Pane;
import com.ldtteam.blockui.PaneParams;
import com.ldtteam.blockui.controls.AbstractTextBuilder.AutomaticTooltipBuilder;
import com.ldtteam.blockui.controls.Tooltip.AutomaticTooltip;
import com.ldtteam.blockui.util.SpacerTextComponent;
import com.ldtteam.blockui.util.ToggleableTextComponent;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import steve_gall.minecolonies_tweaks.core.client.gui.RenderUtils;

public class FluidIcon extends Pane
{
	protected static final float DEFAULT_ITEMSTACK_SIZE = 16.0F;
	protected static final MutableComponent FIX_VANILLA_TOOLTIP = SpacerTextComponent.of(1);

	@Nullable
	protected FluidStack fluidStack;

	protected boolean tooltipUpdateScheduled = false;
	protected boolean showAmount = true;

	public FluidIcon()
	{
		super();
	}

	public FluidIcon(PaneParams params)
	{
		super(params);

		var fluidName = params.getString("fluid");

		if (fluidName != null)
		{
			var fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidName));

			if (fluid != null)
			{
				var fluidAmount = params.getInteger("amount", 1);
				this.setFluid(new FluidStack(fluid, fluidAmount));
			}

		}

		this.showAmount = params.getBoolean("showAmount", this.showAmount);
	}

	public void setFluid(FluidStack fluidStack)
	{
		this.clearDataAndScheduleTooltipUpdate();
		this.fluidStack = fluidStack;
		this.onFluidUpdate();
	}

	protected void onFluidUpdate()
	{

	}

	public FluidStack getFluid()
	{
		return this.fluidStack;
	}

	public void setShowAmount(boolean showAmount)
	{
		this.showAmount = showAmount;
	}

	public boolean showAmount()
	{
		return this.showAmount;
	}

	public void clearDataAndScheduleTooltipUpdate()
	{
		this.fluidStack = null;
		this.tooltipUpdateScheduled = true;
	}

	protected boolean isFluidEmpty()
	{
		return this.fluidStack == null || this.fluidStack.isEmpty();
	}

	public boolean isDataEmpty()
	{
		return this.isFluidEmpty();
	}

	protected void updateTooltipIfNeeded()
	{
		if (this.tooltipUpdateScheduled)
		{
			if (this.onHover instanceof final AutomaticTooltip tooltip)
			{
				tooltip.setTextOld(this.getModifiedFluidStackTooltip());
			}

			this.tooltipUpdateScheduled = false;
		}

	}

	@Override
	public void drawSelf(PoseStack ms, double mx, double my)
	{
		this.updateTooltipIfNeeded();

		if (!this.isDataEmpty())
		{
			ms.pushPose();
			ms.translate(this.x, this.y, 0.0F);
			RenderUtils.renderFluid(ms, 16, 16, this.fluidStack);
			ms.popPose();
		}

	}

	@Override
	public void onUpdate()
	{
		if (this.onHover == null && !this.isFluidEmpty())
		{
			new AutomaticTooltipBuilder().hoverPane(this).build().setTextOld(this.getModifiedFluidStackTooltip());
		}

	}

	protected int modifyTooltipName(List<Component> tooltipList, TooltipFlag tooltipFlags, int nameOffset)
	{
		return nameOffset;
	}

	protected int appendTooltip(List<Component> tooltipList, TooltipFlag tooltipFlags, int prevTooltipSize)
	{
		return prevTooltipSize;
	}

	public List<Component> getModifiedFluidStackTooltip()
	{
		if (this.isDataEmpty())
		{
			return Collections.emptyList();
		}

		var tooltipFlags = this.mc.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL;
		var fluid = this.fluidStack.getFluid();
		var id = ForgeRegistries.FLUIDS.getKey(fluid);
		var namespace = id.getNamespace();
		var nameOffset = 1;

		var tooltipList = new ArrayList<Component>();
		tooltipList.add(this.fluidStack.getDisplayName());

		if (tooltipFlags.isAdvanced())
		{
			tooltipList.add(Component.literal(id.toString()).withStyle(ChatFormatting.DARK_GRAY));
		}

		if (this.showAmount())
		{
			tooltipList.add(Component.literal(String.format("%,d", this.fluidStack.getAmount()) + " mB").withStyle(ChatFormatting.GRAY));
		}

		tooltipList.add(Component.literal(ModList.get().getModContainerById(namespace).map(f -> f.getModInfo().getDisplayName()).orElse(namespace)).withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC));

		nameOffset = this.modifyTooltipName(tooltipList, tooltipFlags, nameOffset);

		var prevTooltipSize = tooltipList.size();

		if (tooltipFlags.isAdvanced() && this.mc.player.isCreative())
		{
			var c = nameOffset + 1;
			ForgeRegistries.FLUIDS.getHolder(fluid).map(Holder::getTagKeys).ifPresent(tags -> tags.forEach(tag -> tooltipList.add(c, wrapShift(Component.literal("#" + tag.location()).withStyle(ChatFormatting.DARK_PURPLE)))));
		}

		prevTooltipSize = this.appendTooltip(tooltipList, tooltipFlags, prevTooltipSize);

		if (prevTooltipSize != tooltipList.size())
		{
			tooltipList.add(ToggleableTextComponent.ofNegated(Screen::hasShiftDown, Component.empty()));
			tooltipList.add(ToggleableTextComponent.ofNegated(Screen::hasShiftDown, Component.translatable("blockui.tooltip.item_additional_info", Component.translatable("key.keyboard.left.shift")).withStyle(ChatFormatting.GOLD)));
		}

		tooltipList.add(nameOffset, FIX_VANILLA_TOOLTIP);
		return tooltipList;
	}

	protected static MutableComponent wrapShift(final MutableComponent wrapped)
	{
		return ToggleableTextComponent.of(Screen::hasShiftDown, wrapped);
	}

	protected static MutableComponent wrapShift(final MutableComponent wrapped, final boolean shouldWrap)
	{
		return shouldWrap ? ToggleableTextComponent.of(Screen::hasShiftDown, wrapped) : wrapped;
	}

}
