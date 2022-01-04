import jorm.annotation.Table;
import jorm.annotation.Column;

@Table
public class Character {
    @Column
    private String name;
    @Column
    private int level;

    public Character() {
        this.name = "";
        this.level = 0;
    }

    public Character(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}