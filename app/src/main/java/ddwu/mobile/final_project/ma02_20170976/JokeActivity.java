package ddwu.mobile.final_project.ma02_20170976;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class JokeActivity extends AppCompatActivity {
    private SensorManager manager;
    private Sensor light;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joke);

        Toast.makeText(this, "요플레", Toast.LENGTH_SHORT).show();

        manager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        light = manager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }


    @Override
    protected void onPause() {
        super.onPause();
        manager.unregisterListener(mLightSensorListener);
    }
    @Override
    protected void onResume() {
        super.onResume();

        manager.registerListener(mLightSensorListener, light, SensorManager.SENSOR_DELAY_UI);
    }

    //check light
    SensorEventListener mLightSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.accuracy != SensorManager.SENSOR_STATUS_UNRELIABLE) {
                if (sensorEvent.values[0] < 10) {
                    Intent intent = new Intent(JokeActivity.this, SensorActivity.class);
                   startActivity(intent);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

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
