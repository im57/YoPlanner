package ddwu.mobile.final_project.ma02_20170976.place;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import ddwu.mobile.final_project.ma02_20170976.R;

public class PlaceActivity extends AppCompatActivity {
    public static final String TAG = "PlaActivity";

    private LatLngResultReceiver latLngResultReceiver;

    private GoogleMap mGoogleMap;
    private MarkerOptions options;
    private Marker centerMarker;

    private MarkerOptions newOptions;
    private PolylineOptions pOptions;

    private LatLng loc;

    private static String place = "";
    String name;
    String placeId;
    String vicinity;

   TextView tvSelectPlace;

    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        tvSelectPlace = findViewById(R.id.tvSelectPlace);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        placeId = intent.getStringExtra("placeId");
        vicinity = intent.getStringExtra("vicinity");

        tvSelectPlace.setText(name);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReadyCallBack);

    }

    OnMapReadyCallback mapReadyCallBack = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;

            //주소->위도경도
            latLngResultReceiver = new LatLngResultReceiver(new Handler());
            startLatLngService(vicinity);

            mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    // TODO: 마커의 InfoWindow 클릭 시 이벤트 처리
                    SurroundingsDto dto = new SurroundingsDto();
                    dto.setPlaceID(placeId);

                    Intent intent = new Intent(PlaceActivity.this, SurroundingsDetailActivity.class);
                    intent.putExtra("place", dto);
                    startActivity(intent);
                }
            });

        }
    };

    public void onClick(View v) {
        switch(v.getId()) {
            //show my location
            case R.id.btnMyPath:
                pOptions = new PolylineOptions();
                pOptions.color(Color.RED);
                pOptions.width(5);

                newOptions = new MarkerOptions();
                newOptions.title("내 위치");
                newOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_round));

                if(checkPermission())
                    locationManager.requestLocationUpdates(locationManager.getBestProvider(new Criteria(), true), 100, 10, locationListener);

                break;
        }

    }


    public void onPause(){
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        //show my path
        public void onLocationChanged(Location location) {
            loc = new LatLng(location.getLatitude(), location.getLongitude());

            mGoogleMap.clear();

            pOptions.add(loc);
            mGoogleMap.addPolyline(pOptions);

            newOptions.position(loc);
            centerMarker = mGoogleMap.addMarker(newOptions);
            centerMarker = mGoogleMap.addMarker(options);
            centerMarker.showInfoWindow();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    /* 주소 → 위도/경도 변환 IntentService 실행 */
    private void startLatLngService(String p) {
        Intent intent = new Intent(this, FetchLatLngIntentService.class);
        intent.putExtra(Constants.RECEIVER, latLngResultReceiver);
        intent.putExtra(Constants.ADDRESS_DATA_EXTRA, p);
        startService(intent);
    }

    /* 주소 → 위도/경도 변환 ResultReceiver */
    class LatLngResultReceiver extends ResultReceiver {
        public LatLngResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            String lat;
            String lng;
            ArrayList<LatLng> latLngList = null;

            if (resultCode == Constants.SUCCESS_RESULT) {
                if (resultData == null)
                    return;

                latLngList = (ArrayList<LatLng>) resultData.getSerializable(Constants.RESULT_DATA_KEY);

                if (latLngList == null) {
                    lat = String.valueOf(37.540397);
                    lng = String.valueOf(127.069252999999973);
                } else {
                    LatLng sLatlng = latLngList.get(0);
                    lat = String.valueOf(sLatlng.latitude);
                    lng = String.valueOf(sLatlng.longitude);
                }

            } else {
                lat = String.valueOf(37.540397);
                lng = String.valueOf(127.069252999999973);
            }

            loc = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

            mGoogleMap.clear();
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 17));

            //마커 표시
            options = new MarkerOptions();
            options.position(loc);
            options.title(name);
            centerMarker = mGoogleMap.addMarker(options);
            centerMarker.showInfoWindow();
        }
    }


    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_close, menu);
        return true;
    }

    //choose menu
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.closeMenu:
                Intent intent = new Intent(this, MapActivity.class);
                intent.putExtra("place", place);

                startActivity(intent);
                break;
        }
        return true;
    }

    /* 필요 permission 요청 */
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION },
                        100);
                return false;
            }
        }
        return true;
    }
}

