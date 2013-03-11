package com.geocent.security.audit;

import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author bpriest
 */
public class PEPAuditEvent extends Event {

    private JSONObject pepAuditJson;
    private HashMap<String, String> subjectAttributes;
    private HashMap<String, String> resourceAttributes;
    private HashMap<String, String> actionAttributes;
    private HashMap<String, String> environmentAttributes;
    
    public PEPAuditEvent(String rawEvent) throws JSONException {
        super(rawEvent);
        subjectAttributes = new HashMap<String, String>();
        resourceAttributes = new HashMap<String, String>();
        actionAttributes = new HashMap<String, String>();
        environmentAttributes = new HashMap<String, String>();
        parsePEPAuditStatement(rawEvent);
    }
    
    private void parsePEPAuditStatement(String rawEvent) throws JSONException{
        pepAuditJson = new JSONObject(rawEvent.substring(rawEvent.indexOf("{\"PEP_AUDIT")));
        parseSubjectAttributes();
        parseResourceAttributes();
        parseActionAttributes();
        parseEnvironmentAttributes();
    }
    
    private void parseSubjectAttributes() throws JSONException{
        JSONObject subject = pepAuditJson.getJSONArray("PEP_AUDIT")
                .getJSONObject(0)
                .getJSONObject("Request")
                .getJSONObject("Subject");
        subjectAttributes = getAttributeKeyValuePairs(subject);
    }
    
    private void parseResourceAttributes() throws JSONException {
        JSONObject resource = pepAuditJson.getJSONArray("PEP_AUDIT")
                .getJSONObject(0)
                .getJSONObject("Request")
                .getJSONObject("Resource");
        resourceAttributes = getAttributeKeyValuePairs(resource);
    }
    
    private void parseActionAttributes() throws JSONException {
        JSONObject action = pepAuditJson.getJSONArray("PEP_AUDIT")
                .getJSONObject(0)
                .getJSONObject("Request")
                .getJSONObject("Action");
        actionAttributes = getAttributeKeyValuePairs(action);
        
    }
    
    private void parseEnvironmentAttributes() throws JSONException {
        JSONObject environment = pepAuditJson.getJSONArray("PEP_AUDIT")
                .getJSONObject(0)
                .getJSONObject("Request")
                .getJSONObject("Environment");
        environmentAttributes = getAttributeKeyValuePairs(environment);
    }
    
    private HashMap<String, String> getAttributeKeyValuePairs(JSONObject obj) throws JSONException{
        HashMap<String, String> attributeKeyValuePairs = new HashMap<String, String>();
        JSONArray attributes = obj.getJSONArray("Attribute");
        
        for (int x = 0; x < attributes.length(); x++){
            JSONObject jsonObj = attributes.getJSONObject(x);
            JSONObject attributeInfo = jsonObj.getJSONObject("@attributes");
            attributeKeyValuePairs.put(attributeInfo.getString("AttributeId"), jsonObj.getString("AttributeValue"));
        }
        return attributeKeyValuePairs;
    }
    
    public String getDecision() throws JSONException{
        JSONObject decision = pepAuditJson.getJSONArray("PEP_AUDIT")
                .getJSONObject(1)
                .getJSONObject("Response")
                .getJSONObject("Result");
        return decision.getString("Decision");
    }
    
    public String getSubjectAttributeById(String attributeId){
        return subjectAttributes.get(attributeId);
    }
    
    public String getResourceAttributeById(String attributeId){
        return resourceAttributes.get(attributeId);
    }
    
    public String getActionAttributeById(String attributeId){
        return actionAttributes.get(attributeId);
    }
    
    public String getEnvrionmentAttributeById(String attributeId){
        return environmentAttributes.get(attributeId);
    }
    
    public HashMap<String, String> getSubjectAttributes(){
        return subjectAttributes;
    }
    
    public HashMap<String, String> getResourceAttributes(){
        return resourceAttributes;
    }
    
    public HashMap<String, String> getActionAttributes(){
        return actionAttributes;
    }
    
    public HashMap<String, String> getEnvironmentAttributes(){
        return environmentAttributes;
    }
    
    public boolean containsAttribute(String attributeId){
        if (subjectAttributes.containsKey(attributeId))
            return true;
        if (resourceAttributes.containsKey(attributeId))
            return true;
        if (actionAttributes.containsKey(attributeId))
            return true;
        if (environmentAttributes.containsKey(attributeId))
            return true;
        else
            return false;
    }
    
    public String getAttributeById(String attributeId){
        String value = null;
        for(int x = 0; x < 4; x++){
            value = getAttributeById(attributeId, x);
            if (value != null)
                break;
        }
        return value;
    }
    
    private String getAttributeById(String attributeId, int index){
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
