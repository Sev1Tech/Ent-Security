package com.geocent.security.audit.controller;

import com.geocent.security.audit.event.PEPAuditEvent;
import com.geocent.security.audit.event.XACMLRequestFactory;
import com.geocent.security.audit.model.AuditTableModel;
import com.geocent.security.audit.model.QueryType;
import com.geocent.security.audit.view.AuditView;
import com.geocent.security.audit.view.TextAreaPopup;
import com.geocent.security.audit.worker.EventSearchTask;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
            new EventSearchTask(view.getSearchField().getText(), QueryType.AND, view).execute();
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
            ListSelectionModel model = view.getListSelectionModel();
            PEPAuditEvent e = ((AuditTableModel) view.getAuditTable().getModel()).getAuditEventAt(model.getLeadSelectionIndex());
            TextAreaPopup req = new TextAreaPopup(XACMLRequestFactory.createXacmlRequest(e));
            req.setVisible(true);
        }
    }
}
