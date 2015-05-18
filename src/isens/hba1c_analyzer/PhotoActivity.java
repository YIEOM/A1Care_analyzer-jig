package isens.hba1c_analyzer;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.RunActivity.AnalyzerState;
import isens.hba1c_analyzer.TimerDisplay.whichClock;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
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

public class PhotoActivity extends Activity {
	
	private SerialPort PhotoSerial;
	
	private Handler handler = new Handler();
	private TimerTask OneSecondPeriod;
	private Timer timer;
	
	private Button escBtn,
				   runBtn,
				   cancelBtn;
	
	private TextView photoText;
		
	public static TextView TimeText;
	
	private String photoData[] = new String[256];
				   
	private int dataIndex = 0;
	
	private RunActivity.AnalyzerState photoState;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo);
		
		TimeText = (TextView)findViewById(R.id.timeText);
				
		photoText = (TextView) findViewById(R.id.phototext);
		
		/*System setting Activity activation*/
		escBtn = (Button)findViewById(R.id.escicon);
		escBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
		
				escBtn.setEnabled(false);
				
				WhichIntent(TargetIntent.Test);
			}
		});
		
		runBtn = (Button)findViewById(R.id.runbtn);
		runBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
		
				runBtn.setEnabled(false);
				cancelBtn.setEnabled(true);
				
				PhotoMeasure();
			}
		});
		
		cancelBtn = (Button)findViewById(R.id.cancelbtn);
		cancelBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
		
				cancelBtn.setEnabled(false);
				runBtn.setEnabled(true);
				
				MeasureCancel();
			}
		});
		
		PhotoInit();
	}
	
	public void PhotoInit() {
		
		TimerDisplay.timerState = whichClock.PhotoClock;		
		CurrTimeDisplay();
		
		new Thread(new Runnable() {
		    public void run() {    
		        runOnUiThread(new Runnable(){
		            public void run() {
		            	
		            	photoText.setTextColor(Color.parseColor("#04A458"));
		            	photoText.setText("READY");
		            }
		        });
		    }
		}).start();	
		
		PhotoSerial = new SerialPort();
		
		if(TestActivity.WhichTest == TestActivity.FILE_SAVE) {
			
			TimerInit();
		}
	}
	
	public void PhotoMeasure() {
		
		photoData = new String[256];
		
		dataIndex = 0;
		
		photoState = AnalyzerState.MeasurePosition;
		
		for(int i = 0; i < 3; i++) {
			
			switch(photoState) {
			
			case MeasurePosition :
				MotionInstruct(RunActivity.MEASURE_POSITION, SerialPort.CtrTarget.PhotoSet);			
				BoardMessage(RunActivity.MEASURE_POSITION, AnalyzerState.FilterDark);
				break;
				
			case FilterDark :
				MotionInstruct(RunActivity.FILTER_DARK, SerialPort.CtrTarget.PhotoSet);
				BoardMessage(RunActivity.FILTER_DARK, AnalyzerState.Filter535nm);
				break;
				
			case Filter535nm :
				/* 535nm filter Measurement */
				MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
				BoardMessage(RunActivity.NEXT_FILTER, AnalyzerState.Filter660nm);
				break;
				
			default	:
				break;
			}
		}
		
		TimerInit();
	}
	
	public void MeasureCancel() {
		
		timer.cancel();
		
		photoState = AnalyzerState.FilterHome;
		
		for(int i = 0; i < 2; i++) {
			
			switch(photoState) {
			
			case FilterHome :
				MotionInstruct(RunActivity.FILTER_DARK, SerialPort.CtrTarget.PhotoSet);
				BoardMessage(RunActivity.FILTER_DARK, AnalyzerState.CartridgeHome);
				break;
			
			case CartridgeHome :
				MotionInstruct(RunActivity.HOME_POSITION, SerialPort.CtrTarget.PhotoSet);
				BoardMessage(RunActivity.HOME_POSITION, AnalyzerState.NormalOperation);
				break;
				
			default	:
				break;
			}
		}
		
		SerialPort.Sleep(1000);
		
		new Thread(new Runnable() {
		    public void run() {    
		        runOnUiThread(new Runnable(){
		            public void run() {
		            	
		            	photoText.setTextColor(Color.parseColor("#04A458"));
		            	photoText.setText("READY");
		            }
		        });
		    }
		}).start();	
	}
	
	public void MotionInstruct(String str, SerialPort.CtrTarget target) { // Motion of system instruction
		
		PhotoSerial.BoardTx(str, target);
	}
	
	public synchronized double AbsorbanceMeasure() { // Absorbance measurement
	
		int time = 0;
		String rawValue;
		double douValue = 0;
		
		PhotoSerial.BoardTx("VH", SerialPort.CtrTarget.PhotoSet);
		
		rawValue = PhotoSerial.BoardMessageOutput();			
		
		while(rawValue.length() != 8) {
		
			rawValue = PhotoSerial.BoardMessageOutput();			
		
			SerialPort.Sleep(100);
		}	
	
		douValue = Double.parseDouble(rawValue);
			
		return (douValue - RunActivity.BlankValue[0]);
	}
	
	public void BoardMessage(String colRsp, AnalyzerState nextState) {
		
		int time = 0;
		String temp = "";
		
		while(true) {
			
			temp = PhotoSerial.BoardMessageOutput();
			
			if(colRsp.equals(temp)) {
				
				photoState = nextState;
				break;
			} 
			
			SerialPort.Sleep(100);
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
	
	public void TimerInit() {
		
		OneSecondPeriod = new TimerTask() {
			
			public void run() {
				Runnable updater = new Runnable() {
					public void run() {
						
						PhotoDisplay();
					}
				};
				
				handler.post(updater);		
			}
		};
		
		timer = new Timer();
		timer.schedule(OneSecondPeriod, 0, 1000); // Timer period : 1sec
	}
	
	public void PhotoDisplay() {
		
		double photo;
		DecimalFormat dfm = new DecimalFormat("#.0");		
		final String photoStr;		
		
		photo = AbsorbanceMeasure();
		photoStr = dfm.format(photo);
		
		new Thread(new Runnable() {
			public void run() {    
				runOnUiThread(new Runnable(){
					public void run() {
						
						photoText.setTextColor(Color.parseColor("#565656"));
						photoText.setText(photoStr);
					}
				});
			}
		}).start();

		photoData[dataIndex++] = photoStr;
		
		if(dataIndex == 256) {
			
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
			TestActivity.WhichTest = TestActivity.FILE_SAVE;
			
			Intent FileSaveIntent = new Intent(getApplicationContext(), FileSaveActivity.class);
			FileSaveIntent.putExtra("WhichIntent", TestActivity.PHOTO);
			FileSaveIntent.putExtra("Test Time", TimerDisplay.rTime[3] + " " + TimerDisplay.rTime[4] + ":" + TimerDisplay.rTime[5]);
			for(int i = 0; i < 256; i ++) {
				
				FileSaveIntent.putExtra("Photo Data" + Integer.toString(i), photoData[i]);
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
