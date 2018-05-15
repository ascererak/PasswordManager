package edu.khai.csn.abondar.passwordmanager.Model.Entities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by Alexey Bondar on 18-Apr-18.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = DBHelper.class.getSimpleName();
    private static final String DB_NAME = "users.db";
    private static final int DB_VERSION = 1;

    private static final String USER_TABLE = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASS = "masterPassword";
    //public static final String COLUMN_PASSWORDS = "passwords";

    private static final String PASSWORD_TABLE = "passwords";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_SERVICENAME = "serviceName";
    private static final String COLUMN_ADDINFO = "additionalInformation";
    private static final String COLUMN_CREATION_DATE = "creationDate";
    private static final String COLUMN_MODIFYNG_DATE = "modifingDate";
    private static final String COLUMN_KEY = "key";

    /**
     * create table users(
     * id integer primary key autoincrement,
     * name text,
     * username text,
     * email text,
     * password text)
     */
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + USER_TABLE + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NAME + " TEXT,"
            + COLUMN_USERNAME  + " TEXT,"
            + COLUMN_EMAIL + " TEXT,"
            + COLUMN_PASS + " TEXT);";

    private static final String CREATE_TABLE_PASSWORDS = "CREATE TABLE " + PASSWORD_TABLE + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_PASSWORD + " TEXT,"
            + COLUMN_USERNAME  + " TEXT,"
            + COLUMN_SERVICENAME + " TEXT,"
            + COLUMN_ADDINFO + " TEXT,"
            + COLUMN_CREATION_DATE + " TEXT,"
            + COLUMN_MODIFYNG_DATE + " TEXT,"
            + COLUMN_KEY + " TEXT);";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_PASSWORDS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +  USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PASSWORD_TABLE);
        onCreate(db);
    }

    public void addUser(User user, Password password){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PASS, user.getMasterPassword());


        long id1 = db.insert(USER_TABLE, null, values);

        values = new ContentValues();
        values.put(COLUMN_PASSWORD, password.getPassword());
        values.put(COLUMN_USERNAME, password.getUserName());
        values.put(COLUMN_SERVICENAME, password.getServiceName());
        values.put(COLUMN_ADDINFO, password.getAdditionalInformation());
        values.put(COLUMN_CREATION_DATE, password.getCreationDate().toString());
        values.put(COLUMN_MODIFYNG_DATE, password.getModifyingDate().toString());

        SQLiteDatabase db2 = this.getReadableDatabase();
        String selectQuery = "select * from " + USER_TABLE + " where " +
                COLUMN_USERNAME + " = " + "'" + user.getUsername() + "'";
        Cursor cursor = db2.rawQuery(selectQuery, null);
        //Move to first row
        cursor.moveToFirst();
        values.put(COLUMN_KEY, cursor.getString(1));

        long id2 = db.insert(PASSWORD_TABLE, null, values);
        db.close();
        db2.close();

        Log.d(TAG, "user inserted" + id1);
        Log.d(TAG, "Password inserted" + id2);
    }//end addUser

    public boolean getUser(String username, String password){

        String selectQuery = "select * from " + USER_TABLE + " where " +
                COLUMN_USERNAME + " = " + "'" + username + "'" + " and " + COLUMN_PASS
                + " = " + "'" + password +"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        //Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0) {
            return true;
        }

        cursor.close();
        db.close();

        return false;
    }

    public boolean getUser(String username, String email, int mode){
        String selectQuery = "select * from " + USER_TABLE + " where " +
                COLUMN_USERNAME + " = " + "'" + username + "'" + " or " + COLUMN_EMAIL
                + " = " + "'" + email +"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        //Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0) {
            return true;
        }

        cursor.close();
        db.close();

        return false;
    }

    public String getUserByEmail(String email){
        String password;
        String selectQuery = "select * from " + USER_TABLE + " where " +
                COLUMN_EMAIL + " = " + "'" + email +"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        if(cursor.getCount() <= 0) {
            password =  null;
        }
        else{
            password = cursor.getString(4);
        }

        cursor.close();
        db.close();

        return password;
    }

    public User getUser(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        User user = new User();
        String selectQuery = "select * from " + USER_TABLE + " where " +
                COLUMN_USERNAME + " = " + "'" + username + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
       // for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
       //     if(cursor.getString(2) == username) {
                user.setUserID(cursor.getInt(0));
                user.setName(cursor.getString(1));
                user.setUsername(cursor.getString(2));
                user.setEmail(cursor.getString(3));
                user.setMasterPassword(cursor.getString(4));
                Bundle extras = cursor.getExtras();
                user.setPasswords((ArrayList<Password>) extras.getSerializable("Passwords"));
       //     }
       // }


        //    if (cursor.getCount() > 0) {
        //        return true;
        //    }
//
        cursor.close();
        db.close();


        return user;
    }

//    public boolean updateUser(User user) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//
//    }
    public void addPassword(User user, Password password){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, password.getPassword());
        values.put(COLUMN_USERNAME, password.getUserName());
        values.put(COLUMN_SERVICENAME, password.getServiceName());
        values.put(COLUMN_ADDINFO, password.getAdditionalInformation());
        values.put(COLUMN_CREATION_DATE, password.getCreationDate().toString());
        values.put(COLUMN_MODIFYNG_DATE, password.getModifyingDate().toString());

        SQLiteDatabase db2 = this.getReadableDatabase();
        String selectQuery = "select * from " + USER_TABLE + " where " +
                COLUMN_USERNAME + " = " + "'" + user.getUsername() + "'";
        Cursor cursor = db2.rawQuery(selectQuery, null);
        //Move to first row
        cursor.moveToFirst();
        values.put(COLUMN_KEY, cursor.getString(2));

        long id = db.insert(PASSWORD_TABLE, null, values);
        db.close();
        db2.close();

        Log.d(TAG, "Password inserted" + id);
    }

    public ArrayList<Password> getPassword(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Password> list = new ArrayList<>();
        String selectQuery = "select * from " + PASSWORD_TABLE + " where "
                + COLUMN_KEY + " = " + "'" + username + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);

        SimpleDateFormat format = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
        Date date = new Date();

        try {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                list.add(new Password(cursor.getString(1), cursor.getString(3),
                        cursor.getString(2), cursor.getString(4), Integer.parseInt(cursor.getString(0)),
                        format.parse(cursor.getString(5)), format.parse(cursor.getString(6))));
            }
        }
        catch (Exception e){
            Log.e("Exeption", "Something went wrong while getting passwords");
        }

        cursor.close();
        db.close();


        return list;
    }

    public void deletePassword(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        //String selectQuery = "select * from " + PASSWORD_TABLE + " where "
        //        + COLUMN_ID + " = " + "'" + id + "'";
        db.delete(PASSWORD_TABLE, "ID = ?", new String[]{Integer.toString(id)});
        db.close();
    }

    public void updatePassword(Password password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //values.put(COLUMN_ID, password.getId());
        values.put(COLUMN_PASSWORD , password.getPassword());
        values.put(COLUMN_USERNAME, password.getUserName());
        values.put(COLUMN_ADDINFO, password.getAdditionalInformation());
        values.put(COLUMN_SERVICENAME, password.getServiceName());
        values.put(COLUMN_MODIFYNG_DATE, password.getModifyingDate().toString());
        db.update(PASSWORD_TABLE, values, "ID = ?", new String[]{Integer.toString(password.getId())});
        db.close();
    }
}
