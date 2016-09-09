package de.example.christian.shoppinglist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class ShoppingMemoDataSource {

    private static final String LOG_TAG = ShoppingMemoDataSource.class.getSimpleName();
    private SQLiteDatabase db;
    private ShoppingMemoDbHelper helper;

    private String[] columns =
            {
                    ShoppingMemoDbHelper.COLUMN_ID,
                    ShoppingMemoDbHelper.COLUMN_PRODUCT,
                    ShoppingMemoDbHelper.COLUMN_QUANTITY,
                    ShoppingMemoDbHelper.COLUMN_CHECKED
            };

    public ShoppingMemoDataSource(Context con) {
        Log.d(LOG_TAG, "Der Helper wird erzeugt.");
        helper = new ShoppingMemoDbHelper(con);
    }

    public void open() {
        Log.d(LOG_TAG, "Wir öffnen die Datenbank im RW Mode.");
        db = helper.getWritableDatabase();
        Log.d(LOG_TAG, "Pfad zur DB " + db.getPath());
    }

    public void close() {
        helper.close();
        Log.d(LOG_TAG, "Datenbank wurde geschlossen.");
    }

    public ShoppingMemo createShoppingMemo(String product, int quantity) {
        ContentValues values = new ContentValues();
        values.put(ShoppingMemoDbHelper.COLUMN_PRODUCT, product);
        values.put(ShoppingMemoDbHelper.COLUMN_QUANTITY, quantity);

        long insertId = db.insert(ShoppingMemoDbHelper.TABLE_SHOPPING_LIST, null, values);

        Cursor cursor = db.query(ShoppingMemoDbHelper.TABLE_SHOPPING_LIST, columns,
                ShoppingMemoDbHelper.COLUMN_ID + "=" + insertId, null, null, null, null);
        cursor.moveToFirst();
        ShoppingMemo shoppingMemo = cursorToShoppingMemo(cursor);
        cursor.close();
        return shoppingMemo;
    }

    private ShoppingMemo cursorToShoppingMemo(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(ShoppingMemoDbHelper.COLUMN_ID);
        int idProduct = cursor.getColumnIndex(ShoppingMemoDbHelper.COLUMN_PRODUCT);
        int idQuantity = cursor.getColumnIndex(ShoppingMemoDbHelper.COLUMN_QUANTITY);
        int idChecked =  cursor.getColumnIndex(ShoppingMemoDbHelper.COLUMN_CHECKED);

        String product = cursor.getString(idProduct);
        int quality = cursor.getInt(idQuantity);
        long id = cursor.getLong(idIndex);
        int intValueChecked = cursor.getInt(idChecked);
        boolean isChecked = (intValueChecked != 0);
        ShoppingMemo shoppingMemo = new ShoppingMemo(product, quality, id, isChecked);
        return shoppingMemo;
    }


    public List<ShoppingMemo> getAllShoppingMemos() {
        List<ShoppingMemo> shoppingMemoList = new ArrayList<>();
        Cursor cursor = db.query(ShoppingMemoDbHelper.TABLE_SHOPPING_LIST, columns, null, null, null, null, null);
        cursor.moveToFirst();
        ShoppingMemo memo;
        while (!cursor.isAfterLast()) {
            memo = cursorToShoppingMemo(cursor);
            shoppingMemoList.add(memo);
            cursor.moveToNext();
        }
        cursor.close();
        return shoppingMemoList;
    }

    public void deleteShoppingMemo(ShoppingMemo shoppingMemo) {
        long id = shoppingMemo.getId();
        db.delete(ShoppingMemoDbHelper.TABLE_SHOPPING_LIST, ShoppingMemoDbHelper.COLUMN_ID + "=" + id, null);
    }

    public ShoppingMemo updateShoppingMemo(long id, String produkt, int quantity, boolean newChecked) {
        int intValueChecked = (newChecked)? 1:0;
        ContentValues values = new ContentValues();
        values.put(ShoppingMemoDbHelper.COLUMN_PRODUCT, produkt);
        values.put(ShoppingMemoDbHelper.COLUMN_QUANTITY, quantity);
        values.put(ShoppingMemoDbHelper.COLUMN_CHECKED,intValueChecked);

        db.update(ShoppingMemoDbHelper.TABLE_SHOPPING_LIST, values, ShoppingMemoDbHelper.COLUMN_ID + "=" + id, null);
        Cursor cursor = db.query(ShoppingMemoDbHelper.TABLE_SHOPPING_LIST, columns,
                ShoppingMemoDbHelper.COLUMN_ID + "=" + id, null, null, null, null);
        cursor.moveToFirst();
        ShoppingMemo shoppingMemo = cursorToShoppingMemo(cursor);
        cursor.close();
        return shoppingMemo;
    }
}

