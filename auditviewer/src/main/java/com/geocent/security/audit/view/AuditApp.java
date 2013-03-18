package com.geocent.security.audit.view;

import com.geocent.security.audit.controller.AuditController;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;

/**
 *
 * @author bpriest
 */
public final class AuditApp {


    public static void main(String[] args) throws JSONException {

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    createAndShowGUI();
                } catch (JSONException ex) {
                    Logger.getLogger(AuditApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private static void createAndShowGUI() throws JSONException {
        AuditView view = new AuditView();
        AuditController controller = new AuditController(view);
        
        view.pack();
        view.setVisible(true);
    }
}
