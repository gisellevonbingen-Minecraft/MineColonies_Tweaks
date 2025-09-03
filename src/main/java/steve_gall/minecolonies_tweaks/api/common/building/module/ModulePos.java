package steve_gall.minecolonies_tweaks.api.common.building.module;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.minecolonies.api.colony.buildings.modules.IBuildingModule;
import com.minecolonies.api.colony.buildings.modules.IBuildingModuleView;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import steve_gall.minecolonies_tweaks.api.common.building.BuildingPos;

public class ModulePos
{
	public static final String TAG_BUILDING_POS = "buildingPos";
	public static final String TAG_MODULE_NAME = "moduleName";

	public static Codec<ModulePos> CODEC = RecordCodecBuilder.create(builder -> builder.group(//
			BuildingPos.CODEC.fieldOf("buildingPos").forGetter(ModulePos::getBuildingPos), //
			Codec.STRING.fieldOf("moduleName").forGetter(ModulePos::getModuleName) //
	).apply(builder, ModulePos::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ModulePos> STREAM_CODEC = StreamCodec.composite(//
			BuildingPos.STREAM_CODEC, ModulePos::getBuildingPos, //
			ByteBufCodecs.STRING_UTF8, ModulePos::getModuleName, //
			ModulePos::new);

	@NotNull
	private final BuildingPos buildingPos;
	private final String moduleName;

	public ModulePos(@NotNull CompoundTag tag)
	{
		this.buildingPos = new BuildingPos(tag.getCompound(TAG_BUILDING_POS));
		this.moduleName = tag.getString(TAG_MODULE_NAME);
	}

	public ModulePos(@NotNull FriendlyByteBuf buffer)
	{
		this.buildingPos = new BuildingPos(buffer);
		this.moduleName = buffer.readUtf();
	}

	public ModulePos(@NotNull BuildingPos buildingPos, @NotNull String moduleName)
	{
		this.buildingPos = buildingPos;
		this.moduleName = moduleName;
	}

	public ModulePos(@NotNull IBuildingModule module)
	{
		this(new BuildingPos(module.getBuilding()), module.getProducer().key);
	}

	public ModulePos(@NotNull IBuildingModuleView moduleView)
	{
		this(new BuildingPos(moduleView.getBuildingView()), moduleView.getProducer().key);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.buildingPos, this.moduleName);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		else if (obj instanceof ModulePos other)
		{
			return this.buildingPos.equals(other.buildingPos) && this.moduleName.equals(other.moduleName);
		}
		else
		{
			return false;
		}

	}

	@SuppressWarnings("unchecked")
	@Nullable
	public IBuildingModule getModule()
	{
		var building = this.getBuildingPos().getBuilding();

		if (building == null)
		{
			return null;
		}

		var producer = BuildingEntry.getProducer(this.moduleName);

		if (producer == null)
		{
			return null;
		}

		return building.getModule(producer);
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public IBuildingModuleView getModuleView()
	{
		var building = this.getBuildingPos().getBuildingView();

		if (building == null)
		{
			return null;
		}

		var producer = BuildingEntry.getProducer(this.moduleName);

		if (producer == null)
		{
			return null;
		}

		return building.getModuleView(producer);
	}

	@NotNull
	public CompoundTag serializeNBT()
	{
		var tag = new CompoundTag();
		tag.put(TAG_BUILDING_POS, this.buildingPos.serializeNBT());
		tag.putString(TAG_MODULE_NAME, this.moduleName);

		return tag;
	}

	public void serializeBuffer(@NotNull FriendlyByteBuf buffer)
	{
		this.buildingPos.serializeBuffer(buffer);
		buffer.writeUtf(this.moduleName);
	}

	@NotNull
	public BuildingPos getBuildingPos()
	{
		return this.buildingPos;
	}

	@NotNull
	public ResourceKey<Level> getDimensionId()
	{
		return this.buildingPos.getDimensionId();
	}

	public int getColonyId()
	{
		return this.buildingPos.getColonyId();
	}

	@NotNull
	public BlockPos getBuildingId()
	{
		return this.buildingPos.getBuildingId();
	}

	@NotNull
	public String getModuleName()
	{
		return this.moduleName;
	}

	public int getX()
	{
		return this.getBuildingId().getX();
	}

	public int getY()
	{
		return this.getBuildingId().getY();
	}

	public int getZ()
	{
		return this.getBuildingId().getZ();
	}

}
