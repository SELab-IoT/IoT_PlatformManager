package selab.hanyang.ac.kr.platformmanager.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

//윤근
@Controller
public class DeviceRegister {

    /*
    *  pepID:"aaa" //url로
    *  devices: key 생략.
    *  [
    *       {...},
    *       {...}
    *  ]
    * */

    @RequestMapping(value = "devices/{pepID}", method = RequestMethod.POST)
    public @ResponseBody
    String updateDeviceProfile(@PathVariable String pepID, HttpServletRequest request, HttpServletResponse httpResponse){

        RequestParser parser = new RequestParser(request);
        List<JsonObject> devices = RequestParser.mapToObject(parser.getAsJsonArray());

        devices.stream().forEachOrdered(device -> {
            String id = device.get("deviceID").getAsString(); //Device.deviceID, DeviceAction.deviceID
            String name = device.get("deviceName").getAsString(); //Device.deviceName
            List<JsonObject> actions = RequestParser.mapToObject(device.get("actions").getAsJsonArray());
            actions.stream().forEach(action -> {
                String actionID = action.get("actionID").getAsString();  // DeviceAction.actionID
                String actionName = action.get("actionName").getAsString(); // DeviceAction.actionName
                JsonArray params = action.get("params").getAsJsonArray(); // DeviceAction.params

            });
        });

        return null;
    }
}
