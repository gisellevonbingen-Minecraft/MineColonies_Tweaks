package steve_gall.minecolonies_tweaks.apiimpl.common.crafting;

import org.jetbrains.annotations.NotNull;

import com.google.common.reflect.TypeToken;
import com.minecolonies.api.colony.requestsystem.StandardFactoryController;
import com.minecolonies.api.colony.requestsystem.factory.IFactory;
import com.minecolonies.api.colony.requestsystem.factory.IFactoryController;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.api.util.constant.TypeConstants;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import steve_gall.minecolonies_tweaks.api.common.SerializationIds;
import steve_gall.minecolonies_tweaks.api.common.crafting.CustomizableRecipeStorage;
import steve_gall.minecolonies_tweaks.api.common.crafting.CustomizedRecipeStorageRegistry;
import steve_gall.minecolonies_tweaks.api.common.crafting.ICustomizedRecipeStorage;

public class CustomizableRecipeStorageFactory implements IFactory<IToken<?>, CustomizableRecipeStorage>
{
	public static final TypeToken<CustomizableRecipeStorage> OUTPUT_TYPE = TypeToken.of(CustomizableRecipeStorage.class);
	public static final TypeToken<IToken<?>> INPUT_TYPE = TypeConstants.ITOKEN;

	public static final String IMPL_TAG = "impl";
	public static final String TOKEN_TAG = "token";

	@NotNull
	@Override
	public TypeToken<CustomizableRecipeStorage> getFactoryOutputType()
	{
		return OUTPUT_TYPE;
	}

	@NotNull
	@Override
	public TypeToken<? extends IToken<?>> getFactoryInputType()
	{
		return INPUT_TYPE;
	}

	@Override
	public @NotNull CustomizableRecipeStorage getNewInstance(@NotNull IFactoryController factoryController, @NotNull IToken<?> input, @NotNull Object... context) throws IllegalArgumentException
	{
		var impl = (ICustomizedRecipeStorage) context[0];
		return new CustomizableRecipeStorage(input, impl);
	}

	@Override
	public @NotNull CompoundTag serialize(@NotNull IFactoryController controller, @NotNull CustomizableRecipeStorage output)
	{
		var tag = new CompoundTag();
		tag.put(IMPL_TAG, CustomizedRecipeStorageRegistry.INSTANCE.serialize(output.getImpl()));
		tag.put(TOKEN_TAG, StandardFactoryController.getInstance().serialize(output.getToken()));
		return tag;
	}

	@Override
	public @NotNull CustomizableRecipeStorage deserialize(@NotNull IFactoryController controller, @NotNull CompoundTag tag) throws Throwable
	{
		var impl = CustomizedRecipeStorageRegistry.INSTANCE.deserialize(tag.getCompound(IMPL_TAG));
		IToken<?> token = StandardFactoryController.getInstance().deserialize(tag.getCompound(TOKEN_TAG));
		return new CustomizableRecipeStorage(token, impl);
	}

	@Override
	public void serialize(@NotNull IFactoryController controller, @NotNull CustomizableRecipeStorage output, FriendlyByteBuf buffer)
	{
		buffer.writeNbt(CustomizedRecipeStorageRegistry.INSTANCE.serialize(output.getImpl()));
		controller.serialize(buffer, output.getToken());
	}

	@Override
	public @NotNull CustomizableRecipeStorage deserialize(@NotNull IFactoryController controller, @NotNull FriendlyByteBuf buffer) throws Throwable
	{
		var impl = CustomizedRecipeStorageRegistry.INSTANCE.deserialize(buffer.readNbt());
		IToken<?> token = controller.deserialize(buffer);
		return new CustomizableRecipeStorage(token, impl);
	}

	@Override
	public short getSerializationId()
	{
		return SerializationIds.CUSTOMIZABLE_RECIPE_STORAGE_ID;
	}

}
