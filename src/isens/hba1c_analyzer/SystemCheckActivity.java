package isens.hba1c_analyzer;

import isens.hba1c_analyzer.CalibrationActivity.Cart1stShaking;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.RunActivity.AnalyzerState;
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

	static byte NUMBER_CELL_BLOCK_TEMP_CHECK = 60;
	static final byte NUMBER_AMBIENT_TEMP_CHECK = 30/5;
	final static String SHAKING_CHECK_TIME = "0030";
	
	private enum TmpState {FirstTmp, SecondTmp, ThirdTmp, ForthTmp, FifthTmp}
	
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
	
	private AnalyzerState systemState;
	
	public TmpState tmpNumber;
	
	private byte checkError;
	private byte photoCheck;
	
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
		
		/* Temperature setting */
		SystemTmp = new Temperature(); // to test
		SystemTmp.TmpInit(); // to test
		
		ParameterInit();

		BrightnessInit();
		
		VolumeInit();
		
		WhichIntent(TargetIntent.Home);
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
		
		photoCheck = 0;
		checkError = HomeActivity.NORMAL_OPERATION;
		
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

	public void WhichIntent(TargetIntent Itn) { // Activity conversion
		
		switch(Itn) {
		
		case Home		:				
			Intent HomeIntent = new Intent(getApplicationContext(), HomeActivity.class);
			HomeIntent.putExtra("System Check State", (int) checkError);
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
