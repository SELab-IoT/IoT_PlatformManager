package selab.hanyang.ac.kr.platformmanager.database.model;

import javax.persistence.*;

@Entity
@Table(name = "pep")
public class PEP {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int _id;

    @Column(name = "pep_id", nullable = false)
    private String pepId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "policy_id")
    private Policy policy;

    public int getId() {
        return _id;
    }
}
