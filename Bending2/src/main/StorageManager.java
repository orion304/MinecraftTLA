package main;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import tools.Abilities;
import tools.BendingType;
import tools.ConfigManager;
import tools.Tools;

public class StorageManager {

		private File dataFolder;
		public BendingPlayers config;
		public static Boolean useMySQL;
		public static Boolean useFlatFile;
		public MySQL MySql;

		// private InputStream defConfigStream;

		// public BendingPlayers(File file, InputStream inputStream) {
		// load();
		// dataFolder = file;
		// defConfigStream = inputStream;
		// }

		public StorageManager(File file) {
			dataFolder = file;
			initialize(dataFolder);
		}

		public void removeBending(Player player) {
			if (StorageManager.useFlatFile){
				if (config.checkKeys(player.getName())){
					for (int i = 0; i <= 8; i++) {
						removeAbility(player, i);
						}
					for (Material mat: Material.values()){
						removeAbility(player, mat);
					}
					config.setKey(player.getName(), "");
					player.setDisplayName(player.getName());
				}
			} else if (StorageManager.useMySQL){
				String removeEle = "DELETE FROM bending_element WHERE player ='"+ player.getName() + "'";
				this.MySql.delete(removeEle);
				String removeBind = "DELETE FROM bending_ability WHERE player ='"+ player.getName() + "'";
				this.MySql.delete(removeBind);
				//MySql Query
			}
			return;
		}

		public boolean isBender(Player player, BendingType type) {
			if (StorageManager.useFlatFile) {
				if (config.checkKeys(player.getName())) {
					if (config.getKey(player.getName()).contains("a")
							&& type == BendingType.Air) {
						return true;
					}
					if (config.getKey(player.getName()).contains("e")
							&& type == BendingType.Earth) {
						return true;
					}
					if (config.getKey(player.getName()).contains("w")
							&& type == BendingType.Water) {
						return true;
					}
					if (config.getKey(player.getName()).contains("f")
							&& type == BendingType.Fire) {
						return true;
					}
					if (config.getKey(player.getName()).contains("c")
							&& type == BendingType.ChiBlocker) {
						return true;
					}
				}
			} else if (StorageManager.useMySQL){
				try {
				String getEle = "SELECT bending FROM bending_element WHERE player ='" + player.getName() + "'";
				ResultSet bending = this.MySql.getConnection().createStatement().executeQuery(getEle);
					if (bending.next()){
						if (bending.getString("bending").contains("a")
								&& type == BendingType.Air) {
							return true;
						}
						if (bending.getString("bending").contains("e")
								&& type == BendingType.Earth) {
							return true;
						}
						if (bending.getString("bending").contains("w")
								&& type == BendingType.Water) {
							return true;
						}
						if (bending.getString("bending").contains("f")
								&& type == BendingType.Fire) {
							return true;
						}
						if (bending.getString("bending").contains("c")
								&& type == BendingType.ChiBlocker) {
							return true;
						}
					}
				} catch (SQLException e) {
					return false;
				}
			}
				return false;
			}

		public boolean isBender(String player, BendingType type) {
			if (StorageManager.useFlatFile) {
				if (config.checkKeys(player)) {
					if (config.getKey(player).contains("a")
						&& type == BendingType.Air) {
						return true;
					}
					if (config.getKey(player).contains("e")
							&& type == BendingType.Earth) {
						return true;
					}
					if (config.getKey(player).contains("w")
							&& type == BendingType.Water) {
						return true;
					}
					if (config.getKey(player).contains("f")
							&& type == BendingType.Fire) {
						return true;
					}
					if (config.getKey(player).contains("f")
							&& type == BendingType.ChiBlocker) {
						return true;
					}
				}
			} else if (StorageManager.useMySQL){
				try {
					String getEle = "SELECT bending FROM bending_element WHERE player ='" + player + "'";
					ResultSet bending = this.MySql.getConnection().createStatement().executeQuery(getEle);						if (bending.next()){
							if (bending.getString("bending").contains("a")
									&& type == BendingType.Air) {
								return true;
							}
							if (bending.getString("bending").contains("e")
									&& type == BendingType.Earth) {
								return true;
							}
							if (bending.getString("bending").contains("w")
									&& type == BendingType.Water) {
								return true;
							}
							if (bending.getString("bending").contains("f")
									&& type == BendingType.Fire) {
								return true;
							}
							if (bending.getString("bending").contains("c")
									&& type == BendingType.ChiBlocker) {
								return true;
							}
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
			return false;
		}

		public void setBending(Player player, BendingType type) {
			String bending = "";
			String bendingstring = "";
			if (type == BendingType.Air) {
				bending = "a";
				bendingstring = "As an airbender, you now take no falling damage, have faster sprinting and higher "
						+ "jumps. Additionally, daily activities are easier for you - your food meter decays at a "
						+ "much slower rate";
			} else if (type == BendingType.Earth) {
				bending = "e";
				bendingstring = "As an earthbender, upon landing on bendable earth, you will briefly turn the "
						+ "area to soft sand, negating any fall damage you would have otherwise taken.";
			} else if (type == BendingType.Water) {
				bending = "w";
				bendingstring = "As a waterbender, you no longer take any fall damage when landing on ice, snow "
						+ "or even 1-block-deep water. Additionally, sneaking in the water with a bending ability "
						+ "selected that does not utilize sneak (or no ability at all)"
						+ " will give you accelerated swimming.";
			} else if (type == BendingType.Fire) {
				bending = "f";
				bendingstring = "As a firebender, you now more quickly smother yourself when you catch on fire.";
			} else if (type == BendingType.ChiBlocker) {
				bending = "c";
				bendingstring = "As a chiblocker, you have no active abilities to bind. Instead, you have improved "
						+ "sprinting and jumping, have a dodge chance and deal more damage with your fists. "
						+ "Additionally, punching a bender will block his/her chi for a few seconds, preventing "
						+ "him/her from bending (and even stopping their passive!).";
			} else {
				bending = "s";
				player.setDisplayName(player.getName());
				return;
			}
			if (StorageManager.useFlatFile){
				config.setKey(player.getName(), bending);
			} else if (StorageManager.useMySQL){
				String checkEntry = "SELECT bending FROM bending_element WHERE player ='" + player.getName() + "'";
				ResultSet rs = this.MySql.select(checkEntry);
				try {
					if (rs.next()){
						String updateEntry = "UPDATE bending_element SET bending = " + bending + " WHERE player ='" + player.getName() + "'";
						this.MySql.update(updateEntry);
					} else {
						String insertEntry = "INSERT INTO bending_element VALUES('" + player.getName() + "','" + bending + "')";
						this.MySql.insert(insertEntry);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			player.sendMessage(ChatColor.GOLD + bendingstring);
			player.sendMessage(ChatColor.GOLD
					+ "Use '/bending help' if you need assistance.");

			if (ConfigManager.enabled) {
				String append = "";
				if (!player.isOp()) {
					if (Tools.isBender(player, BendingType.Air)) {
						append = ConfigManager.getPrefix("Air");
					} else if (Tools.isBender(player, BendingType.Earth)) {
						append = ConfigManager.getPrefix("Earth");
					} else if (Tools.isBender(player, BendingType.Fire)) {
						append = ConfigManager.getPrefix("Fire");
					} else if (Tools.isBender(player, BendingType.Water)) {
						append = ConfigManager.getPrefix("Water");
					} else if (Tools.isBender(player, BendingType.ChiBlocker)) {
						append = ConfigManager.getPrefix("ChiBlocker");
					}
					if (!(ConfigManager.compatibility))
						player.setDisplayName(append + player.getName());
				}
				if ((ConfigManager.compatibility) && (ConfigManager.enabled)) {
					ChatColor color = ChatColor.WHITE;
					if (ConfigManager.colors && (!player.isOp())) {
						if (Tools.isBender(player, BendingType.Air)) {
							color = Tools.getColor(ConfigManager.getColor("Air"));
						} else if (Tools.isBender(player, BendingType.Earth)) {
							color = Tools.getColor(ConfigManager.getColor("Earth"));
						} else if (Tools.isBender(player, BendingType.Fire)) {
							color = Tools.getColor(ConfigManager.getColor("Fire"));
						} else if (Tools.isBender(player, BendingType.Water)) {
							color = Tools.getColor(ConfigManager.getColor("Water"));
						} else if (Tools.isBender(player, BendingType.ChiBlocker)) {
							color = Tools.getColor(ConfigManager
									.getColor("ChiBlocker"));
						}
						player.setDisplayName("<" + color + append
								+ player.getName() + ChatColor.WHITE + ">");
					}
				}
			}
		}

		public void setBending(Player player, String type) {
			BendingType bendingtype = BendingType.getType(type);
			if (bendingtype != null) {
				setBending(player, bendingtype);
			}

		}

		public void addBending(Player player, BendingType type) {
			String bending = "";
			if (StorageManager.useFlatFile)
				bending = config.getKey(player.getName());
			else if (StorageManager.useMySQL){
				String getBending = "SELECT bending FROM bending_element WHERE player ='" + player.getName() + "'";
				ResultSet rs = this.MySql.select(getBending);
				
				try {
					if (rs.next()){
						bending = rs.getString("bending");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (!isBender(player, type)) {
				if (type == BendingType.Air) {
					bending += "a";
				} else if (type == BendingType.Earth) {
					bending += "e";
				} else if (type == BendingType.Water) {
					bending += "w";
				} else if (type == BendingType.Fire) {
					bending += "f";
				} else if (type == BendingType.ChiBlocker) {
					bending += "c";
				}
			}
			if(StorageManager.useFlatFile)
				config.setKey(player.getName(), bending);
			else if (StorageManager.useMySQL){
				String checkEntry = "SELECT * FROM bending_element WHERE player ='" + player.getName() + "'";
				ResultSet rs = this.MySql.select(checkEntry);
				try {
					if (rs.next()){
						String updateEntry = "UPDATE bending_element SET bending = '" + bending + "' WHERE player ='" + player.getName() + "'";
						this.MySql.update(updateEntry);
					} else {
						String insertEntry = "INSERT INTO bending_element VALUES('" + player.getName() + "','" + bending + "')";
						this.MySql.insert(insertEntry);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		public void addBending(Player player, String type) {
			BendingType bendingtype = BendingType.getType(type);
			if (bendingtype != null) {
				addBending(player, bendingtype);
			}
		}
		
		public void addBending(String player, BendingType type) {
			String bending = "";
			if (StorageManager.useFlatFile)
				bending = config.getKey(player);
			else if (StorageManager.useMySQL){
				String getBending = "SELECT bending FROM bending_element WHERE player ='" + player + "'";
				ResultSet rs = this.MySql.select(getBending);
				
				try {
					if (rs.next()){
						bending = rs.getString("bending");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (!isBender(player, type)) {
				if (type == BendingType.Air) {
					bending += "a";
				} else if (type == BendingType.Earth) {
					bending += "e";
				} else if (type == BendingType.Water) {
					bending += "w";
				} else if (type == BendingType.Fire) {
					bending += "f";
				} else if (type == BendingType.ChiBlocker) {
					bending += "c";
				}
			}
			if(StorageManager.useFlatFile)
				config.setKey(player, bending);
			else if (StorageManager.useMySQL){
				String checkEntry = "SELECT * FROM bending_element WHERE player ='" + player + "'";
				ResultSet rs = this.MySql.select(checkEntry);
				try {
					if (rs.next()){
						String updateEntry = "UPDATE bending_element SET bending = '" + bending + "' WHERE player ='" + player + "'";
						this.MySql.update(updateEntry);
					} else {
						String insertEntry = "INSERT INTO bending_element VALUES('" + player + "','" + bending + "')";
						this.MySql.insert(insertEntry);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		public void addBending(String player, String type) {
			BendingType bendingtype = BendingType.getType(type);
			if (bendingtype != null) {
				addBending(player, bendingtype);
			}
		}

		public boolean isBender(Player player) {
			if (StorageManager.useFlatFile) {
				if (config.checkKeys(player.getName())) {
					if (config.getKey(player.getName()).contains("a")
							|| config.getKey(player.getName()).contains(
									"e")
							|| config.getKey(player.getName()).contains(
									"w")
							|| config.getKey(player.getName()).contains(
									"f")
							|| config.getKey(player.getName()).contains(
									"s")
							|| config.getKey(player.getName()).contains(
									"c")) {
						return true;
					}
				}
			} else if (StorageManager.useMySQL) {
				String getEle = "SELECT bending FROM bending_element WHERE player ='" + player.getName() + "'";
				ResultSet result = this.MySql.select(getEle);
				try {
					if (result.next()){
						String bending = result.getString("bending");
						if (bending.contains("a")
								|| bending.contains(
										"e")
								|| bending.contains(
										"w")
								|| bending.contains(
										"f")
								|| bending.contains(
										"s")
								|| bending.contains(
										"c")) {
							return true;
						}
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				return false;
			}

		public void setAbility(Player player, String ability, int slot) {
			for (Abilities a : Abilities.values()) {
				if (ability.equalsIgnoreCase(a.name())) {
					setAbility(player, a, slot);
				}
			}
		}
		
		public void setAbility(String player, String ability, int slot) {
			for (Abilities a : Abilities.values()) {
				if (ability.equalsIgnoreCase(a.name())) {
					setAbility(player, a, slot);
				}
			}
		}

		public void setAbility(Player player, Abilities ability, int slot) {
			String setter = player.getName() + "<Bind" + slot + ">";
			if (StorageManager.useFlatFile){
				config.setKey(setter, ability.name());
			} else if (StorageManager.useMySQL){
				String checkAbilities = "SELECT ability FROM bending_ability WHERE setter ='" + setter + "' AND player = '" + player.getName() + "'";
				ResultSet set = this.MySql.select(checkAbilities);
				try {
					if (set.next()){
								String updateAbility = "UPDATE bending_ability SET ability = '" + ability.name() + "' WHERE player ='" + player.getName() + "' AND setter = '" + setter + "'";
								this.MySql.update(updateAbility);
					} else {
						String insertAbility = "INSERT INTO bending_ability VALUES('" + player.getName() + "','" + setter + "','" + ability.name() + "')";
						this.MySql.insert(insertAbility);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		public void setAbility(String player, Abilities ability, int slot) {
			String setter = player + "<Bind" + slot + ">";
			if (StorageManager.useFlatFile){
				config.setKey(setter, ability.name());
			} else if (StorageManager.useMySQL){
				String checkAbilities = "SELECT ability FROM bending_ability WHERE setter ='" + setter + "' AND player = '" + player + "'";
				ResultSet set = this.MySql.select(checkAbilities);
				try {
					if (set.next()){
								String updateAbility = "UPDATE bending_ability SET ability = '" + ability.name() + "' WHERE player ='" + player + "' AND setter = '" + setter + "'";
								this.MySql.update(updateAbility);
					} else {
						String insertAbility = "INSERT INTO bending_ability VALUES('" + player + "','" + setter + "','" + ability.name() + "')";
						this.MySql.insert(insertAbility);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		// Bind to item
		public void setAbility(Player player, String ability, Material mat) {
			for (Abilities a : Abilities.values()) {
				if (ability.equalsIgnoreCase(a.name())) {
					setAbility(player, a, mat);
				}
			}
		}
		
		public void setAbility(String player, String ability, Material mat) {
			for (Abilities a : Abilities.values()) {
				if (ability.equalsIgnoreCase(a.name())) {
					setAbility(player, a, mat);
				}
			}
		}

		public void setAbility(Player player, Abilities ability, Material mat) {
			String setter = player.getName() + "<Bind" + mat.name() + ">";
			if (StorageManager.useFlatFile){
				config.setKey(setter, ability.name());
			} else if (StorageManager.useMySQL){
				String checkAbilities = "SELECT ability FROM bending_ability WHERE setter ='" + setter + "' AND player ='" + player.getName() + "'";
				ResultSet set = this.MySql.select(checkAbilities);
				try {
					if (set.next()){
						String updateAbility = "UPDATE bending_ability SET ability = '" + ability.name() + "' WHERE player ='" + player.getName() + "' AND setter = '" + setter + "'";
						this.MySql.update(updateAbility);
					} else {
						String insertAbility = "INSERT INTO bending_ability VALUES('" + player.getName() + "','" + setter + "','" + ability.name() + "')";
						this.MySql.insert(insertAbility);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		public void setAbility(String player, Abilities ability, Material mat) {
			String setter = player + "<Bind" + mat.name() + ">";
			if (StorageManager.useFlatFile){
				config.setKey(setter, ability.name());
			} else if (StorageManager.useMySQL){
				String checkAbilities = "SELECT ability FROM bending_ability WHERE setter ='" + setter + "' AND player ='" + player + "'";
				ResultSet set = this.MySql.select(checkAbilities);
				try {
					if (set.next()){
						String updateAbility = "UPDATE bending_ability SET ability = '" + ability.name() + "' WHERE player ='" + player + "' AND setter = '" + setter + "'";
						this.MySql.update(updateAbility);
					} else {
						String insertAbility = "INSERT INTO bending_ability VALUES('" + player + "','" + setter + "','" + ability.name() + "')";
						this.MySql.insert(insertAbility);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		public Abilities getAbility(Player player) {
			if (ConfigManager.bendToItem == false)
				return getAbility(player, player.getInventory().getHeldItemSlot());
			return getAbility(player, player.getItemInHand().getType());
		}

		public Abilities getAbility(Player player, int slot) {
			String ability = "";
			String setter = player.getName() + "<Bind" + slot + ">";
			if (StorageManager.useFlatFile){
				ability = config.getKey(setter);
			} else if (StorageManager.useMySQL){
				String selectAbility = "SELECT ability FROM bending_ability WHERE setter ='" + setter + "' AND player = '" + player.getName() + "'";
				ResultSet abilitySet = this.MySql.select(selectAbility);
				
				try {
					if (abilitySet.next()){
						ability = abilitySet.getString("ability");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			for (Abilities a : Abilities.values()) {
				if (ability.equalsIgnoreCase(a.name()))
					return a;
			}
			return null;
		}

		// Bind to item

		public Abilities getAbility(Player player, Material mat) {
			String ability = "";
			String setter = player.getName() + "<Bind" + mat.name() + ">";
			if (StorageManager.useFlatFile){
			ability = config.getKey(setter);
			} else if (StorageManager.useMySQL){
				String selectAbility = "SELECT ability FROM bending_ability WHERE setter ='" + setter + "' AND player = '" + player.getName() + "'";
				ResultSet abilitySet = this.MySql.select(selectAbility);
				
				try {
					if (abilitySet.next()){
						ability = abilitySet.getString("ability");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			for (Abilities a : Abilities.values()) {
				if (ability.equalsIgnoreCase(a.name()))
					return a;
			}
			return null;
		}

		public boolean hasAbility(Player player, Abilities ability) {
			for (int i = 0; i <= 8; i++) {
				if (getAbility(player, i) != null)
					if (getAbility(player, i) == ability)
						return true;
			}
			return false;
		}

		public List<BendingType> getBendingTypes(Player player) {
			List<BendingType> list = Arrays.asList();

			for (BendingType type : BendingType.values()) {
				if (isBender(player, type)) {
					list.add(type);
				}
			}
			return list;
		}

		public void removeAbility(Player player, int slot) {
			if (StorageManager.useFlatFile){
				String setter = player.getName() + "<Bind" + slot + ">";
				config.setKey(setter, null);
			} else if (StorageManager.useMySQL){
				
			}
			
		}

		public void removeAbility(Player player, Material mat) {
			if (StorageManager.useFlatFile){
			String setter = player.getName() + "<Bind" + mat.name() + ">";
			config.setKey(setter, null);
			}
		}

		public void permaRemoveBending(Player player) {
			removeBending(player);
			BendingType type = null;
			setBending(player, type);

		}
		
		private void initialize(File file){
			StorageManager.useMySQL = ConfigManager.useMySQL;
			StorageManager.useFlatFile = !ConfigManager.useMySQL;
			if (StorageManager.useMySQL){
				this.MySql = new MySQL(ConfigManager.dbHost, ConfigManager.dbUser, ConfigManager.dbPass, ConfigManager.dbDB, ConfigManager.dbPort);
				String createTable1 = "CREATE TABLE IF NOT EXISTS Bending_Element(player TEXT NOT NULL, bending TEXT NOT NULL)";
			    String createTable2 = "CREATE TABLE IF NOT EXISTS Bending_Ability(player TEXT NOT NULL, setter TEXT NOT NULL, ability TEXT NOT NULL)";
			    MySql.execute(createTable1);
			    MySql.execute(createTable2);
			} else if (StorageManager.useFlatFile){
				this.config = new BendingPlayers(file);
			}
			Tools.verbose(StorageManager.useFlatFile? "Flat" : "MySQL");
		}

	}
