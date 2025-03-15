package steve_gall.minecolonies_tweaks.api.common.tool;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import steve_gall.minecolonies_tweaks.core.common.util.GsonHelper2;

public class ConfigToolType extends CustomToolType
{
	private final boolean hasVariableMaterials;
	@NotNull
	private final String translationKey;
	@NotNull
	private final int defaultLevel;

	public ConfigToolType(Builder builder, String namespace)
	{
		super(new ResourceLocation(namespace, builder.name));

		this.hasVariableMaterials = builder.hasVariableMaterials;
		this.translationKey = builder.translationKey.orElseGet(() -> getFallbackTranslationKey(this.getName().getPath()));
		this.defaultLevel = builder.defaultLevel;
	}

	@Override
	public boolean hasVariableMaterials()
	{
		return this.hasVariableMaterials;
	}

	@Override
	protected Component createDisplayName()
	{
		return Component.translatable(this.translationKey);
	}

	@Override
	public int getToolLevel(@NotNull ItemStack stack)
	{
		if (this.defaultLevel > -1)
		{
			return this.defaultLevel;
		}

		return super.getToolLevel(stack);
	}

	public static class Builder
	{
		@NotNull
		private final String name;

		private boolean hasVariableMaterials = false;
		@NotNull
		private Optional<String> translationKey = Optional.empty();
		private int defaultLevel = -1;

		public Builder(@NotNull String name)
		{
			this.name = name;
		}

		public Builder(@NotNull ConfigToolType data)
		{
			this.name = data.getName().getPath();
			this.hasVariableMaterials = data.hasVariableMaterials;
			this.translationKey = Optional.of(data.translationKey);
			this.defaultLevel = data.defaultLevel;
		}

		public Builder(@NotNull JsonObject json)
		{
			this.name = GsonHelper.getAsString(json, "name");
			this.hasVariableMaterials = GsonHelper.getAsBoolean(json, "hasVariableMaterials", false);
			this.translationKey = GsonHelper2.of(json, "translationKey", GsonHelper::getAsString);
			this.defaultLevel = GsonHelper.getAsInt(json, "defaultLevel", -1);
		}

		@NotNull
		public JsonObject toObject()
		{
			var json = new JsonObject();
			json.addProperty("name", this.name());
			json.addProperty("hasVariableMaterials", this.hasVariableMaterials());
			GsonHelper2.ifPresent("translationKey", this.translationKey(), json::addProperty);
			json.addProperty("defaultLevel", this.defaultLevel());
			return json;
		}

		@NotNull
		public String name()
		{
			return this.name;
		}

		public boolean hasVariableMaterials()
		{
			return this.hasVariableMaterials;
		}

		@NotNull
		public Builder hasVariableMaterials(boolean hasVariableMaterials)
		{
			this.hasVariableMaterials = hasVariableMaterials;
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

	}

}
