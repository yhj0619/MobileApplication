package mobile.example.dbtest;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class InsertContactActivity extends Activity {

	ContactDBHelper helper;
	EditText etName;
	EditText etPhone;
	EditText etCategory;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_insert_contact);

//      DBHelper 생성
		helper = new ContactDBHelper(this);
		
		etName = (EditText)findViewById(R.id.editText1);
		etPhone = (EditText)findViewById(R.id.editText2);
		etCategory = (EditText)findViewById(R.id.editText3);
	}
	
	
	public void onClick(View v) {
		SQLiteDatabase db = helper.getWritableDatabase();

		ContentValues row = new ContentValues();
		row.put(ContactDBHelper.COL_NAME,etName.getText().toString());
		row.put(ContactDBHelper.COL_PHONE,etPhone.getText().toString());
		row.put(ContactDBHelper.COL_CAT,etCategory.getText().toString());

		db.insert(ContactDBHelper.TABLE_NAME,null,row); // 결과의 반환값 있음-> log띄우기 가능

//		db.execSQL("insert into" + ContactDBHelper.TABLE_NAME + " values (NULL, '"
//				+ etName.getText().toString() + "','" + etPhone.getText().toString() + "','" + etCategory.getText().toString() + ")");
		//반환값 없기에 try-catch 로 처리

		helper.close();
	}
}
