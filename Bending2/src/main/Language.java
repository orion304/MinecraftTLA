package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import tools.Abilities;
import tools.Tools;
import waterbending.Bloodbending;
import waterbending.FreezeMelt;
import waterbending.HealingWaters;
import waterbending.IceSpike;
import waterbending.OctopusForm;
import waterbending.WaterManipulation;
import waterbending.WaterSpout;
import waterbending.WaterWall;
import airbending.AirBlast;
import airbending.AirBubble;
import airbending.AirBurst;
import airbending.AirScooter;
import airbending.AirShield;
import airbending.AirSpout;
import airbending.AirSuction;
import airbending.AirSwipe;
import airbending.Tornado;
import chiblocking.HighJump;
import chiblocking.Paralyze;
import chiblocking.RapidPunch;
import earthbending.Catapult;
import earthbending.Collapse;
import earthbending.EarthArmor;
import earthbending.EarthBlast;
import earthbending.EarthColumn;
import earthbending.EarthGrab;
import earthbending.EarthTunnel;
import earthbending.Shockwave;
import earthbending.Tremorsense;
import firebending.ArcOfFire;
import firebending.Extinguish;
import firebending.FireBlast;
import firebending.FireBurst;
import firebending.FireJet;
import firebending.FireShield;
import firebending.Illumination;
import firebending.Lightning;
import firebending.WallOfFire;

public class Language {

	private HashMap<String, HashMap<String, String>> descriptions = new HashMap<String, HashMap<String, String>>();
	private HashMap<String, String> defaultdescriptions = new HashMap<String, String>();

	private List<String> supportedlanguages = new ArrayList<String>();
	private String defaultlanguage = "en";

	FileConfiguration config = new YamlConfiguration();

	public void load(File file) {

		try {
			if (file.exists())
				config.load(file);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}

		supportedlanguages.add(defaultlanguage);

		if (config.contains("SupportedLanguages"))
			supportedlanguages = config.getStringList("SupportedLanguages");

		String[] indexlist = new String[] { "AirBlast", "AirBubble",
				"AirBurst", "AirScooter", "AirShield", "AirSpout",
				"AirSuction", "AirSwipe", "Tornado", "HighJump", "Paralyze",
				"RapidPunch", "Catapult", "Collapse", "EarthArmor",
				"EarthBlast", "RaiseEarth", "EarthGrab", "EarthTunnel",
				"Shockwave", "Tremorsense", "Blaze", "HeatControl",
				"FireBlast", "FireBurst", "FireJet", "FireShield",
				"Illumination", "Lightning", "WallOfFire", "Bloodbending",
				"PhaseChange", "HealingWaters", "IceSpike", "OctopusForm",
				"WaterBubble", "WaterManipulation", "WaterSpout", "Surge" };

		defaultdescriptions.put("AirBlast", AirBlast.getDescription());
		defaultdescriptions.put("AirBubble", AirBubble.getDescription());
		defaultdescriptions.put("AirBurst", AirBurst.getDescription());
		defaultdescriptions.put("AirScooter", AirScooter.getDescription());
		defaultdescriptions.put("AirShield", AirShield.getDescription());
		defaultdescriptions.put("AirSpout", AirSpout.getDescription());
		defaultdescriptions.put("AirSuction", AirSuction.getDescription());
		defaultdescriptions.put("AirSwipe", AirSwipe.getDescription());
		defaultdescriptions.put("Tornado", Tornado.getDescription());
		defaultdescriptions.put("HighJump", HighJump.getDescription());
		defaultdescriptions.put("Paralyze", Paralyze.getDescription());
		defaultdescriptions.put("RapidPunch", RapidPunch.getDescription());
		defaultdescriptions.put("Catapult", Catapult.getDescription());
		defaultdescriptions.put("Collapse", Collapse.getDescription());
		defaultdescriptions.put("EarthArmor", EarthArmor.getDescription());
		defaultdescriptions.put("EarthBlast", EarthBlast.getDescription());
		defaultdescriptions.put("RaiseEarth", EarthColumn.getDescription());
		defaultdescriptions.put("EarthGrab", EarthGrab.getDescription());
		defaultdescriptions.put("EarthTunnel", EarthTunnel.getDescription());
		defaultdescriptions.put("Shockwave", Shockwave.getDescription());
		defaultdescriptions.put("Tremorsense", Tremorsense.getDescription());
		defaultdescriptions.put("Blaze", ArcOfFire.getDescription());
		defaultdescriptions.put("HeatControl", Extinguish.getDescription());
		defaultdescriptions.put("FireBlast", FireBlast.getDescription());
		defaultdescriptions.put("FireBurst", FireBurst.getDescription());
		defaultdescriptions.put("FireJet", FireJet.getDescription());
		defaultdescriptions.put("FireShield", FireShield.getDescription());
		defaultdescriptions.put("Illumination", Illumination.getDescription());
		defaultdescriptions.put("Lightning", Lightning.getDescription());
		defaultdescriptions.put("WallOfFire", WallOfFire.getDescription());
		defaultdescriptions.put("Bloodbending", Bloodbending.getDescription());
		defaultdescriptions.put("PhaseChange", FreezeMelt.getDescription());
		defaultdescriptions
				.put("HealingWaters", HealingWaters.getDescription());
		defaultdescriptions.put("IceSpike", IceSpike.getDescription());
		defaultdescriptions.put("OctopusForm", OctopusForm.getDescription());
		defaultdescriptions.put("WaterBubble", AirBubble.getDescription());
		defaultdescriptions.put("WaterManipulation",
				WaterManipulation.getDescription());
		defaultdescriptions.put("WaterSpout", WaterSpout.getDescription());
		defaultdescriptions.put("Surge", WaterWall.getDescription());

		for (String language : supportedlanguages) {
			HashMap<String, String> langdescriptions = new HashMap<String, String>();

			for (String index : indexlist) {
				String element;
				if (Abilities.isAirbending(Abilities.getAbility(index))) {
					element = "Air";
				} else if (Abilities
						.isWaterbending(Abilities.getAbility(index))) {
					element = "Water";
				} else if (Abilities.isFirebending(Abilities.getAbility(index))) {
					element = "Fire";
				} else if (Abilities
						.isEarthbending(Abilities.getAbility(index))) {
					element = "Earth";
				} else if (Abilities.isChiBlocking(Abilities.getAbility(index))) {
					element = "Chiblocker";
				} else {
					element = "Other";
				}
				langdescriptions.put(index,
						config.getString(
								language + "." + element + "." + index,
								defaultdescriptions.get(index)));
			}

			HashMap<String, String> messages = new HashMap<String, String>();

			messages.put("Sunrise", config.getString(language + "."
					+ "Messages.Sunrise", BendingManager.defaultsunrisemessage));
			messages.put("Sunset", config.getString(language + "."
					+ "Messages.Sunset", BendingManager.defaultsunsetmessage));
			messages.put("Moonrise", config.getString(language + "."
					+ "Messages.Moonrise",
					BendingManager.defaultmoonrisemessage));
			messages.put("Moonset", config.getString(language + "."
					+ "Messages.Moonset", BendingManager.defaultmoonsetmessage));

			HashMap<String, String> general = new HashMap<String, String>();
			general.put("usage", config.getString(language + ".General.usage",
					BendingCommand.usage));
			general.put("the_server", config.getString(language
					+ ".General.the_server", BendingCommand.the_server));
			general.put("who_usage", config.getString(language
					+ ".General.who_usage", BendingCommand.who_usage));
			general.put("who_not_on_server", config.getString(language
					+ ".General.who_not_on_server",
					BendingCommand.who_not_on_server));
			general.put("who_player_usage", config.getString(language
					+ ".General.who_player_usage",
					BendingCommand.who_player_usage));
			general.put("choose_usage", config.getString(language
					+ ".General.choose_usage", BendingCommand.choose_usage));
			general.put("choose_player_usage", config.getString(language
					+ ".General.choose_player_usage",
					BendingCommand.choose_player_usage));
			general.put("no_perms_air", config.getString(language
					+ ".General.no_perms_air", BendingCommand.no_perms_air));
			general.put("no_perms_fire", config.getString(language
					+ ".General.no_perms_fire", BendingCommand.no_perms_fire));
			general.put("no_perms_earth", config.getString(language
					+ ".General.no_perms_earth", BendingCommand.no_perms_earth));
			general.put("no_perms_water", config.getString(language
					+ ".General.no_perms_water", BendingCommand.no_perms_water));
			general.put("no_perms_chi", config.getString(language
					+ ".General.no_perms_chi", BendingCommand.no_perms_chi));
			general.put("other_no_perms_air", config.getString(language
					+ ".General.other_no_perms_air",
					BendingCommand.other_no_perms_air));
			general.put("other_no_perms_fire", config.getString(language
					+ ".General.other_no_perms_fire",
					BendingCommand.other_no_perms_fire));
			general.put("other_no_perms_earth", config.getString(language
					+ ".General.other_no_perms_earth",
					BendingCommand.other_no_perms_earth));
			general.put("other_no_perms_water", config.getString(language
					+ ".General.other_no_perms_water",
					BendingCommand.other_no_perms_water));
			general.put("other_no_perms_chi", config.getString(language
					+ ".General.other_no_perms_chi",
					BendingCommand.other_no_perms_chi));
			general.put("choosen_air", config.getString(language
					+ ".General.choosen_air", BendingCommand.choosen_air));
			general.put("choosen_fire", config.getString(language
					+ ".General.choosen_fire", BendingCommand.choosen_fire));
			general.put("choosen_earth", config.getString(language
					+ ".General.choosen_earth", BendingCommand.choosen_earth));
			general.put("choosen_water", config.getString(language
					+ ".General.choosen_water", BendingCommand.choosen_water));
			general.put("choosen_chi", config.getString(language
					+ ".General.choosen_chi", BendingCommand.choosen_chi));
			general.put("you_changed", config.getString(language
					+ ".General.you_changed", BendingCommand.you_changed));
			general.put("changed_you", config.getString(language
					+ ".General.changed_you", BendingCommand.changed_you));
			general.put("import_usage", config.getString(language
					+ ".General.import_usage", BendingCommand.import_usage));
			general.put("import_noSQL", config.getString(language
					+ ".General.import_noSQL", BendingCommand.import_noSQL));
			general.put("import_success", config.getString(language
					+ ".General.import_success", BendingCommand.import_success));
			general.put("no_execute_perms", config.getString(language
					+ ".General.no_execute_perms",
					BendingCommand.no_execute_perms));
			general.put("no_bind_perms", config.getString(language
					+ ".General.no_bind_perms", BendingCommand.no_bind_perms));
			general.put("no_use_perms", config.getString(language
					+ ".General.no_use_perms", BendingCommand.no_use_perms));
			general.put("reload_success", config.getString(language
					+ ".General.reload_success", BendingCommand.reload_success));
			general.put("display_usage", config.getString(language
					+ ".General.display_usage", BendingCommand.display_usage));
			general.put("display_element_usage", config.getString(language
					+ ".General.display_element_usage",
					BendingCommand.display_element_usage));
			general.put("slot", config.getString(language + ".General.slot",
					BendingCommand.slot));
			general.put("display_no_abilities", config.getString(language
					+ ".General.display_no_abilities",
					BendingCommand.display_no_abilities));
			general.put("toggle_usage", config.getString(language
					+ ".General.toggle_usage", BendingCommand.toggle_usage));
			general.put("toggle_off", config.getString(language
					+ ".General.toggle_off", BendingCommand.toggle_off));
			general.put("toggle_on", config.getString(language
					+ ".General.toggle_on", BendingCommand.toggle_on));
			general.put("admin_toggle_off", config.getString(language
					+ ".General.admin_toggle_off",
					BendingCommand.admin_toggle_off));
			general.put("admin_toggle_on", config.getString(language
					+ ".General.admin_toggle_on",
					BendingCommand.admin_toggle_on));
			general.put("admin_toggle", config.getString(language
					+ ".General.admin_toggle", BendingCommand.admin_toggle));
			general.put("not_from_console", config.getString(language
					+ ".General.not_from_console",
					BendingCommand.not_from_console));
			general.put("permaremove_message", config.getString(language
					+ ".General.permaremove_message",
					BendingCommand.permaremove_message));
			general.put("you_permaremove", config.getString(language
					+ ".General.you_permaremove",
					BendingCommand.you_permaremove));
			general.put("permaremove_you", config.getString(language
					+ ".General.permaremove_you",
					BendingCommand.permaremove_you));
			general.put("remove_usage", config.getString(language
					+ ".General.remove_usage", BendingCommand.remove_usage));
			general.put("remove_you", config.getString(language
					+ ".General.remove_you", BendingCommand.remove_you));
			general.put("you_remove", config.getString(language
					+ ".General.you_remove", BendingCommand.you_remove));
			general.put("add_self", config.getString(language
					+ ".General.add_self", BendingCommand.add_self));
			general.put("add_other", config.getString(language
					+ ".General.add_other", BendingCommand.add_other));
			general.put("clear_all", config.getString(language
					+ ".General.clear_all", BendingCommand.clear_all));
			general.put("clear_slot", config.getString(language
					+ ".General.clear_slot", BendingCommand.clear_slot));
			general.put("clear_item", config.getString(language
					+ ".General.clear_item", BendingCommand.clear_item));
			general.put("cleared_message", config.getString(language
					+ ".General.cleared_message",
					BendingCommand.cleared_message));
			general.put("slot_item_cleared", config.getString(language
					+ ".General.slot_item_cleared",
					BendingCommand.slot_item_cleared));
			general.put("bind_slot", config.getString(language
					+ ".General.bind_slot", BendingCommand.bind_slot));
			general.put("bind_to_slot", config.getString(language
					+ ".General.bind_to_slot", BendingCommand.bind_to_slot));
			general.put("bind_item", config.getString(language
					+ ".General.bind_item", BendingCommand.bind_item));
			general.put("bind_to_item", config.getString(language
					+ ".General.bind_to_item", BendingCommand.bind_to_item));
			general.put("not_air", config.getString(language
					+ ".General.not_air", BendingCommand.not_air));
			general.put("not_earth", config.getString(language
					+ ".General.not_earth", BendingCommand.not_earth));
			general.put("not_fire", config.getString(language
					+ ".General.not_fire", BendingCommand.not_fire));
			general.put("not_water", config.getString(language
					+ ".General.not_water", BendingCommand.not_water));
			general.put("not_chi", config.getString(language
					+ ".General.not_chi", BendingCommand.not_chi));
			general.put("bound_to", config.getString(language
					+ ".General.bound_to", BendingCommand.bound_to));
			general.put("bound_to_slot", config.getString(language
					+ ".General.bound_to_slot", BendingCommand.bound_to_slot));
			general.put("help_list", config.getString(language
					+ ".General.help_list", BendingCommand.help_list));
			general.put("command_list", config.getString(language
					+ ".General.command_list", BendingCommand.command_list));
			general.put("ability_list", config.getString(language
					+ ".General.ability_list", BendingCommand.ability_list));
			general.put("player", config.getString(
					language + ".General.player", BendingCommand.player));
			general.put("element", config.getString(language
					+ ".General.element", BendingCommand.element));

			HashMap<String, String> total = new HashMap<String, String>();
			for (String index : messages.keySet()) {
				if (total.containsKey(index)) {
					Tools.verbose("Duplicate values in the code!");
					return;
				}
				total.put(index, messages.get(index));
			}
			for (String index : general.keySet()) {
				if (total.containsKey(index)) {
					Tools.verbose("Duplicate values in the code!");
					return;
				}
				total.put(index, general.get(index));
			}
			for (String index : langdescriptions.keySet()) {
				if (total.containsKey(index)) {
					Tools.verbose("Duplicate values in the code!");
					return;
				}
				total.put(index, langdescriptions.get(index));
			}

			descriptions.put(language, total);

		}

		try {
			config.setDefaults(config);
			config.options().copyDefaults(true);
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
