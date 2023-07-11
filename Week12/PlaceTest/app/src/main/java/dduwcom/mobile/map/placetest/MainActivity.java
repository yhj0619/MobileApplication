package dduwcom.mobile.map.placetest;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceTypes;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;

import ddwu.mobile.place.placebasic.OnPlaceBasicResult;
import ddwu.mobile.place.placebasic.PlaceBasicManager;
import ddwu.mobile.place.placebasic.pojo.PlaceBasic;
import retrofit2.http.Field;


public class MainActivity extends AppCompatActivity {

    final String TAG = "MainActivity";
    PlaceBasicManager placeBasicManager;
    PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        placeBasicManager = new PlaceBasicManager(getString(R.string.google_api_key));

        placeBasicManager.setOnPlaceBasicResult(new OnPlaceBasicResult() {
            @Override
            public void onPlaceBasicResult(List<PlaceBasic> list) {
                for(PlaceBasic place : list){
                    Log.d(TAG, place.toString());
                }
            }
        });

        Places.initialize(getApplicationContext(),
                            getString(R.string.google_api_key));
        placesClient = Places.createClient(this);
    }

    public void onClick(View v){
        List<Place.Field> placeFields = Arrays.asList(
                Place.Field.ID, Place.Field.NAME,
                Place.Field.PHONE_NUMBER, Place.Field.ADDRESS,
                Place.Field.BUSINESS_STATUS
        );
        FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder("ChIJU6E4aFq7fDUR_So_-u3NFwE", placeFields).build();
                                                                        //오매떡 placeId



        switch (v.getId()){
            case R.id.btnSearch:
                placeBasicManager.searchPlaceBasic(37.604094, 127.042463,100, PlaceTypes.RESTAURANT);
                break;
            case R.id.btnDetail:
                placesClient.fetchPlace(fetchPlaceRequest)
                        .addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                            @Override
                            public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                                Place place = fetchPlaceResponse.getPlace();
                                Log.d(TAG, place.toString());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if(e instanceof ApiException){
                                    ApiException apiException = (ApiException) e;
                                    Log.e(TAG, apiException.toString());
                                }
                            }
                        });
                break;
        }

    }
}