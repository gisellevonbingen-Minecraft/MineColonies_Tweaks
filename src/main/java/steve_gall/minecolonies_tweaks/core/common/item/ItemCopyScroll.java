package steve_gall.minecolonies_tweaks.core.common.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.modules.IBuildingModule;
import com.minecolonies.api.colony.buildings.modules.ICraftingBuildingModule;
import com.minecolonies.api.colony.buildings.modules.IEntityListModule;
import com.minecolonies.api.colony.buildings.modules.IItemListModule;
import com.minecolonies.api.colony.buildings.modules.IMinimumStockModule;
import com.minecolonies.api.colony.buildings.modules.IPersistentModule;
import com.minecolonies.api.colony.buildings.modules.ISettingsModule;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import com.minecolonies.api.colony.requestsystem.requestable.IDeliverable;
import com.minecolonies.api.tileentities.AbstractTileEntityColonyBuilding;
import com.minecolonies.core.colony.buildings.modules.RestaurantMenuModule;

import io.netty.buffer.Unpooled;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
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
import steve_gall.minecolonies_tweaks.api.common.building.module.IIdListModule;
import steve_gall.minecolonies_tweaks.api.common.building.module.IMaximumStockModule;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.core.common.building.BuildingUtils;

public class ItemCopyScroll extends Item
{
	public static final String NBT_DATA = MineColoniesTweaks.rl("copyscroll").toString();
	public static final String NBT_BUILDING_NAME = "name";
	public static final String NBT_ENTRIES = "tagList";
	public static final String NBT_ENTRY_KEY = "key";
	public static final String NBT_ENTRY_TAG = "tag";

	public static final Component MESSAGE_NO_DATA = Component.translatable("item.minecolonies_tweaks.copyscroll.no_data");
	public static final Component MESSAGE_CLEARED = Component.translatable("item.minecolonies_tweaks.copyscroll.cleared");

	public static final List<Component> TOOLTIPS = Arrays.asList(//
			Component.translatable("item.minecolonies_tweaks.copyscroll.tooltip1"), //
			Component.translatable("item.minecolonies_tweaks.copyscroll.tooltip2"), //
			Component.translatable("item.minecolonies_tweaks.copyscroll.tooltip3")//
	);

	public ItemCopyScroll(Item.Properties properties)
	{
		super(properties.stacksTo(1));
	}

	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag)
	{
		super.appendHoverText(stack, level, tooltip, flag);

		tooltip.addAll(TOOLTIPS);

		var tag = stack.getTagElement(NBT_DATA);

		if (tag == null)
		{
			return;
		}

		tooltip.add(Component.empty());
		tooltip.add(Component.translatable("item.minecolonies_tweaks.copyscroll.data", Component.Serializer.fromJson(tag.getString(NBT_BUILDING_NAME)).withStyle(ChatFormatting.DARK_PURPLE)));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		if (!level.isClientSide() && player.isShiftKeyDown())
		{
			var stack = player.getItemInHand(hand);
			stack.removeTagKey(NBT_DATA);

			player.sendSystemMessage(MESSAGE_CLEARED);
		}

		return super.use(level, player, hand);
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context)
	{
		if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof AbstractTileEntityColonyBuilding be)
		{
			if (context.getLevel().isClientSide())
			{
				return InteractionResult.SUCCESS;
			}

			var building = be.getBuilding();

			if (building == null)
			{
				return InteractionResult.PASS;
			}

			var player = context.getPlayer();

			if (player == null)
			{
				return InteractionResult.PASS;
			}

			if (player.isShiftKeyDown())
			{
				var entries = new ListTag();
				var names = Component.empty();

				for (var moduleProducer : BuildingEntry.getALlModuleProducers().values())
				{
					if (building.hasModule(moduleProducer))
					{
						@SuppressWarnings("unchecked")
						var module = building.getModule(moduleProducer);

						if (module instanceof IPersistentModule persistentModule && canCopy(persistentModule))
						{
							var text = getModuleViewText(module);
							var entry = new CompoundTag();
							entry.putString(NBT_ENTRY_KEY, moduleProducer.key);
							entry.put(NBT_ENTRY_TAG, copy(persistentModule));
							entries.add(entry);

							if (entries.size() > 1)
							{
								names.append(", ");
							}

							names.append(Component.translatable("'%s'", text));
						}

					}

				}

				var data = stack.getOrCreateTagElement(NBT_DATA);
				data.putString(NBT_BUILDING_NAME, Component.Serializer.toJson(BuildingUtils.getDisplayName(building)));
				data.put(NBT_ENTRIES, entries);

				player.sendSystemMessage(Component.translatable("item.minecolonies_tweaks.copyscroll.copied", entries.size(), names));
			}
			else
			{
				var data = stack.getTagElement(NBT_DATA);

				if (data == null)
				{
					player.sendSystemMessage(MESSAGE_NO_DATA);
					return InteractionResult.SUCCESS;
				}

				var entries = data.getList(NBT_ENTRIES, Tag.TAG_COMPOUND);
				var pasted = 0;
				var names = Component.empty();

				for (var i = 0; i < entries.size(); i++)
				{
					var entry = entries.getCompound(i);
					var key = entry.getString(NBT_ENTRY_KEY);
					var tag = entry.getCompound(NBT_ENTRY_TAG);

					var producer = BuildingEntry.getProducer(key);

					if (producer == null)
					{
						continue;
					}

					if (building.hasModule(producer))
					{
						@SuppressWarnings("unchecked")
						var module = building.getModule(producer);

						if (module instanceof IPersistentModule persistentModule && canCopy(persistentModule))
						{
							try
							{
								var text = getModuleViewText(module);
								paste(persistentModule, tag);
								pasted++;

								if (pasted > 1)
								{
									names.append(", ");
								}

								names.append(Component.translatable("'%s'", text));
							}
							catch (Exception e)
							{

							}

						}

					}

				}

				player.sendSystemMessage(Component.translatable("item.minecolonies_tweaks.copyscroll.pasted", pasted, names));
			}

			return InteractionResult.SUCCESS;
		}
		else
		{
			return InteractionResult.PASS;
		}

	}

	public static CompoundTag copy(IPersistentModule module)
	{
		var tag = new CompoundTag();
		module.serializeNBT(tag);
		return tag;
	}

	public static void paste(IPersistentModule module, CompoundTag tag)
	{
		onPastePre(module);

		module.deserializeNBT(tag);
		module.markDirty();

		onPastePost(module);
	}

	private static void onPastePre(IPersistentModule module)
	{
		if (module instanceof ICraftingBuildingModule craftingModule)
		{
			for (var token : new ArrayList<>(craftingModule.getRecipes()))
			{
				craftingModule.removeRecipe(token);
			}

		}

	}

	private static void onPastePost(IPersistentModule module)
	{
		if (module instanceof ICraftingBuildingModule craftingModule)
		{
			var requestManager = craftingModule.getBuilding().getColony().getRequestManager();

			for (var token : new ArrayList<>(craftingModule.getRecipes()))
			{
				var recipeStorage = IColonyManager.getInstance().getRecipeManager().getRecipes().get(token);

				if (recipeStorage != null)
				{
					var allOutputs = Stream.concat(Stream.of(recipeStorage.getPrimaryOutput()), recipeStorage.getAlternateOutputs().stream()).filter(stack -> !stack.isEmpty()).toList();
					requestManager.onColonyUpdate(request -> request.getRequest() instanceof IDeliverable delivery && allOutputs.stream().anyMatch(i -> delivery.matches(i)));
				}

			}

		}

	}

	public static boolean canCopy(IPersistentModule module)
	{
		return module instanceof ISettingsModule || module instanceof IMinimumStockModule //
				|| module instanceof IEntityListModule || module instanceof IItemListModule //
				|| module instanceof RestaurantMenuModule || module instanceof ICraftingBuildingModule //
				|| module instanceof IMaximumStockModule || module instanceof IIdListModule //
		;
	}

	public static Component getModuleViewText(IBuildingModule module)
	{
		try
		{
			var view = BuildingEntry.produceViewWithoutBuilding(module.getProducer().key, null);

			if (view != null)
			{
				var buf = new FriendlyByteBuf(Unpooled.buffer());
				module.serializeToView(buf, true);
				view.deserialize(buf);

				return view.getDesc();
			}

		}
		catch (Exception e)
		{

		}

		return Component.literal(module.getProducer().key);

	}

}
