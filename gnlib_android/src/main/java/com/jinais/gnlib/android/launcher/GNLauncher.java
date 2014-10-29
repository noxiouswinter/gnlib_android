package com.jinais.gnlib.android.launcher;

import android.content.Context;
import android.content.Intent;
import com.jinais.gnlib.android.LogGN;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by jkader on 10/20/14.
 */
public class GNLauncher {

    private static GNLauncher sharedInstance = null;
    private CustomInvocationHandler customInvocationHandler = null;

    public static GNLauncher get() {
        if (sharedInstance == null) {
            sharedInstance = new GNLauncher();
        }
        return sharedInstance;
    }

    public Object getProxy(Context context, Class activityInterfaceClass, Class activityClass) {
        if (sharedInstance == null) {
            sharedInstance = new GNLauncher();
        }

        clearCachedLaunchData();
        this.customInvocationHandler = new CustomInvocationHandler(context, activityClass);
        return Proxy.newProxyInstance(activityInterfaceClass.getClassLoader(), new Class<?>[]{activityInterfaceClass}, this.customInvocationHandler);
    }

    public void ping(Context activityObject) {
        if (activityObject.getClass().equals(this.customInvocationHandler.getActivityClass())) {
            try {
                Object result = this.customInvocationHandler.getMethod().invoke(activityObject, this.customInvocationHandler.getArgs());
                clearCachedLaunchData();
            } catch (IllegalAccessException e) {
                LogGN.e(e);
            } catch (InvocationTargetException e) {
                LogGN.e(e);
            }
        }
    }

    private void clearCachedLaunchData() {
        this.customInvocationHandler = null;
    }

    private static class CustomInvocationHandler implements InvocationHandler {

        Object proxy = null;
        Method method = null;
        Object[] args = null;

        Class activityClass = null;
        Context context = null;

        public CustomInvocationHandler(Context context, Class activityClass) {
            this.activityClass = activityClass;
            this.context = context;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            this.proxy = proxy;
            this.method = method;
            this.args = args;

            Intent intent = new Intent(this.context, this.activityClass);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            this.context.startActivity(intent);

            return null;
        }

        public Object getProxy() {
            return proxy;
        }

        public Method getMethod() {
            return method;
        }

        public Object[] getArgs() {
            return args;
        }

        public Class getActivityClass() {
            return activityClass;
        }

        public Context getContext() {
            return context;
        }
    }
}
