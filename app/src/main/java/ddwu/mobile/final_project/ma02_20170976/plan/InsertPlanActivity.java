package ddwu.mobile.final_project.ma02_20170976.plan;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

import ddwu.mobile.final_project.ma02_20170976.R;

public class InsertPlanActivity extends Activity {
	final int REQ_CODE = 400;
	final int RESULT_ADD = 100;
	final int RESULT_DELETE = 200;
	final int RESULT_CANCELE = 300;

	EditText etContent;
	EditText etDate;
	EditText etPlace;
	EditText etAttendance;

	PlanDBHelper helper;
	SQLiteDatabase db;

	Boolean addAlarm = false;

	PendingIntent pendingIntent = null;

	int count = 0;
	String time = "";
	String alarm = "";
	String repeat = "";
	String interval = "";
	int al = 0;
	long id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_insert_plans);

		etContent = findViewById(R.id.etInsertContent);
		etDate = findViewById(R.id.etInsertDate);
		etPlace = findViewById(R.id.etInsertPlace);
		etAttendance = findViewById(R.id.etInsertAttendance);

		helper = new PlanDBHelper(this);
	}
	
	
	public void onClick(View v) {
		AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

		String content = etContent.getText().toString();
		String date = etDate.getText().toString();
		String place = etPlace.getText().toString();
		String attend = etAttendance.getText().toString();
		int[] dates;
		char check;

		switch(v.getId()) {
		//add  to DB
		case R.id.btnAddNewPlan:
			//not number
			for(int i = 0; i<date.length(); i++){
				check = date.charAt(i);
				if( check < 48 || check > 58)
				{
					Toast.makeText(this, "날짜를 정확하게 입력하세요!", Toast.LENGTH_SHORT).show();
					return;
				}
			}
			//text length is long
			if(date.length() != 8){
				Toast.makeText(this, "날짜를 정확하게 입력하세요!", Toast.LENGTH_SHORT).show();
				return;
			}
			//didn't write
			if(date.equals("")){
				Toast.makeText(this, "날짜를 입력하세요!", Toast.LENGTH_SHORT).show();
				return;
			}
			dates = new int[]{Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(4, 6)), Integer.parseInt(date.substring(6, 8))};

			//didn't write
			if(content.equals("")){
				Toast.makeText(this, "내용을 입력하세요!", Toast.LENGTH_SHORT).show();
				return;
			}
			//check date
			if(dates[1] > 12 || dates[1] < 1){
				Toast.makeText(this, "날짜를 정확하게 입력하세요!", Toast.LENGTH_SHORT).show();
				return;
			}
			if((dates[1] == 1 || dates[1] == 3 || dates[1] == 5 || dates[1] == 7 || dates[1] == 8 || dates[1] == 10 || dates[1] == 12) && (dates[2] > 31 || dates[2] < 1)){
				Toast.makeText(this, "날짜를 정확하게 입력하세요!", Toast.LENGTH_SHORT).show();
				return;
			}
			if((dates[1] == 4 || dates[1] == 6 || dates[1] == 9 || dates[1] == 11) && (dates[2] > 30 || dates[2] < 1)){
				Toast.makeText(this, "날짜를 정확하게 입력하세요!", Toast.LENGTH_SHORT).show();
				return;
			}
			if(((dates[0] % 4 == 0 && dates[0] % 100 != 0) || dates[0] % 400 == 0) && (dates[1] == 2 ) && (dates[2] > 29 || dates[2] < 1)){
				Toast.makeText(this, "날짜를 정확하게 입력하세요!", Toast.LENGTH_SHORT).show();
				return;
			}
			if(((dates[0] % 4 != 0 || dates[0] % 100 == 0) && dates[0] % 400 != 0) && (dates[1] == 2 ) && (dates[2] > 28 || dates[2] < 1)){
				Toast.makeText(this, "날짜를 정확하게 입력하세요!", Toast.LENGTH_SHORT).show();
				return;
			}
			//didn't write
			if(place.equals("")){
				Toast.makeText(this, "장소를 입력하세요!", Toast.LENGTH_SHORT).show();
				return;
			}


			db = helper.getWritableDatabase();

			ContentValues row = new ContentValues();
			row.put(PlanDBHelper.COL_CONTENT, content);
			row.put(PlanDBHelper.COL_DATE, date);
			row.put(PlanDBHelper.COL_PLACE, place);
			row.put(PlanDBHelper.COL_ATTENDANCE, attend);

			db.insert(PlanDBHelper.TABLE_NAME, null, row);

			helper.close();

			//get id
			SQLiteDatabase db2 = helper.getReadableDatabase();
			Cursor cursor = db2.rawQuery("select _id from " + PlanDBHelper.TABLE_NAME , null);
			while(cursor.moveToNext())
				id = Integer.parseInt(cursor.getString(0));

			//add alarm
			if(addAlarm){
				Intent intentAlarm = new Intent(this, AlarmReceiver.class);

				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(System.currentTimeMillis());
				calendar.set(dates[0], dates[1]-1, dates[2], Integer.parseInt(time.substring(0,2)), Integer.parseInt(time.substring(2,4)), Integer.parseInt(time.substring(4,6)));

				//repeat
				if(repeat.equals("NoRepeat")) {
					intentAlarm.putExtra("content", alarm);
					intentAlarm.putExtra("id", id);

					intentAlarm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					pendingIntent = PendingIntent.getBroadcast(this, Integer.parseInt(String.valueOf(id)), intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

					manager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
					Log.d("arlam: ", "no repeat");
				}
				//no repeat
				if(repeat.equals("Repeat")){
					intentAlarm.putExtra("content", alarm);
					intentAlarm.putExtra("id", id);

					intentAlarm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					pendingIntent = PendingIntent.getBroadcast(this, Integer.parseInt(String.valueOf(id)), intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

					manager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), Integer.parseInt(interval) * 100, pendingIntent);
					Log.d("arlam: ", "repeat");
				}

				cursor.close();
				helper.close();
			}
			//add alarm in DB
			SQLiteDatabase db3 = helper.getWritableDatabase();
			ContentValues rowA = new ContentValues();
			rowA.put(PlanDBHelper.COL_PATH, "");
			rowA.put(PlanDBHelper.COL_TIME, time);
			rowA.put(PlanDBHelper.COL_ALARM, alarm);
			if(repeat.equals("NoRepeat")){
				rowA.put(PlanDBHelper.COL_REPEAT, "NoRepeat");
			}
			else{
				rowA.put(PlanDBHelper.COL_REPEAT, "Repeat");
			}
			rowA.put(PlanDBHelper.COL_INTERVAL, interval);

			String whereClause = PlanDBHelper.COL_ID + "=?";
			String[] whereArgs = new String[] { String.valueOf(id) };

			long result = db3.update(PlanDBHelper.TABLE_NAME, rowA, whereClause, whereArgs);

			helper.close();

			String msg = result > 0 ? "일정 추가 성공!" : "일정 추가 실패!";
			Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

			Intent intent = new Intent(InsertPlanActivity.this, PlanActivity.class);
			intent.putExtra("date", date);
			startActivity(intent);

			break;
		case R.id.btnAddNewPlanClose:
			finish();
			break;
//		case R.id.btnBookmark:
//
//				break;
		case R.id.btnInsertAlarm:
			if(date.equals("")){
				Toast.makeText(this, "날짜입력 후 알람을 설정해주세요.", Toast.LENGTH_SHORT).show();
				return;
			}
			for(int i = 0; i<date.length(); i++){
				check = date.charAt(i);
				if( check < 48 || check > 58)
				{
					//Not number
					Toast.makeText(this, "날짜를 정확하게 입력하세요!", Toast.LENGTH_SHORT).show();
					return;
				}
			}
			if(date.length() != 8){
				Toast.makeText(this, "날짜를 정확하게 입력하세요!", Toast.LENGTH_SHORT).show();
				return;
			}
			dates = new int[]{Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(4, 6)), Integer.parseInt(date.substring(6, 8))};

			if(dates[1] > 12 || dates[1] < 1){
				Toast.makeText(this, "날짜를 정확하게 입력하세요!", Toast.LENGTH_SHORT).show();
				return;
			}
			if((dates[1] == 1 || dates[1] == 3 || dates[1] == 5 || dates[1] == 7 || dates[1] == 8 || dates[1] == 10 || dates[1] == 12) && (dates[2] > 31 || dates[2] < 1)){
				Toast.makeText(this, "날짜를 정확하게 입력하세요!", Toast.LENGTH_SHORT).show();
				return;
			}
			if((dates[1] == 4 || dates[1] == 6 || dates[1] == 9 || dates[1] == 11) && (dates[2] > 30 || dates[2] < 1)){
				Toast.makeText(this, "날짜를 정확하게 입력하세요!", Toast.LENGTH_SHORT).show();
				return;
			}
			if(((dates[0] % 4 == 0 && dates[0] % 100 != 0) || dates[0] % 400 == 0) && (dates[1] == 2 ) && (dates[2] > 29 || dates[2] < 1)){
				Toast.makeText(this, "날짜를 정확하게 입력하세요!", Toast.LENGTH_SHORT).show();
				return;
			}
			if(((dates[0] % 4 != 0 || dates[0] % 100 == 0) && dates[0] % 400 != 0) && (dates[1] == 2 ) && (dates[2] > 28 || dates[2] < 1)){
				Toast.makeText(this, "날짜를 정확하게 입력하세요!", Toast.LENGTH_SHORT).show();
				return;
			}

/*			//get id (id ==count+1)
			SQLiteDatabase db2 = helper.getReadableDatabase();
			Cursor cursor = db2.rawQuery("select @@IDENTITY()", null);
			while(cursor.moveToNext())
				id = Integer.parseInt(cursor.getString(0)) + 1;

			Log.d("aaaaaaa", String.valueOf(id));
*/
			Intent aIntent = new Intent(this, AlarmActivity.class);
			aIntent.putExtra("id", Long.parseLong(String.valueOf(id)));
			aIntent.putExtra("date", etDate.getText().toString());
			aIntent.putExtra("content", etContent.getText().toString());
			aIntent.putExtra("place", etPlace.getText().toString());
			aIntent.putExtra("attend", etAttendance.getText().toString());

			//put last data entered
			if(al == 1){
				aIntent.putExtra("time", time);
				aIntent.putExtra("alarm", alarm);
				aIntent.putExtra("repeat", repeat);
				if(repeat.equals("Repeat"))
					aIntent.putExtra("interval", interval);
			}

			//cursor.close();
			//helper.close();

			startActivityForResult(aIntent, REQ_CODE);
			break;

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Result of AlarmActivity
		if (requestCode == REQ_CODE) {
			switch(resultCode) {
				case RESULT_ADD:
					id = data.getLongExtra("id", 0);
					time = data.getStringExtra("time");
					alarm = data.getStringExtra("alarm");
					repeat = data.getStringExtra("repeat");
					if(repeat.equals("Repeat"))
						interval = data.getStringExtra("interval");
					addAlarm = true;
					al=1;

					if(data.getStringExtra("content") != null){
						etContent.setText(data.getStringExtra("content"));
						etPlace.setText(data.getStringExtra("place"));
						etAttendance.setText(data.getStringExtra("attend"));
					}

					break;
				case RESULT_DELETE:
					if(data.getStringExtra("content") != null){
						etContent.setText(data.getStringExtra("content"));
						etPlace.setText(data.getStringExtra("place"));
						etAttendance.setText(data.getStringExtra("attend"));
					}

					break;
				case RESULT_CANCELE:
					if(data.getStringExtra("content") != null){
						etContent.setText(data.getStringExtra("content"));
						etPlace.setText(data.getStringExtra("place"));
						etAttendance.setText(data.getStringExtra("attend"));
					}

					break;
			}
		}
	}
}
