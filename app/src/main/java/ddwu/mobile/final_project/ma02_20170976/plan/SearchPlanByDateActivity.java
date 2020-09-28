package ddwu.mobile.final_project.ma02_20170976.plan;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ddwu.mobile.final_project.ma02_20170976.R;

public class SearchPlanByDateActivity extends AppCompatActivity {

    TextView tvTitle;
    EditText etSearch;
    ListView lvSearchResult;
    TextView tvNothing;

    PlanDBHelper helper;
    MyCursorAdapter myCursorAdapter;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_content);

        tvTitle = findViewById(R.id.text_search_title);
        etSearch = findViewById(R.id.etSearchContent);
        lvSearchResult = findViewById(R.id.lvSearchResult);
        tvNothing = findViewById(R.id.nothiing);

        tvTitle.setText("날짜로 일정 검색");

        helper = new PlanDBHelper(this);

        lvSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        tvNothing.setText("");

        //set adapter
        SQLiteDatabase db = helper.getReadableDatabase();

        String searchDate = etSearch.getText().toString();

        cursor = db.rawQuery("select * from " + PlanDBHelper.TABLE_NAME + " where "
                + PlanDBHelper.COL_DATE + " like '%" + searchDate +"%'", null);

        myCursorAdapter = new MyCursorAdapter(this, R.layout.listview_layout, cursor);

        lvSearchResult.setAdapter(myCursorAdapter);

        if(cursor.getCount() == 0){
            tvNothing.setText("일정이 없습니다.");
        }

        helper.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null) cursor.close();
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnSearchPlan:
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
