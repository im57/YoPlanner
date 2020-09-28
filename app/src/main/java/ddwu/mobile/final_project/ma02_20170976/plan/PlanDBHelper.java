package ddwu.mobile.final_project.ma02_20170976.plan;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PlanDBHelper extends SQLiteOpenHelper {
	
	private final static String DB_NAME = "plan_db";
	public final static String TABLE_NAME = "plan_table";
	public final static String COL_ID = "_id";
    public final static String COL_CONTENT = "content";
	public final static String COL_DATE = "date";
	public final static String COL_PLACE = "place";
	public final static String COL_ATTENDANCE = "attendance";
	public final static String COL_TIME = "time";
	public final static String COL_ALARM = "alarm";
	public final static String COL_REPEAT = "repeat";
	public final static String COL_INTERVAL = "interval";
	public final static String COL_PATH = "path";
	public final static String COL_BOOKMARKNAME = "bookmark_name";
	public final static String COL_BOOKMARKADDRESS = "bookmark_address";



	public PlanDBHelper(Context context) {
		super(context, DB_NAME, null, 1);
	}




	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table " + TABLE_NAME + " ( " + COL_ID + " integer primary key autoincrement,"
				+ COL_CONTENT + " TEXT, " + COL_DATE + " TEXT, " + COL_PLACE + " TEXT, " + COL_ATTENDANCE + " TEXT, " + COL_TIME + " TEXT," + COL_ALARM + " TEXT, " + COL_REPEAT + " TEXT, " + COL_INTERVAL + " TEXT, " + COL_PATH + " TEXT, " + COL_BOOKMARKNAME + " TEXT, " + COL_BOOKMARKADDRESS + " TEXT);");

//		샘플 데이터
		db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '데베프 팀플', '20200823', '동덕여자대학교', '7조 팀원들', '', '', '', '', '', '', '');");
		db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '연극보기', '20200923', '대학로 유니플렉스', '친구', '', '', '', '', '', '', '');");
		db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '알고리즘 과제하기', '20200924', '집', '없음', '', '', '', '', '', '', '');");
		db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '데베프 팀플', '20200922', '동덕여자대학교', '7조 팀원들', '', '', '', '', '', '', '');");
		db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '연극보기', '20200924', '대학로 유니플렉스', '친구', '', '', '', '', '', '', '');");
		db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '알고리즘 과제하기', '20200925', '집', '없음', '', '', '', '', '', '', '');");
		db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '데베프 팀플', '20200923', '동덕여자대학교', '7조 팀원들', '', '', '', '', '', '', '');");
		db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '연극보기', '20200923', '대학로 유니플렉스', '친구', '', '', '', '', '', '', '');");
		db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '알고리즘 과제하기', '20200924', '집', '없음', '', '', '', '', '', '', '');");
		db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '데베프 팀플', '20201022', '동덕여자대학교', '7조 팀원들', '', '', '', '', '', '', '');");
		db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '연극보기', '20200924', '대학로 유니플렉스', '친구', '', '', '', '', '', '', '');");
		db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '알고리즘 과제하기', '20200928', '집', '없음', '', '', '', '', '', '', '');");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + TABLE_NAME);
        onCreate(db);
	}

}
