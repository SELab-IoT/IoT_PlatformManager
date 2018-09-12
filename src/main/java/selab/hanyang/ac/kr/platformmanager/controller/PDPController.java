package selab.hanyang.ac.kr.platformmanager.controller;

import com.google.gson.JsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.wso2.balana.ctx.xacml3.RequestCtx;
import selab.hanyang.ac.kr.platformmanager.pdp.PDPInterface;
import selab.hanyang.ac.kr.platformmanager.pdp.XACMLConverter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URISyntaxException;

//윤근(수정)
@Controller
public class PDPController {

    @Autowired
    PDPInterface pdpInterface;

    @CrossOrigin(origins = "http://localhost")
    @PostMapping("/evaluate")
    public @ResponseBody
    String evaluatePolicyRequest(HttpServletRequest request, HttpServletResponse httpResponse) {
        RequestParser parser = new RequestParser(request);

        System.out.println("Request:"+request);

        JsonArray payload = parser.getAsJsonArray("body");
        System.out.println("Request Body:"+payload);

        String pepId = parser.getAsString("pepId");
        System.out.println("PEP ID:"+pepId);

        RequestCtx requestCtx = null;
        try {
            requestCtx = new XACMLConverter().convert(payload);
            System.out.println(requestCtx);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        String response = evaluateRequest(requestCtx, pepId);
        if (response == null)
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return response;

    }

    private String evaluateRequest(RequestCtx requestBody, String pepId) {
        return !(requestBody == null) ? pdpInterface.evaluate(requestBody, pepId) : null;
    }

    @CrossOrigin(origins = "http://localhost")
    @PostMapping("/reload")
    public @ResponseBody String reloadPDP(HttpServletRequest request, HttpServletResponse httpResponse) {

        RequestParser parser = new RequestParser(request);

        String pdpName = parser.getAsString("pdpName");
        if(pdpName == null)
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        boolean isSuccess = PDPInterface.getInstance().reloadPDP(pdpName);
        if (isSuccess) return "reloadSuccess";
        else httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return null;
    }

}
