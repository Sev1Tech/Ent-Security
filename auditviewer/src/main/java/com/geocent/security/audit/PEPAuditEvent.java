package com.geocent.security.audit;

import com.sun.xacml.ParsingException;
import com.sun.xacml.attr.AttributeValue;
import java.net.URI;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author bpriest
 */
public class PEPAuditEvent extends Event {

    private JSONObject pepAuditJson;
    private Map<URI, AttributeValue> subjectMap;
    private Map<URI, AttributeValue> resourceMap;
    private Map<URI, AttributeValue> actionMap;
    private Map<URI, AttributeValue> environmentMap;
    
    public PEPAuditEvent(String rawEvent) throws JSONException, ParseException, ParsingException {
        super(rawEvent);
        subjectMap = new HashMap<URI, AttributeValue>();
        resourceMap = new HashMap<URI, AttributeValue>();
        actionMap = new HashMap<URI, AttributeValue>();
        environmentMap = new HashMap<URI, AttributeValue>();
        parsePEPAuditStatement(rawEvent);
    }
    
    private void parsePEPAuditStatement(String rawEvent) throws JSONException, ParseException, ParsingException{
        pepAuditJson = new JSONObject(rawEvent.substring(rawEvent.indexOf("{\"PEP_AUDIT")));
        parseSubjectAttributes();
        parseResourceAttributes();
        parseActionAttributes();
        parseEnvironmentAttributes();
    }
    
    private void parseSubjectAttributes() throws JSONException, ParseException, ParsingException{
        JSONObject subject = pepAuditJson.getJSONArray("PEP_AUDIT")
                .getJSONObject(0)
                .getJSONObject("Request")
                .getJSONObject("Subject");
        subjectMap = getAttributeKeyValueMap(subject);
        
    }
    
    private void parseResourceAttributes() throws JSONException, ParseException, ParsingException {
        JSONObject resource = pepAuditJson.getJSONArray("PEP_AUDIT")
                .getJSONObject(0)
                .getJSONObject("Request")
                .getJSONObject("Resource");
        resourceMap = getAttributeKeyValueMap(resource);
    }
    
    private void parseActionAttributes() throws JSONException, ParseException, ParsingException {
        JSONObject action = pepAuditJson.getJSONArray("PEP_AUDIT")
                .getJSONObject(0)
                .getJSONObject("Request")
                .getJSONObject("Action");
        actionMap = getAttributeKeyValueMap(action);
        
    }
    
    private void parseEnvironmentAttributes() throws JSONException, ParseException, ParsingException {
        JSONObject environment = pepAuditJson.getJSONArray("PEP_AUDIT")
                .getJSONObject(0)
                .getJSONObject("Request")
                .getJSONObject("Environment");
        environmentMap = getAttributeKeyValueMap(environment);
    }
    
    private HashMap<URI, AttributeValue> getAttributeKeyValueMap(JSONObject obj) throws JSONException, ParseException, ParsingException{
        HashMap<URI, AttributeValue> attributeKeyValueMap = new HashMap<URI, AttributeValue>();
        JSONArray attributes = obj.getJSONArray("Attribute");
        
        for (int x = 0; x < attributes.length(); x++){
            JSONObject jsonObj = attributes.getJSONObject(x);
            JSONObject attributeInfo = jsonObj.getJSONObject("@attributes");
            AttributeValue attrVal = AttributeFactory.getAttribute(
                    attributeInfo.getString("DataType"),
                    jsonObj.getString("AttributeValue"));
            attributeKeyValueMap.put(URI.create(attributeInfo.getString("AttributeId")), attrVal);
        }
        
        return attributeKeyValueMap;
    }
    
    public String getDecision() throws JSONException{
        JSONObject decision = pepAuditJson.getJSONArray("PEP_AUDIT")
                .getJSONObject(1)
                .getJSONObject("Response")
                .getJSONObject("Result");
        return decision.getString("Decision");
    }
    
    public Object getSubjectAttributeById(String attributeId){
        return subjectMap.get(URI.create(attributeId));
    }
    
    public Object getResourceAttributeById(String attributeId){
        return resourceMap.get(URI.create(attributeId));
    }
    
    public Object getActionAttributeById(String attributeId){
        return actionMap.get(URI.create(attributeId));
    }
    
    public Object getEnvrionmentAttributeById(String attributeId){
        return environmentMap.get(URI.create(attributeId));
    }
    
    public Map<URI, AttributeValue> getSubjectAttributes(){
        return subjectMap;
    }
    
    public Map<URI, AttributeValue> getResourceAttributes(){
        return resourceMap;
    }
    
    public Map<URI, AttributeValue> getActionAttributes(){
        return actionMap;
    }
    
    public Map<URI, AttributeValue> getEnvironmentAttributes(){
        return environmentMap;
    }
    
    public boolean containsAttribute(String attributeId){
        if (subjectMap.containsKey(URI.create(attributeId)))
            return true;
        if (resourceMap.containsKey(URI.create(attributeId)))
            return true;
        if (actionMap.containsKey(URI.create(attributeId)))
            return true;
        if (environmentMap.containsKey(URI.create(attributeId)))
            return true;
        else
            return false;
    }
    
    public Object getAttributeById(String attributeId){
        Object value = null;
        for(int x = 0; x < 4; x++){
            value = getAttributeById(attributeId, x);
            if (value != null)
                break;
        }
        return value;
    }
    
    private Object getAttributeById(String attributeId, int index){
        switch(index){
            case 0:
                return getSubjectAttributeById(attributeId);
            case 1:
                return getResourceAttributeById(attributeId);
            case 2:
                return getActionAttributeById(attributeId);
            case 3:
                return getEnvrionmentAttributeById(attributeId);     
        }
        return null;
    }
    
    @Override
    public String getRawEvent(){
        return super.getRawEvent();
    }
}
