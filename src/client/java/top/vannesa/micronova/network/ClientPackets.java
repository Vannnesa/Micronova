package top.vannesa.micronova.network;

public final class ClientPackets {

    public static void register() {
        ClientHealthReceiver.register();
    }

    private ClientPackets() {}
}
