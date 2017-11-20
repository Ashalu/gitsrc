package com.tadqa.android.util;

public class API_LINKS {

    private static String BASE_URL = "http://43.252.91.43:98/admin_tadqa/";

    public static String ALL_ITEMS_URL = BASE_URL + "getAllMenu/?start=0&max=120";
    public static String AREA_LOCATION_URL = BASE_URL + "getallArea?getall=area";
    public static String ALL_CATEGORY_URL = BASE_URL + "getCategories/?start=0&max=30";
    public static String MENU_URL = BASE_URL + "getMenuByCategoryId/?ID=";
    public static String REGISTRATION_URL = BASE_URL + "signUp";
    public static String PLACE_ORDER_URL = BASE_URL + "placeOrder";
    public static String CANCEL_ORDER_URL = BASE_URL + "cancelOrder?";
    public static String SIGN_IN_URL = BASE_URL + "signIn";
    public static String CITY_DATA_URL = " http://43.252.91.43:98/admin_tadqa/getTadqaOutlets";
    public static String AREA_DETAIL_URL = BASE_URL + "getAreaDetails?areaname=";
    public static String GET_DELIVERY_AREA_BY_OUTLET_URL = BASE_URL + "getAreaByOutlets?outletname=";
    public static String BANNERS_URL = "http://43.252.91.43:98/admin_tadqa/getBanners/?start=0&max=6";
    public static String UPDATE_PROFILE_URL = BASE_URL + "updateProfile/?";
    public static String CHANGE_PASSWORD_URL = BASE_URL + "changePassword?";
    public static String GENERATE_OTP_URL = BASE_URL + "generateOTP?";
    public static String SEND_FEEDBACK_URL = BASE_URL + "feedback?";
    public static String ORDER_OTP_URL = BASE_URL + "generateOrderOTP?";
    public static String CREATE_NEW_PASSWORD_URL = BASE_URL + "createNewPswd";
    public static String ADD_EDIT_ADDRESS_URL = BASE_URL + "updateAddress?";
    public static String ITEM_DESCRIPTION_URL = BASE_URL + "getItemDescription?";
    public static String ABOUT_US_URL = BASE_URL + "aboutUs/?start=0&max=10";
    public static String GET_USER_PROFILE_URL = BASE_URL + "getProfile?Contact=";
    public static String GET_ADDRESS_URL = BASE_URL + "getAddress?Contact=";
    public static String HISTORY_ORDER_URL = BASE_URL + "getHistoryOrders/?Contact=";
    public static String CURRENT_ORDER_URL = BASE_URL + "getRecentOrders/?Contact=";
    public static String FAQ_URL = BASE_URL + "getFAQ/?start=0&max=12";
    public static String VERIFY_OTP_URL = BASE_URL + "verifyOTP?Contact=";//&OTP=1738"
    public static String GET_ORDER_ID_URL = BASE_URL + "getOrderID?From=Android";
    public static String VERIFY_COUPON = BASE_URL + "ApplyCouponCode?coupon=";
    public static String GENERATE_CHECKSUM_URL = "http://43.252.91.23:71/GenerateChecksum.aspx";
    public static String VERIFY_CHECKSUM_URL = "http://43.252.91.23:71/VerifyChecksum.aspx";
    public static String GET_OPENING_TIME_URL = BASE_URL + "getopeningtime?tag=";
    public static String SEND_FCM_DETAILS = BASE_URL + "Notifyparams";
    public static String APPLY_COUPON_POST_URL = BASE_URL + "ApplyCoupon";
}
