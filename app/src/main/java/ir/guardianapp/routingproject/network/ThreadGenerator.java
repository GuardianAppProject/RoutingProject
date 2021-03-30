package ir.guardianapp.routingproject.network;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ir.guardianapp.routingproject.ListViewAdapter;
import ir.guardianapp.routingproject.R;
import ir.guardianapp.routingproject.RoutingInformation;
import ir.guardianapp.routingproject.SearchPlaces;
import ir.guardianapp.routingproject.SelectNavigationActivity;
import okhttp3.Response;
import static com.google.android.gms.maps.model.JointType.ROUND;

public class ThreadGenerator {

    private static Polyline blackPolyLine, greyPolyLine;
    private static GoogleMap mMap;
    private static ArrayList<LatLng> pathArr = new ArrayList<>();

    public static Thread getCoinDetail(SelectNavigationActivity activity, GoogleMap mMap, double source_latitude, double source_longitude, double dest_latitude, double dest_longitude, Handler handler){
        return new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("ENTER", "getCoinDetail");
                Response response = Requester.getInstance().RequestBestRoute(source_latitude, source_longitude, dest_latitude, dest_longitude);
                System.out.println(response);
                try {
                    if(response==null) {
                        return;
                    }
                    String routeString = response.body().string();
                    Log.d("routeString", "" + routeString);

                    JSONObject coinJson = new JSONObject(routeString);
                    JSONObject jsonObject = (JSONObject) coinJson.getJSONArray("routes").get(0);
                    JSONObject jsonObject1 = jsonObject.getJSONObject("geometry");
                    double distance = jsonObject.getDouble("distance");
                    double duration = jsonObject.getDouble("duration");
                    JSONArray array = jsonObject1.getJSONArray("coordinates");
                    ArrayList<LatLng> path = new ArrayList<>();
                    for(int i=0; i<array.length(); i++) {
                        String [] map = array.getString(i).split(",");
                        double lng = Double.parseDouble(map[0].substring(1));
                        double lat = Double.parseDouble(map[1].substring(0, map[1].length()-1));
                        path.add(new LatLng(lat, lng));
                    }

                    PolylineOptions options = new PolylineOptions().width(10).geodesic(true);

                    int padding = 280; // padding around start and end marker
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                    final float red_start = 37.0f;
                    final float green_start = 189.0f;
                    final float blue_start = 37.0f;
                    final float red_end = 42.0f;
                    final float green_end = 203.0f;
                    final float blue_end = 255.0f;
                    float redSteps = ((red_end - red_start) / path.size());
                    float greenSteps = ((green_end - green_start) / path.size());
                    float yellowSteps = ((blue_end - blue_start) / path.size());

                    for (int z = 0; z < path.size(); z++) {
                        LatLng point = path.get(z);
                        System.out.println("pathhhh");
                        builder.include(path.get(z));
                        options.add(point);

//                        int redColor = (int) (red ); //- (redSteps * z)
//                        int greenColor = (int) (green ); //- (greenSteps * z)
//                        int yellowColor = (int) (yellow);// - (yellowSteps * z)
//                        Log.e("Color", "" + redColor);
//                        int color = Color.rgb(redColor, greenColor, yellowColor);
//                        options.color(color);
                    }

                    ThreadGenerator.mMap = mMap;
                    ThreadGenerator.pathArr = path;

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.dismissProgressDialog();

                            options.width(10);
                            options.color(Color.rgb(34, 139, 34));
                            options.startCap(new SquareCap());
                            options.endCap(new SquareCap());
                            options.jointType(ROUND);
                            blackPolyLine = mMap.addPolyline(options);

                            PolylineOptions greyOptions = new PolylineOptions();
                            greyOptions.width(10);
                            greyOptions.color(Color.rgb(139, 213, 68));
                            greyOptions.startCap(new SquareCap());
                            greyOptions.endCap(new SquareCap());
                            greyOptions.jointType(ROUND);
                            greyPolyLine = mMap.addPolyline(greyOptions);


//                            mMap.addPolyline(options);
//
//                            LatLngBounds bounds = builder.build();
//                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//                            mMap.animateCamera(cu);

                            animatePolyLine(path, blackPolyLine);

                            LatLngBounds bounds = builder.build();
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                            mMap.animateCamera(cu);

                            TextView arrivalTextView = activity.findViewById(R.id.arrivalTime);
                            arrivalTextView.setText(RoutingInformation.getInstance().calculateArrivalTime(duration));
                            TextView durationTextView = activity.findViewById(R.id.duration);
                            durationTextView.setText(RoutingInformation.getInstance().calculateDuration(duration));
                            TextView distanceTextView = activity.findViewById(R.id.distance);
                            distanceTextView.setText(RoutingInformation.getInstance().calculateDistance(distance));

                            LinearLayout routingInformationBox = activity.findViewById(R.id.routingInformationBox);
                            routingInformationBox.setVisibility(View.VISIBLE);
                            Button button = activity.findViewById(R.id.startTrip);
                            button.setVisibility(View.VISIBLE);
                        }
                    });


                    Message message = new Message();
                    message.what = MessageResult.SUCCESSFUL;
                    handler.sendMessage(message);

                } catch (JSONException | IOException e) {
                    Message message = new Message();
                    message.what = MessageResult.FAILED;
                    handler.sendMessage(message);
                    e.printStackTrace();
                }
            }
        });
    }


    private static void animatePolyLine(ArrayList<LatLng> listLatLng, Polyline blackPolyLine) {

        ValueAnimator animator = ValueAnimator.ofInt(0, 100);
        animator.setDuration(2500);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {

                List<LatLng> latLngList = blackPolyLine.getPoints();
                int initialPointSize = latLngList.size();
                int animatedValue = (int) animator.getAnimatedValue();
                int newPoints = (animatedValue * listLatLng.size()) / 100;

                if (initialPointSize <= newPoints ) {
                    latLngList.addAll(listLatLng.subList(initialPointSize, newPoints));
                    blackPolyLine.setPoints(latLngList);
                }


            }
        });

        animator.addListener(polyLineAnimationListener);
        animator.start();
    }

    static Animator.AnimatorListener polyLineAnimationListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {

            List<LatLng> blackLatLng = blackPolyLine.getPoints();
            List<LatLng> greyLatLng = greyPolyLine.getPoints();

            greyLatLng.clear();
            greyLatLng.addAll(blackLatLng);
            blackLatLng.clear();

            blackPolyLine.setPoints(blackLatLng);
            greyPolyLine.setPoints(greyLatLng);

            blackPolyLine.setZIndex(100);

            animator.start();
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {
            animator.cancel();

        }
    };


    public static Thread getPlaces(Activity activity, String places, ListViewAdapter adapter, Handler handler){
        return new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("ENTER", "getCoinDetail");
                Response response = Requester.getInstance().RequestPlaces(places);
                System.out.println(response);
                try {
                    if(response==null) {
                        return;
                    }
                    String placesString = response.body().string();
                    Log.d("placesString", "" + placesString);

                    JSONArray jsonArray = new JSONArray(placesString);
                    ArrayList<SearchPlaces> placesArray = new ArrayList<>();
                    for(int i=0; i<jsonArray.length() ;i++) {
                        JSONObject placeObject = jsonArray.getJSONObject(i);
                        String displayName = placeObject.getString("display_name");
                        JSONObject address = placeObject.getJSONObject("address");
                        String country = "Iran";
                        if(address.has("country")) {
                            country = address.getString("country");
                        }
                        double longitude = placeObject.getDouble("lon");
                        double latitude = placeObject.getDouble("lat");

                        String city = "";//address.getString("city");
                        String neighbourhood = "neighbourhood";//address.getString("neighbourhood");

                        // use keys() iterator, you don't need to know what keys are there/missing
                        Iterator<String> iter = address.keys();
                        while (iter.hasNext()) {
                            String key = iter.next();
                            String lightObject = address.getString(key);
                            System.out.println("key: " + key + ", OBJECT " + lightObject);
                            neighbourhood = lightObject;
                            break;

                        }


                        if(country.contains("ایران") || country.contains("Iran") || country.contains("IR")) {
                            placesArray.add(new SearchPlaces(neighbourhood, city, displayName, latitude, longitude));
                        }
                    }


                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            mMap.addPolyline(options);
//
//                            LatLngBounds bounds = builder.build();
//                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//                            mMap.animateCamera(cu);
                            adapter.setArraylist(placesArray);
                            adapter.filter();

                            ProgressBar progressBar = activity.findViewById(R.id.progressBar);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });

                    Message message = new Message();
                    message.what = MessageResult.SUCCESSFUL;
                    message.obj = placesArray;
                    handler.sendMessage(message);



                } catch (JSONException | IOException e) {
                    Message message = new Message();
                    message.what = MessageResult.FAILED;
                    handler.sendMessage(message);
                    e.printStackTrace();
                }
            }
        });
    }


}
