package de.example.christian.shoppinglist;


import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private ShoppingMemoDataSource dataSource;
    private ListView mShoppingMemosListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataSource = new ShoppingMemoDataSource(this);
        initializeShoppingMemoListView();
        activateAddButton();
        initializeContextualActionBar();
    }

    private void initializeShoppingMemoListView() {
        List<ShoppingMemo> emptyListForInitialisation = new ArrayList<>();
        mShoppingMemosListView = (ListView) findViewById(R.id.listview_shopping_memos);
        ArrayAdapter<ShoppingMemo> arrayAdapter = new ArrayAdapter<ShoppingMemo>(this, android.R.layout.simple_list_item_single_choice, emptyListForInitialisation) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                ShoppingMemo memo = (ShoppingMemo) mShoppingMemosListView.getItemAtPosition(position);
                if (memo.isBought()) {
                    textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    textView.setTextColor(Color.rgb(175, 175, 175));
                } else {
                    textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    textView.setTextColor(Color.DKGRAY);
                }
                return view;
            }
        };
        mShoppingMemosListView.setAdapter(arrayAdapter);
        mShoppingMemosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShoppingMemo memo = (ShoppingMemo) parent.getItemAtPosition(position);
                ShoppingMemo updateMemo = dataSource.updateShoppingMemo(memo.getId(), memo.getProduct(), memo.getQuantity(), !memo.isBought());
                showAllListEntries();
            }
        });
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
        showAllListEntries();
    }

    private void initializeContextualActionBar() {
        final ListView shoppingMemoListView = (ListView) findViewById(R.id.listview_shopping_memos);
        shoppingMemoListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        shoppingMemoListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            int selectCount = 0;

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if (checked) {
                    selectCount++;
                } else {
                    selectCount--;
                }
                String capTitle = selectCount + " " + getString(R.string.bought);
                mode.setTitle(capTitle);
                mode.invalidate();
            }


            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getMenuInflater().inflate(R.menu.menue_contextual_action_bar, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                MenuItem item = menu.findItem(R.id.change);
                if (selectCount == 1) {
                    item.setVisible(true);
                } else {
                    item.setVisible(false);
                }

                return true;
            }


            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                SparseBooleanArray touchedMemoPosition;
                touchedMemoPosition = shoppingMemoListView.getCheckedItemPositions();
                switch (item.getItemId()) {
                    case R.id.delete:
                        for (int i = 0; i < touchedMemoPosition.size(); i++) {
                            boolean isChecked = touchedMemoPosition.valueAt(i);
                            if (isChecked) {
                                int posInListView = touchedMemoPosition.keyAt(i);
                                ShoppingMemo memo = (ShoppingMemo) shoppingMemoListView.getItemAtPosition(posInListView);
                                dataSource.deleteShoppingMemo(memo);
                            }
                        }
                        showAllListEntries();
                        break;
                    case R.id.change:
                        for (int i = 0; i < touchedMemoPosition.size(); i++) {
                            boolean isChecked = touchedMemoPosition.valueAt(i);
                            if (isChecked) {
                                int posInListView = touchedMemoPosition.keyAt(i);
                                ShoppingMemo memo = (ShoppingMemo) shoppingMemoListView.getItemAtPosition(posInListView);
                                AlertDialog editShoppingDialog = createEditShoppingMemoDialog(memo);
                                editShoppingDialog.show();
                            }
                        }
                        break;
                    default:
                        return false;
                }
                mode.finish();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                selectCount = 0;
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
                    editQuantity.setError(getString(R.string.editText_errorMessage_anzahl));
                    return;
                }
                if (TextUtils.isEmpty(product)) {
                    editProduct.setError(getString(R.string.editText_errorMessage_text));
                    return;
                }
                int quantity = Integer.parseInt(quantitiyTmp);
                editProduct.setText("");
                editQuantity.setText("");
                dataSource.createShoppingMemo(product, quantity);
                hideKeyboard();
                showAllListEntries();
            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, InputMethodManager.HIDE_NOT_ALWAYS);
    }


    private void showAllListEntries() {
        List<ShoppingMemo> list = dataSource.getAllShoppingMemos();
        ArrayAdapter<ShoppingMemo> adapter = (ArrayAdapter<ShoppingMemo>) mShoppingMemosListView.getAdapter();
        adapter.clear();
        adapter.addAll(list);
        adapter.notifyDataSetChanged();
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

    private AlertDialog createEditShoppingMemoDialog(final ShoppingMemo memo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogsView = inflater.inflate(R.layout.dialog_edit_shopping_memo, null);
        final EditText editTextQuantity = (EditText) dialogsView.findViewById(R.id.editText_new_quantity);
        editTextQuantity.setText(String.valueOf(memo.getQuantity()));
        final EditText editTextProduct = (EditText) dialogsView.findViewById(R.id.editText_new_product);
        editTextProduct.setText(String.valueOf(memo.getProduct()));

        builder.setView(dialogsView)
                .setTitle(R.string.dialog_title)
                .setPositiveButton(R.string.dialog_button_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String quantityString = editTextQuantity.getText().toString();
                        String product = editTextProduct.getText().toString();
                        if (TextUtils.isEmpty(quantityString) || TextUtils.isEmpty(product)) {
                            return;
                        }
                        int quantity = Integer.parseInt(quantityString);
                        ShoppingMemo shoppingMemo = dataSource.updateShoppingMemo(memo.getId(), product, quantity, memo.isBought());

                        showAllListEntries();
                        dialog.dismiss();
                        hideKeyboard();
                    }
                })
                .setNegativeButton(R.string.dialog_button_negative, new DialogInterface.OnClickListener() {
                    /**
                     * This method will be invoked when a button in the dialog is clicked.
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }
}
