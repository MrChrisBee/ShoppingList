package de.example.christian.shoppinglist;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private ShoppingMemoDataSource dataSource;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataSource = new ShoppingMemoDataSource(this);
        activateAddButton();
        initializeContextualActionBar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "Quelle wird geoffnet.");
        dataSource.open();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "Quelle wird geschlossen.");
        dataSource.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "Quelle wird geschlossen.");
        dataSource.close();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "Quelle wird geschlossen.");
        dataSource.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "Quelle wird geoffnet.");
        dataSource.open();
    }

    private void initializeContextualActionBar() {
        final ListView shoppingMemoListView = (ListView) findViewById(R.id.listview_shopping_memos);
        shoppingMemoListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        shoppingMemoListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

            }


            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getMenuInflater().inflate(R.menu.menue_contextual_action_bar, menu);
                return true;
            }


            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }


            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:
                        SparseBooleanArray touchedMemoPosition;
                        touchedMemoPosition = shoppingMemoListView.getCheckedItemPositions();
                        for (int i = 0; i < touchedMemoPosition.size(); i++) {
                            boolean isChecked = touchedMemoPosition.valueAt(i);
                            if (isChecked) {
                                int posInListView = touchedMemoPosition.keyAt(i);
                                ShoppingMemo memo = (ShoppingMemo) shoppingMemoListView.getItemAtPosition(posInListView);
                                dataSource.deleteShoppingMemo(memo);
                            }

                        }
                        showAllListEntries();
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
    }


    private void activateAddButton() {
        Button button = (Button) findViewById(R.id.button_add_product);
        final EditText editQuantity = (EditText) findViewById(R.id.editText_quantity);
        final EditText editProduct = (EditText) findViewById(R.id.editText_product);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantitiyTmp = editQuantity.getText().toString();
                String product = editProduct.getText().toString();
                if (TextUtils.isEmpty(quantitiyTmp)) {
                    editQuantity.setError(getString(R.string.editText_errorMessage));
                    return;
                }
                if (TextUtils.isEmpty(product)) {
                    editProduct.setError(getString(R.string.editText_errorMessage));
                    return;
                }
                int quantity = Integer.parseInt(quantitiyTmp);
                editProduct.setText("");
                editQuantity.setText("");
                dataSource.createShoppingMemo(product, quantity);
                /*
                * Lässt die Tastatur nach klick auf den Button verschwinden
                * */
                InputMethodManager inputMethodManager;
                inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (getCurrentFocus() != null) {
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                showAllListEntries();
            }
        });
    }

    private void showAllListEntries() {
        List<ShoppingMemo> list = dataSource.getAllShoppingMemos();
        ArrayAdapter<ShoppingMemo> adapter = new ArrayAdapter<ShoppingMemo>(this, android.R.layout.simple_list_item_single_choice, list);
        ListView listView = (ListView) findViewById(R.id.listview_shopping_memos);
        listView.setAdapter(adapter);
    }

    @Override

    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;

    }


    @Override

    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

}
