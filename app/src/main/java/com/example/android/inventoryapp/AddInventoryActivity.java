package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract;
import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

/**
 * Created by MRgarciaE on 1/24/2018.
 */

public class AddInventoryActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_add_inventory);
        // Retrieving the edit text on activity_add_inventory
        productNameEditView = findViewById(R.id.edit_text_product_name);
        priceEditView = findViewById(R.id.edit_text_price);
        quantityEditView = findViewById(R.id.edit_text_quantity);
        quantityEditView.setText(InventoryEntry.QUANTITY_DEFAULT);
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
                    Toast.makeText(AddInventoryActivity.this,getString(R.string.no_items_left),
                            Toast.LENGTH_SHORT).show();
                quantityEditView.setText(Integer.toString(currentQuantityInteger));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_add_inventory.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_add_inventory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "update data" menu option
            case R.id.action_save:
                saveProduct();

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

        // and check if all the fields in the editor are blank
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

        // This is a NEW product, so insert a new product into the provider,
        // returning the content URI for the new product.
        Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

        // Show a toast message depending on whether or not the insertion was successful.
        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.write_data_product_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.write_data_product_successful),
                    Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}

