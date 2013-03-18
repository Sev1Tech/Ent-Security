package com.geocent.security.audit.view;

import com.geocent.security.audit.event.AttributeFactory;
import com.geocent.security.audit.event.PEPAuditEvent;
import com.sun.xacml.attr.AttributeValue;
import java.awt.GridLayout;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.json.JSONException;

/**
 *
 * @author bpriest
 */
public class EventDetailPanel extends JPanel {

    private PEPAuditEvent detailRecord;

    public EventDetailPanel(PEPAuditEvent record) {
        super();
        this.detailRecord = record;
        updateAuditDetail(detailRecord);
    }

    public EventDetailPanel() {
        super();
    }

    public void setPEPAuditRecord(PEPAuditEvent event) {
        this.detailRecord = event;
        updateAuditDetail(detailRecord);
    }

    private void updateAuditDetail(PEPAuditEvent event) {

        JPanel requestPanel = new JPanel();
        JPanel subjectPanel = new JPanel();
        JPanel resourcePanel = new JPanel();
        JPanel actionPanel = new JPanel();
        JPanel envPanel = new JPanel();
        JPanel responsePanel = new JPanel();
        JPanel decisionPanel = new JPanel();

        setName("detailPanel");

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        requestPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Request"),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)));

        requestPanel.setLayout(new BoxLayout(requestPanel, BoxLayout.PAGE_AXIS));
        subjectPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Subject"),
                BorderFactory.createEmptyBorder(5, 15, 5, 5)));

        subjectPanel.setLayout(new GridLayout(0, 2));

        if (event != null) {
            Iterator it = event.getSubjectAttributes().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                JLabel label = new JLabel();
                StringBuilder sb = new StringBuilder();
                sb.append("<html><body>");
                sb.append(pairs.getKey());
                sb.append("=");
                sb.append(AttributeFactory.getStringValue((AttributeValue) pairs.getValue()));
                sb.append("</br></body></html>");
                label.setText(sb.toString());
                subjectPanel.add(label);
            }
        }

        resourcePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Resource"),
                BorderFactory.createEmptyBorder(5, 15, 5, 5)));

        resourcePanel.setLayout(new GridLayout(0, 2));

        if (event != null) {
            Iterator it = event.getResourceAttributes().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                JLabel label = new JLabel();
                StringBuilder sb = new StringBuilder();
                sb.append("<html><body>");
                sb.append(pairs.getKey());
                sb.append("=");
                sb.append(AttributeFactory.getStringValue((AttributeValue) pairs.getValue()));
                sb.append("</br></body></html>");
                label.setText(sb.toString());
                resourcePanel.add(label);
            }
        }

        actionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Action"),
                BorderFactory.createEmptyBorder(5, 15, 5, 5)));

        actionPanel.setLayout(new GridLayout(0, 2));

        if (event != null) {
            Iterator it = event.getActionAttributes().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                JLabel label = new JLabel();
                StringBuilder sb = new StringBuilder();
                sb.append("<html><body>");
                sb.append(pairs.getKey());
                sb.append("=");
                sb.append(AttributeFactory.getStringValue((AttributeValue) pairs.getValue()));
                sb.append("</br></body></html>");
                label.setText(sb.toString());
                actionPanel.add(label);
            }
        }

        envPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Environment"),
                BorderFactory.createEmptyBorder(5, 15, 5, 5)));

        envPanel.setLayout(new GridLayout(0, 2));

        if (event != null) {
            Iterator it = event.getEnvironmentAttributes().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                JLabel label = new JLabel();
                StringBuilder sb = new StringBuilder();
                sb.append("<html><body>");
                sb.append(pairs.getKey());
                sb.append("=");
                sb.append(AttributeFactory.getStringValue((AttributeValue) pairs.getValue()));
                sb.append("</br></body></html>");
                label.setText(sb.toString());
                envPanel.add(label);
            }
        }


        responsePanel.setLayout(new BoxLayout(responsePanel, BoxLayout.PAGE_AXIS));
        responsePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Response"),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)));

        decisionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Decision"),
                BorderFactory.createEmptyBorder(5, 15, 5, 5)));

        decisionPanel.setLayout(new GridLayout(0, 2));
        JLabel decision = new JLabel();
        if (event != null) {
            try {
                decision.setText(event.getDecision());
            } catch (JSONException ex) {
                Logger.getLogger(EventDetailPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        decisionPanel.add(decision);

        requestPanel.add(subjectPanel);
        requestPanel.add(resourcePanel);
        requestPanel.add(actionPanel);
        requestPanel.add(envPanel);

        responsePanel.add(decisionPanel);

        requestPanel.setVisible(true);
        add(requestPanel);
        add(responsePanel);

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Audit Record Detail"),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)));
    }
}
