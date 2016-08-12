package org.kitdroid.proxyhelper.wifi;

import android.content.Context;
import android.net.IpConfiguration;
import android.net.LinkProperties;
import android.net.ProxyInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import org.kitdroid.helper.ContextMate;
import org.kitdroid.helper.Logger;
import org.kitdroid.helper.Toaster;
import org.kitdroid.proxyhelper.proxy.ProxyEntity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huiyh on 2016/4/18.
 */
public class WiFiProxyManager {

    public static final String TAG = WiFiProxyManager.class.getSimpleName();
    public static final String PROXYSETTINGS_STATIC = "STATIC";
    public static final String PROXYSETTINGS_NONE = "NONE";

    private static WiFiProxyManager sInstance;

    private final Context mContext = ContextMate.getApplication();
    private List<OnProxyChangeListener> mOnProxyChangeListeners;

    public static WiFiProxyManager getInstance() {
        if (sInstance == null) {
            synchronized (WiFiProxyManager.class) {
                if (sInstance == null) {
                    sInstance = new WiFiProxyManager();
                }
            }
        }
        return sInstance;
    }

    private WiFiProxyManager() {
    }

    public void addOnProxyChangeListener(OnProxyChangeListener listener) {
        if (mOnProxyChangeListeners == null) {
            mOnProxyChangeListeners = new ArrayList<>();
        }
        mOnProxyChangeListeners.add(listener);
    }

    public void removeOnProxyChangeListener(OnProxyChangeListener listener) {
        if (mOnProxyChangeListeners != null) {
            mOnProxyChangeListeners.remove(listener);
        }
    }

    public void setProxy(@Nullable ProxyEntity entity) {
        WifiConnect wifiConnect = new WifiConnect(mContext);
        WifiInfo connected = wifiConnect.getConnected();
        if (connected == null) {
            Toaster.showShort("未发现正在使用的WiFi连接");
            return;
        }

        String ssid = connected.getSSID();
        if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
            ssid = ssid.substring(1, ssid.length() - 1);
        }
        WifiConfiguration config = wifiConnect.isExsits(ssid);
        if (config == null) {
            // TODO EventPoint
            Toaster.showShort("未找到对应的WifiConfiguration: " + ssid);
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            IpConfiguration ipConfiguration = config.getIpConfiguration();
            if (entity != null) {
                // enable http proxy
                ipConfiguration.setProxySettings(IpConfiguration.ProxySettings.STATIC);
                ipConfiguration.setIpAssignment(IpConfiguration.IpAssignment.DHCP);
                ipConfiguration.setHttpProxy(new ProxyInfo(entity.getHost(), entity.getPort(), null));
            } else {
                // disable http proxy
                ipConfiguration.setProxySettings(IpConfiguration.ProxySettings.NONE);
                ipConfiguration.setHttpProxy(null);
            }
            config.setIpConfiguration(ipConfiguration);

        } else {

            LinkProperties linkProperties = (LinkProperties) RefInvoker.getFieldObject(config, WifiConfiguration.class, "linkProperties");
            if (linkProperties == null) {
                // TODO EventPoint
                Toaster.showShort("反射获取LinkProperties linkProperties失败");
                return;
            }

            if (entity != null) {
                addWiFiProxy(config, linkProperties, entity);
            } else {
                removeWifiProxy(config, linkProperties);
            }

            config.preSharedKey = getWiFiConigKey(ssid);

        }

        wifiConnect.updateConfig(config);

        if (mOnProxyChangeListeners != null) {
            for (OnProxyChangeListener listener : mOnProxyChangeListeners) {
                listener.onProxyChange(entity);
            }
        }
    }

    @NonNull
    private String getWiFiConigKey(String ssid) {
        // TODO
        return "Welcome@123";
    }

    public ProxyEntity getCurrentWiFiProxy() {
        WifiConnect wifiConnect = new WifiConnect(mContext);
        WifiInfo connected = wifiConnect.getConnected();
        if (connected == null) {
            Toaster.showShort("未发现正在使用的WiFi连接");
            return null;
        }

        String ssid = connected.getSSID();
        if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
            ssid = ssid.substring(1, ssid.length() - 1);
        }
        WifiConfiguration config = wifiConnect.isExsits(ssid);
        if (config == null) {
            // TODO EventPoint
            Toaster.showShort("未找到对应的WifiConfiguration: " + ssid);
            return null;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            IpConfiguration ipConfiguration = config.getIpConfiguration();
            if (ipConfiguration.getProxySettings() == IpConfiguration.ProxySettings.STATIC && ipConfiguration.getHttpProxy() != null) {
                ProxyEntity entity = new ProxyEntity();
                entity.setHost(ipConfiguration.getHttpProxy().getHost());
                entity.setPort(ipConfiguration.getHttpProxy().getPort());
                return entity;
            }
        } else {

            LinkProperties linkProperties = (LinkProperties) RefInvoker.getFieldObject(config, WifiConfiguration.class, "linkProperties");
            if (linkProperties == null) {
                // TODO EventPoint
                Toaster.showShort("反射获取LinkProperties linkProperties失败");
                return null;
            }
            Object mHttpProxy = RefInvoker.getFieldObject(linkProperties, LinkProperties.class, "mHttpProxy");
            if (mHttpProxy != null) {
                String mHost = (String) RefInvoker.getFieldObject(mHttpProxy, mHttpProxy.getClass(), "mHost");
                Integer mPort = (Integer) RefInvoker.getFieldObject(mHttpProxy, mHttpProxy.getClass(), "mPort");
                ProxyEntity entity = new ProxyEntity();
                entity.setHost(mHost);
                entity.setPort(mPort != null ? mPort : 80);
                return entity;
            }
        }
        return null;
    }


    private void addWiFiProxy(WifiConfiguration config, LinkProperties linkProperties, ProxyEntity entity) {
        Toast.makeText(mContext, "没有代理,添加代理", Toast.LENGTH_SHORT).show();
        Field field = RefInvoker.findField(LinkProperties.class, "mHttpProxy");
        if (field != null) {
            Object httpProxy = createProxyConfig(field.getType(), entity.getHost(), entity.getPort(), "");
            setValue4mHttpProxy(linkProperties, httpProxy);
            setValue4proxySettings(config, PROXYSETTINGS_STATIC);
        } else {
            Logger.i(TAG, "Can't find field \"mHttpProxy\" in LinkProperties.class");
        }
    }

    private void removeWifiProxy(WifiConfiguration config, LinkProperties linkProperties) {
        Toast.makeText(mContext, "有代理,取消代理", Toast.LENGTH_SHORT).show();
        setValue4mHttpProxy(linkProperties, null);
        setValue4proxySettings(config, PROXYSETTINGS_NONE);
    }

    private void setValue4mHttpProxy(LinkProperties linkProperties, Object httpProxy) {
        RefInvoker.setFieldObject(linkProperties, LinkProperties.class, "mHttpProxy", httpProxy);
    }

    private boolean setValue4proxySettings(WifiConfiguration config, String name) {
        Object proxySettings = RefInvoker.getFieldObject(config, WifiConfiguration.class, "proxySettings");
        if (proxySettings != null) {
            Object enumStatic = RefInvoker.getStaticFieldObject(proxySettings.getClass(), name);
            if (enumStatic != null) {
                RefInvoker.setFieldObject(config, WifiConfiguration.class, "proxySettings", enumStatic);
                return true;
            }
        }
        return false;
    }

    private Object createProxyConfig(Class<?> type, String host, int port, String ex) {
        try {
            Constructor<?> constructor = type.getConstructor(String.class, int.class, String.class);
            Object object = constructor.newInstance(host, port, ex);
            return object;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface OnProxyChangeListener {
        void onProxyChange(ProxyEntity entity);
    }
}
