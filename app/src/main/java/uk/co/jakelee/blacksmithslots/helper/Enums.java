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

    public enum Iap {
        BlacksmithPass(1),
        VipLevel1(2), VipLevel2(3), VipLevel3(4), VipLevel4(5), VipLevel5(6), VipLevel6(7),
        BronzeOre1000(10), BronzeOre5000(11),BronzeOre10000(12),
        BronzeSecondary1000(13), BronzeSecondary5000(14),BronzeSecondary10000(15),
        IronOre1000(16), IronOre5000(17),IronOre10000(18),
        IronSecondary1000(19), IronSecondary5000(20),IronSecondary10000(21);
        public int value;
        Iap(int value) {
            this.value = value;
        }
        public static Iap get(int value) {
            for (Iap item : Iap.values()) {
                if (value == item.value) {
                    return item;
                }
            }
            return null;
        }
    }

    public enum ItemBundleType {
        SlotResource(1), SlotReward(2), IapReward(3), PassReward(4);
        public int value;
        ItemBundleType(int value) {
            this.value = value;
        }
        public static ItemBundleType get(int value) {
            for (ItemBundleType item : ItemBundleType.values()) {
                if (value == item.value) {
                    return item;
                }
            }
            return null;
        }
    }

    public enum Map {
        Home(1), Neighbourhood(2), Forest(3);
        public int value;
        Map(int value) {
            this.value = value;
        }
        public static Map get(int value) {
            for (Map item : Map.values()) {
                if (value == item.value) {
                    return item;
                }
            }
            return null;
        }
    }

    public enum Person {
        Map1Mum(1), Map2Blacksmith(2);
        public int value;
        Person(int value) {
            this.value = value;
        }
        public static Person get(int value) {
            for (Person item : Person.values()) {
                if (value == item.value) {
                    return item;
                }
            }
            return null;
        }
    }


    public enum Setting {
        Music(1), Sound(2), AttemptLogin(3), AutosaveMinutes(4), OnlyActiveResources(5), Language(6), NotificationSounds(7), PeriodicBonusNotification(8), SaveImported(9),
        OnlyShowStocked(10), OrderByTier(11), OrderReversed(12), BlacksmithPassNotification(13);
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

    public enum Slot {
        Map1LuckyCoin(1),
        Map2Furnace(2), Map2Accessories(3), Map2Tools(4), Map2Weapons(5), Map2Armour(6),
        Map3Secondary(7);
        public int value;
        Slot(int value) {
            this.value = value;
        }
        public static Slot get(int value) {
            for (Slot item : Slot.values()) {
                if (value == item.value) {
                    return item;
                }
            }
            return null;
        }
    }

    public enum Statistic {
        Xp(1), Level(2), TotalSpins(3), QuestsCompleted(4), LastAutosave(5), ResourcesGambled(6), ResourcesWon(7), AdvertsWatched(8), PacksPurchased(9),
        CollectedBonuses(10), VipLevel(11), LastBonusClaimed(12), SaveImported(13), LastAdvertWatched(14), CurrentPassClaimedDay(15), HighestPassClaimedDay(16), ExtraPassMonths(17),
        TotalPassDaysClaimed(17);
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
        None(0), Ore(1), Bar(2), Dagger(3), Sword(4), Longsword(5), Bow(6), HalfShield(7), FullShield(8), Chainmail(9), Platebody(10), HalfHelmet(11), FullHelmet(12), Boots(13), Gloves(14), Pickaxe(15), Hatchet(16), FishingRod(17), Hammer(18), Secondary(19),
        LuckyCoin(20),
        Wildcard (999), MinigameFlip(998);
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
