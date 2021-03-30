package ir.guardianapp.routingproject.network;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Requester {

    private static Requester requester;
    private Requester(){}

    public static Requester getInstance(){
        if(requester==null) requester = new Requester();
        return requester;
    }

    public Response RequestBestRoute(double source_latitude, double source_longitude, double dest_latitude, double dest_longitude){
        OkHttpClient okHttpClient = new OkHttpClient();
        // http://router.project-osrm.org/route/v1/driving/51.404343,35.715298;50.99155,35.83266?geometries=geojson
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://router.project-osrm.org/route/v1/driving/" + source_longitude + "," + source_latitude + ";" + dest_longitude + "," + dest_latitude + "?geometries=geojson&overview=full")
                .newBuilder();
        String url = urlBuilder.build().toString();
        final Request request = new Request.Builder().url(url)
                .build();
        try {
            return okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Response RequestPlaces(String places){
        OkHttpClient okHttpClient = new OkHttpClient();
        // https://nominatim.openstreetmap.org/search?q=سالاریه&format=json&addressdetails=1
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://nominatim.openstreetmap.org/search?q=" + places + "&format=json&addressdetails=1")
                .newBuilder();
        String url = urlBuilder.build().toString();
        final Request request = new Request.Builder().url(url)
                .build();
        try {
            return okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
