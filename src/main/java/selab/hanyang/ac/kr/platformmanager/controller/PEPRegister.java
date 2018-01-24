package selab.hanyang.ac.kr.platformmanager.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import selab.hanyang.ac.kr.platformmanager.database.model.PEP;
import selab.hanyang.ac.kr.platformmanager.database.model.PEPGroup;
import selab.hanyang.ac.kr.platformmanager.database.model.User;
import selab.hanyang.ac.kr.platformmanager.database.repository.PEPGroupRepository;
import selab.hanyang.ac.kr.platformmanager.database.repository.PEPRepository;
import selab.hanyang.ac.kr.platformmanager.database.repository.UserRepository;

@Controller
public class PEPRegister {

    @Autowired
    PEPGroupRepository pepGroupRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PEPRepository pepRepository;

    @RequestMapping(name = "/groups/{userID}/{pepID}")
    public @ResponseBody String searchPEPGroup(@PathVariable String userID, @PathVariable String pepID) {
        Gson gson = new GsonBuilder().create();
        User user = userRepository.findOne(userID);
        PEP pep = pepRepository.findOne(pepID);
        if (user != null && pep.getPepGroup() != null) {
            pepGroupRepository.findByOwnerAndPEP(user, pep);
        }

        return null;
    }

}
