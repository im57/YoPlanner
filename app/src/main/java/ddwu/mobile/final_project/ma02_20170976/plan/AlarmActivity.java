package ddwu.mobile.final_project.ma02_20170976.plan;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

import ddwu.mobile.final_project.ma02_20170976.R;

public class AlarmActivity extends AppCompatActivity {
    final int RESULT_ADD = 100;
    final int RESULT_DELETE = 200;
    final int RESULT_CANCELE = 300;

    TextView tvAlarmDate;
    EditText etAlarmTime;
    EditText etAlarmContent;
    EditText etAlarmInterval;
    RadioGroup rBtnRepeat;
    RadioButton rBtnNoRe;
    RadioButton rBtnRe;

    long id;

    Intent getIntent;
    PlanDBHelper helper;

    String c = "";
    String p = "";
    String a = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        tvAlarmDate = findViewById(R.id.tvAlarmDate);
        etAlarmTime = findViewById(R.id.etAlarmTime);
        etAlarmContent = findViewById(R.id.etAlarmContent);
        etAlarmInterval = findViewById(R.id.etAlarmInterval);
        rBtnRepeat = findViewById(R.id.rBtnRepeat);
        rBtnNoRe = findViewById(R.id.rBtnNoRe);
        rBtnRe = findViewById(R.id.rBtnRe);

        helper = new PlanDBHelper(this);

        //set date automatically
        getIntent = getIntent();
        tvAlarmDate.setText(getIntent.getStringExtra("date"));

        //get info of plan if have
        if(!getIntent.getStringExtra("content").equals(""))
            c = getIntent.getStringExtra("content");
        if(!getIntent.getStringExtra("place").equals(""))
            p = getIntent.getStringExtra("place");
        if(!getIntent.getStringExtra("attend").equals(""))
            a = getIntent.getStringExtra("attend");

        id = getIntent.getLongExtra("id", 0);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //get alarm info
        if(id != 0){
            SQLiteDatabase dbSelect = helper.getReadableDatabase();
            Cursor cursor = dbSelect.rawQuery( "select * from " + PlanDBHelper.TABLE_NAME + " where " + PlanDBHelper.COL_ID + "=?", new String[] { String.valueOf(id) });
            while (cursor.moveToNext()) {
                etAlarmTime.setText(cursor.getString( cursor.getColumnIndex(PlanDBHelper.COL_TIME)));
                etAlarmContent.setText(cursor.getString(cursor.getColumnIndex(PlanDBHelper.COL_ALARM)));
                etAlarmInterval.setText(cursor.getString(cursor.getColumnIndex(PlanDBHelper.COL_INTERVAL)));
                if(cursor.getString(cursor.getColumnIndex(PlanDBHelper.COL_REPEAT)).equals("Repeat")) {
                    rBtnRe.setChecked(true);
                }
                else {
                    rBtnNoRe.setChecked(true);
                }
            }
            cursor.close();
            helper.close();
        }

        if(getIntent.getStringExtra("time") != null){
            etAlarmTime.setText(getIntent.getStringExtra("time"));
            etAlarmContent.setText(getIntent.getStringExtra("alarm"));
            if(getIntent.getStringExtra("repeat").equals("Repeat")) {
                rBtnRe.setChecked(true);
            }
            else {
                rBtnNoRe.setChecked(true);
            }
            if(getIntent.getStringExtra("repeat").equals("Repeat"))
                etAlarmInterval.setText(getIntent.getStringExtra("interval"));
        }
    }

    public void onClick(View v){
        Intent intent = new Intent();

        String date = tvAlarmDate.getText().toString();
        String time = etAlarmTime.getText().toString();

        switch (v.getId()){
            //add alarm
            case R.id.btnAlarmAdd:
                Date d = new Date();
                int nowDate = (d.getYear()+1900)*10000 + (d.getMonth()+1)*100 + d.getDate();
                int nowTime = d.getHours()*10000 + d.getMinutes()*100 + d.getSeconds();
                char check;

                //date is bigger then now
                if(Integer.parseInt(date) < nowDate){
                    Toast.makeText(this, "입력 날짜가 현재 날짜보다 작아 알람 설정이 불가능 합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                for(int i = 0; i<time.length(); i++){
                    check = time.charAt(i);

                    //if time character is not number
                    if( check < 48 || check > 58)
                    {
                        Toast.makeText(this, "시간을 정확하게 입력하세요!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                //if time length is not 8
                if(time.length() != 6){
                    Toast.makeText(this, "시간을 정확하게 입력하세요!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //if didn't input time
                if(time.equals("")){
                    Toast.makeText(this, "시간을 입력하세요!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int hour = Integer.parseInt(time.substring(0,2));
                int min = Integer.parseInt(time.substring(2,4));
                int sec = Integer.parseInt(time.substring(4,6));


                //time check
                if(hour > 23 || hour < 0){
                    Toast.makeText(this, "시간을 정확하게 입력하세요!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(min > 59 || min < 0){
                    Toast.makeText(this, "시간을 정확하게 입력하세요!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(sec > 59 || sec <0){
                    Toast.makeText(this, "시간을 정확하게 입력하세요!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //time is bigger then now
                if((Integer.parseInt(date) == nowDate) && (Integer.parseInt(time) < nowTime)){
                    Toast.makeText(this, "현재 시간 이후의 시간을 입력하세요!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //if want to repeat, need to input interval except '0'
                if((rBtnRe.isChecked() && etAlarmInterval.getText().toString().equals("")) || (rBtnRe.isChecked() && etAlarmInterval.getText().toString().equals("0"))) {
                    Toast.makeText(this, "알람 간격을 입력하세요!", Toast.LENGTH_SHORT).show();
                    return;
                }


                intent.putExtra("time", time);
                intent.putExtra("alarm", etAlarmContent.getText().toString());
                //no repeat
                if(rBtnNoRe.isChecked()) {
                    intent.putExtra("repeat", "NoRepeat");
                }
                //repeat
                if(rBtnRe.isChecked()){
                    intent.putExtra("repeat", "Repeat");
                }
                //need inteval, if repeat
                if(rBtnRe.isChecked())
                    intent.putExtra("interval", etAlarmInterval.getText().toString());

                intent.putExtra("id", id);
                intent.putExtra("date", date);
                intent.putExtra("content", c);
                intent.putExtra("place", p);
                intent.putExtra("attend", a);

                setResult(RESULT_ADD, intent);
                finish();
                break;
            case R.id.btnAlarmDelete:
                //delete alarm
                intent.putExtra("id", id);
                intent.putExtra("date", date);
                intent.putExtra("content", c);
                intent.putExtra("place", p);
                intent.putExtra("attend", a);

                setResult(RESULT_DELETE, intent);
                finish();
                break;
            case R.id.btnAlarmCancel:
                intent.putExtra("id", id);
                intent.putExtra("content", c);
                intent.putExtra("place", p);
                intent.putExtra("attend", a);
                intent.putExtra("date", date);

                setResult(RESULT_CANCELE, intent);
                finish();
                break;
        }
    }

}
