package selab.hanyang.ac.kr.platformmanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

//윤근
@Controller
public class UtilityController {
    @RequestMapping(value = "pep-group/profile/{userID}", method = RequestMethod.GET)
    public @ResponseBody
    String getPEPGroups(@PathVariable String userVariable, HttpServletResponse httpResponse){
        return null;
    }
}
