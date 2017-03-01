package uk.co.jakelee.blacksmithslots.helper;

public class Enums {
    public enum Event {
        Spin(1);
        private int event;
        Event(int event) {
            this.event = event;
        }
    }

    public enum SlotType {
        Furnace(1), Weapon(2), Tool(3), Armour(4), Accessory(5), Misc(6);
        private int slotType;
        SlotType(int slotType) {
            this.slotType = slotType;
        }
    }

    public enum Tier {
        Internal(-1), None(0), Bronze(1), Iron(2), Steel(3), Mithril(4);
        private int tier;
        Tier(int tier) {
            this.tier = tier;
        }
    }

    public enum Type {
        Wildcard (-1), None(0), Ore(1), Bar(2), Dagger(3), Sword(4), Longsword(5), Bow(6), Halfshield(7), Fullshield(8), Chainmail(9), Platebody(10), HalfHelmet(11), FullHelmet(12), Boots(13), Gloves(14), Pickaxe(15), Hatchet(16), FishingRod(17), Hammer(18);
        private int type;
        Type(int type) {
            this.type = type;
        }
    }

    public enum RequirementType {
        Resource(1), Event(2);
        private int requirementType;
        RequirementType(int requirementType) {
            this.requirementType = requirementType;
        }
    }
}
