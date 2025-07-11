package cl.camodev.wosbot.console.enumerable;

public enum TpDailyTaskEnum {
	// Enumeración de tareas diarias
	//@formatter:off
	 	HERO_RECRUITMENT(1, "Hero Recruitment",      	EnumConfigurationKey.BOOL_HERO_RECRUITMENT),
	    NOMADIC_MERCHANT(2, "Nomadic Merchant",       	EnumConfigurationKey.BOOL_NOMADIC_MERCHANT),
	    WAR_ACADEMY_SHARDS(3, "War Academy Shards",  	EnumConfigurationKey.BOOL_WAR_ACADEMY_SHARDS),
	    CRYSTAL_LABORATORY(4, "Crystal Laboratory",  	EnumConfigurationKey.BOOL_CRYSTAL_LAB_FC),
	    VIP_POINTS(5, "VIP Points",                   	EnumConfigurationKey.BOOL_VIP_POINTS),
	    PET_ADVENTURE(6, "Pet Adventure",             	EnumConfigurationKey.PET_PERSONAL_TREASURE_BOOL),
	    EXPLORATION_CHEST(7, "Exploration Chest",       EnumConfigurationKey.BOOL_EXPLORATION_CHEST),

	    LIFE_ESSENCE(9, "Life Essence",                	EnumConfigurationKey.LIFE_ESSENCE_BOOL),
	    LIFE_ESSENCE_CARING(10, "Life Essence Caring",  EnumConfigurationKey.ALLIANCE_LIFE_ESSENCE_BOOL),

	    MAIL_REWARDS(30, "Mail Rewards",               	EnumConfigurationKey.MAIL_REWARDS_BOOL),
	    DAILY_MISSIONS(31, "Daily Missions",           	EnumConfigurationKey.DAILY_MISSION_BOOL),
	    STOREHOUSE_CHEST(32, "Storehouse Chest",       	EnumConfigurationKey.STOREHOUSE_CHEST_BOOL),
	    INTEL(33, "Intel",                             	EnumConfigurationKey.INTEL_BOOL),
	    STOREHOUSE_STAMINA(34, "Storehouse Stamina",   	EnumConfigurationKey.STOREHOUSE_STAMINA_BOOL),

	    ALLIANCE_AUTOJOIN(40, "Alliance Autojoin",     	EnumConfigurationKey.ALLIANCE_AUTOJOIN_BOOL),
	    ALLIANCE_HELP(41, "Alliance Help",             	EnumConfigurationKey.ALLIANCE_HELP_REQUESTS_BOOL),
	    ALLIANCE_TECH(42, "Alliance Tech",             	EnumConfigurationKey.ALLIANCE_TECH_BOOL),
	    ALLIANCE_PET_TREASURE(43, "Alliance Pet Treasure", EnumConfigurationKey.ALLIANCE_PET_TREASURE_BOOL),
	    ALLIANCE_CHESTS(44, "Alliance Chests",         EnumConfigurationKey.ALLIANCE_CHESTS_BOOL),
	    ALLIANCE_TRIUMPH(45, "Alliance Triumph",       EnumConfigurationKey.ALLIANCE_TRIUMPH_BOOL),

	    PET_SKILL_STAMINA(51, "Pet Skill Stamina",     EnumConfigurationKey.PET_SKILL_STAMINA_BOOL),
	    PET_SKILL_FOOD(52, "Pet Skill Food",           EnumConfigurationKey.PET_SKILL_FOOD_BOOL),
	    PET_SKILL_TREASURE(53, "Pet Skill Treasure",   EnumConfigurationKey.PET_SKILL_TRESURE_BOOL),
	    PET_SKILL_GATHERING(54, "Pet Skill Gathering", EnumConfigurationKey.PET_SKILL_GATHERING_BOOL),

	    TRAINING_INFANTRY(61, "Training Infantry",     EnumConfigurationKey.TRAIN_INFANTRY_BOOL),
	    TRAINING_LANCER(62, "Training Lancer",         EnumConfigurationKey.TRAIN_LANCER_BOOL),
	    TRAINING_MARKSMAN(63, "Training Marksman",     EnumConfigurationKey.TRAIN_MARKSMAN_BOOL),

	    GATHER_BOOST(101, "Gather Speed Boost",       EnumConfigurationKey.GATHER_SPEED_BOOL),
	    GATHER_MEAT(102, "Gather Meat",               EnumConfigurationKey.GATHER_MEAT_BOOL),
	    GATHER_WOOD(103, "Gather Wood",               EnumConfigurationKey.GATHER_WOOD_BOOL),
	    GATHER_COAL(104, "Gather Stone",              EnumConfigurationKey.GATHER_COAL_BOOL),
	    GATHER_IRON(105, "Gather Iron",               EnumConfigurationKey.GATHER_IRON_BOOL),

	    CITY_UPGRADE_FURNACE(70, "City Upgrade Furnace", EnumConfigurationKey.CITY_UPGRADE_FURNACE_BOOL),

	    BANK(14, "Bank",                              null),
	    INITIALIZE(100, "Initialize",                  null);
//	CITY_UPGRADE_OTHERS(71, "City Upgrade Others"),
	
//	SHOP_MYSTERY(80, "Shop Mystery"),
    private final int id;
    private final String name;
    private final EnumConfigurationKey configKey;

    TpDailyTaskEnum(int id, String name, EnumConfigurationKey configKey) {
        this.id = id;
        this.name = name;
        this.configKey = configKey;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /** Clave que usará el perfil para saber si esta tarea está habilitada */
    public EnumConfigurationKey getConfigKey() {
        return configKey;
    }

    public static TpDailyTaskEnum fromId(int id) {
        for (TpDailyTaskEnum t : values()) {
            if (t.id == id) {
                return t;
            }
        }
        throw new IllegalArgumentException("No existe un TpDailyTaskEnum con id " + id);
    }
}
