package selab.hanyang.ac.kr.platformmanager.controller;
import com.google.gson.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.stream.Collectors;

// 리퀘스트 편하게 쓰기 위한 클래스.
// 다른 클래스에서 Gson이나 JsonObject 신경 안쓰게 하기 위한 클래스.
class RequestParser{

    private Gson gson;
    private JsonObject json;

    public RequestParser(HttpServletRequest request){
        this.gson = new GsonBuilder().create();
        setRequest(request);
    }

    // request 설정
    public void setRequest(HttpServletRequest request){
        try {
            String requestText = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            json = gson.fromJson(requestText, JsonObject.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // get full JsonElement
    public JsonElement get(){
        return json;
    }

    // get full JsonElement as JsonArray
    public JsonArray getAsJsonArray(){
        return json.getAsJsonArray();
    }

    // get JsonElement with key
    public JsonElement get(String key){
        return json.get(key);
    }

    // get String value
    public String getAsString(String key){
        return get(key) == null ? null : get(key).getAsString();
    }

    // get Int value
    public int getAsInt(String key){
        return get(key) == null ? null : get(key).getAsInt();
    }


}