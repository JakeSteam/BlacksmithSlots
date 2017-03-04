package uk.co.jakelee.blacksmithslots.helper;

public class Enums {
    public enum Event {
        Spin(1);
        private int value;
        Event(int value) {
            this.value = value;
        }
    }

    public enum RequirementType {
        Resource(1), Event(2);
        private int value;
        RequirementType(int value) {
            this.value = value;
        }
    }

    public enum SlotType {
        Furnace(1), Weapon(2), Tool(3), Armour(4), Accessory(5), Misc(6);
        private int value;
        SlotType(int value) {
            this.value = value;
        }
    }

    public enum Statistic {
        Xp(1), Level(2), TotalSpins(3);
        public int value;
        Statistic(int value) {
            this.value = value;
        }
    }

    public enum Tier {
        Internal(999), None(0), Bronze(1), Iron(2), Steel(3), Mithril(4);
        public int value;
        Tier(int value) {
            this.value = value;
        }
    }

    public enum Type {
        Wildcard (999), None(0), Ore(1), Bar(2), Dagger(3), Sword(4), Longsword(5), Bow(6), Halfshield(7), Fullshield(8), Chainmail(9), Platebody(10), HalfHelmet(11), FullHelmet(12), Boots(13), Gloves(14), Pickaxe(15), Hatchet(16), FishingRod(17), Hammer(18);
        public int value;
        Type(int value) {
            this.value = value;
        }
    }
}
