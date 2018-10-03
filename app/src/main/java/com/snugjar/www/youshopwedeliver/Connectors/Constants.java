package com.snugjar.www.youshopwedeliver.Connectors;

public class Constants {
    //URLs to connect to server and databases
    public static final String BASE_URL_LOGIC = "https://www.snugjar.com/Apps/Shopdrop/Android/Logic/";
    public static final String BASE_URL_SUPERMARKET_IMAGE = "https://www.snugjar.com/Apps/Shopdrop/SupermarketPics/";
    public static final String BASE_URL_SUPERMARKET_LOGOS = "https://www.snugjar.com/Apps/Shopdrop/SupermarketLogos/";
    public static final String BASE_URL_SUPERMARKET_ADS = "https://www.snugjar.com/Apps/Shopdrop/SupermarketAds/";
    public static final String PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.snugjar.www.youshopwedeliver";
    public static final String GOOGLE_MAPS_API_KEY = "AIzaSyCCZpukF_GBg6rmdow7IUlYSBQcpER0v4E";
    public static final String DISTANCE_MATRIX_API = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=";
    public static final String DISTANCE_MATRIX_API2 = "&destinations=";
    public static final String DISTANCE_MATRIX_API3 = "&mode=driving&departure_time=now&key=" + GOOGLE_MAPS_API_KEY;

    //saving a user's personal data in shared preferences
    public static final String SHARED_PREF_NAME = "Shopdrop_pref";
    public static final String PERSON_ID = "shopdrop_personID";
    public static final String PROFILE_PHOTO = "shopdrop_user_photo";
    public static final String NAME = "shopdrop_user_name";
    public static final String EMAIL = "shopdrop_user_email";
    public static final String IMEI = "shopdrop_user_IMEI";
    public static final String USER_TYPE = "shopdrop_user_type";
    public static final String COUNTRY = "shopdrop_country";
    public static final String PHONE = "shopdrop_user_phone";
    public static final String DATE_JOINED = "shopdrop_user_date_joined";

    //saving user's supermarket preference in shared preferences

    public static final String SUPERMARKET_NAME = "shopdrop_supermarket_name";
    public static final String SUPERMARKET_BRANCH = "shopdrop_supermarket_branch";
    public static final String DELIVERY_LATITUDE = "shopdrop_delivery_latitude";
    public static final String DELIVERY_LONGITUDE = "shopdrop_delivery_longitude";
    public static final String DELIVERY_DURATION = "shopdrop_delivery_duration";
    public static final String DELIVERY_COST = "shopdrop_delivery_cost";
    public static final String ESSENTIALS_DELIVERY_COST = "shopdrop_delivery_essentials_cost";
}
