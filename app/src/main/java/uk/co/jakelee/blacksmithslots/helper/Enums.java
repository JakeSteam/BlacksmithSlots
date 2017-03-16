package uk.co.jakelee.blacksmithslots.helper;

public class Enums {
    public enum Achievement {
        UnlockSlot1("achievementcode");
        public String value;
        Achievement(String value) {
            this.value = value;
        }
    }

    public enum DataType {
        Integer(1), Boolean(2), String(3), Long(4);
        public int value;
        DataType(int value) {
            this.value = value;
        }
        public static DataType get(int value) {
            for (DataType item : DataType.values()) {
                if (value == item.value) {
                    return item;
                }
            }
            return null;
        }
    }

    public enum Language {
        English(1), French(2), Russian(3);
        public int value;
        Language(int value) {
            this.value = value;
        }
        public static Language get(int value) {
            for (Language item : Language.values()) {
                if (value == item.value) {
                    return item;
                }
            }
            return null;
        }
    }

    public enum Setting {
        Music(1), Sound(2), AttemptLogin(3), AutosaveMinutes(4), OnlyActiveResources(5), Language(6), NotificationSounds(7), PeriodicBonusNotification(8);
        public int value;
        Setting(int value) {
            this.value = value;
        }
        public static Setting get(int value) {
            for (Setting item : Setting.values()) {
                if (value == item.value) {
                    return item;
                }
            }
            return null;
        }
    }

    public enum SettingGroup {
        Internal(0), Audio(1), Gameplay(2), Notifications(3);
        public int value;
        SettingGroup(int value) {
            this.value = value;
        }
        public static SettingGroup get(int value) {
            for (SettingGroup item : SettingGroup.values()) {
                if (value == item.value) {
                    return item;
                }
            }
            return null;
        }
    }

    public enum SlotType {
        Furnace(1), Weapon(2), Tool(3), Armour(4), Accessory(5), Misc(6);
        public int value;
        SlotType(int value) {
            this.value = value;
        }
        public static SlotType get(int value) {
            for (SlotType item : SlotType.values()) {
                if (value == item.value) {
                    return item;
                }
            }
            return null;
        }
    }

    public enum Statistic {
        Xp(1), Level(2), TotalSpins(3), QuestsCompleted(4), LastAutosave(5), ResourcesGambled(6), ResourcesWon(7), AdvertsWatched(8), PacksPurchased(9), CollectedBonuses(10), VipLevel(11), LastBonusClaimed(12);
        public int value;
        Statistic(int value) {
            this.value = value;
        }
        public static Statistic get(int value) {
            for (Statistic item : Statistic.values()) {
                if (value == item.value) {
                    return item;
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
        public static Tier get(int value) {
            for (Tier item : Tier.values()) {
                if (value == item.value) {
                    return item;
                }
            }
            return null;
        }
    }

    public enum Type {
        Wildcard (999), None(0), Ore(1), Bar(2), Dagger(3), Sword(4), Longsword(5), Bow(6), Halfshield(7), FullShield(8), Chainmail(9), Platebody(10), HalfHelmet(11), FullHelmet(12), Boots(13), Gloves(14), Pickaxe(15), Hatchet(16), FishingRod(17), Hammer(18);
        public int value;
        Type(int value) {
            this.value = value;
        }
        public static Type get(int value) {
            for (Type item : Type.values()) {
                if (value == item.value) {
                    return item;
                }
            }
            return null;
        }
    }
}
