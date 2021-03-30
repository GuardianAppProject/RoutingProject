package ir.guardianapp.routingproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

import ir.guardianapp.routingproject.network.MessageResult;
import ir.guardianapp.routingproject.network.ThreadGenerator;

public class SearchPlacesActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    ListView list;
    ListViewAdapter adapter;
    SearchView editsearch;
    String[] animalNameList;
    ArrayList<SearchPlaces> arraylist = new ArrayList<SearchPlaces>();

    private static ArrayList<SearchPlaces> placesArray = new ArrayList<>();

    public static void setPlacesArray(ArrayList<SearchPlaces> placesArray) {
        SearchPlacesActivity.placesArray.clear();
        SearchPlacesActivity.placesArray.addAll(placesArray);
    }

    private Handler handler;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_places);

         progressBar = findViewById(R.id.progressBar);

        // Generate sample data

        animalNameList = new String[]{"Lion", "Tiger", "Dog",
                "Cat", "Tortoise", "Rat", "Elephant", "Fox",
                "Cow","Donkey","Monkey"};

        // Locate the ListView in listview_main.xml
        list = (ListView) findViewById(R.id.listview);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("doneee");
                SearchPlaces item = adapter.getItem(position);
                if(SelectNavigationActivity.getSourceDest() == SourceDest.NOTHING_SELECTED) {
                    SelectNavigationActivity.setSearchMode(SearchMode.SOURCE);
                    System.out.println("firstt");
                } else if(SelectNavigationActivity.getSourceDest() == SourceDest.SOURCE) {
                    SelectNavigationActivity.setSearchMode(SearchMode.DESTINATION);
                }
                System.out.println("it is " + item.getLatitude() + " and " + item.getLongitude());
                Intent intent = new Intent(SearchPlacesActivity.this, SelectNavigationActivity.class);
//                intent.putExtra("lat", item.getLatitude());
//                intent.putExtra("lon", item.getLongitude());
                SelectNavigationActivity.setSearchLatitude(item.getLatitude());
                SelectNavigationActivity.setSearchLongitude(item.getLongitude());
                startActivity(intent);
            }
        });

        for (int i = 0; i < animalNameList.length; i++) {
//            SearchPlaces searchPlaces = new SearchPlaces(animalNameList[i]);
            // Binds all strings into an array
//            arraylist.add(searchPlaces);
        }

        // Pass results to ListViewAdapter Class
        adapter = new ListViewAdapter(this, placesArray);

        // Binds the Adapter to the ListView
        list.setAdapter(adapter);

        // Locate the EditText in listview_main.xml
        editsearch = (SearchView) findViewById(R.id.search);
        editsearch.setOnQueryTextListener(this);


        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MessageResult.SUCCESSFUL) {
                    ArrayList<String> placesArr = (ArrayList<String>) msg.obj;
                } else {
                    Toast.makeText(getBaseContext(), "Error: make sure your connection is stable", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    Thread requestThread;

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText.trim();
        if (text.length() > 0) {
            progressBar.setVisibility(View.VISIBLE);
            if(requestThread != null)
                requestThread.interrupt();
            requestThread = ThreadGenerator.getPlaces(this, newText, adapter, handler);
            requestThread.start();
        }
        return false;
    }
}