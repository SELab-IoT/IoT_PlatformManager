package selab.hanyang.ac.kr.platformmanager.database.model;

import javax.persistence.*;

@Entity @IdClass(GroupMemberId.class)
@Table(name = "group_member")
public class GroupMember {

    public GroupMember() {
    }

    public GroupMember(User user, PEPGroup pepGroup) {
        this.user = user;
        this.pepGroup = pepGroup;
    }

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "pep_group_id")
    private PEPGroup pepGroup;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PEPGroup getPepGroup() {
        return pepGroup;
    }

    public void setPepGroup(PEPGroup pepGroup) {
        this.pepGroup = pepGroup;
    }
}
