package ddwu.mobile.final_project.ma02_20170976.joke;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

import ddwu.mobile.final_project.ma02_20170976.OpenActivity;
import ddwu.mobile.final_project.ma02_20170976.R;

public class SensorActivity extends AppCompatActivity {
    BallView ballView;

    private SensorManager mSensorMgr;
    private Sensor accelerometer;
    private Sensor magnetometer;

    int level = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor);
        ballView = new BallView(this);
        setContentView(ballView);

        mSensorMgr = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorMgr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorMgr.unregisterListener(ballView);
    }


    @Override
    protected void onResume() {
        super.onResume();

        mSensorMgr.registerListener(ballView, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorMgr.registerListener(ballView, magnetometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    class BallView extends View implements SensorEventListener {

        float[] mGravity = null;
        float[] mGeomagnetic= null;

        Paint paint;
        BitmapDrawable drawable;
        Bitmap bitmap;
        Bitmap bitmapCan;

        int width;
        int height;

        int x;
        int y;

        int xCan;
        int yCan;

        boolean reset;

        Random random;

        public BallView(Context context) {
            super(context);
            paint = new Paint();
            reset = true;

            random = new Random();

            Resources res = getResources();
            drawable = (BitmapDrawable)res.getDrawable(R.mipmap.yoplanner);
            bitmap = drawable.getBitmap();
            drawable = (BitmapDrawable)res.getDrawable(R.mipmap.trashcan);
            bitmapCan = drawable.getBitmap();

            bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
            bitmapCan = Bitmap.createScaledBitmap(bitmapCan, 150, 150, true);

            Log.d("width", String.valueOf(bitmap.getWidth()));
            Log.d("height", String.valueOf(bitmap.getHeight()));
        }

        public void onDraw(Canvas canvas) {
            if(reset) {
                width = canvas.getWidth();
                height = canvas.getHeight();
                x =  width / 2;
                y =  height / 2;

                //random location
                xCan = random.nextInt(canvas.getWidth() - bitmapCan.getWidth()/2);
                yCan = random.nextInt(canvas.getHeight() - bitmapCan.getHeight()/2);

                reset = false;
            }

            canvas.drawBitmap(bitmap, x, y, paint);
            canvas.drawBitmap(bitmapCan, xCan, yCan, paint);

            //hide
            if(((x > xCan - bitmapCan.getWidth()/2)&& (x < xCan + bitmapCan.getWidth()/2)) && ((y > yCan - bitmapCan.getHeight()/2) && (y < yCan + bitmapCan.getHeight()/2))){
                reset = true;
                Toast.makeText(SensorActivity.this, "요플레 버리기 성공", Toast.LENGTH_SHORT).show();
                level++;

                bitmapCan = Bitmap.createScaledBitmap(bitmapCan, bitmapCan.getWidth()-5, bitmapCan.getHeight()-5, true);

                if(level > 5){
                    Intent intent = new Intent(SensorActivity.this, Sensor2Activity.class);
                    startActivity(intent);
                    level = 1;
                    reset = true;
                }

                invalidate();
            }
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
//			중력과 자기장 값 획득, 각각 획득되므로 모두 수집할 때까지 멤버변수에 저장
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                mGravity = event.values.clone();
            }
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                mGeomagnetic = event.values.clone();

//		    두 센서 값이 모두 수집되었을 경우
            if (mGravity != null && mGeomagnetic != null) {
                float rotationMatrix[] = new float[9];
//		    	기기 측정 중력 및 자기장 값을 토대로 회전 정보 획득, rotationMatrix에 해당 값 저장
                boolean success = SensorManager.getRotationMatrix(rotationMatrix, null, mGravity, mGeomagnetic);

                if (success) {
                    float values[] = new float[3];
//		    		회전 정보 매트릭스를 통해 기기의 orientation 획득, values 에 저장
                    SensorManager.getOrientation(rotationMatrix, values);

//		    		from rad. to degree
                    for (int i=0; i < values.length; i++) {
                        Double degrees = Math.toDegrees(values[i]);
                        values[i] = degrees.floatValue();
                    }

                    float pitch = values[1];
                    float roll = values[2];

                    if (pitch > 0) {		// 앞으로 기울여졌을 때
                        if (y > 0)
                            y -= 1;
                    } else if (pitch < 0) {		// 뒤로 기울여졌을 때
                        if (y < (height - bitmap.getHeight()))
                            y += 1;
                    }

                    if (roll > 0) { 	// 오른쪽으로 기울여졌을 때
                        if (x < (width - bitmap.getWidth()))
                            x += 1;
                    } else if (roll < 0) { 		// 왼쪽으로 기울여졌을 때
                        if (x > 0)
                            x -= 1;
                    }

                    invalidate();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
                Intent intent = new Intent(this, OpenActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}
