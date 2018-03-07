package selab.hanyang.ac.kr.platformmanager.database.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity @IdClass(SessionKeyId.class)
@Table(name = "session_key")
public class SessionKey{

    @Id
    @Column
    public String pepID;

    @Id
    @Column
    public String sessionKey;

    public SessionKey(String pepID, String sessionKey) {
        this.pepID = pepID;
        this.sessionKey = sessionKey;
    }
}
