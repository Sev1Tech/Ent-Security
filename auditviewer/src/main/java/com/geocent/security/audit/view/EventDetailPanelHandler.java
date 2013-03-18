package com.geocent.security.audit.view;

import com.geocent.security.audit.event.PEPAuditEvent;
import java.util.EventListener;

/**
 *
 * @author bpriest
 */
public class EventDetailPanelHandler implements EventListener {

    private EventDetailPanel detailPanel;

    public EventDetailPanelHandler(EventDetailPanel panel) {
        this.detailPanel = panel;
    }

    public void updateDetailPanel(PEPAuditEvent e) {
        detailPanel.removeAll();
        detailPanel.setPEPAuditRecord(e);
        detailPanel.updateUI();
    }
}
