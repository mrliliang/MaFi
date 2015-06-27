package com.mafi.wifi;

import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.content.Context;

import java.util.List;

public class WifiAdmin {
	// 定义WifiManager对象    
    private WifiManager mWifiManager; 
    // 定义WifiInfo对象    
    private WifiInfo mWifiInfo;   
    // 扫描出的网络连接列表    
    private List<ScanResult> mWifiList;   
    // 网络连接列表    
    private List<WifiConfiguration> mWifiConfiguration;   
    // 定义一个WifiLock    
    WifiLock mWifiLock;   
  
   
    // 构造器    
    public WifiAdmin(Context context) {   
        // 取得WifiManager对象    
        mWifiManager = (WifiManager) context   
                .getSystemService(Context.WIFI_SERVICE);   
        // 取得WifiInfo对象    
        mWifiInfo = mWifiManager.getConnectionInfo();   
    }   
   
    // 打开WIFI    
    public boolean openWifi() {   
        if (!mWifiManager.isWifiEnabled()) {   
            return mWifiManager.setWifiEnabled(true);   
        }
        return true;
    }   
   
    // 关闭WIFI    
    public void closeWifi() {   
        if (mWifiManager.isWifiEnabled()) {   
            mWifiManager.setWifiEnabled(false);   
        }   
    }   
   
    // 检查当前WIFI状态    
    public int checkState() {   
        return mWifiManager.getWifiState();   
    }   
   
    // 锁定WifiLock    
    public void acquireWifiLock() {   
        mWifiLock.acquire();   
    }   
   
    // 解锁WifiLock    
    public void releaseWifiLock() {   
        // 判断时候锁定    
        if (mWifiLock.isHeld()) {   
            mWifiLock.acquire();   
        }   
    }   
   
    // 创建一个WifiLock    
    public void creatWifiLock() {   
        mWifiLock = mWifiManager.createWifiLock("Test");   
    }   
   
    // 得到配置好的网络    
    public List<WifiConfiguration> getConfiguration() {   
        return mWifiConfiguration;   
    }   
   
    // 指定配置好的网络进行连接    
    public void connectConfiguration(int index) {   
        // 索引大于配置好的网络索引返回    
        if (index > mWifiConfiguration.size()) {   
            return;   
        }   
        // 连接配置好的指定ID的网络    
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,   
                true);   
    }   
   
    public void startScan() {   
        mWifiManager.startScan();   
        // 得到扫描结果    
        mWifiList = mWifiManager.getScanResults();   
        // 得到配置好的网络连接    
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();   
    }   
   
    // 得到网络列表    
    public List<ScanResult> getWifiList() {   
        return mWifiList;   
    }   
   
    // 查看扫描结果    
    public StringBuilder lookUpScan() {   
        StringBuilder stringBuilder = new StringBuilder();   
        for (int i = 0; i < mWifiList.size(); i++) {   
            stringBuilder   
                    .append("Index_" + new Integer(i + 1).toString() + ":");   
            // 将ScanResult信息转换成一个字符串包    
            // 其中把包括：BSSID、SSID、capabilities、frequency、level    
            stringBuilder.append((mWifiList.get(i)).toString());   
            stringBuilder.append("/n");   
        }   
        return stringBuilder;   
    }  
   
    // 得到MAC地址    
    public String getMacAddress() {   
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();   
    }   
   
    // 得到接入点的BSSID    
    public String getBSSID() {   
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();   
    }   
   
    // 得到IP地址    
    public int getIPAddress() {   
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();   
    }   
   
    // 得到连接的ID    
    public int getNetworkId() {   
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();   
    }   
   
    // 得到WifiInfo的所有信息包    
    public String getWifiInfo() {   
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();   
    }   
   
    // 添加一个网络并连接    
    public boolean addNetwork(WifiConfiguration wcg) {   
     int wcgID = mWifiManager.addNetwork(wcg);   
     boolean b =  mWifiManager.enableNetwork(wcgID, true);   
     return b;  
    }   
   
    // 断开指定ID的网络    
    public void disconnectWifi(int netId) {   
        mWifiManager.disableNetwork(netId);   
        mWifiManager.disconnect();   
    }   
   
//然后是一个实际应用方法，只验证过没有密码的情况：  
   
    public WifiConfiguration createWifiInfo(String SSID, String Password, int Type)   
    {   
          WifiConfiguration config = new WifiConfiguration();     
          config.allowedAuthAlgorithms.clear();   
          config.allowedGroupCiphers.clear();   
          config.allowedKeyManagement.clear();   
          config.allowedPairwiseCiphers.clear();   
          config.allowedProtocols.clear();   
          config.SSID = "\"" + SSID + "\"";     
            
          WifiConfiguration tempConfig = this.IsExsits(SSID);             
          if(tempConfig != null) {    
              mWifiManager.removeNetwork(tempConfig.networkId);    
          }  
            
          if(Type == 1) //WIFICIPHER_NOPASS  
          {   
               config.wepKeys[0] = "";   
               config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);   
               config.wepTxKeyIndex = 0;   
          }   
          if(Type == 2) //WIFICIPHER_WEP  
          {   
              config.hiddenSSID = true;  
              config.wepKeys[0]= "\""+Password+"\"";   
              config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);   
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);   
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);   
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);   
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);   
              config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);   
              config.wepTxKeyIndex = 0;   
          }   
          if(Type == 3) //WIFICIPHER_WPA  
          {   
	          config.preSharedKey = "\""+Password+"\"";   
	          config.hiddenSSID = true;     
	          config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);     
	          config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);                           
	          config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);                           
	          config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);                      
	          //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);    
	          config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);  
	          config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);  
	          config.status = WifiConfiguration.Status.ENABLED;     
          }  
          return config;
    }   
      
    private WifiConfiguration IsExsits(String SSID)    
    {    
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();    
           for (WifiConfiguration existingConfig : existingConfigs)     
           {    
             if (existingConfig.SSID.equals("\""+SSID+"\""))    
             {    
                 return existingConfig;    
             }    
           }    
        return null;     
    }
    
    private boolean isWifiInScope(String SSID) {
    	for(int i = 0; i < mWifiList.size(); i++) {
    		if(SSID.equals(mWifiList.get(i).SSID))
    			return true;
    	}
    	return false;
    }
    
    public boolean connectWifi(String ssid, String password, int type) throws Exception {
    	if(!openWifi()) {
    		throw new Exception("启动Wifi失败");
    	}
    	
    	startScan();
    	
    	if(!isWifiInScope(ssid)) {
    		throw new Exception("Wifi不在范围内");
    	}
    	
    	WifiConfiguration wifiConfig = createWifiInfo(ssid, password, type);
    	return addNetwork(wifiConfig);
    }
}
