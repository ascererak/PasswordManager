package edu.khai.csn.abondar.passwordmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.khai.csn.abondar.passwordmanager.Model.Entities.Password;

/**
 * Created by Alexey Bondar on 28-Apr-18.
 */

public class DBHelperPassword extends SQLiteOpenHelper {

    public static final String TAG = DBHelperPassword.class.getSimpleName();
    public static final String DB_NAME = "passwords.db";
    public static final int DB_VERSION = 1;

    public static final String PASSWORD_TABLE = "passwords";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_SERVICENAME = "serviceName";
    public static final String COLUMN_ADDINFO = "additionalInformation";
    public static final String COLUMN_CREATION_DATE = "creationDate";
    public static final String COLUMN_MODIFYNG_DATE = "modifingDate";

    /**

     */
    public static final String CREATE_TABLE_PASSWORDS = "CREATE TABLE " + PASSWORD_TABLE + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_PASSWORD + " TEXT,"
            + COLUMN_USERNAME  + " TEXT,"
            + COLUMN_SERVICENAME + " TEXT,"
            + COLUMN_ADDINFO + " TEXT,"
            + COLUMN_CREATION_DATE + " TEXT,"
            + COLUMN_MODIFYNG_DATE + " TEXT);";


    public DBHelperPassword(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PASSWORDS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +  PASSWORD_TABLE);
        onCreate(db);
    }

    public void addPassword(Password password){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, password.getPassword());
        values.put(COLUMN_USERNAME, password.getUserName());
        values.put(COLUMN_SERVICENAME, password.getServiceName());
        values.put(COLUMN_ADDINFO, password.getAdditionalInformation());
        values.put(COLUMN_CREATION_DATE, password.getCreationDate().toString());
        values.put(COLUMN_MODIFYNG_DATE, password.getModifyingDate().toString());

        long id = db.insert(PASSWORD_TABLE, null, values);
        db.close();

        Log.d(TAG, "Password inserted" + id);
    }//end addUser

    public ArrayList<Password> getPassword(){

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Password> list = new ArrayList<>();
        String selectQuery = "select * from " + PASSWORD_TABLE;
//        try {
//            Connection connection = DriverManager.getConnection("****", "****", "****");
//            Statement statement = connection.createStatement();
//            ResultSet set = statement.executeQuery(selectQuery);
//            while (set.next()) {
//                Password password = new Password(set.getString("password"),
//                        set.getString("serviceName"), set.getString("username"),
//                        set.getString("additionalInformation"), set.getInt("id"),
//                        set.getDate("creationDate"), set.getDate("modifingDate"));
//                list.add(password);
//            }
//        }
//        catch (Exception e){}

        Cursor cursor = db.rawQuery(selectQuery, null);
        //    //Move to first row
            //cursor.moveToFirst();
        SimpleDateFormat format = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
        Date date = new Date();

        try {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                list.add(new Password(cursor.getString(1), cursor.getString(3),
                        cursor.getString(2), cursor.getString(4), Integer.parseInt(cursor.getString(0)),
                        format.parse(cursor.getString(5)), format.parse(cursor.getString(6))));
            }
        }
        catch (Exception e){}

        //    if (cursor.getCount() > 0) {
        //        return true;
        //    }
//
            cursor.close();
            db.close();


        return list;
    }
}
