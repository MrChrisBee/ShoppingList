package de.example.christian.shoppinglist;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ShoppingMemoDbHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = ShoppingMemoDbHelper.class.getSimpleName();
    //Datenbank
    public static final String DB_NAME = "shopping_list.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE_SHOPPING_LIST = "shopping_list";
    //Spalten
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PRODUCT = "product";
    public static final String COLUMN_QUANTITY = "quantity";

    public static final String SQL_CREATE = "CREATE TABLE " + TABLE_SHOPPING_LIST +
            "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
            COLUMN_PRODUCT + " TEXT NOT NULL, " +
            COLUMN_QUANTITY + " INTEGER NOT NULL );";

    public ShoppingMemoDbHelper(Context con){
        super(con,DB_NAME,null,DB_VERSION);
        Log.d(LOG_TAG,"Helper hat die DB " + getDatabaseName() + " erzeugt");
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG,"Versuch die Tabelle zu erstellen !" );
        try {
            db.execSQL(SQL_CREATE);
        } catch (SQLException e) {
            Log.e(LOG_TAG,"Fehler beim Anlegen " + e.getMessage());
            e.printStackTrace();
        } finally {
            Log.d(LOG_TAG,"Tabelle erstellt !" );
        }
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p/>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
