package rafa.tfg.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "champions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Champion {

    @Id
    private String id;

    @Column(name = "champion_key")
    private String key;

    private String name;
    private String title;

    @Column(length = 1000)
    private String blurb;

    private String version;

    @Column(name = "par_type")
    private String partype;

    // Info stats
    private Integer attackInfo;
    private Integer defenseInfo;
    private Integer magicInfo;
    private Integer difficultyInfo;

    // Tags - stored as comma-separated string
    private String tags;

    // Stats
    private Double hp;
    private Double hpperlevel;
    private Double mp;
    private Double mpperlevel;
    private Double movespeed;
    private Double armor;
    private Double armorperlevel;
    private Double spellblock;
    private Double spellblockperlevel;
    private Integer attackrange;
    private Double hpregen;
    private Double hpregenperlevel;
    private Double mpregen;
    private Double mpregenperlevel;
    private Double attackdamage;
    private Double attackdamageperlevel;
    private Double attackspeed;
    private Double attackspeedperlevel;
}
