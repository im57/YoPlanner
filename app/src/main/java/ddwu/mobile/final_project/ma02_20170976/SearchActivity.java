package ddwu.mobile.final_project.ma02_20170976;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class SearchActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnSearchContent:
                Intent cIntent = new Intent(this, SearchPlanByContentActivity.class);
                startActivity(cIntent);
                break;
            case R.id.btnSearchDate:
                Intent dIntent = new Intent(this, SearchPlanByDateActivity.class);
                startActivity(dIntent);
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
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}
