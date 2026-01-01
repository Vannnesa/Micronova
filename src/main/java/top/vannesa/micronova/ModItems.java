package top.vannesa.micronova;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import top.vannesa.micronova.weapon.WeaponConfig;

public final class ModItems {
    public static Item PISTOL_9MM;

    public static void register() {
        PISTOL_9MM = Registry.register(Registries.ITEM, new Identifier("micronova", "pistol_9mm"), new top.vannesa.micronova.item.PistolItem(new Item.Settings()));
    }

    private ModItems() {}
}
