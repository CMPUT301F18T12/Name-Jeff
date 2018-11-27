/*
 * Controller Name: RecordController
 *
 *  Version: Version 1.0
 *
 *  Date: November 8, 2018
 *
 *  Copyright (c) Team 12, CMPUT301, University of Alberta - All Rights Reserved. You may use, distribute, or modify this code under terms and conditions of the Code of Students Behaviour at the University of Alberta
 */

package com.example.jerry.healemgood.controller;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.jerry.healemgood.model.problem.Problem;
import com.example.jerry.healemgood.model.record.Record;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.Update;
import io.searchbox.indices.IndicesExists;

/**
 * Represents a RecordController
 *
 * @author joeyUalberta
 * @version 1.0
 * @since 1.0
 */

public class RecordController {
    private static JestDroidClient client=null;
    private static String indexName = "cmput301f18t12";
    private static final String introQuery="{\n" +
            "    \"query\" : {\n" +
            "    \"bool\" : {\n" +
            "        \"must\": [\n"
            ;
    private static String searchQuery = introQuery;
    private static boolean building=false;

    /**
     * Get record by Id, used for testing
     */
    public static class  GetRecordByIdTask extends AsyncTask<String,Void,Record>{
        protected Record doInBackground(String... rids){
            setClient();
            Record r= null;
            String rid = rids[0];
            Get get = new Get.Builder(indexName, rid).type("record").build();
            try{
                DocumentResult result = client.execute(get);
                r = result.getSourceAsObject(Record.class);
            }catch(Exception e){
                Log.d("Name-Jeff","Fail to get record by id");
            }
            return r;
        }
    }

    /**
     * This will create a record in the database
     * @params record
     */
    public static class CreateRecordTask extends AsyncTask<Record,Void,Void> {

        private  AppCompatActivity context=null;

        public RecordController.CreateRecordTask setContext(AppCompatActivity c){
            this.context =c;
            return this;
        }

        protected Void doInBackground(Record... records) {
            setClient();
            Record record = records[0];
            Index index = new Index.Builder(record).index(indexName).type("record").build();
            try{
                DocumentResult result = client.execute(index);
                if(result.isSucceeded()){
                    record.setrId(result.getId());
                }
            }catch(IOException e){
                Log.d("Name-Jeff"," IOexception when executing client");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v){
            super.onPostExecute(v);
            if (context != null){
                try{
                    Thread.sleep(1000);
                }
                catch (Exception e){

                }

                context.finish();
            }

        }
    }

    /**
     * Delete a record from DB given the the record as input
     */
    public static class DeleteRecordTask extends AsyncTask<Record,Void,Void> {

        protected Void doInBackground(Record... records) {
            setClient();
            String rid = records[0].getrId();
            Delete delete = new Delete.Builder(rid).index(indexName).type("record").build();
            try{
                DocumentResult result = client.execute(delete);
                if(result.isSucceeded()){
                    Log.d("Name-Jeff","Record removed");
                }
            }catch(IOException e){
                Log.d("Joey Error"," IOexception when executing client");
            }
            return null;
        }
    }
    public static class UpdateRecordTask extends AsyncTask<Record,Void,Void> {

        protected Void doInBackground(Record... records) {
            setClient();
            Index index = new Index.Builder(records[0]).index(indexName).type("record").id(records[0].getrId()).build();
            try{
                DocumentResult result = client.execute(index);
                Log.d("Name-Jeff","Record updated");
            }catch(IOException e){
                Log.d("Joey Error"," IOexception when executing client");
            }
            return null;
        }
    }
    /***
     * Seach for a list of records by the searchQuery created. At the end, it will reset the searchQuery.
     * To build searchQuery, use initSearchQuery(), finalizeSearchQuery(), searchBy.....()
     * @params It will use the searchQuery user created before
     */
    public static class SearchRecordTask extends AsyncTask<Void,Void,ArrayList<Record>> {
        protected ArrayList<Record> doInBackground(Void... empty) {
            setClient();
            searchQuery += "]\n"+
                    "           }\n"+
                    "           }\n"+
                    "}";
            ArrayList<Record> records = new ArrayList<Record>();
            Search search = new Search.Builder(searchQuery).addIndex(indexName).addType("record").build();
            Log.d("Name-Jeff",searchQuery);
            searchQuery = introQuery;
            building = false;
            try{
                SearchResult result = client.execute(search);
                if(result.isSucceeded()){
                    Log.d("Name-Jeff","Record searched");
                    List<Record> resultList = result.getSourceAsObjectList(Record.class);
                    records.addAll(resultList);
                }
            }catch(IOException e){
                Log.d("Joey Error"," IOexception when executing client");
            }
            searchQuery=introQuery;
            return records;
        }
    }


    /**
     * Modify the search query so it will search for records by keyword in descriptions and title
     * @param keyword
     */
    public static void searchByKeyword(String keyword){
        if(keyword!="") {
            if(building==true){
                searchQuery+=",";
            }
            searchQuery += "   {\"multi_match\" : {\n" +
                    "   \"query\": \""+keyword+"\", \n"+
                    "   \"fields\": [\"title\",\"description\"] \n"+
                    "   }\n"+
                    " }\n";
            building=true;
        }
    }

    /**
     * Modify the search query so it will search for records by bodyLocation
     * @param location
     */
    public static void searchByBodyLocation(int location){
        if (location>=0){
            if(building==true){
                searchQuery+=",";
            }
            searchQuery += "   {\"term\" : {\n" +
                    "   \"bodyLocation\": \""+String.valueOf(location)+"\" \n"+
                    "       }\n"+
                    "   }\n";
            building=true;
        }
    }
    public static void searchByProblemIds(String ... piDs){
        if(building==true){
            searchQuery+=",";
        }
        searchQuery +="        {\"terms\" :{ \"pId\" : [";
        for (int i =0;i<piDs.length;i++){
            searchQuery += "\""+piDs[i].toLowerCase()+"\"";
            if(i!=piDs.length-1){
                searchQuery+=",";
            }
        }
        searchQuery+="]}\n"+
                "    }\n";
        building=true;
    }


    /**
     *
     * Modify the search query so it it will search for records that are inside the distance of place
     * @param latlng     Enter a location
     * @param distance  Enter the radius from the location in km
     */
    public static void searchByGeoLocation(LatLng latlng, int distance){
        double lat = latlng.latitude;
        double lon = latlng.longitude;
        if(building==true){
            searchQuery+=",";
        }
        searchQuery +=   "   {\"filtered\" : {\n"+
                "   \"filter\" : {\n"+
                "   \"geo_distance\" : {\n" +
                "               \"distance\": \""+String.valueOf(distance)+"km\", \n"+
                "                   \"geoLocation\": ["+String.valueOf(lon)+","+String.valueOf(lat)+"] \n"+
                "       }\n"+
                "       }\n"+
                "       }\n"+
                "   }\n";
        building=true;

    }


    /**
     * Create client
     */
    public static void setClient(){
        if(client==null){
            DroidClientConfig config = new DroidClientConfig.Builder("http://cmput301.softwareprocess.es:8080/").build();
            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            client=(JestDroidClient)factory.getObject();
        }
    }

}
