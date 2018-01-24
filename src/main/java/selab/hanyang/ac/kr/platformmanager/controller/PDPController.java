package selab.hanyang.ac.kr.platformmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
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

    @RequestMapping(value = "evaluate", method = RequestMethod.POST)
    public @ResponseBody
    String evaluatePolicyRequest(HttpServletRequest request, HttpServletResponse httpResponse) {
        RequestParser reqParser = new RequestParser(request);

        String requestBody = reqParser.getAsString("body");
        String pepId = reqParser.getAsString("pepId");

        RequestCtx requestCtx = null;
        try {
            requestCtx = new XACMLConverter().convert(reqParser.getAsJsonArray());
            System.out.println(requestCtx);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        String response = evaluateRequest(requestCtx, pepId);
        if (response == null)
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return response;

    }


    /* Deprecated */
    private String evaluateRequest(String requestBody, String pepId) {
        return !(requestBody == null ||requestBody.isEmpty()) ? pdpInterface.evaluate(requestBody, pepId) : null;
    }

    private String evaluateRequest(RequestCtx requestBody, String pepId) {
        return !(requestBody == null) ? pdpInterface.evaluate(requestBody, pepId) : null;
    }

    @RequestMapping(value = "reload", method = RequestMethod.POST)
    public @ResponseBody String reloadPDP(HttpServletRequest request, HttpServletResponse httpResponse) {

        RequestParser reqParser = new RequestParser(request);

        String pdpName = reqParser.getAsString("pdpName");
        if(pdpName == null)
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        boolean isSuccess = PDPInterface.getInstance().reloadPDP(pdpName);
        if (isSuccess) return "reloadSuccess";
        else httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return null;
    }

}
