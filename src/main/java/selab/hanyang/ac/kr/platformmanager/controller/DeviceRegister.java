package selab.hanyang.ac.kr.platformmanager.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import selab.hanyang.ac.kr.platformmanager.database.model.Device;
import selab.hanyang.ac.kr.platformmanager.database.model.DeviceAction;
import selab.hanyang.ac.kr.platformmanager.database.model.PEP;
import selab.hanyang.ac.kr.platformmanager.database.repository.DeviceActionRepository;
import selab.hanyang.ac.kr.platformmanager.database.repository.DeviceRepository;
import selab.hanyang.ac.kr.platformmanager.database.repository.PEPRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//윤근
@Controller
public class DeviceRegister {

    @Autowired
    private PEPRepository pepRepo;

    @Autowired
    private DeviceRepository devRepo;

    @Autowired
    private DeviceActionRepository devActRepo;

    // Device 등록 기능
    @RequestMapping(value = "devices/{pepID}", method = RequestMethod.POST)
    public @ResponseBody
    String updateDevice(@PathVariable String pepID, HttpServletRequest request, HttpServletResponse httpResponse){

        RequestParser parser = new RequestParser(request);
        List<JsonObject> devices = RequestParser.mapToObject(parser.getAsJsonArray());
        PEP pep = pepRepo.findOneByPepId(pepID);
        boolean success = updateDevice(pep, devices);

        // TODO: ack/nak 메시지 - 추후 형식에 맞게 수정
        return success ? "Ack":"Nak";
    }

    // 해당 pep에 대한 device들 DB에 추가(Device) - PEPRegister 에서도 사용
    public boolean updateDevice(PEP pep, List<JsonObject> devices) {
        devices.stream().forEachOrdered(dev -> {
            String devID = dev.get("deviceID").getAsString();
            String devName = dev.get("deviceName").getAsString();

            //Device DB에 저장
            Device device = new Device(devID, devName, pep);
            devRepo.save(device);

            //DeviceAction DB에 저장
            List<JsonObject> actions = RequestParser.mapToObject(dev.get("actions").getAsJsonArray());
            updateDeviceAction(device, actions);
        });
        return true;
    }

    // 해당 device에 대한 action들 DB에 추가(DeviceAction)
    private boolean updateDeviceAction(Device device, List<JsonObject> actions) {
        Stream<DeviceAction> as = actions.stream().map(act -> {
            String actionID = act.get("actionID").getAsString();  // DeviceAction.actionID
            String actionName = act.get("actionName").getAsString(); // DeviceAction.actionName
            JsonArray params = act.get("params").getAsJsonArray(); // DeviceAction.params
            return new DeviceAction(actionID, actionName, device, params.toString());
        });
        devActRepo.save(as.collect(Collectors.toList()));
        return true;
    }

}
