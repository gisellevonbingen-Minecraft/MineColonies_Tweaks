package steve_gall.minecolonies_tweaks.core.client.view;

import com.ldtteam.blockui.Loader;
import com.ldtteam.blockui.Pane;
import com.ldtteam.blockui.PaneParams;

public class Addition extends Pane
{
	public Addition()
	{
		this.width = 1;
		this.height = 1;
	}

	public Addition(PaneParams params)
	{
		this.width = 1;
		this.height = 1;
	}

	@Override
	public void parseChildren(PaneParams params)
	{
		var parent = this.getParent();

		for (var node : params.getChildren())
		{
			Loader.createFromPaneParams(node, parent);
		}

	}

}
