/*
 * ExecutorProxy.java
 *
 * Created on February 3, 2006, 9:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
        
package sdtconnector;
import edu.emory.mathcs.backport.java.util.concurrent.Callable;
import edu.emory.mathcs.backport.java.util.concurrent.Executor;
import edu.emory.mathcs.backport.java.util.concurrent.FutureTask;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 *
 * @author wayne
 */
public class ExecutorProxy {
    
    /** Creates a new instance of ExecutorProxy */
    public ExecutorProxy() {
        
    }
    public static Object create(Executor exec, Class handlerClass, Object handler) {
        return Proxy.newProxyInstance(handlerClass.getClassLoader(),
                new Class[] { handlerClass }, new ExecutorProxyHandler(exec, handler));
    }
    
}
class ExecutorProxyHandler implements InvocationHandler {
    public ExecutorProxyHandler(Executor exec, Object handler) {
        _handler = handler;
        _exec = exec;
    }
    
    public Object invoke(final Object proxy, final Method method,
            final Object[] args) throws Throwable {
        // Tell Ecore to execute this method from the main Ecore thread
        FutureTask ft = new FutureTask(new Callable() {
            
            public Object call() {
                try {
                    return method.invoke(_handler, args);
                } catch (Throwable t) {
                    return null;
                }
            }
        });
        _exec.execute(ft);
        return ft.get();
    }
    private Object _handler;
    private Executor _exec;
}