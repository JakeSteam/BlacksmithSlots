package uk.co.jakelee.blacksmithslots.helper;

public class Enums {
    public enum Type {
        Furnace(1), Weapon(2), Tool(3), Armour(4), Accessory(5), Misc(6);
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
