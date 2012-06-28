package tools;

import java.util.ArrayList;

public enum Abilities {

	AirBlast, AirBubble, AirShield, AirSuction, AirSwipe, Tornado,

	Catapult, EarthColumn, EarthGrab, EarthTunnel, EarthWall, CompactColumn, EarthBlast, PatchTheEarth,

	ArcOfFire, Extinguish, Fireball, FireStream, HeatMelt, RingOfFire, FireJet,

	WaterBubble, Freeze, HealingWaters, Melt, Plantbending, WalkOnWater, WaterManipulation, WaterSpout, WaterWall, Wave,

	AvatarState;

	private enum AirbendingAbilities {
		AirBlast, AirBubble, AirShield, AirSuction, AirSwipe, Speed, Tornado;
	}

	private enum EarthbendingAbilities {
		Catapult, DirtToSand, EarthColumn, EarthGlide, EarthGrab, EarthTunnel, EarthWall, CompactColumn, EarthBlast, RockShield, PatchTheEarth;
	}

	private enum FirebendingAbilities {
		ArcOfFire, Extinguish, Fireball, FireStream, HeatMelt, RingOfFire, FireJet;
	}

	private enum WaterbendingAbilities {
		WaterBubble, Freeze, FreezeFeet, HealingWaters, Melt, Plantbending, WalkOnWater, WaterManipulation, WaterShield, WaterSpout, WaterWall, Wave;
	}

	public static Abilities getAbility(String ability) {
		for (Abilities a : Abilities.values()) {
			if (ability.equalsIgnoreCase(a.name())) {
				return a;
			}
		}
		return null;
	}

	public static boolean isAirbending(Abilities ability) {
		for (AirbendingAbilities a : AirbendingAbilities.values()) {
			if (a.name().equalsIgnoreCase(ability.name()))
				return true;
		}
		return false;
	}

	public static String[] getAirbendingAbilities() {
		ArrayList<String> list = new ArrayList<String>();
		for (Abilities a : Abilities.values()) {
			if (isAirbending(a)) {
				list.add(a.name());
			}
		}
		return list.toArray(new String[list.size()]);
	}

	public static boolean isWaterbending(Abilities ability) {
		for (WaterbendingAbilities a : WaterbendingAbilities.values()) {
			if (a.name().equalsIgnoreCase(ability.name()))
				return true;
		}
		return false;
	}

	public static String[] getWaterbendingAbilities() {
		ArrayList<String> list = new ArrayList<String>();
		for (Abilities a : Abilities.values()) {
			if (isWaterbending(a)) {
				list.add(a.name());
			}
		}
		return list.toArray(new String[list.size()]);
	}

	public static boolean isEarthbending(Abilities ability) {
		for (EarthbendingAbilities a : EarthbendingAbilities.values()) {
			if (a.name().equalsIgnoreCase(ability.name()))
				return true;
		}
		return false;
	}

	public static String[] getEarthbendingAbilities() {
		ArrayList<String> list = new ArrayList<String>();
		for (Abilities a : Abilities.values()) {
			if (isEarthbending(a)) {
				list.add(a.name());
			}
		}
		return list.toArray(new String[list.size()]);
	}

	public static boolean isFirebending(Abilities ability) {
		for (FirebendingAbilities a : FirebendingAbilities.values()) {
			if (a.name().equalsIgnoreCase(ability.name()))
				return true;
		}
		return false;
	}

	public static String[] getFirebendingAbilities() {
		ArrayList<String> list = new ArrayList<String>();
		for (Abilities a : Abilities.values()) {
			if (isFirebending(a)) {
				list.add(a.name());
			}
		}
		return list.toArray(new String[list.size()]);
	}

}
