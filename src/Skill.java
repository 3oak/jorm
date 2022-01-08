import jorm.annotation.*;

@Table(name="skills")
public class Skill {
    @Column(name="id")
    @PrimaryKey
    public String id;

    @Column(name="character_id")
    @ForeignKey(tableName = "characters")
    public String characterId;

    public Skill(String id, String characterId) {
        this.id = id;
        this.characterId = characterId;
    }

    public String getId() {
        return id;
    }

    public String getCharacterId() {
        return characterId;
    }

    @Override
    public String toString() {
        return "Skill{" +
                "id='" + id + '\'' +
                ", characterId='" + characterId + '\'' +
                '}';
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCharacterId(String characterId) {
        this.characterId = characterId;
    }
}
