package selab.hanyang.ac.kr.platformmanager.controller;

import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import selab.hanyang.ac.kr.platformmanager.util.RequestParser;
import selab.hanyang.ac.kr.platformmanager.database.model.Device;
import selab.hanyang.ac.kr.platformmanager.database.model.DeviceAction;
import selab.hanyang.ac.kr.platformmanager.database.model.PEP;
import selab.hanyang.ac.kr.platformmanager.database.repository.DeviceActionRepository;
import selab.hanyang.ac.kr.platformmanager.database.repository.DeviceRepository;
import selab.hanyang.ac.kr.platformmanager.database.repository.PEPRepository;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

//윤근
@Controller
public class DeviceRegistrationController {

    @Autowired
    private PEPRepository pepRepo;

    @Autowired
    private DeviceRepository devRepo;

    @Autowired
    private DeviceActionRepository devActRepo;

    // Device 등록 기능
    @CrossOrigin(origins = "http://localhost")
    @PostMapping("devices/{pepId}")
    public @ResponseBody
    String updateDevices(@PathVariable String pepId, @RequestBody String request, HttpServletResponse httpResponse){

        System.out.println("Debug in DeviceRegister::updateDevice : 38");
        System.out.println("Request: "+request);

        RequestParser parser = new RequestParser(request);
        List<JsonObject> devices = RequestParser.mapToObject(parser.getAsJsonArray());
        PEP pep = pepRepo.findOneByPepId(pepId);
        boolean success = updateDevice(pep, devices);
        return "{\"success\":"+success+"}";

    }

    // 해당 pep에 대한 device들 DB에 추가(Device) - PEPRegister 에서도 사용
    public boolean updateDevice(PEP pep, List<JsonObject> devices) {
        devices.forEach(dev -> {
            String devId = dev.get("deviceId").getAsString();
            String devName = dev.get("deviceName").getAsString();
            String macAddr = dev.get("macAddress").getAsString();

            //Device DB에 저장
            Device device = new Device(devId, devName, macAddr, pep);
            devRepo.save(device);

            //DeviceAction DB에 저장
            List<JsonObject> actions = RequestParser.mapToObject(dev.get("actions").getAsJsonArray());
            updateDeviceAction(device, actions);
        });
        return true;
    }

    // 해당 device에 대한 action들 DB에 추가(DeviceAction)
    private boolean updateDeviceAction(Device device, List<JsonObject> actions) {
        actions.forEach(act -> {
            System.out.println("Debug in DeviceRegister::updateDeviceAction : 70");
            System.out.println(act.toString());
            String actionId = act.get("actionId").getAsString();  // DeviceAction.actionId
            String actionName = act.get("actionName").getAsString(); // DeviceAction.actionName
            String params = act.get("params").toString(); // DeviceAction.params
            devActRepo.save(new DeviceAction(actionId, actionName, device, params));
        });
        return true;
    }

}
