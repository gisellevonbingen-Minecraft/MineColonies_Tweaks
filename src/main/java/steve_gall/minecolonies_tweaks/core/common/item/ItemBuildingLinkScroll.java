package steve_gall.minecolonies_tweaks.core.common.item;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.creativetab.ModCreativeTabs;
import com.minecolonies.api.tileentities.AbstractTileEntityColonyBuilding;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import steve_gall.minecolonies_tweaks.api.common.building.BuildingPos;
import steve_gall.minecolonies_tweaks.core.common.building.BuildingUtils;

public abstract class ItemBuildingLinkScroll extends Item
{
	public static final Component TOOLIP_HOW_TO_LINK = Component.translatable("item.minecolonies_tweaks.buildingscroll.how_to_link");

	public static final Component MESSAGE_MISSING_POS = Component.translatable("item.minecolonies_tweaks.buildingscroll.missing_pos");
	public static final Component MESSAGE_MISSING_BUILDING = Component.translatable("item.minecolonies_tweaks.buildingscroll.missing_building");

	public static final Component TEXT_BUILDING_MISSING = Component.translatable("item.minecolonies_tweaks.buildingscroll.building_missing").withStyle(ChatFormatting.GRAY);
	public static final Component TEXT_LINKED = Component.translatable("item.minecolonies_tweaks.buildingscroll.linked");

	public static final String TAG_POS = "pos";

	public ItemBuildingLinkScroll(Item.Properties properites)
	{
		super(properites.stacksTo(1).tab(ModCreativeTabs.MINECOLONIES));
	}

	@Nullable
	public static void setPos(@NotNull ItemStack stack, @Nullable BuildingPos pos)
	{
		var tag = stack.getOrCreateTag();

		if (pos != null)
		{
			tag.put(TAG_POS, pos.serializeNBT());
		}
		else
		{
			tag.remove(TAG_POS);
		}

	}

	@Nullable
	public static BuildingPos getPos(@NotNull ItemStack stack)
	{
		var tag = stack.getTag();

		if (tag == null)
		{
			return null;
		}

		return new BuildingPos(tag.getCompound(TAG_POS));
	}

	protected abstract void openWindow(@NotNull ItemStack stack, @Nullable Player player, @Nullable IBuildingView buildingView);

	public abstract boolean testForLink(@NotNull IBuilding building);

	public void openWindow(@NotNull ItemStack stack, @Nullable Player player)
	{
		var pos = getPos(stack);

		if (pos == null)
		{
			if (player != null)
			{
				player.sendSystemMessage(MESSAGE_MISSING_POS);
			}

			return;
		}

		var buildingView = pos.getBuildingView();

		if (buildingView == null)
		{
			if (player != null)
			{
				player.sendSystemMessage(MESSAGE_MISSING_BUILDING);
			}

			return;
		}

		this.openWindow(stack, player, buildingView);
	}

	@Override
	public InteractionResult useOn(UseOnContext context)
	{
		var level = context.getLevel();
		var stack = context.getItemInHand();
		var blockEntity = level.getBlockEntity(context.getClickedPos());

		if (level.isClientSide())
		{
			if (blockEntity instanceof AbstractTileEntityColonyBuilding)
			{

			}
			else
			{
				this.openWindow(stack, context.getPlayer());
			}

		}
		else if (blockEntity instanceof AbstractTileEntityColonyBuilding buildingEntity)
		{
			var building = buildingEntity.getBuilding();

			if (building != null && this.testForLink(building))
			{
				setPos(stack, new BuildingPos(building));
				context.getPlayer().sendSystemMessage(TEXT_LINKED);
			}

		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		var stack = player.getItemInHand(hand);

		if (level.isClientSide())
		{
			this.openWindow(stack, player);
		}

		return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag)
	{
		super.appendHoverText(stack, level, tooltip, flag);
		tooltip.add(TOOLIP_HOW_TO_LINK);

		if (level == null)
		{
			return;
		}

		var pos = getPos(stack);

		if (pos == null)
		{
			return;
		}

		tooltip.add(Component.empty());
		tooltip.add(Component.translatable("item.minecolonies_tweaks.buildingscroll.linked_pos", pos.getX(), pos.getY(), pos.getZ()));

		var buildingView = pos.getBuildingView();
		Component buildingName = null;

		if (buildingView == null)
		{
			buildingName = TEXT_BUILDING_MISSING;
		}
		else
		{
			buildingName = Component.empty().append(BuildingUtils.getDisplayName(buildingView)).withStyle(ChatFormatting.DARK_PURPLE);
		}

		tooltip.add(Component.translatable("item.minecolonies_tweaks.buildingscroll.linked_building", buildingName));
	}

}
