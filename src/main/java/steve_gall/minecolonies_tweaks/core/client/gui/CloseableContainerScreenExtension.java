package steve_gall.minecolonies_tweaks.core.client.gui;

public interface CloseableContainerScreenExtension extends CloseableWindowExtension
{
	void minecolonies_tweaks$onInit(int leftPos, int topPos, int imageWidth, int imageHeight, addCloseButton addCloseButton);

	@FunctionalInterface
	public interface addCloseButton
	{
		void invoke(int x, int y, int width, int height);
	}

}
