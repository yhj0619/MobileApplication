package mobile.example.dbtest;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class AllContactsActivity extends Activity {
	
	private ListView lvContacts = null;

	private ArrayAdapter<Contact> adapter;
	private ContactDB contactDB;
	private ArrayList<Contact> contactList;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_contacts);

		contactDB = ContactDB.getInstance(this);

		contactList = new ArrayList<Contact>();

		lvContacts = (ListView)findViewById(R.id.lvContacts);
		adapter = new ArrayAdapter<Contact>(this, android.R.layout.simple_list_item_1, contactList);

		lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getApplicationContext(), UpdateContactActivity.class);
				intent.putExtra("contact", contactList.get(position));
				startActivity(intent);
			}
		});

		lvContacts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				Contact delContact = contactList.get(position);

				new Thread() {
					@Override
					public void run() {
						contactDB.contactDao().delete(delContact);
						new ReadDBThread().start();
					}
				}.start();
				adapter.notifyDataSetChanged();

				Toast.makeText(getApplicationContext(), "삭제 완료", Toast.LENGTH_SHORT).show();
				return false;
			}
		});

		lvContacts.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		new ReadDBThread().start();
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ContactDB.destroyInstance();
	}

	private class ReadDBThread extends Thread {
		@Override
		public void run() {
			contactList.clear();
			List<Contact> contacts = contactDB.contactDao().getAllContact();
			for(Contact contact : contacts) {
				contactList.add(contact);
			}
		}
	}
}




