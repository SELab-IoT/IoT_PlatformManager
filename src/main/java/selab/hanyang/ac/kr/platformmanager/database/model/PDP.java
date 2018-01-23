package selab.hanyang.ac.kr.platformmanager.database.model;

import javax.persistence.*;

@Entity
@Table(name = "pdp")
public class PDP {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int _id;

    @Column(name = "name")
    private String name;

    public String getName() {
        return name;
    }
}
