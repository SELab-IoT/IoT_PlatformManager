package selab.hanyang.ac.kr.platformmanager.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import selab.hanyang.ac.kr.platformmanager.database.repository.GroupMemberRepository;
import selab.hanyang.ac.kr.platformmanager.database.repository.PEPGroupRepository;
import selab.hanyang.ac.kr.platformmanager.database.repository.PEPRepository;
import selab.hanyang.ac.kr.platformmanager.database.repository.UserRepository;
import selab.hanyang.ac.kr.platformmanager.service.PEPRegisterService;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;

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


    @RequestMapping(value = "/groups", method = RequestMethod.POST)
    public @ResponseBody String addPEPtoPEPGroup(HttpServletRequest request) {
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



    @RequestMapping(value = "/groups/{userID}/{pepID}", method = RequestMethod.GET)
    public @ResponseBody String searchPEPGroup(@PathVariable String userID, @PathVariable String pepID) {
        Gson gson = new GsonBuilder().create();
        JsonObject response = null;
        try {
            response = pepRegisterService.searchPEPGroup(userID, pepID).get();
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
