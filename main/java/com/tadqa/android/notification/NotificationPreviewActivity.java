package com.tadqa.android.notification;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tadqa.android.R;
import com.tadqa.android.pojo.Notification;
import com.tadqa.android.util.NotificationStorageHelper;

import java.util.ArrayList;
import java.util.List;

public class NotificationPreviewActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar mToolbar;
    RecyclerView rvNotificationList;
    private Context mContext = NotificationPreviewActivity.this;
    List<Notification> notificationList = new ArrayList<>();

    RelativeLayout rlNoNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_preview);

        mToolbar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Notifications");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        rlNoNotification = (RelativeLayout) findViewById(R.id.rlNoNotification);

        rvNotificationList = (RecyclerView) findViewById(R.id.rvNotificationList);

        notificationList = getNotificationList();
        if (notificationList.size() != 0) {
            rvNotificationList.setLayoutManager(new LinearLayoutManager(mContext));
            NotificationAdapter notificationAdapter = new NotificationAdapter(mContext, notificationList);
            rvNotificationList.setAdapter(notificationAdapter);
            ItemTouchHelper.Callback callback = new SwipeHelper(notificationAdapter);
            ItemTouchHelper helper = new ItemTouchHelper(callback);
            helper.attachToRecyclerView(rvNotificationList);
        } else {
            rvNotificationList.setVisibility(View.INVISIBLE);
            rlNoNotification.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {

    }

    public List<Notification> getNotificationList() {
        List<Notification> notificationList = new ArrayList<>();
        NotificationStorageHelper notificationStorageHelper = new NotificationStorageHelper(mContext);
        notificationStorageHelper.open();
        Cursor nCursor = notificationStorageHelper.getNotifications();
        nCursor.moveToFirst();

        for (int i = 0; i < nCursor.getCount(); i++) {
            nCursor.moveToPosition(i);

            Notification notification = new Notification();
            notification.setId(nCursor.getInt(nCursor.getColumnIndex("ID")));
            notification.setMessage(nCursor.getString(nCursor.getColumnIndex("MESSAGE")));

            notificationList.add(notification);
        }
        return notificationList;
    }

    @Override
    public void onBackPressed() {
        Intent upIntent = NavUtils.getParentActivityIntent(this);
        if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
            // This activity is NOT part of this app's task, so create a new task
            // when navigating up, with a synthesized back stack.
            TaskStackBuilder.create(this)
                    // Add all of this activity's parents to the back stack
                    .addNextIntentWithParentStack(upIntent)
                    // Navigate up to the closest parent
                    .startActivities();
        } else {
            // This activity is part of this app's task, so simply
            // navigate up to the logical parent activity.
            NavUtils.navigateUpTo(this, upIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                            // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
            default: return true;
        }
    }

    public class NotificationAdapter extends RecyclerView.Adapter<NotificationHolder> {

        List<Notification> notificationList;
        Context mContext;

        public NotificationAdapter(Context context, List<Notification> list) {
            mContext = context;
            this.notificationList = list;
        }

        @Override
        public NotificationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NotificationHolder(LayoutInflater.from(mContext).inflate(R.layout.notification_layout, null));
        }

        @Override
        public void onBindViewHolder(NotificationHolder holder, int position) {
            holder.tvMessage.setText(notificationList.get(position).getMessage());
        }

        @Override
        public int getItemCount() {
            return notificationList.size();
        }

        public void removeItem(int position) {
            NotificationStorageHelper notificationStorageHelper = new NotificationStorageHelper(mContext);
            notificationStorageHelper.open();
            notificationStorageHelper.removeNotification(notificationList.get(position).getId());
            notificationStorageHelper.close();

            notificationList.remove(position);

            notifyDataSetChanged();
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, notificationList.size());

            if (notificationList.size() == 0) {
                rvNotificationList.setVisibility(View.INVISIBLE);
                rlNoNotification.setVisibility(View.VISIBLE);
            }
        }
    }

    public class NotificationHolder extends RecyclerView.ViewHolder {

        TextView tvMessage;

        NotificationHolder(View itemView) {
            super(itemView);

            tvMessage = (TextView) itemView.findViewById(R.id.tvNotificationMessage);
        }
    }

    public class SwipeHelper extends ItemTouchHelper.SimpleCallback {

        NotificationAdapter adapter;

        public SwipeHelper(NotificationAdapter adapter) {
            super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT);
            this.adapter = adapter;
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            adapter.removeItem(viewHolder.getAdapterPosition());
        }
    }
}
