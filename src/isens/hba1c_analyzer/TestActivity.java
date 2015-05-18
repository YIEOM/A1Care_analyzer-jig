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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class TestActivity extends Activity {
	
	final static int NORMAL = 0,
					 PHOTO_TEMPERATURE = 6,
					 LAB_VIEW = 7,
					 TEMPERATURE = 8,
					 PHOTO = 9,
					 PHOTO_ABSORBANCE = 10,
					 FILE_SAVE = 20;
	
	final static int NUMBER_PHOTO_TEMP = 40;
	
	public TimerDisplay SystemSettingTimer;
	
	private Button escBtn,
				   labviewBtn,
				   stabilityBtn,
				   tempBtn,
				   photoTempBtn,
				   photoBtn,
				   photoAbsBtn;
		
	public static TextView TimeText;
	
	public static int WhichTest,
					  NumofPhotoTemp;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.test);
		
		TimeText = (TextView) findViewById(R.id.timeText);
				
		TestInit();
		
		/*Home Activity activation*/
		escBtn = (Button)findViewById(R.id.escicon);
		escBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				escBtn.setEnabled(false);
				
				WhichIntent(TargetIntent.Home);
			}
		});
		
		/*Lab View Activity activation*/
		labviewBtn = (Button)findViewById(R.id.labviewbtn);
		labviewBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				labviewBtn.setEnabled(false);
				
				WhichTest = LAB_VIEW;
				
				WhichIntent(TargetIntent.LabView);
			}
		});
		
		/*Stability Activity activation*/
		stabilityBtn = (Button)findViewById(R.id.stabilitybtn);
		stabilityBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				stabilityBtn.setEnabled(false);
				
				WhichIntent(TargetIntent.Stability);
			}
		});
		
		/*Temperature Activity activation*/
		tempBtn = (Button)findViewById(R.id.tempbtn);
		tempBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				tempBtn.setEnabled(false);
				
				WhichTest = TEMPERATURE;
				
				WhichIntent(TargetIntent.Temperature);
			}
		});

		/*Photo Activity activation*/
		photoBtn = (Button)findViewById(R.id.photobtn);
		photoBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				photoBtn.setEnabled(false);
				
				WhichTest = PHOTO;
				
				WhichIntent(TargetIntent.Photo);
			}
		});
		
		/*Photo & Temperature Activity activation*/
		photoTempBtn = (Button)findViewById(R.id.phototempbtn);
		photoTempBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				photoTempBtn.setEnabled(false);
				
				NumofPhotoTemp = 0;
				WhichTest = PHOTO_TEMPERATURE;
				
				WhichIntent(TargetIntent.Blank);
			}
		});
		
		/*Photo & Absorbance Activity activation*/
		photoAbsBtn = (Button)findViewById(R.id.photoabsbtn);
		photoAbsBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				photoAbsBtn.setEnabled(false);
				
				WhichTest = PHOTO_ABSORBANCE;
				
				WhichIntent(TargetIntent.Blank);
			}
		});
	}
	
	public void TestInit() {
		
		TimerDisplay.timerState = whichClock.TestClock;		
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
						
		case LabView		:				
			Intent LabViewIntent = new Intent(getApplicationContext(), LabViewActivity.class);
			startActivity(LabViewIntent);
			break;
		
//		case Stability		:				
//			Intent DisplayIntent = new Intent(getApplicationContext(), StabilityActivity.class);
//			startActivity(DisplayIntent);
//			break;
			
		case Temperature	:				
			Intent TempIntent = new Intent(getApplicationContext(), TemperatureTestActivity.class);
			startActivity(TempIntent);
			break;
			
		case Photo			:				
			Intent PhotoIntent = new Intent(getApplicationContext(), PhotoActivity.class);
			startActivity(PhotoIntent);
			break;
			
		case Blank			:
			Intent BlankIntent = new Intent(getApplicationContext(), BlankActivity.class);
			startActivity(BlankIntent);
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