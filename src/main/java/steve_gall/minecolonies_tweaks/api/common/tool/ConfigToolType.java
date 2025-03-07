package steve_gall.minecolonies_tweaks.api.common.tool;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;
import com.minecolonies.api.equipment.ModEquipmentTypes;
import com.minecolonies.api.util.ItemStackUtils;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import steve_gall.minecolonies_tweaks.core.common.util.GsonHelper2;

public class ConfigToolType extends CustomToolType
{
	@NotNull
	private final AutoLevelType autoLevelType;
	@NotNull
	private final String translationKey;
	@NotNull
	private final int defaultLevel;
	@NotNull
	private final int durabilityBase;

	public ConfigToolType(Builder builder, String namespace)
	{
		super(new ResourceLocation(namespace, builder.name));

		this.autoLevelType = builder.autoLevelType;
		this.translationKey = builder.translationKey.orElseGet(() -> getFallbackTranslationKey(this.getName()));
		this.defaultLevel = builder.defaultLevel;
		this.durabilityBase = builder.durabilityBase;
	}

	@NotNull
	public AutoLevelType getAutoLevelType()
	{
		return this.autoLevelType;
	}

	public int getDurabilityBase()
	{
		return this.durabilityBase;
	}

	@Override
	protected Component createDisplayName()
	{
		return Component.translatable(this.translationKey);
	}

	@Override
	public boolean isTool(@NotNull ItemStack stack)
	{
		return super.isTool(stack);
	}

	@Override
	public int getToolLevel(@NotNull ItemStack stack)
	{
		var autoLevelType = this.getAutoLevelType();
		return autoLevelType.getToolLevel(stack, this);
	}

	@Override
	public int getDefaultLevel()
	{
		return this.defaultLevel;
	}

	public static enum AutoLevelType
	{
		NONE()
			{
				@Override
				public int getToolLevel(@NotNull ItemStack stack, @NotNull ConfigToolType toolType)
				{
					return -1;
				}
			},
		VANILLA_ARMOR()
			{
				@Override
				public int getToolLevel(@NotNull ItemStack stack, @NotNull ConfigToolType toolType)
				{
					return ItemStackUtils.getArmorLevel(stack);
				}
			},
		VANILLA_TOOL()
			{
				@Override
				public int getToolLevel(@NotNull ItemStack stack, @NotNull ConfigToolType toolType)
				{
					return ModEquipmentTypes.vanillaToolLevel(stack, toolType.getToolType());
				}

			},
		DURABILITY_BASE()
			{
				@Override
				public int getToolLevel(@NotNull ItemStack stack, @NotNull ConfigToolType toolType)
				{
					return ModEquipmentTypes.durabilityBasedLevel(stack, toolType.getDurabilityBase());
				}

			},
		// EOL
		;

		public int getToolLevel(@NotNull ItemStack stack, @NotNull ConfigToolType toolType)
		{
			return -1;
		}

	}

	public static class Builder
	{
		@NotNull
		private final String name;

		@NotNull
		private AutoLevelType autoLevelType = AutoLevelType.NONE;
		@NotNull
		private Optional<String> translationKey = Optional.empty();
		private int defaultLevel = 0;
		private int durabilityBase = 0;

		public Builder(@NotNull String name)
		{
			this.name = name;
		}

		public Builder(@NotNull ConfigToolType data)
		{
			this.name = data.getName().getPath();
			this.autoLevelType = data.autoLevelType;
			this.translationKey = Optional.of(data.translationKey);
			this.defaultLevel = data.defaultLevel;
			this.durabilityBase = data.durabilityBase;
		}

		public Builder(@NotNull JsonObject json)
		{
			this.name = GsonHelper.getAsString(json, "name");
			this.autoLevelType = AutoLevelType.valueOf(GsonHelper.getAsString(json, "autoLevelType", AutoLevelType.NONE.name()));
			this.translationKey = GsonHelper2.of(json, "translationKey", GsonHelper::getAsString);
			this.defaultLevel = GsonHelper.getAsInt(json, "defaultLevel", 0);
			this.durabilityBase = GsonHelper.getAsInt(json, "durabilityBase", Integer.MAX_VALUE);
		}

		@NotNull
		public JsonObject toObject()
		{
			var json = new JsonObject();
			json.addProperty("name", this.name());
			json.addProperty("autoLevelType", this.autoLevelType().name());
			GsonHelper2.ifPresent("translationKey", this.translationKey(), json::addProperty);
			json.addProperty("defaultLevel", this.defaultLevel());
			json.addProperty("durabilityBase", this.durabilityBase());
			return json;
		}

		@NotNull
		public String name()
		{
			return this.name;
		}

		@NotNull
		public AutoLevelType autoLevelType()
		{
			return this.autoLevelType;
		}

		@NotNull
		public Builder autoLevelType(@NotNull AutoLevelType autoLevelType)
		{
			this.autoLevelType = autoLevelType;
			return this;
		}

		@NotNull
		public Optional<String> translationKey()
		{
			return this.translationKey;
		}

		@NotNull
		public Builder translationKey(@NotNull Optional<String> translationKey)
		{
			this.translationKey = translationKey;
			return this;
		}

		@NotNull
		public int defaultLevel()
		{
			return this.defaultLevel;
		}

		@NotNull
		public Builder defaultLevel(int defaultLevel)
		{
			this.defaultLevel = defaultLevel;
			return this;
		}

		public int durabilityBase()
		{
			return this.durabilityBase;
		}

		@NotNull
		public Builder durabilityBase(int durabilityBase)
		{
			this.durabilityBase = durabilityBase;
			return this;
		}

	}

}
