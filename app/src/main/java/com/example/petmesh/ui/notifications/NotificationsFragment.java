package com.example.petmesh.ui.notifications;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.petmesh.PetInfo;
import com.example.petmesh.PetList;
import com.example.petmesh.R;
import com.example.petmesh.customlist.CustomListOwnerPetList;
import com.example.petmesh.customlist.CustomListOwnerPetListSearches;
import com.example.petmesh.customlist.CustomSuggestionsAdapter;
import com.example.petmesh.datahelpers.Api;
import com.example.petmesh.datahelpers.Keystore;
import com.example.petmesh.datahelpers.PetSearches;
import com.example.petmesh.datahelpers.Pets;
import com.example.petmesh.datahelpers.RequestHandler;
import com.example.petmesh.datahelpers.Searches;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import kotlin.collections.ArrayDeque;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class NotificationsFragment extends Fragment {
    private List<Searches> lastSearches=new ArrayList<>();
    private NotificationsViewModel notificationsViewModel;

    private CustomSuggestionsAdapter customSuggestionsAdapter;
    Keystore store;
    private MaterialSearchBar searchBar;
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    ArrayList<Searches> Items1=null;
    ArrayList<PetSearches> Items2=null;
    ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;
    static String searchtext="";
    EditText ss;
    HashMap<String, String> params = new HashMap<>();
    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.arg1 == 1)
                Toast.makeText(getActivity().getApplicationContext(),"Network error. Please check your internet and try again.", Toast.LENGTH_LONG).show();
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =new ViewModelProvider(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        store=Keystore.getInstance(root.getContext());
        searchBar = root.findViewById(R.id.searchBar);
        LayoutInflater inflaters= (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        customSuggestionsAdapter = new CustomSuggestionsAdapter(inflaters,searchBar);
        searchBar.setHint("Search...");
        searchBar.setSearchIcon(R.drawable.ic_baseline_search_24);
        searchBar.setCardViewElevation(20);
        searchBar.setRoundedSearchBarEnabled(true);


        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("LOG_TAG", getClass().getSimpleName() + " text changed " + searchBar.getText());
                customSuggestionsAdapter.getFilter().filter(searchBar.getText());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                /*String s = enabled ? "enabled" : "disabled";
                Toast.makeText(getActivity(), "Search " + s, Toast.LENGTH_SHORT).show();*/
                if(enabled){
                searchBar.showSuggestionsList();}
                else{
                searchBar.hideSuggestionsList();}


            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                if(!text.equals("")){
                lastSearches.clear();

                 if(store.get("SearchSuggestionFromDisks")!=null)
                {
                        lastSearches.add(new Searches(store.get("SearchSuggestionFromDisks"),"0"));
                }

                if(lastSearches!=null){
                if(lastSearches.size()>5) {
                    int sizes=lastSearches.size()-5;
                    lastSearches.subList(0,sizes).clear();
                }}

                    lastSearches.add(new Searches(text.toString(),"0"));
                searchBar.setLastSuggestions(lastSearches);

                store.put("SearchSuggestionFromDisks",text.toString());
                Toast.makeText(getActivity(), "Search: " +text, Toast.LENGTH_SHORT).show();
                searchBar.closeSearch();
                searchBar.setPlaceHolder(text);

                params.put("searches", text.toString());
                searchtext=text.toString();

                PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_SEARCH_PETS, params, CODE_POST_REQUEST, getActivity());
                request.execute();
                }
                else
                {
                    searchBar.closeSearch();
                }
                //startSearch(text.toString(), true, null, true);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                Toast.makeText(getActivity(), "Search: " +buttonCode, Toast.LENGTH_SHORT).show();
                switch (buttonCode){
                    case 1:
                        searchBar.openSearch();
                        break;
                }
            }
        });

        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.RECORD_AUDIO},1);
        }

        searchBar.clearSuggestions();
        //restore last queries from disk
        if(store.get("SearchSuggestionFromDisks")!=null)
        {
            if(!store.get("SearchSuggestionFromDisks").equals("")){
                lastSearches.add(new Searches(store.get("SearchSuggestionFromDisks"),"0"));

                params.put("searches", store.get("SearchSuggestionFromDisks"));
            }
            searchBar.setLastSuggestions(lastSearches);
            customSuggestionsAdapter.setSuggestions(lastSearches);
            searchBar.setCustomSuggestionAdapter(customSuggestionsAdapter);

            PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_SEARCH_PETS, params, CODE_POST_REQUEST, getActivity());
            request.execute();
        }
        else
        {
            params.put("searches", "");
            PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_SEARCH_PETS, params, CODE_POST_REQUEST, getActivity());
            request.execute();
        }

        listView=root.findViewById(R.id.l3view);
        swipeRefreshLayout=root.findViewById(R.id.swiperefresh1);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                if(!searchtext.equals("")) {
                    PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_SEARCH_PETS, params, CODE_POST_REQUEST, getActivity());
                    request.execute();
                }
                else
                {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        listView.setOnItemClickListener(this::onItemClick);
        return root;
    }


    public void onItemClick(AdapterView<?> l, View v, int position, long id) {

        final PetSearches iteme = (PetSearches) l.getAdapter().getItem(position);

        store.put("pId",Integer.toString(iteme.getId()));
        store.put("pName",iteme.getName());
        store.put("pMicrochip",iteme.getMicrochip());
        store.put("pBreed",iteme.getBreed());
        store.put("pGender",iteme.getGender());
        store.put("pDateofBirth",iteme.getDateofBirth());
        store.put("pAge",iteme.getAge());
        store.put("pWeight",iteme.getWeight());
        store.put("pCoat",iteme.getCoat());
        store.put("pSpecie",iteme.getSpecie());
        store.put("pAntiRabiesSerialno",iteme.getAntiRabiesSerialno());
        store.put("pAntiRabiesDate",iteme.getAntiRabiesDate());
        store.put("pVaccinationno",iteme.getVaccinationno());
        store.put("pVeterinarian",iteme.getVeterinarian());
        store.put("pPRCandValidity",iteme.getPRCandValidity());
        store.put("pImageData",getStringImage(iteme.getImageData()));
        String ss=getStringImage(iteme.getImageData());
        Intent i=new Intent(getContext(), PetInfo.class);
        startActivity(i);
        (getActivity()).overridePendingTransition(R.anim.slide_right, R.anim.slide_up);
    }
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public class PerformNetworkRequest extends AsyncTask<Void, Void, String> {

        public boolean connection;
        private static final int CODE_GET_REQUEST = 1024;
        private static final int CODE_POST_REQUEST = 1025;

        //the url where we need to send the request
        String url;

        //the parameters
        HashMap<String, String> params;

        //the request code to define whether it is a GET or POST
        int requestCode;
        Context context;
        Activity activity;
        //constructor to initialize values
        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode,Context context) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
            this.context=context;
            this.activity=(Activity)context;
        }

        //when the task started displaying a progressbar
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        //this method will give the response from the request
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                if(this.connection){
                    if(isAdded()){
                    JSONObject object = new JSONObject(s);

                    if (!object.getBoolean("error")) {
                        refreshItemList(object.getJSONArray("users"));
                    }}}
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //the network operation will be performed in background
        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            if(isNetworkAvailable(this.context))
            {
                Log.d("NetworkAvailable","TRUE");
                if(connectGoogle())
                {
                    Log.d("GooglePing","TRUE");
                    connection=true;

                    if (requestCode == CODE_POST_REQUEST)
                        return requestHandler.sendPostRequest(url, params);

                    if (requestCode == CODE_GET_REQUEST)
                        return requestHandler.sendGetRequest(url);
                }
                else
                {
                    Message msg = handler.obtainMessage();
                    msg.arg1 = 1;
                    handler.sendMessage(msg);
                    //Toast.makeText(context.getApplicationContext(), "Network error.", Toast.LENGTH_SHORT).show();
                    Log.d("GooglePing","FALSE");
                    connection=false;
                }
            }
            else {
                Message msg = handler.obtainMessage();
                msg.arg1 = 1;
                handler.sendMessage(msg);
                //Toast.makeText(context.getApplicationContext(), "Network error.", Toast.LENGTH_SHORT).show();
                connection=false;
            }

            return null;
        }


        public boolean isNetworkAvailable(Context context) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                return true;
            }
            return false;
        }

        public boolean connectGoogle() {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("https://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(10000);
                urlc.connect();
                return (urlc.getResponseCode() == 200);

            } catch (IOException e) {

                Log.d("GooglePing","IOEXCEPTION");
                e.printStackTrace();
                return false;
            }
        }
    }
    //ListRefresher
    private void refreshItemList(JSONArray heroes) throws JSONException {

        Items2=new ArrayList<PetSearches>();

        for (int i = 0; i < heroes.length(); i++) {
            JSONObject obj = heroes.getJSONObject(i);
            byte[] decodedString = Base64.decode(obj.getString("ImageData"), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            Items2.add(new PetSearches(
                    obj.getInt("Id"),
                    obj.getString("Name"),
                    obj.getString("Microchip"),
                    obj.getString("Breed"),
                    obj.getString("Gender"),
                    obj.getString("DateofBirth"),
                    obj.getString("Age"),
                    obj.getString("Weight"),
                    obj.getString("Coat"),
                    obj.getString("Specie"),
                    obj.getString("AntiRabiesSerialno"),
                    obj.getString("AntiRabiesDate"),
                    obj.getString("Vaccinationno"),
                    obj.getString("Veterinarian"),
                    obj.getString("PRCandValidity"),
                    decodedByte,
                    "Owner: "+obj.getString("Fullname")
            ));
        }

        CustomListOwnerPetListSearches customListView=new CustomListOwnerPetListSearches(getActivity(),R.layout.customlvperownerpet,Items2);

        listView.setAdapter(customListView);

        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

}