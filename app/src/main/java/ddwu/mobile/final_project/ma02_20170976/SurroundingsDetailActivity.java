package ddwu.mobile.final_project.ma02_20170976;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

public class SurroundingsDetailActivity extends AppCompatActivity {

    public static final String TAG = "SurroundingsDetailActivity";

    TextView tvCafeName;
    TextView tvVicinity;
    TextView tvPhone;
    TextView tvOpen;
    TextView tvWeek;

    SurroundingsDto surroundingsDto;

    ArrayList<SurroundingsDto.ReviewDTO> review;
    ReviewAdapter reviewAdapter;
    ListView lvReview;

    String query;
    String apiAddress;
    SurroundingsDetailXmlParser parser;

    String address;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surroundingdatail);

        tvCafeName = findViewById(R.id.tvPlaceName);
        tvVicinity = findViewById(R.id.tvVicinity);
        tvPhone = findViewById(R.id.tvPhone);
        tvOpen = findViewById(R.id.tvOpen);
        tvWeek = findViewById(R.id.tvWeek);
        lvReview = findViewById(R.id.lvReview);

        apiAddress = getResources().getString(R.string.sur_detail_api_url);
        parser = new SurroundingsDetailXmlParser();

        Intent intent = getIntent();
        surroundingsDto = (SurroundingsDto)intent.getSerializableExtra("place");
        address = intent.getStringExtra("address");

        review = new ArrayList();
        reviewAdapter = new ReviewAdapter(SurroundingsDetailActivity.this, R.layout.listview_review, review);
        lvReview.setAdapter(reviewAdapter);

        surParsing();
    }

    public void surParsing() {
        //쿼리값으로 장소 id 받아옴
        query = surroundingsDto.getPlaceID();
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
            progressDlg = ProgressDialog.show(SurroundingsDetailActivity.this, "Wait", "Downloading...");
        }
        @Override
        protected String doInBackground(String... strings) {
            String address = strings[0];
            String result = downloadContents(address);
            if (result == null) return "Error!";
            return result;
        }
        @SuppressLint("LongLogTag")
        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, result);

            surroundingsDto = parser.parse(result, surroundingsDto);      // 파싱 수행

            tvCafeName.setText(surroundingsDto.getName());
            tvVicinity.setText(surroundingsDto.getVicinity());
            tvOpen.setText(surroundingsDto.getOpen_now());
            tvPhone.setText(surroundingsDto.getPhone());
            tvWeek.setText(surroundingsDto.getWeekday_text());

            //reviewDTO 받아오고 값 리스트뷰에 보이기
            review = surroundingsDto.getReviewDTO();
            reviewAdapter.setList(review);
            reviewAdapter.notifyDataSetChanged();

//            Log.d("review", review.get(0).getText());
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
        getMenuInflater().inflate(R.menu.menu_place, menu);
        return true;
    }

    //메뉴 항목 선택
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            //지도표시
            case R.id.placeToMap:
                Intent intent = new Intent(this, PlaceActivity.class);
                intent.putExtra("place", address);
                intent.putExtra("name", tvCafeName.getText());
                intent.putExtra("placeId", surroundingsDto.getPlaceID());
                intent.putExtra("vicinity", tvVicinity.getText());

                startActivity(intent);
                break;
            case R.id.closePlaceMenu:
                Intent closeIntent = new Intent(this, MapActivity.class);
                closeIntent.putExtra("place", address);

                startActivity(closeIntent);
                break;
        }
        return true;
    }
}
