package selab.hanyang.ac.kr.platformmanager.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.wso2.balana.ctx.xacml3.RequestCtx;
import selab.hanyang.ac.kr.platformmanager.pdp.PDPInterface;
import selab.hanyang.ac.kr.platformmanager.pdp.XACMLConverter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.stream.Collectors;

@Controller
public class PDPController {

    Gson gson = new GsonBuilder().create();


    @Autowired
    PDPInterface pdpInterface;

    @RequestMapping(value = "evaluate", method = RequestMethod.POST)
    public @ResponseBody
    String evaluatePolicyRequest(HttpServletRequest request, HttpServletResponse httpResponse) {
        try {
            String requestText = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

            JsonObject inputJson = gson.fromJson(requestText, JsonObject.class);
            String requestBody = inputJson.get("body").getAsString();

            String pepId = null;
            if (inputJson.get("pepId") != null)
                pepId = inputJson.get("pepId").getAsString();

            RequestCtx requestCtx = null;
            try {
                requestCtx = new XACMLConverter().convert(gson.fromJson(requestBody, JsonArray.class));
                System.out.println(requestCtx);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            String response = evaluateRequest(requestCtx, pepId);

            if (response == null) {
                httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return response;
            }

            return response;
        } catch (IOException e) {
            e.printStackTrace();
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
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
        Gson gson = new GsonBuilder().create();
        try {
            String requestText = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            JsonObject inputJson = gson.fromJson(requestText, JsonObject.class);
            String pdpName = null;
            if (inputJson.get("pdpName") != null)
                pdpName = inputJson.get("pdpName").getAsString();
            else
                httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            boolean isSuccess = PDPInterface.getInstance().reloadPDP(pdpName);
            if (isSuccess)
                return "reloadSuccess";
            else
                httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            return null;
        } catch (IOException e) {
            e.printStackTrace();
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

    }

}
