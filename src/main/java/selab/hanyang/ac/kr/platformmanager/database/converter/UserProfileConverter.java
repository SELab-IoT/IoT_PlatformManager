package selab.hanyang.ac.kr.platformmanager.database.converter;

import com.google.gson.Gson;
import selab.hanyang.ac.kr.platformmanager.database.model.UserProfile;

import javax.persistence.AttributeConverter;

public class UserProfileConverter implements AttributeConverter<UserProfile, String>{

    @Override
    public String convertToDatabaseColumn(UserProfile userProfile) {
        Gson parser = new Gson();
        return parser.toJson(userProfile, UserProfile.class);
    }

    @Override
    public UserProfile convertToEntityAttribute(String s) {
        Gson parser = new Gson();
        return parser.fromJson(s, UserProfile.class);
    }
}
