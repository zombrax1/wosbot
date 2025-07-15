package cl.camodev.wosbot.serv.task;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

import cl.camodev.wosbot.console.enumerable.TpDailyTaskEnum;
import cl.camodev.wosbot.ot.DTOProfiles;
import cl.camodev.wosbot.serv.task.impl.*;
import cl.camodev.wosbot.serv.task.impl.GatherTask.GatherType;

public class DelayedTaskRegistry {
	private static final Map<TpDailyTaskEnum, Function<DTOProfiles, DelayedTask>> registry = new EnumMap<>(TpDailyTaskEnum.class);

	static {
		registry.put(TpDailyTaskEnum.HERO_RECRUITMENT, profile -> new HeroRecruitmentTask(profile, TpDailyTaskEnum.HERO_RECRUITMENT));
		registry.put(TpDailyTaskEnum.NOMADIC_MERCHANT, profile -> new NomadicMerchantTask(profile, TpDailyTaskEnum.NOMADIC_MERCHANT));
		registry.put(TpDailyTaskEnum.WAR_ACADEMY_SHARDS, profile -> new WarAcademyTask(profile, TpDailyTaskEnum.WAR_ACADEMY_SHARDS));
		registry.put(TpDailyTaskEnum.CRYSTAL_LABORATORY, profile -> new CrystalLaboratoryTask(profile, TpDailyTaskEnum.CRYSTAL_LABORATORY));
		registry.put(TpDailyTaskEnum.VIP_POINTS, profile -> new VipTask(profile, TpDailyTaskEnum.VIP_POINTS));
		registry.put(TpDailyTaskEnum.PET_ADVENTURE, profile -> new PetAdventureChestTask(profile, TpDailyTaskEnum.PET_ADVENTURE));
		registry.put(TpDailyTaskEnum.EXPLORATION_CHEST, profile -> new ExplorationTask(profile, TpDailyTaskEnum.EXPLORATION_CHEST));
		registry.put(TpDailyTaskEnum.LIFE_ESSENCE, profile -> new LifeEssenceTask(profile, TpDailyTaskEnum.LIFE_ESSENCE));
		registry.put(TpDailyTaskEnum.LIFE_ESSENCE_CARING, profile -> new LifeEssenceCaringTask(profile, TpDailyTaskEnum.LIFE_ESSENCE_CARING));

		// gathering tasks
		registry.put(TpDailyTaskEnum.GATHER_MEAT, profile -> new GatherTask(profile, TpDailyTaskEnum.GATHER_MEAT, GatherType.MEAT));
		registry.put(TpDailyTaskEnum.GATHER_WOOD, profile -> new GatherTask(profile, TpDailyTaskEnum.GATHER_WOOD, GatherType.WOOD));
		registry.put(TpDailyTaskEnum.GATHER_COAL, profile -> new GatherTask(profile, TpDailyTaskEnum.GATHER_COAL, GatherType.COAL));
		registry.put(TpDailyTaskEnum.GATHER_IRON, profile -> new GatherTask(profile, TpDailyTaskEnum.GATHER_IRON, GatherType.IRON));
		registry.put(TpDailyTaskEnum.BANK, profile -> new BankTask(profile, TpDailyTaskEnum.BANK));
//		registry.put(TpDailyTaskEnum.BEAST_SLAY, profile -> new BeastSlayTask(profile, TpDailyTaskEnum.BEAST_SLAY));
		registry.put(TpDailyTaskEnum.GATHER_BOOST, profile -> new GatherSpeedTask(profile, TpDailyTaskEnum.GATHER_BOOST));

		// Recompensas diarias / bandeja
		registry.put(TpDailyTaskEnum.MAIL_REWARDS, profile -> new MailRewardsTask(profile, TpDailyTaskEnum.MAIL_REWARDS));
		registry.put(TpDailyTaskEnum.DAILY_MISSIONS, profile -> new DailyMissionTask(profile, TpDailyTaskEnum.DAILY_MISSIONS));
		registry.put(TpDailyTaskEnum.STOREHOUSE_CHEST, profile -> new StorehouseChest(profile, TpDailyTaskEnum.STOREHOUSE_CHEST));
		registry.put(TpDailyTaskEnum.INTEL, profile -> new IntelligenceTask(profile, TpDailyTaskEnum.INTEL));
		registry.put(TpDailyTaskEnum.STOREHOUSE_STAMINA, profile -> new DailyStaminaTask(profile, TpDailyTaskEnum.STOREHOUSE_STAMINA));

		// alliance tasks
		registry.put(TpDailyTaskEnum.ALLIANCE_AUTOJOIN, profile -> new AllianceAutojoinTask(profile, TpDailyTaskEnum.ALLIANCE_AUTOJOIN));
		registry.put(TpDailyTaskEnum.ALLIANCE_HELP, profile -> new AllianceHelpTask(profile, TpDailyTaskEnum.ALLIANCE_HELP));
		registry.put(TpDailyTaskEnum.ALLIANCE_TECH, profile -> new AllianceTechTask(profile, TpDailyTaskEnum.ALLIANCE_TECH));
		registry.put(TpDailyTaskEnum.ALLIANCE_PET_TREASURE, profile -> new PetAllianceTreasuresTask(profile, TpDailyTaskEnum.ALLIANCE_PET_TREASURE));
		registry.put(TpDailyTaskEnum.ALLIANCE_CHESTS, profile -> new AllianceChestTask(profile, TpDailyTaskEnum.ALLIANCE_CHESTS));
		registry.put(TpDailyTaskEnum.ALLIANCE_TRIUMPH, profile -> new TriumphTask(profile, TpDailyTaskEnum.ALLIANCE_TRIUMPH));

		// pet skills tasks
		registry.put(TpDailyTaskEnum.PET_SKILL_STAMINA, profile -> new PetSkillsTask(profile, TpDailyTaskEnum.PET_SKILL_STAMINA, PetSkillsTask.PetSkill.STAMINA));
		registry.put(TpDailyTaskEnum.PET_SKILL_FOOD, profile -> new PetSkillsTask(profile, TpDailyTaskEnum.PET_SKILL_FOOD, PetSkillsTask.PetSkill.FOOD));
		registry.put(TpDailyTaskEnum.PET_SKILL_TREASURE, profile -> new PetSkillsTask(profile, TpDailyTaskEnum.PET_SKILL_TREASURE, PetSkillsTask.PetSkill.TREASURE));
		registry.put(TpDailyTaskEnum.PET_SKILL_GATHERING, profile -> new PetSkillsTask(profile, TpDailyTaskEnum.PET_SKILL_GATHERING, PetSkillsTask.PetSkill.GATHERING));

		// training troops tasks
		registry.put(TpDailyTaskEnum.TRAINING_INFANTRY, profile -> new TrainingTroopsTask(profile, TpDailyTaskEnum.TRAINING_INFANTRY, TrainingTroopsTask.TroopType.INFANTRY));
		registry.put(TpDailyTaskEnum.TRAINING_LANCER, profile -> new TrainingTroopsTask(profile, TpDailyTaskEnum.TRAINING_LANCER, TrainingTroopsTask.TroopType.LANCER));
		registry.put(TpDailyTaskEnum.TRAINING_MARKSMAN, profile -> new TrainingTroopsTask(profile, TpDailyTaskEnum.TRAINING_MARKSMAN, TrainingTroopsTask.TroopType.MARKSMAN));

		// Mejora de ciudad
		registry.put(TpDailyTaskEnum.CITY_UPGRADE_FURNACE, profile -> new UpgradeFurnaceTask(profile, TpDailyTaskEnum.CITY_UPGRADE_FURNACE));
//		registry.put(TpDailyTaskEnum.CITY_UPGRADE_OTHERS, profile -> new ArenaTask(profile, TpDailyTaskEnum.CITY_UPGRADE_OTHERS));
		registry.put(TpDailyTaskEnum.CITY_SURVIVORS, profile -> new NewSurvivorsTask(profile, TpDailyTaskEnum.CITY_SURVIVORS));
		// Inicializar
		registry.put(TpDailyTaskEnum.INITIALIZE, profile -> new InitializeTask(profile, TpDailyTaskEnum.INITIALIZE));
	}

	/**
	 * Creates a new instance of {@link DelayedTask} based on the provided task type and profile.
	 */
	public static DelayedTask create(TpDailyTaskEnum type, DTOProfiles profile) {
		Function<DTOProfiles, DelayedTask> factory = registry.get(type);
		if (factory == null) {
			throw new IllegalArgumentException("No factory registered for task type: " + type);
		}
		return factory.apply(profile);
	}
}
