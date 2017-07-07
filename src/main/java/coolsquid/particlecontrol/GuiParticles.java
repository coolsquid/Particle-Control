package coolsquid.particlecontrol;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiPageButtonList.GuiButtonEntry;
import net.minecraft.client.gui.GuiPageButtonList.GuiListEntry;
import net.minecraft.client.gui.GuiPageButtonList.GuiResponder;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumParticleTypes;

public class GuiParticles extends GuiScreen {

	public class Responder implements GuiResponder {

		@Override
		public void setEntryValue(int id, boolean value) {
			ParticleControl.toggle(id, value);
		}

		@Override
		public void setEntryValue(int id, float value) {

		}

		@Override
		public void setEntryValue(int id, String value) {

		}
	}

	private final GuiScreen parent;
	private GuiPageButtonList list;

	public GuiParticles(GuiScreen parent) {
		this.mc = Minecraft.getMinecraft();
		this.parent = parent;
	}

	@Override
	public void initGui() {
		super.initGui();
		this.buttonList.clear();
		this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height - 27, I18n.format("gui.done")));
		GuiListEntry[] entries = new GuiListEntry[EnumParticleTypes.values().length];
		for (int i = 0; i < entries.length; i++) {
			entries[i] = new GuiButtonEntry(i, EnumParticleTypes.values()[i].getParticleName(), true,
					ParticleControl.isParticleEnabled(i));
		}
		this.list = new GuiPageButtonList(this.mc, this.width, this.height, 32, this.height - 32, 25, new Responder(),
				new GuiListEntry[][] { entries });
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		this.list.handleMouseInput();
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.enabled && button.id == 200) {
			this.mc.displayGuiScreen(this.parent);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		this.list.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		this.list.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.list.drawScreen(mouseX, mouseY, partialTicks);
		this.drawCenteredString(this.fontRenderer, "Particles", this.width / 2, 5, 16777215);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		ParticleControl.save();
	}
}