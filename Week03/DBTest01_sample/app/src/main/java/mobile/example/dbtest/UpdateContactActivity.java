package mobile.example.dbtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class UpdateContactActivity extends Activity {

    private ContactDB contactDB;
    EditText etName;
    EditText etPhone;
    EditText etCategory;
    EditText etAddress;
    private Contact updateContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_contact);

//      DB 인스턴스 생성
        contactDB = ContactDB.getInstance(this);

        etName = (EditText)findViewById(R.id.editText1);
        etPhone = (EditText)findViewById(R.id.editText2);
        etCategory = (EditText)findViewById(R.id.editText3);
        etAddress = (EditText)findViewById(R.id.editText4);

        Intent intent = getIntent();
        updateContact = (Contact)intent.getSerializableExtra("contact");

        etName.setText(updateContact.getName());
        etPhone.setText(updateContact.getPhone());
        etCategory.setText(updateContact.getCategory());
        etAddress.setText(updateContact.getAddress());
    }


    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnUpdateContactSave:
                updateContact.setName(etName.getText().toString());
                updateContact.setPhone(etPhone.getText().toString());
                updateContact.setCategory(etCategory.getText().toString());
                updateContact.setAddress(etAddress.getText().toString());

                Runnable insertRunnable = new Runnable() {
                    @Override
                    public void run() {
                        contactDB.contactDao().update(updateContact);
                    }
                };
                new Thread(insertRunnable).start();
                Toast.makeText(getApplicationContext(),"수정완료", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.btnUpdateContactClose:
                finish();
                break;
        }

    }

}