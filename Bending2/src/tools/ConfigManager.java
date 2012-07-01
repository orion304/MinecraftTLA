package tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigManager {

	public static boolean enabled = true;
	public static boolean colors = true;
	public static boolean compatibility = true;
	public static int airdmg = 1;
	public static int earthdmg = 7;
	public static int waterdmg = 7;
	public static Map<String, String> prefixes = new HashMap<String, String>();
	public static Map<String, String> color = new HashMap<String, String>();
	public static List<String> earthbendable = new ArrayList<String>();
	public static Map<String, Boolean> useWeapon = new HashMap<String, Boolean>();

	private static List<String> defaultearthbendable = new ArrayList<String>();

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
		config.setDefaults(getDefaults());

		colors = config.getBoolean("Chat.Colors");

		airdmg = config.getInt("Bending.Damage.AirSwipe");

		enabled = config.getBoolean("Chat.Enabled");

		compatibility = config.getBoolean("Chat.Compatibility");

		waterdmg = config.getInt("Bending.Damage.WaterManipulation");

		earthdmg = config.getInt("Bending.Damage.EarthBlast");

		prefixes.put("Air", config.getString("Chat.Prefix.Air"));
		prefixes.put("Avatar", config.getString("Chat.Prefix.Avatar"));
		prefixes.put("Fire", config.getString("Chat.Prefix.Fire"));
		prefixes.put("Water", config.getString("Chat.Prefix.Water"));
		prefixes.put("Earth", config.getString("Chat.Prefix.Earth"));

		color.put("Avatar", config.getString("Chat.Color.Avatar"));
		color.put("Air", config.getString("Chat.Color.Air"));
		color.put("Fire", config.getString("Chat.Color.Fire"));
		color.put("Water", config.getString("Chat.Color.Water"));
		color.put("Earth", config.getString("Chat.Color.Earth"));

		earthbendable = config.getStringList("Bending.Option.EarthBendable");

		useWeapon.put("Air",
				config.getBoolean("Bending.Option.Bend-With-Weapon.Air"));
		useWeapon.put("Earth",
				config.getBoolean("Bending.Option.Bend-With-Weapon.Earth"));
		useWeapon.put("Fire",
				config.getBoolean("Bending.Option.Bend-With-Weapon.Fire"));
		useWeapon.put("Water",
				config.getBoolean("Bending.Option.Bend-With-Weapon.Water"));

		try {
			config.options().copyDefaults(true);
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private MemoryConfiguration getDefaults() {
		MemoryConfiguration config = new MemoryConfiguration();
		config.set("Chat.Enabled", Boolean.valueOf(true));
		config.set("Chat.Colors", Boolean.valueOf(true));
		config.set("Chat.Compatibility", Boolean.valueOf(false));
		config.set("Chat.Prefix.Avatar", "[Avatar] ");
		config.set("Chat.Prefix.Air", "[Airbender] ");
		config.set("Chat.Prefix.Fire", "[Firebender] ");
		config.set("Chat.Prefix.Water", "[Waterbender] ");
		config.set("Chat.Prefix.Earth", "[Earthbender] ");
		config.set("Bending.Damage.AirSwipe", Integer.valueOf(2));
		config.set("Bending.Damage.EarthBlast", Integer.valueOf(7));
		config.set("Bending.Damage.WaterManipulation", Integer.valueOf(5));
		config.set("Chat.Color.Avatar", "DARK_PURPLE");
		config.set("Chat.Color.Air", "GRAY");
		config.set("Chat.Color.Fire", "RED");
		config.set("Chat.Color.Water", "AQUA");
		config.set("Chat.Color.Earth", "GREEN");
		config.set("Bending.Option.EarthBendable", defaultearthbendable);
		config.set("Bending.Option.Bend-With-Weapon.Air", false);
		config.set("Bending.Option.Bend-With-Weapon.Fire", true);
		config.set("Bending.Option.Bend-With-Weapon.Water", true);
		config.set("Bending.Option.Bend-With-Weapon.Earth", true);
		config.set("Properties.Air.AirBlast.Speed", 25);
		config.set("Properties.Air.AirBlast.Range", 20);
		config.set("Properties.Air.AirBlast.Affecting-Radius", 2);
		config.set("Properties.Air.AirBlast.Push-Factor", 1);
		config.set("Properties.Air.AirBubble.Radius", 5);
		config.set("Properties.Air.Passive.Factor", 0.3F);
		config.set("Properties.Air.AirShield.Radius", 7);
		config.set("Properties.Air.AirSuction.Speed", 25);
		config.set("Properties.Air.AirSuction.Range", 20);
		config.set("Properties.Air.AirSuction.Affecting-Radius", 2);
		config.set("Properties.Air.AirSuction.Push-Factor", 1);
		config.set("Properties.Air.AirSwipe.Range", 16);
		config.set("Properties.Air.AirSwipe.Arc", 20);
		config.set("Properties.Air.AirSwipe.Speed", 25);
		config.set("Properties.Air.AirSwipe.Affecting-Radius", 2);
		config.set("Properties.Air.AirSwipe.Push-Factor", 1);
		config.set("Properties.Air.AirSwipe.Cooldown", 1000);
		config.set("Properties.Air.Tornado.Radius", 10);
		config.set("Properties.Air.Tornado.Height", 25);
		config.set("Properties.Air.Tornado.Range", 25);
		config.set("Properties.Air.Tornado.Mob-Push-Factor", 1);
		config.set("Properties.Air.Tornado.Player-Push-Factor", 1);
		config.set("Properties.Earth.Catapult.Length", 7);
		config.set("Properties.Earth.Catapult.Speed", 12);
		config.set("Properties.Earth.Catapult.Push", 5);
		config.set("Properties.Earth.CompactColumn.Range", 20);
		config.set("Properties.Earth.CompactColumn.Speed", 8);
		config.set("Properties.Earth.EarthBlast.Range", 20);
		config.set("Properties.Earth.EarthBlast.Speed", 35);
		config.set("Properties.Earth.EarthColumn.Height", 6);
		config.set("Properties.Earth.EarthGrab.Range", 15);
		config.set("Properties.Earth.EarthPassive.Wait-Before-Reverse-Changes", 3000);
		config.set("Properties.Earth.EarthTunnel.Max-Radius", 1);
		config.set("Properties.Earth.EarthTunnel.Range", 10);
		config.set("Properties.Earth.EarthTunnel.Radius", 0.25);
		config.set("Properties.Earth.EarthTunnel.Interval", 30);
		config.set("Properties.Earth.EarthWall.Range", 15);
		config.set("Properties.Earth.EarthWall.Height", 8);
		config.set("Properties.Earth.EarthTunnel.Width", 6);
		config.set("Properties.Earth.PatchTheEarth.Range", 20);
		config.set("Properties.Earth.PatchTheEarth.Radius", 7);
		config.set("Properties.Fire.ArcOfFire.Arc", 20);
		config.set("Properties.Fire.Extinguish.Range", 20);
		config.set("Properties.Fire.Extinguish.Radius", 20);
		config.set("Properties.Fire.Fireball.Cooldown", 1000);
		config.set("Properties.Fire.Fireball.Speed", 0.3);
		config.set("Properties.Fire.FireJet.Speed", 0.6);
		config.set("Properties.Fire.FireJet.Duration", 2000);
		config.set("Properties.Fire.FireJet.CoolDown", 6000);
		config.set("Properties.Fire.FireStream.Speed", 20);
		config.set("Properties.Fire.FireStream.Range", 20);
		config.set("Properties.Fire.HeatMelt.Range", 20);
		config.set("Properties.Fire.HeatMelt.Radius", 6000);
		return config;
	}

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

		defaultearthbendable.add("NETHERRACK");

		defaultearthbendable.add("REDSTONE_ORE");

		defaultearthbendable.add("SAND");

		defaultearthbendable.add("SANDSTONE");

	}
}