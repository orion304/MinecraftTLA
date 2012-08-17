package tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigManager {

	public static boolean enabled = true;
	public static boolean bendToItem = false;
	public static boolean colors = true;
	public static boolean compatibility = false;

	public static Map<String, String> prefixes = new HashMap<String, String>();
	public static Map<String, String> color = new HashMap<String, String>();
	public static List<String> earthbendable = new ArrayList<String>();
	public static Map<String, Boolean> useWeapon = new HashMap<String, Boolean>();
	public static boolean useMySQL = false;
	public static String dbHost = "localhost";
	public static String dbUser = "root";
	public static String dbPass = "";
	public static String dbDB = "minecraft";
	public static String dbPort = "3306";

	public static int seaLevel = 62;

	public static long globalCooldown = 500;

	public static long chiblockduration = 2500;
	public static double dodgechance = 25;
	public static double punchdamage = 3;
	public static double falldamagereduction = 50;

	public static boolean reverseearthbending = true;
	public static boolean safeRevert = true;
	public static long revertchecktime = 300000;

	public static boolean useTagAPI = true;

	private static List<String> defaultearthbendable = new ArrayList<String>();

	// Air
	// AirBlast
	public static double airBlastSpeed = 25;
	public static double airBlastRange = 20;
	public static double airBlastRadius = 2;
	public static double airBlastPush = 1.2;

	// AirBubble
	public static int airBubbleRadius = 7;

	// AirPassive
	public static float airPassiveFactor = 0.3F;

	// AirScooter
	public static double airScooterSpeed = .675;

	// AirShield
	public static double airShieldRadius = 7;

	// AirSpout
	public static double airSpoutHeight = 20;

	// AirSuction
	public static double airSuctionSpeed = 25;
	public static double airSuctionRange = 20;
	public static double airSuctionRadius = 2;
	public static double airSuctionPush = 1;

	// AirSwipe
	public static int airdmg = 2;
	public static double airSwipeRange = 16;
	public static int airSwipeArc = 20;
	public static double airSwipeSpeed = 25;
	public static double airSwipeRadius = 2;
	public static double airSwipePush = 1;
	public static long airSwipeCooldown = 1000;

	// Tornado
	public static double tornadoRadius = 10;
	public static double tornadoHeight = 25;
	public static double tornadoRange = 25;
	public static double tornadoMobPush = 1;
	public static double tornadoPlayerPush = 1;

	// ChiBlocker
	// HighJump
	public static int jumpheight = 1;
	public static long highJumpCooldown = 10000;

	// RapidPunch
	public static int rapidPunchDamage = 1;
	public static int rapidPunchDistance = 4;
	public static long rapidPunchCooldown = 15000;
	public static int rapidPunchPunches = 5;

	// Earth
	// Catapult
	public static int catapultLength = 7;
	public static double catapultSpeed = 12;
	public static double catapultPush = 5;

	// Collapse
	public static int collapseRange = 20;
	public static double collapseRadius = 7;

	// CompactColumn
	public static double compactColumnRange = 20;
	public static double compactColumnSpeed = 8;

	// EarthArmor
	public static long eartharmorduration = 30000;
	public static int eartharmorstrength = 2;
	public static long eartharmorcooldown = 150000;

	// EarthBlast
	public static int earthdmg = 7;
	public static boolean earthBlastHitSelf = false;
	public static double earthBlastPrepareRange = 7;
	public static double earthBlastRange = 20;
	public static double earthBlastSpeed = 35;
	public static boolean earthBlastRevert = true;

	// EarthColumn
	public static int earthColumnHeight = 6;

	// EarthGrab
	public static double earthGrabRange = 15;

	// EarthPassive
	public static long earthPassive = 3000;

	// EarthTunnel
	public static double earthTunnelMaxRadius = 1;
	public static double earthTunnelRange = 10;
	public static double earthTunnelRadius = 0.25;
	public static long earthTunnelInterval = 30;
	public static boolean earthTunnelRevert = true;

	// EarthWall
	public static int earthWallRange = 15;
	public static int earthWallHeight = 8;
	public static int earthWallWidth = 6;

	// Tremorsense
	public static long tremorsenseCooldown = 3000;
	public static int tremorsenseMaxDepth = 10;
	public static int tremorsenseRadius = 5;
	public static byte tremorsenseLightThreshold = 7;

	// Fire
	// ArcOfFire
	public static int arcOfFireArc = 20;
	public static int arcOfFireRange = 9;

	// Extinguish
	public static double extinguishRange = 20;
	public static double extinguishRadius = 20;

	// Fireball
	public static long fireballCooldown = 2000;
	public static double fireballSpeed = 0.3;

	// FireBlast
	public static double fireBlastSpeed = 15;
	public static double fireBlastRange = 15;
	public static double fireBlastRadius = 2;
	public static double fireBlastPush = .3;
	public static int fireBlastDamage = 2;
	public static long fireBlastCooldown = 1500;
	public static boolean fireBlastDissipate = false;

	// FireJet
	public static double fireJetSpeed = 0.7;
	public static long fireJetDuration = 1500;
	public static long fireJetCooldown = 6000;

	// FireStream
	public static double fireStreamSpeed = 15;
	public static long dissipateAfter = 400;

	// HeatMelt
	public static int heatMeltRange = 15;
	public static int heatMeltRadius = 5;

	// Illumination
	public static int illuminationRange = 5;

	// Lightning
	public static long lightningwarmup = 1500;
	public static int lightningrange = 15;
	public static double lightningmisschance = 10;

	// RingOfFire
	public static int ringOfFireRange = 7;

	// WallOfFire
	public static int wallOfFireRange = 20;
	public static int wallOfFireHeight = 3;
	public static int wallOfFireWidth = 6;
	public static long wallOfFireDuration = 5000;
	public static int wallOfFireDamage = 4;
	public static long wallOfFireInterval = 400;
	public static long wallOfFireCooldown = 10000;

	// Day
	public static double dayFactor = 1.3;

	// Water
	// Bloodbending
	public static double bloodbendingThrowFactor = 2;
	public static int bloodbendingRange = 10;

	// FastSwimming
	public static double fastSwimmingFactor = 0.7;

	// FreezeMelt
	public static int freezeMeltRange = 20;
	public static int freezeMeltRadius = 5;

	// HealingWaters
	public static double healingWatersRadius = 5;
	public static long healingWatersInterval = 750;

	// IceSpike
	public static long icespikecooldown = 6000;
	public static int icespikedamage = 4;
	public static int icespikerange = 20;
	public static double icespikethrowingmult = 0.7;

	// Plantbending
	public static long plantbendingRegrowTime = 180000;

	// SpikeField
	public static long icespikeareacooldown = 20000;
	public static int icespikeareadamage = 2;
	public static int icespikearearadius = 4;
	public static double icespikeareathrowingmult = 1;

	// WaterBubble
	public static double waterBubbleRadius = airBubbleRadius;

	// WaterManipulation
	public static int waterdmg = 5;
	public static double waterManipulationRange = 20;
	public static double waterManipulationSpeed = 35;

	// WaterSpout
	public static int waterSpoutHeight = 15;

	// WaterWall
	public static double waterWallRange = 5;
	public static double waterWallRadius = 2;

	// Wave
	public static double waveRadius = 3;
	public static double waveHorizontalPush = 1;
	public static double waveVerticalPush = 0.2;

	// Night
	public static double nightFactor = 1.5;

	public void load(File file) {
		FileConfiguration config = new YamlConfiguration();
		try {
			if (file.exists())
				config.load(file);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}

		// Chat:
		enabled = config.getBoolean("Chat.Enabled", enabled);
		colors = config.getBoolean("Chat.Colors", colors);
		compatibility = config.getBoolean("Chat.Compatibility", compatibility);

		// Prefix
		prefixes.put("Air", config.getString("Chat.Prefix.Air", "[Airbender] "));
		prefixes.put("Avatar",
				config.getString("Chat.Prefix.Avatar", "[Avatar] "));
		prefixes.put("Fire",
				config.getString("Chat.Prefix.Fire", "[Firebender ]"));
		prefixes.put("Water",
				config.getString("Chat.Prefix.Water", "[Waterbender] "));
		prefixes.put("Earth",
				config.getString("Chat.Prefix.Earth", "[Earthbender] "));
		prefixes.put("ChiBlocker",
				config.getString("Chat.Prefix.ChiBlocker", "[Chiblocker] "));

		// Color
		color.put("Avatar",
				config.getString("Chat.Color.Avatar", "DARK_PURPLE"));
		color.put("Air", config.getString("Chat.Color.Air", "GRAY"));
		color.put("Fire", config.getString("Chat.Color.Fire", "RED"));
		color.put("Water", config.getString("Chat.Color.Water", "AQUA"));
		color.put("Earth", config.getString("Chat.Color.Earth", "GREEN"));
		color.put("ChiBlocker",
				config.getString("Chat.Color.ChiBlocker", "GOLD"));

		// Bending
		// Damage
		airdmg = config.getInt("Bending.Damage.AirSwipe", airdmg);
		earthdmg = config.getInt("Bending.Damage.EarthBlast", earthdmg);
		waterdmg = config.getInt("Bending.Damage.WaterManipulation", waterdmg);

		// Option
		earthbendable = defaultearthbendable;
		if (config.contains("Bending.Option.EarthBendable"))
			earthbendable = config
					.getStringList("Bending.Option.EarthBendable");

		// EarthBendable
		useWeapon
				.put("Air", config.getBoolean(
						"Bending.Option.Bend-With-Weapon.Air", false));
		useWeapon.put("Earth", config.getBoolean(
				"Bending.Option.Bend-With-Weapon.Earth", false));
		useWeapon.put("Fire", config.getBoolean(
				"Bending.Option.Bend-With-Weapon.Fire", false));
		useWeapon.put("Water", config.getBoolean(
				"Bending.Option.Bend-With-Weapon.Water", false));
		useWeapon.put("ChiBlocker", config.getBoolean(
				"Bending.Option.Bend-With-Weapon.ChiBlocker", false));

		bendToItem = config.getBoolean("Bending.Option.Bend-To-Item",
				bendToItem);
		useTagAPI = config.getBoolean("Bending.Option.Use-TagAPI", useTagAPI);
		reverseearthbending = config.getBoolean(
				"Bending.Option.Reverse-Earthbending", reverseearthbending);
		revertchecktime = config.getLong(
				"Bending.Option.Reverse-Earthbending-Check-Time",
				revertchecktime);
		safeRevert = config
				.getBoolean("Bending.Option.Safe-Revert", safeRevert);
		dissipateAfter = config.getLong(
				"Bending.Option.Firebending-Dissipate-Time", dissipateAfter);
		seaLevel = config.getInt("Bending.Option.Sea-Level", seaLevel);

		// Properties
		// ChiBlocker
		chiblockduration = config.getLong(
				"Properties.ChiBlocker.ChiBlock-Duration", chiblockduration);
		dodgechance = config.getDouble("Properties.ChiBlocker.Dodge-Chance",
				dodgechance);
		punchdamage = config.getDouble("Properties.ChiBlocker.Punch-Damage",
				punchdamage);
		falldamagereduction = config.getDouble(
				"Properties.ChiBlocker.Fall-Damage-Reduction",
				falldamagereduction);

		globalCooldown = config.getLong("Properties.GlobalCooldown",
				globalCooldown);

		// HighJump
		jumpheight = config.getInt("Properties.ChiBlocker.HighJump.Height",
				jumpheight);
		highJumpCooldown = config.getLong(
				"Properties.ChiBlocker.HighJump.Cooldown", highJumpCooldown);

		// RapidPunch
		rapidPunchDamage = config.getInt(
				"Properties.ChiBlocker.RapidPunch.Damage", rapidPunchDamage);
		rapidPunchDistance = config
				.getInt("Properties.ChiBlocker.RapidPunch.Distance",
						rapidPunchDistance);
		rapidPunchCooldown = config
				.getLong("Properties.ChiBlocker.RapidPunch.Cooldown",
						rapidPunchCooldown);
		rapidPunchPunches = config.getInt(
				"Properties.ChiBlocker.RapidPunch.Punches", rapidPunchPunches);

		// Air
		// AirBlast
		airBlastSpeed = config.getDouble("Properties.Air.AirBlast.Speed",
				airBlastSpeed);
		airBlastRange = config.getDouble("Properties.Air.AirBlast.Range",
				airBlastRange);
		airBlastRadius = config.getDouble(
				"Properties.Air.AirBlast.Affecting-Radius", airBlastRadius);
		airBlastPush = config.getDouble("Properties.Air.AirBlast.Push-Factor",
				airBlastPush);

		// AirBubble
		airBubbleRadius = config.getInt("Properties.Air.AirBubble.Radius",
				airBubbleRadius);

		// AirPassive
		airPassiveFactor = (float) config.getDouble(
				"Properties.Air.Passive.Factor", airPassiveFactor);

		// AirShield
		airShieldRadius = config.getDouble("Properties.Air.AirShield.Radius",
				airShieldRadius);

		// AirSuction
		airSuctionSpeed = config.getDouble("Properties.Air.AirSuction.Speed",
				airSuctionSpeed);
		airSuctionRange = config.getDouble("Properties.Air.AirSuction.Range",
				airSuctionRange);
		airSuctionRadius = config.getDouble(
				"Properties.Air.AirSuction.Affecting-Radius", airSuctionRadius);
		airSuctionPush = config.getDouble(
				"Properties.Air.AirSuction.Push-Factor", airSuctionPush);

		// AirSwipe
		airSwipeRange = config.getDouble("Properties.Air.AirSwipe.Range",
				airSwipeRange);
		airSwipeArc = config.getInt("Properties.Air.AirSwipe.Arc", airSwipeArc);
		airSwipeSpeed = config.getDouble("Properties.Air.AirSwipe.Speed",
				airSwipeSpeed);
		airSwipeRadius = config.getDouble(
				"Properties.Air.AirSwipe.Affecting-Radius", airSwipeRadius);
		airSwipePush = config.getDouble("Properties.Air.AirSwipe.Push-Factor",
				airSwipePush);
		airSwipeCooldown = config.getLong("Properties.Air.AirSwipe.Cooldown",
				airSwipeCooldown);

		// Tornado
		tornadoRadius = config.getDouble("Properties.Air.Tornado.Radius",
				tornadoRadius);
		tornadoHeight = config.getDouble("Properties.Air.Tornado.Height",
				tornadoHeight);
		tornadoRange = config.getDouble("Properties.Air.Tornado.Range",
				tornadoRange);
		tornadoMobPush = config.getDouble(
				"Properties.Air.Tornado.Mob-Push-Factor", tornadoMobPush);
		tornadoPlayerPush = config.getDouble(
				"Properties.Air.Tornado.Player-Push-Factor", tornadoPlayerPush);

		// Air Scooter
		airScooterSpeed = config.getDouble("Properties.Air.AirScooter.Speed",
				airScooterSpeed);

		// Air Spout
		airSpoutHeight = config.getDouble("Properties.Air.AirSpout.Height",
				airSpoutHeight);

		// Earth
		// Catapult
		catapultLength = config.getInt("Properties.Earth.Catapult.Length",
				catapultLength);
		catapultSpeed = config.getDouble("Properties.Earth.Catapult.Speed",
				catapultSpeed);
		catapultPush = config.getDouble("Properties.Earth.Catapult.Push",
				catapultPush);

		// CompactColumn
		compactColumnRange = config.getDouble(
				"Properties.Earth.CompactColumn.Range", compactColumnRange);
		compactColumnSpeed = config.getDouble(
				"Properties.Earth.CompactColumn.Speed", compactColumnSpeed);

		// EarthBlast
		earthBlastHitSelf = config.getBoolean(
				"Properties.Earth.EarthBlast.Hit-Self", earthBlastHitSelf);
		earthBlastPrepareRange = config.getDouble(
				"Properties.Earth.EarthBlast.Prepare-Range",
				earthBlastPrepareRange);
		earthBlastRange = config.getDouble("Properties.Earth.EarthBlast.Range",
				earthBlastRange);
		earthBlastSpeed = config.getDouble("Properties.Earth.EarthBlast.Speed",
				earthBlastSpeed);
		earthBlastRevert = config.getBoolean(
				"Properties.Earth.EarthBlast.Revert", earthBlastRevert);

		// EarthColumn
		earthColumnHeight = config.getInt(
				"Properties.Earth.EarthColumn.Height", earthColumnHeight);

		// EarthGrab
		earthGrabRange = config.getDouble("Properties.Earth.EarthGrab.Range",
				earthGrabRange);

		// EarthPassive
		earthPassive = config.getLong(
				"Properties.Earth.EarthPassive.Wait-Before-Reverse-Changes",
				earthPassive);

		// EarthTunnel
		earthTunnelMaxRadius = config
				.getDouble("Properties.Earth.EarthTunnel.Max-Radius",
						earthTunnelMaxRadius);
		earthTunnelRange = config.getDouble(
				"Properties.Earth.EarthTunnel.Range", earthTunnelRange);
		earthTunnelRadius = config.getDouble(
				"Properties.Earth.EarthTunnel.Radius", earthTunnelRadius);
		earthTunnelInterval = config.getLong(
				"Properties.Earth.EarthTunnel.Interval", earthTunnelInterval);
		earthTunnelRevert = config.getBoolean(
				"Properties.Earth.EarthTunnel.Revert", earthTunnelRevert);

		// EarthWall
		earthWallRange = config.getInt("Properties.Earth.EarthWall.Range",
				earthWallRange);
		earthWallHeight = config.getInt("Properties.Earth.EarthWall.Height",
				earthWallHeight);
		earthWallWidth = config.getInt("Properties.Earth.EarthWall.Width",
				earthWallWidth);

		// Collapse
		collapseRange = config.getInt("Properties.Earth.Collapse.Range",
				collapseRange);
		collapseRadius = config.getDouble("Properties.Earth.Collapse.Radius",
				collapseRadius);

		// Tremorsense
		tremorsenseCooldown = config.getLong(
				"Properties.Earth.Tremorsense.Cooldown", tremorsenseCooldown);
		tremorsenseMaxDepth = config.getInt(
				"Properties.Earth.Tremorsense.Max-Depth", tremorsenseMaxDepth);
		tremorsenseRadius = config.getInt(
				"Properties.Earth.Tremorsense.Radius", tremorsenseRadius);
		tremorsenseLightThreshold = (byte) config.getInt(
				"Properties.Earth.Tremorsense.Light-Threshold",
				tremorsenseLightThreshold);

		// EarthArmor
		eartharmorduration = config.getLong(
				"Properties.Earth.EarthArmor.Duration", eartharmorduration);
		eartharmorstrength = config.getInt(
				"Properties.Earth.EarthArmor.Strength", eartharmorstrength);
		eartharmorcooldown = config.getLong(
				"Properties.Earth.EarthArmor.Cooldown", eartharmorcooldown);

		// Fire
		// FireBlast
		fireBlastRange = config.getDouble("Properties.Fire.FireBlast.Range",
				fireBlastRange);
		fireBlastSpeed = config.getDouble("Properties.Fire.FireBlast.Speed",
				fireBlastSpeed);
		fireBlastPush = config.getDouble("Properties.Fire.FireBlast.Push",
				fireBlastPush);
		fireBlastRadius = config.getDouble("Properties.Fire.FireBlast.Radius",
				fireBlastRadius);
		fireBlastCooldown = config.getLong(
				"Properties.Fire.FireBlast.Cooldown", fireBlastCooldown);
		fireBlastDamage = config.getInt("Properties.Fire.FireBlast.Damage",
				fireBlastDamage);
		fireBlastDissipate = config.getBoolean(
				"Properties.Fire.FireBlast.Dissipates", fireBlastDissipate);

		// ArcOfFire
		arcOfFireArc = config.getInt("Properties.Fire.ArcOfFire.Arc",
				arcOfFireArc);
		arcOfFireRange = config.getInt("Properties.Fire.ArcOfFire.Range",
				arcOfFireRange);

		// RingOfFire
		ringOfFireRange = config.getInt("Properties.Fire.RingOfFire.Range",
				ringOfFireRange);

		// Extinguish
		extinguishRange = config.getDouble("Properties.Fire.Extinguish.Range",
				extinguishRange);
		extinguishRadius = config.getDouble(
				"Properties.Fire.Extinguish.Radius", extinguishRadius);

		// Fireball
		fireballCooldown = config.getLong("Properties.Fire.Fireball.Cooldown",
				fireballCooldown);
		fireballSpeed = config.getDouble("Properties.Fire.Fireball.Speed",
				fireballSpeed);

		// FireJet
		fireJetSpeed = config.getDouble("Properties.Fire.FireJet.Speed",
				fireJetSpeed);
		fireJetDuration = config.getLong("Properties.Fire.FireJet.Duration",
				fireJetDuration);
		fireJetCooldown = config.getLong("Properties.Fire.FireJet.CoolDown",
				fireJetCooldown);

		// FireStream
		fireStreamSpeed = config.getDouble("Properties.Fire.FireStream.Speed",
				fireStreamSpeed);

		// WallOfFire
		wallOfFireRange = config.getInt("Properties.Fire.WallOfFire.Range",
				wallOfFireRange);
		wallOfFireHeight = config.getInt("Properties.Fire.WallOfFire.Height",
				wallOfFireHeight);
		wallOfFireWidth = config.getInt("Properties.Fire.WallOfFire.Width",
				wallOfFireWidth);
		wallOfFireDuration = config.getLong(
				"Properties.Fire.WallOfFire.Duration", wallOfFireDuration);
		wallOfFireDamage = config.getInt("Properties.Fire.WallOfFire.Damage",
				wallOfFireDamage);
		wallOfFireInterval = config.getLong(
				"Properties.Fire.WallOfFire.Interval", wallOfFireInterval);
		wallOfFireCooldown = config.getLong(
				"Properties.Fire.WallOfFire.Cooldown", wallOfFireCooldown);

		// HeatMelt
		heatMeltRange = config.getInt("Properties.Fire.HeatMelt.Range",
				heatMeltRange);
		heatMeltRadius = config.getInt("Properties.Fire.HeatMelt.Radius",
				heatMeltRadius);

		// Illumination
		illuminationRange = config.getInt("Properties.Fire.Illumination.Range",
				illuminationRange);

		// Lightning
		lightningwarmup = config.getLong("Properties.Fire.Lightning.Warmup",
				lightningwarmup);
		lightningrange = config.getInt("Properties.Fire.Lightning.Range",
				lightningrange);
		lightningmisschance = config.getDouble(
				"Properties.Fire.Lightning.Miss-Chance", lightningmisschance);

		// Day
		dayFactor = config.getDouble("Properties.Fire.Day-Power-Factor",
				dayFactor);

		// Water
		// Bloodbending
		bloodbendingThrowFactor = config.getDouble(
				"Properties.Water.Bloodbending.Throw-Factor",
				bloodbendingThrowFactor);
		bloodbendingRange = config.getInt(
				"Properties.Water.Bloodbending.Range", bloodbendingRange);

		// FreezeMelt
		freezeMeltRange = config.getInt("Properties.Water.FreezeMelt.Range",
				freezeMeltRange);
		freezeMeltRadius = config.getInt("Properties.Water.FreezeMelt.Radius",
				freezeMeltRadius);

		// HealingWaters
		healingWatersRadius = config.getDouble(
				"Properties.Water.HealingWaters.Radius", healingWatersRadius);
		healingWatersInterval = config.getLong(
				"Properties.Water.HealingWaters.Interval",
				healingWatersInterval);

		// Plantbending
		plantbendingRegrowTime = config.getLong(
				"Properties.Water.Plantbending.Regrow-Time",
				plantbendingRegrowTime);

		// WaterManipulation
		waterManipulationRange = config.getDouble(
				"Properties.Water.WaterManipulation.Range",
				waterManipulationRange);
		waterManipulationSpeed = config.getDouble(
				"Properties.Water.WaterManipulation.Speed",
				waterManipulationSpeed);

		// WaterSpout
		waterSpoutHeight = config.getInt("Properties.Water.WaterSpout.Height",
				waterSpoutHeight);

		// WaterWall
		waterWallRange = config.getDouble("Properties.Water.WaterWall.Range",
				waterWallRange);
		waterWallRadius = config.getDouble("Properties.Water.WaterWall.Radius",
				waterWallRadius);

		// Wave
		waveRadius = config.getDouble("Properties.Water.Wave.Radius",
				waveRadius);
		waveHorizontalPush = config.getDouble(
				"Properties.Water.Wave.Horizontal-Push-Force",
				waveHorizontalPush);
		waveVerticalPush = config.getDouble(
				"Properties.Water.Wave.Vertical-Push-Force", waveVerticalPush);

		// Fast Swimming
		fastSwimmingFactor = config.getDouble(
				"Properties.Water.FastSwimming.Factor", fastSwimmingFactor);

		// IceSpike
		icespikecooldown = config.getLong("Properties.Water.IceSpike.Cooldown",
				icespikecooldown);
		icespikedamage = config.getInt("Properties.Water.IceSpike.Damage",
				icespikedamage);
		icespikerange = config.getInt("Properties.Water.IceSpike.Range",
				icespikerange);
		icespikethrowingmult = config.getDouble(
				"Properties.Water.IceSpike.ThrowingMult", icespikethrowingmult);
		icespikeareacooldown = config.getLong(
				"Properties.Water.IceSpike.AreaCooldown", icespikeareacooldown);
		icespikeareadamage = config.getInt(
				"Properties.Water.IceSpike.AreaDamage", icespikeareadamage);
		icespikearearadius = config.getInt(
				"Properties.Water.IceSpike.AreaRadius", icespikearearadius);
		icespikeareathrowingmult = config.getDouble(
				"Properties.Water.IceSpike.AreaThrowingMult",
				icespikeareathrowingmult);

		// Night
		nightFactor = config.getDouble("Properties.Water.Night-Power-Factor",
				nightFactor);

		// MySQL
		useMySQL = config.getBoolean("MySQL.Use-MySQL", useMySQL);
		dbHost = config.getString("MySQL.MySQL-host", dbHost);
		dbUser = config.getString("MySQL.User", dbUser);
		dbPass = config.getString("MySQL.Password", dbPass);
		dbDB = config.getString("MySQL.Database", dbDB);
		Integer dbPortint = (Integer) config.getInt("MySQL.MySQL-portnumber",
				Integer.parseInt(dbPort));
		dbPort = dbPortint.toString();

		try {
			config.options().copyDefaults(true);
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// private MemoryConfiguration getDefaults() {
	// MemoryConfiguration config = new MemoryConfiguration();
	// config.set("Chat.Enabled", Boolean.valueOf(true));
	// config.set("Chat.Colors", Boolean.valueOf(true));
	// config.set("Chat.Compatibility", Boolean.valueOf(false));
	// config.set("Chat.Prefix.Avatar", "[Avatar] ");
	// config.set("Chat.Prefix.Air", "[Airbender] ");
	// config.set("Chat.Prefix.Fire", "[Firebender] ");
	// config.set("Chat.Prefix.Water", "[Waterbender] ");
	// config.set("Chat.Prefix.Earth", "[Earthbender] ");
	// config.set("Chat.Prefix.ChiBlocker", "[ChiBlocker] ");
	// config.set("Bending.Damage.AirSwipe", Integer.valueOf(2));
	// config.set("Bending.Damage.EarthBlast", Integer.valueOf(7));
	// config.set("Bending.Damage.WaterManipulation", Integer.valueOf(5));
	// config.set("Chat.Color.Avatar", "DARK_PURPLE");
	// config.set("Chat.Color.Air", "GRAY");
	// config.set("Chat.Color.Fire", "RED");
	// config.set("Chat.Color.Water", "AQUA");
	// config.set("Chat.Color.Earth", "GREEN");
	// config.set("Chat.Color.ChiBlocker", "GOLD");
	//
	// config.set("Bending.Option.EarthBendable", defaultearthbendable);
	// config.set("Bending.Option.Bend-With-Weapon.Air", false);
	// config.set("Bending.Option.Bend-With-Weapon.Fire", true);
	// config.set("Bending.Option.Bend-With-Weapon.Water", true);
	// config.set("Bending.Option.Bend-With-Weapon.Earth", true);
	// config.set("Bending.Option.Bend-With-Weapon.ChiBlocker", false);
	// config.set("Bending.Option.Bend-To-Item", false);
	// config.set("Bending.Option.Use-TagAPI", true);
	//
	// config.set("Bending.Option.Reverse-Earthbending", true);
	// config.set("Bending.Option.Safe-Revert", true);
	// config.set("Bending.Option.Reverse-Earthbending-Check-Time", 500000);
	// config.set("Bending.Option.Firebending-Dissipate-Time", 400);
	//
	// config.set("Properties.ChiBlocker.ChiBlock-Duration", 2500);
	// config.set("Properties.ChiBlocker.Dodge-Chance", 25);
	// config.set("Properties.ChiBlocker.Punch-Damage", 3);
	// config.set("Properties.ChiBlocker.Fall-Damage-Reduction", 50);
	//
	// config.set("Properties.GlobalCooldown", 500);
	//
	// config.set("Properties.Air.AirBlast.Speed", 25);
	// config.set("Properties.Air.AirBlast.Range", 20);
	// config.set("Properties.Air.AirBlast.Affecting-Radius", 2);
	// config.set("Properties.Air.AirBlast.Push-Factor", 1.2);
	//
	// config.set("Properties.Air.AirBubble.Radius", 5);
	//
	// config.set("Properties.Air.Passive.Factor", 0.3F);
	//
	// config.set("Properties.Air.AirShield.Radius", 7);
	//
	// config.set("Properties.Air.AirSuction.Speed", 25);
	// config.set("Properties.Air.AirSuction.Range", 20);
	// config.set("Properties.Air.AirSuction.Affecting-Radius", 2);
	// config.set("Properties.Air.AirSuction.Push-Factor", 1);
	//
	// config.set("Properties.Air.AirSwipe.Range", 16);
	// config.set("Properties.Air.AirSwipe.Arc", 20);
	// config.set("Properties.Air.AirSwipe.Speed", 25);
	// config.set("Properties.Air.AirSwipe.Affecting-Radius", 2);
	// config.set("Properties.Air.AirSwipe.Push-Factor", 1);
	// config.set("Properties.Air.AirSwipe.Cooldown", 1000);
	//
	// config.set("Properties.Air.Tornado.Radius", 10);
	// config.set("Properties.Air.Tornado.Height", 25);
	// config.set("Properties.Air.Tornado.Range", 25);
	// config.set("Properties.Air.Tornado.Mob-Push-Factor", 1);
	// config.set("Properties.Air.Tornado.Player-Push-Factor", 1);
	//
	// config.set("Properties.Air.AirScooter.Speed", .675);
	//
	// config.set("Properties.Air.AirSpout.Height", 20);
	//
	// config.set("Properties.Earth.Catapult.Length", 7);
	// config.set("Properties.Earth.Catapult.Speed", 12);
	// config.set("Properties.Earth.Catapult.Push", 5);
	//
	// config.set("Properties.Earth.CompactColumn.Range", 20);
	// config.set("Properties.Earth.CompactColumn.Speed", 8);
	//
	// config.set("Properties.Earth.EarthBlast.Hit-Self", false);
	// config.set("Properties.Earth.EarthBlast.Prepare-Range", 7);
	// config.set("Properties.Earth.EarthBlast.Range", 20);
	// config.set("Properties.Earth.EarthBlast.Speed", 35);
	// config.set("Properties.Earth.EarthBlast.Revert", true);
	//
	// config.set("Properties.Earth.EarthColumn.Height", 6);
	//
	// config.set("Properties.Earth.EarthGrab.Range", 15);
	//
	// config.set("Properties.Earth.EarthPassive.Wait-Before-Reverse-Changes",
	// 3000);
	//
	// config.set("Properties.Earth.EarthTunnel.Max-Radius", 1);
	// config.set("Properties.Earth.EarthTunnel.Range", 10);
	// config.set("Properties.Earth.EarthTunnel.Radius", 0.25);
	// config.set("Properties.Earth.EarthTunnel.Interval", 30);
	// config.set("Properties.Earth.EarthTunnel.Revert", true);
	//
	// config.set("Properties.Earth.EarthWall.Range", 15);
	// config.set("Properties.Earth.EarthWall.Height", 8);
	//
	// config.set("Properties.Earth.EarthWall.Width", 6);
	//
	// config.set("Properties.Earth.Collapse.Range", 20);
	// config.set("Properties.Earth.Collapse.Radius", 7);
	//
	// config.set("Properties.Earth.Tremorsense.Cooldown", 3000);
	// config.set("Properties.Earth.Tremorsense.Max-Depth", 10);
	// config.set("Properties.Earth.Tremorsense.Radius", 5);
	// config.set("Properties.Earth.Tremorsense.Light-Threshold", 7);
	//
	// config.set("Properties.Fire.FireBlast.Speed", 15);
	// config.set("Properties.Fire.FireBlast.Damage", 2);
	// config.set("Properties.Fire.FireBlast.Cooldown", 1500);
	// config.set("Properties.Fire.FireBlast.Radius", 2);
	// config.set("Properties.Fire.FireBlast.Push", .3);
	// config.set("Properties.Fire.FireBlast.Range", 15);
	// config.set("Properties.Fire.FireBlast.Dissipates", false);
	//
	// config.set("Properties.Fire.ArcOfFire.Arc", 20);
	// config.set("Properties.Fire.ArcOfFire.Range", 9);
	//
	// config.set("Properties.Fire.RingOfFire.Range", 7);
	//
	// config.set("Properties.Fire.Extinguish.Range", 20);
	// config.set("Properties.Fire.Extinguish.Radius", 20);
	//
	// config.set("Properties.Fire.Fireball.Cooldown", 2000);
	// config.set("Properties.Fire.Fireball.Speed", 0.3);
	//
	// config.set("Properties.Fire.FireJet.Speed", 0.7);
	// config.set("Properties.Fire.FireJet.Duration", 1500);
	// config.set("Properties.Fire.FireJet.CoolDown", 6000);
	//
	// config.set("Properties.Fire.FireStream.Speed", 15);
	//
	// config.set("Properties.Fire.HeatMelt.Range", 15);
	// config.set("Properties.Fire.HeatMelt.Radius", 5);
	//
	// config.set("Properties.Fire.Illumination.Range", 5);
	//
	// config.set("Properties.Fire.Day-Power-Factor", 1.3);
	//
	// config.set("Properties.Fire.WallOfFire.Range", 20);
	// config.set("Properties.Fire.WallOfFire.Height", 3);
	// config.set("Properties.Fire.WallOfFire.Width", 6);
	// config.set("Properties.Fire.WallOfFire.Duration", 5000);
	// config.set("Properties.Fire.WallOfFire.Damage", 4);
	// config.set("Properties.Fire.WallOfFire.Interval", 400);
	// config.set("Properties.Fire.WallOfFire.Cooldown", 10000);
	//
	// config.set("Properties.Water.FreezeMelt.Range", 20);
	// config.set("Properties.Water.FreezeMelt.Radius", 5);
	//
	// config.set("Properties.Water.HealingWaters.Radius", 5);
	// config.set("Properties.Water.HealingWaters.Interval", 750);
	//
	// config.set("Properties.Water.Plantbending.Regrow-Time", 180000);
	//
	// config.set("Properties.Water.WalkOnWater.Radius", 3.5);
	//
	// config.set("Properties.Water.WaterManipulation.Range", 20);
	// config.set("Properties.Water.WaterManipulation.Speed", 35);
	//
	// config.set("Properties.Water.WaterSpout.Height", 15);
	//
	// config.set("Properties.Water.WaterWall.Range", 5);
	// config.set("Properties.Water.WaterWall.Radius", 2);
	//
	// config.set("Properties.Water.Wave.Radius", 3);
	// config.set("Properties.Water.Wave.Horizontal-Push-Force", 1);
	// config.set("Properties.Water.Wave.Vertical-Push-Force", 0.2);
	//
	// config.set("Properties.Water.FastSwimming.Factor", 0.4);
	//
	// config.set("Properties.Water.Night-Power-Factor", 1.5);
	//
	// config.set("Properties.Fire.Lightning.Warmup", 7500);
	// config.set("Properties.Fire.Lightning.Range", 15);
	// config.set("Properties.Fire.Lightning.SpoutPlugin", false);
	// config.set("Properties.Fire.Lightning.Damage", 5);
	//
	// config.set("Properties.Earth.EarthArmor.Duration", 30000);
	// config.set("Properties.Earth.EarthArmor.Strength", 2);
	// config.set("Properties.Earth.EarthArmor.Cooldown", 150000);
	//
	// config.set("Properties.Fire.Lightning.Warmup", 2000);
	// config.set("Properties.Fire.Lightning.Range", 15);
	// config.set("Properties.Fire.Lightning.Miss-Chance", 10);
	//
	// config.set("Properties.Earth.EarthArmor.Duration", 30000);
	// config.set("Properties.Earth.EarthArmor.Strength", 2);
	// config.set("Properties.Earth.EarthArmor.Cooldown", 150000);
	//
	// config.set("Properties.Water.IceSpike.Cooldown", 6000);
	// config.set("Properties.Water.IceSpike.Damage", 4);
	// config.set("Properties.Water.IceSpike.Range", 20);
	// config.set("Properties.Water.IceSpike.ThrowingMult", 0.7);
	// config.set("Properties.Water.IceSpike.AreaCooldown", 20000);
	// config.set("Properties.Water.IceSpike.AreaDamage", 2);
	// config.set("Properties.Water.IceSpike.AreaRadius", 4);
	// config.set("Properties.Water.IceSpike.AreaThrowingMult", 1);
	//
	// config.set("MySQL.Use-MySQL", false);
	// config.set("MySQL.MySQL-host", "localhost");
	// config.set("MySQL.MySQL-portnumber", 3306);
	// config.set("MySQL.User", "root");
	// config.set("MySQL.Password", "");
	// config.set("MySQL.Database", "minecraft");
	//
	// return config;
	// }

	public static String getColor(String element) {
		return color.get(element);
	}

	public static String getPrefix(String element) {
		return prefixes.get(element);
	}

	static {
		defaultearthbendable.add("STONE");

		defaultearthbendable.add("CLAY");

		defaultearthbendable.add("COAL_ORE");

		defaultearthbendable.add("DIAMOND_ORE");

		defaultearthbendable.add("DIRT");

		defaultearthbendable.add("GOLD_ORE");

		defaultearthbendable.add("GRASS");

		defaultearthbendable.add("GRAVEL");

		defaultearthbendable.add("IRON_ORE");

		defaultearthbendable.add("LAPIS_ORE");

		defaultearthbendable.add("REDSTONE_ORE");

		defaultearthbendable.add("SAND");

		defaultearthbendable.add("SANDSTONE");

		defaultearthbendable.add("GLOWING_REDSTONE_ORE");

	}
}
