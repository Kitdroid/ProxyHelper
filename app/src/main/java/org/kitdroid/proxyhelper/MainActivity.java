package org.kitdroid.proxyhelper;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.IpConfiguration;
import android.net.LinkProperties;
import android.net.Network;
import android.net.ProxyInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.Toast;

import org.kitdroid.helper.ContextMate;
import org.kitdroid.helper.DialogHelper;
import org.kitdroid.helper.Logger;
import org.kitdroid.helper.Toaster;
import org.kitdroid.proxyhelper.adapter.ProxyConfigAdatper;
import org.kitdroid.proxyhelper.proxy.ProxyDataManager;
import org.kitdroid.proxyhelper.proxy.ProxyEntity;
import org.kitdroid.proxyhelper.wifi.RefInvoker;
import org.kitdroid.proxyhelper.wifi.WifiConnect;
import org.kitdroid.util.IntentUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnItemLongClickListener, OnCheckedChangeListener {

    public static final int REQUEST_CODE_EDIT_PROXY = 1;
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String PROXYSETTINGS_STATIC = "STATIC";
    public static final String PROXYSETTINGS_NONE = "NONE";
    private ListView mListView;
    private ProxyConfigAdatper adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentUtils.startActivityForResult(getActivity(), ProxyDetailEditActivity.class, REQUEST_CODE_EDIT_PROXY);

            }
        });


        mListView = (ListView) findViewById(R.id.listView);
        mListView.setOnItemLongClickListener(this);

        adapter = new ProxyConfigAdatper(LayoutInflater.from(getActivity()), this);
        mListView.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        ProxyEntity currentWiFiProxy = getCurrentWiFiProxy();
        if (currentWiFiProxy != null) {
            adapter.setCurrentProxy(currentWiFiProxy.getHost(), currentWiFiProxy.getPort());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings: {
                IntentUtils.startActivity(getActivity(), SettingsActivity.class);
                return true;
            }
            case R.id.action_wificonfig: {
                IntentUtils.startActivity(getActivity(), WiFiConfigListActivity.class);
                return true;
            }
            case R.id.action_about: {
                IntentUtils.startActivity(getActivity(), SettingsAboutActivity.class);
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_EDIT_PROXY: {
                onProxysDataChanged();
                break;
            }
            default: {

            }
        }
    }

    private void onProxysDataChanged() {
        adapter.notifyDataSetChanged();
    }

    @NonNull
    private MainActivity getActivity() {
        return MainActivity.this;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        ProxyEntity item = (ProxyEntity) adapter.getItem(position);
        showDelDailog(item);
        return true;
    }

    private void showDelDailog(final ProxyEntity entity) {
        String proxyDesc = entity.getHost() + ":" + entity.getPort();
        DialogHelper.showConfirmDialog(getActivity(), "删除代理:\n" + proxyDesc, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (entity.equals(getCurrentWiFiProxy())) {
                    setProxy(null);
                }
                ProxyDataManager.getInstance().removeProxy(entity);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            Object tag = buttonView.getTag();
            if (tag != null && tag instanceof ProxyEntity) {
                setProxy((ProxyEntity) tag);
            }
        } else {
            setProxy(null);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setProxy(@Nullable ProxyEntity entity) {
        WifiConnect wifiConnect = new WifiConnect(MainActivity.this);
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
    }

    @NonNull
    private String getWiFiConigKey(String ssid) {
        // TODO
        return "Welcome@123";
    }

    private ProxyEntity getCurrentWiFiProxy() {
        WifiConnect wifiConnect = new WifiConnect(MainActivity.this);
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
        Toast.makeText(MainActivity.this, "没有代理,添加代理", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(MainActivity.this, "有代理,取消代理", Toast.LENGTH_SHORT).show();
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

}
