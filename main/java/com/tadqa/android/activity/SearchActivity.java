package com.tadqa.android.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tadqa.android.accessibility.ConvertInputStream;
import com.tadqa.android.accessibility.SolrData;
import com.tadqa.android.ProductDescription;
import com.tadqa.android.R;
import com.tadqa.android.pojo.FoodItem;
import com.tadqa.android.util.API_LINKS;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    private static final int REQ_CODE_SPEECH_INPUT = 200;
    ListView searchListView;
    Toolbar searchBar;
    List<FoodItem> list;
    EditText search;
    SearchAdapter adapter;
    ImageView mic;
    SolrData solrData;
    LinearLayout noResultsLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        /**
         * Getting global list which was created
         *  at the time of Splash Screen.
         */
        list = SolrData.getSearchList();
        adapter = new SearchAdapter(SearchActivity.this, list);
        solrData = (SolrData) getApplicationContext();
        mapping();

        /**
         * If the global search list can be retain then simply populate the
         *  list with adapter else start an async task to regenerate list.
         */
        if (list.size() != 0) {
            searchListView.setAdapter(adapter);
        } else {
            Log.d("Caution!", "List Empty, fetching again!");
            CreateSearchList creation = new CreateSearchList();
            creation.execute();
            searchListView.setAdapter(adapter);
        }

        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                /**
                 * In this method whatsoever query is entered by the
                 *  user is received and list is filtered accordingly.
                 */
                if (search.getText().toString().length() > 2) {
                    searchListView.setVisibility(View.VISIBLE);
                    adapter.filter(search.getText().toString());
                } else {
                    searchListView.setVisibility(View.INVISIBLE);
                }
            }
        });

        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
    }

    private void mapping() {
        searchListView = (ListView) findViewById(R.id.searchResultList);
        searchBar = (Toolbar) findViewById(R.id.action_bar_search);
        search = (EditText) searchBar.findViewById(R.id.editText_search);
        mic = (ImageView) searchBar.findViewById(R.id.voice_search);
        noResultsLinearLayout = (LinearLayout) findViewById(R.id.no_result_linear_layout);
        setSupportActionBar(searchBar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public class SearchAdapter extends BaseAdapter {

        List<FoodItem> foodList;
        List<FoodItem> filteredFoodList;
        Context context;

        public SearchAdapter(Context context, List<FoodItem> list) {
            foodList = list;
            filteredFoodList = new ArrayList<>();
            this.context = context;
        }

        @Override
        public int getCount() {
            return filteredFoodList.size();
        }

        @Override
        public Object getItem(int position) {
            return filteredFoodList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.search_item, null);

            TextView name = (TextView) view.findViewById(R.id.searchItem);
            name.setText(filteredFoodList.get(position).getName());

            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent productDescriptionIntent = new Intent(SearchActivity.this, ProductDescription.class);
                    productDescriptionIntent.putExtra("product_name", filteredFoodList.get(position).getName());
                    productDescriptionIntent.putExtra("product_url", filteredFoodList.get(position).getImageUrl());
                    productDescriptionIntent.putExtra("product_id", filteredFoodList.get(position).getId());
                    productDescriptionIntent.putExtra("product_price", filteredFoodList.get(position).getFullPrice());
                    startActivity(productDescriptionIntent);
                }
            });
            return view;
        }

        public void filter(String enteredText) {

            filteredFoodList.clear();

            if (enteredText.equals("") || enteredText.equals(null)) {
                filteredFoodList.addAll(foodList);
            }

            for (FoodItem model : foodList) {
                if (model.getName().toLowerCase().contains(enteredText.toLowerCase())) {
                    if (!filteredFoodList.contains(enteredText)) {
                        filteredFoodList.add(model);
                    }
                }
            }

            if (filteredFoodList.size() == 0) {
                noResultsLinearLayout.setVisibility(View.VISIBLE);
            } else {
                noResultsLinearLayout.setVisibility(View.INVISIBLE);
            }
            notifyDataSetChanged();
        }
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "No Voice Search Input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case (REQ_CODE_SPEECH_INPUT):
                if (resultCode == RESULT_OK && data != null) {

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    search.setText(result.get(0));
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public class CreateSearchList extends AsyncTask<String, String, String> {

        String response = "";
        String stringToParseForFoodItems = "";
        // String stringToParseForMeals = "";

        @Override
        protected String doInBackground(String... params) {

            try {

                /**
                 * Below code is for A La Carte items.
                 */
                URL foodItemUrl = new URL(API_LINKS.ALL_ITEMS_URL);
                HttpURLConnection foodItemConnection = (HttpURLConnection) foodItemUrl.openConnection();
                foodItemConnection.setRequestMethod("GET");


                foodItemConnection.connect();


                if (foodItemConnection.getResponseCode() == 200) {
                    stringToParseForFoodItems = ConvertInputStream.toString(foodItemConnection.getInputStream());
                    response = "success";
                } else {
                    response = "failed";
                }

            } catch (SocketTimeoutException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equals("success")) {
                if (!stringToParseForFoodItems.equals("")) {

                    try {

                        //Parsing food items list.
                        JSONObject parseJson = new JSONObject(stringToParseForFoodItems);
                        JSONObject responseJson = parseJson.getJSONObject("responseString");
                        JSONArray docsArray = responseJson.getJSONArray("docs");

                        for (int i = 0; i < docsArray.length(); i++) {
                            FoodItem model = new FoodItem();
                            model.setId(docsArray.getJSONObject(i).getString("ItemId"));
                            model.setName(docsArray.getJSONObject(i).getString("ItemName"));

                            if (docsArray.getJSONObject(i).has("ItemHalfPrice")) {
                                model.setHalfPrice(docsArray.getJSONObject(i).getString("ItemHalfPrice"));
                            }
                            if (docsArray.getJSONObject(i).has("ItemFullPrice")) {
                                model.setFullPrice(docsArray.getJSONObject(i).getString("ItemFullPrice"));
                            }
                            if (docsArray.getJSONObject(i).has("ItemQtrPrice")) {
                                model.setQtrPrice(docsArray.getJSONObject(i).getString("ItemQtrPrice"));
                            }
                            model.setImageUrl(docsArray.getJSONObject(i).getString("ImageUrl"));
                            list.add(model);
                        }

                        //Parsing meals list.
//                        JSONObject parseMealsJson = new JSONObject(stringToParseForMeals);
//                        JSONObject responseMealsJson = parseMealsJson.getJSONObject("responseString");
//                        JSONArray docsMealsArray = responseMealsJson.getJSONArray("docs");
//
//                        for (int i = 0; i < docsMealsArray.length(); i++) {
//                            FoodItem model = new FoodItem();
//                            model.setId(docsMealsArray.getJSONObject(i).getString("Itemid"));
//                            model.setName(docsMealsArray.getJSONObject(i).getString("ItemName"));
//
//                            if (docsMealsArray.getJSONObject(i).has("Price")) {
//                                model.setFullPrice(docsMealsArray.getJSONObject(i).getString("Price"));
//                            }
//                            model.setImageUrl(docsMealsArray.getJSONObject(i).getString("ImgUrl"));
//                            list.add(model);
//                        }

                        //Making this search list a global variable
                        SolrData.setSearchList(list);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.d("Connection Result", "Failed");
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
