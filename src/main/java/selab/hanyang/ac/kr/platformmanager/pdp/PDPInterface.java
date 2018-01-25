package selab.hanyang.ac.kr.platformmanager.pdp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wso2.balana.*;
import org.wso2.balana.attr.AttributeFactory;
import org.wso2.balana.combine.CombiningAlgFactory;
import org.wso2.balana.cond.FunctionFactoryProxy;
import org.wso2.balana.ctx.xacml3.RequestCtx;
import selab.hanyang.ac.kr.platformmanager.database.model.PEP;
import selab.hanyang.ac.kr.platformmanager.database.repository.PEPRepository;
import selab.hanyang.ac.kr.platformmanager.database.repository.PDPRepository;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;
import java.util.List;

@Component
public class PDPInterface {

    private static PDPInterface pdpInterface;
    private HashMap<String, PDP> pdpHashMap;
    private Balana balana;

    @Autowired
    private PEPRepository pepRepository;

    @Autowired
    private PDPRepository pdpRepository;

    //Thread-safe singleton
    public static PDPInterface getInstance() {
        return pdpInterface = Singleton.instance;
    }
    private PDPInterface() {
        pdpHashMap = new HashMap<>();
    }

    private static class Singleton{
        private static final PDPInterface instance = new PDPInterface();
    }

    private JsonObject getDatabaseConf(String loc) {
        File confFile = new File(loc);
        String confString = readFromFile(confFile);
        Gson gson = new GsonBuilder().create();
        JsonObject confJson = gson.fromJson(confString, JsonObject.class);
        return confJson;
    }
    private String readFromFile(File file) {
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            String totalLine = "";
            while ((line = br.readLine()) != null)
                totalLine += line;
            return totalLine;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /* Deprecated */
    // API 1. evaluate
    public String evaluate(String request, String pepId) {
        // 매번 생성하는 로드를 줄이기 위한 방법을 검토해볼 것.
        // (예를들어, 현재 PDP와 pdpConfigName이 같다면 재활용 한다던지...
        // 단 같은 이름이어도 config.xml이 수정될수도 있으니 유의해야함)
        String pdpName = getPDPConfigName(pepId);
        if (pdpName != null) {
            PDP pdp = pdpHashMap.get(pdpName);
            return pdp.evaluate(request);
        } else {
            System.out.println("pdpName is null");
            return null;
        }
    }


    public String evaluate(RequestCtx requestCtx, String pepId) {
        String pdpName = getPDPConfigName(pepId);
        if (pdpName != null) {
            PDP pdp = pdpHashMap.get(pdpName);
            return pdp.evaluate(requestCtx).encode();
        } else {
            System.out.println("pdpName is null");
            return null;
        }
    }

    private String getPDPConfigName(String pepId) {

        PEP pep = pepRepository.findOneByPepId(pepId);
        if (pep != null){
            selab.hanyang.ac.kr.platformmanager.database.model.PDP pdp = pep.getPDP();
            return pdp.getName();
        } else {
            return null;
        }
    }

    private PDP getPDPNewInstance(String pdpConfigName) {
        reloadBalana(pdpConfigName, null, null);
        PDPConfig pdpConfig = balana.getPdpConfig();
        return new PDP(pdpConfig);
    }

    // API 2 ? (이 부분 API로 따야하는지?)
    private boolean reloadBalana(String pdpConfigName, String attributeFactoryName, String functionFactoryName) {
        try {
            ConfigurationStore configurationStore = new ConfigurationStore();
            if (configurationStore != null) {
                PDPConfig pdpConfig = pdpConfigName != null
                        ? configurationStore.getPDPConfig(pdpConfigName)
                        : configurationStore.getDefaultPDPConfig();

                AttributeFactory attributeFactory = attributeFactoryName != null
                        ? configurationStore.getAttributeFactory(attributeFactoryName)
                        : configurationStore.getDefaultAttributeFactoryProxy().getFactory();

                FunctionFactoryProxy proxy = functionFactoryName != null
                        ? configurationStore.getFunctionFactoryProxy(functionFactoryName)
                        : configurationStore.getDefaultFunctionFactoryProxy();

                CombiningAlgFactory combiningAlgFactory = functionFactoryName != null
                        ? configurationStore.getCombiningAlgFactory(functionFactoryName)
                        : configurationStore.getDefaultCombiningFactoryProxy().getFactory();

                balana.setPdpConfig(pdpConfig);
                balana.setAttributeFactory(attributeFactory);
                balana.setFunctionTargetFactory(proxy.getTargetFactory());
                balana.setFunctionConditionFactory(proxy.getConditionFactory());
                balana.setFunctionGeneralFactory(proxy.getGeneralFactory());
                balana.setCombiningAlgFactory(combiningAlgFactory);
            }
            return true;
        } catch (ParsingException | UnknownIdentifierException e) {
            e.printStackTrace();
            return false;
        }
    }

    //TODO: rest api로 제공 필요
    public boolean reloadPDP(String pdpName) {
        try {
            pdpHashMap.put(pdpName, getPDPNewInstance(pdpName));
            return true;
        } catch (Exception e){
            return false;
        }
    }

    @PostConstruct
    private void initBalana(){
        // Set balana config file.
        String configLocation = "src" + File.separator + "main" + File.separator + "resources"+File.separator+"config.xml";
        System.setProperty(ConfigurationStore.PDP_CONFIG_PROPERTY, configLocation);

        // Create default instance of Balana
        balana = Balana.getInstance();

        getPDPNameList().forEach(pdpName -> reloadPDP(pdpName));

    }

    private List<String> getPDPNameList() {
        return pdpRepository.findAllName();
    }


}
