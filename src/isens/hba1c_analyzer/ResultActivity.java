package isens.hba1c_analyzer;

import isens.hba1c_analyzer.ActionActivity.BarcodeScan;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.TimerDisplay.whichClock;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ResultActivity extends Activity {
	
	final static byte ACTION_ACTIVITY = 1,
					  HOME_ACTIVITY = 2;
	
	private Temperature ResultTemp;
	private SerialPort ResultSerial;
	
	public static TextView TimeText;
	
	private TextView HbA1cText,
					 DateText,
					 AMPMText,
					 Ref;
	
	private RelativeLayout resultLinear;
	private View errorPopupView;
	private PopupWindow errorPopup;
	
	private Button homeIcon,
				   printBtn,
				   patientIDScanBtn,
				   hisBtn,
				   nextSampleBtn,
				   errorBtn;
	
	private String getTime[] = new String[6];
	
	public static int ItnData;
	public int dataCnt;
	private double EndCellBlockTemp[] = new double[5],
				   EndAmbientTemp[] = new double[5];
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.result);
		
		TimeText = (TextView) findViewById(R.id.timeText);		
		
		/* Popup window activation */
		resultLinear = (RelativeLayout)findViewById(R.id.resultlinear);
		errorPopupView = View.inflate(this, R.layout.errorbtnpopup, null);
		errorPopup = new PopupWindow(errorPopupView, 288, 255, true);
		
		ResultInit();
		
		/*Home Activity activation*/
		homeIcon = (Button)findViewById(R.id.homeicon);
		homeIcon.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				homeIcon.setEnabled(false);
				
				WhichIntent(TargetIntent.Home);
			}
		});
		
		printBtn = (Button)findViewById(R.id.printbtn);
		printBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				PrintResultData();
			}
		});
		
		patientIDScanBtn = (Button)findViewById(R.id.patientidscanbtn);
		patientIDScanBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
//				patientIDScanBtn.setEnabled(false);
			}
		});
		
		/*Test Activity activation*/
//		hisBtn = (Button)findViewById(R.id.hisbtn);
//		hisBtn.setOnClickListener(new View.OnClickListener() {
//		
//			public void onClick(View v) {

//				hisBtn.setEnabled(false);
//				
////				Log.w("Response time", "start");
//				
//				HL7Intent();
//				
//				hisBtn.setEnabled(true);
//			}
//		});
		
		/*Test Activity activation*/
		nextSampleBtn = (Button)findViewById(R.id.nextsamplebtn);
		nextSampleBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {

				nextSampleBtn.setEnabled(false);
				
				WhichIntent(TargetIntent.Run);
			}
		});
		
		/* error pop-up Close */
		errorBtn = (Button)errorPopupView.findViewById(R.id.errorbtn);
		errorBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				errorPopup.dismiss();
			}
		});
		
		ErrorPopup();
	}
	
	public void ResultInit() {
		
		TimerDisplay.timerState = whichClock.ResultClock;		
		CurrTimeDisplay();
		
		GetCurrTime();
		
		double cellBlockTemp = 0,
			   ambientTemp = 0;
					
		switch(TestActivity.WhichTest) {
		
		case TestActivity.PHOTO_TEMPERATURE	:
			ResultTemp = new Temperature();
			
			for(int i = 0; i < 5; i++) {
				
				EndCellBlockTemp[i] = ResultTemp.CellTmpRead();
				EndAmbientTemp[i] = ResultTemp.AmbTmpRead();
			}
			
			WhichIntent(TargetIntent.Test);
			break;
			
		default	:
			GetDataCnt();
			
			HbA1cText = (TextView)findViewById(R.id.hba1cPct);
			DateText = (TextView)findViewById(R.id.r_testdate1);
			AMPMText = (TextView)findViewById(R.id.r_testdate2);
			Ref = (TextView) findViewById(R.id.ref);
			
			HbA1cText.setText(RunActivity.HbA1cPctStr);
			DateText.setText(TimerDisplay.rTime[0] + "." + TimerDisplay.rTime[1] + "." + TimerDisplay.rTime[2] + " " + TimerDisplay.rTime[4] + ":" + TimerDisplay.rTime[5]);
			AMPMText.setText(TimerDisplay.rTime[3]);
			Ref.setText(Barcode.RefNum);
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
	
	public void ErrorPopup() { // E101 error pop-up window
		
		Intent itn = getIntent();
		ItnData = itn.getIntExtra("RunState", 0);
		
		if(ItnData == (int) RunActivity.ERROR_RESULT) {
			
			errorPopupView.post(new Runnable() {
		        public void run() {
		
		        	errorPopup.showAtLocation(resultLinear, Gravity.CENTER, 0, 0);
					errorPopup.setAnimationStyle(0);					
								
					TextView errorText = (TextView)errorPopup.getContentView().findViewById(R.id.errortext);
					errorText.setText("E101");	
		        }
		    });
		}
	}
	
	public void GetCurrTime() { // getting the current date and time
		
		getTime[0] = TimerDisplay.rTime[0];
		getTime[1] = TimerDisplay.rTime[1];
		getTime[2] = TimerDisplay.rTime[2];
		getTime[3] = TimerDisplay.rTime[3];		
		if(TimerDisplay.rTime[4].length() != 2) getTime[4] = "0" + TimerDisplay.rTime[4];
		else getTime[4] = TimerDisplay.rTime[4];
		getTime[5] = TimerDisplay.rTime[5];			
	}
	
	public void GetDataCnt() {
		
		if(Barcode.RefNum.substring(0, 1).equals("C")) dataCnt = RemoveActivity.ControlDataCnt;
		else if(Barcode.RefNum.substring(0, 1).equals("H")) dataCnt = RemoveActivity.PatientDataCnt;		
	}
	
	public void PrintResultData() {
		
		StringBuffer txData = new StringBuffer();
		DecimalFormat dfm = new DecimalFormat("0000");
		
		txData.delete(0, txData.capacity());
		
		txData.append(getTime[0]);
		txData.append(getTime[1]);
		txData.append(getTime[2]);
		txData.append(getTime[3]);
		txData.append(getTime[4]);
		txData.append(getTime[5]);
		txData.append(dfm.format(dataCnt));
		txData.append(Barcode.RefNum);
		txData.append(RunActivity.HbA1cPctStr);
		
		ResultSerial = new SerialPort();
		ResultSerial.PrinterTxStart(SerialPort.PRINTRESULT, txData);	
	}
	
	public void WhichIntent(TargetIntent Itn) { // Activity conversion after intent data deliver
		
		Intent DataSaveIntent = new Intent(getApplicationContext(), FileSaveActivity.class);
		DecimalFormat photoDfm = new DecimalFormat("0.0");
		DecimalFormat absorbDfm = new DecimalFormat("0.0000");
		
		DataSaveIntent.putExtra("RunState", ItnData);
		DataSaveIntent.putExtra("Year", getTime[0]);
		DataSaveIntent.putExtra("Month", getTime[1]);
		DataSaveIntent.putExtra("Day", getTime[2]);
		DataSaveIntent.putExtra("AmPm", getTime[3]);
		DataSaveIntent.putExtra("Hour", getTime[4]);
		DataSaveIntent.putExtra("Minute", getTime[5]);
		DataSaveIntent.putExtra("DataCnt", dataCnt);

		DataSaveIntent.putExtra("RefNumber", Barcode.RefNum);
		DataSaveIntent.putExtra("Hba1cPct", RunActivity.HbA1cPctStr);
		
		DataSaveIntent.putExtra("RunMin", (int) RunActivity.runMin);
		DataSaveIntent.putExtra("RunSec", (int) RunActivity.runSec);
		DataSaveIntent.putExtra("BlankVal0", photoDfm.format(RunActivity.BlankValue[0]));
		DataSaveIntent.putExtra("BlankVal1", photoDfm.format(RunActivity.BlankValue[1]));
		DataSaveIntent.putExtra("BlankVal2", photoDfm.format(RunActivity.BlankValue[2]));
		DataSaveIntent.putExtra("BlankVal3", photoDfm.format(RunActivity.BlankValue[3]));
		
		for(int i = 0; i < 3; i++) {
			
			DataSaveIntent.putExtra("St1Abs1by" + Integer.toString(i), absorbDfm.format(RunActivity.Step1stAbsorb1[i]));
			DataSaveIntent.putExtra("St1Abs2by" + Integer.toString(i), absorbDfm.format(RunActivity.Step1stAbsorb2[i]));
			DataSaveIntent.putExtra("St1Abs3by" + Integer.toString(i), absorbDfm.format(RunActivity.Step1stAbsorb3[i]));
			DataSaveIntent.putExtra("St2Abs1by" + Integer.toString(i), absorbDfm.format(RunActivity.Step2ndAbsorb1[i]));
			DataSaveIntent.putExtra("St2Abs2by" + Integer.toString(i), absorbDfm.format(RunActivity.Step2ndAbsorb2[i]));
			DataSaveIntent.putExtra("St2Abs3by" + Integer.toString(i), absorbDfm.format(RunActivity.Step2ndAbsorb3[i]));
		}
		
		switch(TestActivity.WhichTest) {
		
		case TestActivity.PHOTO_TEMPERATURE	:
			for(int i =0; i < 5; i++) {
				
				DataSaveIntent.putExtra("CellBlockTemp1" + Integer.toString(i), photoDfm.format(BlankActivity.StartCellBlockTemp[i]));
				DataSaveIntent.putExtra("CellBlockTemp2" + Integer.toString(i), photoDfm.format(ActionActivity.MidCellBlockTemp[i]));
				DataSaveIntent.putExtra("CellBlockTemp3" + Integer.toString(i), photoDfm.format(EndCellBlockTemp[i]));
				DataSaveIntent.putExtra("AmbientTemp1" + Integer.toString(i), photoDfm.format(BlankActivity.StartAmbientTemp[i]));
				DataSaveIntent.putExtra("AmbientTemp2" + Integer.toString(i), photoDfm.format(ActionActivity.MidAmbientTemp[i]));
				DataSaveIntent.putExtra("AmbientTemp3" + Integer.toString(i), photoDfm.format(EndAmbientTemp[i]));
			}
			
			DataSaveIntent.putExtra("WhichIntent", TestActivity.PHOTO_TEMPERATURE);
			startActivity(DataSaveIntent);
			break;
			
		default	:
			switch(Itn) {
			
			case Home		:							
				DataSaveIntent.putExtra("WhichIntent", (int) HOME_ACTIVITY);
				startActivity(DataSaveIntent);
				break;

			case Run	:			
				DataSaveIntent.putExtra("WhichIntent", (int) ACTION_ACTIVITY);
				startActivity(DataSaveIntent);
				break;

			default			:	
				break;			
			}
			
			break;
		}	
		
		finish();
	}
	
	public void finish() {
		
		super.finish();
		overridePendingTransition(R.anim.fade, R.anim.hold);
	}
}
