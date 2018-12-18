package selab.hanyang.ac.kr.platformmanager.database.model;

import selab.hanyang.ac.kr.platformmanager.database.converter.UserProfileConverter;

import javax.persistence.*;

@Entity
@Table(name = "user")
public class User {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_pw")
    private String userPW;

    @Column(name = "profile")
    @Convert(converter = UserProfileConverter.class)
    private UserProfile userProfile;

    public String getUserId() {
        return userId;
    }

}
