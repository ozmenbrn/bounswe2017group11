package com.culturage.oceans_eleven.culturage.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import com.culturage.oceans_eleven.culturage.newsFeed.HeritageItem;
import com.culturage.oceans_eleven.culturage.newsFeed.NewsFeedActivity;
import com.culturage.oceans_eleven.culturage.signup_login.LoginActivity;

/**
 * Created by me on 21.10.2017.
 * <p>
 * Fetches data from url
 * does some other url stuff internally
 */

public class Fetcher {
    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = Fetcher.class.getSimpleName();

    /**
     * Query the "backend" and return an {@link HeritageItem} object to represent a single heritage item.
     */
    public static ArrayList<HeritageItem> fetchNewsFeedData(String requestUrl, Context context) {

        // Create URL object
        //FIXME
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url,context);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        ArrayList<HeritageItem> newsFeed = extractHeritageItemsFromJson(jsonResponse);

        // Return the {@link Event}
        return newsFeed;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }


    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url,Context context) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");


            SharedPreferences preferences  = PreferenceManager.getDefaultSharedPreferences(context);
            String token = preferences.getString("token", "null");
           // String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoyMiwidXNlcm5hbWUiOiJha29rc2FsIiwiZW1haWwiOiJha29rc2FsQGEuY29tIiwiZXhwIjoyNTA4Njc4OTE1fQ.PgPIJppA9u5umhrHGxPmv7_1Hi2ItASDgd7NH4DHcO0";
            urlConnection.setRequestProperty("Authorization","JWT " + token);
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news feed JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return an {@link HeritageItem} object by parsing out information
     * about the first heritage item from the input newsFeedJSON string.
     */
    private static ArrayList<HeritageItem> extractHeritageItemsFromJson(String newsFeedJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsFeedJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding HeritageItem's to
        ArrayList<HeritageItem> heritageItems = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            //TODO delete these commented ones
//            JSONObject json = new JSONObject(newsFeedJSON);
//            JSONArray features = json.getJSONArray("features");


            JSONArray items = new JSONArray(newsFeedJSON);

            for (int i = 0; i < items.length(); i++) {
                JSONObject values = items.getJSONObject(i);
                String name =values.getString("name");
                String description =  values.getString("description");
                String imageURL = values.getString("featured_img");
                String rate = values.getString("rate");
                String createdAt = values.getString("created_at");


                //TODO dont forget to give the parameters
                heritageItems.add(new HeritageItem(name,description,imageURL,rate,createdAt));
                int a = heritageItems.size();
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the news feed JSON results", e);
        }

        // Return the list of heritageItems
        return heritageItems;
    }
}
