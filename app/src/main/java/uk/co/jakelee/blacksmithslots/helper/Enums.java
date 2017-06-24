package uk.co.jakelee.blacksmithslots.helper;

public class Enums {

    public enum DataType {
        Integer(1), Boolean(2), String(3), Long(4);
        public final int value;
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
        English(1), Chinese(2), Dutch(3), French(4), German(5), Korean(6), Portuguese(7), Russian(8), Spanish(9);
        public final int value;
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
        IronSecondary1000(19), IronSecondary5000(20),IronSecondary10000(21),
        SteelOre1000(22), SteelOre5000(23),SteelOre10000(24),
        SteelSecondary1000(25), SteelSecondary5000(26),SteelSecondary10000(27),
        MithrilOre1000(28), MithrilOre5000(29),MithrilOre10000(30),
        MithrilSecondary1000(31), MithrilSecondary5000(32),MithrilSecondary10000(33),
        GemRed(34), GemBlue(35), GemGreen(36), GemOrange(37), GemYellow(38);
        public final int value;
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
        public final int value;
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
        Home(1), Neighbourhood(2), Forest(3), Marketplace(4), Castle(5),
        Mines(6), DeepMines(7), RuinedVillage(8), HauntedPort(9), Expanse(10),
        Isolates(11), Camp(12), UselessRiches(13), Mercenaria(14), Library(15),
        HauntedCorridors(16), UndeadJewellers(17), NoTurningBack(18), Battle(19),
        TheEnd(20);
        public final int value;
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
        Mom(1), LowLevelBlacksmith(2), Woman(3), Man(4), Mouse(5), Snail(6),
        Frog(7), Caveman(8), Mouse2(9), Guard(10), PurpleBlob(11), Worm(12),
        GroupOfPeople(13), MineWorker(14), MidLevelBlacksmith(15), OldMan(16),
        Fisherman(17), Sailor(18), PillarGuardian(19), RedNinja(20), BlueNinja(21),
        YellowNinja(22), GreenNinja(23), Chef(24), Mercenary(25), Jeweller(26),
        RedBook(27), YellowBook(28), GreenBook(29), BlueBook(30), Robot(31),
        Skeleton(32), Skeleton2(33), Ghost(34), Ghost2(35), HighLevelBlacksmith(36),
        BronzeAdventurer(37), IronAdventurer(38), SteelAdventurer(39), MithrilAdventurer(40),
        Boss(41), PixelBlacksmith(42), JewellerRed(43), JewellerOrange(44), JewellerGreen(45), JewellerBlue(46);
        public final int value;
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
        Music(1), Sound(2), AttemptLogin(3), OnlyActiveResources(5), Language(6), NotificationSounds(7), PeriodicBonusNotification(8), SaveImported(9),
        OnlyShowStocked(10), OrderByTier(11), OrderReversed(12), BlacksmithPassNotification(13), PlayLogout(14), Autosave(15), Orientation(16), Prestige(17);
        public final int value;
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
        Internal(0), Audio(1), Gameplay(2), Notifications(3), GooglePlay(4), Saves(5), Misc(6);
        public final int value;
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
        Map1Mom(1),
        Map2Furnace(2), Map2Accessories(3), Map2Tools(4), Map2Weapons(5), Map2Armour(6),
        Map3Mouse(7), Map3Snail(8), Map3Human(9), Map3Frog(10), Map3Mouse2(11),
        Map4Fruit(12), Map4Fruit2(13), Map4Fruit3(14), Map4Purple(15), Map4Guard(16),
        Map5Gate(17), Map5Vault(18), Map5Worm(19), Map5Study(20),
        Map6Exit(21), Map6Gems(22), Map6Elitist(23), Map6Armoury(24), Map6Smeltery(25),
        Map7Nook(26), Map7Gateman(27), Map7Weapon(28), Map7Interchange(29),
        Map8Exit(30), Map8Resident(31), Map8Bronzed(32), Map8Exotic(33), Map8Lifer(34), Map8Store(35),
        Map9Left(36), Map9Right(37), Map9Fish(38), Map9Tool(39), Map9Captain(40), Map9Amour(41), Map9Charon(42),
        Map10Stocked(43), Map10Pale(44), Map10Old(45), Map10Endless(46),
        Map11Contact(47), Map11Red(48), Map11Blue(49), Map11Yellow(50), Map11Green(51),
        Map12Rupert(52), Map12Ellen(53), Map12Daniel(54), Map12Pete(55), Map12Lucy(56), Map12Chef(57),
        Map13Deranged(58), Map13Miner(59), Map13Distorted(60), Map13Sailor(61), Map13Kitchen(62),
        Map14Frankie(63), Map14Bobbie(64), Map14Danny(65), Map14Jimmy(66), Map14BigTony(67),
        Map15Red(68), Map15Yellow(69), Map15Green(70), Map15Blue(71), Map15Robot(72),
        Map16Furnace(73), Map16Accessories(74), Map16Tools(75), Map16Weapons(76), Map16Armour(77),
        Map17Blue(78), Map17Yellow(79), Map17Orange(80), Map17Green(81), Map17Red(82),
        Map18Watchers(83), Map18Guard(84), Map18Bronze(85), Map18Iron(86), Map18Steel(87), Map18Mithril(88), Map18Purple(89),
        Map19Touch(90), Map19Travel(91), Map19Hunger(92), Map19Defence(93), Map19Melee(94), Map19Archery(95), Map19Protection(96), Map19Power(97), Map19Boss(98),
        Map20PixelBlacksmith(99), Map20TradesEntrance(100);



        public final int value;
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
        TotalPassDaysClaimed(18), MinigameDice(19), MinigameChest(20), MinigameFlip(21), TrophiesEarned(22), MinigameHigher(23), Prestiges(24), MinigameMemoryLastClaim(25), MinigameMemory(26);
        public final int value;
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

    public enum StatisticType {
        Progress(1), Events(2), Bonuses(3), BlacksmithPass(4), Misc(5), Minigames(6), Version(7);
        public final int value;
        StatisticType(int value) {
            this.value = value;
        }
        public static StatisticType get(int value) {
            for (StatisticType item : StatisticType.values()) {
                if (value == item.value) {
                    return item;
                }
            }
            return null;
        }
    }

    public enum Tier {
        Internal(999), None(0),
        Bronze(1), Iron(2), Steel(3), Mithril(4), Adamant(5), Silver(6), Gold(7),
        PartialFood(10);
        public final int value;
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
        None(0), Ore(1), Bar(2), Secondary(19), LuckyCoin(20),
        Dagger(3), Sword(4), Longsword(5), Bow(6), HalfShield(7), FullShield(8), Chainmail(9), Platebody(10), HalfHelmet(11), FullHelmet(12), Boots(13), Gloves(14), Pickaxe(15), Hatchet(16), FishingRod(17), Hammer(18),
        Apple(21), Lime(22), Orange(23), Peach(24), Pineapple(25), Banana(26), Cherry(27), Watermelon(28), Grapes(29), Steak(30), Potato(31), Egg(32), Fish(33), ForbiddenFood(34),
        GemYellow(35), GemOrange(36), GemGreen(37), GemBlue(38), GemRed(39), GemPurple(40),
        SandRed(41), SandBlue(42), SandYellow(43), SandGreen(44),
        BookRed(45), BookYellow(46), BookGreen(47), BookBlue(48), BookPink(49), BookBrown(50), BookBlack(51), BookGrey(52), BookCollection(53),
        Wildcard (999), MinigameFlip(998), MinigameChest(997), MinigameDice(996), MinigameHigher(995);
        public final int value;
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
