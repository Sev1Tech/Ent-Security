package com.geocent.security.audit.ui;

import com.geocent.security.audit.AttributeFactory;
import com.geocent.security.audit.PEPAuditEvent;
import com.geocent.security.audit.ui.model.AuditTableModel;
import com.sun.xacml.attr.AttributeValue;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.json.JSONException;

/**
 *
 * @author bpriest
 */
public final class AuditViewerApp extends JPanel implements ActionListener, ListSelectionListener {

    private JTable table;
    private JLabel resultsLbl;
    private JTextArea searchField;
    private JScrollPane scrollPane;
    private List<PEPAuditEvent> auditEvents;
    private AuditDetailPanelHandler handler;

    private enum QueryType {

        AND, OR, NOT
    }

    public AuditViewerApp() throws JSONException {
        super();
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        auditEvents = new ArrayList<PEPAuditEvent>();
        parseAuditLog();

        resultsLbl = new JLabel();
        resultsLbl.setText("Loaded " + auditEvents.size() + " total records.");
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new GridLayout(2, 1));
        searchPanel.setMaximumSize(new Dimension(2000, 100));

        searchField = new JTextArea();
        searchField.setBorder(BorderFactory.createEtchedBorder());
        searchField.setLineWrap(true);
        searchField.setWrapStyleWord(true);
        JButton query = new JButton();
        query.addActionListener(this);
        query.setText("Query");

        searchPanel.add(searchField);
        searchPanel.add(query);
        searchPanel.add(resultsLbl);

        add(searchPanel);

        table = new JTable();
        loadTableData(auditEvents);

        //Create the scroll pane and add the table to it.
        scrollPane = new JScrollPane(table);
        scrollPane.setMaximumSize(new Dimension(2000, 500));
        scrollPane.setMinimumSize(new Dimension(0, 500));
        //Add the scroll pane to this panel.
        add(scrollPane);

        AuditDetailPanel detailPanel = new AuditDetailPanel();

        handler = new AuditDetailPanelHandler(detailPanel);

        add(detailPanel);

        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                int r = table.rowAtPoint(e.getPoint());
                if (r >= 0 && r < table.getRowCount()) {
                    table.setRowSelectionInterval(r, r);
                } else {
                    table.clearSelection();
                }

                int rowindex = table.getSelectedRow();
                if (rowindex < 0) {
                    return;
                }
                if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
                    JPopupMenu popup = createTableContextMenu();
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    private JPopupMenu createTableContextMenu() {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem("Open Raw Event");
        menuItem.addActionListener(this);

        JMenuItem menuItem2 = new JMenuItem("Replay Request");
        menuItem2.addActionListener(this);
        popup.add(menuItem);
        popup.add(menuItem2);

        return popup;
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("Query")) {
            List<PEPAuditEvent> searchResults = performSearch(searchField.getText(), QueryType.AND);
            loadTableData(searchResults);
            this.resultsLbl.setText("Found " + searchResults.size() + " matches of " + auditEvents.size() + " total records.");
        } else if (ae.getActionCommand().equals("Open Raw Event")) {
            ListSelectionModel model = table.getSelectionModel();
            PEPAuditEvent e = ((AuditTableModel) table.getModel()).getAuditEventAt(model.getLeadSelectionIndex());
            RawEventPopup event = new RawEventPopup(e.getRawEvent());
            event.setVisible(true);
        } else if (ae.getActionCommand().equals("Replay Request")) {
            JOptionPane.showMessageDialog(this, "This feature is not yet implemented.", "", JOptionPane.WARNING_MESSAGE);
        }
    }

    private List<PEPAuditEvent> performSearch(String criteria, QueryType type) {
        if (criteria.equals("")) {
            return auditEvents;
        }

        List<PEPAuditEvent> searchResults = new ArrayList<PEPAuditEvent>();
        String[] singleCriteria = criteria.split(" ");

        for (PEPAuditEvent event : auditEvents) {
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

    private void loadTableData(List<PEPAuditEvent> events) {
        table.setModel(new AuditTableModel(events));
        table.setPreferredScrollableViewportSize(new Dimension(800, 200));
        table.setFillsViewportHeight(true);

        ListSelectionModel listSelectionModel = table.getSelectionModel();
        listSelectionModel.addListSelectionListener(this);
        table.setSelectionModel(listSelectionModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public void valueChanged(ListSelectionEvent lse) {
        if (!lse.getValueIsAdjusting()) {
            ListSelectionModel model = table.getSelectionModel();
            handler.updateDetailPanel(((AuditTableModel) table.getModel()).getAuditEventAt(model.getLeadSelectionIndex()));
        }
    }

    private void parseAuditLog() throws JSONException {

        JFileChooser jc = new JFileChooser();
        int returnVal = jc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = jc.getSelectedFile();
            BufferedReader reader = null;

            try {
                reader = new BufferedReader(new FileReader(file));
                String text = null;

                while ((text = reader.readLine()) != null) {
                    if (text.contains("PEP_AUDIT")) {
                        auditEvents.add(new PEPAuditEvent(text));
                    }
                }
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                }
            }
        }
    }

    public static void main(String[] args) throws JSONException {

        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                try {
                    createAndShowGUI();
                } catch (JSONException ex) {
                    Logger.getLogger(AuditViewerApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private static void createAndShowGUI() throws JSONException {
        JFrame frame = new JFrame("AuditViewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        AuditViewerApp newContentPane = new AuditViewerApp();
        newContentPane.setOpaque(true);
        frame.setContentPane(newContentPane);

        frame.pack();
        frame.setVisible(true);
    }

    class RawEventPopup extends JDialog {

        private JTextArea textArea;
        private JOptionPane optionPane;

        public RawEventPopup(String info) {
            super();

            textArea = new JTextArea();
            textArea.setText(info);
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setSize(800, 400);
            optionPane = new JOptionPane(textArea);

            setContentPane(optionPane);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            pack();
        }
    }
}
