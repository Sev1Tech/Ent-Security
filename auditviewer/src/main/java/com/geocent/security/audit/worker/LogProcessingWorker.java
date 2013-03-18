package com.geocent.security.audit.worker;

import com.geocent.security.audit.event.PEPAuditEvent;
import java.util.List;
import javax.swing.SwingWorker;

/**
 *
 * @author bpriest
 */
public class LogProcessingWorker extends SwingWorker<List<PEPAuditEvent>, Void>{

    @Override
    protected List<PEPAuditEvent> doInBackground() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    protected void done(){
        
    }
    
}
