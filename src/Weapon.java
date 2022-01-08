import jorm.annotation.Column;
import jorm.annotation.ForeignKey;
import jorm.annotation.PrimaryKey;
import jorm.annotation.Table;

@Table(name="weapon")
public class Weapon {
    @Column(name="id")
    @PrimaryKey
    public String id;

    @Column(name="character_id")
    @ForeignKey(tableName = "characters")
    public String characterId;

    public Weapon() {
        
    }

    public Weapon(String id, String characterId) {
        this.id = id;
        this.characterId = characterId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCharacterId() {
        return characterId;
    }

    public void setCharacterId(String characterId) {
        this.characterId = characterId;
    }

    @Override
    public String toString() {
        return "Weapon{" +
                "id='" + id + '\'' +
                ", characterId='" + characterId + '\'' +
                '}';
    }
}
