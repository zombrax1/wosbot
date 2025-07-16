package cl.camodev.wosbot.almac.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "config")
public class Config {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Profile profile;

    @ManyToOne
    private TpConfig tpConfig;

    private String key;
    private String valor;

    public Config() {}

    public Config(Profile profile, TpConfig tpConfig, String key, String valor) {
        this.profile = profile;
        this.tpConfig = tpConfig;
        this.key = key;
        this.valor = valor;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Profile getProfile() { return profile; }
    public void setProfile(Profile profile) { this.profile = profile; }
    public TpConfig getTpConfig() { return tpConfig; }
    public void setTpConfig(TpConfig tpConfig) { this.tpConfig = tpConfig; }
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }
}
