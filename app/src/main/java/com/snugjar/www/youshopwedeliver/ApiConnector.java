package com.snugjar.www.youshopwedeliver;

import android.net.Uri;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

public class ApiConnector {
    //used to register a new user into the database
    String RegisterUser(String Sname,
                        String Semail,
                        String Sphone,
                        String Stype,
                        String Scountry,
                        Uri Sphoto,
                        String SpersonID,
                        String phone_IMEI) {
        String url = Constants.BASE_URL_LOGIC + "registerUser.php?name=" + Sname +
                "&email=" + Semail +
                "&phone=" + Sphone +
                "&type=" + Stype +
                "&country=" + Scountry +
                "&photo=" + Sphoto +
                "&person_id=" + SpersonID +
                "&device_imei=" + phone_IMEI;

        HttpEntity httpEntity = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            httpEntity = httpResponse.getEntity();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String response = null;

        if (httpEntity != null) {
            try {
                response = EntityUtils.toString(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response;
    }

    //used to get all the supermarkets in that country for that day
    public JSONArray GetAvailableSupermarkets(String country) {
        String url = Constants.BASE_URL_LOGIC + "getAvailableSupermarkets.php?country=" + country;
        HttpEntity httpEntity = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            httpEntity = httpResponse.getEntity();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONArray jsonArray = null;
        if (httpEntity != null) {
            try {
                String entityResponse = EntityUtils.toString(httpEntity);
                jsonArray = new JSONArray(entityResponse);
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

    //used to check whether user already exists in database
    String CheckExistingUser(String personID) {
        String url = Constants.BASE_URL_LOGIC + "checkExistingUser.php?personID=" + personID;
        HttpEntity httpEntity = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            httpEntity = httpResponse.getEntity();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String response = null;

        if (httpEntity != null) {
            try {
                response = EntityUtils.toString(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response;
    }

    //used to check whether mobile number already exists in database
    String CheckUserMobile(String mobile) {
        String url = Constants.BASE_URL_LOGIC + "checkUserMobile.php?mobile=" + mobile;
        HttpEntity httpEntity = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            httpEntity = httpResponse.getEntity();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String response = null;

        if (httpEntity != null) {
            try {
                response = EntityUtils.toString(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response;
    }

    //used to get specific details about the user
    public JSONArray GetUserInformation(String personID) {
        String url = Constants.BASE_URL_LOGIC + "getUserInformation.php?personID=" + personID;
        HttpEntity httpEntity = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            httpEntity = httpResponse.getEntity();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONArray jsonArray = null;
        if (httpEntity != null) {
            try {
                String entityResponse = EntityUtils.toString(httpEntity);
                jsonArray = new JSONArray(entityResponse);
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

    //used to register a new user into the database
    String UpdateUser(String SpersonID, String Sphone) {
        String url = Constants.BASE_URL_LOGIC + "updateUser.php?personID=" + SpersonID + "&phone=" + Sphone;

        HttpEntity httpEntity = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            httpEntity = httpResponse.getEntity();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String response = null;

        if (httpEntity != null) {
            try {
                response = EntityUtils.toString(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response;
    }
}
