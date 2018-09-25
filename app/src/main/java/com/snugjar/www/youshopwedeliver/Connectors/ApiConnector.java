package com.snugjar.www.youshopwedeliver.Connectors;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ApiConnector {
    //used to register a new user into the database
    public String RegisterUser(String Sname,
                               String Semail,
                               String Sphone,
                               String Stype,
                               String Scountry,
                               Uri Sphoto,
                               String SpersonID,
                               String phone_IMEI) {

        StringBuilder result = new StringBuilder();
        HttpsURLConnection urlConnection = null;
        String response = null;

        try {
            URL url = new URL(Constants.BASE_URL_LOGIC + "registerUser.php?name=" + Sname +
                    "&email=" + Semail +
                    "&phone=" + Sphone +
                    "&type=" + Stype +
                    "&country=" + Scountry +
                    "&photo=" + Sphoto +
                    "&person_id=" + SpersonID +
                    "&device_imei=" + phone_IMEI);

            urlConnection = (HttpsURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            in.close();
            reader.close();

            response = String.valueOf(result);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return response;
    }

    //used to get all the supermarkets in that country weather available or not
    public JSONArray GetAvailableSupermarkets(String country) {
        StringBuilder result = new StringBuilder();
        JSONArray jsonArray = null;
        HttpsURLConnection urlConnection = null;

        try {
            URL url = new URL(Constants.BASE_URL_LOGIC + "getAvailableSupermarkets.php?country=" + country);
            urlConnection = (HttpsURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            in.close();
            reader.close();

            //handing the JSON to return to function
            try {
                jsonArray = new JSONArray(String.valueOf(result));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return jsonArray;

    }

    //used to check whether user already exists in database before beginning registration
    public String CheckExistingUser(String personID) {
        StringBuilder result = new StringBuilder();
        HttpsURLConnection urlConnection = null;
        String response = null;

        try {
            URL url = new URL(Constants.BASE_URL_LOGIC + "checkExistingUser.php?personID=" + personID);
            urlConnection = (HttpsURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            in.close();
            reader.close();

            response = String.valueOf(result);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return response;

    }

    //used to check whether mobile number already exists in database before adding it
    public String CheckUserMobile(String mobile) {
        StringBuilder result = new StringBuilder();
        HttpsURLConnection urlConnection = null;
        String response = null;

        try {
            URL url = new URL(Constants.BASE_URL_LOGIC + "checkUserMobile.php?mobile=" + mobile);
            urlConnection = (HttpsURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            in.close();
            reader.close();

            response = String.valueOf(result);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return response;

    }

    //used to get specific details about the user when requested in the profile
    public JSONArray GetUserInformation(String personID) {
        StringBuilder result = new StringBuilder();
        JSONArray jsonArray = null;
        HttpsURLConnection urlConnection = null;

        try {
            URL url = new URL(Constants.BASE_URL_LOGIC + "getUserInformation.php?personID=" + personID);
            urlConnection = (HttpsURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            in.close();
            reader.close();

            //handing the JSON to return to function
            try {
                jsonArray = new JSONArray(String.valueOf(result));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return jsonArray;

    }

    //used to update user information in the database when a user changes it
    public String UpdateUser(String SpersonID, String Sphone) {
        StringBuilder result = new StringBuilder();
        HttpsURLConnection urlConnection = null;
        String response = null;

        try {
            URL url = new URL(Constants.BASE_URL_LOGIC + "updateUser.php?personID=" + SpersonID + "&phone=" + Sphone);
            urlConnection = (HttpsURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            in.close();
            reader.close();

            response = String.valueOf(result);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return response;

    }

    //collecting data when a user is in a unsupported country for the Management Information System
    public String newCountryCollect(String country) {
        StringBuilder result = new StringBuilder();
        HttpsURLConnection urlConnection = null;
        String response = null;

        try {
            URL url = new URL(Constants.BASE_URL_LOGIC + "newCountryCollect.php?country=" + country);
            urlConnection = (HttpsURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            in.close();
            reader.close();

            response = String.valueOf(result);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return response;

    }

    //used to get sliding images of supermarkets
    public JSONArray GetSlidingImages(String country) {
        StringBuilder result = new StringBuilder();
        JSONArray jsonArray = null;
        HttpsURLConnection urlConnection = null;

        try {
            URL url = new URL(Constants.BASE_URL_LOGIC + "getSlidingSupermarketImages.php?country=" + country);
            urlConnection = (HttpsURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            in.close();
            reader.close();

            //handing the JSON to return to function
            try {
                jsonArray = new JSONArray(String.valueOf(result));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return jsonArray;
    }

    //used to get sliding ads from the database
    public JSONArray GetGeneralSlidingAds(String country, String placement) {
        StringBuilder result = new StringBuilder();
        JSONArray jsonArray = null;
        HttpsURLConnection urlConnection = null;

        try {
            URL url = new URL(Constants.BASE_URL_LOGIC + "getGeneralSlidingAds.php?country=" + country + "&placement=" + placement);
            urlConnection = (HttpsURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            in.close();
            reader.close();

            //handing the JSON to return to function
            try {
                jsonArray = new JSONArray(String.valueOf(result));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return jsonArray;
    }

    //used to update user information in the database when a user changes it
    public String UpdateUserLocation(String SpersonID, Double DLatitude, Double DLongitude) {
        StringBuilder result = new StringBuilder();
        HttpsURLConnection urlConnection = null;
        String response = null;

        try {
            URL url = new URL(Constants.BASE_URL_LOGIC + "updateUserLocation.php?personID=" + SpersonID +
                    "&latitude=" + DLatitude +
                    "&longitude=" + DLongitude);

            urlConnection = (HttpsURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            in.close();
            reader.close();

            response = String.valueOf(result);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return response;

    }

    //used to get sliding ads from the database
    public JSONArray GetSupermarketSlidingAds(String country, String supermarketName) {
        StringBuilder result = new StringBuilder();
        JSONArray jsonArray = null;
        HttpsURLConnection urlConnection = null;

        try {
            URL url = new URL(Constants.BASE_URL_LOGIC + "getSupermarketSlidingAds.php?country=" + country + "&supermarket=" + supermarketName);
            urlConnection = (HttpsURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            in.close();
            reader.close();

            //handing the JSON to return to function
            try {
                jsonArray = new JSONArray(String.valueOf(result));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return jsonArray;
    }

    //used to get all the supermarket branches for the supermarket selected by the user
    public JSONArray GetAllBranches(String supermarketID) {
        StringBuilder result = new StringBuilder();
        JSONArray jsonArray = null;
        HttpsURLConnection urlConnection = null;

        try {
            URL url = new URL(Constants.BASE_URL_LOGIC + "getAllSupermarketBranches.php?supermarketID=" + supermarketID);
            urlConnection = (HttpsURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            in.close();
            reader.close();

            //handing the JSON to return to function
            try {
                jsonArray = new JSONArray(String.valueOf(result));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return jsonArray;

    }

    //used to get all the supermarket branches distance for the user's current location
    public String[] GetBranchDistance(String Olatitude, String Olongitude, String Dlatitude, String Dlongitude) {
        StringBuilder result = new StringBuilder();
        String SDistance = null,
                SDuration = null;
        JSONObject jsonObject = null;
        HttpsURLConnection urlConnection = null;

        try {
            URL url = new URL(Constants.DISTANCE_MATRIX_API + Olatitude + "," + Olongitude +
                    Constants.DISTANCE_MATRIX_API2 + Dlatitude + "," + Dlongitude + Constants.DISTANCE_MATRIX_API3);
            urlConnection = (HttpsURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            in.close();
            reader.close();

            //handing the JSON to return to function
            try {
                jsonObject = new JSONObject(String.valueOf(result));

                SDistance = jsonObject.getJSONArray("rows")
                        .getJSONObject(0)
                        .getJSONArray("elements")
                        .getJSONObject(0)
                        .getJSONObject("distance")
                        .getString("text");

                SDuration = jsonObject.getJSONArray("rows")
                        .getJSONObject(0)
                        .getJSONArray("elements")
                        .getJSONObject(0)
                        .getJSONObject("duration_in_traffic")
                        .getString("text");

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        //return SDistance;

        return new String[] {SDistance, SDuration};

    }

    //used to check whether mobile number already exists in database before adding it
    public String CheckTimeFromServer() {
        StringBuilder result = new StringBuilder();
        HttpsURLConnection urlConnection = null;
        String response = null;

        try {
            URL url = new URL(Constants.BASE_URL_LOGIC + "checkTimeFromServer.php");
            urlConnection = (HttpsURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            in.close();
            reader.close();

            response = String.valueOf(result);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return response;

    }

    //used to get all the supermarket branches for the supermarket selected by the user
    public JSONArray GetProducts(String country) {
        StringBuilder result = new StringBuilder();
        JSONArray jsonArray = null;
        HttpsURLConnection urlConnection = null;

        try {
            URL url = new URL(Constants.BASE_URL_LOGIC + "getProducts.php?country=" + country);
            urlConnection = (HttpsURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            in.close();
            reader.close();

            //handing the JSON to return to function
            try {
                jsonArray = new JSONArray(String.valueOf(result));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return jsonArray;

    }
}
