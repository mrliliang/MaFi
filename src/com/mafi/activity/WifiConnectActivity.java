package com.mafi.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mafi.wifi.WifiAdmin;
import com.mafi.main.R;
import com.mafi.receiver.WifiReceiver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WifiConnectActivity extends Activity {
	private EditText ssidField = null;
	private EditText passwordField = null;
	private EditText securityField = null;
	private Button connectBtn = null;
	private String ssid = null;
	private String password = null;
	private String security = null;
	private final String SSID_INDICATOR = "S";
	private final String PASSWORD_INDICATOR = "P";
	private final String SECURITY_INDICATOR = "T";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_connect);
		
		ssidField = (EditText) this.findViewById(R.id.ssid);
		passwordField = (EditText) this.findViewById(R.id.password);
		securityField = (EditText) this.findViewById(R.id.security_type);
		
		Intent qrcodeIntent = this.getIntent();
		Bundle data = qrcodeIntent.getExtras();
		String wifiStr = data.getString("result");
		
		//this.splitWifiData(wifiStr);
		
		//String ssid = getSSID(wifiStr);
		ssid = getWifiData(SSID_INDICATOR, wifiStr);
		ssidField.setText(ssid);
		//String password = getPassword(wifiStr);
		password = getWifiData(PASSWORD_INDICATOR, wifiStr);
		passwordField.setText(password);
		//String security = getSecurity(wifiStr);
		security = getWifiData(SECURITY_INDICATOR, wifiStr);
		if(security == null || "".equals(security) || "nopass".equals(security)) {
			security = "无加密";
		}
		securityField.setText(security);
		
		connectBtn = (Button) this.findViewById(R.id.connect_btn);
		connectBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					WifiAdmin admin = new WifiAdmin(WifiConnectActivity.this);
					if(admin.connectWifi(ssid, password, convertSecurityType(security))) {
						ProgressDialog dialog = ProgressDialog.show(WifiConnectActivity.this, "Wifi Connection", "Connecting...", true);
						WifiReceiver receiver = new WifiReceiver(dialog);
						IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
						WifiConnectActivity.this.registerReceiver(receiver, filter);
					} else {
						Toast.makeText(WifiConnectActivity.this, "连接失败", 3000).show();
					}
				} catch (Exception e) {
					Toast.makeText(WifiConnectActivity.this, e.getMessage(), 3000).show();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.wifi_connect, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private int convertSecurityType(String security) {
		int type = 0;
		if ("无加密".equals(security)) {
			type = 1;
		} else if ("WEP".equals(security)){
			type = 2;
		} else if ("WPA".equals(security)) {
			type = 3;
		}
		
		return type;
	}
	
	private String getWifiData(String indicator, String wifiStr) {
		String data = null;
		int beginIndex = wifiStr.indexOf(":" + indicator + ":") == 4 
				? wifiStr.indexOf(":" + indicator + ":") 
				: wifiStr.indexOf(";" + indicator + ":");
		if(beginIndex != -1) {
			int endIndex = wifiStr.indexOf(";", beginIndex + 3);
			if(endIndex != -1) {
				data = wifiStr.substring(beginIndex + 3, endIndex);
			} else {
				data = wifiStr.substring(beginIndex + 3);
			}
		}
		return data;
	}

	private String getSSID(String str) {
		String ssid = null;
		int beginIndex = str.indexOf(":S:") == 4 ? str.indexOf(":S:") : str.indexOf(";S:");
		if(beginIndex != -1) {
			int endIndex = str.indexOf(";", beginIndex + 3);
			if(endIndex != -1) {
				ssid = str.substring(beginIndex + 3, endIndex);
			} else {
				ssid = str.substring(beginIndex + 3);
			}
		}
		return ssid;
	}
	
	private String getPassword(String str) {
		String password = null;
		int beginIndex = str.indexOf(":P:") == 4 ? str.indexOf(":P:") : str.indexOf(";P:");
		if(beginIndex != -1) {
			int endIndex = str.indexOf(";", beginIndex + 3);
			if(endIndex != -1) {
				password = str.substring(beginIndex + 3, endIndex);
			} else {
				password = str.substring(beginIndex + 3);
			}
		}
		return password;
	}

	private String getSecurity(String str) {
		String security = null;
		int beginIndex = str.indexOf(":T:") == 4 ? str.indexOf(":T:") : str.indexOf(";T:");
		if(beginIndex != -1) {
			int endIndex = str.indexOf(";", beginIndex + 3);
			if(endIndex != -1) {
				security = str.substring(beginIndex + 3, endIndex);
			} else {
				security = str.substring(beginIndex + 3);
			}
		}
		return security;
	}
	
	private void splitWifiData(String wifiStr) {
		String ssid = null;
		String password = null;
		String security = null;
		int beginIndex = 0;
		int endIndex = 0;
		int tempIndex1 = 0;
		int tempIndex2 = 0;
		if(wifiStr.indexOf(":S:") == 4) {
			beginIndex = wifiStr.indexOf(":S:") + 3;
			tempIndex1 = wifiStr.indexOf(";P:", beginIndex);
			tempIndex2 = wifiStr.indexOf(";T:", beginIndex);
			if(tempIndex1 < tempIndex2 && tempIndex1 > -1) {
				endIndex = tempIndex1;
				ssid = wifiStr.substring(beginIndex, endIndex);
				beginIndex = endIndex + 3;
				endIndex = tempIndex2;
				password = wifiStr.substring(beginIndex, endIndex);
				beginIndex = endIndex + 3;
				security = wifiStr.substring(beginIndex, wifiStr.length() - 1);
			} else if(tempIndex1 > tempIndex2 && tempIndex2 > -1){
				endIndex = wifiStr.indexOf(";T:", beginIndex);
				ssid = wifiStr.substring(beginIndex, endIndex);
				beginIndex = endIndex + 3;
				endIndex = wifiStr.indexOf(";P:", beginIndex);
				security = wifiStr.substring(beginIndex, endIndex);
				beginIndex = endIndex + 3;
				password = wifiStr.substring(beginIndex, wifiStr.length() - 1);
			}
		} else if(wifiStr.indexOf(":P:") == 4) {
			beginIndex = wifiStr.indexOf(":P:") + 3;
			tempIndex1 = wifiStr.indexOf(";S:", beginIndex);
			tempIndex2 = wifiStr.indexOf(";T:", beginIndex);
			if(tempIndex1 < tempIndex2 && tempIndex1 > -1) {
				endIndex = wifiStr.indexOf(";S:", beginIndex);
				password = wifiStr.substring(beginIndex, endIndex);
				beginIndex = endIndex + 3;
				endIndex = wifiStr.indexOf(";T:", beginIndex);
				ssid = wifiStr.substring(beginIndex, endIndex);
				beginIndex = endIndex + 3;
				security = wifiStr.substring(beginIndex + wifiStr.length() - 1);
			} else if(tempIndex1 > tempIndex2 && tempIndex2 > -1){
				endIndex = wifiStr.indexOf(";T:", beginIndex);
				password = wifiStr.substring(beginIndex, endIndex);
				beginIndex = endIndex + 3;
				endIndex = wifiStr.indexOf(";S:", beginIndex);
				security = wifiStr.substring(beginIndex, endIndex);
				beginIndex = endIndex + 3;
				ssid = wifiStr.substring(beginIndex, wifiStr.length() - 1);
			}
		} else if(wifiStr.indexOf(":T:") == 4) {
			beginIndex = wifiStr.indexOf(":T:") + 3;
			tempIndex1 = wifiStr.indexOf(";S:", beginIndex);
			tempIndex2 = wifiStr.indexOf(";P:", beginIndex);
			if(tempIndex1 < tempIndex2 && tempIndex1 > -1) {
				endIndex = wifiStr.indexOf(";S:", beginIndex);
				security = wifiStr.substring(beginIndex, endIndex);
				beginIndex = endIndex + 3;
				endIndex = wifiStr.indexOf(";P:", beginIndex);
				ssid = wifiStr.substring(beginIndex, endIndex);
				beginIndex = endIndex + 3;
				password = wifiStr.substring(beginIndex, wifiStr.length() - 1);
			} else if(tempIndex1 > tempIndex2 && tempIndex2 > -1){
				endIndex = wifiStr.indexOf(";P:", beginIndex);
				security = wifiStr.substring(beginIndex, endIndex);
				beginIndex = endIndex + 3;
				endIndex = wifiStr.indexOf(";S:", beginIndex);
				password = wifiStr.substring(beginIndex, endIndex);
				beginIndex = endIndex + 3;
				ssid = wifiStr.substring(beginIndex, wifiStr.length() - 1);
			}
		} 
		
		//String ssid = getSSID(wifiStr);
		if(ssid != null)
			ssidField.setText(ssid);
		//String password = getPassword(wifiStr);
		if(password != null)
			passwordField.setText(password);
		//String security = getSecurity(wifiStr);
		if(security != null)
			securityField.setText(security);
	}
}
