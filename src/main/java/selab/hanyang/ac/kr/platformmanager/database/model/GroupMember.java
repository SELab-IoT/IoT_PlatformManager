package selab.hanyang.ac.kr.platformmanager.database.model;

import javax.persistence.*;

@Entity @IdClass(GroupMemberId.class)
@Table(name = "group_member")
public class GroupMember {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "pep_group_id")
    private PEPGroup pepGroup;
}
