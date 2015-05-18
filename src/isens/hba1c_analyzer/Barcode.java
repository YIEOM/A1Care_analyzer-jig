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
	
	private String WhichTest[] = {"OT", "CH", "CA", "HS", "HT", "AS", "AT"};
	private float refScale[] = {1f, 1f, 1f, 1f, 0.6f, 1.4f, 1f};
	
	public static String RefNum;
	public static double a1, 
						 b1, 
						 a21, 
						 b21, 
						 a22, 
						 b22, 
						 L, 
						 H;
	
	public static double Sm, Im, Ss, Is;
	
	public void BarcodeCheck(StringBuffer buffer) { // Check a barcode data
		
		int len, 
			test, 
			yearTens, 
			yearUnits, 
			week, 
			day, 
			locate, 
			check, 
			sum,
			year,
			month,
			line;
		
		float scale;
		
		len = buffer.length();
		
		if(len == 18) { // Check length of barcode data

			try {
				
				DecimalFormat dfm = new DecimalFormat("00");

				if(HomeActivity.BARCODE_SYSTEM == HomeActivity.BARCODE_v2_0) {
				
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

				} else if(HomeActivity.BARCODE_SYSTEM == HomeActivity.BARCODE_v4_0) {
				
				/* Classification for each digit barcode */
				test   = (int) buffer.charAt(0) - 64;
				scale = refScale[test - 1];
				year   = (int) buffer.charAt(1) - 64;
				if(year > 26) year -= 6;
				month  = (int) buffer.charAt(2) - 64;
				day    = (int) buffer.charAt(3) - 64;
				if(day > 26) day -= 6;
				line   = (int) buffer.charAt(4) - 64;
				locate = (int) buffer.charAt(5) - 42;
				check  = (int) buffer.charAt(15) - 48;
				
				RefNum = buffer.substring(0, 5);
				
//				a1  = 0.0001 * (0.5 * ((int) buffer.charAt(6) - 42) - 20) + scale * a1ref; // tHb slope
//				b1  = 0.0001 * (20 * ((int) buffer.charAt(7) - 42) - 800) + scale * b1ref; // tHb y-intercept
//				a21 = 0.00025 * (((int) buffer.charAt(8) - 42) - 40) + scale * a21ref; // A1c-Low slope
//				b21 = 0.001 * (((int) buffer.charAt(9) - 42) - 40) + scale * b21ref; // A1c-Low intercept
//				a22 = 0.00025 * (((int) buffer.charAt(10) - 42) - 40) + scale * a22ref; // A1c-High slope
//				b22 = 0.001 * (((int) buffer.charAt(11) - 42) - 40) + scale * b22ref; // A1c-High intercept
//				L   = 0.2 * (0.5 * ((int) buffer.charAt(12) - 42) - 5) + 5; // A1c-Low %
//				H   = 0.2 * (0.5 * ((int) buffer.charAt(13) - 42) - 5) + 9; // A1c-High %

				Sm = 0.0237 * ((int) buffer.charAt(6) - 43) + 0.1;
				Im = 0.158 * ((int) buffer.charAt(7) - 43) - 6;
				Ss = 0.0003*((int) buffer.charAt(8) - 43);
				Is = 0.002*((int) buffer.charAt(9) - 43);
				
				a1 = 0.009793532;
				b1 = -0.028;
				a21 = 0.050135658;
				b21 = 0.0283;
				a22 = 0.034922675;
				b22 = 0.04333;
				L   = 4.8;
				H   = 9;
				
				sum = (test + year + month + day + line + locate) % 10; // Checksum bit
				
				}
				
				Log.w("Barcode", "test : " + test);
				Log.w("Barcode", "yearTens : " + scale);
				Log.w("Barcode", "yearUnits : " + year);
				Log.w("Barcode", "week : " + month);
				Log.w("Barcode", "day : " + day);
				Log.w("Barcode", "line : " + line);
				Log.w("Barcode", "locate : " + locate);
				Log.w("Barcode", "check : " + check);
				Log.w("Barcode", "Check Sum : " + sum);
				
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
