package com.geocent.security.audit.view;

import com.geocent.security.audit.event.PEPAuditEvent;
import com.geocent.security.audit.model.AuditModel;
import com.geocent.security.audit.model.AuditTableModel;
import com.geocent.security.audit.worker.LogIndexingTask;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import org.json.JSONException;

/**
 *
 * @author bpriest
 */
public class AuditView extends JFrame {

    private JTable auditTable;
    private JLabel resultsLbl;
    private JTextArea searchField;
    private JScrollPane scrollPane;
    private JButton queryBtn;
    private JPopupMenu contextMenu;
    private JMenuBar menuBar;
    private JMenuItem rawEventMenuItem;
    private JMenuItem xacmlRequestMenuItem;
    private ListSelectionModel listSelectionModel;
    private EventDetailPanelHandler detailPanelHandler;
    private final String OPEN_RAW_EVENT = "Open Raw Event";
    private final String CREATE_XACML_REQ = "Create XACML Request";
    private final String QUERY = "Query";

    public AuditView() throws JSONException {
        super();
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(true);
        this.setContentPane(contentPanel);
        this.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        parseAuditLog();

        menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        JMenuItem openMenuItem = new JMenuItem("Open");
        fileMenu.add(openMenuItem);
        setJMenuBar(menuBar);
        
        resultsLbl = new JLabel();
        
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new GridLayout(2, 1));
        searchPanel.setMaximumSize(new Dimension(2000, 100));

        searchField = new JTextArea();
        searchField.setBorder(BorderFactory.createEtchedBorder());
        searchField.setLineWrap(true);
        searchField.setWrapStyleWord(true);
        queryBtn = new JButton();

        queryBtn.setText(QUERY);

        contextMenu = new JPopupMenu();
        rawEventMenuItem = new JMenuItem(OPEN_RAW_EVENT);
        xacmlRequestMenuItem = new JMenuItem(CREATE_XACML_REQ);
        
        contextMenu.add(rawEventMenuItem);
        contextMenu.add(xacmlRequestMenuItem);
        
        searchPanel.add(searchField);
        searchPanel.add(queryBtn);
        searchPanel.add(resultsLbl);

        add(searchPanel);

        auditTable = new JTable();

        auditTable.setPreferredScrollableViewportSize(new Dimension(800, 200));
        auditTable.setFillsViewportHeight(true);
        listSelectionModel = auditTable.getSelectionModel();
        auditTable.setSelectionModel(listSelectionModel);
        auditTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        loadTableData(AuditModel.getInstance().getAuditEvents());

        //Create the scroll pane and add the table to it.
        scrollPane = new JScrollPane(auditTable);
        scrollPane.setMaximumSize(new Dimension(2000, 500));
        scrollPane.setMinimumSize(new Dimension(0, 500));
        //Add the scroll pane to this panel.
        add(scrollPane);

        EventDetailPanel detailPanel = new EventDetailPanel();

        detailPanelHandler = new EventDetailPanelHandler(detailPanel);

        add(detailPanel);

        auditTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                int r = auditTable.rowAtPoint(e.getPoint());
                if (r >= 0 && r < auditTable.getRowCount()) {
                    auditTable.setRowSelectionInterval(r, r);
                } else {
                    auditTable.clearSelection();
                }

                int rowindex = auditTable.getSelectedRow();
                if (rowindex < 0) {
                    return;
                }
                if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
                    contextMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    public EventDetailPanelHandler getDetailPanelHandler() {
        return detailPanelHandler;
    }

    public void setDetailPanelHandler(EventDetailPanelHandler handler) {
        this.detailPanelHandler = handler;
    }

    public JLabel getResultsLbl() {
        return resultsLbl;
    }

    public void setResultsLbl(JLabel resultsLbl) {
        this.resultsLbl = resultsLbl;
    }

    public JTextArea getSearchField() {
        return searchField;
    }

    public void setSearchField(JTextArea searchField) {
        this.searchField = searchField;
    }

    public JTable getAuditTable() {
        return auditTable;
    }

    public void setAuditTable(JTable table) {
        this.auditTable = table;
    }

    private void parseAuditLog() throws JSONException {
        JFileChooser jc = new JFileChooser();
        int returnVal = jc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            new LogIndexingTask(jc.getSelectedFile(), this).execute();
        }
    }

    public void loadTableData(List<PEPAuditEvent> events) {
        auditTable.setModel(new AuditTableModel(events));     
    }

    public void addTableSelectionListener(ListSelectionListener listener) {
        listSelectionModel.addListSelectionListener(listener);
    }

    public void addQueryBtnListener(ActionListener listener) {
        this.queryBtn.addActionListener(listener);
    }

    public void addRawEventActionListener(ActionListener listener) {
        this.rawEventMenuItem.addActionListener(listener);
    }
    
    public void addXacmlRequestActionListener(ActionListener listener){
        this.xacmlRequestMenuItem.addActionListener(listener);
    }

    public ListSelectionModel getListSelectionModel() {
        return listSelectionModel;
    }

    public void setListSelectionModel(ListSelectionModel listSelectionModel) {
        this.listSelectionModel = listSelectionModel;
    }
}
