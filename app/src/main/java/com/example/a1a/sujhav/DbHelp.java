package com.example.a1a.sujhav;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhishek on 23-12-2017.
 */

public class DbHelp extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SujhavSamplesData";
    String tableName="SujhavSamples";
    String sampleId="SampleId";
    String latitude="Latitude";
    String longitude="Longitude";
    String pH="pH";
    String EC="EC";
    String farmerId="farmerId";
    public DbHelp(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + tableName +
                "(id integer primary key," +sampleId +" TEXT,"+ latitude + " TEXT," +longitude +" TEXT," +pH +" TEXT," +EC +" TEXT," +farmerId +" TEXT)";
        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + tableName);


        onCreate(sqLiteDatabase);

    }


    public void addData(String farmerId,String sampleId, String latitude,String longitude,String pH,String EC) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(this.sampleId,sampleId);
        values.put(this.latitude,latitude);
        values.put(this.longitude,longitude);
        values.put(this.pH,pH);
        values.put(this.EC,EC);
        values.put(this.farmerId,farmerId);
        db.insert(tableName, null, values);
        db.close(); // Closing database connection
    }
    // Getting single contact
    public String getData() throws JSONException {

        // We have to return a List which contains only String values. Lets create a List first
        String data = null;


        SQLiteDatabase sqliteDatabase = this.getReadableDatabase();

        // We need a a guy to read the database query. Cursor interface will do it for us
        //(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy)
        Cursor cursor = sqliteDatabase.query(this.tableName, null, null, null, null, null, null);
        // Above given query, read all the columns and fields of the table

        JSONArray jsonArray = new JSONArray();
        while (cursor.moveToNext()) {
            String fid=cursor.getString(cursor.getColumnIndex(farmerId));
            String samplenp = cursor.getString(cursor.getColumnIndex(sampleId));
            String lat = cursor.getString(cursor.getColumnIndex(latitude));
            String lon = cursor.getString(cursor.getColumnIndex(longitude));
            String p = cursor.getString(cursor.getColumnIndex(pH));
            String e=cursor.getString(cursor.getColumnIndex(EC));
            JSONObject j = new JSONObject();
            j.put("FarmerID", fid);
            j.put("pH",p);
            j.put("Ec", e);
            j.put("SampleNo", samplenp);
            j.put("Longitude", lon);
            j.put("Latitutude", lat);
            jsonArray.put(j);
        }
        try {
            data = URLEncoder.encode("json_data", "UTF-8")
                    + "=" + URLEncoder.encode(jsonArray.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        sqliteDatabase.close();

        return data;
    }
    public void deleteAll()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ tableName);
        db.close();
    }
}