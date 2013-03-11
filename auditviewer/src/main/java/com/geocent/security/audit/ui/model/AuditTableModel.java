package com.geocent.security.audit.ui.model;

import com.geocent.security.audit.PEPAuditEvent;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author bpriest
 */
public class AuditTableModel extends AbstractTableModel {

    private List<PEPAuditEvent> auditEvents;

    public AuditTableModel(List<PEPAuditEvent> events) {
        super();
        auditEvents = events;
    }
    
    private String[] columnNames = {"Timestamp",
        "Subject (username)",
        "Resource",
        "Action",
        "Decision"};

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return auditEvents.size();
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    public PEPAuditEvent getAuditEventAt(int row){
        if(row < 0)
            return null;
        else
            return auditEvents.get(row);
    }   
    
    public Object getValueAt(int row, int col) {
        try {
            switch (col) {
                case 0:
                    return auditEvents.get(row).getTimeStamp();
                case 1:
                    return ((PEPAuditEvent) auditEvents.get(row)).getSubjectAttributeById("urn:vba.va.gov:css:common:userName");
                case 2:
                    return ((PEPAuditEvent) auditEvents.get(row)).getResourceAttributeById("urn:oasis:names:tc:xacml:1.0:resource:resource-id");
                case 3:
                    return ((PEPAuditEvent) auditEvents.get(row)).getActionAttributeById("urn:oasis:names:tc:xacml:1.0:action:action-id");
                case 4:
                    return ((PEPAuditEvent) auditEvents.get(row)).getDecision();
            }
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.toString());
        }
        return null;
    }
}
