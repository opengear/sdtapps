/*
 * SwingInvocationProxy.java
 *
 * Created on February 5, 2006, 2:44 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.opengear.util;

import edu.emory.mathcs.backport.java.util.concurrent.Callable;
import edu.emory.mathcs.backport.java.util.concurrent.ExecutionException;
import edu.emory.mathcs.backport.java.util.concurrent.FutureTask;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import javax.swing.SwingUtilities;

/**
 *
 * @author wayne
 */
public class SwingInvocationProxy {
    
    /** Creates a new instance of SwingInvocationProxy */
    private SwingInvocationProxy() {
    }
         
    public static Object create(Class handlerClass, Object handler) {
        return Proxy.newProxyInstance(handlerClass.getClassLoader(),
                new Class[] { handlerClass }, new SwingProxyHandler(handler));
    }
    
}
class SwingProxyHandler implements InvocationHandler {
    public SwingProxyHandler(Object handler) {
        _handler = handler;
    }
    
    public Object invoke(final Object proxy, final Method method,
            final Object[] args) throws Throwable {
        // Execute this method on the Executor and wait for it to complete
        FutureTask ft = new FutureTask(new Callable() {
            public Object call() throws Exception {
                return method.invoke(_handler, args);                
            }
        });
        SwingUtilities.invokeAndWait(ft);
        try {
            return ft.get();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }        
    }
    private Object _handler;  
}
