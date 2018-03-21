package selab.hanyang.ac.kr.platformmanager.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import selab.hanyang.ac.kr.platformmanager.controller.DeviceRegister;
import selab.hanyang.ac.kr.platformmanager.controller.OTP;
import selab.hanyang.ac.kr.platformmanager.database.model.*;
import selab.hanyang.ac.kr.platformmanager.database.repository.*;

import java.util.LinkedList;
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

    @Autowired
    SessionKeyRespository sessionKeyRespository;

    @Async
    public Future<JsonObject> addPEPtoPEPGroup(JsonObject object) {
        String userId = object.get("userID").getAsString();
        String pepId = object.get("pepID").getAsString(); // TODO: NullPointerException 발생
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
        } else if (pep.getPepGroup() != null) { // TODO: NullPointerException 해결
            PEPGroup pepGroup = pepGroupRepository.findByOwnerAndPEP(user, pep);
            response.addProperty("hasGroup", true);
            response.addProperty("pepGroup", pepGroup.getPepGroupID());
        } else {
            response.addProperty("hasGroup", false);
            List<PEPGroup> pepGroups = pepGroupRepository.findByOwner(user);
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
        List<JsonObject> devices = new LinkedList<>();
        deviceProfiles.forEach(jsonElement -> {
            devices.add(jsonElement.getAsJsonObject());
        });

        DeviceRegister deviceRegister = new DeviceRegister();
        deviceRegister.updateDevice(pep, devices);

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
        try {
            returnSessionKey(pep.pepId, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.addProperty("error", "can't create sessionKey");
        }
    }

    private void returnSessionKey(String pepID, JsonObject response) throws Exception {
        String otpKey = OTP.create(pepID);
        sessionKeyRespository.save(new SessionKey(pepID, otpKey));
        response.addProperty("sessionKey", otpKey);
    }

    private void createAndAddGroup(JsonObject object, User user, PEP pep, JsonObject response) {
        String pepGroupName = object.get("pepGroupName").getAsString();
        String pepGroupPW = object.get("pepGroupPW").getAsString();
        PEPGroup pepGroup = createPEPGroup(user, pepGroupName, pepGroupPW);
        pep.setPepGroup(pepGroup);
        pepRepository.saveAndFlush(pep);
        GroupMember groupMember = new GroupMember(user, pepGroup);
        groupMemberRepository.saveAndFlush(groupMember);
        try {
            returnSessionKey(pep.pepId, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.addProperty("error", "can't create sessionKey");
        }
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
