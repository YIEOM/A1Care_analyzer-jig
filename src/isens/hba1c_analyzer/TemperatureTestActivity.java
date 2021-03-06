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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class TemperatureTestActivity extends Activity {
	
	public final static int TEMP_DATA_SIZE = 30;
	
	private Temperature TemperatureTemp;
	
	private Handler handler = new Handler();
	private TimerTask OneSecondPeriod;
	private Timer timer;
	
	private Button escBtn;
	
	private TextView cellBlockTempText, 
					 ambientTempText;
		
	public static TextView TimeText;
	
	private String 
//	cellBlockTempData[] = new String[256],
				   ambientTempData[] = new String[TEMP_DATA_SIZE];
	
	private int dataIndex = 0;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.temperaturetest);
		
		TimeText = (TextView)findViewById(R.id.timeText);
				
//		cellBlockTempText = (TextView) findViewById(R.id.cellblocktemptext);
		ambientTempText = (TextView) findViewById(R.id.ambienttemptext);
		
		/*System setting Activity activation*/
		escBtn = (Button)findViewById(R.id.escicon);
		escBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
		
				escBtn.setEnabled(false);
				
				WhichIntent(TargetIntent.Test);
			}
		});
		
		TemperatureInit();
	}
	
	public void TemperatureInit() {
		
		TimerDisplay.timerState = whichClock.TemperatureClock;		
		CurrTimeDisplay();
		
//		cellBlockTempData = new String[256];
		ambientTempData = new String[TEMP_DATA_SIZE];
		
		dataIndex = 0;
		
		TimerInit();
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
	
	public void TimerInit() {
		
		OneSecondPeriod = new TimerTask() {
		
			int cnt = 0;
			
			public void run() {
				Runnable updater = new Runnable() {
					public void run() {
						
						if((cnt++ % 10) == 0) TemperatureDisplay();
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
//		cellBlock = TemperatureTemp.CellTmpRead();
		ambient = TemperatureTemp.AmbTmpRead();
//		cellBlockStr = dfm.format(cellBlock);
		ambientStr = dfm.format(ambient);
		
		new Thread(new Runnable() {
			public void run() {    
				runOnUiThread(new Runnable(){
					public void run() {
						
//						cellBlockTempText.setText(cellBlockStr);
						ambientTempText.setText(ambientStr);
					}
				});
			}
		}).start();

//		cellBlockTempData[dataIndex] = cellBlockStr;
		ambientTempData[dataIndex++] = ambientStr;
		
		if(dataIndex == TEMP_DATA_SIZE) {
			
			dataIndex = 0;
			
			WhichIntent(TargetIntent.FileSave);	
		}
	}
	
	public void WhichIntent(TargetIntent Itn) { // Activity conversion
		
		timer.cancel();
		
		switch(Itn) {
		
		case Test			:
			Intent TestIntent = new Intent(getApplicationContext(), TestActivity.class);
			startActivity(TestIntent);
			break;
						
		case FileSave		:
			Intent FileSaveIntent = new Intent(getApplicationContext(), FileSaveActivity.class);
			FileSaveIntent.putExtra("WhichIntent", TestActivity.TEMPERATURE);
			FileSaveIntent.putExtra("Test Time", TimerDisplay.rTime[1] + "." + TimerDisplay.rTime[2] + " " + TimerDisplay.rTime[3] + " " + TimerDisplay.rTime[4] + ":" + TimerDisplay.rTime[5]);
			for(int i = 0; i < TEMP_DATA_SIZE; i ++) {
				
//				FileSaveIntent.putExtra("Chamber Temp Data" + Integer.toString(i), cellBlockTempData[i]);
				FileSaveIntent.putExtra("Inside Temp Data" + Integer.toString(i), ambientTempData[i]);
			}
			startActivity(FileSaveIntent);
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