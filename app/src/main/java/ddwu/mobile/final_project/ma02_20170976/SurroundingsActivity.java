package ddwu.mobile.final_project.ma02_20170976;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class SurroundingsActivity extends AppCompatActivity {

    public static final String TAG = "SurroundingsActivity";

    private LatLngResultReceiver latLngResultReceiver;

    ListView lvList;
    EditText etCorRSur;
    TextView tvPlace;

    String apiAddress;
    String query;
    static String address;

    static String latitude;
    static String longitude;

    SurroundingsAdapter adapter;
    ArrayList<SurroundingsDto> list;
    SurroundingsXmlParser parser;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surroundings);

        latLngResultReceiver = new LatLngResultReceiver(new Handler());

        lvList = findViewById(R.id.lvSurroundings);
        etCorRSur = findViewById(R.id.etCorRSur);
        tvPlace = findViewById(R.id.tvPlace);

        list = new ArrayList();
        adapter = new SurroundingsAdapter(this, R.layout.listview_surroundings, list);
        lvList.setAdapter(adapter);

        parser = new SurroundingsXmlParser();

        Intent getIntent = getIntent();
        address = getIntent.getStringExtra("place");
        if(address == "")
            address = "장소없음";
        tvPlace.setText(address);


        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(SurroundingsActivity.this, SurroundingsDetailActivity.class);
                intent.putExtra("place", list.get(i));
                intent.putExtra("address", address);
                startActivity(intent);
            }
        });
    }

    public void onClick(View v) {

        if (etCorRSur.getText().toString().equals("카페")) {
            apiAddress = getResources().getString(R.string.sur_api_url);
        } else if (etCorRSur.getText().toString().equals("음식점")) {
            apiAddress = getResources().getString(R.string.sur_res_api_url);
        }

        startLatLngService();
    }

    protected void onDestroy() {
        super.onDestroy();
    }


    /* 주소 → 위도/경도 변환 IntentService 실행 */
    private void startLatLngService() {
        Intent intent = new Intent(this, FetchLatLngIntentService.class);
        intent.putExtra(Constants.RECEIVER, latLngResultReceiver);
        intent.putExtra(Constants.ADDRESS_DATA_EXTRA, address);
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
                if (resultData == null) return;
                latLngList = (ArrayList<LatLng>) resultData.getSerializable(Constants.RESULT_DATA_KEY);
                if (latLngList == null) {
                    lat = String.valueOf(37.6068163);
                    lng = String.valueOf(127.04238319999999);
                } else {
                    LatLng sLatlng = latLngList.get(0);
                    lat = String.valueOf(sLatlng.latitude);
                    lng = String.valueOf(sLatlng.longitude);
                }

                //url에 location 값 추가 후 파싱
                query = lat + "," + lng;
                surParsing(query);

            } else {
                latitude = getString(R.string.no_address_found);
                longitude = getString(R.string.no_address_found);
            }
        }
    }


    public void surParsing(String query) {
        try {
            //쿼리값 붙이기
            new surAsyncTask().execute(apiAddress + URLEncoder.encode(query, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    class surAsyncTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDlg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(SurroundingsActivity.this, "Wait", "Downloading...");
        }
        @Override
        protected String doInBackground(String... strings) {
            String address = strings[0];
            String result = downloadContents(address);
            if (result == null) return "Error!";
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, result);

            //버튼 또 누르면 초기화
            list.clear();
            list = parser.parse(result);      // 파싱 수행

            adapter.setList(list);    // Adapter 에 파싱 결과를 담고 있는 ArrayList 를 설정
            adapter.notifyDataSetChanged();

            progressDlg.dismiss();
        }

        /* 네트워크 관련 메소드 */
        /* 네트워크 환경 조사 */
        private boolean isOnline() {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            return (networkInfo != null && networkInfo.isConnected());
        }

        // URLConnection 을 전달받아 연결정보 설정 후 연결, 연결 후 수신한 InputStream 반환
        private InputStream getNetworkConnection(HttpURLConnection conn) throws Exception {
            // 클라이언트 아이디 및 시크릿 그리고 요청 URL 선언
            conn.setReadTimeout(3000);
            conn.setConnectTimeout(3000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setRequestProperty("key", getResources().getString(R.string.sur_client_id));

            if (conn.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + conn.getResponseCode());
            }

            return conn.getInputStream();
        }

        /* InputStream을 전달받아 문자열로 변환 후 반환 */
        protected String readStreamToString(InputStream stream){
            StringBuilder result = new StringBuilder();

            try {
                InputStreamReader inputStreamReader = new InputStreamReader(stream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String readLine = bufferedReader.readLine();

                while (readLine != null) {
                    result.append(readLine + "\n");
                    readLine = bufferedReader.readLine();
                }

                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result.toString();
        }
        /* 주소(address)에 접속하여 문자열 데이터를 수신한 후 반환 */
        protected String downloadContents(String address) {
            HttpURLConnection conn = null;
            InputStream stream = null;
            String result = null;

            try {
                URL url = new URL(address);
                conn = (HttpURLConnection)url.openConnection();
                stream = getNetworkConnection(conn);
                result = readStreamToString(stream);
                if (stream != null) stream.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) conn.disconnect();
            }
            return result;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_close, menu);
        return true;
    }

    //메뉴 항목 선택
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.closeMenu:
                Intent intent = new Intent(this, MapActivity.class);
                intent.putExtra("place", address);

                startActivity(intent);
                break;
        }
        return true;
    }

}
