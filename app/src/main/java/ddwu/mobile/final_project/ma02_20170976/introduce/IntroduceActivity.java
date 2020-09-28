package ddwu.mobile.final_project.ma02_20170976.introduce;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import ddwu.mobile.final_project.ma02_20170976.OpenActivity;
import ddwu.mobile.final_project.ma02_20170976.R;

public class IntroduceActivity extends AppCompatActivity {
    ArrayList<String> versionInfo;
    ListView lvVersion;

    TextView tvName;

    ArrayAdapter<String> adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);

        tvName = findViewById(R.id.text_developer_name);

        versionInfo = new ArrayList<String>();
        versionInfo.add("V1.0");
        versionInfo.add("V2.0");

        adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, versionInfo);

        lvVersion = findViewById(R.id.lv_version);
        lvVersion.setAdapter(adapter);

        lvVersion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(IntroduceActivity.this, VersionInfoActivity.class);
                intent.putExtra("version", versionInfo.get(i));
                startActivity(intent);
            }
        });
    }


    public void onClick(View v){
        Toast.makeText(this, "안녕하세요 반갑습니다", Toast.LENGTH_SHORT).show();
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
