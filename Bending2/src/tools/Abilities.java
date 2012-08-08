package tools;

import java.util.ArrayList;

public enum Abilities {

	AirBlast, AirBubble, AirShield, AirSuction, AirSwipe, Tornado, AirScooter, AirBurst, AirSpout,

	Catapult, RaiseEarth, EarthGrab, EarthTunnel, CompactColumn, EarthBlast, Collapse, Tremorsense, Shockwave, EarthArmor,

	ArcOfFire, Extinguish, Fireball, HeatMelt, RingOfFire, FireJet, Illumination, WallOfFire, FireBlast, Lightning,

	WaterBubble, FreezeMelt, HealingWaters, Plantbending, WalkOnWater, WaterManipulation, WaterSpout, WaterWall, Wave, Bloodbending, IceSpike,

	HighJump, RapidPunch,

	AvatarState;

	private enum AirbendingAbilities {
		AirBlast, AirBubble, AirShield, AirSuction, AirSwipe, Speed, Tornado, AirScooter, AirBurst, AirSpout;
	}

	private enum EarthbendingAbilities {
		Catapult, RaiseEarth, EarthGrab, EarthTunnel, CompactColumn, EarthBlast, Collapse, Tremorsense, Shockwave, EarthArmor;
	}

	private enum FirebendingAbilities {
		ArcOfFire, Extinguish, Fireball, HeatMelt, RingOfFire, FireJet, Illumination, WallOfFire, FireBlast, Lightning;
	}

	private enum WaterbendingAbilities {
		WaterBubble, FreezeMelt, HealingWaters, Plantbending, WalkOnWater, WaterManipulation, WaterSpout, WaterWall, Wave, Bloodbending, IceSpike;
	}

	private enum ChiBlockingAbilities {
		HighJump, RapidPunch;
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

	public static boolean isChiBlocking(Abilities ability) {
		for (ChiBlockingAbilities a : ChiBlockingAbilities.values()) {
			if (a.name().equalsIgnoreCase(ability.name()))
				return true;
		}
		return false;
	}

	public static String[] getChiBlockingAbilities() {
		ArrayList<String> list = new ArrayList<String>();
		for (Abilities a : Abilities.values()) {
			if (isChiBlocking(a)) {
				list.add(a.name());
			}
		}
		return list.toArray(new String[list.size()]);
	}

}