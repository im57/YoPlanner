package ddwu.mobile.final_project.ma02_20170976.plan;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Date;

import ddwu.mobile.final_project.ma02_20170976.MainActivity;
import ddwu.mobile.final_project.ma02_20170976.R;

import static java.lang.String.valueOf;

public class PlanActivity extends AppCompatActivity {

    MaterialCalendarView calendar;

    TextView tvPlanDate;
    ListView lvPlans = null;
    ImageView ivClock;
    TextView tvNothing;

    PlanDBHelper helper;
    SQLiteDatabase db;
    Cursor cursor = null;
    MyCursorAdapter myCursorAdapter;

    String sDate;
    String today;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        calendar = (MaterialCalendarView)findViewById(R.id.calendarView);

        tvPlanDate = findViewById(R.id.tvPlanDate);
        lvPlans = (ListView)findViewById(R.id.lvPlans);
        ivClock = findViewById(R.id.ivClock);
        tvNothing = findViewById(R.id.nothiing);

        helper = new PlanDBHelper(this);

        Date date = new Date();
        int nToday = (1900 + date.getYear())*10000 + (1+date.getMonth())*100 + date.getDate();
        today = String.valueOf(nToday);
        sDate = today;

        myCursorAdapter = new MyCursorAdapter(this, R.layout.listview_layout, cursor);

        lvPlans.setAdapter(myCursorAdapter);

        calendar.setOnDateChangedListener(new OnDateSelectedListener() {

            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                if((date.getMonth() + 1) < 10 && date.getDay() < 10)
                    sDate = date.getYear() + "0" + (date.getMonth() +1) + "0" + date.getDay();
                else if((date.getMonth() + 1) < 10 && date.getDay() >= 10)
                    sDate = date.getYear() + "0" + (date.getMonth() +1) + "" + date.getDay();
                else if((date.getMonth()  + 1 >= 10) && date.getDay() < 10)
                    sDate = date.getYear() + "" + (date.getMonth() +1) + "0" + date.getDay();
                else
                    sDate = date.getYear() + "" + (date.getMonth() +1) + "" + date.getDay();

                readAllPlans();
            }
        });

        //click listview
        lvPlans.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PlanActivity.this, UpdateActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        //long click listview
        lvPlans.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final long targetId = id;
                TextView tvName = view.findViewById(R.id.tvPlanContent);

                String dialogMessage = "'" + tvName.getText().toString() + "' 일정 삭제?";


                new AlertDialog.Builder(PlanActivity.this).setTitle("삭제확인")
                        .setMessage(dialogMessage)
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {

                            //							삭제 수행
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //delete alarm
                                AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                                Intent intent = new Intent(PlanActivity.this, AlarmReceiver.class);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(PlanActivity.this, Integer.parseInt(String.valueOf(targetId)), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                if (pendingIntent != null)
                                    manager.cancel(pendingIntent);

                                SQLiteDatabase db = helper.getWritableDatabase();

                                String whereClause = PlanDBHelper.COL_ID + "=?";
                                String[] whereArgs = new String[] { String.valueOf(targetId) };

                                db.delete(PlanDBHelper.TABLE_NAME, whereClause, whereArgs);
                                helper.close();
                                readAllPlansInCalendar();  //aplly delete
                            }
                        })
                        .setNegativeButton("취소", null)
                        .show();
                return true;
            }
        });
    }


    //get plan of especially date
    private void readAllPlans() {
        tvPlanDate.setText(sDate);
        tvNothing.setText("");

        cursor = null;

        //set adapter
        db = helper.getReadableDatabase();

        cursor = db.rawQuery("select * from " + PlanDBHelper.TABLE_NAME + " where " + PlanDBHelper.COL_DATE + " = " + sDate, null);

        myCursorAdapter.changeCursor(cursor);

        if(cursor.getCount() == 0){
            tvNothing.setText("일정이 없습니다.");
        }

        helper.close();
    }

    //show has plan with dot in calendar
    private  void readAllPlansInCalendar(){
        db = helper.getReadableDatabase();
        cursor = db.rawQuery("select distinct date from " + PlanDBHelper.TABLE_NAME, null);

        ArrayList<CalendarDay> cList = new ArrayList<>();
        CalendarDay cDay;
        Date date;

        String sDate;
        int year;
        int month;
        int day;

        for(int i = 0; i < cursor.getCount(); i++){
            cursor.moveToNext();

            sDate = cursor.getString(0);
            year = Integer.parseInt(sDate.substring(0,4))-1900;
            month = Integer.parseInt(sDate.substring(4,6))-1;
            day = Integer.parseInt(sDate.substring(6,8));

            date = new Date(year, month, day);
            cDay = CalendarDay.from(date);
            cList.add(cDay);
        }

        calendar.addDecorator(new EventDecorator(R.color.mainDark, cList));
        calendar.addDecorator(new TodayDecorator());

        readAllPlans();

        cursor.close();
        helper.close();

    }

    @Override
    protected void onResume() {
        super.onResume();
        readAllPlansInCalendar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helper.close();

        if (cursor != null)
            cursor.close();
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
