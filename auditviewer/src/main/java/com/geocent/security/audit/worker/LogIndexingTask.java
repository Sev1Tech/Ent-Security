package com.geocent.security.audit.worker;

import com.geocent.security.audit.event.PEPAuditEvent;
import com.geocent.security.audit.model.AuditModel;
import com.geocent.security.audit.view.AuditView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import javax.swing.SwingWorker;

/**
 *
 * @author bpriest
 */
public class LogIndexingTask extends SwingWorker<List<PEPAuditEvent>, Void> {

    private AuditView view;
    private File logFile;

    public LogIndexingTask(File file, AuditView view) {
        this.view = view;
        this.logFile = file;
    }

    @Override
    protected List<PEPAuditEvent> doInBackground() throws Exception {
        File file = logFile;
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            String text = null;

            while ((text = reader.readLine()) != null) {
                if (text.contains("PEP_AUDIT")) {
                    AuditModel.getInstance().getAuditEvents().add(new PEPAuditEvent(text));
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
        return AuditModel.getInstance().getAuditEvents();
    }

    @Override
    protected void done() {
        view.getResultsLbl().setText("Loaded " + AuditModel.getInstance().getAuditEvents().size() + " total records.");
        view.loadTableData(AuditModel.getInstance().getAuditEvents());
    }
}
