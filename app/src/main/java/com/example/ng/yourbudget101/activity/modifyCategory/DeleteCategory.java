package com.example.ng.yourbudget101.activity.modifyCategory;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.ng.yourbudget101.R;
import com.example.ng.yourbudget101.activity.database.SQLiteManager;
import com.example.ng.yourbudget101.category.Earning;
import com.example.ng.yourbudget101.category.ExpensesCategory;
import com.example.ng.yourbudget101.network.AppConfig;
import com.example.ng.yourbudget101.network.AppController;
import com.example.ng.yourbudget101.network.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class DeleteCategory extends AppCompatActivity {

    Toolbar toolbar;
    Button button_Delete;
    Spinner spinner_Category;
    Spinner spinner_Name;
    SQLiteManager db;
    String[] name;
    private ProgressDialog pDialog;
    private static final String TAG = DeleteCategory.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_category);

        toolbar = (Toolbar) findViewById(R.id.include);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Budget");
        getSupportActionBar().setIcon(R.drawable.icon);
        toolbar.setTitleTextColor(Color.WHITE);

        button_Delete = (Button) findViewById(R.id.button_Delete);
        spinner_Category = (Spinner) findViewById(R.id.spinner_Category);
        spinner_Name = (Spinner) findViewById(R.id.spinner_Name);
        db = new SQLiteManager(getApplicationContext());

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        button_Delete.setVisibility(View.INVISIBLE);
        spinner_Name.setVisibility(View.INVISIBLE);

        String[] category = new String[]{"Category", "Income", "Expenses"};
        ArrayAdapter<String> catAdapter = new ArrayAdapter<String>(DeleteCategory.this,
                android.R.layout.simple_spinner_dropdown_item, category);
        spinner_Category.setAdapter(catAdapter);
        spinner_Category.setSelection(0);

        spinner_Category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    spinner_Name.setVisibility(View.INVISIBLE);
                    button_Delete.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(),
                            "Please select Category", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    spinner_Name.setVisibility(View.VISIBLE);
                    button_Delete.setVisibility(View.INVISIBLE);
                    if (position == 1) {
                        Vector<Earning> temp = db.getEarningCat();
                        name = new String[temp.size() + 1];
                        Iterator<Earning> i = temp.iterator();
                        name[0] = "Name";
                        int counter = 1;

                        while (i.hasNext()) {
                            Earning e = i.next();
                            name[counter] = e.getName();
                            counter++;
                        }
                    } else {
                        Vector<ExpensesCategory> temp = db.getExpensesCat();
                        name = new String[temp.size() + 1];
                        Iterator<ExpensesCategory> i = temp.iterator();
                        name[0] = "Name";
                        int counter = 1;

                        while (i.hasNext()) {
                            ExpensesCategory e = i.next();
                            name[counter] = e.getName();
                            counter++;
                        }
                    }
                    ArrayAdapter<String> nameAdapter = new ArrayAdapter<String>(DeleteCategory.this,
                            android.R.layout.simple_spinner_dropdown_item, name);
                    spinner_Name.setAdapter(nameAdapter);
                    spinner_Name.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_Name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    button_Delete.setVisibility(View.INVISIBLE);
                } else if (position == 1) {
                    button_Delete.setVisibility(View.VISIBLE);
                } else {
                    button_Delete.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        button_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCategory(name[spinner_Name.getSelectedItemPosition()],
                        spinner_Category.getSelectedItemPosition());
            }
        });
    }

    private void deleteCategory(final String name, final int option) {
        String tag_string_req = "req_register";

        pDialog.setMessage("Deleting ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_DELCAT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean result = jObj.getBoolean("result");
                    if (result) {
                        if (option == 1) {
                            db.deleteEarningCat(name);
                        } else {
                            db.deleteExpensesCat(name);
                        }
                        setResult(RESULT_OK, null);
                        finish();
                    } else {
                        Log.e(TAG, "Delete Category Error: " + name);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                SQLiteHandler user = new SQLiteHandler(getApplicationContext());
                params.put("unique_id", user.getUserId());
                params.put("name", name);
                params.put("option", String.valueOf(option));

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
