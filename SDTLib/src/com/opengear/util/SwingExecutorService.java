/*
 * SwingExecutorService.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.opengear.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import edu.emory.mathcs.backport.java.util.concurrent.AbstractExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;

/**
 *
 */
public class SwingExecutorService extends AbstractExecutorService {
    
    /** Creates a new instance of SwingExecutorService */
    public SwingExecutorService() {
    }
    
    public void shutdown() {
        _running = false;
    }
    
    public List<Runnable> shutdownNow() {
        _running = false;
        return Collections.emptyList();
    }
    
    public boolean isShutdown() {
        return !_running;
    }
    
    public boolean isTerminated() {
        return !_running;
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
        } catch (InvocationTargetException ex) {
        }
    }
    private boolean _running = true;
}
