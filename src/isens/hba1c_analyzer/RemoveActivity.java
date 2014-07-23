package isens.hba1c_analyzer;

import java.lang.annotation.Target;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.TimerDisplay.whichClock;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class RemoveActivity extends Activity {

	SerialPort RemoveSerial;
	
	final static byte ACTION_ACTIVITY = 1,
					  HOME_ACTIVITY = 2,
					  COVER_ACTION_ESC = 3;
	
	public AnimationDrawable RemoveAni;
	public ImageView RemoveImage;
	
	public static TextView TimeText;
	public static int PatientDataCnt,
					  ControlDataCnt;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.remove);

		RemoveSerial = new SerialPort();
		
		TimeText = (TextView) findViewById(R.id.timeText);
		
		RemoveInit();
	}
	
	public void RemoveInit() {
		
		TimerDisplay.timerState = whichClock.RemoveClock;		
		CurrTimeDisplay();
		
		switch(TestActivity.WhichTest) {
		
		case TestActivity.PHOTO_TEMPERATURE	:
			if(++TestActivity.NumofPhotoTemp != TestActivity.NUMBER_PHOTO_TEMP) {
				
				WhichIntent(TargetIntent.Blank);
				
			} else {
				
				WhichIntent(TargetIntent.Home);	
			}
			break;
			
		default	:
			UserAction UserActionObj = new UserAction();
			UserActionObj.start();
			break;
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
	
	public class UserAction extends Thread {
		
		public void run() {
			
			int whichIntent;
			
			User1stAction();
			
			GpioPort.DoorActState = true;
			GpioPort.CartridgeActState = true;
	
			while(ActionActivity.CartridgeCheckFlag != 0);
			
			while(ActionActivity.DoorCheckFlag != 1);

			GpioPort.DoorActState = false;
			GpioPort.CartridgeActState = false;
			
			RemoveAni.stop();
			
			SerialPort.Sleep(200);
			
			Intent itn = getIntent();
			whichIntent = itn.getIntExtra("WhichIntent", 0);
			
			if(whichIntent != COVER_ACTION_ESC) {
					 
				if(Barcode.RefNum.substring(0, 1).equals("C")) {
					
					ControlDataCnt = itn.getIntExtra("DataCnt", 0);	
				
				} else if(Barcode.RefNum.substring(0, 1).equals("H")) {
					
					PatientDataCnt = itn.getIntExtra("DataCnt", 0);
				}
				
				DataCntSave();
			
			}
			
			switch(whichIntent) {
			
			case ACTION_ACTIVITY	:
				WhichIntent(TargetIntent.Action);
				break;
			
			case HOME_ACTIVITY		:	
				WhichIntent(TargetIntent.Home);
				break;
				
			case COVER_ACTION_ESC	:
				WhichIntent(TargetIntent.Home);
				break;
				
			default	:
				break;
			}
		}
	}
	
	public void User1stAction() { // Cartridge remove animation start
		
		RemoveImage = (ImageView)findViewById(R.id.removeAct1);
		RemoveAni = (AnimationDrawable)RemoveImage.getBackground();
		
//		new Thread(new Runnable() {
//		    public void run() {    
//		        runOnUiThread(new Runnable(){
//		            public void run() {
		            	
		            	RemoveAni.start();
//		            }
//		        });
//		    }
//		}).start();	
	}
	
	public void DataCntSave() { // Saving data number
		
		SharedPreferences DcntPref = getSharedPreferences("Data Counter", MODE_PRIVATE);
		SharedPreferences.Editor edit = DcntPref.edit();
		
		edit.putInt("PatientDataCnt", PatientDataCnt);
		edit.putInt("ControlDataCnt", ControlDataCnt);
		
		edit.commit();
	}
	
	public void WhichIntent(TargetIntent Itn) { // Activity conversion
		
		SerialPort.Sleep(1000);		
		
		switch(Itn) {
		
		case Home		:				
			Intent HomeIntent = new Intent(getApplicationContext(), HomeActivity.class);
			startActivity(HomeIntent);
			break;
						
		case Action		:				
			Intent ActionIntent = new Intent(getApplicationContext(), ActionActivity.class);
			startActivity(ActionIntent);
			break;
			
		case Blank		:				
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
