package com.theupswing.csusbapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "CSUSB.database";

    // For ScavengerHunt table
    public static final String TABLE_NAME_SCAVENGER_HUNT = "location_table";
    public static final String COL_ID = "ID";
    public static final String COL_LOC = "Location";
    public static final String COL_LAT = "Latitude";
    public static final String COL_LONG = "Longitude";
    public static final String COL_DESC = "Description";
    public static final String COL_IMAGE = "Image";
    public static final String COL_VISITED = "Visited";

    // For StudyGroups table
    public static final String TABLE_NAME_STUDY_GROUPS = "study_group_table";
    public static final String COL_SESSION_ID = "SessionID";
    public static final String COL_COURSE_SUBJECT = "CourseSubject";
    public static final String COL_COURSE_NUMBER = "CourseNumber";
    public static final String COL_INSTRUCTOR_NAME = "InstructorName";
    public static final String COL_CLASS_TIME = "ClassTime";
    public static final String COL_MEETING_DATE = "MeetingDate";
    public static final String COL_MEETING_TIME = "MeetingTime";
    public static final String COL_MEETING_LOC = "MeetingLocation";
    public static final String COL_TOPIC = "Topic";
    public static final String COL_DESCRIPTION = "Description";
    public static final String COL_MAX_PARTICIPANTS = "MaxParticipants";

    private SQLiteDatabase database;
    private Context context;

    // Constructor to create a new database instance
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        database = this.getWritableDatabase();
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME_SCAVENGER_HUNT + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_LOC + " TEXT, "
                + COL_LAT + " REAL, "
                + COL_LONG + " REAL, "
                + COL_DESC + " TEXT, "
                + COL_IMAGE + " TEXT, "
                + COL_VISITED + " INTEGER)");


        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME_STUDY_GROUPS + " ("
                + COL_SESSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_COURSE_SUBJECT + " TEXT, "
                + COL_COURSE_NUMBER + " INTEGER, "
                + COL_INSTRUCTOR_NAME + " TEXT, "
                + COL_CLASS_TIME + " TEXT, "
                + COL_MEETING_DATE + " TEXT, "
                + COL_MEETING_TIME + " TEXT, "
                + COL_MEETING_LOC + " TEXT, "
                + COL_MAX_PARTICIPANTS + " INTEGER, "
                + COL_TOPIC + " TEXT, "
                + COL_DESCRIPTION + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_SCAVENGER_HUNT);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_STUDY_GROUPS);
        onCreate(sqLiteDatabase);
    }

    /**
     * Inputs the initial data into the table
     */
    public void setUpScavengerHuntTable() {
        insertDataScavengerHunt("Pfau Library", 34.18265304856431, -117.32407059520483, "You can study here", "library", 0);
        insertDataScavengerHunt("Student Union", 34.181309518547195, -117.32371486723423, "You can chill here", "student_union", 0);
        insertDataScavengerHunt("Gym", 34.181309518547298, -117.32371486723623, "You can work out here", "gym", 0);
    }


    // Scavenger Hunt Functions:

    /**
     * Insert a row of data into the ScavengerHunt table
     */
    public int insertDataScavengerHunt(String location, double latitude, double longitude, String description, String image, int visited) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_LOC, location);
        contentValues.put(COL_LAT, latitude);
        contentValues.put(COL_LONG, longitude);
        contentValues.put(COL_DESC, description);
        contentValues.put(COL_IMAGE, image);
        contentValues.put(COL_VISITED, visited);

        return (int) database.insert(TABLE_NAME_SCAVENGER_HUNT, null, contentValues);
    }

    public Cursor getAllDataForScavengerHunt() {
        return database.rawQuery("SELECT * FROM " + TABLE_NAME_SCAVENGER_HUNT, null);
    }

    /**
     * Returns the description field
     *
     * @param location: The specific location whose description is returned
     */
    public String getDescription(String location) {
        Cursor cursor = database.query(TABLE_NAME_SCAVENGER_HUNT, null, COL_LOC + " = ?", new String[]{location}, null, null, null);
        cursor.moveToNext();
        return cursor.getString(cursor.getColumnIndex(COL_DESC));
    }

    /**
     * Returns an image identifier that can later be used to get the image from the drawable folder
     *
     * @param location: The specific location whose description is returned
     */
    public int getImage(String location) {
        Cursor cursor = database.query(TABLE_NAME_SCAVENGER_HUNT, null, COL_LOC + " = ?", new String[]{location}, null, null, null);
        cursor.moveToNext();

        String image = cursor.getString(cursor.getColumnIndex(COL_IMAGE));
        String identifier = context.getPackageName() + ":drawable/" + image;
        return context.getResources().getIdentifier(identifier, null, null);
        //return cursor.getInt(cursor.getColumnIndex(COL_IMAGE));
    }

    /**
     * Returns the latitude of the specified location
     */
    public double getLatitude(String location) {
        Cursor cursor = database.query(TABLE_NAME_SCAVENGER_HUNT, null, COL_LOC + " = ?", new String[]{location}, null, null, null);
        cursor.moveToNext();
        return cursor.getDouble(cursor.getColumnIndex(COL_LAT));
    }

    /**
     * Returns the longitude of the specified location
     */
    public double getLongitude(String location) {
        Cursor cursor = database.query(TABLE_NAME_SCAVENGER_HUNT, null, COL_LOC + " = ?", new String[]{location}, null, null, null);
        cursor.moveToNext();
        return cursor.getDouble(cursor.getColumnIndex(COL_LONG));
    }

    /**
     * Updates the latitude and longitude fields of the specified location
     *
     * @param location:  Specific location
     * @param latitude:  New latitude value
     * @param longitude: New longitude value
     */
    public void updateLatLng(String location, double latitude, double longitude) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_LAT, latitude);
        contentValues.put(COL_LONG, longitude);

        database.update(TABLE_NAME_SCAVENGER_HUNT, contentValues, COL_LOC + " = ?", new String[]{location});  //"location" is substituted into the ? in the where clause. Setting the where clause to null will update all values
    }

    /**
     * Updates the visited field of the specified location
     *
     * @param location: Specific location
     * @param visited:  New visited value
     */
    public void updateVisited(String location, int visited) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_VISITED, visited);

        database.update(TABLE_NAME_SCAVENGER_HUNT, contentValues, COL_LOC + " = ?", new String[]{location});  //"location" is substituted into the ? in the where clause. Setting the where clause to null will update all values
    }

    /**
     * Updates the latitude and longitude fields of the specified location
     *
     * @param location: Specific location
     * @param image:    The new image name (should be exactly like the name in the Drawable directory)
     */
    public void updateImage(String location, String image) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_IMAGE, image);

        database.update(TABLE_NAME_SCAVENGER_HUNT, contentValues, COL_LOC + " = ?", new String[]{location});  //"location" is substituted into the ? in the where clause. Setting the where clause to null will update all values
    }

    /**
     * Checks if a certain value is in the database
     *
     * @param column:     Specific column
     * @param searchItem: Specific value in the column
     * @return true if the searchItem is found
     */
    public boolean isInDatabase(String column, String searchItem) {
        String[] columns = {column};
        String[] selectionArgs = {searchItem};

        Cursor cursor = database.query(TABLE_NAME_SCAVENGER_HUNT, columns, column + " =?", selectionArgs, null, null, null, "1");
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }


    // StudyGroup table functions:

    /**
     * Inputs the initial data into the database.
     * DELETE this part after Firebase is integrated. It's just meant to hardcode posts for demonstration purposes.
     */
    public void setUpStudyGroupTable() {
        insertDataStudyGroup("CSE",
                308,
                "Dr. Fadi Muheidat",
                "MW 8:00am - 10:00am",
                "11/23/2020",
                "2:00pm",
                "Pfau Library 1st floor",
                3,
                "Midterm Review",
                "I want to over chapter 5 in the book");

        insertDataStudyGroup("BIOL",
                200,
                "Dr. Nicholas Cordero",
                "MW 2:00pm - 4:00pm",
                "12/07/2020",
                "3:00pm",
                "Einstein Bagels",
                4,
                "Mitosis vs Meiosis",
                "I'm struggling differentiating between the two concepts. I'm going to be creating detailed notes and flashcards on each if anyone would like to join.");

        insertDataStudyGroup("CSE",
                350,
                "Dr. Mathew Harvel",
                "TR 10:00am - 12:00pm",
                "11/29/2020",
                "5:00pm",
                "The Wedge",
                3,
                "Binary Search Trees",
                "I am going to be practicing Binary Search Tree exercises from the lectures and book. Feel free to join!");

    }


    /**
     * Insert a row of data into the StudyGroups table
     */
    public int insertDataStudyGroup(String courseSubject, int courseNumber, String instructorName, String classTime, String meetingDate, String meetingTime, String meetingLocation, int maxParticipants, String topic, String description) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_COURSE_SUBJECT, courseSubject);
        contentValues.put(COL_COURSE_NUMBER, courseNumber);
        contentValues.put(COL_INSTRUCTOR_NAME, instructorName);
        contentValues.put(COL_CLASS_TIME, classTime);
        contentValues.put(COL_MEETING_DATE, meetingDate);
        contentValues.put(COL_MEETING_TIME, meetingTime);
        contentValues.put(COL_MEETING_LOC, meetingLocation);
        contentValues.put(COL_MAX_PARTICIPANTS, maxParticipants);
        contentValues.put(COL_TOPIC, topic);
        contentValues.put(COL_DESCRIPTION, description);

        return (int) database.insert(TABLE_NAME_STUDY_GROUPS, null, contentValues);
    }

    /**
     * Returns a query based on the chosen arguments
     * @param subject: The Course Subject
     * @param number: The Course Number
     * @return a query with the appropriate data
     */
    public Cursor getDataForStudyGroups(String subject, String number) {
        if(subject.equals("--") && number.equals("--")) {
            return database.rawQuery("SELECT * FROM " + TABLE_NAME_STUDY_GROUPS, null);

        } else if(!subject.equals("--") && number.equals("--")){
            return database.rawQuery("SELECT * FROM " + TABLE_NAME_STUDY_GROUPS
                            + " WHERE " + COL_COURSE_SUBJECT + " = '" + subject + "'"
                    , null);

        } else if(!subject.equals("--") && !number.equals("--")){
            return database.rawQuery("SELECT * FROM " + TABLE_NAME_STUDY_GROUPS
                            + " WHERE " + COL_COURSE_SUBJECT + " = '" + subject + "'"
                            + " AND " + COL_COURSE_NUMBER + " = " + number
                    , null);

        } else {
            return database.rawQuery("SELECT * FROM " + TABLE_NAME_STUDY_GROUPS
                            + " WHERE " + COL_COURSE_NUMBER + " = " + number
                    , null);
        }
    }

    /**
     * Returns the data pertaining to a specific session in the table
     * @param sessionID: The ID of the desired session
     * @return a cursor to the queried data
     */
    public Cursor getSessionInfo(int sessionID){
        return database.rawQuery("SELECT * FROM " + TABLE_NAME_STUDY_GROUPS
                        + " WHERE " + COL_SESSION_ID + " = " + sessionID
                , null);
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
