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
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.Unpooled;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
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
import steve_gall.minecolonies_tweaks.core.common.building.BuildingUtils;
import steve_gall.minecolonies_tweaks.core.common.init.MCTweaksDataComponents;

public class ItemCopyScroll extends Item
{
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
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag)
	{
		super.appendHoverText(stack, context, tooltip, flag);

		tooltip.addAll(TOOLTIPS);

		var data = stack.get(MCTweaksDataComponents.COPYSCROLL_DATA);

		if (data == null)
		{
			return;
		}

		tooltip.add(Component.empty());
		tooltip.add(Component.translatable("item.minecolonies_tweaks.copyscroll.data", Component.empty().append(data.name()).withStyle(ChatFormatting.DARK_PURPLE)));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		if (!level.isClientSide() && player.isShiftKeyDown())
		{
			var stack = player.getItemInHand(hand);
			stack.remove(MCTweaksDataComponents.COPYSCROLL_DATA);

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

			var registryAccess = player.registryAccess();

			if (player.isShiftKeyDown())
			{
				var entries = new ArrayList<Entry>();
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
							entries.add(new Entry(moduleProducer.key, copy(registryAccess, persistentModule)));

							if (entries.size() > 1)
							{
								names.append(", ");
							}

							names.append(Component.translatable("'%s'", text));
						}

					}

				}

				stack.set(MCTweaksDataComponents.COPYSCROLL_DATA, new CopyData(BuildingUtils.getDisplayName(building), entries));
				player.sendSystemMessage(Component.translatable("item.minecolonies_tweaks.copyscroll.copied", entries.size(), names));
			}
			else
			{
				var data = stack.get(MCTweaksDataComponents.COPYSCROLL_DATA);

				if (data == null)
				{
					player.sendSystemMessage(MESSAGE_NO_DATA);
					return InteractionResult.SUCCESS;
				}

				var entries = data.entries();
				var pasted = 0;
				var names = Component.empty();

				for (var entry : entries)
				{
					var producer = BuildingEntry.getProducer(entry.key);

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
								paste(persistentModule, registryAccess, entry.tag);
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

	public static CompoundTag copy(HolderLookup.Provider provider, IPersistentModule module)
	{
		var tag = new CompoundTag();
		module.serializeNBT(provider, tag);
		return tag;
	}

	public static void paste(IPersistentModule module, HolderLookup.Provider provider, CompoundTag tag)
	{
		onPastePre(module);

		module.deserializeNBT(provider, tag);
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
				|| module instanceof IMaximumStockModule || module instanceof IIdListModule//
		;
	}

	public static Component getModuleViewText(IBuildingModule module)
	{
		var view = BuildingEntry.produceViewWithoutBuilding(module.getProducer().key);

		if (view != null)
		{
			try
			{
				@SuppressWarnings("deprecation")
				var buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), module.getBuilding().getColony().getWorld().registryAccess());
				module.serializeToView(buf, true);
				view.deserialize(buf);
			}
			catch (Exception e)
			{

			}

			return view.getDesc();
		}
		else
		{
			return Component.literal(module.getProducer().key);
		}

	}

	public record CopyData(Component name, List<Entry> entries)
	{
		public static final Codec<CopyData> CODEC = RecordCodecBuilder.create(builder -> builder.group(//
				ComponentSerialization.CODEC.fieldOf("name").forGetter(CopyData::name), //
				Codec.list(Entry.CODEC).fieldOf("entries").forGetter(CopyData::entries)//
		).apply(builder, CopyData::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, CopyData> STREAM_CODEC = StreamCodec.composite(//
				ComponentSerialization.STREAM_CODEC, CopyData::name, //
				Entry.STREAM_CODEC.apply(ByteBufCodecs.list()), CopyData::entries, //
				CopyData::new);

	}

	public static record Entry(String key, CompoundTag tag)
	{
		public static final Codec<Entry> CODEC = RecordCodecBuilder.create(builder -> builder.group(//
				Codec.STRING.fieldOf("key").forGetter(Entry::key), //
				CompoundTag.CODEC.fieldOf("tag").forGetter(Entry::tag)//
		).apply(builder, Entry::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC = StreamCodec.composite(//
				ByteBufCodecs.STRING_UTF8, Entry::key, //
				ByteBufCodecs.COMPOUND_TAG, Entry::tag, //
				Entry::new);

	}

}
