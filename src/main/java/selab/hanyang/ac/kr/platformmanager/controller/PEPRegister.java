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
import selab.hanyang.ac.kr.platformmanager.database.model.GroupMember;
import selab.hanyang.ac.kr.platformmanager.database.model.PEP;
import selab.hanyang.ac.kr.platformmanager.database.model.PEPGroup;
import selab.hanyang.ac.kr.platformmanager.database.model.User;
import selab.hanyang.ac.kr.platformmanager.database.repository.GroupMemberRepository;
import selab.hanyang.ac.kr.platformmanager.database.repository.PEPGroupRepository;
import selab.hanyang.ac.kr.platformmanager.database.repository.PEPRepository;
import selab.hanyang.ac.kr.platformmanager.database.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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


    @RequestMapping(name = "/groups", method = RequestMethod.POST)
    public @ResponseBody String addPEPtoPEPGroup(HttpServletRequest request) {
        RequestParser parser = new RequestParser(request);
        JsonObject object = parser.getAsJsonObject();
        String userId = object.get("userID").getAsString();
        String pepId = object.get("pepID").getAsString();
        User user = userRepository.findOne(userId);
        PEP pep = pepRepository.findOneByPepId(pepId);
        Gson gson = new GsonBuilder().create();
        JsonObject response = new JsonObject();
        if (user == null) {
            response.addProperty("error", "Not found user");
        } else if (pep == null) {
            response.addProperty("error", "Not found pep");
        } else if (object.get("pepGroupPW") != null) {
            String pepGroupName = object.get("pepGroupName").getAsString();
            String pepGroupPW = object.get("pepGroupPW").getAsString();
            PEPGroup pepGroup = new PEPGroup(pepGroupName, pepGroupPW, user);
            pepGroupRepository.saveAndFlush(pepGroup);
            pep.setPepGroup(pepGroup);
            pepRepository.saveAndFlush(pep);
            GroupMember groupMember = new GroupMember(user, pepGroup);
            groupMemberRepository.saveAndFlush(groupMember);
            response.addProperty("pepGroupId",pepGroup.getPepGroupID());
            response.addProperty("pepGroupName", pepGroup.getPepGroupName());
            response.addProperty("owner", pepGroup.getOwner().getUserId());
            List<GroupMember> groupMembers = groupMemberRepository.findByPepGroup(pepGroup);
            response.addProperty("members", gson.toJson(groupMembers.stream().map(groupMember1 -> groupMember1.getUser().getUserId()).toArray()));
        } else {
            long pepGroupId = object.get("pepGroupID").getAsLong();
            PEPGroup pepGroup = pepGroupRepository.findOne(pepGroupId);
            pep.setPepGroup(pepGroup);
            pepRepository.saveAndFlush(pep);
            response.addProperty("pepGroupId",pepGroup.getPepGroupID());
            response.addProperty("pepGroupName", pepGroup.getPepGroupName());
            response.addProperty("owner", pepGroup.getOwner().getUserId());
            List<GroupMember> groupMembers = groupMemberRepository.findByPepGroup(pepGroup);
            response.addProperty("members", gson.toJson(groupMembers.stream().map(groupMember1 -> groupMember1.getUser().getUserId()).toArray()));
        }

        return gson.toJson(response);
    }

    @RequestMapping(name = "/groups/{userID}/{pepID}", method = RequestMethod.GET)
    public @ResponseBody String searchPEPGroup(@PathVariable String userID, @PathVariable String pepID) {
        Gson gson = new GsonBuilder().create();
        User user = userRepository.findOne(userID);
        PEP pep = pepRepository.findOne(pepID);
        JsonObject response = new JsonObject();
        if (user == null) {
            response.addProperty("error", "wrong user");
        } else if (pep.getPepGroup() != null) {
            PEPGroup pepGroup = pepGroupRepository.findByOwnerAndPEP(user, pep);
            response.addProperty("hasGroup", true);
            response.addProperty("pepGroup", pepGroup.getPepGroupID());
        } else {
            response.addProperty("hasGroup", false);
            List<PEPGroup> pepGroups = pepGroupRepository.findPEPGroupsByOwner(user);
            response.addProperty("pepGroupID", gson.toJson(pepGroups.stream().map(pepGroup -> pepGroup.getPepGroupID()).toArray()));
        }
        return gson.toJson(response);
    }



}
