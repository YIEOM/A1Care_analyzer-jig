package isens.hba1c_analyzer;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.TimerDisplay.whichClock;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class TemperatureActivity extends Activity {
	
	private SerialPort TemperatureSerial;
	private Temperature TemperatureTemp;
	private DataStorage TemperatureData;
	
	private Handler handler = new Handler();
	private TimerTask OneSecondPeriod;
	private Timer timer;
	
	private Button escBtn;
	
	private TextView cellBlockTempText, 
					 ambientTempText;
		
	public static TextView TimeText;
	
	private String cellBlockTempData[] = new String[256],
				   ambientTempData[] = new String[256];
	
	private int dataIndex = 0;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.temperature);
		
		TimeText = (TextView)findViewById(R.id.timeText);
				
		cellBlockTempText = (TextView) findViewById(R.id.cellblocktemptext);
		ambientTempText = (TextView) findViewById(R.id.ambienttemptext);
		
		/*System setting Activity activation*/
		escBtn = (Button)findViewById(R.id.escicon);
		escBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
		
				escBtn.setEnabled(false);
				
				WhichIntent(TargetIntent.SystemSetting);
			}
		});
		
		TemperatureInit();
	}
	
	public void TemperatureInit() {
		
		TimerDisplay.timerState = whichClock.TemperatureClock;		
		CurrTimeDisplay();
		
		cellBlockTempData = new String[256];
		ambientTempData = new String[256];
		
		dataIndex = 0;
		
		TimerInit();
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
	
	public void TimerInit() {
		
		OneSecondPeriod = new TimerTask() {
			
			public void run() {
				Runnable updater = new Runnable() {
					public void run() {
						
						TemperatureDisplay();
					}
				};
				
				handler.post(updater);		
			}
		};
		
		timer = new Timer();
		timer.schedule(OneSecondPeriod, 0, 1000); // Timer period : 1sec
	}
	
	public void TemperatureDisplay() {
		
		double cellBlock, 
			   ambient;
		DecimalFormat dfm = new DecimalFormat("#.0");		
		final String cellBlockStr, 
					 ambientStr;		
		
		TemperatureTemp = new Temperature();
		cellBlock = TemperatureTemp.CellTmpRead();
		ambient = TemperatureTemp.AmbTmpRead();
		cellBlockStr = dfm.format(cellBlock);
		ambientStr = dfm.format(ambient);
		
		new Thread(new Runnable() {
			public void run() {    
				runOnUiThread(new Runnable(){
					public void run() {
						
						cellBlockTempText.setText(cellBlockStr);
						ambientTempText.setText(ambientStr);
					}
				});
			}
		}).start();

		cellBlockTempData[dataIndex] = cellBlockStr;
		ambientTempData[dataIndex++] = ambientStr;
		
//		if(dataIndex == 256) {
//			
//			WhichIntent(TargetIntent.FileSave);
//		}
	}
	
	public void WhichIntent(TargetIntent Itn) { // Activity conversion
		
		timer.cancel();
		
		switch(Itn) {
		
		case SystemSetting	:
			Intent SystemSettingIntent = new Intent(getApplicationContext(), SystemSettingActivity.class);
			startActivity(SystemSettingIntent);
			break;
						
		case FileSave		:
			Intent TempFileSaveIntent = new Intent(getApplicationContext(), TempFileSaveActivity.class);
			TempFileSaveIntent.putExtra("Test Time", TimerDisplay.rTime[3] + " " + TimerDisplay.rTime[4] + ":" + TimerDisplay.rTime[5]);
			for(int i = 0; i < 256; i ++) {
				
				TempFileSaveIntent.putExtra("Cell Block Temp Data" + Integer.toString(i), cellBlockTempData[i]);
				TempFileSaveIntent.putExtra("Ambient Temp Data" + Integer.toString(i), ambientTempData[i]);
			}
			startActivity(TempFileSaveIntent);
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