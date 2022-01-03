import jorm.annotation.Table;
import jorm.annotation.Column;

@Table
public class Character {
    @Column
    private String name;
    @Column
    private int level;

    public Character(){}

    public Character(String name, int level){
        this.name = name;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }
}