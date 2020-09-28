package ddwu.mobile.final_project.ma02_20170976.place;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.List;

import ddwu.mobile.final_project.ma02_20170976.MainActivity;
import ddwu.mobile.final_project.ma02_20170976.R;
import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

public class MapActivity extends AppCompatActivity {
    public static final String TAG = "MapActivity";

    private AddressResultReceiver addressResultReceiver;
    private LatLngResultReceiver latLngResultReceiver;

    private GoogleMap mGoogleMap;
    private MarkerOptions options;
    private Marker centerMarker;

    private MarkerOptions markerOptions;

    private static String place = "";

    private LatLng loc;
    static Location location = null;

    EditText etCorR;
    EditText etPlace;

    LocationManager locationManager;

    // TODO: Place 클라이언트 객체 선언
    private PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        etCorR = findViewById(R.id.etCorR);
        etPlace = findViewById(R.id.etPlace);

        Intent intent = getIntent();
        if (intent != null) {
            place = intent.getStringExtra("place");

            if(place != null){
                etPlace.setText(place);
            }
        }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReadyCallBack);

        Places.initialize(getApplicationContext(), getString(R.string.sur_api_url));
        placesClient = Places.createClient(this);

        checkPermission();

        location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

    }

    OnMapReadyCallback mapReadyCallBack = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;

            //주소->위도경도
            latLngResultReceiver = new LatLngResultReceiver(new Handler());
            if(place != null){
                startLatLngService(place);
            }
            else{
                if(checkPermission()){
                    locationManager.requestLocationUpdates(locationManager.getBestProvider(new Criteria(), true), 3000, 10, locationListener);
                }
            }

            mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    // TODO: 마커의 InfoWindow 클릭 시 이벤트 처리
                    String placeId = marker.getTag().toString();

                    SurroundingsDto dto = new SurroundingsDto();
                    dto.setPlaceID(placeId);

                    Intent intent = new Intent(MapActivity.this, SurroundingsDetailActivity.class);
                    intent.putExtra("place", dto);
                    intent.putExtra("address", place);
                    startActivity(intent);
                }
            });

        }
    };

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnPlace:
                mGoogleMap.clear();

                place = etPlace.getText().toString();
                startLatLngService(place);

                break;
            case R.id.btnSurr:
                mGoogleMap.clear();

                options.position(loc);
                centerMarker = mGoogleMap.addMarker(options);
                centerMarker.showInfoWindow();

                if (etCorR.getText().toString().equals("카페")) {
                    new NRPlaces.Builder().listener(placesListener)
                            .key(getString(R.string.sur_client_id))
                            .latlng(loc.latitude, loc.longitude)
                            .radius(200)
                            .type(PlaceType.CAFE)
                            .build()
                            .execute();
                } else if (etCorR.getText().toString().equals("음식점")) {
                    new NRPlaces.Builder().listener(placesListener)
                            .key(getString(R.string.sur_client_id))
                            .latlng(loc.latitude, loc.longitude)
                            .radius(200)
                            .type(PlaceType.RESTAURANT)
                            .build()
                            .execute();
                }
                markerOptions = new MarkerOptions();
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                break;
        }

        Log.d("latitude: ", String.valueOf(loc.latitude));
        Log.d("longitude: ", String.valueOf(loc.longitude));


    }

    // TODO: PlaceListener 구현
    PlacesListener placesListener = new PlacesListener() {
        @Override
        public void onPlacesFailure(PlacesException e) {

        }

        @Override
        public void onPlacesStart() {

        }

        @Override
        public void onPlacesSuccess(final List<Place> places) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    for (noman.googleplaces.Place place : places) {
                        markerOptions.title(place.getName());
                        markerOptions.position(new LatLng(place.getLatitude(), place.getLongitude()));
                        Marker newMarker = mGoogleMap.addMarker(markerOptions);
                        newMarker.setTag(place.getPlaceId());
                        Log.d(TAG, "ID: " + place.getPlaceId());
                    }
                }
            });
        }

        @Override
        public void onPlacesFinished() {

        }
    };

    public void onResume() {
        super.onResume();
        checkPermission();
    }

    public void onPause(){
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        //if change location
        public void onLocationChanged(Location location) {
            loc = new LatLng(location.getLatitude(), location.getLongitude());

            addressResultReceiver = new AddressResultReceiver(new Handler());
            startAddressService(loc);
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

    /* 위도/경도 → 주소 변환 IntentService 실행 */
    private void startAddressService(LatLng l) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, addressResultReceiver);
        intent.putExtra(Constants.LAT_DATA_EXTRA, l.latitude);
        intent.putExtra(Constants.LNG_DATA_EXTRA, l.longitude);
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
                place = "건대입구";
            }

            loc = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

            mGoogleMap.clear();
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 17));

            //마커 표시
            options = new MarkerOptions();
            options.position(loc);
            options.title("도착지 위치");
            centerMarker = mGoogleMap.addMarker(options);
            centerMarker.showInfoWindow();
        }
    }

    /* 위도/경도 → 주소 변환 ResultReceiver */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            String addressOutput = "";

            if (resultCode == Constants.SUCCESS_RESULT) {
                if (resultData == null)
                    return;
                addressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
                if (addressOutput == null)
                    addressOutput = "";
                place = addressOutput;
            } else {
                place = "건대입구";
            }

            mGoogleMap.clear();

            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 17));

            //마커 표시
            options = new MarkerOptions();
            options.position(loc);
            options.title("현재 위치");
            centerMarker = mGoogleMap.addMarker(options);
            centerMarker.showInfoWindow();

            locationManager.removeUpdates(locationListener);

        }
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_find, menu);
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

    //choose menu
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            //find place
            case R.id.findSurroundingsMenu:
                Intent surIntent = new Intent(this, SurroundingsActivity.class);
                surIntent.putExtra("place", place);

                startActivity(surIntent);
                break;
            case R.id.closeMapMenu:
                Intent closeIntent = new Intent(this, MainActivity.class);
                startActivity(closeIntent);
                break;
        }
        return true;
    }
}
