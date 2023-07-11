package ddwu.mobile.finalproject.ma02_20200984;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DiaryDBHelper extends SQLiteOpenHelper {

    final static String TAG = "DiartDBHelper";

    final static String DB_NAME = "diary.db";
    public final static String TABLE_NAME = "diary_table";

    public final static String COL_ID = "_id";
    public final static String COL_TITLE = "title";
    public final static String COL_CONTENTS = "contents";
    public final static String COL_DATE = "date";
    public final static String COL_TIME = "time";
    public final static String COL_LOCATION = "location";
    public final static String COL_WEATHER = "weather";

    public DiaryDBHelper(Context context){
        super(context,DB_NAME,null,1);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE " + TABLE_NAME + " (" + COL_ID + " integer primary key autoincrement, " +
                COL_TITLE + " TEXT, " + COL_CONTENTS + " TEXT, " + COL_DATE + " TEXT, " + COL_TIME + " TEXT, "+ COL_LOCATION + " TEXT, " + COL_WEATHER + " TEXT)";
        Log.d(TAG, sql);
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
