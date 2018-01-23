package selab.hanyang.ac.kr.platformmanager.pdp;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.wso2.balana.attr.AttributeFactory;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.DateTimeAttribute;
import org.wso2.balana.ctx.Attribute;
import org.wso2.balana.ctx.xacml3.RequestCtx;
import org.wso2.balana.xacml3.Attributes;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class XACMLConverter {


    @Deprecated
    public RequestCtx convert(JsonArray requestJson) throws URISyntaxException {
        Set<Attributes> attributesSet = new HashSet<>();
        for (JsonElement requestElement : requestJson) {
            JsonObject attributesObject = requestElement.getAsJsonObject();
            JsonArray attributeArray = attributesObject.get("attributes").getAsJsonArray();
            Set<Attribute> attributes = new HashSet<>();
            String category = null;
            if (attributesObject.get("category") != null) {
                category = attributesObject.get("category").getAsString();
            }
            for (JsonElement attributeElement : attributeArray) {
                JsonObject attributeObject = attributeElement.getAsJsonObject();
                String id, issuer = null;
                DateTimeAttribute issueInstant = null;
                List<AttributeValue> attributeValues = new ArrayList<>();
                //TODO: includeInResult 추가
                boolean includeInResult = false;
                int version = 3;
                if (attributeObject.get("id") != null) {
                    id = attributeObject.get("id").getAsString();
                } else {
                    return null;
                }
                if (attributeObject.get("issuer") != null) {
                    issuer = attributeObject.get("issuer").getAsString();
                    issueInstant = new DateTimeAttribute();
                }
                if (attributeObject.get("values") != null) {
                    Iterator<JsonElement> iterator = attributeObject.get("values").getAsJsonArray().iterator();
                    iterator.forEachRemaining(jsonElement -> {
                        String type = null, value = null;
                        try {
                            if (jsonElement.getAsJsonObject().get("type") != null) {
                                type = jsonElement.getAsJsonObject().get("type").getAsString();
                            } else {
                                throw new Exception("");
                            }
                            if (jsonElement.getAsJsonObject().get("value") != null) {
                                value = jsonElement.getAsJsonObject().get("value").getAsString();
                            } else {
                                throw new Exception("");
                            }
                            attributeValues.add(AttributeFactory.getInstance().createValue(new URI(type), value));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
                if (attributeValues.size() == 0) {
                    return null;
                }
                Attribute attribute = new Attribute(new URI(id), attributeValues.get(0).getType(), issuer, issueInstant, attributeValues, includeInResult, version);
                attributes.add(attribute);
            }
            attributesSet.add(new Attributes(new URI(category), attributes));
        }

        //TODO: returnPolicyList, combineDecision 추가
        RequestCtx request = new RequestCtx(null, attributesSet, false, true, null, null);
        request.encode(System.out);
        return request;
    }

    //TODO: convert Response to easy format
}
