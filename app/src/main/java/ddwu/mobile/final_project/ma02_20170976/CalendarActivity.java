package ddwu.mobile.final_project.ma02_20170976;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

import static java.lang.String.valueOf;

public class CalendarActivity extends AppCompatActivity {

    CalendarView calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendar = (CalendarView)findViewById(R.id.calendarView);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            //날짜 선택
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                //forward date
                Intent intent = new Intent(CalendarActivity.this, AllPlansActivity.class);
                String sDate;

                if((month + 1) < 10 && day < 10)
                    sDate = year + "0" + (month+1) + "0" + day;
                else if((month + 1) < 10 && day >= 10)
                    sDate = year + "0" + (month+1) + "" + day;
                else if((month + 1 >= 10) && day < 10)
                    sDate = year + "" + (month+1) + "0" + day;
                else
                    sDate = year + "" + (month+1) + "" + day;

                Log.d("sDate", sDate);
                intent.putExtra("date", sDate);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    //choose menu
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.addMenu:
                Intent intent = new Intent(this, InsertPlanActivity.class);
                startActivity(intent);
                break;
            case R.id.closeAddMenu:
                Intent intent2 = new Intent(this, MainActivity.class);
                startActivity(intent2);
                break;
        }
        return true;
    }
}
