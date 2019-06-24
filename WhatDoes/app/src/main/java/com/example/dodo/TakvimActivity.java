package com.example.dodo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TimeFormatException;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.text.DateFormat.SHORT;

public class TakvimActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private Toolbar mToolbar;
    private Button show_calender_btn,AlarmCancel;
    private TextView alarmTime,alarmTitle;

    private  String title,date;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takvim);


        mToolbar=findViewById(R.id.does_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Takvim");

        show_calender_btn=findViewById(R.id.show_calender_btn);
        alarmTime=findViewById(R.id.alarmTime);
        alarmTitle=findViewById(R.id.alarmTitle);
        AlarmCancel=findViewById(R.id.AlarmCancel);

        Intent DoesToTakvim = getIntent();
         title=DoesToTakvim.getExtras().getString("alarmTitle");
         date=DoesToTakvim.getExtras().getString("alarmDate");

        show_calender_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment timePicker=new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(),"time picker");
            }
        });

        AlarmCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarm();
            }
        });
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        Calendar c=Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY,hourOfDay);
        c.set(Calendar.MINUTE,minute);
        c.set(Calendar.SECOND,0);
        alarmTime.setText(hourOfDay+":"+minute);

        updateTimeText(c);
        startAlarm(c);
    }

    private void updateTimeText(Calendar c) {
        String timeText = "Alarm zamanı: ";
        timeText += DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());

        alarmTime.setText(timeText);
    }

    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        alarmManager.cancel(pendingIntent);
        alarmTime.setText("Alarm durduruldu");
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (TextUtils.isEmpty(title))
        {
           alarmTitle.setText("konu");
        }
        else
        {
            alarmTitle.setText(title);
        }
    }
}
