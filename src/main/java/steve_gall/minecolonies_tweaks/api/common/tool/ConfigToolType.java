package steve_gall.minecolonies_tweaks.api.common.tool;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import steve_gall.minecolonies_tweaks.core.common.util.GsonHelper2;

public class ConfigToolType extends CustomToolType
{
	private final boolean hasVariableMaterials;
	@NotNull
	private final String translationKey;
	@NotNull
	private final Optional<Integer> defaultLevel;

	public ConfigToolType(Builder builder, String namespace)
	{
		super(new ResourceLocation(namespace, builder.name));

		this.hasVariableMaterials = builder.hasVariableMaterials;
		this.translationKey = builder.translationKey.orElseGet(() -> getFallbackTranslationKey(this.getName().getPath()));
		this.defaultLevel = builder.defaultLevel;
	}

	public boolean hasVariableMaterials()
	{
		return this.hasVariableMaterials;
	}

	@Override
	protected Component createDisplayName()
	{
		return Component.translatable(this.translationKey);
	}

	@NotNull
	public Optional<Integer> getDefaultLevel()
	{
		return this.defaultLevel;
	}

	public static class Builder
	{
		@NotNull
		private final String name;

		private boolean hasVariableMaterials = false;
		@NotNull
		private Optional<String> translationKey = Optional.empty();
		private Optional<Integer> defaultLevel = Optional.empty();

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
			this.defaultLevel = GsonHelper2.of(json, "defaultLevel", GsonHelper::getAsInt);
		}

		@NotNull
		public JsonObject toObject()
		{
			var json = new JsonObject();
			json.addProperty("name", this.name());
			json.addProperty("hasVariableMaterials", this.hasVariableMaterials());
			GsonHelper2.ifPresent("translationKey", this.translationKey(), json::addProperty);
			GsonHelper2.ifPresent("defaultLevel", this.defaultLevel(), json::addProperty);
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

		@NotNull
		public Optional<Integer> defaultLevel()
		{
			return this.defaultLevel;
		}

		@NotNull
		public Builder defaultLevel(@NotNull Optional<Integer> defaultLevel)
		{
			this.defaultLevel = defaultLevel;
			return this;
		}

	}

}
