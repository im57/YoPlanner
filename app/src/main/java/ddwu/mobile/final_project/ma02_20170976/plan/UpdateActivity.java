package ddwu.mobile.final_project.ma02_20170976.plan;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ddwu.mobile.final_project.ma02_20170976.place.MapActivity;
import ddwu.mobile.final_project.ma02_20170976.R;

public class UpdateActivity extends Activity {

    final static String TAG = "UpdateActivity";
    final int REQ_CODE = 400;
    final int RESULT_ADD = 100;
    final int RESULT_DELETE = 200;
    final int RESULT_CANCELE = 300;

    private static final int REQUEST_TAKE_PHOTO = 200;  // 원본 이미지 사용 요청
    private File photoFile;
    private String mCurrentPhotoPath = "";
    private Uri photoUri;

    ImageView ivUpdatePhoto;

    EditText etContent;
    EditText etDate;
    EditText etPlace;
    EditText etAttendance;

    PlanDBHelper helper;
    long id;

    Boolean addAlarm = false;
    Boolean deleteAlarm = false;

    PendingIntent pendingIntent = null;

    String time = "";
    String alarm = "";
    String repeat = "";
    String interval = "";
    int al = 0;

    String da = "";
    String co = "";
    String pl = "";
    String at = "";

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        ivUpdatePhoto = findViewById(R.id.ivUpdatePhoto);
        etContent = findViewById(R.id.etUpdateContent);
        etDate = findViewById(R.id.etUpdateDate);
        etPlace = findViewById(R.id.etUpdatePlace);
        etAttendance = findViewById(R.id.etUpdateAttendance);

        helper = new PlanDBHelper(this);

        id = getIntent().getLongExtra("id", 0);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        //touch photo area
        ivUpdatePhoto.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    외부 카메라 호출
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    if(takePictureIntent.resolveActivity(getPackageManager()) != null){
                        photoFile = null;

                        try{
                            photoFile = createImageFile();
                        } catch(IOException ex){
                            ex.printStackTrace();
                        }

                        if(photoFile != null){
                            photoUri = FileProvider.getUriForFile(UpdateActivity.this,
                                    "ddwu.mobile.final_project.ma02_20170976",
                                    photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                        }
                    }
                    return true;
                }
                return false;
            }
        });
    }

    /*Change photo size*/
    private void setPic() {
        // Get the dimensions of the View
        int targetW = ivUpdatePhoto.getWidth();
        int targetH = ivUpdatePhoto.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        //bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        ivUpdatePhoto.setImageBitmap(bitmap);
    }

    /*Produce file info*/
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onResume() {
        super.onResume();

        SQLiteDatabase db = helper.getReadableDatabase();

        //Get saved info
        Cursor cursor = db.rawQuery( "select * from " + PlanDBHelper.TABLE_NAME + " where " + PlanDBHelper.COL_ID + "=?", new String[] { String.valueOf(id) });
        while (cursor.moveToNext()) {
            etContent.setText( cursor.getString( cursor.getColumnIndex(PlanDBHelper.COL_CONTENT) ) );
            etDate.setText( cursor.getString( cursor.getColumnIndex(PlanDBHelper.COL_DATE) ) );
            etPlace.setText( cursor.getString( cursor.getColumnIndex(PlanDBHelper.COL_PLACE) ) );
            etAttendance.setText( cursor.getString( cursor.getColumnIndex(PlanDBHelper.COL_ATTENDANCE) ) );
            if(!cursor.getString(cursor.getColumnIndex(PlanDBHelper.COL_PATH)).equals("")){
                mCurrentPhotoPath = cursor.getString(cursor.getColumnIndex(PlanDBHelper.COL_PATH));
                bitmap = BitmapFactory.decodeFile(cursor.getString(cursor.getColumnIndex(PlanDBHelper.COL_PATH)));
                ivUpdatePhoto.setImageBitmap(bitmap);
            }
        }
        cursor.close();
        helper.close();

        if(!co.equals("")){
            etDate.setText(da);
            etContent.setText(co);
            etPlace.setText(pl);
            etAttendance.setText(at);
        }
    }



    public void onClick(View v) {
        AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        String content = etContent.getText().toString();
        String date = etDate.getText().toString();
        String place = etPlace.getText().toString();
        String attend = etAttendance.getText().toString();
        int[] dates;
        char check;

        switch(v.getId()) {
            //Update DB
            case R.id.btnUpdatePlan:
                //Not number
                for(int i = 0; i<date.length(); i++){
                    check = date.charAt(i);
                    if( check < 48 || check > 58)
                    {
                        Toast.makeText(this, "날짜를 정확하게 입력하세요!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                //text length is long
                if(date.length() != 8){
                    Toast.makeText(this, "날짜를 정확하게 입력하세요!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //didn't write
                if(date.equals("")){
                    Toast.makeText(this, "날짜를 입력하세요!", Toast.LENGTH_SHORT).show();
                    return;
                }
                dates = new int[]{Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(4, 6)), Integer.parseInt(date.substring(6, 8))};


                //check date
                if(dates[1] > 12 || dates[1] < 1){
                    Toast.makeText(this, "날짜를 정확하게 입력하세요!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if((dates[1] == 1 || dates[1] == 3 || dates[1] == 5 || dates[1] == 7 || dates[1] == 8 || dates[1] == 10 || dates[1] == 12) && (dates[2] > 31 || dates[2] < 1)){
                    Toast.makeText(this, "날짜를 정확하게 입력하세요!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if((dates[1] == 4 || dates[1] == 6 || dates[1] == 9 || dates[1] == 11) && (dates[2] > 30 || dates[2] < 1)){
                    Toast.makeText(this, "날짜를 정확하게 입력하세요!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(((dates[0] % 4 == 0 && dates[0] % 100 != 0) || dates[0] % 400 == 0) && (dates[1] == 2 ) && (dates[2] > 29 || dates[2] < 1)){
                    Toast.makeText(this, "날짜를 정확하게 입력하세요!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(((dates[0] % 4 != 0 || dates[0] % 100 == 0) && dates[0] % 400 != 0) && (dates[1] == 2 ) && (dates[2] > 28 || dates[2] < 1)){
                    Toast.makeText(this, "날짜를 정확하게 입력하세요!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //didn't write
                if(content.equals("")){
                    Toast.makeText(this, "내용을 입력하세요!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(place.equals("")){
                    Toast.makeText(this, "장소를 입력하세요!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //update db
                SQLiteDatabase db = helper.getWritableDatabase();
                ContentValues row = new ContentValues();
                row.put(PlanDBHelper.COL_CONTENT, content);
                row.put(PlanDBHelper.COL_DATE, date);
                row.put(PlanDBHelper.COL_PLACE, place);
                row.put(PlanDBHelper.COL_ATTENDANCE, attend);
                row.put(PlanDBHelper.COL_PATH, mCurrentPhotoPath);
                Log.d("path: ", mCurrentPhotoPath);

                String whereClause = PlanDBHelper.COL_ID + "=?";
                String[] whereArgs = new String[] { String.valueOf(id) };
                int result = db.update(PlanDBHelper.TABLE_NAME, row, whereClause, whereArgs);

                //add alarm
                if(addAlarm){
                    Intent intentAlarm = new Intent(this, AlarmReceiver.class);

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(dates[0], dates[1]-1, dates[2], Integer.parseInt(time.substring(0,2)), Integer.parseInt(time.substring(2,4)), Integer.parseInt(time.substring(4,6)));

                    //repeat
                    if(repeat.equals("NoRepeat")) {
                        intentAlarm.putExtra("content", alarm);
                        intentAlarm.putExtra("id", id);

                        pendingIntent = PendingIntent.getBroadcast(this, (int) id, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
                        manager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
                        Log.d("arlam: ", "no repeat");
                    }
                    //no repeat
                    if(repeat.equals("Repeat")){
                        intentAlarm.putExtra("content", alarm);
                        intentAlarm.putExtra("id", id);

                        pendingIntent = PendingIntent.getBroadcast(this, (int) id, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
                        manager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), Integer.parseInt(interval) * 100, pendingIntent);
                        Log.d("arlam: ", "repeat");
                    }

                    //add alarm in DB
                    ContentValues rowUpdate = new ContentValues();
                    rowUpdate.put(PlanDBHelper.COL_TIME, time);
                    rowUpdate.put(PlanDBHelper.COL_ALARM, alarm);
                    if(repeat.equals("NoRepeat")){
                        rowUpdate.put(PlanDBHelper.COL_REPEAT, "NoRepeat");
                    }
                    else {
                        rowUpdate.put(PlanDBHelper.COL_REPEAT, "Repeat");
                    }
                    rowUpdate.put(PlanDBHelper.COL_INTERVAL, interval);

                    String whereClauseUpdate = PlanDBHelper.COL_ID + "=?";
                    String[] whereArgsUpdate = new String[] { String.valueOf(id) };

                    db.update(PlanDBHelper.TABLE_NAME, rowUpdate, whereClauseUpdate, whereArgsUpdate);
                }

                //delete alarm
                if(deleteAlarm){
                    Intent intent = new Intent(this, AlarmReceiver.class);
                    pendingIntent = PendingIntent.getBroadcast(this, (int) id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    if (pendingIntent != null)
                        manager.cancel(pendingIntent);

                    //delete alarm in DB
                    ContentValues rowDelete = new ContentValues();
                    rowDelete.put(PlanDBHelper.COL_TIME, "");
                    rowDelete.put(PlanDBHelper.COL_ALARM, "");
                    rowDelete.put(PlanDBHelper.COL_REPEAT, "");
                    rowDelete.put(PlanDBHelper.COL_INTERVAL, "");

                    String whereClauseDelete = PlanDBHelper.COL_ID + "=?";
                    String[] whereArgsDelete = new String[] { String.valueOf(id) };

                    db.update(PlanDBHelper.TABLE_NAME, rowDelete, whereClauseDelete, whereArgsDelete);
                }

                helper.close();

                String msg = result > 0 ? "Updated!" : "Failed!";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(UpdateActivity.this, PlanActivity.class);
                intent.putExtra("date", date);
                startActivity(intent);


                break;
            case R.id.btnUpdatePlanClose:
                //Cancel DB data update
                finish();
                break;
//            case R.id.btnBookmark:
//
//                break;
            case R.id.btnUpdateAlarm:
                //Click Alarm button
                if(date.equals("")){
                    Toast.makeText(this, "날짜입력 후 알람을 설정해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                for(int i = 0; i<date.length(); i++){
                    check = date.charAt(i);
                    if( check < 48 || check > 58)
                    {
                        //Not number
                        Toast.makeText(this, "날짜를 정확하게 입력하세요!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if(date.length() != 8){
                    Toast.makeText(this, "날짜를 정확하게 입력하세요!", Toast.LENGTH_SHORT).show();
                    return;
                }
                dates = new int[]{Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(4, 6)), Integer.parseInt(date.substring(6, 8))};

                if(dates[1] > 12 || dates[1] < 1){
                    Toast.makeText(this, "날짜를 정확하게 입력하세요!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if((dates[1] == 1 || dates[1] == 3 || dates[1] == 5 || dates[1] == 7 || dates[1] == 8 || dates[1] == 10 || dates[1] == 12) && (dates[2] > 31 || dates[2] < 1)){
                    Toast.makeText(this, "날짜를 정확하게 입력하세요!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if((dates[1] == 4 || dates[1] == 6 || dates[1] == 9 || dates[1] == 11) && (dates[2] > 30 || dates[2] < 1)){
                    Toast.makeText(this, "날짜를 정확하게 입력하세요!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(((dates[0] % 4 == 0 && dates[0] % 100 != 0) || dates[0] % 400 == 0) && (dates[1] == 2 ) && (dates[2] > 29 || dates[2] < 1)){
                    Toast.makeText(this, "날짜를 정확하게 입력하세요!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(((dates[0] % 4 != 0 || dates[0] % 100 == 0) && dates[0] % 400 != 0) && (dates[1] == 2 ) && (dates[2] > 28 || dates[2] < 1)){
                    Toast.makeText(this, "날짜를 정확하게 입력하세요!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent aIntent = new Intent(this, AlarmActivity.class);
                aIntent.putExtra("date", etDate.getText().toString());
                aIntent.putExtra("id", id);
                aIntent.putExtra("content", etContent.getText().toString());
                aIntent.putExtra("place", etPlace.getText().toString());
                aIntent.putExtra("attend", etAttendance.getText().toString());

                //put last data entered
                if(al == 1){
                    aIntent.putExtra("time", time);
                    aIntent.putExtra("alarm", alarm);
                    aIntent.putExtra("repeat", repeat);
                    if(repeat.equals("Repeat"))
                        aIntent.putExtra("interval", interval);
                }

                startActivityForResult(aIntent, REQ_CODE);

                break;
            case R.id.btnUpdateMap:
                //Click Map button
                Intent mIntent = new Intent(this, MapActivity.class);
                mIntent.putExtra("place", etPlace.getText().toString());
                startActivity(mIntent);

                break;
            //share plan to Facebook
            case R.id.btnFacebook:
                if(bitmap != null){
                    if (shareDialog.canShow(SharePhotoContent.class)) {
                        SharePhoto photo = new SharePhoto.Builder()
                                .setBitmap(bitmap)
                                .setCaption(etContent.getText().toString())
                                .build();
                        SharePhotoContent sharePhotoContent = new SharePhotoContent.Builder()
                                .addPhoto(photo)
                                .setShareHashtag(new ShareHashtag.Builder()
                                        .setHashtag("#YoPlanner" + "  #Imveloper" +"\n내용: " + content + "\n장소: " + place + "\n날짜: " + date + "\n참석자: " + attend)
                                        .build())
                                .setRef("나의 일정이에요")
                                .build();
                        shareDialog.show(sharePhotoContent);
                    }

                }
                //don't have a photo
                else{
                    Toast.makeText(this, "공유할 사진이 없습니다. 저장 후 사용해 주세요.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result of AlarmActivity
        if (requestCode == REQ_CODE) {
            switch(resultCode) {
                case RESULT_ADD:
                    id = data.getLongExtra("id", 0);
                    time = data.getStringExtra("time");
                    alarm = data.getStringExtra("alarm");
                    repeat = data.getStringExtra("repeat");
                    if(repeat.equals("Repeat"))
                        interval = data.getStringExtra("interval");
                    addAlarm = true;
                    al=1;

                    if(!data.getStringExtra("content").equals("")){
                        da = data.getStringExtra("date");
                        co = data.getStringExtra("content");
                        pl = data.getStringExtra("place");
                        at = data.getStringExtra("attend");
                    }

                    break;
                case RESULT_DELETE:
                    deleteAlarm = true;
                    id = data.getLongExtra("id", 0);

                    if(!data.getStringExtra("content").equals("")){
                        da = data.getStringExtra("date");
                        co = data.getStringExtra("content");
                        pl = data.getStringExtra("place");
                        at = data.getStringExtra("attend");
                    }

                    break;
                case RESULT_CANCELE:
                    id = data.getLongExtra("id", 0);
                    if(!data.getStringExtra("content").equals("")){
                        da = data.getStringExtra("date");
                        co = data.getStringExtra("content");
                        pl = data.getStringExtra("place");
                        at = data.getStringExtra("attend");
                    }

                    break;
            }
        }

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            setPic();
        }
    }
}
