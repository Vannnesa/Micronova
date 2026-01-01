package top.vannesa.micronova.weapon;

public final class WeaponConfig {
    public final String id;
    public final float baseDamage;
    public final float penetration;
    public final float bleedChance;
    public final int range;

    public WeaponConfig(String id, float baseDamage, float penetration, float bleedChance, int range) {
        this.id = id;
        this.baseDamage = baseDamage;
        this.penetration = penetration;
        this.bleedChance = bleedChance;
        this.range = range;
    }

    public static WeaponConfig pistol9mm() {
        return new WeaponConfig("pistol_9mm", 40f, 5f, 0.12f, 48);
    }
}
