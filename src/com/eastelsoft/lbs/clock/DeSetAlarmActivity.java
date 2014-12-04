package com.eastelsoft.lbs.clock;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.eastelsoft.lbs.BaifangAddActivity;
import com.eastelsoft.lbs.InfoActivity;
import com.eastelsoft.lbs.InfoAddActivity;
import com.eastelsoft.lbs.R;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class DeSetAlarmActivity extends Activity implements
		TimePickerDialog.OnTimeSetListener {

	private Button btBack;
	private Button btAddInfo;

	private EditText etTime;
	private EditText etTitle;
	private EditText info_date;
	

	private CheckBox week1;
	private CheckBox week2;
	private CheckBox week3;
	private CheckBox week4;
	private CheckBox week5;
	private CheckBox week6;
	private CheckBox week7;

	private Alarm.DaysOfWeek mDaysOfWeek = new Alarm.DaysOfWeek(0);
	private Alarm.DaysOfWeek mNewDaysOfWeek = new Alarm.DaysOfWeek(0);
	String[] weekdays = new DateFormatSymbols().getWeekdays();
	String[] values = new String[] { weekdays[Calendar.MONDAY],
			weekdays[Calendar.TUESDAY], weekdays[Calendar.WEDNESDAY],
			weekdays[Calendar.THURSDAY], weekdays[Calendar.FRIDAY],
			weekdays[Calendar.SATURDAY], weekdays[Calendar.SUNDAY], };

	private int mId;
	private int mHour;
	private int mMinutes;
	private String theday;
	private Alarm mOriginalAlarm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_clocksetting);
		btBack = (Button) findViewById(R.id.btBack);
		btBack.setOnClickListener(new OnBtBackClickListenerImpl());
		btAddInfo = (Button) findViewById(R.id.btAddInfo);
		btAddInfo.setOnClickListener(new OnBtAddInfoClickListenerImpl());
		// 日期
		info_date = (EditText) findViewById(R.id.info_date);
		info_date.setOnTouchListener(new OnBtDateTouchListenerImpl());
		info_date.setOnClickListener(new OnBtDateClickListenerImpl());
		Calendar tt = Calendar.getInstance();
		tt.setTimeInMillis(System.currentTimeMillis());
        tt.add(Calendar.DAY_OF_YEAR, 1);
        theday = (String) DateFormat.format("yyyy-MM-dd", tt);
        info_date.setText(theday);

		//时间
		etTime = (EditText) findViewById(R.id.info_time);
		etTime.setOnTouchListener(new OnBtTimeTouchListenerImpl());
		etTime.setOnClickListener(new OnBtTimeClickListenerImpl());
		
		// 内容
		etTitle = (EditText) findViewById(R.id.info_title);

		week1 = (CheckBox) this.findViewById(R.id.week1);
		week2 = (CheckBox) this.findViewById(R.id.week2);
		week3 = (CheckBox) this.findViewById(R.id.week3);
		week4 = (CheckBox) this.findViewById(R.id.week4);
		week5 = (CheckBox) this.findViewById(R.id.week5);
		week6 = (CheckBox) this.findViewById(R.id.week6);
		week7 = (CheckBox) this.findViewById(R.id.week7);

		// new_contract1.isChecked()
		Alarm alarm = null;
		alarm = new Alarm(theday);
		mOriginalAlarm = alarm;
		updatePrefs(mOriginalAlarm);

	}

	private void updatePrefs(Alarm alarm) {
		mId = alarm.id;
		// mEnabledPref.setChecked(alarm.enabled);
		// mLabel.setText(alarm.label);
		// mLabel.setSummary(alarm.label);
		mHour = alarm.hour;
		mMinutes = alarm.minutes;
		theday = alarm.theday;
		
		updateTime();
	}

	private void updateTime() {
		etTime.setText(Alarms.formatTime(this, mHour, mMinutes, mNewDaysOfWeek,theday));
	}

	private class OnBtTimeTouchListenerImpl implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (v.getId()) {
			case R.id.info_time:
				// actionAlertDialog();
				etTime.requestFocus();
				break;
			default:
				break;
			}
			return false;
		}

	}

	private class OnBtTimeClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			showTimePicker();

		}
	}
	
	private class OnBtDateTouchListenerImpl implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (v.getId()) {
			case R.id.info_date:
				// actionAlertDialog();
				info_date.requestFocus();
				break;
			default:
				break;
			}
			return false;
		}

	}

	private class OnBtDateClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			Date sm = Alarms.strToDateTime(theday);
			// int day =sm.getDay();
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(System.currentTimeMillis());
			cal.setTime(sm);
			try {
				new DatePickerDialog(DeSetAlarmActivity.this, d,
						cal.get(Calendar.YEAR),
						cal.get(Calendar.MONTH),
						cal.get(Calendar.DAY_OF_MONTH)).show();
			} catch (Exception e) {
				
			}
			

		}
	}
	// 当点击DatePickerDialog控件的设置按钮时，调用该方法
		DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				// TODO Auto-generated method stub
				Calendar dateAndTime = Calendar.getInstance();
				dateAndTime.setTimeInMillis(System.currentTimeMillis());
				dateAndTime.set(Calendar.YEAR, year);
				dateAndTime.set(Calendar.MONTH, monthOfYear);
				dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				theday = (String) DateFormat.format("yyyy-MM-dd", dateAndTime);
				info_date.setText(theday);

			}

		};
	
	
	
	

	private void showTimePicker() {
		new TimePickerDialog(this, this, mHour, mMinutes,
				DateFormat.is24HourFormat(this)).show();
	}

	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

		mHour = hourOfDay;
		mMinutes = minute;
		updateTime();
	}

	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				DeSetAlarmActivity.this.finish();
			} catch (Exception e) {

			}
		}
	}

	private class OnBtAddInfoClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			if (week1.isChecked()) {
				mNewDaysOfWeek.set(0, true);
			}
			if (week2.isChecked()) {
				mNewDaysOfWeek.set(1, true);
			}
			if (week3.isChecked()) {
				mNewDaysOfWeek.set(2, true);
			}
			if (week4.isChecked()) {
				mNewDaysOfWeek.set(3, true);
			}
			if (week5.isChecked()) {
				mNewDaysOfWeek.set(4, true);
			}
			if (week6.isChecked()) {
				mNewDaysOfWeek.set(5, true);
			}
			if (week7.isChecked()) {
				mNewDaysOfWeek.set(6, true);
			}
			saveAlarm();
			popAlarmSetToast(DeSetAlarmActivity.this, mHour, mMinutes,
					mNewDaysOfWeek,theday);
			finish();

		}
	}

	static void popAlarmSetToast(Context context, int hour, int minute,
			Alarm.DaysOfWeek daysOfWeek,String theday) {
		popAlarmSetToast(context,
				Alarms.calculateAlarm(hour, minute, daysOfWeek,theday)
						.getTimeInMillis());
	}
	static void popAlarmSetToast(Context context, long timeInMillis) {
        String toastText = formatToast(context, timeInMillis);
        Toast toast = Toast.makeText(context, toastText, Toast.LENGTH_LONG);
        ToastMaster.setToast(toast);
        toast.show();
    }
	 /**
     * format "Alarm set for 2 days 7 hours and 53 minutes from
     * now"
     */
    static String formatToast(Context context, long timeInMillis) {
        long delta = timeInMillis - System.currentTimeMillis();
        long hours = delta / (1000 * 60 * 60);
        long minutes = delta / (1000 * 60) % 60;
        long days = hours / 24;
        hours = hours % 24;

        String daySeq = (days == 0) ? "" :
                (days == 1) ? context.getString(R.string.day) :
                context.getString(R.string.days, Long.toString(days));

        String minSeq = (minutes == 0) ? "" :
                (minutes == 1) ? context.getString(R.string.minute) :
                context.getString(R.string.minutes, Long.toString(minutes));

        String hourSeq = (hours == 0) ? "" :
                (hours == 1) ? context.getString(R.string.hour) :
                context.getString(R.string.hours, Long.toString(hours));

        boolean dispDays = days > 0;
        boolean dispHour = hours > 0;
        boolean dispMinute = minutes > 0;

        int index = (dispDays ? 1 : 0) |
                    (dispHour ? 2 : 0) |
                    (dispMinute ? 4 : 0);

        String[] formats = context.getResources().getStringArray(R.array.alarm_set);
        return String.format(formats[index], daySeq, hourSeq, minSeq);
    }


	private long saveAlarm() {
		Alarm alarm = new Alarm();
		alarm.id = mId;
		alarm.enabled = true;
		alarm.hour = mHour;
		alarm.minutes = mMinutes;
		alarm.daysOfWeek = mNewDaysOfWeek;
		alarm.vibrate = true;
		alarm.label = etTitle.getText().toString();
		alarm.alert = mOriginalAlarm.alert;
		alarm.theday = theday;

		long time;

		time = Alarms.addAlarm(this, alarm);

		mId = alarm.id;

		return time;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}