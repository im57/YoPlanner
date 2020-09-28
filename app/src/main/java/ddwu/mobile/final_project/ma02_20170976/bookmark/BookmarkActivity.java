package ddwu.mobile.final_project.ma02_20170976.bookmark;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.facebook.CallbackManager;
import com.facebook.share.widget.ShareDialog;

import ddwu.mobile.final_project.ma02_20170976.MainActivity;
import ddwu.mobile.final_project.ma02_20170976.R;
import ddwu.mobile.final_project.ma02_20170976.introduce.IntroduceActivity;
import ddwu.mobile.final_project.ma02_20170976.joke.JokeActivity;
import ddwu.mobile.final_project.ma02_20170976.plan.PlanDBHelper;

public class BookmarkActivity extends AppCompatActivity {

    AlertDialog.Builder builder;
    LinearLayout layout;

    EditText etName;
    EditText etAddress;

    PlanDBHelper helper;
    SQLiteDatabase db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        builder = new AlertDialog.Builder(this);

        helper = new PlanDBHelper(this);
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddBookmark:
                layout = (LinearLayout)View.inflate(this, R.layout.layout_bookmark, null);

                etName = layout.findViewById(R.id.etBookmarkName);
                etAddress = layout.findViewById(R.id.etBookmarkAddress);

                db = helper.getWritableDatabase();

                builder.setMessage("북마크 주소 입력")
                        .setView(layout)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //이름, 주소 입력되었고, 주소가 올바르면
                                //db에 저장
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                break;
        }
    }
}
