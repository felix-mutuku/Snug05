package com.snugjar.www.youshopwedeliver;

import android.net.Uri;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

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
}
