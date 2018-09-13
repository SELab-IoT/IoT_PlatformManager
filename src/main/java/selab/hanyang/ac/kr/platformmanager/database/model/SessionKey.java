package selab.hanyang.ac.kr.platformmanager.database.model;

import javax.persistence.*;

@Entity @IdClass(SessionKeyId.class)
@Table(name = "session_key")
public class SessionKey{

    @Id
    @Column
    public String pepId;

    @Id
    @Column
    public String sessionKey;

    public SessionKey() {
    }

    public SessionKey(String pepId, String sessionKey) {
        this.pepId = pepId;
        this.sessionKey = sessionKey;
    }
}
