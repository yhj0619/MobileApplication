package ddwu.mobile.finalproject.ma02_20200984;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.provider.BaseColumns;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

public class DiaryDBManager {
    DiaryDBHelper diaryDBHelper = null;
    Cursor cursor = null;
    ArrayList diaryList;
    public DiaryDBManager(Context context) {diaryDBHelper = new DiaryDBHelper(context);}

    //    DB의 모든 diary 반환
    public ArrayList<Diary> getAllDiary() {
        diaryList = new ArrayList();
        SQLiteDatabase db = diaryDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + diaryDBHelper.TABLE_NAME, null);

        while(cursor.moveToNext()) {
            long id = cursor.getInt(cursor. getColumnIndexOrThrow(diaryDBHelper.COL_ID));
            String title = cursor.getString(cursor. getColumnIndexOrThrow(diaryDBHelper.COL_TITLE));
            String contents = cursor.getString(cursor. getColumnIndexOrThrow(diaryDBHelper.COL_CONTENTS));
            String date = cursor.getString(cursor. getColumnIndexOrThrow(diaryDBHelper.COL_DATE));
            String time = cursor.getString(cursor. getColumnIndexOrThrow(diaryDBHelper.COL_TIME));
            String location = cursor.getString(cursor. getColumnIndexOrThrow(diaryDBHelper.COL_LOCATION));
            String weather = cursor.getString(cursor. getColumnIndexOrThrow(diaryDBHelper.COL_WEATHER));
            diaryList.add ( new Diary (id, title, contents, date, time, location, weather) );
        }

        cursor.close();
        diaryDBHelper.close();
        return diaryList;
    }

    //    DB 에 새로운 diary 추가
    public boolean addNewDiary(Diary newDiary) {
        SQLiteDatabase db = diaryDBHelper.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(DiaryDBHelper.COL_TITLE, newDiary.getTitle());
        value.put(DiaryDBHelper.COL_CONTENTS, newDiary.getContents());
        value.put(DiaryDBHelper.COL_DATE, newDiary.getDate());
        value.put(DiaryDBHelper.COL_TIME, newDiary.getTime());
        value.put(DiaryDBHelper.COL_LOCATION, newDiary.getLocation());
        value.put(DiaryDBHelper.COL_WEATHER, newDiary.getWeather());
        //      insert 메소드를 사용할 경우 데이터 삽입이 정상적으로 이루어질 경우 1 이상, 이상이 있을 경우 0 반환 확인 가능
        long count = db.insert(DiaryDBHelper.TABLE_NAME, null, value);

        diaryDBHelper.close();
        if (count > 0) return true;
        return false;
    }

    //    _id 를 기준으로 diary 의 EditText 내용 변경
    public boolean modifyDiary(Diary diary) {
        SQLiteDatabase sqLiteDatabase = diaryDBHelper.getWritableDatabase();
        ContentValues row = new ContentValues();
        row.put(DiaryDBHelper.COL_TITLE, diary.getTitle());
        row.put(DiaryDBHelper.COL_CONTENTS, diary.getContents());
        row.put(DiaryDBHelper.COL_DATE, diary.getDate());
        row.put(DiaryDBHelper.COL_TIME, diary.getTime());
        row.put(DiaryDBHelper.COL_LOCATION, diary.getLocation());
        row.put(DiaryDBHelper.COL_WEATHER, diary.getWeather());
        String whereClause = DiaryDBHelper.COL_ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(diary.get_id()) };
        int result = sqLiteDatabase.update(DiaryDBHelper.TABLE_NAME, row, whereClause, whereArgs);
        diaryDBHelper.close();
        if (result > 0) return true;
        return false;
    }

    //    _id 를 기준으로 DB에서 diary 삭제
    public boolean removeDiary(long id) {
        SQLiteDatabase sqLiteDatabase = diaryDBHelper.getWritableDatabase();
        String whereClause = DiaryDBHelper.COL_ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(id) };
        int result = sqLiteDatabase.delete(DiaryDBHelper.TABLE_NAME, whereClause,whereArgs);
        diaryDBHelper.close();
        if (result > 0) return true;
        return false;
    }


    public ArrayList<Diary> readRecordOrderByDate() {
        diaryList = new ArrayList();
        SQLiteDatabase db = diaryDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + diaryDBHelper.TABLE_NAME + "ORDER BY " + diaryDBHelper.COL_DATE + "DESC", null);

        while(cursor.moveToNext()) {
            long id = cursor.getInt(cursor. getColumnIndexOrThrow(diaryDBHelper.COL_ID));
            String title = cursor.getString(cursor. getColumnIndexOrThrow(diaryDBHelper.COL_TITLE));
            String contents = cursor.getString(cursor. getColumnIndexOrThrow(diaryDBHelper.COL_CONTENTS));
            String date = cursor.getString(cursor. getColumnIndexOrThrow(diaryDBHelper.COL_DATE));
            String time = cursor.getString(cursor. getColumnIndexOrThrow(diaryDBHelper.COL_TIME));
            String location = cursor.getString(cursor. getColumnIndexOrThrow(diaryDBHelper.COL_LOCATION));
            String weather = cursor.getString(cursor. getColumnIndexOrThrow(diaryDBHelper.COL_WEATHER));


            diaryList.add ( new Diary (id, title, contents, date, time, location, weather) );
        }

        cursor.close();
        diaryDBHelper.close();
        return diaryList;
    }

    //    close 수행
    public void close() {
        if (diaryDBHelper != null) diaryDBHelper.close();
        if (cursor != null) cursor.close();
    };
}
