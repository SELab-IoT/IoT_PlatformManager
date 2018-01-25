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

    // Device 등록 기능
    @RequestMapping(value = "devices/{pepID}", method = RequestMethod.POST)
    public @ResponseBody
    String updateDeviceProfile(@PathVariable String pepID, HttpServletRequest request, HttpServletResponse httpResponse){

        RequestParser parser = new RequestParser(request);
        List<JsonObject> devices = RequestParser.mapToObject(parser.getAsJsonArray());

        devices.stream().forEachOrdered(device -> {
            String deviceID = device.get("deviceID").getAsString(); //Device.deviceID, DeviceAction.deviceID
            String deviceName = device.get("deviceName").getAsString(); //Device.deviceName
            List<JsonObject> actions = RequestParser.mapToObject(device.get("actions").getAsJsonArray());
            updateDeviceProfile(deviceID, deviceName, pepID, actions);
        });

        // ack/nak 메시지
        return null;
    }

    // 실제 DB 업데이트
    private boolean updateDeviceProfile(String deviceID, String deviceName, String pepID, List<JsonObject> actions) {

        // 0. 트랜젝션 begin
        // 1. 해당 deviceID가 있는지 확인
        // 1.a. 있으면 해당 레코드에 대해 업데이트 쿼리 수행하거나 혹은 해당 레코드를 지우고 새로 추가

        // 1.b. 없으면 레코드 새로 추가
        actions.stream().forEach(action -> {
            String actionID = action.get("actionID").getAsString();  // DeviceAction.actionID
            String actionName = action.get("actionName").getAsString(); // DeviceAction.actionName
            JsonObject params = action.get("params").getAsJsonObject(); // DeviceAction.params
            // Device 테이블에 레코드 추가
            // ~~~
            // DeviceAction 테이블에 레코드 추가
            updateDeviceAction(actionID, actionName, deviceID, params);
        });
        // E. 트랜젝션 end
        return true;
    }

    private boolean updateDeviceAction(String actionID, String actionName, String deviceID, JsonObject params) {
        // DeviceAction 테이블에 레코드 추가.
        return true;
    }
}
