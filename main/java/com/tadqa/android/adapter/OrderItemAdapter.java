package com.tadqa.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tadqa.android.OrderItemModel;
import com.tadqa.android.R;

import java.util.List;
import java.util.Locale;

public class OrderItemAdapter extends BaseAdapter implements View.OnClickListener {

    List<OrderItemModel> mOrdersList;
    Context mContext;
    Activity mActivity;
//    String CANCEL_ORDER_LINK = "http://43.252.91.43:88/salt_api/CancelOrder";

    public OrderItemAdapter(Activity activity, List<OrderItemModel> ordersList) {
        mOrdersList = ordersList;
        mContext = activity;
        mActivity = activity;
    }

    @Override
    public int getCount() {
        return mOrdersList.size();
    }

    @Override
    public Object getItem(int i) {
        return mOrdersList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.tvCancelItem:
//
//                final int position = (Integer) view.getTag();
//
//                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(final DialogInterface dialogInterface, int i) {
//
//                        final JSONObject parentObject = new JSONObject();
//                        try {
//                            parentObject.put("OrderId", String.valueOf(mOrdersList.get(position).getOrderId()));
//                            JSONArray itemsArray = new JSONArray();
//                            JSONObject itemObject = new JSONObject();
//                            List<Item> item = ParseJson.ofTypeItem(mOrdersList.get(position).getItems());
//                            itemObject.put("Id", item.get(0).getId());
//                            itemObject.put("Color", item.get(0).getColor());
//                            itemObject.put("Size", item.get(0).getSize());
//
//                            itemsArray.put(0, itemObject);
//                            parentObject.put("Items", itemsArray);
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        Perfecto.with(mActivity)
//                                .fromUrl(CANCEL_ORDER_LINK)
//                                .ofTypePost(parentObject)
//                                .connect(new OnRequestComplete() {
//                                    @Override
//                                    public void onSuccess(String s) {
//                                        dialogInterface.dismiss();
//                                        Log.d("Message", parentObject.toString());
//                                        Log.d("Message", s);
//                                        if (s.toLowerCase().contains("success")) {
//                                            Toast.makeText(mContext, "Cancelled", Toast.LENGTH_SHORT).show();
//                                        } else {
//                                            Toast.makeText(mContext, "Cannot cancel this item", Toast.LENGTH_SHORT).show();
//                                        }
//
//                                    }
//
//                                    @Override
//                                    public void onFailure(String s) {
//                                        dialogInterface.dismiss();
//                                    }
//                                });
//                    }
//                });
//                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                });
//                AlertDialog dialog = builder.create();
//                dialog.setMessage("So you want to cancel item?");
//                dialog.show();
//                break;
        }
    }

    static class ItemHolder {
        TextView itemPrice;
        TextView dateTime;
        TextView itemId;
        TextView itemName;
        TextView itemStatus;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ItemHolder holder;

        if (convertView == null) {

            convertView = LayoutInflater.from(mContext).inflate(R.layout.order_row_item, null);


            holder = new ItemHolder();
            holder.itemId = (TextView) convertView.findViewById(R.id.itemId);
            holder.itemName = (TextView) convertView.findViewById(R.id.items);
            holder.dateTime = (TextView) convertView.findViewById(R.id.itemDate);
            holder.itemPrice = (TextView) convertView.findViewById(R.id.itemPrice);
            holder.itemStatus=(TextView)convertView.findViewById(R.id.ordStatus);

            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }

        //String[] name = list.get(position).getItemName().split(",");
        String[] dateTime = mOrdersList.get(position).getOrderDateTime().split("T");

        holder.itemId.setText(mOrdersList.get(position).getId());
        // holder.itemName.setText(name[0].replace("[", "").toString().replace('"', ' ') + " (" + name[1] + ")");
        ;
        holder.itemName.setText(Html.fromHtml(mOrdersList.get(position).getItemName()));
        holder.dateTime.setText(dateTime[0]);
        holder.itemPrice.setText(convertView.getResources().getString(R.string.Rupee) +" "+ String.format(Locale.getDefault(), "%.2f", Float.valueOf(mOrdersList.get(position).getPrice())));
        holder.itemStatus.setText(mOrdersList.get(position).getOrdStatus());
        return convertView;
    }

//    public String getImageUrl(String itemId) {
//        String url = "http://43.252.91.23:68/Salt/";
//        String tail = ".jpg";
//        String id = itemId.replace("p_", "");
//        return url + id + tail;
//    }

//    public String getItemDetailWithPosition(int position) {
////        List<Item> itemList = ParseJson.ofTypeItem(mOrdersList.get(position).getItems());
////        return itemList.get(0).getId() + "|" + itemList.get(0).getColor() + "|" + itemList.get(0).getSize();
//    }

    public void getOrderList() {

    }
}
