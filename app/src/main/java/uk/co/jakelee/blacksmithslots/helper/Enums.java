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
        Bronze(1), Iron(2), Steel(3), Mithril(4);
        private int tier;
        Tier(int tier) {
            this.tier = tier;
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
