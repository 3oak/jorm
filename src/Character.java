import jorm.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Table
public class Character {
    @Column
    @PrimaryKey
    private String name;
    @Column
    private int level;
    @Column
    @Temporal(value = TemporalType.DATE)
    private Date createdOn;

    @OneToMany
    public List<Skill> skill;

    @OneToOne
    public Weapon weapon;

    public Character() {

    }

    public Character(String name, int level) {
        this.name = name;
        this.level = level;
        this.createdOn = null;
    }

    public Character(String name, int level, Date createdOn) {
        this.name = name;
        this.level = level;
        this.createdOn = createdOn;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public Date getCreatedOn() {
        return createdOn;
    }
}