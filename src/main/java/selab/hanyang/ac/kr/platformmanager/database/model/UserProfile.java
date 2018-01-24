package selab.hanyang.ac.kr.platformmanager.database.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user_profile")
public class UserProfile {

    @Id
    @Column(name = "user_id")
    private String userId;

    @OneToOne
    @PrimaryKeyJoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "role")
    private String role;


}
