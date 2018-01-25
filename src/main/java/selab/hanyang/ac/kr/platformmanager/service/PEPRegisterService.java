package selab.hanyang.ac.kr.platformmanager.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import selab.hanyang.ac.kr.platformmanager.database.model.GroupMember;
import selab.hanyang.ac.kr.platformmanager.database.model.PEP;
import selab.hanyang.ac.kr.platformmanager.database.model.PEPGroup;
import selab.hanyang.ac.kr.platformmanager.database.model.User;
import selab.hanyang.ac.kr.platformmanager.database.repository.GroupMemberRepository;
import selab.hanyang.ac.kr.platformmanager.database.repository.PEPGroupRepository;
import selab.hanyang.ac.kr.platformmanager.database.repository.PEPRepository;
import selab.hanyang.ac.kr.platformmanager.database.repository.UserRepository;

import java.util.List;
import java.util.concurrent.Future;

@Service
public class PEPRegisterService {

    @Autowired
    PEPGroupRepository pepGroupRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PEPRepository pepRepository;

    @Autowired
    GroupMemberRepository groupMemberRepository;

    @Async
    public Future<JsonObject> addPEPtoPEPGroup(JsonObject object) {
        String userId = object.get("userID").getAsString();
        String pepId = object.get("pepID").getAsString();
        User user = userRepository.findOne(userId);
        PEP pep = pepRepository.findOneByPepId(pepId);
        JsonObject response = new JsonObject();
        if (user == null) {
            response.addProperty("error", "Not found user");
        } else if (pep == null) {
            response.addProperty("error", "Not found pep");
        } else if (pep.getPepGroup() != null) {
            addGroupMember(object, user, pep, response);
        } else if (object.get("pepGroupPW") != null) {
            createAndAddGroup(object, user, pep, response);
        } else {
            addGroup(object, pep, response);
        }
        return new AsyncResult<>(response);
    }

    @Async
    public Future<JsonObject> searchPEPGroup(String userID, String pepID) {
        User user = userRepository.findOne(userID);
        PEP pep = pepRepository.findOne(pepID);
        Gson gson = new GsonBuilder().create();
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

        return new AsyncResult<>(response);
    }

    //TODO: 세션키 확인 후 PEPProfile 저장
    @Async
    public Future<JsonObject> savePEPProfile(JsonObject request) {
        String sessionKey = request.get("sessionKey").getAsString();
        String pepID = request.get("pepID").getAsString();
        String pepProfile = request.get("pepProfile").getAsString();
        JsonObject response = new JsonObject();

        Gson gson = new GsonBuilder().create();
        JsonObject pepProfileJson = gson.fromJson(pepProfile, JsonObject.class);
        PEP pep = pepRepository.findOneByPepId(pepID);
        pep.setIp(pepProfileJson.get("ip").getAsString());
        JsonArray deviceProfiles = pepProfileJson.getAsJsonArray("deviceProfiles");

        //TODO: deviceProfiles 저장 (DeviceRegister 완료 후 수정)

        return new AsyncResult<>(response);
    }

    private PEPGroup createPEPGroup(User user, String pepGroupName, String pepGroupPW) {
        PEPGroup pepGroup = new PEPGroup(pepGroupName, pepGroupPW, user);
        pepGroupRepository.saveAndFlush(pepGroup);
        return pepGroup;
    }

    private void addGroup(JsonObject object, PEP pep, JsonObject response) {
        long pepGroupId = object.get("pepGroupID").getAsLong();
        PEPGroup pepGroup = pepGroupRepository.findOne(pepGroupId);
        pep.setPepGroup(pepGroup);
        pepRepository.saveAndFlush(pep);
        returnSessionKey(response);
    }

    //TODO: 실제 세션키로 변경 필요
    private void returnSessionKey(JsonObject response) {
        response.addProperty("sessionKey", "asdf");
    }

    private void createAndAddGroup(JsonObject object, User user, PEP pep, JsonObject response) {
        String pepGroupName = object.get("pepGroupName").getAsString();
        String pepGroupPW = object.get("pepGroupPW").getAsString();
        PEPGroup pepGroup = createPEPGroup(user, pepGroupName, pepGroupPW);
        pep.setPepGroup(pepGroup);
        pepRepository.saveAndFlush(pep);
        GroupMember groupMember = new GroupMember(user, pepGroup);
        groupMemberRepository.saveAndFlush(groupMember);
        returnSessionKey(response);
    }

    private void addGroupMember(JsonObject object, User user, PEP pep, JsonObject response) {
        String pepGroupPW = object.get("pepGroupPW").getAsString();
        if (checkPEPGroupPW(pep.getPepGroup(), pepGroupPW)) {
            GroupMember groupMember = new GroupMember(user, pep.getPepGroup());
            groupMemberRepository.saveAndFlush(groupMember);
            response.addProperty("authenticated", true);
        } else {
            response.addProperty("authenticated", false);
        }
    }

    //TODO: 비밀번호 인증 방법 변경 필요
    private boolean checkPEPGroupPW(PEPGroup pepGroup, String pepGroupPW) {
        return pepGroup.getGroupPW().equals(pepGroupPW);
    }
}
