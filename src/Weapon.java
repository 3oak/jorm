import jorm.annotation.Column;
import jorm.annotation.ForeignKey;
import jorm.annotation.Table;

@Table
public class Weapon {
    @Column
    public int damage;

    @Column
    @ForeignKey(tableName = "Character")
    public String characterId;
}
