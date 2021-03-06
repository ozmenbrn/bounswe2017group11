package com.culturage.oceans_eleven.culturage.network;

import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Class whose only task is to make json formatted posts to to api and get response of course
 */
public class PostJSON {

    //private static final String baseURI = "http://52.90.34.144:85/api/";

    public static String postToApi(JSONObject json, String url, String token) throws  IOException {
        HttpURLConnection urlConnection;
        //String url = baseURI + endpoint;
        String data = json.toString();
        String result = null;
        InputStream inputStream = null;
        Log.v("postApi", url);
        urlConnection = (HttpURLConnection) ((new URL(url).openConnection()));
        Log.v("postApi", " " + (urlConnection == null));
        try {
            //Connect
            urlConnection.setDoInput(true); // Allow Inputs
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Authorization", "JWT " + token);
            urlConnection.connect();
            Log.v("postApi", "url connected");
            //Write
            OutputStream outputStream = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(data);
            writer.close();
            outputStream.close();
            Log.v("postApi", "output closed");


            if (urlConnection.getResponseCode() == 201 || urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                result = readFromStream(inputStream);
            } else {
                Log.v("postApi", json.toString());
                Log.v("postApi", urlConnection.getResponseCode() + "");
                Log.v("postApi-response", urlConnection.getResponseMessage());
                result = readFromStream(urlConnection.getErrorStream());
                Log.v("postApi-result", result);
                return "400";
            }

        } catch (Exception e) {
            Log.v("postApi", "error in connection. Result: " + result);
            e.printStackTrace();
        }
        return result;

    }

    /**
     * @param inputStream the input stream to read from
     * @return the string read from the input stream
     * @throws IOException
     */
    @NonNull
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
        Log.v("postApi-read", "stream is read. output:" + output);
        return output.toString();
    }

    /**
     * Basically jus another json post but specialized for deleting requests from the api
     * @param url the full url of the api to send the delete request
     * @param token the toke of this user
     * @return response code of api example 400 for success null means fail
     * @throws IOException
     */
    public static String deleteToApi(String url, String token) throws IOException {
        HttpURLConnection urlConnection;
        //String url = baseURI + endpoint;
        //String data = json.toString();
        String result = null;
        InputStream inputStream = null;
        Log.v("deleteApi", url);
        urlConnection = (HttpURLConnection) ((new URL(url).openConnection()));
        Log.v("deleteApi", " " + (urlConnection == null));
        try {
            //Connect
            urlConnection.setDoInput(true); // Allow Inputs
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestMethod("DELETE");
            urlConnection.setRequestProperty("Authorization", "JWT " + token);
            urlConnection.connect();
            Log.v("deleteApi", "url connected");
            //Write
            OutputStream outputStream = urlConnection.getOutputStream();
            //BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            //writer.write(data);
            //writer.close();
            outputStream.close();
            Log.v("deleteApi", "output closed");


            if (urlConnection.getResponseCode() == 201 || urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                result = readFromStream(inputStream);
            } else {
                //Log.v("deleteApi", json.toString());
                Log.v("deleteApi", urlConnection.getResponseCode() + "");
                Log.v("deleteApi-response", urlConnection.getResponseMessage());
                result = readFromStream(urlConnection.getErrorStream());
                Log.v("deleteApi-result", result);
                return "400";
            }

        } catch (Exception e) {
            Log.v("deleteApi", "error in connection. Result: " + result);
            e.printStackTrace();
        }
        return result;

    }


}