package uk.co.jakelee.blacksmithslots.helper;

public class Enums {
    public enum Achievement {
        UnlockSlot1("achievementcode");
        public String value;
        Achievement(String value) {
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

    public enum Setting {
        Music(1), Sound(2), AttemptLogin(3), AutosaveMinutes(4);
        private int value;
        Setting(int value) {
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
        Xp(1), Level(2), TotalSpins(3), QuestsCompleted(4), LastAutosave(5), ResourcesGambled(6), ResourcesWon(7), AdvertsWatched(8), PacksPurchased(9), CollectedBonuses(10), VipLevel(11);
        public int value;
        Statistic(int value) {
            this.value = value;
        }
        public static Statistic get(int code) {
            for (Statistic statistic : Statistic.values()) {
                if (code == statistic.value) {
                    return statistic;
                }
            }
            return null;
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
        Wildcard (999), None(0), Ore(1), Bar(2), Dagger(3), Sword(4), Longsword(5), Bow(6), Halfshield(7), FullShield(8), Chainmail(9), Platebody(10), HalfHelmet(11), FullHelmet(12), Boots(13), Gloves(14), Pickaxe(15), Hatchet(16), FishingRod(17), Hammer(18);
        public int value;
        Type(int value) {
            this.value = value;
        }
    }
}
