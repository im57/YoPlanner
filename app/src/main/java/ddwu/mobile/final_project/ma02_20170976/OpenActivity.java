package ddwu.mobile.final_project.ma02_20170976;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.share.widget.ShareDialog;

import ddwu.mobile.final_project.ma02_20170976.introduce.IntroduceActivity;
import ddwu.mobile.final_project.ma02_20170976.joke.JokeActivity;

public class OpenActivity extends AppCompatActivity {
    int touch = 0;
    View view;

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);

        view = findViewById(R.id.vOpen);

        //when touch view 10times change activity
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    touch++;

                    if(touch == 10){
                        Intent realIntent = new Intent(OpenActivity.this, JokeActivity.class);
                        startActivity(realIntent);
                        touch = 0;
                    }
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_open, menu);
        return true;
    }

    @Override
    //choose menu
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            //introduce
            case R.id.mIntroduce:
                Intent intent = new Intent(this, IntroduceActivity.class);
                startActivity(intent);
                break;
            //close app
            case R.id.mClose:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("앱 종료");
                builder.setMessage("앱을 종료하시겠습니까?");
                builder.setCancelable(false);
                builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();//종료
                        System.runFinalization();
                        System.exit(0);
                    }
                });
                builder.setNegativeButton("취소", null);

                Dialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                break;
        }
        return true;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
/*            case R.id.button2:
                try {
                    final KakaoLink link = KakaoLink.getKakaoLink(this);
                    final KakaoTalkLinkMessageBuilder builder = link.createKakaoTalkLinkMessageBuilder();

                    builder.addText("TEXT");
                    builder.addAppButton("앱 실행하기");
                    link.sendMessage(builder, this);

                } catch (KakaoParameterException e) {
                    e.printStackTrace();
                }
                break;
*/        }

    }
}
