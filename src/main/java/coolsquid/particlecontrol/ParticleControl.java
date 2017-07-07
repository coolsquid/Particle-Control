package coolsquid.particlecontrol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionsRowList;
import net.minecraft.client.gui.GuiOptionsRowList.Row;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

@Mod(modid = ParticleControl.MODID, name = ParticleControl.NAME, version = ParticleControl.VERSION, dependencies = ParticleControl.DEPENDENCIES, updateJSON = ParticleControl.UPDATE_JSON_URL)
public class ParticleControl {

	public static final String MODID = "particlecontrol";
	public static final String NAME = "Particle Control";
	public static final String VERSION = "1.0.0";
	public static final String DEPENDENCIES = "required-after:forge@[14.21.1.2387,)";
	public static final String UPDATE_JSON_URL = "https://coolsquid.me/api/version/particlecontrol.json";

	private static boolean[] particles;

	private static HashMap<Integer, IParticleFactory> originalFactories;
	private static Map<Integer, IParticleFactory> particleTypes;

	private static Configuration config;

	@Mod.EventHandler
	public void onPreInit(FMLPreInitializationEvent event) {
		config = new Configuration(event.getSuggestedConfigurationFile());
	}

	@Mod.EventHandler
	public void onInit(FMLInitializationEvent event) {
		particles = new boolean[EnumParticleTypes.values().length];
		originalFactories = new HashMap<>();
		particleTypes = ReflectionHelper.getPrivateValue(ParticleManager.class, Minecraft.getMinecraft().effectRenderer,
				6);

		for (EnumParticleTypes type : EnumParticleTypes.values()) {
			toggle(type.getParticleID(), config.getBoolean(type.getParticleName(), "particles", true,
					"Set to false to disable the particle."));
		}
		if (config.hasChanged()) {
			config.save();
		}

		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post event) {
		if (event.getGui() instanceof GuiVideoSettings) {
			GuiOptionsRowList optionsRowList = ReflectionHelper.getPrivateValue(GuiVideoSettings.class,
					(GuiVideoSettings) event.getGui(), 3);
			List<Row> options = ReflectionHelper.getPrivateValue(GuiOptionsRowList.class, optionsRowList, 0);
			options.add(new Row(new GuiButton(164, event.getGui().width / 2 - 155,
					event.getGui().height / 2 - 155 + 160, 310, 20, "Configure particles individually...") {

				@Override
				public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
					if (super.mousePressed(mc, mouseX, mouseY)) {
						Minecraft.getMinecraft().displayGuiScreen(new GuiParticles(event.getGui()));
						return true;
					}
					return false;
				}
			}, null));
		}
	}

	public static boolean isParticleEnabled(int id) {
		return particles[id];
	}

	static void toggle(int id, boolean toggle) {
		if (toggle) {
			Minecraft.getMinecraft().effectRenderer.registerParticle(id, originalFactories.remove(id));
		} else {
			originalFactories.put(id, particleTypes.get(id));
			Minecraft.getMinecraft().effectRenderer.registerParticle(id, (a, b, c, d, e, f, g, h, i) -> null);
		}
		particles[id] = toggle;
	}

	static void save() {
		for (int i = 0; i < particles.length; i++) {
			config.get("particles", EnumParticleTypes.values()[i].getParticleName(), true).setValue(particles[i]);
		}
		if (config.hasChanged()) {
			config.save();
		}
	}
}