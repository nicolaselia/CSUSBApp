package com.theupswing.csusbapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "CSUSB.database";
    public static final String TABLE_NAME = "location_table";
    public static final String COL_ID = "ID";
    public static final String COL_LOC = "Location";
    public static final String COL_LAT = "Latitude";
    public static final String COL_LONG = "Longitude";
    public static final String COL_DESC = "Description";
    public static final String COL_IMAGE = "Image";
    public static final String COL_VISITED = "Visited";

    private SQLiteDatabase database;
    private Context context;

    // Constructor to create a new database instance
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        database = this.getWritableDatabase();
        this.context = context;
        //database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        //database.execSQL("CREATE TABLE " + TABLE_NAME + " ("+ COL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+COL_LOC+" TEXT, "+COL_LAT+" REAL, "+COL_LONG+" REAL, "+COL_DESC+" TEXT, "+COL_IMAGE+" INTEGER, "+COL_VISITED+" INTEGER)");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + " ("+ COL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+COL_LOC+" TEXT, "+COL_LAT+" REAL, "+COL_LONG+" REAL, "+COL_DESC+" TEXT, "+COL_IMAGE+" TEXT, "+COL_VISITED+" INTEGER)");
        //sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,Latitude INTEGER,Longitude INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    /**
     * Inputs the initial data into the database
     */
    public void setUpDatabase() {
        insertData("Pfau Library", 34.18265304856431, -117.32407059520483, "You can study here", "library", 0);
        insertData("Student Union", 34.181309518547195, -117.32371486723423, "You can chill here", "student_union", 0);
        insertData("Gym", 34.181309518547298, -117.32371486723623, "You can work out here", "gym", 0);
    }

    /**
     * Insert a row of data
     */
    public int insertData(String location, double latitude, double longitude, String description, String image, int visited){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_LOC, location);
        contentValues.put(COL_LAT, latitude);
        contentValues.put(COL_LONG, longitude);
        contentValues.put(COL_DESC, description);
        contentValues.put(COL_IMAGE, image);
        contentValues.put(COL_VISITED, visited);

        return (int) database.insert(TABLE_NAME, null, contentValues);
    }

    public Cursor getAllData(){
        Cursor res = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return res;
    }

    /**
     * Returns the description field
     * @param location: The specific location whose description is returned
     */
    public String getDescription(String location){
        Cursor cursor = database.query(TABLE_NAME, null, COL_LOC + " = ?", new String[]{location}, null, null, null);
        cursor.moveToNext();
        return cursor.getString(cursor.getColumnIndex(COL_DESC));
    }

    /**
     * Returns an image identifier that can later be used to get the image from the drawable folder
     * @param location: The specific location whose description is returned
     */
    public int getImage(String location){
        Cursor cursor = database.query(TABLE_NAME, null, COL_LOC + " = ?", new String[]{location}, null, null, null);
        cursor.moveToNext();

        String image = cursor.getString(cursor.getColumnIndex(COL_IMAGE));
        String identifier = context.getPackageName() + ":drawable/" + image;
        return context.getResources().getIdentifier(identifier, null, null);
        //return cursor.getInt(cursor.getColumnIndex(COL_IMAGE));
    }

    public double getLatitude(String location){
        Cursor cursor = database.query(TABLE_NAME, null, COL_LOC + " = ?", new String[]{location}, null, null, null);
        cursor.moveToNext();
        return cursor.getDouble(cursor.getColumnIndex(COL_LAT));
    }

    public double getLongitude(String location){
        Cursor cursor = database.query(TABLE_NAME, null, COL_LOC + " = ?", new String[]{location}, null, null, null);
        cursor.moveToNext();
        return cursor.getDouble(cursor.getColumnIndex(COL_LONG));
    }

    public void updateLatLng(String location, double latitude, double longitude){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_LAT, latitude);
        contentValues.put(COL_LONG, longitude);

        database.update(TABLE_NAME, contentValues, COL_LOC + " = ?", new String[]{location});  //"location" is substituted into the ? in the where clause. Setting the where clause to null will update all values
    }

    public void updateVisited(String location, int visited){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_VISITED, visited);

        database.update(TABLE_NAME, contentValues, COL_LOC + " = ?", new String[]{location});  //"location" is substituted into the ? in the where clause. Setting the where clause to null will update all values
    }

    public void updateImage(String location, String image){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_IMAGE, image);

        database.update(TABLE_NAME, contentValues, COL_LOC + " = ?", new String[]{location});  //"location" is substituted into the ? in the where clause. Setting the where clause to null will update all values
    }

    public boolean isInDatabase(String column, String searchItem){
        String[] columns = { column };
        String[] selectionArgs = { searchItem };

        Cursor cursor = database.query(TABLE_NAME, columns, column + " =?", selectionArgs, null, null, null, "1");
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }


    // Library: 34.18265304856431, -117.32407059520483
    // Student Union: 34.181309518547195, -117.32371486723423
    // Gym: 34.181243783767364, -117.31866795569658
    // Commons: 34.18043637898142, -117.32064474374056
    // Career Center (UH): 34.18040614622342, -117.32426404953004
    // Student Health Center: 34.18139882900989, -117.32251893728971
    // Writing Center: 34.183066587456764, -117.32203178107738
    // The Wedge: 34.18272543881861, -117.32447091490029


}
