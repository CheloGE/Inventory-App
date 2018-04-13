package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract;
import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;
import com.example.android.inventoryapp.data.InventoryDbHelper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int PRODUCT_LOADER = 0;
    InventoryCursorAdapter mCursorAdapter;
    /**
     * Database helper that will provide us access to the database
     */
    private InventoryDbHelper inventoryDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddInventoryActivity.class);
                startActivity(intent);
            }
        });
        // Find the ListView which will be populated with the product data
        ListView inventoryListView = (ListView) findViewById(R.id.list);
        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_text_instructions);
        inventoryListView.setEmptyView(emptyView);

        //Setup an Adapter to create a list  item for each row of product data in the Cursor
        //There is no inventory data yet(until the loader finishes)so passing null to it.
        mCursorAdapter = new InventoryCursorAdapter(this, null);
        inventoryListView.setAdapter(mCursorAdapter);
        //Setup the item click listener
        inventoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //Create new intent to go to EditorActivity
                Intent intent = new Intent(MainActivity.this, SeeProductDetailsActivity.class);
                //Form the content URI that represents the specific product that was clicked on,
                //by appending the "id" (passed as input to this method) onto the InventoryEntry
                //For example, the URI would be "content://com.example.android.inventoryapp/products/2"
                //if the product with ID 2 was clicked on.
                Uri currentProductUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, id);
                //Set the URI on the data field of the intent
                intent.setData(currentProductUri);
                //Launch the{@link EditorActivity} to display the data for the current product.
                startActivity(intent);
            }
        });

        //kick off the loader
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertDummy();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void insertDummy() {

        // Create a ContentValues object where column names are the keys,
        // and Toto's product attributes are the values.
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, "Samsung S7");
        values.put(InventoryEntry.COLUMN_PRICE, 500);
        values.put(InventoryEntry.COLUMN_QUANTITY, 5);
        values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, "Samsung");
        values.put(InventoryEntry.COLUMN_SUPPLIER_EMAIL, "support@samsung.com");
        values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE, 555444333);
        // Insert a new row for Toto into the provider using the ContentResolver.
        // Use the {@link InventoryEntry#CONTENT_URI} to indicate that we want to insert
        // into the products database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
        // Show a toast message depending on whether or not the insertion was successful
        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.write_data_product_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.write_data_product_successful),
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_QUANTITY,
                InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryEntry.COLUMN_SUPPLIER_EMAIL,
                InventoryEntry.COLUMN_SUPPLIER_PHONE
        };
        //This Loader will execute the contentProvider's query method on a background thread
        return new CursorLoader(this, InventoryEntry.CONTENT_URI, projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //Update adapter with this new cursor containing updated product data
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Callback called when the data needs to be cleared
        mCursorAdapter.swapCursor(null);
    }
}
