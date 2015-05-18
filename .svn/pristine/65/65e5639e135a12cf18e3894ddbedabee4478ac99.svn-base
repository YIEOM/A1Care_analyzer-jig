package isens.hba1c_analyzer;

import java.util.Timer;
import java.util.TimerTask;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.TimerDisplay.whichClock;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
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

public class ActionActivity extends Activity {
	
	private GpioPort ActionGpio;
	private SerialPort ActionSerial;
	
	private Handler handler = new Handler();
	private Timer timer;
	
	private static TextView TimeText;
	private AnimationDrawable scanAni;
	private ImageView scanImage;
		
	private RelativeLayout actionLinear;
	private View escPopupView, 
				 errorBtnPopupView;
	private PopupWindow escPopup, 
						errorBtnPopup;
	
	private Button escBtn, 
				   yesBtn, 
				   noBtn, 
				   errorBtn;
	
	public static boolean IsCorrectBarcode = false, 
						  BarcodeCheckFlag = false;	
	private static boolean ESCButtonFlag = false;
	
	public static byte CartridgeCheckFlag, 
					   DoorCheckFlag;
	
	protected void onCreate(Bundle savedInstanceState) {
			
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.action);
		
		TimeText = (TextView)findViewById(R.id.timeText);
		
		/* Esc Pop-up window */
		actionLinear = (RelativeLayout)findViewById(R.id.actionlinear);
		escPopupView = View.inflate(getApplicationContext(), R.layout.escpopup, null);
		escPopup = new PopupWindow(escPopupView, 504, 174, true);
		
		/* Error Pop-up window */
		errorBtnPopupView = View.inflate(getApplicationContext(), R.layout.errorbtnpopup, null);
		errorBtnPopup = new PopupWindow(errorBtnPopupView, 504, 174, true);
		
		/* Esc Pop-up window activation */
		escBtn = (Button)findViewById(R.id.escicon);
		escBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				escBtn.setEnabled(false);
				noBtn.setEnabled(true);
				
				escPopup.showAtLocation(actionLinear, Gravity.CENTER, 0, 0);
				escPopup.setAnimationStyle(0);
				escPopup.showAsDropDown(escBtn);
			}
		});
		
		/* HOME activity activation */
		yesBtn = (Button)escPopupView.findViewById(R.id.yesbtn);
		yesBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				yesBtn.setEnabled(false);
				
				if(GpioPort.DoorActState) WhichIntent(TargetIntent.Remove);
				else WhichIntent(TargetIntent.Home);
			}
		});
		
		/* Esc Pop-up window close */
		noBtn = (Button)escPopupView.findViewById(R.id.nobtn);
		noBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				noBtn.setEnabled(false);
				escBtn.setEnabled(true);
				
				escPopup.dismiss();
			}
		});
		
		/* Error Pop-up Close */
		errorBtn = (Button)errorBtnPopupView.findViewById(R.id.errorbtn);
		errorBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				errorBtn.setEnabled(false);
				
				errorBtnPopup.dismiss();
				errorBtn.setEnabled(true);
				
				ActionInit();
			}
		});
		
		ActionInit();
	}
	
	public void ActionInit() {
		
		TimerDisplay.timerState = whichClock.ActionClock;		
		CurrTimeDisplay();
		
		IsCorrectBarcode = false;	
		BarcodeCheckFlag = false;
		
		ActionSerial = new SerialPort();
		ActionSerial.BarcodeSerialInit();
		ActionSerial.BarcodeRxStart();
		
		ESCButtonFlag = false;
		
		actionLinear.post(new Runnable() {
	        public void run() {
	        	
	        	BarcodeScan BarcodeScanObj = new BarcodeScan();
	        	BarcodeScanObj.start();
	        }
		});
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
	
	public class BarcodeScan extends Thread {
		
		public void run () {
			
			/* Barcode scan action */
			BarcodeAniStart();
			
//			Log.w("BarcodeScan", "BarcodeCheckFlag : " + BarcodeCheckFlag);
			SerialPort.BarcodeBufIndex = 0;
			
			Trigger();
			
			while(!BarcodeCheckFlag) {
				
				if(ESCButtonFlag) break; // exiting if esc button is pressed
			}

			timer.cancel();
			
			if(!ESCButtonFlag) { 
				
				if(IsCorrectBarcode) {
					
					CartridgeInsert CartridgeInsertObj = new CartridgeInsert();
					CartridgeInsertObj.start();
					
				} else { // to test
				
					ErrorPopup(); // to test
				} // to test	
			}	
		}
	}
		
	public class CartridgeInsert extends Thread {
		
		public void run () {

			/* Cartridge insertion action */
			CartridgeAniStart();
			
			GpioPort.CartridgeActState = true;
			
			while(ActionActivity.CartridgeCheckFlag != 1) { // to test
			
				if(ESCButtonFlag) break;
			}
			
			if(!ESCButtonFlag) {  // to test
				
				ActionActivity.DoorCheckFlag = 0;
				
				CollectorCover CollectorCoverObj = new CollectorCover();
				CollectorCoverObj.start();
			}
		}
	}
	
	public class CollectorCover extends Thread {
		
		public void run() {
			
			/* Cover close action */
			CoverAniStart();
			
			GpioPort.DoorActState = true;
			
			while((ActionActivity.DoorCheckFlag != 1) | (ActionActivity.CartridgeCheckFlag != 1)) {
				
				if(ESCButtonFlag) break;
			}
			
			if(!ESCButtonFlag) {
				
				WhichIntent(TargetIntent.Run);
			}
		}
	}
	
	public void BarcodeAniStart() { // Barcode scan animation start
		
		scanImage = (ImageView)findViewById(R.id.userAct1);
		scanAni = (AnimationDrawable)scanImage.getBackground();
		
    	actionLinear.post(new Runnable() {
			public void run() {
				
				scanAni.start();
            }
   		});
	}
	
	public void CartridgeAniStart() { // Cartridge insertion animation start
		
    	actionLinear.post(new Runnable() {
			public void run() {
        
				actionLinear.setBackgroundResource(R.drawable.ani_3steps_bg);
        		scanImage.setBackgroundResource(0);
				scanAni.stop();
			 }
		});
	}
	
	public void CoverAniStart() { // Cover close animation start
		
		scanImage = (ImageView)findViewById(R.id.userAct4);
		
    	actionLinear.post(new Runnable() {
			public void run() {
				
				actionLinear.setBackgroundResource(R.drawable.ani_close_bg);
				scanImage.setBackgroundResource(R.drawable.useract4);
				scanAni = (AnimationDrawable)scanImage.getBackground();
//				            	Log.w("CoverAniStart", "run");
            	scanAni.start();
			 }
		});
	}
		
	public void Trigger() {
		
		BarcodeScan();
		
		TimerTask triggerTimer = new TimerTask() {
			
			int cnt = 0;
			
			public void run() {
				Runnable updater = new Runnable() {
					public void run() {
						
						if (cnt++ == 5) {
							
							cnt = 0;
							BarcodeScan();
						}
					}
				};
				
				handler.post(updater);		
			}
		};
		
		timer = new Timer();
		timer.schedule(triggerTimer, 0, 1000); // Timer period : 1sec
	}

	public void BarcodeScan() {
		
		ActionGpio = new GpioPort();
		ActionGpio.TriggerLow();
		SerialPort.Sleep(100);
		ActionGpio.TriggerHigh();
	}
	
	public void ErrorPopup() {
		
		new Thread(new Runnable() {
		    public void run() {    
		        runOnUiThread(new Runnable(){
		            public void run(){

		            	scanAni.stop();
		            	
		            	errorBtnPopup.showAtLocation(actionLinear, Gravity.CENTER, 0, 0);
		        		errorBtnPopup.setAnimationStyle(0);
		        							
		        		TextView errorText = (TextView) errorBtnPopup.getContentView().findViewById(R.id.errortext);
		        		errorText.setText("E031");
		        	}
		        });
		    }
		}).start();
		
	}
	
	public void WhichIntent(TargetIntent Itn) { // Activity conversion
			
		SerialPort.bBarcodeRxThread.interrupt();
		GpioPort.CartridgeActState = false;
		GpioPort.DoorActState = false;
		
		ActionGpio = new GpioPort();
		ActionGpio.TriggerHigh();
		
		switch(Itn) {
		
		case Run	:	
			Intent RunIntent = new Intent(getApplicationContext(), RunActivity.class);
			startActivity(RunIntent);
			break;
						
		case Home	:	
			ESCButtonFlag = true;
			
			escPopup.dismiss(); // Popup window close
			
			Intent HomeIntent = new Intent(getApplicationContext(), HomeActivity.class);
			startActivity(HomeIntent);
			break;
				
		case Remove	:	
			ESCButtonFlag = true;
			
			escPopup.dismiss(); // Popup window close
			
			Intent RemoveIntent = new Intent(getApplicationContext(), RemoveActivity.class);
			RemoveIntent.putExtra("WhichIntent", (int) RemoveActivity.COVER_ACTION_ESC);
			startActivity(RemoveIntent);
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
