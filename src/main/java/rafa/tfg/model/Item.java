package rafa.tfg.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    private String id;

    private String name;

    @Column(length = 2000)
    private String description;

    @Column(length = 500)
    private String plaintext;

    private String colloq;

    // Gold info
    private Integer goldBase;
    private Integer goldTotal;
    private Integer goldSell;
    private Boolean purchasable;

    // Tags - stored as comma-separated string
    private String tags;

    // Recipe info - stored as comma-separated strings
    private String buildsFrom;
    private String buildsInto;

    // Some key stats (you can expand this)
    private Double flatHPPoolMod;
    private Double flatMPPoolMod;
    private Double flatArmorMod;
    private Double flatSpellBlockMod;
    private Double flatPhysicalDamageMod;
    private Double flatMagicDamageMod;
    private Double percentAttackSpeedMod;
    private Double percentLifeStealMod;
    private Double flatMovementSpeedMod;
    private Double flatCritChanceMod;

    private Integer depth;
}
