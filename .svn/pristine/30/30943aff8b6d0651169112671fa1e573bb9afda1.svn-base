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

public class SystemSettingActivity extends Activity {
	
	public TimerDisplay SystemSettingTimer;
	public AdjustmentFactorActivity SystemSettingAdjustment;
	
	private RelativeLayout systemSettingLinear;
	private View resetPopupView;
	private PopupWindow resetPopup;
	
	private Button escBtn,
				   displayBtn,
				   dateBtn,
				   timeBtn,
				   soundBtn,
				   languageBtn,
				   resultBtn,
				   calibrationBtn,
				   collelationBtn,
				   reminderBtn,
				   adjustBtn,
				   saveBtn,
				   resetBtn,
				   hisBtn,
				   yesBtn,
				   noBtn,
				   tempBtn;
		
	public static TextView TimeText;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.systemsetting);
		
		TimeText = (TextView) findViewById(R.id.timeText);
		
		/* Reset Pop-up window */
		systemSettingLinear = (RelativeLayout)findViewById(R.id.systemsettinglinear);
		resetPopupView = View.inflate(getApplicationContext(), R.layout.resetpopup, null);
		resetPopup = new PopupWindow(resetPopupView, 504, 174, true);
		
		SystemSettingInit();
		
		/*Home Activity activation*/
		escBtn = (Button)findViewById(R.id.escicon);
		escBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				escBtn.setEnabled(false);
				
				WhichIntent(TargetIntent.Home);
			}
		});
		
		/*Display Activity activation*/
		displayBtn = (Button)findViewById(R.id.displaybtn);
		displayBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				displayBtn.setEnabled(false);
				
				WhichIntent(TargetIntent.Display);
			}
		});
		
		/*Date Activity activation*/
		dateBtn = (Button)findViewById(R.id.datebtn);
		dateBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				dateBtn.setEnabled(false);
				
				WhichIntent(TargetIntent.Date);
			}
		});
		
		/*Time Activity activation*/
		timeBtn = (Button)findViewById(R.id.timebtn);
		timeBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				timeBtn.setEnabled(false);
				
				WhichIntent(TargetIntent.Time);
			}
		});
		
		/*Adjustment Factor Activity activation*/
		adjustBtn = (Button)findViewById(R.id.adjustbtn);
		adjustBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				adjustBtn.setEnabled(false);
				
				WhichIntent(TargetIntent.Adjustment);
			}
		});
		
		/*Correlation Factor Activity activation*/
		collelationBtn = (Button)findViewById(R.id.collelationbtn);
		collelationBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				collelationBtn.setEnabled(false);
				
				WhichIntent(TargetIntent.Correlation);
			}
		});
		
		/*Sound Activity activation*/
		soundBtn = (Button)findViewById(R.id.soundbtn);
		soundBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				soundBtn.setEnabled(false);
				
				WhichIntent(TargetIntent.Sound);
			}
		});
		
		/*Calibration Activity activation*/
		calibrationBtn = (Button)findViewById(R.id.calibrationbtn);
		calibrationBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				calibrationBtn.setEnabled(false);
				
				WhichIntent(TargetIntent.Calibration);
			}
		});
		
		/*Language Activity activation*/
		languageBtn = (Button)findViewById(R.id.languagebtn);
		languageBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				languageBtn.setEnabled(false);
				
				WhichIntent(TargetIntent.Language);
			}
		});
		
		/*Temperature Activity activation*/
		tempBtn = (Button)findViewById(R.id.tempbtn);
		tempBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				tempBtn.setEnabled(false);
				
				WhichIntent(TargetIntent.Temperature);
			}
		});
		
		/*Reset Pop-up activation*/
		resetBtn = (Button)findViewById(R.id.resetbtn);
		resetBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				resetBtn.setEnabled(false);
				
				resetPopup.showAtLocation(systemSettingLinear, Gravity.CENTER, 0, 0);
				resetPopup.setAnimationStyle(0);
				resetPopup.showAsDropDown(resetBtn);
			}
		});
		
		yesBtn = (Button)resetPopupView.findViewById(R.id.yesbtn);
		yesBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				SettingParameterInit();
				
				resetBtn.setEnabled(true);
				
				resetPopup.dismiss();
			}
		});
		
		noBtn = (Button)resetPopupView.findViewById(R.id.nobtn);
		noBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				resetBtn.setEnabled(true);
				
				resetPopup.dismiss();
			}
		});
	}
	
	public void SystemSettingInit() {
		
		TimerDisplay.timerState = whichClock.SystemSettingClock;		
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
	
	public void SettingParameterInit() {
		
		/* Adjustment factor parameter Initialization */
		SharedPreferences AdjustmentPref = getSharedPreferences("User Define", MODE_PRIVATE);
		SharedPreferences.Editor adjustmentedit = AdjustmentPref.edit();
		
		adjustmentedit.putFloat("AF SlopeVal", 1.0f);
		adjustmentedit.putFloat("AF OffsetVal", 0.0f);
		adjustmentedit.putFloat("CF SlopeVal", 1.0f);
		adjustmentedit.putFloat("CF OffsetVal", 0.0f);
		adjustmentedit.commit();
		
		RunActivity.AF_Slope = 1.0f;
		RunActivity.AF_Offset = 0.0f;
		RunActivity.CF_Slope = 1.0f;
		RunActivity.CF_Offset = 0.0f;
	}
	
	public void WhichIntent(TargetIntent Itn) { // Activity conversion
		
		switch(Itn) {
		
		case Home			:				
			Intent HomeIntent = new Intent(getApplicationContext(), HomeActivity.class);
			startActivity(HomeIntent);
			break;
						
		case Setting		:				
			Intent SettingIntent = new Intent(getApplicationContext(), SettingActivity.class);
			startActivity(SettingIntent);
			break;
		
		case Display		:				
			Intent DisplayIntent = new Intent(getApplicationContext(), DisplayActivity.class);
			startActivity(DisplayIntent);
			break;
			
		case Date			:				
			Intent DateIntent = new Intent(getApplicationContext(), DateActivity.class);
			startActivity(DateIntent);
			break;
			
		case Time			:				
			Intent TimeIntent = new Intent(getApplicationContext(), TimeActivity.class);
			startActivity(TimeIntent);
			break;
			
		case HISSetting		:				
			Intent HISIntent = new Intent(getApplicationContext(), HISSettingActivity.class);
			startActivity(HISIntent);
			break;

		case Adjustment		:				
			Intent AdjustIntent = new Intent(getApplicationContext(), AdjustmentFactorActivity.class);
			startActivity(AdjustIntent);
			break;
			
		case Sound			:				
			Intent SoundIntent = new Intent(getApplicationContext(), SoundActivity.class);
			startActivity(SoundIntent);
			break;
			
		case Calibration	:				
			Intent CalibrationIntent = new Intent(getApplicationContext(), CalibrationActivity.class);
			startActivity(CalibrationIntent);
			break;
			
		case Language		:				
			Intent LanguageIntent = new Intent(getApplicationContext(), LanguageActivity.class);
			startActivity(LanguageIntent);
			break;

		case Correlation		:				
			Intent CorrelationIntent = new Intent(getApplicationContext(), CorrelationFactorActivity.class);
			startActivity(CorrelationIntent);
			break;
		
		case Temperature		:				
			Intent TemperatureIntent = new Intent(getApplicationContext(), TemperatureActivity.class);
			startActivity(TemperatureIntent);
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