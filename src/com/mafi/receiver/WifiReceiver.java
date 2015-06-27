package com.mafi.receiver;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class WifiReceiver extends BroadcastReceiver {
	
	private ProgressDialog dialog;
	
	public WifiReceiver(ProgressDialog dialog) {
		super();
		this.dialog = dialog;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
			NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			
			if(info.getState().equals(NetworkInfo.State.CONNECTED)) {
				dialog.dismiss();
				WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				Toast.makeText(context, "连接到网络" + wifiInfo.getSSID(), 3000).show();
				context.unregisterReceiver(this);
			}
		}
	}

}
