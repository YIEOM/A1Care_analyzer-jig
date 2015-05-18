package isens.hba1c_analyzer;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.TimerDisplay.whichClock;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class SettingActivity extends Activity {
	
	private Button systemBtn,
				   dataBtn,
				   maintenanceBtn,
				   escIcon;
	
	private static TextView TimeText;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.setting);
		
		TimeText = (TextView) findViewById(R.id.timeText);
		
		SettingInit();
		
		/*System setting Activity activation*/
		systemBtn = (Button)findViewById(R.id.systembtn);
		systemBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				systemBtn.setEnabled(false);
	
				WhichIntent(TargetIntent.SystemSetting);
			}
		});
		
		/*Data setting Activity activation*/
		dataBtn = (Button)findViewById(R.id.databtn);
		dataBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				dataBtn.setEnabled(false);
				
				WhichIntent(TargetIntent.DataSetting);
			}
		});
		
		/*Maintenance Activity activation*/
		maintenanceBtn = (Button)findViewById(R.id.maintenancebtn);
		maintenanceBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
//				maintenanceBtn.setEnabled(false);
//				
//				WhichIntent(TargetIntent.Maintenance);
			}
		});
		
		/*Home Activity activation*/
		escIcon = (Button)findViewById(R.id.backicon);
		escIcon.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				escIcon.setEnabled(false);
				
				WhichIntent(TargetIntent.Home);
			}
		});
	}
	
	public void SettingInit() {
		
		TimerDisplay.timerState = whichClock.SettingClock;		
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
		
		case Home			:				
			Intent HomeIntent = new Intent(getApplicationContext(), HomeActivity.class);
			startActivity(HomeIntent);
			break;
						
		case SystemSetting	:
			Intent SystemSettingIntent = new Intent(getApplicationContext(), SystemSettingActivity.class);
			startActivity(SystemSettingIntent);
			break;
			
		case DataSetting	:
			Intent DataSettingIntent = new Intent(getApplicationContext(), DataSettingActivity.class);
			startActivity(DataSettingIntent);
			break;			
			
		case Maintenance	:		
			Intent MaintenanceIntent = new Intent(getApplicationContext(), MaintenanceActivity.class);
			startActivity(MaintenanceIntent);
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
