package selab.hanyang.ac.kr.platformmanager.database.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "session_key")
public class SessionKey {

    @Column
    public String pepID;

    @Column
    public String sessionKey;

    public SessionKey(String pepID, String sessionKey) {
        this.pepID = pepID;
        this.sessionKey = sessionKey;
    }
}
