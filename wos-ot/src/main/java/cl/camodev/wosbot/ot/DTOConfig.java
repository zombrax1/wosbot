package cl.camodev.wosbot.ot;

public class DTOConfig {
        private Long profileId; // profile identifier
        private String key;
        private String value;

        public DTOConfig(Long profileId, String key, String value) {
                this.profileId = profileId;
                this.key = key;
                this.value = value;
        }

        // Getters and setters

        public Long getProfileId() {
                return profileId;
        }

        public String getKey() {
                return key;
        }

        public String getValue() {
                return value;
        }

        public void setProfileId(Long profileId) {
                this.profileId = profileId;
        }

        public void setKey(String key) {
                this.key = key;
        }

        public void setValue(String value) {
                this.value = value;
        }
}
