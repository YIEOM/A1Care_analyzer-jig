package isens.hba1c_analyzer;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.TimerDisplay.whichClock;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RunActivity extends Activity {
	
	/* Instruction to motor for filter */
	final static String HOME_POSITION    = "CH", //CO
						MEASURE_POSITION = "CM", //MO
						Step1st_POSITION = "C1",
						Step2nd_POSITION = "C2",
						CARTRIDGE_DUMP   = "CD",
						FILTER_DARK      = "FD", //DO
						FILTER_SPto535nm = "FR", //RO
						FILTER_535nm     = "AR", 
						FILTER_660nm     = "FG", //AG
						FILTER_750nm     = "FB", //AB	
						OPERATE_COMPLETE = "DO",
						MOTOR_COMPLETE   = "AR",
						NEXT_FILTER		 = "FS",
						MOTOR_STOP		 = "MS";
	
	public enum AnalyzerState {MeasurePosition, FilterDark, Filter535nm, Filter660nm, Filter750nm, FilterHome, CartridgeHome, NoResponse}

	private DecimalFormat ShkDf = new DecimalFormat("0000");
	
	final static byte FIRST_SHAKING_TIME = 105, // Motor shaking time, default : 6 * 105(sec) = 0630
					  SECOND_SHAKING_TIME = 90; // Motor shaking time, default : 6 * 90(sec) = 0540
	
	final static double MinAbsorb = 160000,
						MaxAbsorb = 175000;
	
	final static byte NORMAL_RESULT = 1,
					  STOP_RESULT = 2,
					  ERROR_RESULT = 3;
	
	private static boolean MotorShakeFlag = false;

	public Handler runHandler = new Handler();
	public Timer runTimer;
	
	public Toast toast;
	
	public SerialPort RunSerial;
	
	private RelativeLayout runLinear;
	private View escPopupView,
				 errorPopupView;
	private PopupWindow escPopup,
						errorPopup;
	
	private Button escIcon,
				   yesBtn,
				   noBtn;

	public static TextView TimeText,
						   RunTimeText;
	
	public ImageView barani;
	
	public static double BlankValue[]     = new double[4],
						 Step1stValue1[]  = new double[3],
						 Step1stValue2[]  = new double[3],
						 Step1stValue3[]  = new double[3],
						 Step2ndValue1[]  = new double[3],
						 Step2ndValue2[]  = new double[3],
						 Step2ndValue3[]  = new double[3],
						 Step1stAbsorb1[] = new double[3],
						 Step1stAbsorb2[] = new double[3],
						 Step1stAbsorb3[] = new double[3],
						 Step2ndAbsorb1[] = new double[3],
						 Step2ndAbsorb2[] = new double[3],
						 Step2ndAbsorb3[] = new double[3];
	
	private static boolean CartStepStopped = false;
	
	public static byte runSec = 0,
					   runMin = 0;
	
	public static double tHbDbl,
						 HbA1cPctDbl,
						 douValue;
	public static String HbA1cPctStr = "0.0";
	
	public static float AF_Slope,
						AF_Offset,
						CF_Slope,
						CF_Offset;
	
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.run);
		
		TimeText = (TextView) findViewById(R.id.timeText);
		RunTimeText = (TextView) findViewById(R.id.runTimeText);
		
		RunSerial = new SerialPort(); // to test
		
		/* Esc Pop-up window */
		runLinear = (RelativeLayout)findViewById(R.id.runlinear);
		escPopupView = View.inflate(this, R.layout.escpopup, null);
		escPopup = new PopupWindow(escPopupView, 504, 174, true);
		
		/* Error Pop-up window */
		errorPopupView = View.inflate(this, R.layout.errorbtnpopup, null);
		errorPopup = new PopupWindow(errorPopupView, 504, 174, true);
		
		/* esc pop-up window activation */
		escIcon = (Button)findViewById(R.id.escicon);
		escIcon.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				escIcon.setEnabled(false);
				noBtn.setEnabled(true);
				
				escPopup.showAtLocation(runLinear, Gravity.CENTER, 0, 0);
				escPopup.setAnimationStyle(0);
				escPopup.showAsDropDown(escIcon);
			}
		});
		
		/* HOME activity activation */
		yesBtn = (Button)escPopupView.findViewById(R.id.yesbtn);
		yesBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				yesBtn.setEnabled(false);
				
				CartStepStopped = true;
								
				WaitPopup();
				
				if(MotorShakeFlag) MotionInstruct(MOTOR_STOP, SerialPort.CtrTarget.MotorStop);
			}
		});
		
		/* esc pop-up window close */
		noBtn = (Button)escPopupView.findViewById(R.id.nobtn);
		noBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				noBtn.setEnabled(false);
				escIcon.setEnabled(true);
				
				escPopup.dismiss();
			}
		});
	
		Cart1stShaking Cart1stShakingObj = new Cart1stShaking(); // to test
		Cart1stShakingObj.start(); // to test
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
	
	public void WaitPopup() { // Waiting popup window activation
		
		escPopup.dismiss();
		
		runLinear = (RelativeLayout)findViewById(R.id.runlinear);
		escPopupView = View.inflate(this, R.layout.waitpopup, null);
		escPopup = new PopupWindow(escPopupView, 478, 155, true);
		
		escPopup.showAtLocation(runLinear, Gravity.CENTER, 0, 0);
		escPopup.setAnimationStyle(0);
		escPopup.showAsDropDown(escIcon);
	}
	
	public class Cart1stShaking extends Thread { // First shaking motion

		public void run() {
//			Log.w("Cart1stShaking", "start");
			BarAnimation(162);
			RunInit();
			BarAnimation(165);
			
			MotionInstruct(Step1st_POSITION, SerialPort.CtrTarget.PhotoSet);			
			BarAnimation(168);
			while(!Step1st_POSITION.equals(RunSerial.BoardMessageOutput()));
			BarAnimation(171);
			SerialPort.Sleep(500);
			
			MotionInstruct(ShkDf.format(FIRST_SHAKING_TIME * 6), SerialPort.CtrTarget.MotorSet);  // Motor shaking time, default : 6.5 * 10(sec) = 0065
			MotorShakeFlag = true;
			ShakingAniThread ShakingAniThreadObj = new ShakingAniThread(174, FIRST_SHAKING_TIME);
			ShakingAniThreadObj.start();
			while(!MOTOR_COMPLETE.equals(RunSerial.BoardMessageOutput())); // temporary
			MotorShakeFlag = false;
			
			if(!CartStepStopped) {

				Cart1stFilter1 Cart1stFilter1Obj = new Cart1stFilter1();
				Cart1stFilter1Obj.start();	
			} else {

				CartStop CartStopObj = new CartStop(STOP_RESULT);
				CartStopObj.start();
			}	
		}
	}

	public class Cart1stFilter1 extends Thread { // First filter motion of first shaking 

		public void run() {

			BarAnimation(279);
			SerialPort.Sleep(2000);
			BarAnimation(282);
			
			MotionInstruct(MEASURE_POSITION, SerialPort.CtrTarget.PhotoSet);
			BarAnimation(285);
			while(!MEASURE_POSITION.equals(RunSerial.BoardMessageOutput()));
			BarAnimation(288);
			
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			BarAnimation(291);
			while(!NEXT_FILTER.equals(RunSerial.BoardMessageOutput()));
			BarAnimation(294);
			Step1stValue1[0] = AbsorbanceMeasure(); // 535nm Absorbance
			
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			BarAnimation(297);
			while(!NEXT_FILTER.equals(RunSerial.BoardMessageOutput()));
			BarAnimation(300);
			
			Step1stValue1[1] = AbsorbanceMeasure(); // 660nm Absorbance
		
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			BarAnimation(303);
			while(!NEXT_FILTER.equals(RunSerial.BoardMessageOutput()));
			BarAnimation(306);
			
			Step1stValue1[2] = AbsorbanceMeasure(); // 750nm Absorbance
			
			MotionInstruct(FILTER_DARK, SerialPort.CtrTarget.PhotoSet);
			BarAnimation(309);
			while(!FILTER_DARK.equals(RunSerial.BoardMessageOutput()));
			BarAnimation(312);
					
			if(!CartStepStopped) {
				
				Cart1stFilter2 Cart1stFilter2Obj = new Cart1stFilter2();
				Cart1stFilter2Obj.start();	
			} else {
				
				CartStop CartStopObj = new CartStop(STOP_RESULT);
				CartStopObj.start();
			}
		}
	}

	public class Cart1stFilter2 extends Thread { // Second filter motion of first shaking

		public void run() {
			
			SerialPort.Sleep(1000);
			BarAnimation(315);
			
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			BarAnimation(318);
			while(!NEXT_FILTER.equals(RunSerial.BoardMessageOutput()));
			BarAnimation(321);
			Step1stValue2[0] = AbsorbanceMeasure(); // 535nm Absorbance
			
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			BarAnimation(324);
			while(!NEXT_FILTER.equals(RunSerial.BoardMessageOutput()));
			BarAnimation(327);
			
			Step1stValue2[1] = AbsorbanceMeasure(); // 660nm Absorbance
			
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			BarAnimation(330);
			while(!NEXT_FILTER.equals(RunSerial.BoardMessageOutput()));
			BarAnimation(333);
			
			Step1stValue2[2] = AbsorbanceMeasure(); // 750nm Absorbance
			
			MotionInstruct(FILTER_DARK, SerialPort.CtrTarget.PhotoSet);
			BarAnimation(336);
			while(!FILTER_DARK.equals(RunSerial.BoardMessageOutput()));
			BarAnimation(339);
				
			if(!CartStepStopped) {
				
				Cart1stFilter3 Cart1stFilter3Obj = new Cart1stFilter3();
				Cart1stFilter3Obj.start();	
			} else {
				
				CartStop CartStopObj = new CartStop(STOP_RESULT);
				CartStopObj.start();
			}
		}
	}
	
	public class Cart1stFilter3 extends Thread { // Third filter motion of first shaking

		public void run() {
			
			SerialPort.Sleep(1000);
			BarAnimation(342);
			
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			BarAnimation(345);
			while(!NEXT_FILTER.equals(RunSerial.BoardMessageOutput()));
			BarAnimation(348);
			Step1stValue3[0] = AbsorbanceMeasure(); // 535nm Absorbance
			
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			BarAnimation(351);
			while(!NEXT_FILTER.equals(RunSerial.BoardMessageOutput()));
			BarAnimation(354);
			
			Step1stValue3[1] = AbsorbanceMeasure(); // 660nm Absorbance
			
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			BarAnimation(357);
			while(!NEXT_FILTER.equals(RunSerial.BoardMessageOutput()));
			BarAnimation(360);
			
			Step1stValue3[2] = AbsorbanceMeasure(); // 750nm Absorbance
			
			MotionInstruct(FILTER_DARK, SerialPort.CtrTarget.PhotoSet);
			BarAnimation(363);
			while(!FILTER_DARK.equals(RunSerial.BoardMessageOutput()));
			BarAnimation(366);
				
			if(!CartStepStopped) {
				
				Cart2ndShaking Cart2ndShakingObj = new Cart2ndShaking();
				Cart2ndShakingObj.start();	
			} else {
				
				CartStop CartStopObj = new CartStop(STOP_RESULT);
				CartStopObj.start();
			}
		}
	}
	
	public class Cart2ndShaking extends Thread { // Second shaking motion
		
		public void run() {			
			
			MotionInstruct(Step2nd_POSITION, SerialPort.CtrTarget.PhotoSet);
			BarAnimation(369);
			while(!Step2nd_POSITION.equals(RunSerial.BoardMessageOutput()));
			BarAnimation(372);
						
			MotionInstruct(ShkDf.format(SECOND_SHAKING_TIME * 6), SerialPort.CtrTarget.MotorSet);  // Motor shaking time, default : 6 * 10(sec) = 0065
			MotorShakeFlag = true;
			ShakingAniThread ShakingAniThreadObj = new ShakingAniThread(375, SECOND_SHAKING_TIME);
			ShakingAniThreadObj.start();
			while(!MOTOR_COMPLETE.equals(RunSerial.BoardMessageOutput())); // temporary
			MotorShakeFlag = false;
			
			if(!CartStepStopped) {
				
				Cart2ndFilter1 Cart2ndFilter1Obj = new Cart2ndFilter1();
				Cart2ndFilter1Obj.start();	
			} else {
				
				CartStop CartStopObj = new CartStop(STOP_RESULT);
				CartStopObj.start();
			}
		}
	}
	
	public class Cart2ndFilter1 extends Thread { // First filter motion of second shaking
		
		public void run() {			

			BarAnimation(484);	
			SerialPort.Sleep(2000);
			BarAnimation(487);
			
			MotionInstruct(MEASURE_POSITION, SerialPort.CtrTarget.PhotoSet);
			BarAnimation(490);
			while(!MEASURE_POSITION.equals(RunSerial.BoardMessageOutput()));
			BarAnimation(493);
			
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			BarAnimation(496);
			while(!NEXT_FILTER.equals(RunSerial.BoardMessageOutput()));
			BarAnimation(499);
			Step2ndValue1[0] = AbsorbanceMeasure(); // 535nm Absorbance
			
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			BarAnimation(502);
			while(!NEXT_FILTER.equals(RunSerial.BoardMessageOutput()));
			BarAnimation(505);
			Step2ndValue1[1] = AbsorbanceMeasure(); // 535nm Absorbance
			
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			BarAnimation(508);
			while(!NEXT_FILTER.equals(RunSerial.BoardMessageOutput()));
			BarAnimation(511);
			Step2ndValue1[2] = AbsorbanceMeasure(); // 535nm Absorbance
			
			MotionInstruct(FILTER_DARK, SerialPort.CtrTarget.PhotoSet);
			BarAnimation(514);
			while(!FILTER_DARK.equals(RunSerial.BoardMessageOutput()));
			BarAnimation(517);
			
			if(!CartStepStopped) {
				
				Cart2ndFilter2 Cart2ndFilter2Obj = new Cart2ndFilter2();
				Cart2ndFilter2Obj.start();	
			} else {
				
				CartStop CartStopObj = new CartStop(STOP_RESULT);
				CartStopObj.start();
			}
		}
	}
	
	public class Cart2ndFilter2 extends Thread { // Second filter motion of second shaking
		
		public void run() {
			
			SerialPort.Sleep(1000);
			BarAnimation(520);
						
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			BarAnimation(523);
			while(!NEXT_FILTER.equals(RunSerial.BoardMessageOutput()));
			BarAnimation(526);
			Step2ndValue2[0] = AbsorbanceMeasure(); // 535nm Absorbance
			
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			BarAnimation(529);
			while(!NEXT_FILTER.equals(RunSerial.BoardMessageOutput()));
			BarAnimation(532);
			Step2ndValue2[1] = AbsorbanceMeasure(); // 535nm Absorbance
			
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			BarAnimation(535);
			while(!NEXT_FILTER.equals(RunSerial.BoardMessageOutput()));
			BarAnimation(538);
			Step2ndValue2[2] = AbsorbanceMeasure(); // 535nm Absorbance
			
			MotionInstruct(FILTER_DARK, SerialPort.CtrTarget.PhotoSet);
			BarAnimation(541);
			while(!FILTER_DARK.equals(RunSerial.BoardMessageOutput()));
			BarAnimation(544);
			
			if(!CartStepStopped) {
				
				Cart2ndFilter3 Cart2ndFilter3Obj = new Cart2ndFilter3();
				Cart2ndFilter3Obj.start();	
			} else {
				
				CartStop CartStopObj = new CartStop(STOP_RESULT);
				CartStopObj.start();
			}
		}
	}
	
	public class Cart2ndFilter3 extends Thread { // Third filter motion of second shaking
		
		public void run() {
			
			SerialPort.Sleep(1000);
			BarAnimation(547);
			
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			BarAnimation(550);
			while(!NEXT_FILTER.equals(RunSerial.BoardMessageOutput()));
			BarAnimation(553);
			Step2ndValue3[0] = AbsorbanceMeasure(); // 535nm Absorbance
			
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			BarAnimation(556);
			while(!NEXT_FILTER.equals(RunSerial.BoardMessageOutput()));
			BarAnimation(559);
			Step2ndValue3[1] = AbsorbanceMeasure(); // 535nm Absorbance
			
			MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			BarAnimation(562);
			while(!NEXT_FILTER.equals(RunSerial.BoardMessageOutput()));
			BarAnimation(565);
			Step2ndValue3[2] = AbsorbanceMeasure(); // 535nm Absorbance
			
			MotionInstruct(FILTER_DARK, SerialPort.CtrTarget.PhotoSet);
			BarAnimation(568);
			while(!FILTER_DARK.equals(RunSerial.BoardMessageOutput()));
			BarAnimation(571);
			
			if(!CartStepStopped) {
				
				CartDump CartDumpObj = new CartDump();
				CartDumpObj.start();	
			} else {
				
				CartStop CartStopObj = new CartStop(STOP_RESULT);
				CartStopObj.start();
			}
		}
	}
	
	public class CartDump extends Thread { // Cartridge dumping motion
		
		public void run() {
						
			MotionInstruct(CARTRIDGE_DUMP, SerialPort.CtrTarget.PhotoSet);
			BarAnimation(574);
			while(!CARTRIDGE_DUMP.equals(RunSerial.BoardMessageOutput()));
			BarAnimation(577);

			MotionInstruct(HOME_POSITION, SerialPort.CtrTarget.PhotoSet);
			BarAnimation(580);
			while(!HOME_POSITION.equals(RunSerial.BoardMessageOutput()));
			BarAnimation(583);

			HbA1cCalculate();
			BarAnimation(586);
		}
	}
	
	public class CartStop extends Thread { // Cartridge dumping motion
		
		private int whichState;
		
		CartStop(int whichState) {
			
			this.whichState = whichState;
		}
		
		public void run() {
			
			SerialPort.Sleep(500);
			
			MotionInstruct(FILTER_DARK, SerialPort.CtrTarget.PhotoSet);
			while(!FILTER_DARK.equals(RunSerial.BoardMessageOutput()));
			
			MotionInstruct(CARTRIDGE_DUMP, SerialPort.CtrTarget.PhotoSet);		
			while(!CARTRIDGE_DUMP.equals(RunSerial.BoardMessageOutput()));

			MotionInstruct(HOME_POSITION, SerialPort.CtrTarget.PhotoSet);
			while(!HOME_POSITION.equals(RunSerial.BoardMessageOutput()));
			
			switch(whichState) {
			
			case STOP_RESULT	:
				WhichIntent(TargetIntent.ResultStop);
				break;
					
			case ERROR_RESULT	:
				WhichIntent(TargetIntent.ResultError);
				break;
			
			default	:
				break;
			}
		}
	}
	
	public void RunTimeDisplay() { // Display running time
		
		if(runSec == 60) {
			
			runMin++;
			runSec = 0;
		}
			
		runSec++;
		
		RunTimeText.setText(Integer.toString(runMin) + " min " + Integer.toString(runSec) + " sec");
	}
	
	public void RunInit() {
		
		runSec = 0;
		runMin = 0;
		HbA1cPctStr = "0.0";
		MotorShakeFlag = false;
		
		TimerDisplay.timerState = whichClock.RunClock;
		CurrTimeDisplay();
		
		RunTimerInit();
		CartStepStopped = false;
		
//		SerialPort.Sleep(300);
	}
	
	public void RunTimerInit() {
		
		TimerTask OneSecondPeriod = new TimerTask() {
			
			public void run() {
				Runnable updater = new Runnable() {
					public void run() {
						
						RunTimeDisplay();
					}
				};
				
				runHandler.post(updater);		
			}
		};
		
		runTimer = new Timer();
		runTimer.schedule(OneSecondPeriod, 0, 1000); // Timer period : 100msec
	}
	
	public void MotionInstruct(String str, SerialPort.CtrTarget target) { // Motion of system instruction
		
		RunSerial.BoardTx(str, target);
	}

	public synchronized double AbsorbanceMeasure() { // Absorbance measurement
		
		String rawValue;
		double douValue = 0;
		
		RunSerial.BoardTx("VH", SerialPort.CtrTarget.PhotoSet);
			
		rawValue = RunSerial.BoardMessageOutput();
		Log.w("Absorbance measure", "raw : " + rawValue);
		douValue = Double.parseDouble(rawValue);
		
		return (douValue - BlankValue[0]);
	}
	
	public synchronized void HbA1cCalculate() { // Calculation for HbA1c percentage
		
		double A, B, St, Bt, SLA, SHA, BLA, BHA, SLV, SHV, BLV, BHV, a3, b3, b32, a4, b4;
					
		switch(TestActivity.WhichTest) {
		
		case TestActivity.PHOTO_TEMPERATURE	:
			Barcode.a1 = 0.0109;
			Barcode.b1 = -0.0137;
			Barcode.L = 5.8;
			Barcode.H = 8.7;
			Barcode.a21 = 0.0461;
			Barcode.b21 = 0.0246;
			Barcode.a22 = 0.039;
			Barcode.b22 = 0.0219;
			break;
			
		default	:
			break;
		}
		
		A = Absorb1stHandling();
		B = Absorb2ndHandling();
		
		St = (A - Barcode.b1)/Barcode.a1;
		tHbDbl = St;
		Bt = (A - Barcode.b1)/Barcode.a1 + 1;
		
		SLA = St * Barcode.L / 100;
		SHA = St * Barcode.H / 100;
		BLA = Bt * Barcode.L / 100;
		BHA = Bt * Barcode.H / 100;
		
		SLV = SLA * Barcode.a21 + Barcode.b21;
		SHV = SHA * Barcode.a22 + Barcode.b22;
		BLV = BLA * Barcode.a21 + Barcode.b21;
		BHV = BHA * Barcode.a22 + Barcode.b22;
		
		a3 = (SHV - SLV) / (SHA - SLA);
		b3 = SLV - (a3 * SLA);
		
		b32 = BLV - (((BHV - BLV) / (BHA - BLA)) * BLA);
		
		a4 = (b32 - b3) / (Bt - St);
		b4 = b3 - (a4 * St);
		
		HbA1cPctDbl = (B - (St * a4 + b4)) / a3 / St * 100; // %-HbA1c(%)
		HbA1cPctDbl = CF_Slope * (AF_Slope * HbA1cPctDbl + AF_Offset) + CF_Offset;
		
		DecimalFormat hbA1cFormat = new DecimalFormat("0.0");
		
		HbA1cPctStr = hbA1cFormat.format(HbA1cPctDbl);
		
		WhichIntent(TargetIntent.Result);
	}
	
	public double Absorb1stHandling() {
		
		double abs[] = new double[3],
			   dev[] = new double[3],
			   std, 
			   sum, 
			   avg, 
			   res;
		int idx = 0;
		
		/* Step 1st Absorbance */
		Step1stAbsorb1[0] = -Math.log10(Step1stValue1[0]/BlankValue[1]); // 535nm
		Step1stAbsorb1[1] = -Math.log10(Step1stValue1[1]/BlankValue[2]); // 660nm
		Step1stAbsorb1[2] = -Math.log10(Step1stValue1[2]/BlankValue[3]); // 750nm
		
		Step1stAbsorb2[0] = -Math.log10(Step1stValue2[0]/BlankValue[1]);
		Step1stAbsorb2[1] = -Math.log10(Step1stValue2[1]/BlankValue[2]);
		Step1stAbsorb2[2] = -Math.log10(Step1stValue2[2]/BlankValue[3]);
		
		Step1stAbsorb3[0] = -Math.log10(Step1stValue3[0]/BlankValue[1]);
		Step1stAbsorb3[1] = -Math.log10(Step1stValue3[1]/BlankValue[2]);
		Step1stAbsorb3[2] = -Math.log10(Step1stValue3[2]/BlankValue[3]);
		
		abs[0] = Step1stAbsorb1[0] - Step1stAbsorb1[2];
		abs[1] = Step1stAbsorb2[0] - Step1stAbsorb2[2];
		abs[2] = Step1stAbsorb3[0] - Step1stAbsorb3[2];
		
		std = (abs[0] + abs[1] + abs[2]) / 3;
		
		for(int i = 0; i < 3; i++) {
			
			if(std > abs[i]) dev[i] = std - abs[i];
			else dev[i] = abs[i] - std;
		}
		
		if(dev[0] > dev[1]) idx = 0; 
		else idx = 1;
		
		if(dev[2] > dev[idx]) idx = 2;
		
		sum = abs[0] + abs[1] + abs[2];
		
		avg = (sum - abs[idx]) / 2;
		
		return avg;
	}
	
	public double Absorb2ndHandling() {
		
		double abs[] = new double[3],
			   dev[] = new double[3],
			   std, 
			   sum, 
			   avg;
		int idx = 0;
		
		/* Step 2nd Absorbance */
		Step2ndAbsorb1[0] = -Math.log10(Step2ndValue1[0]/BlankValue[1]); // 535nm
		Step2ndAbsorb1[1] = -Math.log10(Step2ndValue1[1]/BlankValue[2]); // 660nm
		Step2ndAbsorb1[2] = -Math.log10(Step2ndValue1[2]/BlankValue[3]); // 750nm
		
		Step2ndAbsorb2[0] = -Math.log10(Step2ndValue2[0]/BlankValue[1]);
		Step2ndAbsorb2[1] = -Math.log10(Step2ndValue2[1]/BlankValue[2]);
		Step2ndAbsorb2[2] = -Math.log10(Step2ndValue2[2]/BlankValue[3]);
		
		Step2ndAbsorb3[0] = -Math.log10(Step2ndValue3[0]/BlankValue[1]);
		Step2ndAbsorb3[1] = -Math.log10(Step2ndValue3[1]/BlankValue[2]);
		Step2ndAbsorb3[2] = -Math.log10(Step2ndValue3[2]/BlankValue[3]);
		
		abs[0] = Step2ndAbsorb1[1] - Step2ndAbsorb1[2];
		abs[1] = Step2ndAbsorb2[1] - Step2ndAbsorb2[2];
		abs[2] = Step2ndAbsorb3[1] - Step2ndAbsorb3[2];
		
		std = (abs[0] + abs[1] + abs[2]) / 3;
				
		for(int i = 0; i < 3; i++) {
			
			if(std > abs[i]) dev[i] = std - abs[i];
			else dev[i] = abs[i] - std;
		}
		
		if(dev[0] > dev[1]) idx = 0; 
		else idx = 1;
		
		if(dev[2] > dev[idx]) idx = 2;
		
		sum = abs[0] + abs[1] + abs[2];
		
		avg = (sum - abs[idx]) / 2;
		
		return avg;
	}
	
	public class ShakingAniThread extends Thread {
		
		private int coordinates,
					time;
		
		ShakingAniThread(int coordinates, int time) {
			
			this.coordinates = coordinates;
			this.time = time;
		}
		
		public void run() {
			
			for(int i = 0; i < time/2; i++) {
				
	        	BarAnimation(coordinates + i*2);
	        	SerialPort.Sleep(2000);
	        	
	        	if(CartStepStopped) break;
			}
		}
	}
	
	public void BoardMessage(String rspStr, int rspTime, AnalyzerState nextState) {
		
		
	}
	
	public void BarAnimation(final int x) { // running bar animation

		barani = (ImageView) findViewById(R.id.progressBar);
		
		new Thread(new Runnable() {
		    public void run() {    
		        runOnUiThread(new Runnable(){
		            public void run() {
		
		            	ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(barani.getLayoutParams());
		            	margin.setMargins(x, 276, 0, 0);
		            	barani.setLayoutParams(new RelativeLayout.LayoutParams(margin));
		            }
		        });
		    }
		}).start();	
	}
	
	public void WhichIntent(TargetIntent Itn) { // Activity conversion

		Intent ResultIntent = new Intent(getApplicationContext(), ResultActivity.class);
		
		SerialPort.Sleep(1000); // Delay of error prevention		
		escPopup.dismiss();
		runTimer.cancel();
		
		SerialPort.Sleep(200);
		
		switch(Itn) {
		
		case Result	:							
			ResultIntent.putExtra("RunState", (int) NORMAL_RESULT); // Normal operation
			startActivity(ResultIntent);
			break;
						
		case ResultStop	:			
			ResultIntent.putExtra("RunState", (int) STOP_RESULT); // Stop operation
			startActivity(ResultIntent);
			break;

		case ResultError	:		
			ResultIntent.putExtra("RunState", (int) ERROR_RESULT); // Error operation
			startActivity(ResultIntent);
			break;
			
		default			:	
			break;			
		}
		
		finish();
	}
	
	public void finish() {
		
		super.finish();
		overridePendingTransition(R.anim.fade, R.anim.hold);
	}
}
