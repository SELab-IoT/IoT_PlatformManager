package selab.hanyang.ac.kr.platformmanager.database.model;

import javax.persistence.*;

@Entity
@Table(name = "user")
public class User {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_pw")
    private String userPW;

}
