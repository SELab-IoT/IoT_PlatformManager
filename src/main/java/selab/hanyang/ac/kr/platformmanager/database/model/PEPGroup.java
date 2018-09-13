package selab.hanyang.ac.kr.platformmanager.database.model;

import javax.persistence.*;

@Entity
@Table(name = "pep_group")
public class PEPGroup {

    public PEPGroup(){}

    public PEPGroup(String pepGroupName, String groupPW, User owner) {
        this.pepGroupName = pepGroupName;
        this.groupPW = groupPW;
        this.owner = owner;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pep_group_id")
    private long pepGroupId;

    @Column(name = "pep_group_name")
    private String pepGroupName;

    @Column(name = "group_pw")
    private String groupPW;

    @ManyToOne
    @JoinColumn(name = "owner")
    private User owner;

    public long getPepGroupId() {
        return pepGroupId;
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
