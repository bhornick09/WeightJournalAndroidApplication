package com.example.projecttwocs360brandonhornick;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
public class DBHelper extends SQLiteOpenHelper{
    // variables for database creation
    public static final String DBName = "register.db";
    private static final String TABLE_WEIGHT = "weight_entries";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_WEIGHT = "weight";
    private static final String COLUMN_NOTES = "notes";
    private static final String COLUMN_IMAGES = "images";

    // initialize dbhelper
    public DBHelper(@Nullable Context context){
        super(context, DBName, null, 3);
    }
    //create table with sql query
    @Override
    public void onCreate(SQLiteDatabase sqldb){
        sqldb.execSQL("create table users(username TEXT primary key, password TEXT)");
        String CREATE_WEIGHT_TABLE = "CREATE TABLE " + TABLE_WEIGHT + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_WEIGHT + " TEXT,"
                + COLUMN_NOTES + " TEXT,"
                + COLUMN_IMAGES + " TEXT" + ")";
        sqldb.execSQL(CREATE_WEIGHT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqldb, int oldVersion, int newVersion){
        // Drop existing tables so they can be recreated with new columns
        sqldb.execSQL("DROP TABLE IF EXISTS users");
        sqldb.execSQL("DROP TABLE IF EXISTS " + TABLE_WEIGHT);

        // Call onCreate to build the fresh tables
        onCreate(sqldb);
    }

    //insert user data for login screen
    public boolean insertData(String username, String password){
        SQLiteDatabase myDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);
        long result = myDB.insert("users", null, contentValues);
        return result != -1;
    }

    //check if user name is taken before allowing user to sign up with it
    public boolean checkUserName(String username){
        SQLiteDatabase myDB = this.getWritableDatabase();
        Cursor cursor = myDB.rawQuery("select * from users where username = ?", new String[] {username});
        return cursor.getCount() > 0;
    }
    // actual login function, checks for valid credentials
    public boolean checkUser(String username, String password){
        SQLiteDatabase myDB = this.getWritableDatabase();
        Cursor cursor = myDB.rawQuery("select * from users where username = ? and password = ?", new String[]{username, password});
        return cursor.getCount() > 0;
    }

    // insert data to the weight table
    public boolean insertWeightEntry(String date, String weight, String notes, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("weight", weight);
        values.put("notes", notes);
        values.put("images", image);
        long result = db.insert("weight_entries", null, values);
        return result != -1;
    }

    // used to display the data on the journal page
    public Cursor getAllWeightEntries() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM weight_entries ORDER BY date DESC", null);
    }
    // delete an entry from the table
    public boolean deleteWeightEntry(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete("weight_entries", "id = ?", new String[]{String.valueOf(id)});
        return deletedRows > 0;
    }

    // new AI functionality, return weight entries in readable format
    public Cursor getRecentWeightEntries(int limit){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT date, weight, notes FROM weight_entries ORDER BY date DESC LIMIT ?",
                new String[]{String.valueOf(limit)}
        );
    }

    public Cursor getEntryByDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM weight_entries WHERE date = ?", new String[]{date});
    }
}
