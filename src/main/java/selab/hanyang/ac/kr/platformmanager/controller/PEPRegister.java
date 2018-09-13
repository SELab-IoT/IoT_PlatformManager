package selab.hanyang.ac.kr.platformmanager.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import selab.hanyang.ac.kr.platformmanager.database.repository.GroupMemberRepository;
import selab.hanyang.ac.kr.platformmanager.database.repository.PEPGroupRepository;
import selab.hanyang.ac.kr.platformmanager.database.repository.PEPRepository;
import selab.hanyang.ac.kr.platformmanager.database.repository.UserRepository;
import selab.hanyang.ac.kr.platformmanager.service.PEPRegisterService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Controller
public class PEPRegister {

    @Autowired
    PEPGroupRepository pepGroupRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PEPRepository pepRepository;

    @Autowired
    GroupMemberRepository groupMemberRepository;

    @Autowired
    PEPRegisterService pepRegisterService;

    @CrossOrigin(origins = "http://localhost")
    @PostMapping(path = "/groups", consumes = "application/json", produces = "application/json")
    public @ResponseBody String addPEPtoPEPGroup(@RequestBody String request) {

        RequestParser parser = new RequestParser(request);
        JsonObject object = parser.getAsJsonObject();
        JsonObject response = null;
        Gson gson = new GsonBuilder().create();
        try {
            response = pepRegisterService.addPEPtoPEPGroup(object).get();
            return gson.toJson(response);
        } catch (InterruptedException e) {
            e.printStackTrace();
            response = new JsonObject();
            response.addProperty("error" , "ToJson error");
        } catch (ExecutionException e) {
            e.printStackTrace();
            response = new JsonObject();
            response.addProperty("error" , "ToJson error");
        }
        return gson.toJson(response);
    }


    @CrossOrigin(origins = "http://localhost")
    @GetMapping(path="/groups/{userId}/{pepId}", produces = "application/json")
    public @ResponseBody String searchPEPGroup(@PathVariable String userId, @PathVariable String pepId) {
        Gson gson = new GsonBuilder().create();
        JsonObject response = null;
        try {
            response = pepRegisterService.searchPEPGroup(userId, pepId).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            response = new JsonObject();
            response.addProperty("error" , "ToJson error");
        } catch (ExecutionException e) {
            e.printStackTrace();
            response = new JsonObject();
            response.addProperty("error" , "ToJson error");
        }
        return gson.toJson(response);
    }



}
