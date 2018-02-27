package selab.hanyang.ac.kr.platformmanager.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import selab.hanyang.ac.kr.platformmanager.database.model.PEP;
import selab.hanyang.ac.kr.platformmanager.database.model.PEPGroup;
import selab.hanyang.ac.kr.platformmanager.database.repository.*;

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
    @GetMapping("/pep-group/profile/{userID}")
    public @ResponseBody
    String getPEPGroups(@PathVariable String userID, HttpServletResponse httpResponse){

        JsonArray groups = new JsonArray();

        // 1. 해당 유저가 속한 pepGroup들 가져와서
        List<PEPGroup> grps = pepGrpRepo.findPEPGroupsByUserId(userID);
        grps.forEach(grp->{

            // 2. Group 정보 꺼내고
            long pepGroupID = grp.getPepGroupID();
            JsonArray pepProfiles = new JsonArray();

            // A ~ D : pepProfiles 만들기
            // A. pepGroup에 속하는 pep들 가져와서
            List<PEP> peps = pepRepo.findByGroupId(pepGroupID);
            peps.forEach(pep->{

                // B. 정보 꺼내고
                String ip = pep.getIp();
                JsonArray devProfiles = new JsonArray();

                // a ~ d : devProfiles 만들기
                // a. pep에 속하는 device들 가져와서
                devRepo.findByPep(pep)
                       .forEach(dev->{
                            // b. 정보 꺼내고
                            String devId = dev.getId();
                            String devName = dev.getName();
                            JsonArray actions = new Gson().toJsonTree(devActRepo.findByDevice(dev)).getAsJsonArray();

                            // c. devProfile 만들어주고
                            JsonObject devProfile = new JsonObject();
                            devProfile.addProperty("deviceID", devId);
                            devProfile.addProperty("deviceName", devName);
                            devProfile.add("actions", actions);

                            // d. JsonArray에 각각 추가
                            devProfiles.add(devProfile);
                       });

                // C. pepProfile 만들어주고
                JsonObject pepProfile = new JsonObject();
                pepProfile.addProperty("ip", ip);
                pepProfile.add("deviceProfiles", devProfiles);

                // D. JsonArray에 각각 추가
                pepProfiles.add(pepProfile);

            });

            // 3. group 만들어주고
            JsonObject group = new JsonObject();
            group.addProperty("pepGroupID", pepGroupID);
            group.add("pepProfiles",pepProfiles);

            // 4. JsonArray에 각각 추가
            groups.add(group);

        });

        System.out.println(groups.toString());//디버그코드
        return groups.toString();
    }

}
