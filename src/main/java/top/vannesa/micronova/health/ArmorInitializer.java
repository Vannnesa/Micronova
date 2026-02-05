package top.vannesa.micronova.health;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/**
 * Handles initialization and syncing of armor from Minecraft inventory to HealthComponent.
 * Called when a player joins or equips/removes armor.
 */
public class ArmorInitializer {

    /**
     * Scan player armor slots and populate the ArmorComponent.
     * Supports stacking armor (e.g., undershirt + chestplate + plate carrier).
     */
    public static void initializeArmorFromInventory(PlayerEntity player, HealthComponent health) {
        ArmorComponent armor = health.getArmorComponent();
        armor.resetArmor();

        // Scan all armor slots
        var armorItems = player.getArmorItems();
        for (ItemStack stack : armorItems) {
            if (stack.isEmpty()) continue;

            String armorType = getArmorTypeFromItem(stack.getItem());
            if (armorType.equals("none")) continue;

            String armorSlot = getArmorSlotFromPosition(armorItems);
            applyArmorToBodyParts(armor, armorSlot, armorType);
        }
    }

    /**
     * Apply a single armor piece to affected body parts.
     */
    private static void applyArmorToBodyParts(ArmorComponent armor, String slot, String type) {
        int armorClass = ArmorComponent.getArmorClassLevel(type);
        float baseValue = ArmorComponent.getBaseArmorValue(type);

        for (BodyPart part : BodyPart.values()) {
            if (ArmorComponent.isBodyPartProtectedBy(part, slot)) {
                // Stack armor if multiple pieces protect same part
                float current = armor.getArmor(part);
                float newValue = current + baseValue;
                armor.setArmor(part, newValue);
                armor.setArmorType(part, type);
            }
        }
    }

    /**
     * Determine armor slot name from item.
     */
    private static String getArmorSlotFromPosition(Iterable<ItemStack> items) {
        int index = 0;
        for (ItemStack stack : items) {
            switch (index) {
                case 0: return "boots";
                case 1: return "leggings";
                case 2: return "chestplate";
                case 3: return "helmet";
            }
            index++;
        }
        return "unknown";
    }

    /**
     * Map Minecraft item to armor type string.
     */
    private static String getArmorTypeFromItem(net.minecraft.item.Item item) {
        if (item == Items.LEATHER_HELMET || item == Items.LEATHER_CHESTPLATE ||
            item == Items.LEATHER_LEGGINGS || item == Items.LEATHER_BOOTS) {
            return "leather";
        } else if (item == Items.CHAINMAIL_HELMET || item == Items.CHAINMAIL_CHESTPLATE ||
                   item == Items.CHAINMAIL_LEGGINGS || item == Items.CHAINMAIL_BOOTS) {
            return "chainmail";
        } else if (item == Items.IRON_HELMET || item == Items.IRON_CHESTPLATE ||
                   item == Items.IRON_LEGGINGS || item == Items.IRON_BOOTS) {
            return "iron";
        } else if (item == Items.DIAMOND_HELMET || item == Items.DIAMOND_CHESTPLATE ||
                   item == Items.DIAMOND_LEGGINGS || item == Items.DIAMOND_BOOTS) {
            return "diamond";
        } else if (item == Items.NETHERITE_HELMET || item == Items.NETHERITE_CHESTPLATE ||
                   item == Items.NETHERITE_LEGGINGS || item == Items.NETHERITE_BOOTS) {
            return "netherite";
        }
        return "none";
    }
}
