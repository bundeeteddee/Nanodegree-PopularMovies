package com.tinytinybites.popularmovies.app.utilities;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import com.tinytinybites.popularmovies.app.data.MoviesContract;

/**
 * Created by bundee on 8/12/16.
 */
public class TestUtility extends AndroidTestCase{

    /**
     * Create a dummy movie content values
     * @return
     */
    public static ContentValues createMovieValues() {
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, (new Date()).getTime());
        weatherValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, "Star Trek: Beyond");
        weatherValues.put(MoviesContract.MovieEntry.COLUMN_SYNOPSIS, "Few weirdos running around in some unknown planets trying to get along with each other");
        weatherValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, 8.00);
        weatherValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_COUNT, 130);

        return weatherValues;
    }

    /**
     * Record validation function
     * @param error
     * @param valueCursor
     * @param expectedValues
     */
    public static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }
}
