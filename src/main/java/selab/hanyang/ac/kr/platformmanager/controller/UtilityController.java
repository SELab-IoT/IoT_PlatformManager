package selab.hanyang.ac.kr.platformmanager.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import selab.hanyang.ac.kr.platformmanager.util.RequestParser;
import selab.hanyang.ac.kr.platformmanager.database.model.DeviceAction;
import selab.hanyang.ac.kr.platformmanager.database.model.PEP;
import selab.hanyang.ac.kr.platformmanager.database.model.PEPGroup;
import selab.hanyang.ac.kr.platformmanager.database.repository.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

//윤근
@Controller
public class UtilityController {

    @Autowired
    PEPGroupRepository pepGrpRepo;

    @Autowired
    PEPRepository pepRepo;

    @Autowired
    DeviceRepository devRepo;

    @Autowired
    DeviceActionRepository devActRepo;

    // 조회 기능
    @CrossOrigin(origins = "http://localhost")
    @PostMapping("/pep-group/profile/")
    public @ResponseBody
    String getPEPGroups(HttpServletRequest request, HttpServletResponse httpResponse){

        RequestParser parser = new RequestParser(request);
        String userId = parser.getAsString("userId");
        String sessionKey = parser.getAsString("sessionKey");

        JsonArray groups = new JsonArray();

        // 1. 해당 유저가 속한 pepGroup들 가져와서
        List<PEPGroup> grps = pepGrpRepo.findPEPGroupsByUserId(userId);

        grps.forEach(grp->{

            // 2. Group 정보 꺼내고
            long pepGroupId = grp.getPepGroupId();
            JsonArray pepProfiles = new JsonArray();

            // A ~ D : pepProfiles 만들기
            // A. pepGroup에 속하는 pep들 가져와서
            List<PEP> peps = pepRepo.findByGroupId(pepGroupId);
            peps.forEach(pep->{

                // B. 정보 꺼내고
                String ip = pep.getIp();
                JsonArray deviceProfiles = new JsonArray();

                // a ~ d : deviceProfiles 만들기
                // a. pep에 속하는 device들 가져와서
                devRepo.findByPep(pep)
                        .forEach(device->{
                            // b. 정보 꺼내고
                            String deviceId = device.getId();
                            String deviceName = device.getName();
                            String macAddress = device.getMacAddress();
                            JsonArray actions = new JsonArray();

                            // 1 ~ 4 : actions 만들기
                            // 1. device에 속하는 action들 가져와서
                            List<DeviceAction> acts = devActRepo.findByDevice(device);
                            acts.forEach(act -> {
                                // 2. 정보 꺼내고
                                String actionId = act.getActionId();
                                String actionName = act.getActionName();
                                String params = act.getParams();

                                // 3. action 만들어주고
                                JsonObject action = new JsonObject();
                                action.addProperty("actionId", actionId);
                                action.addProperty("actionName", actionName);
                                action.addProperty("params", params);

                                // 4. actions에 추가
                                actions.add(action);
                            });

                            // c. devProfile 만들어주고
                            JsonObject devProfile = new JsonObject();
                            devProfile.addProperty("deviceId", deviceId);
                            devProfile.addProperty("deviceName", deviceName);
                            devProfile.addProperty("macAddress", macAddress);
                            devProfile.add("actions", actions);

                            // d. JsonArray에 각각 추가
                            deviceProfiles.add(devProfile);
                        });

                // C. pepProfile 만들어주고
                JsonObject pepProfile = new JsonObject();
                pepProfile.addProperty("ip", ip);
                pepProfile.add("deviceProfiles", deviceProfiles);

                // D. JsonArray에 각각 추가
                pepProfiles.add(pepProfile);

            });

            // 3. group 만들어주고
            JsonObject group = new JsonObject();
            group.addProperty("pepGroupId", pepGroupId);
            group.add("pepProfiles",pepProfiles);

            // 4. JsonArray에 각각 추가
            groups.add(group);

        });

        System.out.println(groups.toString());//디버그코드
        return groups.toString();
    }

}
