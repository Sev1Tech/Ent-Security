package com.geocent.security.audit.model;

import com.geocent.security.audit.event.PEPAuditEvent;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bpriest
 */
public class AuditModel {
    
    private List<PEPAuditEvent> events;
    
    private AuditModel(){
        events = new ArrayList<PEPAuditEvent>();
    }
    
    private static class AuditModelHolder {
        public static final AuditModel INSTANCE = new AuditModel();
    }
    
    public static AuditModel getInstance(){
        return AuditModelHolder.INSTANCE;
    }
    
    public void setAuditEvents(List<PEPAuditEvent> events){
        this.events = events;
    }
    
    public List<PEPAuditEvent> getAuditEvents(){
        return events;
    }
}
