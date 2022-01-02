import jorm.annotation.Table;
import jorm.annotation.Column;

@Table
public class Character {
    @Column
    private String name;
    @Column
    private int x;
}