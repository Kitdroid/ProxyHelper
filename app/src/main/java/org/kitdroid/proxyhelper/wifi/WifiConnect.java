package org.kitdroid.proxyhelper.wifi;

/**
 * Created by huiyh on 14/11/23.
 */

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build.VERSION;

import org.kitdroid.helper.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class WifiConnect {
    private static final String TAG = WifiConnect.class.getSimpleName();
    WifiManager wifiManager;
    private List<ScanResult> wifiList;
    private List<WifiConfiguration> wifiConfigurations;

    //定义几种加密方式，一种是WEP，一种是WPA，还有没有密码的情况
    public enum WifiCipherType {
        INVALID,
        NOPWD,
        WPA,
        WEP;
//
//        private final String name;
//        private final int index;
//
//        private WifiCipherType(String name, int index) {
//            this.name = name;
//            this.index = index;
//        }
//
//        @Override
//        public String toString() {
//            return getClass().getSimpleName() +"."+name+"_"+index;
//        }
    }

    //构造函数
    public WifiConnect(WifiManager wifiManager) {
        this.wifiManager = wifiManager;
    }

    public WifiConnect(Context context) {
        this.wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * 打开wifi功能
     *
     * @return {@code true} if the operation succeeds (or if the existing state
     * is the same as the requested state).
     */
    public boolean openWifi() {
        boolean bRet = true;
        if (!wifiManager.isWifiEnabled()) {
            bRet = wifiManager.setWifiEnabled(true);
        }
        return bRet;
    }

    /**
     * 关闭wifi功能
     *
     * @return {@code true} if the operation succeeds (or if the existing state
     * is the same as the requested state).
     */
    public boolean closeWifi() {
        boolean bRet = true;
        if (wifiManager.isWifiEnabled()) {
            bRet = wifiManager.setWifiEnabled(false);
        }
        return bRet;
    }

    /**
     * 检查当前wifi状态
     **/
    public int getWifiState() {
        return wifiManager.getWifiState();

    }

    public WifiInfo getConnected() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo;
    }

    public void scan() {
        wifiManager.startScan();
    }

    public void notifyWifiChanged() {
        //得到扫描结果
        wifiList = wifiManager.getScanResults();
        //得到配置好的网络连接
        wifiConfigurations = wifiManager.getConfiguredNetworks();
    }

    private void printWifiList() {
        for (ScanResult wifi : wifiList) {
            Logger.i(TAG, "printWifiList: " + wifi.SSID);
        }
    }

    public List<ScanResult> getWifiList() {
        return wifiList;
    }

    public List<WifiConfiguration> getWifiConfigurations() {
        return wifiConfigurations;
    }

    public boolean isInRange(String ssid) {
        for (ScanResult wifi : wifiList) {
            Logger.i(TAG, "isInRange: " + wifi.SSID);
            if (wifi.SSID.equals(ssid)) {
                return true;
            }
        }
        return false;
    }

    public boolean updateConfig(WifiConfiguration configuration) {
        int i = wifiManager.updateNetwork(configuration);
        if (i == -1) {
            return false;
        }
        wifiManager.disableNetwork(configuration.networkId);
        if (VERSION.SDK_INT <= 19) {
//            wifiManager.removeNetwork(configuration.networkId);
//            int netID = wifiManager.addNetwork(configuration);

        }
        int netID = configuration.networkId;
        if (netID != -1) {
            return wifiManager.enableNetwork(netID, true);
        }
        return false;
    }

    //提供一个外部接口，传入要连接的无线网
    public boolean creatConnect(String SSID, String Password, WifiCipherType Type) {
        if (!this.openWifi()) {
            return false;
        }
        System.out.println(">>>wifiCon=");
        //开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
        //状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
        while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            try {
                //为了避免程序一直while循环，让它睡个100毫秒在检测……
                Thread.currentThread();
                Thread.sleep(500);
            } catch (InterruptedException e) {

            }
        }

        WifiConfiguration wifiConfig = this.createWifiConfig(SSID, Password, Type);
        //
        if (wifiConfig == null) {
            return false;
        }
        WifiConfiguration tempConfig = isExsits(SSID);

        if (tempConfig != null) {
            wifiManager.removeNetwork(tempConfig.networkId);
        }

//        try {
//            //高级选项
//            String ip = "192.168.1.201";
//            int networkPrefixLength = 24;
//            InetAddress intetAddress = InetAddress.getByName(ip);
//            int intIp = inetAddressToInt(intetAddress);
//            String dns = (intIp & 0xFF) + "." + ((intIp >> 8) & 0xFF) + "." + ((intIp >> 16) & 0xFF) + ".1";
//            setIpAssignment("STATIC", wifiConfig); //"STATIC" or "DHCP" for dynamic setting
//            setIpAddress(intetAddress, networkPrefixLength, wifiConfig);
//            setGateway(InetAddress.getByName(dns), wifiConfig);
//            setDNS(InetAddress.getByName(dns), wifiConfig);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        int netID = wifiManager.addNetwork(wifiConfig);
        boolean bRet = wifiManager.enableNetwork(netID, true);
//	    	wifiManager.updateNetwork(wifiConfig);
        return bRet;
    }

    //查看以前是否也配置过这个网络
    public WifiConfiguration isExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    private WifiConfiguration createWifiConfig(String SSID, String Password, WifiCipherType Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        switch (Type) {
            case NOPWD: {
                config.wepKeys[0] = "";
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.wepTxKeyIndex = 0;
                break;
            }
            case WPA: {
                config.preSharedKey = "\"" + Password + "\"";
                config.hiddenSSID = true;
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                config.status = WifiConfiguration.Status.ENABLED;
                break;
            }
            case WEP: {
                config.preSharedKey = "\"" + Password + "\"";
                config.hiddenSSID = true;
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.wepTxKeyIndex = 0;
                break;
            }
            default:
                return null;
        }
        return config;
    }


    /**
     * Convert a IPv4 address from an InetAddress to an integer
     *
     * @param inetAddr is an InetAddress corresponding to the IPv4 address
     * @return the IP address as an integer in network byte order
     */
    public static int inetAddressToInt(InetAddress inetAddr) throws IllegalArgumentException {
        byte[] addr = inetAddr.getAddress();
        if (addr.length != 4) {
            throw new IllegalArgumentException("Not an IPv4 address");
        }
        return ((addr[3] & 0xff) << 24) | ((addr[2] & 0xff) << 16) | ((addr[1] & 0xff) << 8) | (addr[0] & 0xff);
    }

    public static void setIpAssignment(String assign, WifiConfiguration wifiConf) throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        setEnumField(wifiConf, assign, "ipAssignment");
    }

    public static void setEnumField(Object obj, String value, String name) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getField(name);
        f.set(obj, Enum.valueOf((Class<Enum>) f.getType(), value));
    }

    public static void setIpAddress(InetAddress addr, int prefixLength, WifiConfiguration wifiConf) throws SecurityException, IllegalArgumentException,
            NoSuchFieldException, IllegalAccessException, NoSuchMethodException, ClassNotFoundException, InstantiationException, InvocationTargetException {

        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties == null) {
            return;
        }
        Class laClass = Class.forName("android.net.LinkAddress");
        Constructor laConstructor = laClass.getConstructor(new Class[]{InetAddress.class, int.class});
        Object linkAddress = laConstructor.newInstance(addr, prefixLength);
        ArrayList mLinkAddresses = (ArrayList) getDeclaredField(linkProperties, "mLinkAddresses");
        mLinkAddresses.clear();
        mLinkAddresses.add(linkAddress);
    }

    public static void setGateway(InetAddress gateway, WifiConfiguration wifiConf) throws SecurityException, IllegalArgumentException,
            NoSuchFieldException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InstantiationException, InvocationTargetException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties == null) {
            return;
        }
        Class routeInfoClass = Class.forName("android.net.RouteInfo");
        Constructor routeInfoConstructor = routeInfoClass.getConstructor(new Class[]{InetAddress.class});
        Object routeInfo = routeInfoConstructor.newInstance(gateway);
        ArrayList mRoutes = (ArrayList) getDeclaredField(linkProperties, "mRoutes");
        mRoutes.clear();
        mRoutes.add(routeInfo);
    }

    public static void setDNS(InetAddress dns, WifiConfiguration wifiConf) throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties == null) {
            return;
        }
        ArrayList<InetAddress> mDnses = (ArrayList<InetAddress>) getDeclaredField(linkProperties, "mDnses");
        mDnses.clear(); // or add a new dns address , here I just want to replace DNS1
        mDnses.add(dns);
    }

    public static Object getField(Object obj, String name) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getField(name);
        Object out = f.get(obj);
        return out;
    }

    public static Object getDeclaredField(Object obj, String name) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        Object out = f.get(obj);
        return out;
    }

//	    public void editStaticWifiConfig(final ScanResult sr,String pwd, String ip, String gateway,int prefixLength,String dns) throws Exception{
//	    	WifiConfiguration historyWifiConfig = getHistoryWifiConfig(sr.SSID);
//
//	    	if(historyWifiConfig == null){
//	    		historyWifiConfig = createComWifiConfig(sr.SSID,pwd);
//	    		int netId = mWifiManager.addNetwork(historyWifiConfig);
//	    		mWifiManager.enableNetwork(netId, true);
//	    	}
//
//	        setIpAssignment("STATIC", historyWifiConfig); //"STATIC" or "DHCP" for dynamic setting
//	        setIpAddress(InetAddress.getByName(ip), prefixLength, historyWifiConfig);
//	        setGateway(InetAddress.getByName(gateway), historyWifiConfig);
//	        setDNS(InetAddress.getByName(dns), historyWifiConfig);
//
//	        mWifiManager.updateNetwork(historyWifiConfig); //apply the setting
//		}
//
//	    public void editDhcpWifiConfig(final ScanResult sr,String pwd) throws Exception{
//	    	WifiConfiguration historyWifiConfig = getHistoryWifiConfig(sr.SSID);
//
//	    	if(historyWifiConfig == null){
//	    		historyWifiConfig = createComWifiConfig(sr.SSID,pwd);
//	    		int netId = mWifiManager.addNetwork(historyWifiConfig);
//	    		mWifiManager.enableNetwork(netId, true);
//	    	}
//
//	        setIpAssignment("DHCP", historyWifiConfig); //"STATIC" or "DHCP" for dynamic setting
//
//	        mWifiManager.updateNetwork(historyWifiConfig); //apply the setting
//		}
}
