package earthbending;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import tools.ConfigManager;
import tools.Tools;

public class EarthArmor {
	
	private static long duration = ConfigManager.eartharmorduration;
	private static int strength = ConfigManager.eartharmorstrength;
	private static long cooldown = ConfigManager.eartharmorcooldown;
	
	private static long interval = 200;
	public static int wallheight = 2;
	public static Map<String, Long> durations = new HashMap<String, Long>();
	private static Map<String, Long> intervals = new HashMap<String, Long>();
	private static Map<String, Long> cooldowns = new HashMap<String, Long>();
	private static Map<String, Location> origins = new HashMap<String, Location>();
	public static ConcurrentMap<Player, Integer> movingArmor = new ConcurrentHashMap<Player, Integer>();
	public static Map<String, ItemStack[]> armorsaves = new HashMap<String, ItemStack[]>();
	private static BlockFace[] faces = {BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST};
	
	public EarthArmor(Player p){
		//Tools.verbose("Trying to create a new instance of Earth Armor");
		if (!movingArmor.containsKey(p)){
		if (cooldowns.containsKey(p.getName())){
			if (cooldowns.get(p.getName()) + cooldown <= System.currentTimeMillis()){
				//Tools.verbose("Cooldown's done");
				movingArmor.put(p, 0);
				origins.put(p.getName(), p.getLocation());
				//cooldowns.remove(p.getName());
			}
		} else {
			movingArmor.put(p, 0);
			origins.put(p.getName(), p.getLocation());
		}
		}
	}

	
	public static void moveArmor(Player p){
		if (intervals.containsKey(p.getName())){
			if (intervals.get(p.getName()) + interval > System.currentTimeMillis()){
				return;
			}
		}
		
		if (!(movingArmor.containsKey(p)))
			return;
			
			if (durations.containsKey(p.getName())){
				if (durations.get(p.getName()) + duration <= System.currentTimeMillis()){
					removeEffect(p);
					//Tools.verbose("REMOVIN");
					return;
				}
				return;
		}
		Block ublock = origins.get(p.getName()).getBlock().getRelative(BlockFace.DOWN);
		
		if (p.getLocation().getBlock() != origins.get(p.getName()).getBlock()){
			movingArmor.remove(p);
			return;
		}
		
		if (ublock.getType() == Material.AIR)
			return;
		if (Tools.isEarthbendable(ublock.getRelative(BlockFace.EAST).getRelative(BlockFace.UP, movingArmor.get(p)))
				&& Tools.isEarthbendable(ublock.getRelative(BlockFace.NORTH).getRelative(BlockFace.UP, movingArmor.get(p)))
				&& Tools.isEarthbendable(ublock.getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP, movingArmor.get(p)))
				&& Tools.isEarthbendable(ublock.getRelative(BlockFace.WEST).getRelative(BlockFace.UP, movingArmor.get(p)))){
			//Tools.verbose("Block check is good but height went wrong");
				if (movingArmor.get(p) < wallheight ){
					//Tools.verbose("Can do");
			Vector direction = new Vector(0, 1, 0);
			for (int i = 0; i < faces.length; i++)
				Tools.moveEarth(ublock.getRelative(faces[i]).getRelative(BlockFace.UP, movingArmor.get(p)), direction, wallheight, false);
			//Tools.moveEarth(ublock.getRelative(BlockFace.NORTH).getRelative(BlockFace.UP, movingArmor.get(p)), direction, wallheight, false);
			//Tools.moveEarth(ublock.getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP, movingArmor.get(p)), direction, wallheight, false);
			//Tools.moveEarth(ublock.getRelative(BlockFace.WEST).getRelative(BlockFace.UP, movingArmor.get(p)), direction, wallheight, false);
			intervals.put(p.getName(), System.currentTimeMillis());
			int i = movingArmor.get(p);
			i++;
			movingArmor.put(p, i);
			return;
			//Tools.verbose(movingArmor.get(p));
				} 
		
		if (movingArmor.get(p) == wallheight && !durations.containsKey(p.getName())){
					//Tools.verbose("Can't do");
					armorsaves.put(p.getName(), p.getInventory().getArmorContents());
					YamlConfiguration dc = new YamlConfiguration();
				    File sv = new File(Bukkit.getPluginManager().getPlugin("Bending").getDataFolder(), "Armour.sav");
				    if (!sv.exists())
				    {
				      Bukkit.getPluginManager().getPlugin("Bending").getDataFolder().mkdirs();
				      try
				      {
				        sv.createNewFile();
				      }
				      catch (IOException e)
				      {
				        e.printStackTrace();
				      }
				    }
				      dc.set("Armors." + p.getName() + ".Boots", armorsaves.get(p.getName())[0].getTypeId() + ":" + armorsaves.get(p.getName())[0].getDurability());
				      dc.set("Armors." + p.getName() + ".Leggings", armorsaves.get(p.getName())[1].getTypeId() + ":" + armorsaves.get(p.getName())[1].getDurability());
				      dc.set("Armors." + p.getName() + ".Chest", armorsaves.get(p.getName())[2].getTypeId() + ":" + armorsaves.get(p.getName())[2].getDurability());
				      dc.set("Armors." + p.getName() + ".Helm", armorsaves.get(p.getName())[3].getTypeId() + ":" + armorsaves.get(p.getName())[3].getDurability());
				 try
				    {
				      dc.save(sv);
				    }
				    catch (IOException e)
				    {
				    }
					ItemStack armors[] = {new ItemStack(Material.LEATHER_BOOTS, 1) ,
							new ItemStack(Material.LEATHER_LEGGINGS, 1), 
							new ItemStack(Material.LEATHER_CHESTPLATE, 1), 
							new ItemStack(Material.LEATHER_HELMET, 1)
					};
					p.getInventory().setArmorContents(armors);
					Vector direction1 = new Vector(1, 0, 0);
					Vector direction3 = new Vector(0, 0, 1);
					Tools.moveEarth(ublock.getRelative(BlockFace.EAST).getRelative(BlockFace.UP).getRelative(BlockFace.UP), direction3, 1);
					Tools.moveEarth(ublock.getRelative(BlockFace.NORTH).getRelative(BlockFace.UP), direction1, 1);
					for (int i = 1 ; i <= wallheight + 1; i++)
						for (int f = 0 ; f <= faces.length - 1; f++)
					ublock.getRelative(faces[f]).getRelative(BlockFace.UP, i).setType(Material.AIR);
					//ublock.getRelative(BlockFace.NORTH).getRelative(BlockFace.UP).getRelative(BlockFace.UP).setType(Material.AIR);
					//ublock.getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP).getRelative(BlockFace.UP).setType(Material.AIR);
					//ublock.getRelative(BlockFace.EAST).getRelative(BlockFace.UP).setType(Material.AIR);
					//ublock.getRelative(BlockFace.WEST).getRelative(BlockFace.UP).setType(Material.AIR);
					//ublock.getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP).setType(Material.AIR);
					//movingArmor.remove(p);
					p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, (int) duration / 50, strength - 1));
					intervals.put(p.getName(), System.currentTimeMillis());
					//int i = movingArmor.get(p);
					//i++;
					//movingArmor.put(p, i);
					durations.put(p.getName(), System.currentTimeMillis());
					//return;
				}
				
		}
		//if (movingArmor.get(p) == wallheight + 1 && !durations.containsKey(p.getName())){
		//	//Tools.verbose("Cannooot do");
		//	ublock.getRelative(BlockFace.UP).setType(Material.AIR);
		//	ublock.getRelative(BlockFace.UP).getRelative(BlockFace.UP).setType(Material.AIR);
		//	durations.put(p.getName(), System.currentTimeMillis());
		//	cooldowns.put(p.getName(), System.currentTimeMillis());
		//	return;
		//}
		//Tools.verbose(movingArmor.get(p));
		//Tools.verbose(durations.containsKey(p.getName())? "Yes" : "No");
		//intervals.put(p.getName(), System.currentTimeMillis());
	}
	
	public static void removeEffect(Player p){
				p.getInventory().setArmorContents(armorsaves.get(p.getName()));
				durations.remove(p.getName());
				intervals.remove(p.getName());
				cooldowns.put(p.getName(), System.currentTimeMillis());
				movingArmor.remove(p);
				armorsaves.remove(p.getName());
				YamlConfiguration dc = new YamlConfiguration();
			    File sv = new File(Bukkit.getPluginManager().getPlugin("Bending").getDataFolder(), "Armour.sav");
				 if (!sv.exists())
				    {
				      Bukkit.getPluginManager().getPlugin("Bending").getDataFolder().mkdirs();
				      try
				      {
				        sv.createNewFile();
				      }
				      catch (IOException e)
				      {
				        e.printStackTrace();
				      }
				    }
				      dc.set("Armors." + p.getName(), "");
				 try
				    {
				      dc.save(sv);
				    }
				    catch (IOException ex)
				    {
				    }
		
	}
}
