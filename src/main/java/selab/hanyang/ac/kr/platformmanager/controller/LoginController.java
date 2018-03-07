package selab.hanyang.ac.kr.platformmanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {
    @CrossOrigin(origins = "http://localhost")
    @PostMapping(path = "/login", consumes = "application/json", produces = "application/json")
    public @ResponseBody
    String addPEPtoPEPGroup(@RequestBody String request) {
        System.out.println("REQUEST: "+request);
        return "{}";
    }
}
