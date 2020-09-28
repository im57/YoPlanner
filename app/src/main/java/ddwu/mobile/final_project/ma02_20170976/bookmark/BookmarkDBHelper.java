package ddwu.mobile.final_project.ma02_20170976.bookmark;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookmarkDBHelper extends SQLiteOpenHelper {
	
	private final static String DB_NAME = "bookmark_db";
	public final static String TABLE_NAME = "bookmark_table";
	public final static String COL_ID = "_id";
	public final static String COL_NAME = "name";
	public final static String COL_ADDRESS = "address";



	public BookmarkDBHelper(Context context) {
		super(context, DB_NAME, null, 1);
	}




	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table " + TABLE_NAME + " ( " + COL_ID + " integer primary key autoincrement,"
				+ COL_NAME + " TEXT, " + COL_ADDRESS + " TEXT);");

//		샘플 데이터
		db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '압구정', '서울특별시 강남구 압구정로 지하 172');");
		db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '학교', '서울 성북구 화랑로13길 60');");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + TABLE_NAME);
        onCreate(db);
	}

}
