package ir.guardianapp.routingproject;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import java.util.Locale;

import ir.guardianapp.routingproject.network.MessageResult;
import ir.guardianapp.routingproject.network.ThreadGenerator;

public class SelectNavigationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static GoogleMap mMap;
    private static SourceDest sourceDest = SourceDest.NOTHING_SELECTED;
    private Handler handler;
    private static double source_latitude;
    private static double source_longitude;
    private static double dest_latitude;
    private static double dest_longitude;
    private static Marker source_marker = null;
    private static Marker dest_marker = null;

    private static SearchMode searchMode = SearchMode.NOTHING;
    private static double searchLatitude;
    private static double searchLongitude;

    private TextView markerSelectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_navigation);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Geocoder geo = new Geocoder(this, new Locale("fa"));

        String languageToLoad = "fa_";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());



        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MessageResult.SUCCESSFUL) {

                } else {
                    Toast.makeText(getBaseContext(), "Error: make sure your connection is stable", Toast.LENGTH_SHORT).show();
                }
            }
        };

        markerSelectText = findViewById(R.id.markerSelectText);
    }

    @Override
    protected void onResume() {
        super.onResume();

        System.out.println(searchMode);
        if(searchMode != SearchMode.NOTHING) {
            LatLng latLng = new LatLng(searchLatitude, searchLongitude);
            addMarker(latLng);
            System.out.println("hiiiiii");
        }
    }

    public static void setSearchMode(SearchMode searchMode) {
        SelectNavigationActivity.searchMode = searchMode;
    }

    public static void setSearchLatitude(double searchLatitude) {
        SelectNavigationActivity.searchLatitude = searchLatitude;
    }

    public static void setSearchLongitude(double searchLongitude) {
        SelectNavigationActivity.searchLongitude = searchLongitude;
    }

    public static void setDest_marker(Marker dest_marker) {
        SelectNavigationActivity.dest_marker = dest_marker;
    }

    public static void setSource_marker(Marker source_marker) {
        SelectNavigationActivity.source_marker = source_marker;
    }

    public static void setSourceDest(SourceDest sourceDest) {
        SelectNavigationActivity.sourceDest = sourceDest;
    }

    public static SourceDest getSourceDest() {
        return sourceDest;
    }

    public static void setSource_latitude(double source_latitude) {
        SelectNavigationActivity.source_latitude = source_latitude;
    }

    public static void setSource_longitude(double source_longitude) {
        SelectNavigationActivity.source_longitude = source_longitude;
    }

    public static void setDest_latitude(double dest_latitude) {
        SelectNavigationActivity.dest_latitude = dest_latitude;
    }

    public static void setDest_longitude(double dest_longitude) {
        SelectNavigationActivity.dest_longitude = dest_longitude;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission
                    (this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    &&
                    ActivityCompat.checkSelfPermission
                            (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, 1);
                //return;
            } else {

                googleMap.setMyLocationEnabled(true);
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria,false));

                if (location != null)
                {
                    LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(15.8f).build(); ///15.4f
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
//                mMap.setOnMyLocationChangeListener(myLocationChangeListener);
//                googleMap.setMyLocationEnabled(true);
//
//                LatLng loc = new LatLng(31.492370, 74.329060);
//
//                googleMap.addMarker(new MarkerOptions().position(loc).title("I am here"));
//
//                // For zooming automatically to the location of the marker
//                CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(12).build();
//                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }


        // Setting a click event handler for the map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                addMarker(latLng);
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

//        case 1:
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED){
            // permission not granted
        }
        else {
            // permission granted
        }
//        break;
        //default:
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    public Bitmap resizeBitmap(String drawableName,int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(drawableName, "drawable", getPackageName()));
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false);
    }

    public void searchPlaces(View view) {
        Intent intent = new Intent(this, SearchPlacesActivity.class);
        startActivity(intent);
    }

    private AlertDialog alertDialog;

    public void showProgressDialog() {
        ProgressDialog.Builder builder = new ProgressDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.routing_progress, null);

        alertDialog = builder.create();
        alertDialog.setView(view, 0, 0, 0, 0);
        alertDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//        lp.width = 700;
//        lp.height = 800;
        lp.x= (int)0;
        lp.y=(int)0;
        alertDialog.getWindow().setAttributes(lp);
    }

    public void dismissProgressDialog() {
        alertDialog.dismiss();
    }

    private void addMarker(LatLng latLng) {
        // Creating a marker
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting the position for the marker
        markerOptions.position(latLng);

//                Bitmap.Config conf = Bitmap.Config.ARGB_8888;
//                Bitmap bmp = Bitmap.createBitmap(100, 100, conf);
//                BitmapFactory.decodeResource(getResources(), R.drawable.marker_source);

        if(sourceDest == SourceDest.NOTHING_SELECTED) {
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap("marker_source",90,90)));
            source_latitude = latLng.latitude;
            source_longitude = latLng.longitude;
            sourceDest = SourceDest.SOURCE;

            markerOptions.title("مبدا");
            source_marker = mMap.addMarker(markerOptions);
            source_marker.showInfoWindow();

            markerSelectText.setText("لطفا مقصد سفر را انتخاب کنید.");
        } else if(sourceDest == SourceDest.SOURCE) {
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap("flag_dest",80,92)));
            dest_latitude = latLng.latitude;
            dest_longitude = latLng.longitude;
            sourceDest = SourceDest.DESTINATION;

            markerOptions.title("مقصد");
            dest_marker = mMap.addMarker(markerOptions);
            source_marker.showInfoWindow();
            dest_marker.showInfoWindow();

        } else if(sourceDest == SourceDest.DESTINATION) {
            // nothing to do!
        }


        if(sourceDest != SourceDest.DONE) {
            //                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bmp));

            // Setting the title for the marker.
            // This will be displayed on taping the marker
            //latLng.latitude + " : " + latLng.longitude);

            // Clears the previously touched position
//                    mMap.clear();

            // Animating to the touched position
            if(sourceDest != SourceDest.DESTINATION) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13.5f));
            }

            // Placing a marker on the touched position
        } else {
            source_marker.showInfoWindow();
            dest_marker.showInfoWindow();
        }

        if(sourceDest == SourceDest.DESTINATION) {
            sourceDest = SourceDest.DONE;
            showProgressDialog();
            ThreadGenerator.getCoinDetail(SelectNavigationActivity.this, mMap, source_latitude, source_longitude, dest_latitude, dest_longitude, handler).start();
        }
    }
}