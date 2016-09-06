package de.example.christian.shoppinglist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Administrator on 06.09.2016.
 */
public class ShoppingMemoDataSource {
    private static final String LOG_TAG = ShoppingMemoDataSource.class.getSimpleName();
    private SQLiteDatabase db;
    private ShoppingMemoDbHelper helper;

    public ShoppingMemoDataSource(Context con){
        Log.d(LOG_TAG, " Der Helper wird erzeugt.");
        helper = new ShoppingMemoDbHelper(con);
    }
}
