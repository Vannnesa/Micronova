package top.vannesa.micronova;

import net.fabricmc.api.ClientModInitializer;
import top.vannesa.micronova.network.ClientHealthReceiver;
import top.vannesa.micronova.network.ClientPackets;

public class MicronovaClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ClientHealthReceiver.register();
		ClientPackets.register();
		top.vannesa.micronova.client.PistolClient.register();
	}
}
