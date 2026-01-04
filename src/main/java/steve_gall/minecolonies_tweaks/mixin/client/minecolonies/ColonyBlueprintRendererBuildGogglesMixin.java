package steve_gall.minecolonies_tweaks.mixin.client.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minecolonies.api.items.ModItems;
import com.minecolonies.core.client.render.worldevent.WorldEventContext;

import net.neoforged.fml.ModList;
import steve_gall.minecolonies_tweaks.core.common.CuriosCompat;

@Mixin(targets = "com.minecolonies.core.client.render.worldevent.ColonyBlueprintRenderer$BuildGoggles", remap = false)
public abstract class ColonyBlueprintRendererBuildGogglesMixin
{
	@Inject(method = "isEnabled", remap = false, at = @At("RETURN"), cancellable = true)
	public void isEnabled(WorldEventContext ctx, CallbackInfoReturnable<Boolean> cir)
	{
		if (!cir.getReturnValueZ() && ModList.get().isLoaded(CuriosCompat.MOD_ID))
		{
			var goggles = CuriosCompat.findFirstCurio(ctx.clientPlayer, is -> is.is(ModItems.buildGoggles));

			if (!goggles.isEmpty())
			{
				cir.setReturnValue(true);
			}

		}

	}

}
