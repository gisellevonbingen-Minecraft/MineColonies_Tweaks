package steve_gall.minecolonies_tweaks.core.common.network.message;

import java.util.ArrayList;
import java.util.List;

import com.minecolonies.api.research.IGlobalResearchTree;
import com.minecolonies.api.util.SoundUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import steve_gall.minecolonies_tweaks.api.common.building.BuildingPos;
import steve_gall.minecolonies_tweaks.api.common.requestsystem.CustomizableRequestable;
import steve_gall.minecolonies_tweaks.core.common.network.AbstractMessage;
import steve_gall.minecolonies_tweaks.core.common.research.ResearchCost;
import steve_gall.minecolonies_tweaks.core.common.research.ResearchCostResolver;

public class ResearchCostRequestMessage extends AbstractMessage
{
	private final BuildingPos buildingPos;
	private final ResourceLocation branch;
	private final ResourceLocation research;
	private final List<ItemStack> stacks;

	public ResearchCostRequestMessage(BuildingPos buildingPos, ResourceLocation branch, ResourceLocation research, List<ItemStack> stacks)
	{
		this.buildingPos = buildingPos;
		this.branch = branch;
		this.research = research;
		this.stacks = new ArrayList<>(stacks);
	}

	public ResearchCostRequestMessage(FriendlyByteBuf buffer)
	{
		super(buffer);

		this.buildingPos = new BuildingPos(buffer);
		this.branch = buffer.readResourceLocation();
		this.research = buffer.readResourceLocation();
		this.stacks = buffer.readList(FriendlyByteBuf::readItem);
	}

	@Override
	public void encode(FriendlyByteBuf buffer)
	{
		super.encode(buffer);

		this.buildingPos.serializeBuffer(buffer);
		buffer.writeResourceLocation(this.branch);
		buffer.writeResourceLocation(this.research);
		buffer.writeCollection(this.stacks, FriendlyByteBuf::writeItem);
	}

	@Override
	public void handle(NetworkEvent.Context context)
	{
		super.handle(context);

		var player = context.getSender();
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
		var request = new ResearchCost(this.branch, this.research, this.stacks, player.getUUID());
		building.createRequest(new CustomizableRequestable(request), true);
		SoundUtils.playSuccessSound(player, building.getPosition());
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

	public List<ItemStack> getStacks()
	{
		return this.stacks;
	}

}
