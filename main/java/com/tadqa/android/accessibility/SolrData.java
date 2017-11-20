package com.tadqa.android.accessibility;

import android.app.Application;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tadqa.android.pojo.BannerModel;
import com.tadqa.android.pojo.FoodItem;
import com.tadqa.android.pojo.LocationModel;
import com.tadqa.android.pojo.MenuModel;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SolrData extends Application {

    public SolrData() {
        searchList = new ArrayList<>();

    }

    public int locationPosition = 0;
    static List<FoodItem> searchList;
    static String[] locationList;
    public List<LocationModel> userSelectedDeliveryAreaByOutLetList;
    public String[] selectedDeliveryLocationArray = new String[100];

    public static String[] getLocationList() {
        return locationList;
    }

    public static void setLocationList(String[] locationList) {
        SolrData.locationList = locationList;
    }

    public static List<FoodItem> getSearchList() {
        return searchList;
    }

    public static void setSearchList(List<FoodItem> searchList) {
        SolrData.searchList = searchList;
    }


    public boolean isTTEClose = false;
    public boolean isTHMClose = false;
    String UserName = "";
    String UserNumber = "";
    String UserAddress = "";
    String OtpCode = "";
    String areaPincode = "";
    String UserEmail = "";
    String UserDOB = "";
    String UserGender = "";

    public boolean isAlreadyExists = false;
    public boolean isEnterFirstTime = false;


    public String getUserName() {
        return UserName;
    }

    public void setUserName(String name) {
        UserName = name;
    }

    public String getUserNumber() {
        return UserNumber;
    }

    public void setUserNumber(String no) {
        UserNumber = no;
    }

    public String getUserAddress() {
        return UserAddress;
    }

    public void setUserAddress(String add) {
        UserAddress = add;
    }

    public String getOtpCode() {
        return OtpCode;
    }

    public void setOtpCode(String otp) {
        OtpCode = otp;
    }

    public String getareaPincode() {
        return areaPincode;
    }

    public void setareaPincode(String pin) {
        areaPincode = pin;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String email) {
        UserEmail = email;
    }

    public String getUserDOB() {
        return UserDOB;
    }

    public void setUserDOB(String dob) {
        UserDOB = dob;
    }

    public String getUserGender() {
        return UserGender;
    }

    public void setUserGender(String gender) {
        UserGender = gender;
    }

    /**
     * Order Details
     *  @orderId unique id that is generated from server
     *      for every new order
     *  @grandTotal total of order amount included taxes and vat
     *  @subTotal total of prices of all items
     *  @vatAmount Value Added Tax on item
     *  @serviceTaxAmount Service tax on subTotal
     */
    public String checkoutSelectedTime = "";
    public String orderId = "";
    public double grandTotal = 0;
    public double subTotal = 0;
    public double serviceTax = 0;
    public double vat = 0;
    public int discountAmount = 0;

    public double getGrandTotal() {

      return (subTotal-discountAmount)+(subTotal*serviceTax/100)+(subTotal*vat/100);
//        return (subTotal - discountAmount) + ((subTotal - discountAmount) * serviceTax / 100) + ((subTotal - discountAmount) * vat / 100);
    }

    public double getGrandTotalBeforeAddingDiscound() {
        return subTotal + (subTotal * serviceTax / 100) + (subTotal * vat / 100);
    }

    public JSONArray checkoutItemsArr;

    public String getcheckoutSelectedTime() {
        return checkoutSelectedTime;
    }

    public void setcheckoutSelectedTime(String time) {
        checkoutSelectedTime = time;
    }

    public JSONArray getcheckoutItemsArr() {
        return checkoutItemsArr;
    }

    public void setcheckoutItemsArr(JSONArray items) {
        checkoutItemsArr = items;
    }

    public String getOrderID() {
        return orderId;
    }

    public void setOrderID(String id) {
        orderId = id;
    }

    int currentMenuPosition = 0;
    final int VEG_STARTER_CODE = 1;
    final int NON_VEG_STARTER_CODE = 2;
    final int BREADS_CODE = 3;
    final int ROLLS_CODE = 4;
    final int RICE_CODE = 5;
    final int PLATTERS_CODE = 6;
    final int VEG_MAIN_COURSE_CODE = 7;
    final int NON_VEG_MAIN_COURSE_CODE = 8;
    final int EXTRAS = 9;
    final int Meal = 10;
    final int Paratha = 11;
    final int Paneer = 12;
    final int Roll = 13;
    final int VegSection = 14;
    final int ChickenSection = 15;
    public static String START = "start=";
    public static String THRESHOLD = "&max=";
    static boolean isLoggedIn;
    public boolean isHomeMade = false;
    public boolean isTadqaTandoorExpress = false;
    public String locationName = "";


    public static boolean isChooseLocalityOnTop() {
        return isChooseLocalityOnTop;
    }

    public static void setIsChooseLocalityOnTop(boolean isChooseLocalityOnTop) {
        SolrData.isChooseLocalityOnTop = isChooseLocalityOnTop;
    }

    static boolean isChooseLocalityOnTop = false;

    public static String baseUrl = "http://43.252.91.43:98/admin_tadqa/";
    public ArrayList<String> aboutData = new ArrayList<>();
    public String MenuUrl = baseUrl + "getMenuByCategoryId/?ID=";//catg01&start=0&max=10";



    public void setToast(Toast toast) {
        LinearLayout linearLayout = (LinearLayout) toast.getView();
        TextView messageTextView = (TextView) linearLayout.getChildAt(0);
        messageTextView.setTextSize(13);
    }

    static public boolean getLoggedInStatus() {
        return isLoggedIn;
    }

    static public void setLoggedInStatus(boolean status) {
        SolrData.isLoggedIn = status;
    }


    static List<MenuModel> Meals = new ArrayList<>();

    static public List<MenuModel> getMealsList() {
        return Meals;
    }

    static public void setMealsList(List<MenuModel> meals) {
        SolrData.Meals = meals;
    }

    static List<FoodItem> VegStarters = new ArrayList<>();
    static List<MenuModel> MenuData = new ArrayList<>();

    static public List<FoodItem> getVegstarterList() {
        return VegStarters;
    }

    static public List<MenuModel> getMenuList() {
        return MenuData;
    }

    static List<BannerModel> banners = new ArrayList<>();

    static public List<BannerModel> getBannerList() {
        return banners;
    }


    static public void setVegstarterList(List<FoodItem> vegstarter) {
        SolrData.VegStarters = vegstarter;
    }

    static public void setMenuList(List<MenuModel> menu) {
        SolrData.MenuData = menu;
    }

    //static public

    static List<FoodItem> NonVegStarters = new ArrayList<>();

    static public List<FoodItem> getNonVegstarterList() {
        return NonVegStarters;
    }

    static public void setNonVegStarterList(List<FoodItem> nonvegstarter) {
        SolrData.NonVegStarters = nonvegstarter;
    }

    static List<FoodItem> extrasList = new ArrayList<>();
//    static List<FoodItem> mealsList=new ArrayList<>();

    static public List<FoodItem> getExtrasList() {
        return extrasList;
    }

    static public void setExtrasList(List<FoodItem> extras) {
        SolrData.extrasList = extras;
    }
//    static public List<FoodItem> getMealList() {
//        return mealsList;
//    }
//
//    static public void setMealList(List<FoodItem> extras) {
//        SolrData.extrasList = extras;
//    }


    static List<FoodItem> Breads = new ArrayList<>();

    static public List<FoodItem> getBreadsList() {
        return Breads;
    }

    static public void setBreadsList(List<FoodItem> breads) {
        SolrData.Breads = breads;
    }

    static List<FoodItem> Rolls = new ArrayList<>();

    static public List<FoodItem> getRollsList() {
        return Rolls;
    }

    static public void setRollsList(List<FoodItem> rolls) {
        SolrData.Rolls = rolls;
    }

    static List<FoodItem> VegMainCourse = new ArrayList<>();

    static public List<FoodItem> getVegMainCourseList() {
        return VegMainCourse;
    }

    static public void setVegMainCourseList(List<FoodItem> maincourse) {
        SolrData.VegMainCourse = maincourse;
    }

    static List<FoodItem> NonVegMainCourse = new ArrayList<>();

    static public List<FoodItem> getNonVegMainCourseList() {
        return NonVegMainCourse;
    }

    static public void setNonVegMainCourseList(List<FoodItem> maincourse) {
        SolrData.NonVegMainCourse = maincourse;
    }

    static List<FoodItem> Platter = new ArrayList<>();

    static public List<FoodItem> getPlatterList() {
        return Platter;
    }

    static public void setPlatterList(List<FoodItem> platter) {
        SolrData.Platter = platter;
    }

    static List<FoodItem> Rice = new ArrayList<>();

    static public List<FoodItem> getRiceList() {
        return Rice;
    }

    static public void setRiceList(List<FoodItem> rice) {
        SolrData.Rice = rice;
    }

    public String getApiLinks(int ITEM_CODE) {

        switch (ITEM_CODE) {

            case VEG_STARTER_CODE:
                return MenuUrl + "catg01&start=0&max=50";
            //return solrUrlAlacarte + "select?q=*%3A*&fq=Category%3AVEGSTARTERS&rows=50&wt=json&indent=true";

            case NON_VEG_STARTER_CODE:
                return MenuUrl + "catg02&start=0&max=50";
            // return solrUrlAlacarte + "select?q=*%3A*&fq=Category%3ANONVEGSTARTERS&rows=50&wt=json&indent=true";

            case BREADS_CODE:
                return MenuUrl + "catg08&start=0&max=20";
            // return solrUrlAlacarte + "select?q=*%3A*&fq=Category%3ABREADS&rows=20&wt=json&indent=true";

            case ROLLS_CODE:
                return MenuUrl + "catg06&start=0&max=20";
            //return solrUrlAlacarte + "select?q=*%3A*&fq=Category%3A+ROLLS&rows=10&wt=json&indent=true";

            case RICE_CODE:
                return MenuUrl + "catg07&start=0&max=20";
            //return solrUrlAlacarte + "select?q=*%3A*&fq=Category%3ARICE&wt=json&indent=true";

            case PLATTERS_CODE:
                return MenuUrl + "catg05&start=0&max=20";
            //return solrUrlAlacarte + "select?q=*%3A*&fq=Category%3APLATTERS+OR+Category%3ACOMBOS+&wt=json&indent=true";

            case VEG_MAIN_COURSE_CODE:
                return MenuUrl + "catg03&start=0&max=50";
            // return solrUrlAlacarte + "select?q=*%3A*&fq=Category%3AVEGMAINCOURSE+OR+Category%3A+CHAAPMAINCOURSE&rows=50&wt=json&indent=true";

            case NON_VEG_MAIN_COURSE_CODE:
                return MenuUrl + "catg04&start=0&max=50";
            // return solrUrlAlacarte + "select?q=*%3A*&fq=Category%3A+NONVEGMAINCOURSE&rows=50&wt=json&indent=true";

            case EXTRAS:
                return MenuUrl + "catg09&start=0&max=20";
            // return solrUrlAlacarte + "select?q=*%3A*&fq=Category%3A+SALADS+OR+Category%3A+RAITAS&wt=json&indent=true";
            case Meal:
                return MenuUrl + "catg10&start=0&max=50";
            case Paratha:
                return MenuUrl + "catg11&start=0&max=50";
            case Paneer:
                return MenuUrl + "catg12&start=0&max=50";
            case Roll:
                return MenuUrl + "catg13&start=0&max=50";
            case VegSection:
                return MenuUrl + "catg14&start=0&max=50";
            case ChickenSection:
                return MenuUrl + "catg15&start=0&max=50";

            default:
                return null;
        }
    }

    public void setCurrentMenuPosition(int position) {
        currentMenuPosition = position;
    }

    public int getCurrentMenuPosition() {
        return currentMenuPosition;
    }

    HashMap<String, Integer> ItemIDQTyCollection = new HashMap<>();

    public void SetItemIDQuantity(String id, int qty) {
        try {
            ItemIDQTyCollection.put(id, qty);
        } catch (Exception ex) {

        }
    }

    public int getItemIDQuantity(String id) {
        if (ItemIDQTyCollection != null && ItemIDQTyCollection.size() > 0)
            return ItemIDQTyCollection.get(id);
        else
            return 0;
    }
}