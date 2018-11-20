package com.snugjar.www.youshopwedeliver.Connectors;

public class Constants {
    //URLs to connect to server and databases
    public static final String BASE_URL_LOGIC = "https://www.snugjar.com/Apps/TrolleyHut/Android/Logic/";
    public static final String BASE_URL_TERMS= "https://www.snugjar.com/Apps/TrolleyHut/Terms/";
    public static final String BASE_URL_SUPERMARKET_LOGOS = "https://www.snugjar.com/Apps/TrolleyHut/SupermarketLogos/";
    public static final String BASE_URL_SUPERMARKET_ADS = "https://www.snugjar.com/Apps/TrolleyHut/SupermarketAds/";
    public static final String PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.snugjar.www.youshopwedeliver";
    public static final String GOOGLE_MAPS_API_KEY = "AIzaSyCCZpukF_GBg6rmdow7IUlYSBQcpER0v4E";
    public static final String GOOGLE_MAPS_LOCATION_API_KEY = "AIzaSyDnwK-37yfkCLo1VA3A0X_DPSp2Q-CTv9U";
    public static final String DISTANCE_MATRIX_API = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=";
    public static final String DISTANCE_MATRIX_API2 = "&destinations=";
    public static final String DISTANCE_MATRIX_API3 = "&mode=driving&departure_time=now&key=" + GOOGLE_MAPS_API_KEY;
    public static final String REVERSE_GEOCODE_API = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
    public static final String REVERSE_GEOCODE_API2 = "&key="+ GOOGLE_MAPS_LOCATION_API_KEY;

    //saving a user's personal data in shared preferences
    public static final String SHARED_PREF_NAME = "Trolleyhut_pref";
    public static final String PERSON_ID = "trolleyhut_personID";
    public static final String PROFILE_PHOTO = "trolleyhut_user_photo";
    public static final String NAME = "trolleyhut_user_name";
    public static final String EMAIL = "trolleyhut_user_email";
    public static final String IMEI = "trolleyhut_user_IMEI";
    public static final String USER_TYPE = "trolleyhut_user_type";
    public static final String COUNTRY = "trolleyhut_country";
    public static final String PHONE = "trolleyhut_user_phone";
    public static final String DATE_JOINED = "trolleyhut_user_date_joined";

    //saving user's supermarket preference in shared preferences

    public static final String SUPERMARKET_NAME = "trolleyhut_supermarket_name";
    public static final String SUPERMARKET_BRANCH = "trolleyhut_supermarket_branch";
    public static final String DELIVERY_LATITUDE = "trolleyhut_delivery_latitude";
    public static final String DELIVERY_LONGITUDE = "trolleyhut_delivery_longitude";
    public static final String DELIVERY_DURATION = "trolleyhut_delivery_duration";
    public static final String DELIVERY_COST = "trolleyhut_delivery_cost";
    public static final String ESSENTIALS_DELIVERY_COST = "trolleyhut_delivery_essentials_cost";
    public static final String DELIVERY_PRICE = "trolleyhut_delivery_price";
    public static final String PICKUP_PRICE = "trolleyhut_pickup_price";
    public static final String CURRENCY = "trolleyhut_currency";
    public static final String PRICE_PER_KILOMETRE = "trolleyhut_price_per_kilometre";
    public static final String CART_ITEMS_NUMBER = "trolleyhut_cart_items_number";

    public static final String ORDER_ID = "trolleyhut_order_id";
}
