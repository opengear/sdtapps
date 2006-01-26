/*
 * SwingExecutorService.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sdtconnector;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;

/**
 *
 */
public class SwingExecutorService extends AbstractExecutorService {
    
    /** Creates a new instance of SwingExecutorService */
    public SwingExecutorService() {
    }
    
    public void shutdown() {
    }
    
    public List<Runnable> shutdownNow() {
        return Collections.emptyList();
    }
    
    public boolean isShutdown() {
        return false;
    }
    
    public boolean isTerminated() {
        return false;
    }
    
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return true;
    }
    
    public void execute(Runnable command) {
        try {
            if (SwingUtilities.isEventDispatchThread()) {
                command.run();
            } else {
                SwingUtilities.invokeAndWait(command);
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }
    
}
