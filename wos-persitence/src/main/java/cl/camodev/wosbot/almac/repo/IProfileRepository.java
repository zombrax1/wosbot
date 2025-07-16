package cl.camodev.wosbot.almac.repo;

import java.util.List;

import cl.camodev.wosbot.almac.entity.Config;
import cl.camodev.wosbot.almac.entity.Profile;
import cl.camodev.wosbot.ot.DTOProfiles;

public interface IProfileRepository {
    List<DTOProfiles> getProfiles();
    boolean addProfile(Profile profile);
    boolean saveProfile(Profile profile);
    boolean deleteProfile(Profile profile);
    Profile getProfileById(Long id);
    List<Config> getProfileConfigs(Long profileId);
    boolean deleteConfigs(List<Config> configs);
    boolean saveConfigs(List<Config> configs);
}
