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

public class TempFileSaveActivity extends Activity {

	public DataStorage TempSaveData;
	
	private TextView Text;
	private Intent itn;
	
	private StringBuffer cellBlockTemp = new StringBuffer(),
						 ambientTemp = new StringBuffer();
	private String time = new String();
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.tempfilesave);
		
		Text = (TextView) findViewById(R.id.text);		
		
		DataInit();
	}
	
	public void DataInit() {
		
		cellBlockTemp.delete(0, cellBlockTemp.capacity());
		ambientTemp.delete(0, ambientTemp.capacity());
		
		TempSaveData = new DataStorage();
		
		itn = getIntent();
		for(int i = 0; i < 256; i++) {
		
			cellBlockTemp.append(itn.getStringExtra("Cell Block Temp Data" + Integer.toString(i)) + "\t");
			ambientTemp.append(itn.getStringExtra("Ambient Temp Data" + Integer.toString(i)) + "\t");
		}
		
		time = itn.getStringExtra("Test Time"); 
		
		TempSaveData.TemperatureSave(time, cellBlockTemp, ambientTemp);
		
		WhichIntent();
	}
	
	public void WhichIntent() { // Activity conversion
	
		Intent TemperatureIntent = new Intent(getApplicationContext(), TemperatureActivity.class);
		startActivity(TemperatureIntent);
		finish();
	}
	
	public void finish() {
		
		super.finish();
		overridePendingTransition(R.anim.fade, R.anim.hold);
	}
}
