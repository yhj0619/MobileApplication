package mobile.example.dbtest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class InsertContactActivity extends Activity {

	private ContactDB contactDB;
	EditText etName;
	EditText etPhone;
	EditText etCategory;
	EditText etAddress;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_insert_contact);

//      DB 인스턴스 생성
		contactDB = ContactDB.getInstance(this);
		
		etName = (EditText)findViewById(R.id.editText1);
		etPhone = (EditText)findViewById(R.id.editText2);
		etCategory = (EditText)findViewById(R.id.editText3);
		etAddress = (EditText)findViewById(R.id.editText4);
	}
	
	
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.btnNewContactSave:
				Contact newContact = new Contact();
				newContact.setName(etName.getText().toString());
				newContact.setPhone(etPhone.getText().toString());
				newContact.setCategory(etCategory.getText().toString());
				newContact.setAddress(etAddress.getText().toString());

				Runnable insertRunnable = new Runnable() {
					@Override
					public void run() {
						contactDB.contactDao().insertAll(newContact);
					}
				};
				new Thread(insertRunnable).start();
				Toast.makeText(getApplicationContext(),"저장완료", Toast.LENGTH_SHORT).show();
				finish();
				break;
			case R.id.btnNewContactClose:
				finish();
				break;
		}

	}
	

}
