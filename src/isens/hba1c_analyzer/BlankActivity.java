package isens.hba1c_analyzer;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.RunActivity.AnalyzerState;
import isens.hba1c_analyzer.TimerDisplay.whichClock;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BlankActivity extends Activity {

	private SerialPort BlankSerial;
	private RunActivity BlankRun;
	public Temperature BlankTemp;
	
	private static TextView TimeText;
	private ImageView barani;
	
	private RunActivity.AnalyzerState blankState;
	
	public static double StartCellBlockTemp[] = new double[5],
						 StartAmbientTemp[] = new double[5];
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.blank);
		
		TimeText = (TextView) findViewById(R.id.timeText);
		
		barani = (ImageView) findViewById(R.id.progressBar);
		
		BlankInit();
	}                     
	
	public void BlankInit() {
		
		TimerDisplay.timerState = whichClock.BlankClock;		
		CurrTimeDisplay();
				
		BlankSerial = new SerialPort();
		BlankRun = new RunActivity();
		
		blankState = RunActivity.AnalyzerState.MeasurePosition;
		
		switch(TestActivity.WhichTest) {
		
		case TestActivity.PHOTO_TEMPERATURE	:
			BlankTemp = new Temperature();
			
			for(int i = 0; i < 5; i++) {
				
				StartCellBlockTemp[i] = BlankTemp.CellTmpRead();
				StartAmbientTemp[i] = BlankTemp.AmbTmpRead();
			}
			break;
			
		default	:
			break;
		}
		
		BlankStep BlankBlank = new BlankStep();
		BlankBlank.start();
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
	
	public class BlankStep extends Thread { // Blank run
		
		public void run() {
			
			for(int i = 0; i < 7; i++) {
				
				switch(blankState) {
				
				case MeasurePosition :
					MotionInstruct(RunActivity.MEASURE_POSITION, SerialPort.CtrTarget.PhotoSet);			
					BoardMessage(RunActivity.MEASURE_POSITION, 5, RunActivity.AnalyzerState.FilterDark);
					BarAnimation(178);
					break;
					
				case FilterDark		:
					MotionInstruct(RunActivity.FILTER_DARK, SerialPort.CtrTarget.PhotoSet);
					BoardMessage(RunActivity.FILTER_DARK, 5, RunActivity.AnalyzerState.NoResponse);
					BarAnimation(206);
					break;
				}
			}
			
			/* Dark filter Measurement */
			RunActivity.BlankValue[0] = 0;
			RunActivity.BlankValue[0] = AbsorbanceMeasure(); // Dark Absorbance
			
			/* 535nm filter Measurement */
			MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			while(!RunActivity.NEXT_FILTER.equals(BlankSerial.BoardMessageOutput()));
			BarAnimation(290);
			RunActivity.BlankValue[1] = AbsorbanceMeasure(); // Dark Absorbance
			
			/* 660nm filter Measurement */
			MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			while(!RunActivity.NEXT_FILTER.equals(BlankSerial.BoardMessageOutput()));
			BarAnimation(374);
			RunActivity.BlankValue[2] = AbsorbanceMeasure(); // Dark Absorbance
			
			/* 750nm filter Measurement */
			MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
			while(!RunActivity.NEXT_FILTER.equals(BlankSerial.BoardMessageOutput()));
			BarAnimation(458);
			RunActivity.BlankValue[3] = AbsorbanceMeasure(); // Dark Absorbance
			
			/* Return to the original position */
			MotionInstruct(RunActivity.FILTER_DARK, SerialPort.CtrTarget.PhotoSet);
			while(!RunActivity.FILTER_DARK.equals(BlankSerial.BoardMessageOutput()));
			BarAnimation(542);
			
			MotionInstruct(RunActivity.HOME_POSITION, SerialPort.CtrTarget.PhotoSet);
			while(!RunActivity.HOME_POSITION.equals(BlankSerial.BoardMessageOutput()));
			
			BarAnimation(579);
			
			SerialPort.Sleep(1000);
	
			WhichIntent(TargetIntent.Action);
		}
	}
	
	public void MotionInstruct(String str, SerialPort.CtrTarget target) { // Motion of system instruction
		
		BlankSerial.BoardTx(str, target);
	}
	
	public synchronized double AbsorbanceMeasure() { // Absorbance measurement
		
		String rawValue;
		double douValue = 0;
		
		BlankSerial.BoardTx("VH", SerialPort.CtrTarget.PhotoSet);
			
		rawValue = BlankSerial.BoardMessageOutput();			
		douValue = Double.parseDouble(rawValue);
		
		return (douValue - RunActivity.BlankValue[0]);
	}
	
	public void BoardMessage(String rspStr, int rspTime, AnalyzerState nextState) {
		
		int sTime,
			cTime;
		boolean isNoralTime = true;
		
		sTime = TimerDisplay.cnt;
		
		while(!rspStr.equals(BlankSerial.BoardMessageOutput())) {
			
			cTime = TimerDisplay.cnt;
			
			if(sTime > cTime)
				
				if((sTime - cTime) > rspTime) isNoralTime = false;
				
			else
				
				if((cTime - sTime) > rspTime) isNoralTime = false;
		}
		
		if(isNoralTime) blankState = AnalyzerState.NoResponse;
		else blankState = AnalyzerState.NoResponse;
	}
	
	public void BarAnimation(final int x) {

		new Thread(new Runnable() {
		    public void run() {    
		        runOnUiThread(new Runnable(){
		            public void run() {
		
		            	ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(barani.getLayoutParams());
		            	margin.setMargins(x, 273, 0, 0);
		            	barani.setLayoutParams(new RelativeLayout.LayoutParams(margin));
		            }
		        });
		    }
		}).start();	
	}
	
	public void WhichIntent(TargetIntent Itn) { // Activity conversion
		
		switch(Itn) {
		
		case Home	:				
			Intent HomeIntent = new Intent(getApplicationContext(), HomeActivity.class);
			startActivity(HomeIntent);
			break;
			
		case Action	:				
			Intent ActionIntent = new Intent(getApplicationContext(), ActionActivity.class);
			startActivity(ActionIntent);
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
