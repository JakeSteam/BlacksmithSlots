# Blacksmith Slots

This is the source code of [Blacksmith Slots](https://play.google.com/store/apps/details?id=uk.co.jakelee.blacksmithslots), feel free to do whatever you want with it!

## Screenshots

Game trailer: https://www.youtube.com/watch?v=d1J1LDp8vps

| In slot | Map | Idle game | Trophies | Stats |
| -- | -- | -- | -- | -- |
| ![image](https://user-images.githubusercontent.com/12380876/157328444-17df8157-6a9b-4bf5-b5ca-a6116d034d24.png) | ![image](https://user-images.githubusercontent.com/12380876/157328488-2244cc56-eb26-4f2c-8de4-59b430b33ec3.png) | ![image](https://user-images.githubusercontent.com/12380876/157328530-9d45ad8c-870c-4b47-a4e1-8a00d05c3dd4.png) | ![image](https://user-images.githubusercontent.com/12380876/157328557-a5fa88f0-423e-4eda-900b-fe00bca2c67d.png) | ![image](https://user-images.githubusercontent.com/12380876/157328623-6f0d2b03-9e1a-40d0-9c25-44e1c711e3f7.png) |





## Play Store description

The world has been infected by the mysterious Purple, can you spin the slots to craft the items needed to save the world?

Explore a wide variety of areas and collect all the items you need to defeat the Purple, and find out where it came from! You'll meet plenty of characters along the way, but not all of them will be friendly...

With over 100 free slots to spin and craft items at, there's plenty of fun to be had in this pixel crafting / adventuring hybrid! Minigames, free bonuses, and tons of slot variation means there's always plenty to do.

Features:

* No premium currency, no pay to win, no "Invite your friends", no forced adverts, no nonsense!
* 100+ slots, each with a slot owner, and differing rewards!
* 20+ areas, each with a storyline and unique slot background!
* 45+ characters!
* 150+ items, all with unique uses!
* Multi-stage crafting system!
* Minigames!
* Available in Chinese, Dutch, French, German, Korean, Portuguese, Russian, and Spanish!

Also:

* Tiny download! Just ~7MB, with no additional downloads!
* Super speedy startup: ~1-2 seconds on almost all phones!
* Hundreds of lines of dialog!
* Regular additions of player-suggested features & content!
* No internet required!
* Active Reddit community (/r/BlacksmithSlots)!
* 24 Statistics to track your progress!

Google Play:

* 30 Achievements
* 8 Leaderboards
* Automatic & manual Cloud Saves

Supported Devices:

* All Android versions from Ice Cream Sandwich to Nougat, and beyond.
* All phone & tablet sizes, from a tiny 3.7" Nexus One to a chubby 5.7" Nexus 6P, and beyond to the 10.1" Nexus 10!
* Google Play Services are optional.

Permissions:

* Billing: Used for in-app purchases.
* Internet, External Storage, Network State: Used to download and cache the appropriate quality adverts.
* External Files / Photos: Used to import Pixel Blacksmith save.

About Developer:

Blacksmith Slots is a spin-off from my first game "Pixel Blacksmith", and is created and maintained by Jake Lee, a software engineer from England. If you've encountered a bug, or have an idea for a new feature, please mention it in a review or on https://reddit.com/r/BlacksmithSlots and I'll reply ASAP. I appreciate all feedback!

Happy spinning, blacksmiths!

## Codebase notes
* `SlotHelper.java` contains all of the slot spinning / rewarding logic.
* `Enums.java` contains most of the game data definition, with `DatabaseHelper.java` storing the rest.
* It requires an older version of Android Studio to build, and likely a lot of changes.

## Licensing
* Entire repository is under the MIT license, essentially do whatever you want but don't blame me if it breaks!
* 377 images have been redacted due to being paid assets by [7soul](https://twitter.com/7souldesign).
* All uncensored images are modified versions of [Kenney](https://www.kenney.nl/assets?s=city) assets.
