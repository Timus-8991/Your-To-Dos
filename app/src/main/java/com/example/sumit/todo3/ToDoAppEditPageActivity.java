package com.example.sumit.todo3;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.support.constraint.ConstraintLayout;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.example.sumit.todo3.R.drawable.abc_ratingbar_indicator_material;
import static com.example.sumit.todo3.R.drawable.happy_birthday_wallpaper;

public class ToDoAppEditPageActivity extends AppCompatActivity {

    int hour ;
    int min ;
    int year ;
    int month ;
    int dateontextview ;


    int pendingintentreqcode = 1;


    TextView dateSet;
    TextView timeSet;
    Date dateforalarm;
    ConstraintLayout constraintLayout ;
    Button SETDATE;
    Button SETTIME;
    EditText editpagetitletextview,editpagedescriptiontextview;
    Button editpagesubmitbutton;
    Calendar myCalender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_app_edit_page);

        myCalender = Calendar.getInstance();
        constraintLayout = (ConstraintLayout) findViewById(R.id.editpage);
        SETDATE = (Button) findViewById(R.id.SetDate);
        SETTIME = (Button) findViewById(R.id.SetTime);
        editpagedescriptiontextview = (EditText) findViewById(R.id.editpagedescriptiontextview);
        editpagetitletextview = (EditText) findViewById(R.id.editpagetitletextview);
        dateSet = (TextView) findViewById(R.id.dateSet);
        timeSet = (TextView) findViewById(R.id.timeSet);
        dateforalarm = new Date();
        editpagesubmitbutton = (Button) findViewById(R.id.submit);
        Intent  i = getIntent();
        String cate = i.getStringExtra(IntentConstants.ENTRY_CATEGORY);

         if(cate.equals(CategoryConstants.CATEGORY_BIRTHDAY)){
             constraintLayout.setBackgroundResource(R.drawable.happy_birthday_wallpaper);
        }
        else if(cate.equals(CategoryConstants.CATEGORY_MEETING))
         {
             constraintLayout.setBackgroundResource(R.drawable.meeting_wallpaper);
         }
         else if(cate.equals(CategoryConstants.CATEGORY_SHOPPING))
         {
             constraintLayout.setBackgroundResource(R.drawable.shopping_wallpaper);
         }
         else if(cate.equals(CategoryConstants.CATEGORY_STUDY))
         {
             constraintLayout.setBackgroundResource(R.drawable.study_wallpaper);
         }
         else if(cate.equals(CategoryConstants.CATEGORY_GYM))
         {
             constraintLayout.setBackgroundResource(R.drawable.gym_wallpaper);
         }
         else if(cate.equals(CategoryConstants.CATEGORY_OTHERS))
         {
             constraintLayout.setBackgroundResource(R.drawable.others_wallpaper);
         }



        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                year = i;
                month = i1 + 1;
                dateontextview = i2;
                dateSet.setText(year + "/" + month + '/' + dateontextview);

            }
        };

        SETDATE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(ToDoAppEditPageActivity.this,date,myCalender.get(Calendar.YEAR),myCalender.get(Calendar.MONTH),myCalender.get(Calendar.DATE)).show();
            }
        });


        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                int hrs = i;
                int min = i1;
                timeSet.setText(hrs + ":" + min);
            }
        };

        SETTIME.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(ToDoAppEditPageActivity.this,time,hour,min,false).show();
            }
        });


        if(i.getIntExtra(IntentConstants.ENTRY_ID,-1) != -1)
        {
           editpagetitletextview.setText(i.getStringExtra(IntentConstants.ENTRY_TITLE));
            editpagedescriptiontextview.setText(i.getStringExtra(IntentConstants.ENTRY_DESCRIPTION));
            dateSet.setText(i.getStringExtra(IntentConstants.ENTRY_DATE));
            timeSet.setText(i.getStringExtra(IntentConstants.ENTRY_TIME));
        }

        editpagesubmitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent  i = getIntent();
                String cate = i.getStringExtra(IntentConstants.ENTRY_CATEGORY);
                String newdescription = editpagedescriptiontextview.getText().toString().trim();
                String newtitle = editpagetitletextview.getText().toString().trim();
                String Date  = dateSet.getText().toString();
                String Time = timeSet.getText().toString();

                if(newtitle.length() != 0){
                ToDoAppOpenHelper toDoAppOpenHelper = ToDoAppOpenHelper.getToDoAppOpenHelper(ToDoAppEditPageActivity.this);

                SQLiteDatabase database = toDoAppOpenHelper.getWritableDatabase();

                ContentValues cv = new ContentValues();
                cv.put(ToDoAppOpenHelper.ENTRY_TITLE,newtitle);
                cv.put(ToDoAppOpenHelper.ENTRY_DESCRIPTION,newdescription);
                cv.put(ToDoAppOpenHelper.ENTRY_CATEGORY,cate);
                cv.put(ToDoAppOpenHelper.ENTRY_DATE,Date);
                cv.put(ToDoAppOpenHelper.ENTRY_TIME,Time);
                    setalarm(Date,Time);

                if(i.getIntExtra(IntentConstants.ENTRY_ID,-1) != -1) {

                    database.update(toDoAppOpenHelper.TABLE_NAME, cv, " " + toDoAppOpenHelper.ENTRY_ID + " = " + i.getIntExtra(IntentConstants.ENTRY_ID,-1), null);
                }
                else
                {
                    database.insert(ToDoAppOpenHelper.TABLE_NAME, null, cv);
                }



                    setResult(RESULT_OK);
                finish();
                }
                else
                {
                    Toast.makeText(ToDoAppEditPageActivity.this," Invalid Title",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }



    private void setalarm(String dateontextview,String Time) {


           String d[] = dateontextview.split("/");
           String t[] = Time.split(":");

           Calendar calendar2 = Calendar.getInstance();
        Calendar calendar = (Calendar) calendar2.clone();
        calendar.set(Calendar.MONTH,Integer.parseInt(d[1]) - 1);
        calendar.set(Calendar.YEAR,Integer.parseInt(d[0]));
        calendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(d[2]));

        calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(t[0]));
        calendar.set(Calendar.MINUTE,Integer.parseInt(t[1]));
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        AlarmManager alarmManager = (AlarmManager) ToDoAppEditPageActivity.this.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(ToDoAppEditPageActivity.this, AlarmsReceiver.class);
        PendingIntent p = PendingIntent.getBroadcast(ToDoAppEditPageActivity.this, pendingintentreqcode++, i, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), p);
    }


    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
