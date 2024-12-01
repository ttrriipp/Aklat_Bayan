package com.example.aklatbayan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String dbname = "AklatBayanDB";
    private static final int dbversion = 1;

    private final static String tblname = "USERS";
    private final static String columnID = "ID";
    private final static String columnUserName = "USERNAME";
    private final static String columnEmail = "EMAIL";
    private final static String columnPassword = "PASSWORD";
    private final static String columnDeleted = "DELETED";

    public DBHelper(Context context) {
        super(context, dbname, null, dbversion);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + tblname + " ( " +
                columnID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                columnUserName + " TEXT UNIQUE, " +
                columnEmail + " TEXT UNIQUE, " +
                columnPassword + " TEXT, " +
                columnDeleted + " TEXT DEFAULT 'NO') ";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + tblname);
        onCreate(db);
    }

    // Adding User
    public void addNewUser (String USERNAME, String EMAIL, String PASSWORD) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(columnUserName, USERNAME);
        values.put(columnEmail, EMAIL);
        values.put(columnPassword, PASSWORD);
        values.put(columnDeleted, "NO");

        db.insert(tblname, null, values);
        db.close();
    }

    //Checking if available pa username
    public boolean usernameAvailability(String USERNAME) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + tblname + " WHERE " + columnUserName + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{USERNAME});

        boolean isTaken = cursor.getCount() > 0;
        cursor.close();
        return isTaken;
    }
    // Get all records
    public Cursor getAllUsers() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + tblname + " WHERE " + columnDeleted + "='NO'";
        return db.rawQuery(query, null);
    }
    // Get records by ID
    public Cursor getRecord(String ID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + tblname + " WHERE " + columnID + "='" + ID + "'";
        return db.rawQuery(query, null);
    }
    // Get records by Username
    public Cursor getUserbyUsername(String USERNAME) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + tblname + " WHERE " + columnUserName + "=? AND " + columnDeleted + "='NO'";
        return db.rawQuery(query, new String[]{USERNAME});
    }
    // Update User
    public boolean updateUser(String USERNAME, String EMAIL, String PASSWORD) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(columnUserName, USERNAME);
        values.put(columnEmail, EMAIL);
        values.put(columnPassword, PASSWORD);

        int result = db.update(tblname, values, columnEmail + "=?", new String[]{EMAIL});
        db.close();
        return result > 0;
    }
    // Delete User (Logical Deletion)
    public boolean deleteUser(String EMAIL) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(columnDeleted, "YES");

        int result = db.update(tblname, values, columnEmail + "=?", new String[]{EMAIL});
        db.close();
        return result > 0;
    }

}
