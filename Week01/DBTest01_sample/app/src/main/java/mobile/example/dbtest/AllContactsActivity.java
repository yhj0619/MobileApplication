package mobile.example.dbtest;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AllContactsActivity extends Activity {
	
	private ListView lvContacts = null;

	private ArrayAdapter<ContactDto> adapter;
	private ContactDBHelper helper;
	private ArrayList<ContactDto> contactList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_contacts);

		helper = new ContactDBHelper(this);
		contactList = new ArrayList<ContactDto>();

		lvContacts = (ListView)findViewById(R.id.lvContacts);
		adapter = new ArrayAdapter<ContactDto>(this, android.R.layout.simple_list_item_1, contactList);

		lvContacts.setAdapter(adapter);
	}



	@Override
	protected void onResume() {
		super.onResume();

		SQLiteDatabase db = helper.getReadableDatabase();

		Cursor cursor = db.rawQuery("select * from " + ContactDBHelper.TABLE_NAME, null);

		contactList.clear(); //이전 내용 보관 안되도록.

		while(cursor.moveToNext()){
			ContactDto dto = new ContactDto();
			dto.setId(cursor.getInt(cursor.getColumnIndex("_id")));
			dto.setName(cursor.getString(cursor.getColumnIndex(ContactDBHelper.COL_NAME)));
			dto.setPhone(cursor.getString(cursor.getColumnIndex(ContactDBHelper.COL_PHONE)));
			dto.setCategory(cursor.getString(cursor.getColumnIndex(ContactDBHelper.COL_CAT)));
			contactList.add(dto); //DTO에 있는 toString 형태로 출력

		}
		adapter.notifyDataSetChanged(); //화면 작업

		cursor.close();
		helper.close();
	}

}




