package com.geocent.security.audit.controller;

import com.geocent.security.audit.event.AttributeFactory;
import com.geocent.security.audit.event.PEPAuditEvent;
import com.geocent.security.audit.event.XACMLRequestFactory;
import com.geocent.security.audit.model.AuditModel;
import com.geocent.security.audit.model.AuditTableModel;
import com.geocent.security.audit.model.QueryType;
import com.geocent.security.audit.view.AuditView;
import com.geocent.security.audit.view.TextAreaPopup;
import com.sun.xacml.attr.AttributeValue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author bpriest
 */
public class AuditController {

    private AuditView view;

    public AuditController(AuditView view) {
        this.view = view;

        this.view.addTableSelectionListener(new AuditTableSelectionListener());
        this.view.addQueryBtnListener(new QueryBtnListener());
        this.view.addRawEventActionListener(new RawEventMenuListener());
        this.view.addXacmlRequestActionListener(new XacmlRequestMenuListener());
    }

    class AuditTableSelectionListener implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent lse) {
            if (!lse.getValueIsAdjusting()) {
                ListSelectionModel model = view.getAuditTable().getSelectionModel();
                view.getDetailPanelHandler().updateDetailPanel(((AuditTableModel) view.getAuditTable().getModel()).getAuditEventAt(model.getLeadSelectionIndex()));
            }
        }
    }

    class QueryBtnListener implements ActionListener {

        public void actionPerformed(ActionEvent ae) {
            List<PEPAuditEvent> searchResults = performSearch(view.getSearchField().getText(), QueryType.AND);
            view.loadTableData(searchResults);
            view.getResultsLbl().setText("Found " + searchResults.size() + " matches of " + AuditModel.getInstance().getAuditEvents().size() + " total records.");
        }
    }

    class RawEventMenuListener implements ActionListener {

        public void actionPerformed(ActionEvent ae) {
            ListSelectionModel model = view.getAuditTable().getSelectionModel();
            PEPAuditEvent e = ((AuditTableModel) view.getAuditTable().getModel()).getAuditEventAt(model.getLeadSelectionIndex());
            TextAreaPopup event = new TextAreaPopup(e.getRawEvent());
            event.setVisible(true);
        }
    }

    class XacmlRequestMenuListener implements ActionListener {

        public void actionPerformed(ActionEvent ae) {
            ListSelectionModel model = view.getAuditTable().getSelectionModel();
            PEPAuditEvent e = ((AuditTableModel) view.getAuditTable().getModel()).getAuditEventAt(model.getLeadSelectionIndex());
            TextAreaPopup req = new TextAreaPopup(XACMLRequestFactory.createXacmlRequest(e));
            req.setVisible(true);
        }
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
