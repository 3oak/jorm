import jorm.annotation.*;

import java.util.ArrayList;

@Table(name="characters")
public class Character {
    @Column(name="char_name")
    @PrimaryKey
    private String name;

    @OneToOne
    public Skill skill;

    @OneToMany
    public ArrayList<Weapon> weapons;

    public Character(String name, Skill skill, ArrayList<Weapon> weapons) {
        this.name = name;
        this.skill = skill;
        this.weapons = weapons;
    }

    public String getName() {
        return name;
    }

    public Skill getSkill() {
        return skill;
    }

    public ArrayList<Weapon> getWeapons() {
        return weapons;
    }

    @Override
    public String toString() {
        return "Character{" +
                "name='" + name + '\'' +
                ", skill=" + skill +
                ", weapons=" + weapons +
                '}';
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public void setWeapons(ArrayList<Weapon> weapons) {
        this.weapons = weapons;
    }
}