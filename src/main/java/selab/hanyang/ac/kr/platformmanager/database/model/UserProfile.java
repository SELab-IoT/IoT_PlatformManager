package selab.hanyang.ac.kr.platformmanager.database.model;

import com.google.gson.JsonArray;

import javax.persistence.*;
import java.io.Serializable;

//TODO: toConvertable JSON

public class UserProfile {

    private String userName;
    private JsonArray role;

}
