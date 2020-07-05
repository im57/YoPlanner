package ddwu.mobile.final_project.ma02_20170976;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AllPlansActivity extends AppCompatActivity {

	final static String TAG = "AllPlansActivity";

	TextView tvPlanDate;
	ListView lvPlans = null;
	ImageView ivClock;

	PlanDBHelper helper;
	Cursor cursor = null;
	MyCursorAdapter myCursorAdapter;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_plans);

		tvPlanDate = findViewById(R.id.tvPlanDate);
		lvPlans = (ListView)findViewById(R.id.lvPlans);
		ivClock = findViewById(R.id.ivClock);

		helper = new PlanDBHelper(this);

		myCursorAdapter = new MyCursorAdapter(this, R.layout.listview_layout, cursor);

		lvPlans.setAdapter(myCursorAdapter);



		//click listview
		lvPlans.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(AllPlansActivity.this, UpdateActivity.class);
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


				new AlertDialog.Builder(AllPlansActivity.this).setTitle("삭제확인")
						.setMessage(dialogMessage)
						.setPositiveButton("삭제", new DialogInterface.OnClickListener() {

							//							삭제 수행
							@Override
							public void onClick(DialogInterface dialog, int which) {
								//delete alarm
								AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
								Intent intent = new Intent(AllPlansActivity.this, AlarmReceiver.class);
								PendingIntent pendingIntent = PendingIntent.getBroadcast(AllPlansActivity.this, Integer.parseInt(String.valueOf(targetId)), intent, PendingIntent.FLAG_UPDATE_CURRENT);
								if (pendingIntent != null)
									manager.cancel(pendingIntent);

								SQLiteDatabase db = helper.getWritableDatabase();

								String whereClause = PlanDBHelper.COL_ID + "=?";
								String[] whereArgs = new String[] { String.valueOf(targetId) };

								db.delete(PlanDBHelper.TABLE_NAME, whereClause, whereArgs);
								helper.close();
								readAllPlans();		//aplly delete
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
		Intent intent = getIntent();
		String date = intent.getStringExtra("date");
		tvPlanDate.setText(date);

		cursor = null;

		//set adapter
		SQLiteDatabase db = helper.getReadableDatabase();

		Log.d("date", date);
		cursor = db.rawQuery("select * from " + PlanDBHelper.TABLE_NAME + " where " + PlanDBHelper.COL_DATE + " = " + date, null);

		myCursorAdapter.changeCursor(cursor);
		helper.close();
	}

	@Override
	protected void onResume() {
		super.onResume();

		readAllPlans();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
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
				Intent intent2 = new Intent(this, CalendarActivity.class);
				startActivity(intent2);
				break;
		}
		return true;
	}
}




