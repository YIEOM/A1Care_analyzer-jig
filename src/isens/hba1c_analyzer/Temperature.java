package isens.hba1c_analyzer;

import isens.hba1c_analyzer.SerialPort.CtrTarget;

import java.text.DecimalFormat;

import android.util.Log;
import android.widget.TextView;

public class Temperature extends SerialPort {

	final static String TEMPERATURE_CELLBLOCK = "VT",
						TEMPERATURE_AMBIENT   = "IA";
	
	public TextView TmpText;
	
	final static double InitTmp = (double) 30; // Celsius temperature
	
	public void TmpInit() { // Initial temperature of cell block set-up
		
		double tmpDouble;
		String tmpString;
		DecimalFormat tmpFormat;
		
		tmpDouble = InitTmp * (double) 1670.17 + (double) 25891.34;
		tmpFormat = new DecimalFormat("#####0");
		
		if(tmpFormat.format(tmpDouble).length() == 5) tmpString = "0" + tmpFormat.format(tmpDouble);
		else tmpString = tmpFormat.format(tmpDouble);
		
		BoardTx("R" + tmpString, CtrTarget.TmpSet);
		BoardMessageOutput();
	}
	
	public double CellTmpRead() { // Read current temperature of cell block
		
		double tmpRaw;
		double tmpDouble;
	
		BoardTx(TEMPERATURE_CELLBLOCK, CtrTarget.TmpCall);
		Sleep(500);
		
		tmpRaw = Double.parseDouble(BoardMessageOutput());
		tmpDouble = (tmpRaw / (double) 1670.17) - (double) 15.5;
		
		return tmpDouble;
	}
	
	public double AmbTmpRead() { // Read current ambient temperature
	
		int tmpADC;
		double tmpDouble,
			   tmpV;
		String tmpData;
		
		BoardTx(TEMPERATURE_AMBIENT, CtrTarget.AmbientTmpCall);
				
		tmpData = SensorMessageOutput();
		
		while(!tmpData.substring(1, 2).equals("T")) {
			
			tmpData = SensorMessageOutput();
		}
		
		tmpADC = Integer.parseInt(tmpData.substring(2));
		
		tmpV = ((double) 5/1024 * (tmpADC + 1));
		tmpDouble = (double) tmpV / 0.01;
		
		return tmpDouble;
	}	
}
