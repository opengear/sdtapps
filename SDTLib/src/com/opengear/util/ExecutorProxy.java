package com.opengear.util;
/*
 * ExecutorProxy.java
 *
 * Copyright (c) 2005 Wayne Meissner
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to 
 * deal in the Software without restriction, including without limitation the 
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is 
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * IN THE SOFTWARE.
 */

import edu.emory.mathcs.backport.java.util.concurrent.Callable;
import edu.emory.mathcs.backport.java.util.concurrent.Executor;
import edu.emory.mathcs.backport.java.util.concurrent.FutureTask;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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
        // Execute this method on the Executor and wait for it to complete
        FutureTask ft = new FutureTask(new Callable() {
            public Object call() throws Exception {
                return method.invoke(_handler, args);                
            }
        });
        _exec.execute(ft);
        try {
            return ft.get();
        } catch (Exception ex) {        
            throw ex.getCause();
        }
    }
    private Object _handler;
    private Executor _exec;
}
