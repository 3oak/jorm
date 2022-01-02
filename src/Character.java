import dam.annotation.Column;
import dam.annotation.Table;

@Table
public class Character {
    @Column
    private String name;
    @Column
    private int x;
}