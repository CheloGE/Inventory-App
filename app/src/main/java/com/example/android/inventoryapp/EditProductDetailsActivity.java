package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.support.v4.app.LoaderManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.Toast;


import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

/**
 * Created by MRgarciaE on 1/24/2018.
 */

public class EditProductDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_PRODUCT_LOADER = 0;
    private Uri currentInventoryUri;
    private TextView productNameEditView;
    private TextView priceEditView;
    private TextView quantityEditView;
    private TextView supplierNameEditView;
    private TextView supplierEmailEditView;
    private TextView supplierPhoneEditView;
    private ImageButton increaseButton;
    private ImageButton decreaseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        Intent intent = getIntent();
        currentInventoryUri = intent.getData();
        // Initialize a loader to read the inventory data from the database
        // and display the current values in the editor
        getSupportLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        productNameEditView = findViewById(R.id.edit_text_product_name);
        priceEditView = findViewById(R.id.edit_text_price);
        quantityEditView = findViewById(R.id.edit_text_quantity);
        supplierNameEditView = findViewById(R.id.edit_text_supplier_name);
        supplierEmailEditView = findViewById(R.id.edit_text_supplier_email);
        supplierPhoneEditView = findViewById(R.id.edit_text_supplier_phone);
        increaseButton = findViewById(R.id.button_increase_price);
        decreaseButton = findViewById(R.id.button_decrease_price);
        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentQuantity = quantityEditView.getText().toString().trim();
                int currentQuantityInteger = Integer.parseInt(currentQuantity);
                currentQuantityInteger++;
                quantityEditView.setText(Integer.toString(currentQuantityInteger));
            }
        });
        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentQuantity = quantityEditView.getText().toString().trim();
                int currentQuantityInteger = Integer.parseInt(currentQuantity);
                if (currentQuantityInteger>0)
                    currentQuantityInteger--;
                else
                    Toast.makeText(EditProductDetailsActivity.this,getString(R.string.no_items_left),
                            Toast.LENGTH_SHORT).show();
                quantityEditView.setText(Integer.toString(currentQuantityInteger));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_edit_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "update data" menu option
            case R.id.action_update:
                saveProduct();

                return true;
            case android.R.id.home:
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Get user input from editor and save product into database
     */
    private void saveProduct() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String productNameString = productNameEditView.getText().toString().trim();
        String priceString = priceEditView.getText().toString().trim();
        String quantityString = quantityEditView.getText().toString().trim();
        String supplierNameString = supplierNameEditView.getText().toString().trim();
        String emailString = supplierEmailEditView.getText().toString().trim();
        String phoneString = supplierPhoneEditView.getText().toString().trim();

        // check if all the fields in the editor are blank
        if (TextUtils.isEmpty(productNameString) || TextUtils.isEmpty(priceString) ||
                TextUtils.isEmpty(quantityString) || TextUtils.isEmpty(supplierNameString) ||
                TextUtils.isEmpty(emailString) || TextUtils.isEmpty(phoneString)) {
            // Since no fields were modified, we can return early without creating a new product.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            Toast.makeText(this, getString(R.string.no_empty_allowed),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and product attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, productNameString);
        values.put(InventoryEntry.COLUMN_PRICE, Integer.parseInt(priceString));
        values.put(InventoryEntry.COLUMN_QUANTITY, Integer.parseInt(quantityString));
        values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(InventoryEntry.COLUMN_SUPPLIER_EMAIL, emailString);
        values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE, Integer.parseInt(phoneString));

        int rowsAffected = getContentResolver().update(currentInventoryUri, values, null, null);

        // Show a toast message depending on whether or not the update was successful.
        if (rowsAffected == 0) {
            // If no rows were affected, then there was an error with the update.
            Toast.makeText(this, getString(R.string.write_data_product_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the update was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.write_data_product_successful),
                    Toast.LENGTH_SHORT).show();
        }
        finish();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        // Since the editor shows all product attributes, define a projection that contains
        // all columns from the inventory table
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_QUANTITY,
                InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryEntry.COLUMN_SUPPLIER_EMAIL,
                InventoryEntry.COLUMN_SUPPLIER_PHONE
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                currentInventoryUri,                 // Query the content URI for the current product
                projection,                     // Columns to include in the resulting Cursor
                null,                  // No selection clause
                null,               // No selection arguments
                null);                 // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of product attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME);
            int emailColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_EMAIL);
            int phoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_PHONE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String email = cursor.getString(emailColumnIndex);
            int phone = cursor.getInt(phoneColumnIndex);

            // Update the views on the screen with the values from the database
            productNameEditView.setText(name);
            priceEditView.setText(Integer.toString(price));
            quantityEditView.setText(Integer.toString(quantity));
            supplierNameEditView.setText(supplierName);
            supplierEmailEditView.setText(email);
            supplierPhoneEditView.setText(Integer.toString(phone));

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        productNameEditView.setText("");
        priceEditView.setText("");
        quantityEditView.setText("");
        supplierNameEditView.setText("");
        supplierEmailEditView.setText("");
        supplierPhoneEditView.setText("");
    }


}
