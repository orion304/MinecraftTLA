package main;

import java.util.List;

public class ConfigValues {

	// Chat Values
	public static boolean ChatEnabled;
	public static boolean ChatColorsEnabled;
	public static boolean ChatCompatibility;
	public static String ChatFormat;
	public static String AvatarPrefix;
	public static String AirPrefix;
	public static String WaterPrefix;
	public static String EarthPrefix;
	public static String FirePrefix;
	public static String ChiPrefix;
	public static String AvatarColor;
	public static String AirColor;
	public static String FireColor;
	public static String EarthColor;
	public static String WaterColor;
	public static String ChiColor;
	
	// Options
	public static boolean BendToItem;
	public static boolean UseTagAPI;
	public static int SeaLevel;
	
	// Abilities
	public static boolean PierceArmor;
	public static int GlobalCooldown;
	
	//AvatarState
	public static int AvatarStatePowerFactor;
	public static boolean AvatarStateRegenerationEnabled;
	public static int AvatarStateRegenerationPower;
	public static boolean AvatarStateResistanceEnabled;
	public static int AvatarStateResistancePower;
	public static boolean AvatarStateFireResistanceEnabled;
	public static int AvatarStateFireResistancePower;
	public static boolean AvatarStateSpeedEnabled;
	public static int AvatarStateSpeedPower;
	//Air
	//Settings
	public static boolean AirBendWithWeapons;
	//AirBlast
	public static double AirBlastSpeed;
	public static double AirBlastRange;
	public static double AirBlastRadius;
	public static double AirBlastPush;
	
	//AirBubble
	public static int AirBubbleRadius;
	
	//AirBurst
	public static double AirBurstPushFactor;
	public static int AirBurstChargeTime;
	
	//AirScooter
	public static double AirScooterSpeed;
	public static double AirScooterRadius;
	
	//AirShield
	public static double AirShieldRadius;
	
	//AirSpout
	public static double AirSpoutHeight;
	
	//AirSuction
	public static double AirSuctionSpeed;
	public static double AirSuctionRange;
	public static double AirSuctionRadius;
	public static double AirSuctionPush;
	
	//AirSwipe
	public static double AirSwipeDamage;
	public static double AirSwipeRadius;
	public static double AirSwipePush;
	public static double AirSwipeRange;
	public static int AirSwipeArcSize;
	public static double AirSwipeSpeed;
	public static int AirSwipeCooldown;
	
	//Air Passive
	public static double AirPassiveFactor;
	
	// Tornado
	public static double TornadoRadius;
	public static double TornadoHeight;
	public static double TornadoRange;
	public static double TornadoMobPush;
	public static double TornadoPlayerPush;
	
	//Water
	//Settings
	public static double WaterNightPowerFactor;
	public static boolean WaterBendWithWeapons;
	//Bloodbending
	public static double BloodbendingThrowFactor;
	public static int BloodbendingRange;
	
	//FastSwimming
	public static double FastSwimmingFactor;
	
	//PhaseChange
	public static int PhaseChangeRange;
	public static int PhaseChangeRadius;
	
	//HealingWaters
	public static double HealingWatersRadius;
	public static int HealingWatersInterval;
	
	//IceSpike
	public static int IceSpikeHeight;
	public static int IceSpikeRange;
	public static int IceSpikeCooldown;
	public static int IceSpikeDamage;
	public static double IceSpikeThrowingMult;
	
	//OctopusForm
	public static int OctopusFormRange;
	public static int OctopusFormRadius;
	public static int OctopusFormInterval;
	public static int OctopusFormDamage;
	
	//Plantbending
	public static int PlantbendingRegrowthTime;
	
	//Surge
	public static double SurgeWaveRadius;
	public static double SurgeWaveHorizontalPush;
	public static double SurgeWaveVerticalPush;
	
	public static double SurgeWallRange;
	public static double SurgeWallRadius;
	
	//Torrent
	public static int TorrentRange;
	public static int TorrentRadius;
	public static int TorrentDamage;
	public static int TorrentDeflectDamage;
	public static int TorrentFactor;
	
	public static int SpiritBendingTime;
	
	//WaterManipulation
	public static double WaterManipulationRange;
	public static double WaterManipulationPush;
	public static int WaterManipulationDamage;
	public static double WaterManipulationSpeed;
	
	//WaterSpout
	public static int WaterSpoutHeight;
	
	//WaterBubble
	public static int WaterBubbleRadius;
	
	//Earth
	//Settings
	public static boolean ReverseEarthbending;
	public static int ReverseEarthbendingCheckTime;
	public static List<String> EarthBendable;
	public static boolean EarthBendWithWeapons;
	
	//Catapult
	public static int CatapultLength;
	public static double CatapultSpeed;
	public static double CatapultPush;
	
	//Collapse
	public static int CollapseRange;
	public static double CollapseRadius;
	
	//EarthArmor
	public static int EarthArmorDuration;
	public static int EarthArmorStrength;
	public static int EarthArmorCooldown;
	
	//EarthBlast
	public static boolean EarthBlastHitSelf;
	public static double EarthBlastRange;
	public static double EarthBlastPrepareRange;
	public static boolean EarthBlastRevert;
	public static int EarthBlastDamage;
	public static double EarthBlastSpeed;
	public static double EarthBlastPush;
	
	//EarthGrab
	public static double EarthGrabRange;
	
	//EarthTunnel
	public static double EarthTunnelRadius;
	public static int EarthTunnelMaxRadius;
	public static double EarthTunnelRange;
	public static boolean EarthTunnelRevert;
	public static int EarthTunnelInterval;
	
	//Passive
	public static int EarthPassiveWaitBeforeRevert;
	
	//RaiseEarth
	public static int RaiseEarthHeight;
	public static int RaiseEarthRange;
	public static int RaiseEarthWidth;
	
	//Shockwave
	public static int ShockwaveChargeTime;
	public static double ShockwaveDamage;
	public static int ShockwaveRadius;
	
	//Tremorsense
	public static int TremorsenseMaxDepth;
	public static int TremorsenseRadius;
	public static int TremorsenseLightThreshold;
	public static int TremorsenseCooldown;
	
	//Fire
	//Settings
	public static int FireDissipateTime;
	public static double FireDayPowerFactor;
	public static boolean FireBendWithWeapons;
	
	//Blaze
	public static int BlazeSize;
	public static int BlazeRange;
	
	//HeatControl
	public static int HeatControlRange;
	public static int HeatControlRadius;
	
	//FireBlast
	public static double FireBlastRadius;
	public static double FireBlastSpeed;
	public static double FireBlastPush;
	public static boolean FireBlastDissipates;
	public static int FireBlastDamage;
	public static double FireBlastRange;
	public static int FireBlastCooldown;
	
	//FireBurst
	public static int FireBurstDamage;
	public static int FireBurstChargeTime;
	
	//FireJet
	public static double FireJetSpeed;
	public static int FireJetDuration;
	public static int FireJetCooldown;
	
	//FireShield
	public static int FireShieldRadius;
	public static boolean FireShieldIgnites;
	
	//Illumination
	public static int IlluminationRange;
	
	//Lightning
	public static int LightningRange;
	public static int LightningWarmup;
	public static double LightningMissChance;
	
	//WallOfFire
	public static int WallOfFireRange;
	public static int WallOfFireHeight;
	public static int WallOfFireWidth;
	public static int WallOfFireDuration;
	public static int WallOfFireDamage;
	public static int WallOfFireCooldown;
	public static int WallOfFireInterval;
	
	//Chi
	//Settings
	public static int ChiBlockDuration;
	public static double ChiDodgeChance;
	public static double ChiPunchDamage;
	public static double ChiFallDamageReduction;
	public static boolean ChiBendWithWeapons;
	
	//HighJump
	public static int HighJumpHeight;
	public static int HighJumpCooldown;
	
	//Paralyze
	public static int ParalyzeCooldown;
	public static int ParalyzeDuration;
	
	//RapidPunch
	public static int RapidPunchDamage;
	public static int RapidPunchDistance;
	public static int RapidPunchPunches;
	public static int RapidPunchCooldown;
	
}
