import jorm.annotation.*;

import java.util.List;

@Table
public class Character {
    @Column
    @PrimaryKey
    private String name;
    @Column
    private int level;

    @OneToMany
    public Skill skill;

    @OneToOne
    public Weapon weapon;

    public Character() {

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
}