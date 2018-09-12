package selab.hanyang.ac.kr.platformmanager.controller;
import com.google.gson.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// 리퀘스트 편하게 쓰기 위한 클래스.
// 다른 클래스에서 Gson이나 JsonObject 신경 안쓰게 하기 위한 클래스.
class RequestParser{

    private Gson gson;
    private JsonElement json;

    public RequestParser(HttpServletRequest request){
        this.gson = new GsonBuilder().create();
        setRequest(request);
    }

    public RequestParser(String request){
        this.gson = new GsonBuilder().create();
        setRequest(request);
    }

    // request 설정
    public void setRequest(HttpServletRequest request){
        try {
            String requestText = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            System.out.println("Request Text : "+requestText);
            json = gson.fromJson(requestText, JsonElement.class);
            System.out.println("Parsed : "+json.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // request 설정
    public void setRequest(String request){
        String requestText = request;
        json = gson.fromJson(requestText, JsonElement.class);
    }

    // get full JsonElement
    public JsonElement get(){
        return json;
    }

    // get full JsonObject
    public JsonObject getAsJsonObject(){
        return json.getAsJsonObject();
    }

    // get full JsonArray
    public JsonArray getAsJsonArray(){
        return json.getAsJsonArray();
    }

    // get JsonElement with key
    public JsonElement get(String key){
        return getAsJsonObject().get(key);
    }

    // get JsonObject with key
    public JsonObject getAsJsonObject(String key){
        return get(key).getAsJsonObject();
    }

    // get JsonArray with key
    public JsonArray getAsJsonArray(String key){
        return get(key).getAsJsonArray();
    }

    // get String value
    public String getAsString(String key){
        return get(key) == null ? null : get(key).getAsString();
    }

    // get Int value
    public int getAsInt(String key){
        return get(key) == null ? null : get(key).getAsInt();
    }

    // map Element to Object
    public static List<JsonObject> mapToObject(JsonArray array){
        List<JsonObject> list = new ArrayList<>();
        array.forEach(e -> list.add(e.getAsJsonObject()));
        return list;
    }


}