package isens.hba1c_analyzer;

import java.text.DecimalFormat;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class Barcode {

	final static double a1ref  = 0.01, 
						b1ref  = -0.05, 
						a21ref = 0.05, 
						b21ref = 0.015, 
						a22ref = 0.042, 
						b22ref = 0.015;
	
	private GpioPort BarcodeGpio;
	
	private String WhichTest[] = {"OT", "CH", "CA", "HS", "HT", "AS", "AT"};
	
	public static String RefNum;
	public static double a1, 
						 b1, 
						 a21, 
						 b21, 
						 a22, 
						 b22, 
						 L, 
						 H;
	
	public void BarcodeCheck(StringBuffer buffer) { // Check a barcode data
		
		int len, 
			test, 
			yearTens, 
			yearUnits, 
			week, 
			day, 
			locate, 
			check, 
			sum; 		
		
		len = buffer.length();
		
		if(len == 18) { // Check length of barcode data

			try {
				
				DecimalFormat dfm = new DecimalFormat("00");
				
				/* Classification for each digit barcode */
				test       = (int) buffer.charAt(0) - 64;
				yearTens   = (int) buffer.charAt(1) - 48;
				yearUnits  = (int) buffer.charAt(2) - 48;
				week       = (int) buffer.charAt(3) - 64;
				day        = (int) buffer.charAt(4) - 64;
				locate     = (int) buffer.charAt(5) - 64;
				check      = (int) buffer.charAt(15) - 48;
				
				RefNum = WhichTest[test-1] + dfm.format(yearTens) + dfm.format(yearUnits) + dfm.format(week) + dfm.format(day);
				
				a1  = 0.0001 * (0.5 * ((int) buffer.charAt(6) - 42) - 20) + a1ref; // tHb slope
				b1  = 0.0001 * (20 * ((int) buffer.charAt(7) - 42) - 800) + b1ref; // tHb y-intercept
				a21 = 0.00025 * (((int) buffer.charAt(8) - 42) - 40) + a21ref; // A1c-Low slope
				b21 = 0.001 * (((int) buffer.charAt(9) - 42) - 40) + b21ref; // A1c-Low intercept
				a22 = 0.00025 * (((int) buffer.charAt(10) - 42) - 40) + a22ref; // A1c-High slope
				b22 = 0.001 * (((int) buffer.charAt(11) - 42) - 40) + b22ref; // A1c-High intercept
				L   = 0.2 * (0.5 * ((int) buffer.charAt(12) - 42) - 5) + 5; // A1c-Low %
				H   = 0.2 * (0.5 * ((int) buffer.charAt(13) - 42) - 5) + 9; // A1c-High %
				
				sum = ( test + yearTens + yearUnits + week + day + locate ) % 10; // Checksum bit
//				Log.w("Barcode", "test : " + test);
//				Log.w("Barcode", "yearTens : " + yearTens);
//				Log.w("Barcode", "yearUnits : " + yearUnits);
//				Log.w("Barcode", "week : " + week);
//				Log.w("Barcode", "day : " + day);
//				Log.w("Barcode", "locate : " + locate);
//				Log.w("Barcode", "Check Sum : " + sum);
				if( sum == check ) { // Whether or not the correct barcode code
	
//					Log.w("Barcode", "Correct Data : " + buffer);
					BarcodeStop(true);
						
				} else {
					
					BarcodeStop(false);	
				}
			
			} catch (NumberFormatException e) {
				
				e.printStackTrace();
			}
		} else {
			
			BarcodeStop(false);
		}
	}
	
	public void BarcodeStop(boolean state) { // Turn off barcode module
		
		SerialPort.bBarcodeRxThread.interrupt();
		
		if(state) {
			
			ActionActivity.IsCorrectBarcode = true;
			ActionActivity.BarcodeCheckFlag = true;
			
		} else {
//			Log.w("BarcodeStop", "BarcodeCheck : " + ActionActivity.BarcodeCheckFlag);
			ActionActivity.IsCorrectBarcode = false;
			ActionActivity.BarcodeCheckFlag = true;
		}
	}
}
