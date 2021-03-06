package com.gtk.localsearch;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by RISHAB on 19-12-2016.
 */

public class Downloader extends AsyncTask<String ,Integer , ArrayList<String>> {

    MainActivity activity;
    public Downloader(MainActivity activity){
        this.activity = activity;
    }

    @Override
    protected ArrayList doInBackground(String... params) {
        ArrayList<ResultPlace> result =new ArrayList<>();
        try {
            Log.d("Value " , params[0]);
            String json = downloadUrl(params[0]);
            Log.d("Value " , json);
            JSONObject jsonObject = new JSONObject(json);
            JSONArray resultArray = jsonObject.getJSONArray("results");

            for(int i=0 ; i<resultArray.length() ; i++)
            {
                JSONObject singleResult = resultArray.getJSONObject(i);
                String Name = singleResult.getString("name");
                String address = singleResult.getString("vicinity");
                Log.d("Result" , Name + " , "+ address);
                JSONObject geometry = singleResult.getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");
                double longitude = location.getDouble("lng");
                double latitude = location.getDouble("lat");
                Log.d("Result" , longitude + " , "+ latitude);
                String photo_references;
                if(singleResult.has("photos")) {
                    String photo_reference_url = singleResult.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
                   photo_references = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&maxheight=300&photoreference=" + photo_reference_url + "&key=AIzaSyDchqRHH-7UyX1exvpeZd_ZPZKzvL9BBnQ";
                    Log.d("resultt_photo", photo_references);
                }else
                    photo_references = "";
                ResultPlace rp = new ResultPlace(Name , address , latitude , longitude , photo_references);
                result.add(rp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(ArrayList strings) {
        super.onPostExecute(strings);
        activity.drawList(strings);
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            //Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

}
