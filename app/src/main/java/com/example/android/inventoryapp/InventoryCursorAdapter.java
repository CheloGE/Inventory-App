/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.data.InventoryContract;
import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;
import com.example.android.inventoryapp.data.InventoryDbHelper;


public class InventoryCursorAdapter extends CursorAdapter {
    public static final String LOG_TAG = InventoryCursorAdapter.class.getSimpleName();
    private Context context;


    /**
     * Constructs a new
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
        this.context = context;
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the inventory data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current product can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final int currentRow = cursor.getPosition() + 1;
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = view.findViewById(R.id.text_view_product_name);
        TextView priceTextView = view.findViewById(R.id.text_view_price);
        TextView quantityTextView = view.findViewById(R.id.text_view_quantity);
        Button saleButton = view.findViewById(R.id.sale_button);
        // Find the columns of product attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);
        int supplierNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME);
        int emailColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_EMAIL);
        int phoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_PHONE);

        // Read the product attributes from the Cursor for the current product
        final String productNameString = cursor.getString(nameColumnIndex);
        final String priceString = cursor.getString(priceColumnIndex);
        final String productQuantity = cursor.getString(quantityColumnIndex);
        final String supplierNameString = cursor.getString(supplierNameColumnIndex);
        final String emailString = cursor.getString(emailColumnIndex);
        final String phoneString = cursor.getString(phoneColumnIndex);

        //listen sale button
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateQuantity(Integer.parseInt(productQuantity), currentRow, view, productNameString,
                        priceString, supplierNameString, emailString, phoneString);
            }
        });

        // Update the TextViews with the attributes for the current product
        nameTextView.setText(productNameString);
        priceTextView.setText(priceString);
        quantityTextView.setText(productQuantity);
    }

    /**
     * subtracts quantity by one
     */
    private void updateQuantity(int productQuantity, int row, View view,
                                String productNameString, String priceString, String supplierNameString, String emailString,
                                String phoneString) {
        if (productQuantity > 0)
            productQuantity--;
        else {
            Toast.makeText(view.getContext(), context.getString(R.string.no_items_left),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        // Create a ContentValues object where column names are the keys,
        // and product attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, productNameString);
        values.put(InventoryEntry.COLUMN_PRICE, Integer.parseInt(priceString));
        values.put(InventoryEntry.COLUMN_QUANTITY, productQuantity);
        values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(InventoryEntry.COLUMN_SUPPLIER_EMAIL, emailString);
        values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE, Integer.parseInt(phoneString));


        int rowsAffected = context.getContentResolver().update(Uri.withAppendedPath(InventoryEntry.CONTENT_URI, String.valueOf(row)),
                values, null, null);
        Toast.makeText(view.getContext(), productNameString+" "+context.getString(R.string.sold),
                Toast.LENGTH_SHORT).show();

    }
}