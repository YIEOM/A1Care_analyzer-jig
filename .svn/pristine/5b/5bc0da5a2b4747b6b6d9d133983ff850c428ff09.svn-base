package isens.hba1c_analyzer;

import java.text.DecimalFormat;
import java.util.Calendar;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.TimerDisplay.whichClock;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TimeActivity extends Activity {

	private TimerDisplay TimeTimer;
	
	public enum AddSub {PLUS, MINUS}
	
	private TextView hourText,
					 minText,
					 ampmText;
	
	private Calendar c;
	
	private Button escBtn,
				   hPlusBtn,
				   hMinusBtn,
				   mPlusBtn,
				   mMinusBtn,
				   ampmUpBtn,
				   ampmDownBtn;
	
	private static TextView TimeText;
	
	private int currHour,
				hour,
				currMin,
				min,
				ampm;
	private String minStr,
				   ampmStr;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.time);
		
		TimeText  = (TextView) findViewById(R.id.timeText);
		hourText  = (TextView) findViewById(R.id.hourtext);
		minText   = (TextView) findViewById(R.id.mintext);
		ampmText   = (TextView) findViewById(R.id.ampmtext);
		
		/*System Setting Activity activation*/
		escBtn = (Button)findViewById(R.id.escicon);
		escBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				escBtn.setEnabled(false);
				
				TimeSave();
				
				WhichIntent(TargetIntent.SystemSetting);
			}
		});
		
		hPlusBtn = (Button) findViewById(R.id.hplusbtn);
		hPlusBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				HourChange(AddSub.PLUS);
			}
		});
		
		hMinusBtn = (Button) findViewById(R.id.hminusbtn);
		hMinusBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {

				HourChange(AddSub.MINUS);
			}
		});
		
		mPlusBtn = (Button) findViewById(R.id.mplusbtn);
		mPlusBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {

				MinChange(AddSub.PLUS);
			}
		});
		
		mMinusBtn = (Button) findViewById(R.id.mminusbtn);
		mMinusBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
			
				MinChange(AddSub.MINUS);
			}
		});

		ampmUpBtn = (Button) findViewById(R.id.ampmupbtn);
		ampmUpBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
			
				AmPmChange();
			}
		});

		ampmDownBtn = (Button) findViewById(R.id.ampmdownbtn);
		ampmDownBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				AmPmChange();
			}
		});
		
		DateInit();
	}
	
	public void DateInit() {
		
		TimerDisplay.timerState = whichClock.TimeClock;		
		CurrTimeDisplay();
		GetCurrTime();
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
	
	public synchronized void TimeDisplay() { // displaying the modifying time value
		
		new Thread(new Runnable() {
		    public void run() {    
		        runOnUiThread(new Runnable(){
		            public void run() {

		        		hourText.setText(Integer.toString(hour));
		        		minText.setText(minStr);
		        		ampmText.setText(ampmStr);
		            }
		        });
		    }
		}).start();
	}
	
	public void GetCurrTime() { // getting the current time data
		
		DecimalFormat dfm = new DecimalFormat("00");
		
		c = Calendar.getInstance();
		hour = c.get(Calendar.HOUR);
		
		if(c.get(Calendar.AM_PM) != 0) {
			
			ampm = 1;
			ampmStr = "PM";
			currHour = hour + 12;
		} else {
			
			ampm = 0;
			ampmStr = "AM";
			currHour = hour;
		}
		
		if(hour == 0) hour = 12;
		
		min  = c.get(Calendar.MINUTE);
		currMin = min;
		
		minStr  = dfm.format(min);
		
//		Log.w("getcurrdate", "" + c.getTimeInMillis());
		
		TimeDisplay();
	}
	
	public void HourChange(AddSub i) { // increasing or decreasing the hour value one by one
		
		switch(i) {
		
		case PLUS	:
			if(hour < 12) {
				hour += 1;
			} else {
				hour = 1;
			}
			break;
			
		case MINUS	:
			if(hour > 1) {
				hour -= 1;
			} else {
				hour = 12;
			}
			break;
			
		default		:
			break;
		}
		
		TimeDisplay();
	}
	
	public void MinChange(AddSub i) { // increasing or decreasing the minute value one by one
		
		DecimalFormat dfm = new DecimalFormat("00"); 
		
		switch(i) {
		
		case PLUS	:
			if(min < 59) {
				min += 1;
			} else {
				min = 0;
			}
			break;
			
		case MINUS	:
			if(min > 0) {
				min -= 1;
			} else {
				min = 59;
			}
			break;
			
		default		:
			break;
		}
		
		minStr = dfm.format(min);
		
		TimeDisplay();
	}
	
	public void AmPmChange() { // changing the am/pm
		
		if(ampm == 0) {
			ampmStr = "PM";
			ampm = 1;
		} else {
			ampmStr = "AM";
			ampm = 0;
		}
		
		TimeDisplay();
	}
	
	public void TimeSave() { // saving the time modified

		int setHour = 0;
		int setMin;
		
		if(ampm == 0) {
			
			if(hour != 12) setHour = hour - currHour;
			else setHour -= currHour;
					
		} else {
			
			if(hour != 12) setHour = (hour + 12) - currHour;
			else setHour = hour - currHour;
		}
		setMin = min - currMin;
		
		c.add(Calendar.MINUTE, setMin);
		c.add(Calendar.HOUR_OF_DAY, setHour);;
				
		TimerDisplay.OneHundredmsPeriod.cancel();
		
		SystemClock.setCurrentTimeMillis(c.getTimeInMillis());
		
		TimeTimer = new TimerDisplay();
		TimeTimer.TimerInit();
		
		SerialPort.Sleep(1000);
		
		if(hour == 0) hour = 12;
		
		CurrTimeDisplay();
		GetCurrTime();
	}
	
	public void finish() {
		
		super.finish();
		overridePendingTransition(R.anim.fade, R.anim.hold);
	}
}