package steve_gall.minecolonies_tweaks.api.common.research;

import org.jetbrains.annotations.NotNull;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.research.IResearchEffect;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

/**
 * {@link MinecraftForge#EVENT_BUS}
 */
public class ResearchEffectChangedEventArgs extends Event
{
	@NotNull
	private final IColony colony;
	@NotNull
	private final IResearchEffect effect;
	private final double prev;
	private final double next;

	public ResearchEffectChangedEventArgs(@NotNull IColony colony, @NotNull IResearchEffect effect, double prev, double next)
	{
		this.colony = colony;
		this.effect = effect;
		this.prev = prev;
		this.next = next;
	}

	@NotNull
	public IColony getColony()
	{
		return this.colony;
	}

	@NotNull
	public IResearchEffect getEffect()
	{
		return this.effect;
	}

	public double getPrev()
	{
		return this.prev;
	}

	public double getNext()
	{
		return this.next;
	}

}
