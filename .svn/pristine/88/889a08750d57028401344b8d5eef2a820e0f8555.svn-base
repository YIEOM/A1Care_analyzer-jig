package isens.hba1c_analyzer;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.TimerDisplay.whichClock;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DisplayActivity extends Activity {
	
	private Button escBtn,
				   minusBtn,
				   plusBtn;
	
	private ImageView barGauge;
	
	private int brightnessValue = 0;
	
	private static TextView TimeText;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.display);
		
		TimeText  = (TextView) findViewById(R.id.timeText);
		barGauge = (ImageView) findViewById(R.id.bargauge);
		
		/*SystemSetting Activity activation*/
		escBtn = (Button)findViewById(R.id.escicon);
		escBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				escBtn.setEnabled(false);
				
				WhichIntent(TargetIntent.SystemSetting);
			}
		});
		
		minusBtn = (Button)findViewById(R.id.minusbtn);
		minusBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				BrightnessDown();
			}
		});
		
		plusBtn = (Button)findViewById(R.id.plusbtn);
		plusBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				BrightnessUp();
			}
		});
				
		DisplayInit();
	}
	
	public void DisplayInit() {
		
		TimerDisplay.timerState = whichClock.DisplayClock;		
		CurrTimeDisplay();
		
		try {
			
			brightnessValue = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
//			Log.w("GetBrightness", "Brightness : " + brightnessValue);
			
			GaugeDisplay();
			
		} catch(Exception e) {
			
		}
	}
	
	public void CurrTimeDisplay() {
		
		new Thread(new Runnable() {
		    public void run() {    
		        runOnUiThread(new Runnable(){
		            public void run() {
		            	
		            	TimeText.setText(TimerDisplay.rTime[3] + " " + TimerDisplay.rTime[4] + ":" + TimerDisplay.rTime[5]);
		            }
		        });
		    }
		}).start();	
	}
	
	public synchronized void BrightnessUp() {
		
		switch(brightnessValue) {
		
		case 51		:
			brightnessValue = 102;
			GaugeDisplay();
			break;

		case 102		:
			brightnessValue = 153;
			GaugeDisplay();
			break;
			
		case 153		:
			brightnessValue = 204;
			GaugeDisplay();
			break;
			
		case 204	:
			brightnessValue = 255;
			GaugeDisplay();
			break;
			
		default		:
			break;
		}
		
		SetBrightness();
	}
	
	public synchronized void BrightnessDown() {
		
		switch(brightnessValue) {
		
		case 102		:
			brightnessValue = 51;
			GaugeDisplay();
			break;
			
		case 153		:
			brightnessValue = 102;
			GaugeDisplay();
			break;
			
		case 204	:
			brightnessValue = 153;
			GaugeDisplay();
			break;
		
		case 255		:
			brightnessValue = 204;
			GaugeDisplay();
			break;
			
		default		:
			break;
		}
		
		SetBrightness();
	}
	
	public void GaugeDisplay() {
		
		switch(brightnessValue) {
		
		case 51		:
			barGauge.setBackgroundResource(R.drawable.display_bar_gauge_green_1);
			break;

		case 102	:
			barGauge.setBackgroundResource(R.drawable.display_bar_gauge_green_2);
			break;
			
		case 153	:
			barGauge.setBackgroundResource(R.drawable.display_bar_gauge_green_3);
			break;
			
		case 204	:
			barGauge.setBackgroundResource(R.drawable.display_bar_gauge_green_4);
			break;
		
		case 255	:
			barGauge.setBackgroundResource(R.drawable.display_bar_gauge_green_5);
			break;
			
		default		:
			break;
		}
	}
	
	public void SetBrightness() {
		
		try {
			
//			Log.w("SetBrightness", "Brightness : " + brightnessValue);
			
			WindowManager.LayoutParams params = getWindow().getAttributes();
			params.screenBrightness = (float)brightnessValue/100;
			getWindow().setAttributes(params);
			
			android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, brightnessValue);
			
		} catch(Exception e) {
			
		}
	}
	
	public void WhichIntent(TargetIntent Itn) { // Activity conversion
		
		switch(Itn) {
		
		case SystemSetting	:				
			Intent SystemSettingIntent = new Intent(getApplicationContext(), SystemSettingActivity.class);
			startActivity(SystemSettingIntent);
			break;
						
		default		:	
			break;			
		}
		
		finish();
	}
	
	public void finish() {
		
		super.finish();
		overridePendingTransition(R.anim.fade, R.anim.hold);
	}
}
