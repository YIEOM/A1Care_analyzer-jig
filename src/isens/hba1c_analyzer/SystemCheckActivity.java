package isens.hba1c_analyzer;

import isens.hba1c_analyzer.CalibrationActivity.Cart1stShaking;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.TimerDisplay.whichClock;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SystemCheckActivity extends Activity {

	final static byte MOTOR_CHECK_NUMBER = 13,
					  NUMBER_TEMPERATURE_CHECK = 5/5;
	final static String SHAKING_CHECK_TIME = "0030";

	private enum CheckState {Step1Position, Step1Shaking, Step2Position, Step2Shaking, MeasurePosition, Filter535nm, Filter660nm, Filter750nm, PhotoMeasure, FilterDark, CartridgeDump, HomePosition, BlankMode, ShakingMotorError, FilterMotorError, PhotoSensorError}
	private enum AmbTmpState {FirstTmp, SecondTmp, ThirdTmp, ForthTmp, FifthTmp}
	
	private GpioPort SystemGpio;
	private SerialPort SystemSerial;
	private Temperature SystemTmp;
	private TimerDisplay SystemTimer;
	
	private AudioManager audioManager;
	
	private RelativeLayout systemCheckLinear;
	
	private AnimationDrawable systemCheckAni;
	private ImageView systemCheckImage;
	
	private View errorPopupView;
	private PopupWindow errorPopup;
	
	private static CheckState State = CheckState.Step1Position;
	
	private byte CheckError = HomeActivity.NORMAL_OPERATION,
				 testNum = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.systemcheck);
		
		/* Error Pop-up window */
		systemCheckLinear = (RelativeLayout)findViewById(R.id.systemchecklinear);		
		errorPopupView = View.inflate(getApplicationContext(), R.layout.errorpopup, null);
		errorPopup = new PopupWindow(errorPopupView, 478, 155, true);

		SystemCheckInit();
	}
	
	public void SystemCheckInit() {
		
		SystemAniStart();
	
		/* Serial communication start */
		SystemSerial = new SerialPort();
		SystemSerial.BoardSerialInit();
		SystemSerial.BoardRxStart();
		SystemSerial.PrinterSerialInit();
		
		/* Timer start */
		TimerDisplay.timerState = whichClock.SystemCheckClock;
		SystemTimer = new TimerDisplay();
		SystemTimer.TimerInit(); 
		SystemTimer.RealTime();
		
		/* Barcode reader off */
		SystemGpio = new GpioPort();
		SystemGpio.TriggerHigh();
		
		ParameterInit();

		BrightnessInit();
		
		VolumeInit();
		
//		TemperatureCheck TemperatureCheckObj = new TemperatureCheck();
//		TemperatureCheckObj.start();
		
		SensorCheck SensorCheckObj = new SensorCheck();
		SensorCheckObj.start();
	}
	
	public class SensorCheck extends Thread {
		
		public void run() {
			
			GpioPort.DoorActState = true;			
			GpioPort.CartridgeActState = true;
			
			SerialPort.Sleep(2000);
			
			if(ActionActivity.CartridgeCheckFlag != 0) ErrorPopup(HomeActivity.CART_SENSOR_ERROR);
			while(ActionActivity.CartridgeCheckFlag != 0);
			errorPopup.dismiss();
			
			if(ActionActivity.DoorCheckFlag != 1) ErrorPopup(HomeActivity.DOOR_SENSOR_ERROR);
			while(ActionActivity.DoorCheckFlag != 1);
			errorPopup.dismiss();
			 
			GpioPort.DoorActState = false;			
			GpioPort.CartridgeActState = false;
			
			MotorCheck MotorCheckObj = new MotorCheck();
			MotorCheckObj.start();
		}
	}
	
	public class MotorCheck extends Thread {
		
		public void run() {
			
			BlankStep BlankStepObj = new BlankStep();
			TemperatureCheck TemperatureCheckObj = new TemperatureCheck();
			
			for(int i = 0; i < MOTOR_CHECK_NUMBER; i++) {
			
				switch(State) {
				
				case Step1Position		:
					MotionInstruct(RunActivity.Step1st_POSITION, SerialPort.CtrTarget.PhotoSet);			
					SerialPort.Sleep(200);
					if(!RunActivity.Step1st_POSITION.equals(SystemSerial.BoardMessageOutput())) {
						
						State = CheckState.ShakingMotorError;
						
					} else State = CheckState.Step1Shaking;
					break;
					
				case Step1Shaking		:
					MotionInstruct(SHAKING_CHECK_TIME, SerialPort.CtrTarget.MotorSet);
					SerialPort.Sleep(6000);
					if(!RunActivity.MOTOR_COMPLETE.equals(SystemSerial.BoardMessageOutput())) {
						
						State = CheckState.ShakingMotorError;
						
					} else State = CheckState.Step2Position;
					break;
					
				case Step2Position		:
					MotionInstruct(RunActivity.Step2nd_POSITION, SerialPort.CtrTarget.PhotoSet);
					SerialPort.Sleep(200);
					if(!RunActivity.Step2nd_POSITION.equals(SystemSerial.BoardMessageOutput())) {
						
						State = CheckState.ShakingMotorError;
						
					} else State = CheckState.Step2Shaking;
					break;
					
				case Step2Shaking		:
					MotionInstruct(SHAKING_CHECK_TIME, SerialPort.CtrTarget.MotorSet);
					SerialPort.Sleep(6000);
					if(!RunActivity.MOTOR_COMPLETE.equals(SystemSerial.BoardMessageOutput())) {
						
						State = CheckState.ShakingMotorError;
						
					} else State = CheckState.MeasurePosition;
					break;
					
				case MeasurePosition	:
					MotionInstruct(RunActivity.MEASURE_POSITION, SerialPort.CtrTarget.PhotoSet);
					SerialPort.Sleep(1200);
					if(!RunActivity.MEASURE_POSITION.equals(SystemSerial.BoardMessageOutput())) {
						
						State = CheckState.ShakingMotorError;
						
					} else State = CheckState.Filter535nm;
					break;
					
				case Filter535nm		:
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
					SerialPort.Sleep(1000);
					if(!RunActivity.NEXT_FILTER.equals(SystemSerial.BoardMessageOutput())) {
						
						State = CheckState.FilterMotorError;
						
					} else State = CheckState.Filter660nm;
					break;
					
				case Filter660nm		:
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
					SerialPort.Sleep(1000);
					if(!RunActivity.NEXT_FILTER.equals(SystemSerial.BoardMessageOutput())) {
						
						State = CheckState.FilterMotorError;
						
					} else State = CheckState.Filter750nm;
					break;
					
				case Filter750nm		:
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
					SerialPort.Sleep(1000);
					if(!RunActivity.NEXT_FILTER.equals(SystemSerial.BoardMessageOutput())) {
						
						State = CheckState.FilterMotorError;
						
					} else State = CheckState.PhotoMeasure;
					break;
					
				case PhotoMeasure		:
					RunActivity.BlankValue[0] = 0;
					if(AbsorbanceMeasure() == 0) {
						
						State = CheckState.PhotoSensorError;
						
					} else State = CheckState.FilterDark;
					break;
					
				case FilterDark			:
					MotionInstruct(RunActivity.FILTER_DARK, SerialPort.CtrTarget.PhotoSet);
					SerialPort.Sleep(400);
					if(!RunActivity.FILTER_DARK.equals(SystemSerial.BoardMessageOutput())) {
						
						State = CheckState.FilterMotorError;
						
					} else State = CheckState.CartridgeDump;
					break;
					
				case CartridgeDump		:
					MotionInstruct(RunActivity.CARTRIDGE_DUMP, SerialPort.CtrTarget.PhotoSet);
					SerialPort.Sleep(6000);
					if(!RunActivity.CARTRIDGE_DUMP.equals(SystemSerial.BoardMessageOutput())) {
						
						State = CheckState.ShakingMotorError;
											
					} else State = CheckState.HomePosition;
					break;
					
				case HomePosition		:
					MotionInstruct(RunActivity.HOME_POSITION, SerialPort.CtrTarget.PhotoSet);
					SerialPort.Sleep(1300);
					if(!RunActivity.HOME_POSITION.equals(SystemSerial.BoardMessageOutput())) {
						
						State = CheckState.ShakingMotorError;
						
					} else State = CheckState.BlankMode;
					break;
					
				case BlankMode			:
					CheckError = HomeActivity.NORMAL_OPERATION;
					TemperatureCheckObj.start();
					i = MOTOR_CHECK_NUMBER;
					State = CheckState.Step1Position;
					break;
					
				case ShakingMotorError	:
					CheckError = HomeActivity.SHAKING_MOTOR_ERROR;
					BlankStepObj.start();
					i = MOTOR_CHECK_NUMBER;
					break;
					
				case FilterMotorError	:
					CheckError = HomeActivity.FILTER_MOTOR_ERROR;
					BlankStepObj.start();
					i = MOTOR_CHECK_NUMBER;
					break;
				
				case PhotoSensorError	:
					CheckError = HomeActivity.PHOTO_SENSOR_ERROR;
					BlankStepObj.start();
					i = MOTOR_CHECK_NUMBER;
					break;
				}
				
				if(i == MOTOR_CHECK_NUMBER) break;
			}	
		}
	}
	
	public class TemperatureCheck extends Thread {
		
		public void run() {
			
			int i;
			double tmp, 
				   tempTmp = 0;
			boolean ambTmpCheckFlag = false;
			AmbTmpState state = AmbTmpState.FirstTmp;
			
			BlankStep BlankStepObj = new BlankStep();
			
			/* Temperature setting */
			SystemTmp = new Temperature(); // to test
			SystemTmp.TmpInit(); // to test
			
			for(i = 0; i < NUMBER_TEMPERATURE_CHECK; i++) {
				
				tmp = SystemTmp.CellTmpRead();
//				Log.w("TemperatureCheck", "Cell Temperature : " + tmp);
				
				if((tmp < (Temperature.InitTmp - 1)) || ((Temperature.InitTmp + 1) < tmp)) SerialPort.Sleep(5000);
				else break;
			}
			
			if(i != NUMBER_TEMPERATURE_CHECK) {
			
				for(i = 0; i < NUMBER_TEMPERATURE_CHECK; i++) {
					
					tmp = SystemTmp.AmbTmpRead();
//					Log.w("TemperatureCheck", "Amb Temperature : " + tmp);
					
					switch(state) {
					
					case FirstTmp	:
						tempTmp = tmp;
						state = AmbTmpState.SecondTmp;
						break;
						
					case SecondTmp	:
						if(tempTmp == tmp) state = AmbTmpState.ThirdTmp;
						else state = AmbTmpState.FirstTmp;
						break;

					case ThirdTmp	:
						if(tempTmp == tmp) state = AmbTmpState.ForthTmp;
						else state = AmbTmpState.FirstTmp;
						break;
						
					case ForthTmp	:
						if(tempTmp == tmp) state = AmbTmpState.FifthTmp;
						else state = AmbTmpState.FirstTmp;
						break;
						
					case FifthTmp	:
						if(tempTmp == tmp) ambTmpCheckFlag = true;
						else state = AmbTmpState.FirstTmp;
						break;
					}
					
					if(ambTmpCheckFlag) break;
					
					SerialPort.Sleep(5000);
				}
				
				if(i != NUMBER_TEMPERATURE_CHECK) {
					
					PhotoCheck PhotoCheckObj = new PhotoCheck();
					PhotoCheckObj.start();
				
				} else {
					
					CheckError = HomeActivity.AMBIENT_TEMPERATURE_ERROR;
					BlankStepObj.start();
				}

			} else {
				
				CheckError = HomeActivity.CELL_TEMPERATURE_ERROR;
				BlankStepObj.start();
			}
		}
	}
	
	public class PhotoCheck extends Thread {
		
		public void run() {
			
			MotionInstruct(RunActivity.MEASURE_POSITION, SerialPort.CtrTarget.PhotoSet);			
			while(!RunActivity.MEASURE_POSITION.equals(SystemSerial.BoardMessageOutput()));
				
			for(int i = 0; i < 5; i++) {
			
				MotionInstruct(RunActivity.FILTER_DARK, SerialPort.CtrTarget.PhotoSet);
				while(!RunActivity.FILTER_DARK.equals(SystemSerial.BoardMessageOutput()));
				
				MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
				while(!RunActivity.NEXT_FILTER.equals(SystemSerial.BoardMessageOutput()));
			
				RunActivity.BlankValue[1] = AbsorbanceMeasure(); // 535nm Absorbance
				
//				Log.w("PhotoCheck", "535nm blank : " + RunActivity.BlankValue[1]);
				
				MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
				while(!RunActivity.NEXT_FILTER.equals(SystemSerial.BoardMessageOutput()));
				
//				AbsorbanceMeasure();
				
//				RunActivity.BlankValue[2] = SystemRun.AbsorbanceChange(); // 660nm Absorbance
	
//				Log.w("PhotoCheck", "660nm blank : " + RunActivity.BlankValue[2]);
				
				MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
				while(!RunActivity.NEXT_FILTER.equals(SystemSerial.BoardMessageOutput()));
				
//				AbsorbanceMeasure();
				
//				RunActivity.BlankValue[3] = SystemRun.AbsorbanceChange(); // 750nm Absorbance
				
//				Log.w("PhotoCheck", "750nm blank : " + RunActivity.BlankValue[3]);
			}			
			
			BlankStep BlankStepObj = new BlankStep();
			BlankStepObj.start();
		}
	}
	
	public class BlankStep extends Thread { // Blank run
		
		public void run() {
			
			MotionInstruct(RunActivity.MEASURE_POSITION, SerialPort.CtrTarget.PhotoSet);			
			while(!RunActivity.MEASURE_POSITION.equals(SystemSerial.BoardMessageOutput()));
			
			/* Dark filter Measurement */
			MotionInstruct(RunActivity.FILTER_DARK, SerialPort.CtrTarget.PhotoSet);
			while(!RunActivity.FILTER_DARK.equals(SystemSerial.BoardMessageOutput()));
			RunActivity.BlankValue[0] = AbsorbanceMeasure(); // Dark Absorbance
			
			/* 535nm filter Measurement */
			MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			while(!RunActivity.NEXT_FILTER.equals(SystemSerial.BoardMessageOutput()));
			RunActivity.BlankValue[1] = AbsorbanceMeasure(); // Dark Absorbance
			
			/* 660nm filter Measurement */
			MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			while(!RunActivity.NEXT_FILTER.equals(SystemSerial.BoardMessageOutput()));
			RunActivity.BlankValue[2] = AbsorbanceMeasure(); // Dark Absorbance
			
			/* 750nm filter Measurement */
			MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			while(!RunActivity.NEXT_FILTER.equals(SystemSerial.BoardMessageOutput()));
			RunActivity.BlankValue[3] = AbsorbanceMeasure(); // Dark Absorbance
			
			/* Return to the original position */
			MotionInstruct(RunActivity.FILTER_DARK, SerialPort.CtrTarget.PhotoSet);
			while(!RunActivity.FILTER_DARK.equals(SystemSerial.BoardMessageOutput()));
			
			MotionInstruct(RunActivity.HOME_POSITION, SerialPort.CtrTarget.PhotoSet);
			while(!RunActivity.HOME_POSITION.equals(SystemSerial.BoardMessageOutput()));
			
			SerialPort.Sleep(1000);
						
			systemCheckAni.stop();
			
			WhichIntent(HomeActivity.TargetIntent.Home);
		}
	}
	
	public void MotionInstruct(String str, SerialPort.CtrTarget target) { // Motion of system instruction
		
		SystemSerial.BoardTx(str, target);
	}
	
	public void SystemAniStart() { // System Check animation start
		
		systemCheckImage = (ImageView)findViewById(R.id.systemcheckani);
		systemCheckAni = (AnimationDrawable)systemCheckImage.getBackground();
		
		systemCheckLinear.post(new Runnable() {
	        public void run() {

	        	systemCheckAni.start();
	        }
		});
	}
	
	public synchronized double AbsorbanceMeasure() { // Absorbance measurement
		
		String rawValue;
		double douValue = 0;
		
		SystemSerial.BoardTx("VH", SerialPort.CtrTarget.PhotoSet);
			
		rawValue = SystemSerial.BoardMessageOutput();			
		douValue = Double.parseDouble(rawValue);
		
		return (douValue - RunActivity.BlankValue[0]);
	}
	
	public void VolumeInit() { 
		
		int volume;
		
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		
		volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		
		if((volume % 3) != 0 ) {
			
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 3, AudioManager.FLAG_PLAY_SOUND);
		}
	}
	
	public void BrightnessInit() {
		
		int brightness;
		
		try {
		
			brightness = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
		
			if((brightness % 51) != 0) {
				
				WindowManager.LayoutParams params = getWindow().getAttributes();
				params.screenBrightness = (float)brightness/100;
				getWindow().setAttributes(params);
				
				android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, brightness);
			}
		} catch (Exception e) {
			
		}
	}
	
	public void ParameterInit() { // Load to saved various parameter
		
		SharedPreferences DcntPref = getSharedPreferences("Data Counter", MODE_PRIVATE);
		RemoveActivity.PatientDataCnt = DcntPref.getInt("PatientDataCnt", 1);
		RemoveActivity.ControlDataCnt = DcntPref.getInt("ControlDataCnt", 1);
		
		SharedPreferences AdjustmentPref = getSharedPreferences("User Define", MODE_PRIVATE);
		RunActivity.AF_Slope = AdjustmentPref.getFloat("AF SlopeVal", 1.0f);
		RunActivity.AF_Offset = AdjustmentPref.getFloat("AF OffsetVal", 0f);
		RunActivity.CF_Slope = AdjustmentPref.getFloat("CF SlopeVal", 1.0f);
		RunActivity.CF_Offset = AdjustmentPref.getFloat("CF OffsetVal", 0f);
		
		SharedPreferences LoginPref = getSharedPreferences("Log in", MODE_PRIVATE);
		HomeActivity.LoginFlag = LoginPref.getBoolean("Activation", true);
		HomeActivity.CheckFlag = LoginPref.getBoolean("Check Box", false);
	}
	
	public void ErrorPopup(final byte error) {
		
		new Thread(new Runnable() {
		    public void run() {    
		        runOnUiThread(new Runnable(){
		            public void run() {
		            			            	
		            	errorPopup.showAtLocation(systemCheckLinear, Gravity.CENTER, 0, 0);
						errorPopup.setAnimationStyle(0);
											
						TextView errorText = (TextView) errorPopup.getContentView().findViewById(R.id.errortext);
						
						switch(error) {
						
						case	HomeActivity.DOOR_SENSOR_ERROR		:
							errorText.setText(R.string.e001);
							break;
						
						case	HomeActivity.CART_SENSOR_ERROR		:
							errorText.setText(R.string.e011);
							break;
						}
		            }
		        });
		    }
		}).start();
	}
	
	public void WhichIntent(TargetIntent Itn) { // Activity conversion
		
		switch(Itn) {
		
		case Home		:				
			Intent HomeIntent = new Intent(getApplicationContext(), HomeActivity.class);
			HomeIntent.putExtra("System Check State", (int) CheckError);
			startActivity(HomeIntent);
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
