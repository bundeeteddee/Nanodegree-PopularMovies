package com.tinytinybites.popularmovies.app.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;
import com.tinytinybites.popularmovies.app.utilities.TestUtility;

/**
 * Created by bundee on 8/12/16.
 *
 */
public class TestDb extends AndroidTestCase{
    //Tag
    protected static final String TAG = TestDb.class.getCanonicalName();

    /**
     * Since we want each test to start with a clean slate
     */
    void deleteTheDatabase() {
        mContext.deleteDatabase(MoviesDBHelper.DATABASE_NAME);
    }

    /**
     * This function gets called before each test is executed to delete the database.
     * This makes sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    /**
     * Test database creation
     * @throws Throwable
     */
    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MoviesContract.MovieEntry.TABLE_NAME);

        mContext.deleteDatabase(MoviesDBHelper.DATABASE_NAME);
        SQLiteDatabase db = new MoviesDBHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: This means that the database has not been created correctly", c.moveToFirst());

        // verify that the tables have been created. We only have 1 table for now
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain movie table
        assertTrue("Error: Your database was created without movie table", tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MoviesContract.MovieEntry.TABLE_NAME + ")", null);
        assertTrue("Error: This means that we were unable to query the database for table information.", c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> movieColumnHashSet = new HashSet<String>();
        movieColumnHashSet.add(MoviesContract.MovieEntry._ID);
        movieColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE);
        movieColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_SYNOPSIS);
        movieColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_TITLE);
        movieColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE);
        movieColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_VOTE_COUNT);
        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            movieColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required movie entry columns
        assertTrue("Error: The database doesn't contain all of the required location movie columns", movieColumnHashSet.isEmpty());

        //Close db
        db.close();
    }

    public void testMovieTable(){
        insertMovie();
    }

    public long insertMovie() {
        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        MoviesDBHelper dbHelper = new MoviesDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step: Create ContentValues of what you want to insert
        ContentValues testValues = TestUtility.createMovieValues();

        // Third Step: Insert ContentValues into database and get a row ID back
        long rowId = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(rowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                MoviesContract.MovieEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from query", cursor.moveToFirst() );

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        //TestUtility.validateCurrentRecord("Error: Query Validation Failed", cursor, testValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from query", cursor.moveToNext() );

        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.close();
        return rowId;
    }

}
