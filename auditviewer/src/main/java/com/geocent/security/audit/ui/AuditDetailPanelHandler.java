package com.geocent.security.audit.ui;

import com.geocent.security.audit.PEPAuditEvent;
import java.util.EventListener;

/**
 *
 * @author bpriest
 */
public class AuditDetailPanelHandler implements EventListener {

    private AuditDetailPanel detailPanel;

    public AuditDetailPanelHandler(AuditDetailPanel panel) {
        this.detailPanel = panel;
    }

    public void updateDetailPanel(PEPAuditEvent e) {
        detailPanel.removeAll();
        detailPanel.setPEPAuditRecord(e);
        detailPanel.updateUI();
    }
}
