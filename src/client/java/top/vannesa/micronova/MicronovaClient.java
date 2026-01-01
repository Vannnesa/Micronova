package top.vannesa.micronova;

import net.fabricmc.api.ClientModInitializer;
import top.vannesa.micronova.network.ClientHealthReceiver;
import top.vannesa.micronova.network.ClientPackets;
import top.vannesa.micronova.ui.HealthHudRenderer;

public class MicronovaClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ClientHealthReceiver.register();
		HealthHudRenderer.register();
		ClientPackets.register();
	}
}
