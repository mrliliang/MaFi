package com.mafi.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ImageView;

import com.google.zxing.WriterException;
import com.mafi.main.R;
import com.zxing.activity.CaptureActivity;
import com.zxing.encoding.EncodingHandler;


public class MainActivity extends Activity {
	private final String NOSEC = "无加密";
	private EditText ssidField = null;
	private EditText passwordField = null;
	private Spinner securityField = null;
	private ImageView qrCodeImage = null;
	private Button saveBtn = null;
	private TextWatcher textWatcher = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ssidField = (EditText) this.findViewById(R.id.ssid);
        passwordField = (EditText) this.findViewById(R.id.password);
        securityField = (Spinner) this.findViewById(R.id.security_type);
        qrCodeImage = (ImageView) this.findViewById(R.id.qrcode);
        saveBtn = (Button) this.findViewById(R.id.saveBtn);
        
        textWatcher = new TextWatcher() {
        	@Override
    		public void beforeTextChanged(CharSequence s, int start, int count,
    				int after) {
    			
    		}

    		@Override
    		public void onTextChanged(CharSequence s, int start, int before,
    				int count) {
    			generateQRCode();
    		}

    		@Override
    		public void afterTextChanged(Editable s) {
    			
    		}
        };
        
        ssidField.addTextChangedListener(textWatcher);
        
        passwordField.addTextChangedListener(textWatcher);
        
        securityField.setOnItemSelectedListener(new AdapterView.
        		OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				String secType = getResources().getStringArray(R.array.security_option)
						[position];
				if(NOSEC.equals(secType)) {
					passwordField.setText("");
					passwordField.setEnabled(false);
				} else {
					passwordField.setEnabled(true);
				}
				generateQRCode();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
        	
		});
        
        saveBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//save qrcode as an image
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.scan_btn) {
        	Intent scanIntent = new Intent(MainActivity.this, 
        			CaptureActivity.class);
        	startActivity(scanIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void generateQRCode() {
    	String ssid = ssidField.getText().toString();
    	String password = passwordField.getText().toString();
    	String securityType = securityField.getSelectedItem().toString();
    	if("WPA/WPA2".equals(securityType)) {
    		securityType = "WPA";
    	}
    	
    	String contentString = null;
    	if(NOSEC.equals(securityType)) {
    		contentString = "WIFI:T:nopass;" + "S:" + ssid +";";
    	} else {
    		contentString = "WIFI:T:" + securityType +
    				";S:" + ssid +
    				";P:" + password +";";
    	}
    	
    	Bitmap qrCodeBitmap;
		try {
			qrCodeBitmap = EncodingHandler.createQRCode(contentString, 500);
			qrCodeImage.setImageBitmap(qrCodeBitmap);
		} catch (WriterException e) {
			e.printStackTrace();
		}
    }
    
    public boolean isExternalStorageWritable() {
    	String state = Environment.getExternalStorageState();
    	if(Environment.MEDIA_MOUNTED.equals(state)) {
    		return true;
    	}
    	return false;
    }
}