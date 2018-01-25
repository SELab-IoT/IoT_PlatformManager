package selab.hanyang.ac.kr.platformmanager.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import selab.hanyang.ac.kr.platformmanager.database.model.PEPGroup;
import selab.hanyang.ac.kr.platformmanager.database.repository.PEPGroupRepository;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

//윤근
@Controller
public class UtilityController {

    @Autowired
    PEPGroupRepository pepGrpRepo;

    // 조회 기능
    @RequestMapping(value = "pep-group/profile/{userID}", method = RequestMethod.GET)
    public @ResponseBody
    String getPEPGroups(@PathVariable String userID, HttpServletResponse httpResponse){
        String groups = getPEPGroups(userID);
        return groups;
    }

    private String getPEPGroups(String owner){
        List<PEPGroup> groups = pepGrpRepo.findPEPGroupsByOwner_UserId(owner);
        return new Gson().toJson(groups);
    }

}
