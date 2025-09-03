package steve_gall.minecolonies_tweaks.core.common.network.message;

import java.util.ArrayList;
import java.util.List;

import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.research.IGlobalResearchTree;
import com.minecolonies.api.util.SoundUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import steve_gall.minecolonies_tweaks.api.common.building.BuildingPos;
import steve_gall.minecolonies_tweaks.api.common.network.AbstractMessage;
import steve_gall.minecolonies_tweaks.api.common.requestsystem.CustomizableRequestable;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.core.common.research.ResearchCost;
import steve_gall.minecolonies_tweaks.core.common.research.ResearchCostResolver;
import steve_gall.minecolonies_tweaks.core.common.util.SerializationHelper;

public class ResearchCostRequestMessage extends AbstractMessage
{
	public static final CustomPacketPayload.Type<ResearchCostRequestMessage> TYPE = new CustomPacketPayload.Type<>(MineColoniesTweaks.rl("research_cost_request"));

	private final BuildingPos buildingPos;
	private final ResourceLocation branch;
	private final ResourceLocation research;
	private final List<ItemStorage> items;

	public ResearchCostRequestMessage(BuildingPos buildingPos, ResourceLocation branch, ResourceLocation research, List<ItemStorage> items)
	{
		this.buildingPos = buildingPos;
		this.branch = branch;
		this.research = research;
		this.items = new ArrayList<>(items);
	}

	public ResearchCostRequestMessage(RegistryFriendlyByteBuf buffer)
	{
		super(buffer);

		this.buildingPos = new BuildingPos(buffer);
		this.branch = buffer.readResourceLocation();
		this.research = buffer.readResourceLocation();
		this.items = buffer.readList(SerializationHelper::deserializer);
	}

	@Override
	public void encode(RegistryFriendlyByteBuf buffer)
	{
		super.encode(buffer);

		this.buildingPos.serializeBuffer(buffer);
		buffer.writeResourceLocation(this.branch);
		buffer.writeResourceLocation(this.research);
		buffer.writeCollection(this.items, SerializationHelper::serializer);
	}

	@Override
	public void handle(IPayloadContext context)
	{
		super.handle(context);

		var player = context.player();
		var building = this.buildingPos.getBuilding();

		if (!ResearchCostResolver.hasResolver(building))
		{
			player.sendSystemMessage(Component.literal("Resolver not exist").withStyle(ChatFormatting.GRAY));
			SoundUtils.playErrorSound(player, building.getPosition());
			return;
		}
		else if (ResearchCost.isRequested(building, this.branch, this.research))
		{
			player.sendSystemMessage(Component.translatable("minecolonies_tweaks.gui.already_requested").withStyle(ChatFormatting.GRAY));
			SoundUtils.playErrorSound(player, building.getPosition());
			return;
		}

		var tree = IGlobalResearchTree.getInstance();
		var branch = tree.getBranchData(this.branch);
		var research = branch != null ? tree.getResearch(this.branch, this.research) : null;

		if (research == null)
		{
			player.sendSystemMessage(Component.translatable("minecolonies_tweaks.gui.research_not_found", this.branch, this.research).withStyle(ChatFormatting.GRAY));
			SoundUtils.playErrorSound(player, building.getPosition());
			return;
		}

		player.sendSystemMessage(Component.translatable("minecolonies_tweaks.gui.research_cost_requested", MutableComponent.create(branch.getName()), MutableComponent.create(research.getName())).withStyle(ChatFormatting.GRAY));
		var request = new ResearchCost(this.branch, this.research, this.items, player.getUUID());
		building.createRequest(new CustomizableRequestable(request), true);
		SoundUtils.playSuccessSound(player, building.getPosition());
	}

	@Override
	public CustomPacketPayload.Type<ResearchCostRequestMessage> type()
	{
		return TYPE;
	}

	public BuildingPos getBuildingPos()
	{
		return this.buildingPos;
	}

	public ResourceLocation getBranch()
	{
		return this.branch;
	}

	public ResourceLocation getResearch()
	{
		return this.research;
	}

	public List<ItemStorage> getItems()
	{
		return this.items;
	}

}
