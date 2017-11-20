package com.tadqa.android.util;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.tadqa.android.OrderItemModel;
import com.tadqa.android.R;
import com.tadqa.android.accessibility.SolrData;
import com.tadqa.android.pojo.FoodItem;
import com.tadqa.android.pojo.LocationModel;
import com.tadqa.android.pojo.Outlet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import developer.shivam.perfecto.Printer;

public class ParseJson {

    /**
     * @json is the json string received from server which is parsed
     * and items are added in list and returned.
     */
    public static List<FoodItem> ofFoodItemsSearchList(String json) {
        List<FoodItem> foodItemSearchList = new ArrayList<>();

        try {
            JSONObject parentObject = new JSONObject(json);
            if (parentObject.getInt("Success") == 1) {
                JSONArray docsArray = parentObject.getJSONArray("Menu");

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
                    foodItemSearchList.add(model);
                }

            }
        } catch (JSONException e) {
            Printer.writeError("Error parsing list for search items");
            e.printStackTrace();
        }
        return foodItemSearchList;
    }

    /**
     * @param json is the response that is received from server
     * @return outlet list after parsing json data.
     */
    public static List<Outlet> ofTypeOutlet(String json) {
        List<Outlet> outletList = new ArrayList<>();
        try {
            JSONArray outletJSONArray = new JSONArray(json);
            for (int i = 0; i < outletJSONArray.length(); i++) {
                Outlet outlet = new Outlet();
                JSONObject outletObject = outletJSONArray.getJSONObject(i);
                outlet.setId(outletObject.getString("ID"));
                outlet.setState(outletObject.getString("State"));
                outlet.setCity(outletObject.getString("City"));
                outlet.setPinCode(outletObject.getString("Pincode"));
                outlet.setArea(outletObject.getString("Area"));
                outlet.setContact(outletObject.getString("Contact"));
                if (outletObject.getString("isTHM").equals("TRUE")) {
                    outlet.setHomeMade(true);
                    outlet.setTadqaTandoor(false);
                } else {
                    outlet.setHomeMade(false);
                    outlet.setTadqaTandoor(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outletList;
    }

    public static String[] ofTypeLocation(String json) {
        String[] locationList = new String[0];
        try {
            JSONObject responseJson = new JSONObject(json);
            if (responseJson.getInt("Success") == 1) {
                JSONArray docsArray = responseJson.getJSONArray("Areas");
                locationList = new String[responseJson.getJSONArray("Areas").length()];
                for (int i = 0; i < docsArray.length(); i++) {
                    locationList[i] = docsArray.get(i).toString();
                }
                SolrData.setLocationList(locationList);
            } else {
                Log.d("Error", "Success in not 1");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return locationList;
    }

    public static List<OrderItemModel> ofTypeOrder(String json) {
        List<OrderItemModel> list = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(json);
            Log.d("OrderHistory", json);
            if (object.getInt("Success") == 1) {
                String totalCount = object.getString("TotalCount");
                JSONArray docs = object.getJSONArray("Orders");

                for (int i = 0; i < docs.length(); i++) {
                    OrderItemModel model = new OrderItemModel();
                    model.setId(docs.getJSONObject(i).getString("OrderId"));
                    JSONArray items = docs.getJSONObject(i).getJSONArray("Items");//.split("\\,");
                    String Orderitems = "";
                    StringBuilder stringBuilder = new StringBuilder();
                    int length = items.length();
                    for (int y = 0; y < items.length(); y++) {

                        try {
                            JSONObject rec = items.getJSONObject(y);
                            String arr = "";
                            if (rec.has("ItemName")) {
                                String val = "ItemName : " + rec.getString("ItemName");
                                arr = stringBuilder.append(val).toString();//.append(",").toString();
                            }
                            if (rec.has("Type")) {
                                //String val = " Type : " + rec.getString("Type");
                                String val = rec.getString("Type");
                                if (val.toLowerCase().contains("half")) {
                                    val = "(Half)";
                                    arr = stringBuilder.append(val).append("<br>").toString();
                                } else if (val.toLowerCase().contains("quarter")) {
                                    val = "(Qtr)";
                                    arr = stringBuilder.append(val).append("<br>").toString();
                                } else if (val.toLowerCase().contains("full")) {
                                    arr = stringBuilder.append(",").append("<br>").toString();
                                }
                            }
                            if (rec.has("Qty")) {
                                String val = " Qty : " + rec.getString("Qty");
                                arr = stringBuilder.append(val).append(", ").toString();
                            }
                            if (rec.has("Price")) {
                                String val = "Price : " + rec.getString("Price");
                                arr = stringBuilder.append(val).append("<br>").toString();
                            }

                            //String arr = items.get(y).toString();

                            Orderitems = arr;//stringBuilder.append(arr).append("<br>").toString();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    //Orderitems = Orderitems.replace("[", "").toString().replace('"', ' ').replaceFirst(" ", "");//.substring(0,Orderitems.length()-1);;
                    model.setItemName(Orderitems);

                    model.setOrderDateTime(docs.getJSONObject(i).getString("OrderConfirmationDateTime"));
                    model.setPrice(docs.getJSONObject(i).getString("GrandTotal"));
                    model.setOrdStatus(docs.getJSONObject(i).getString("OrdStatus"));
                    list.add(model);
                }
            }
        } catch (JSONException e) {
            e.getMessage();
        }
        return list;
    }

}
