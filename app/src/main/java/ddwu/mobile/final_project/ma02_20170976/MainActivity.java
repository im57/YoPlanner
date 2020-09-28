package ddwu.mobile.final_project.ma02_20170976;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import ddwu.mobile.final_project.ma02_20170976.bookmark.BookmarkActivity;
import ddwu.mobile.final_project.ma02_20170976.plan.InsertPlanActivity;
import ddwu.mobile.final_project.ma02_20170976.plan.PlanActivity;
import ddwu.mobile.final_project.ma02_20170976.plan.SearchActivity;
import ddwu.mobile.final_project.ma02_20170976.place.MapActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v) {
        Intent intent = null;

        switch (v.getId()) {
            case R.id.btnOpenAllPlanA:
                intent = new Intent(this, PlanActivity.class);
                break;
            case R.id.btnAddNewPlanA:
                intent = new Intent(this, InsertPlanActivity.class);
                break;
            case R.id.btnSearchPlanA:
                intent = new Intent(this, SearchActivity.class);
                break;
            case R.id.btnMapA:
                intent = new Intent(this, MapActivity.class);
                break;
//            case R.id.btnBookmark:
//                intent = new Intent(this, BookmarkActivity.class);
//                break;
        }

        if (intent != null) startActivity(intent);
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
                Intent intent = new Intent(this, OpenActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

}
