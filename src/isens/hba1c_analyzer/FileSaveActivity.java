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

public class FileSaveActivity extends Activity {

	public static byte NORMAL_RESULT = 1,
					   CONTROL_TEST = 1,
					   PATIENT_TEST = 2;
	
	private DataStorage SaveData;
	
	private TextView Text;
	private Intent itn;
	
	private StringBuffer overallData = new StringBuffer(),
						 historyData = new StringBuffer();

	public static int DataCnt;
	private int runState,
				whichState;
	private String dataType;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.filesave);
		
		Text = (TextView) findViewById(R.id.text);		
		
		DataInit();
	}
	
	public void DataInit() {
		
		SaveData = new DataStorage();
		
		itn = getIntent();
		
		whichState = itn.getIntExtra("WhichIntent", 0);
		
		switch(whichState) {
		
		case 6	:
			DataArrayPhotoTemp();
			
			SaveData.PhotoTempSave(overallData, historyData);
			break;
			
		case 8	:
			DataArrayTemp();
			
			SaveData.TestSave("/TempData", overallData);
			break;
			
		case 9	:
			DataArrayPhoto();
			
			SaveData.TestSave("/PhotoData", overallData);
			break;
		
		case 10	:
			DataArrayPhotoAbs();
			
			SaveData.TestSave("/PhotoAbsData", overallData);
			break;
			
		default	:
			DataArray();
			
			dataType = itn.getStringExtra("RefNumber").substring(0, 1);
			
			if(itn.getIntExtra("RunState", 0) == (int) NORMAL_RESULT) {

				if(dataType.equals("C")) {
					
					SaveData.DataSave(CONTROL_TEST, overallData);

				} else if(dataType.equals("H")) {
					
					SaveData.DataSave(PATIENT_TEST, overallData); // if HbA1c test is normal, the Result data is saved
				}
			}
			
			SaveData.DataHistorySave(overallData, historyData); // the History data is saved
			break;
		}		
		
		WhichIntent();
	}
	
	public void DataArray() { // Enumerating data
		
		DecimalFormat dfm = new DecimalFormat("0000");
		
		overallData.delete(0, overallData.capacity());
		historyData.delete(0, historyData.capacity());
		
		itn = getIntent();
		
		DataCnt = itn.getIntExtra("DataCnt", 0);
		dataType = itn.getStringExtra("RefNumber").substring(0, 1);

		overallData.append(itn.getStringExtra("Year"));
		overallData.append(itn.getStringExtra("Month"));
		overallData.append(itn.getStringExtra("Day"));
		overallData.append(itn.getStringExtra("AmPm"));
		overallData.append(itn.getStringExtra("Hour"));
		overallData.append(itn.getStringExtra("Minute"));
		overallData.append(dfm.format(DataCnt));
		overallData.append(itn.getStringExtra("RefNumber"));
		overallData.append(itn.getStringExtra("Hba1cPct"));			
		
		historyData.append(itn.getIntExtra("RunMin", 0) + "\t");
		historyData.append(itn.getIntExtra("RunSec", 0) + "\t");
		historyData.append(itn.getStringExtra("BlankVal0") + "\t");
		historyData.append(itn.getStringExtra("BlankVal1") + "\t");
		historyData.append(itn.getStringExtra("BlankVal2") + "\t");
		historyData.append(itn.getStringExtra("BlankVal3") + "\t");
		
		for(int i = 0; i < 3; i++) {
			
			historyData.append(itn.getStringExtra("St1Abs1by" + Integer.toString(i)) + "\t");
			historyData.append(itn.getStringExtra("St1Abs2by" + Integer.toString(i)) + "\t");
			historyData.append(itn.getStringExtra("St1Abs3by" + Integer.toString(i)) + "\t");
			historyData.append(itn.getStringExtra("St2Abs1by" + Integer.toString(i)) + "\t");
			historyData.append(itn.getStringExtra("St2Abs2by" + Integer.toString(i)) + "\t");
			historyData.append(itn.getStringExtra("St2Abs3by" + Integer.toString(i)) + "\t");
		}
	}
	
	public void DataArrayPhotoTemp() {
		
		DecimalFormat dfm = new DecimalFormat("0000");
		
		overallData.delete(0, overallData.capacity());
		historyData.delete(0, historyData.capacity());
		
		itn = getIntent();
		
		overallData.append(itn.getStringExtra("Year"));
		overallData.append(itn.getStringExtra("Month"));
		overallData.append(itn.getStringExtra("Day"));
		overallData.append(itn.getStringExtra("AmPm"));
		overallData.append(itn.getStringExtra("Hour"));
		overallData.append(itn.getStringExtra("Minute"));
		overallData.append(itn.getStringExtra("Hba1cPct"));			
					
		for(int i = 0; i < 5; i++) {
			
			historyData.append(itn.getStringExtra("CellBlockTemp1" + Integer.toString(i)) + "\t");
			historyData.append(itn.getStringExtra("CellBlockTemp2" + Integer.toString(i)) + "\t");
			historyData.append(itn.getStringExtra("CellBlockTemp3" + Integer.toString(i)) + "\t");
			historyData.append(itn.getStringExtra("AmbientTemp1" + Integer.toString(i)) + "\t");
			historyData.append(itn.getStringExtra("AmbientTemp2" + Integer.toString(i)) + "\t");
			historyData.append(itn.getStringExtra("AmbientTemp3" + Integer.toString(i)) + "\t");

		}
		
		historyData.append(itn.getIntExtra("RunMin", 0) + "\t");
		historyData.append(itn.getIntExtra("RunSec", 0) + "\t");
		historyData.append(itn.getStringExtra("BlankVal0") + "\t");
		historyData.append(itn.getStringExtra("BlankVal1") + "\t");
		historyData.append(itn.getStringExtra("BlankVal2") + "\t");
		historyData.append(itn.getStringExtra("BlankVal3") + "\t");
		
		for(int i = 0; i < 3; i++) {
			
			historyData.append(itn.getStringExtra("St1Abs1by" + Integer.toString(i)) + "\t");
			historyData.append(itn.getStringExtra("St1Abs2by" + Integer.toString(i)) + "\t");
			historyData.append(itn.getStringExtra("St1Abs3by" + Integer.toString(i)) + "\t");
			historyData.append(itn.getStringExtra("St2Abs1by" + Integer.toString(i)) + "\t");
			historyData.append(itn.getStringExtra("St2Abs2by" + Integer.toString(i)) + "\t");
			historyData.append(itn.getStringExtra("St2Abs3by" + Integer.toString(i)) + "\t");
		}
	}
	
	public void DataArrayTemp() {
		
		overallData.delete(0, overallData.capacity());
		
		itn = getIntent();
		
		overallData.append("\r\n" + itn.getStringExtra("Test Time"));
//		overallData.append("Chamber Temp Data" + "\r\n");
//
//		for(int i = 0; i < 256; i ++) {
//		
//			overallData.append(itn.getStringExtra("Chamber Temp Data" + Integer.toString(i)) + "\n");
//		}

		overallData.append("\r\n" +"Inside Temp Data");

		for(int i = 0; i < TemperatureTestActivity.TEMP_DATA_SIZE; i ++) {
		
			overallData.append("\r\n" + itn.getStringExtra("Inside Temp Data" + Integer.toString(i)));
		}
	}
	
	public void DataArrayPhoto() {
		
		overallData.delete(0, overallData.capacity());
		
		itn = getIntent();
		
		overallData.append(itn.getStringExtra("Test Time") + "\r\n");
		overallData.append("535nm Light Intensity" + "\r\n");

		for(int i = 0; i < 256; i ++) {
		
			overallData.append(itn.getStringExtra("Photo Data" + Integer.toString(i)) + "\t");
		}
	}
	
	public void DataArrayPhotoAbs() {
		
		overallData.delete(0, overallData.capacity());
		
		itn = getIntent();
		
		overallData.append(itn.getStringExtra("Year"));
		overallData.append(itn.getStringExtra("Month"));
		overallData.append(itn.getStringExtra("Day"));
		overallData.append(itn.getStringExtra("AmPm"));
		overallData.append(itn.getStringExtra("Hour"));
		overallData.append(itn.getStringExtra("Minute"));
		overallData.append(itn.getStringExtra("Hba1cPct") + "\t");			
			
		overallData.append(itn.getStringExtra("BlankVal0") + "\t");
		overallData.append(itn.getStringExtra("BlankVal1") + "\t");
		overallData.append(itn.getStringExtra("BlankVal2") + "\t");
		overallData.append(itn.getStringExtra("BlankVal3") + "\t");
		
		for(int i = 0; i < 3; i++) {
			
			overallData.append(itn.getStringExtra("Step1Value1" + Integer.toString(i)) + "\t");
			overallData.append(itn.getStringExtra("Step1Value2" + Integer.toString(i)) + "\t");
			overallData.append(itn.getStringExtra("Step1Value3" + Integer.toString(i)) + "\t");
			overallData.append(itn.getStringExtra("Step2Value1" + Integer.toString(i)) + "\t");
			overallData.append(itn.getStringExtra("Step2Value2" + Integer.toString(i)) + "\t");
			overallData.append(itn.getStringExtra("Step2Value3" + Integer.toString(i)) + "\t");
		
			overallData.append(itn.getStringExtra("St1Abs1by" + Integer.toString(i)) + "\t");
			overallData.append(itn.getStringExtra("St1Abs2by" + Integer.toString(i)) + "\t");
			overallData.append(itn.getStringExtra("St1Abs3by" + Integer.toString(i)) + "\t");
			overallData.append(itn.getStringExtra("St2Abs1by" + Integer.toString(i)) + "\t");
			overallData.append(itn.getStringExtra("St2Abs2by" + Integer.toString(i)) + "\t");
			overallData.append(itn.getStringExtra("St2Abs3by" + Integer.toString(i)) + "\t");
		}
	}
	
	public void WhichIntent() { // Activity conversion
	
		
		switch(whichState) {
		
		case 6	:
			Intent RemoveIntent = new Intent(getApplicationContext(), RemoveActivity.class);
			startActivity(RemoveIntent);
			break;
			
		case 8	:
			Intent TempIntent = new Intent(getApplicationContext(), TemperatureTestActivity.class);
			startActivity(TempIntent);
			break;
			
		case 9	:
			Intent PhotoIntent = new Intent(getApplicationContext(), PhotoActivity.class);
			startActivity(PhotoIntent);
			break;
		
		case 10	:
			Intent PhotoAbsIntent = new Intent(getApplicationContext(), RemoveActivity.class);
			PhotoAbsIntent.putExtra("WhichIntent", whichState);
			startActivity(PhotoAbsIntent);
			break;
			
		default	:
			Intent RemoveIntent2 = new Intent(getApplicationContext(), RemoveActivity.class);
			RemoveIntent2.putExtra("WhichIntent", whichState);
			RemoveIntent2.putExtra("DataCnt", DataCnt);
			startActivity(RemoveIntent2);
			break;
		}
		
		finish();
	}
	
	public void finish() {
		
		super.finish();
		overridePendingTransition(R.anim.fade, R.anim.hold);
	}
}
