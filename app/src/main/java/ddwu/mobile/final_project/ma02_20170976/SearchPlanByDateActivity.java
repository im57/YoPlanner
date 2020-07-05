package ddwu.mobile.final_project.ma02_20170976;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SearchPlanByDateActivity extends AppCompatActivity {

    EditText etSearchDate;
    ListView lvSearchResultDate;

    PlanDBHelper helper;
    MyCursorAdapter myCursorAdapter;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_date);

        etSearchDate = findViewById(R.id.etSearchDate);
        lvSearchResultDate = findViewById(R.id.lvSearchResultDate);

        helper = new PlanDBHelper(this);

        lvSearchResultDate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchPlanByDateActivity.this, UpdateActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
    }

    //get plan of date
    private void readAllPlans() {

        //set adapter
        SQLiteDatabase db = helper.getReadableDatabase();

        String searchDate = etSearchDate.getText().toString();

        cursor = db.rawQuery("select * from " + PlanDBHelper.TABLE_NAME + " where "
                + PlanDBHelper.COL_DATE + " like '%" + searchDate +"%'", null);

        myCursorAdapter = new MyCursorAdapter(this, R.layout.listview_layout, cursor);

        lvSearchResultDate.setAdapter(myCursorAdapter);

        helper.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null) cursor.close();
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnSearchPlanDate:
                readAllPlans();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_close, menu);
        return true;
    }

    //choose menu
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.closeMenu:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}
