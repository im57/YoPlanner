package ddwu.mobile.final_project.ma02_20170976.introduce;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import ddwu.mobile.final_project.ma02_20170976.R;

public class VersionInfoActivity extends AppCompatActivity {
    TextView tvVersion;

    ListView lvDetail;
    ArrayList<String> versionDetail;

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version);

        tvVersion = findViewById(R.id.tv_version);
        lvDetail = findViewById(R.id.list_version_detail);

        versionDetail = new ArrayList<>();

        Intent intent = getIntent();
        String version = intent.getStringExtra("version");

        tvVersion.setText(version);

        switch (version){
            case "V1.0":
                versionDetail.add("일정관리 (추가, 검색, 수정, 삭제, 공유)");
                versionDetail.add("주변 검색 (주변 카페 나타내기, 카페와 나의 경로 표시)");
                versionDetail.add("미니게임 (요플레 버리기, 요플레 냉장고에 넣기)");

                break;
            case "V2.0":
                versionDetail.add("UI 개선");
                versionDetail.add("캘린더에 일정 등록 날짜 표시");

                break;
        }

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, versionDetail);
        lvDetail.setAdapter(adapter);
    }
}
