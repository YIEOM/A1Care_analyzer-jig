package isens.hba1c_analyzer;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.TimerDisplay.whichClock;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class LabViewActivity extends Activity {
	
	public SerialPort LabViewSerial;
	
	private Button escBtn,
				   connectBtn;
	
	private static TextView TimeText;
	private TextView connectText;
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.labview);
		
		TimeText = (TextView)findViewById(R.id.timeText);
		
		connectText = (TextView)findViewById(R.id.connecttext);
		
		/*System setting Activity activation*/
		escBtn = (Button)findViewById(R.id.escicon);
		escBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
		
				escBtn.setEnabled(false);
				
				SerialPort.mLabViewRxThread.interrupt();
				SerialPort.LabViewRxDataObj.interrupt();
				
				WhichIntent(TargetIntent.Setting);
			}
		});
		
		/*System setting Activity activation*/
		connectBtn = (Button)findViewById(R.id.connectbtn);
		connectBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
		
				connectBtn.setEnabled(false);
				
				LabViewConnect();
			}
		});
		
		LabViewInit();
	}
	
	public void LabViewInit() {
		
		TimerDisplay.timerState = whichClock.LabViewClock;		
		CurrTimeDisplay();
		
		connectText.setTextColor(Color.parseColor("#565656"));
		connectText.setText("Not connected...");
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
	
	public void LabViewConnect() {
		
		LabViewSerial = new SerialPort();
		LabViewSerial.LabViewRxStart();
		
		connectText.setTextColor(Color.parseColor("#04A458"));
		connectText.setText("Connection Success!!");
	}
	
	public void WhichIntent(TargetIntent Itn) { // Activity conversion
		
		switch(Itn) {
		
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