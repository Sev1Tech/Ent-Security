package com.geocent.security.audit.worker;

import com.geocent.security.audit.event.AttributeFactory;
import com.geocent.security.audit.event.PEPAuditEvent;
import com.geocent.security.audit.model.AuditModel;
import com.geocent.security.audit.model.QueryType;
import com.geocent.security.audit.view.AuditView;
import com.sun.xacml.attr.AttributeValue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

/**
 *
 * @author bpriest
 */
public class EventSearchTask extends SwingWorker<List<PEPAuditEvent>, Void>{

    private String criteria;
    private QueryType cond;
    private AuditView view;
    
    public EventSearchTask(String searchCriteria, QueryType condition, AuditView view){
        this.criteria = searchCriteria;
        this.cond = condition;
        this.view = view;
    }
    
    @Override
    protected List<PEPAuditEvent> doInBackground() throws Exception {
        return performSearch(this.criteria, this.cond);
    }
    
    @Override
    protected void done(){
        List<PEPAuditEvent> searchResults = null;
        try {
            searchResults = get();
        } catch (InterruptedException ex) {
            Logger.getLogger(EventSearchTask.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(EventSearchTask.class.getName()).log(Level.SEVERE, null, ex);
        }
        view.getResultsLbl().setText("Found " + searchResults.size() + " matches of " + AuditModel.getInstance().getAuditEvents().size() + " total records.");
        view.loadTableData(searchResults);
    }
    
    private List<PEPAuditEvent> performSearch(String criteria, QueryType type) {
        if (criteria.equals("")) {
            return AuditModel.getInstance().getAuditEvents();
        }

        List<PEPAuditEvent> searchResults = new ArrayList<PEPAuditEvent>();
        String[] singleCriteria = criteria.split(" ");

        for (PEPAuditEvent event : AuditModel.getInstance().getAuditEvents()) {
            List<Boolean> matchResults = new ArrayList<Boolean>();
            for (int x = 0; x < singleCriteria.length; x++) {
                if (isMatch(event, singleCriteria[x])) {
                    matchResults.add(true);
                } else {
                    matchResults.add(false);
                }
            }

            if (type.equals(QueryType.AND)) {
                if (!matchResults.contains(false)) {
                    searchResults.add(event);
                }
            }
        }
        return searchResults;
    }
    
    private boolean isMatch(PEPAuditEvent event, String singleCriteria) {
        boolean isMatch = false;
        String[] tokenPair = singleCriteria.split("=");
        if (event.containsAttribute(tokenPair[0])) {
            isMatch = true;
            if (tokenPair.length > 1) {
                String value = AttributeFactory.getStringValue((AttributeValue) event.getAttributeById(tokenPair[0]));
                if (!(value != null && value.equals(tokenPair[1]))) {
                    isMatch = false;
                }
            }
        }
        return isMatch;
    }
}
