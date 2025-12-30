package top.vannesa.micronova;

import net.fabricmc.api.ModInitializer;
import top.vannesa.micronova.network.ModPackets;
import top.vannesa.micronova.command.DebugHealthCommand;

public class MicronovaMod implements ModInitializer {

	@Override
	public void onInitialize() {
		ModPackets.registerServer();
		DebugHealthCommand.register();
	}
}
