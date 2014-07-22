package isens.hba1c_analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Calendar;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.TimerDisplay.whichClock;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

public class MaintenanceActivity extends Activity {
	
	private Button backBtn,
				   homeBtn,
				   systemBtn,
				   opticalBtn,
				   serviceBtn;
		
	private static TextView TimeText;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.maintenance);
		
		TimeText = (TextView) findViewById(R.id.timeText);
		
		MaintenanceInit();
					
		/*Setting Activity activation*/
		backBtn = (Button)findViewById(R.id.backicon);
		backBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				backBtn.setEnabled(false);
				
				WhichIntent(TargetIntent.Setting);
			}
		});
		
		/*Home Activity activation*/
		homeBtn = (Button)findViewById(R.id.homeicon);
		homeBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				homeBtn.setEnabled(false);

				WhichIntent(TargetIntent.Home);
			}
		});
	}
	
	public void MaintenanceInit() {
		
		TimerDisplay.timerState = whichClock.MaintenanceClock;		
		CurrTimeDisplay();
	}

	public void CurrTimeDisplay() {
		
		new Thread(new Runnable() {
		    public void run() {    
		        runOnUiThread(new Runnable(){
		            public void run() {
		            	
//		            	Log.w("SettingTimeDisplay", "run");
		            	TimeText.setText(TimerDisplay.rTime[3] + " " + TimerDisplay.rTime[4] + ":" + TimerDisplay.rTime[5]);
		            }
		        });
		    }
		}).start();	
	}
	
	public void WhichIntent(TargetIntent Itn) { // Activity conversion
		
		switch(Itn) {
		
		case Home		:				
			Intent HomeIntent = new Intent(getApplicationContext(), HomeActivity.class);
			startActivity(HomeIntent);
			break;
						
		case Setting	:
			Intent SettingIntent = new Intent(getApplicationContext(), SettingActivity.class);
			startActivity(SettingIntent);
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