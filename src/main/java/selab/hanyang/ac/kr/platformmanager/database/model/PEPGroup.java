package selab.hanyang.ac.kr.platformmanager.database.model;

import javax.persistence.*;

@Entity
@Table(name = "pep_group")
public class PEPGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pep_group_id")
    private long pepGroupID;

    @Column(name = "pep_group_name")
    private String pepGroupName;

    @Column(name = "group_pw")
    private String groupPW;

    @ManyToOne
    @JoinColumn(name = "owner")
    private User owner;

    public long getPepGroupID() {
        return pepGroupID;
    }

    public String getPepGroupName() {
        return pepGroupName;
    }

    public String getGroupPW() {
        return groupPW;
    }

    public User getOwner() {
        return owner;
    }
}
