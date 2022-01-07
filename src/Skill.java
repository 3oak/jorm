import jorm.annotation.*;

@Table
public class Skill {
    @Column
    @PrimaryKey
    public String id;

    @Column
    public int damage;

    @Column
    @ForeignKey(tableName = "Character")
    public String characterId;

    public Skill(String id, int damage, String characterId){
        this.id = id;
        this.damage = damage;
        this.characterId = characterId;
    }
}
