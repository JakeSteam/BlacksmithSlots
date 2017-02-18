package uk.co.jakelee.blacksmithslots.helper;

public class Enums {
    public enum Type {
        Weapon(1), Tool(2), Armour(3), Accessory(4), Misc(5), Crafting(6);
        private int type;
        Type(int type) {
            this.type = type;
        }
    }

    public enum Tier {
        Bronze(1), Iron(2), Steel(3), Mithril(4);
        private int tier;
        Tier(int tier) {
            this.tier = tier;
        }
    }
}
