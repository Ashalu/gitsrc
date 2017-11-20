package com.tadqa.android.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.tadqa.android.util.API_LINKS;
import com.tadqa.android.util.ConnectionDetector;
import com.tadqa.android.accessibility.ConvertInputStream;
import com.tadqa.android.accessibility.SolrData;
import com.tadqa.android.pojo.FaqCollection;
import com.tadqa.android.R;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Help extends AppCompatActivity {
    private AnimatedExpandableListView listView;
    private ExampleAdapter adapter;
    SolrData solrdata;
    ConnectionDetector condetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        condetector=new ConnectionDetector(Help.this);
        if (ConnectionDetector.isConnectingToInternet(this)) {
        setContentView(R.layout.help);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if(toolbar!=null) {
            toolbar.setTitle("Help");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

         solrdata = (SolrData) getApplicationContext();
         //url=solrData.solrUrl+"FAQ_URL/select?q=*%3A*&rows=12&wt=json&indent=true";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        getdata(API_LINKS.FAQ_URL);

        // In btnOrder to show animations, we need to use a custom click handler
        // for our ExpandableListView.
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // We call collapseGroupWithAnimation(int) and
                // expandGroupWithAnimation(int) to animate group
                // expansion/collapse.
                if (listView.isGroupExpanded(groupPosition)) {
                    listView.collapseGroupWithAnimation(groupPosition);
                } else {
                    listView.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }

        });
        } else {
            setContentView(R.layout.netconnection);
        }
    }

    protected void getdata(String url)
    {
        try
        {
            List<GroupItem> items = new ArrayList<GroupItem>();
            datalist= getOrderDetailsFrmSolr(url);


            for(int i = 1; i < datalist.size(); i++) {
                GroupItem item = new GroupItem();
                item.title=datalist.get(i).FaqQuestion;
                ChildItem child = new ChildItem();
                child.title = datalist.get(i).FaqAnswer;
                item.items.add(child);
                items.add(item);
            }

            adapter = new ExampleAdapter(this);
            adapter.setData(items);

            listView = (AnimatedExpandableListView) findViewById(R.id.expandable_lv_social_list_view);
            listView.setAdapter(adapter);
        }
        catch(Exception e)
        {}
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private static class GroupItem {
        String title;
        List<ChildItem> items = new ArrayList<ChildItem>();
    }

    private static class ChildItem {
        String title;
        String hint;
    }

    private static class ChildHolder {
        TextView title;
        TextView hint;
    }

    private static class GroupHolder {
        TextView title;
    }

    public List<FaqCollection> datalist =new ArrayList<>();


    public List<FaqCollection> getOrderDetailsFrmSolr(String url) {

        HttpURLConnection connection;
        String response = "";

        InputStream instream = null;
        JSONObject jObj = null;
        String json = "";
        try {

            URL starterUrl = new URL(url);
            connection = (HttpURLConnection) starterUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            if (connection.getResponseCode() == 200)
            {
                instream = new BufferedInputStream(connection.getInputStream());
                response = ConvertInputStream.toString(instream);


            } else {

                condetector.isServerNotRunning();
                return null;
            }


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
           if(e.getMessage().contains("failed to connect"))
           {
               new ConnectionDetector(Help.this).isServerNotRunning();
           }
        }

        //parse string to jsonObject
        try {


            jObj = new JSONObject(response);

            //JSONObject obj2 = jObj.getJSONObject("responseString");
            JSONArray array = (JSONArray) jObj.getJSONArray("FAQ");
            for (int i = 0; i < array.length(); i++) {

                JSONObject obj = array.getJSONObject(i);
                FaqCollection data =new FaqCollection();
                data.setTitle(obj.getString("Question"));
                data.setDescription(obj.getString("Answer"));

                datalist.add(data);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return datalist;
    }



    /**
     * Adapter for our listOne of {@link GroupItem}s.
     */
    private class ExampleAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
        private LayoutInflater inflater;
        //Typeface custom_font = Typeface.createFromAsset(getResources().getAssets(), "fonts/Roboto-Light.ttf");

        private List<GroupItem> items;

        public ExampleAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void setData(List<GroupItem> items) {
            this.items = items;
        }

        @Override
        public ChildItem getChild(int groupPosition, int childPosition) {
            return items.get(groupPosition).items.get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildHolder holder;
            ChildItem item = getChild(groupPosition, childPosition);
            if (convertView == null) {
                holder = new ChildHolder();
                convertView = inflater.inflate(R.layout.list_item_expandable_social_child, parent, false);
                holder.title = (TextView) convertView.findViewById(R.id.expandable_item_social_child_name);

                convertView.setTag(holder);
            } else {
                holder = (ChildHolder) convertView.getTag();
            }

            holder.title.setText(item.title.replaceAll("[-+.^:]", ""));

            condetector.setTypeFont(holder.title);

            return convertView;
        }

        @Override
        public int getRealChildrenCount(int groupPosition) {
            return items.get(groupPosition).items.size();
        }

        @Override
        public GroupItem getGroup(int groupPosition) {
            return items.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return items.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupHolder holder;
            GroupItem item = getGroup(groupPosition);
            if (convertView == null) {
                holder = new GroupHolder();
                convertView = inflater.inflate(R.layout.list_item_expandable_social, parent, false);
                holder.title = (TextView) convertView.findViewById(R.id.expandable_item_social_name);
                convertView.setTag(holder);
            } else {
                holder = (GroupHolder) convertView.getTag();
            }

            holder.title.setText(item.title.replaceAll("[-+.^:]", ""));

            //holder.title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (int) getResources().getDimension(R.dimen.font_size15));
            condetector.setTypeFont(holder.title);

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int arg0, int arg1) {
            return true;
        }

    }

}

