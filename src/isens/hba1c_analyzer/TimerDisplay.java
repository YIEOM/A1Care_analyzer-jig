package isens.hba1c_analyzer;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class TimerDisplay {
	
	public Handler handler = new Handler();
	public static TimerTask OneHundredmsPeriod;
	
	public enum whichClock 	{HomeClock, TestClock, RunClock, ActionClock, ResultClock, 
							 MemoryClock, BlankClock, SettingClock, SystemSettingClock, 
							 RemoveClock, PatientClock, ControlClock, ExportClock,
							 ImageClock, DataSettingClock, MaintenanceClock, DateClock, 
							 TimeClock, DisplayClock, HISClock, HISSettingClock, AdjustmentClock, 
							 SoundClock, CalibrationClock, LanguageClock, SystemCheckClock, 
							 CorrelationClock, TemperatureClock, LabViewClock, PhotoClock}
	public static whichClock timerState;

	final static String rTime[] = new String[8];
	
	private Timer timer;
	
	private RunActivity TimerRun;	
	private HomeActivity TimerHome;
	private BlankActivity TimerBlank;
	private ActionActivity TimerAction;
	private ResultActivity TimerResult;
	private RemoveActivity TimerRemove;
	private MemoryActivity TimerMemory;
	private ExportActivity TimerExport;
	private SettingActivity	TimerSetting;
	private PatientTestActivity TimerPatient;
	private ControlTestActivity TimerControl;
	private SystemSettingActivity TimerSystemSetting;
	private DateActivity TimerDate;
	private DisplayActivity TimerDisplay;
	private HISActivity	TimerHIS;
	private HISSettingActivity TimerHISSetting;
	private AdjustmentFactorActivity TimerAdjustment;
	private SoundActivity TimerSound;
	private CalibrationActivity TimerCalibration;
	private LanguageActivity TimerLanguage;
	private GpioPort TimerGpio;
	private CorrelationFactorActivity TimerCorrelation;
	private TemperatureActivity	TimerTemperature;
	private LabViewActivity TimerLabView;
	private PhotoActivity TimerPhoto;
	
	public static int cnt = 0;
	
	public void TimerInit() {
		
		OneHundredmsPeriod = new TimerTask() {
			
//			int cnt = 0;
			
			public void run() {
				Runnable updater = new Runnable() {
					public void run() {
						
						if(cnt++ == 200) cnt = 0;
						
						if((cnt % 10) == 0) { // One second period
						
							TimerGpio = new GpioPort();
							TimerGpio.CartridgeSensorScan();
							TimerGpio.DoorSensorScan();
						
							RealTime();
							
							if(Integer.parseInt(rTime[6]) == 0) { // Whenever 00 second
											
								ClockDecision();
							}
							
						} else if((cnt % 5) == 0) {
							
							TimerGpio = new GpioPort();
							TimerGpio.CartridgeSensorScan();
							TimerGpio.DoorSensorScan();
						}
					}
				};
				
				handler.post(updater);		
			}
		};
		
		timer = new Timer();
		timer.schedule(OneHundredmsPeriod, 0, 100); // Timer period : 100msec
	}
	
	public void RealTime() { // Get current date and time
	
		Calendar c = Calendar.getInstance();
		
		DecimalFormat dfm = new DecimalFormat("00");
		
		rTime[0] = Integer.toString(c.get(Calendar.YEAR));
		rTime[1] = dfm.format(c.get(Calendar.MONTH) + 1);
		rTime[2] = dfm.format(c.get(Calendar.DAY_OF_MONTH));
		
		if(c.get(Calendar.AM_PM) == 0) {

			rTime[3] = "AM";			
		} else {

			rTime[3] = "PM";			
		}
		
		if(c.get(Calendar.HOUR) != 0) {
			
			rTime[4] = Integer.toString(c.get(Calendar.HOUR));
		} else {
		
			rTime[4] = "12";
		}
		rTime[5] = dfm.format(c.get(Calendar.MINUTE));		
		rTime[6] = dfm.format(c.get(Calendar.SECOND));
		rTime[7] = dfm.format(c.get(Calendar.HOUR_OF_DAY));		
	}
	
	public void ClockDecision() { // Whenever activity change, Corresponding clock action
		
		switch(timerState){
		
		case HomeClock			:	
			TimerHome = new HomeActivity();
			TimerHome.CurrTimeDisplay();
			break;
									
		case ActionClock		:	
			TimerAction = new ActionActivity();
			TimerAction.CurrTimeDisplay();
			break;
			
		case RunClock			:	
			TimerRun = new RunActivity();
			TimerRun.CurrTimeDisplay();
			break;
			
		case ResultClock		:	
			TimerResult = new ResultActivity();
			TimerResult.CurrTimeDisplay();
			break;
			
		case MemoryClock		:	
			TimerMemory = new MemoryActivity();
			TimerMemory.CurrTimeDisplay();
			break;
								
		case BlankClock			:	
			TimerBlank = new BlankActivity();
			TimerBlank.CurrTimeDisplay();
			break;
								
		case SettingClock		:	
			TimerSetting = new SettingActivity();
			TimerSetting.CurrTimeDisplay();
			break;
								
		case SystemSettingClock	:	
			TimerSystemSetting = new SystemSettingActivity();
			TimerSystemSetting.CurrTimeDisplay();
			break;
							
		case RemoveClock		:
			TimerRemove = new RemoveActivity();
			TimerRemove.CurrTimeDisplay();
			break;
			
		case PatientClock		:
			TimerPatient = new PatientTestActivity();
			TimerPatient.CurrTimeDisplay();
			break;
			
		case ControlClock		:
			TimerControl = new ControlTestActivity();
			TimerControl.CurrTimeDisplay();
			break;
			
		case ExportClock		:
			TimerExport = new ExportActivity();
			TimerExport.CurrTimeDisplay();
			break;
			
		case DateClock			:
			TimerDate = new DateActivity();
			TimerDate.CurrTimeDisplay();
			break;

		case DisplayClock		:
			TimerDisplay = new DisplayActivity();
			TimerDisplay.CurrTimeDisplay();
			break;
			
		case HISSettingClock	:
			TimerHISSetting = new HISSettingActivity();
			TimerHISSetting.CurrTimeDisplay();
			break;
			
		case HISClock			:
			TimerHIS = new HISActivity();
			TimerHIS.CurrTimeDisplay();
			break;
		
		case AdjustmentClock	:
			TimerAdjustment = new AdjustmentFactorActivity();
			TimerAdjustment.CurrTimeDisplay();
			break;

		case SoundClock			:
			TimerSound = new SoundActivity();
			TimerSound.CurrTimeDisplay();
			break;
			
		case CalibrationClock	:
			TimerCalibration = new CalibrationActivity();
			TimerCalibration.CurrTimeDisplay();
			break;
		
		case LanguageClock		:
			TimerLanguage = new LanguageActivity();
			TimerLanguage.CurrTimeDisplay();
			break;
		
		case CorrelationClock	:
			TimerCorrelation = new CorrelationFactorActivity();
			TimerCorrelation.CurrTimeDisplay();
			break;
			
		case TemperatureClock	:
			TimerTemperature = new TemperatureActivity();
			TimerTemperature.CurrTimeDisplay();
			break;
			
		case LabViewClock		:
			TimerLabView = new LabViewActivity();
			TimerLabView.CurrTimeDisplay();
			break;
			
		case PhotoClock		:
			TimerPhoto = new PhotoActivity();
			TimerPhoto.CurrTimeDisplay();
			break;
			
		default					:	
			break;		
		}
	}
}
