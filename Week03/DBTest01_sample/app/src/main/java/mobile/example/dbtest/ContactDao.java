package mobile.example.dbtest;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ContactDao {

    @Query("SELECT * FROM contact")
    List<Contact> getAllContact();


    @Insert
    void insertAll(Contact... contacts);

    @Update
    void update(Contact contact);

    @Delete
    void delete(Contact contact);
}
