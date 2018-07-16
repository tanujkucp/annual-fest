package sahil.iiitk_foundationday_app.views;
//Made by Tanuj
import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;

import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import sahil.iiitk_foundationday_app.R;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap mGoogleMap;
    List<String> titles=new ArrayList<>();
    List<LatLng> locations=new ArrayList<>();
    float zoom=18;
    int centerLocationIndex=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (googleServicesAvailable()) {
            setContentView(R.layout.activity_map);
            initMap();
        } else {
            // No Google Map
            Toast.makeText(getApplicationContext(), "This version of android doesn't support Google Maps.", Toast.LENGTH_LONG).show();
        }
        //todo add new places here if needed
        titles.add("Prabha Bhawan, IIIT Kota Office");
        titles.add("Annapurna");
        titles.add("Central Lawn");
        titles.add("Library");
        titles.add("Student Activity Center, VLTC");
        titles.add("Playground");
        titles.add("Sports Complex");
        titles.add("Gargi Hostel");
        titles.add("Aurobindo Hostel");
        titles.add("PMC");

        locations.add(new LatLng(26.8638857,75.8106416));
        locations.add(new LatLng(26.863886,75.810642));
        locations.add(new LatLng(26.861956,75.809402));
        locations.add(new LatLng(26.862005,75.808837));
        locations.add(new LatLng(26.863029,75.814487));
        locations.add(new LatLng(26.859971,75.813775));
        locations.add(new LatLng(26.861920,75.813686));
        locations.add(new LatLng(26.864493,75.815075));
        locations.add(new LatLng(26.862742,75.820326));
        locations.add(new LatLng(26.864123,75.812394));
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapfragment);
        mapFragment.getMapAsync(this);
    }

    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS)
            return true;
        else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(getApplicationContext(), "Can't connect to play services", Toast.LENGTH_LONG);
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;
        googleMap.setMinZoomPreference(15);
        googleMap.setMaxZoomPreference(20);
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setAllGesturesEnabled(true);
        //set the retro style of map
       googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapActivity.this,R.raw.retro));
       //set bounds to the map
        LatLngBounds bounds=new LatLngBounds(new LatLng(26.853329,75.806964),new LatLng(26.871391,75.824294));
        googleMap.setLatLngBoundsForCameraTarget(bounds);

        //add markers
        addMarkers();
        zoomToCenter();
    }

    public void addMarkers(){
        for (int i=0;i<locations.size();i++){
            MarkerOptions options = new MarkerOptions().title(titles.get(i))
                    .position(locations.get(i)).snippet("MNIT Jaipur");
            mGoogleMap.addMarker(options);
        }
    }

    public void zoomToCenter(){
        CameraPosition position=new CameraPosition.Builder()
                .target(locations.get(centerLocationIndex))
                .zoom(zoom)
                .tilt(90).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
    }

}
