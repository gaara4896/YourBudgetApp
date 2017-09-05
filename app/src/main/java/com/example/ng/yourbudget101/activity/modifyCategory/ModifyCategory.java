package com.example.ng.yourbudget101.activity.modifyCategory;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class ModifyCategory extends AppCompatActivity {

    Toolbar toolbar;
    Button button;
    EditText editText_Budget;
    EditText editText_Name;
    Spinner spinner_Category;
    Spinner spinner_Name;
    Spinner spinner_Proportion;
    Spinner spinner_Type;
    SQLiteManager db;
    String[] name;
    Vector categories;
    private ProgressDialog pDialog;
    private static final String TAG = ModifyCategory.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_category);

        toolbar = (Toolbar) findViewById(R.id.include);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Budget");
        getSupportActionBar().setIcon(R.drawable.icon);
        toolbar.setTitleTextColor(Color.WHITE);

        button = (Button) findViewById(R.id.button);
        editText_Budget = (EditText) findViewById(R.id.editText_Budget);
        editText_Name = (EditText) findViewById(R.id.editText_Name);
        spinner_Category = (Spinner) findViewById(R.id.spinner_Category);
        spinner_Name = (Spinner) findViewById(R.id.spinner_Name);
        spinner_Proportion = (Spinner) findViewById(R.id.spinner_Proportion);
        spinner_Type = (Spinner) findViewById(R.id.spinner_Type);

        db = new SQLiteManager(getApplicationContext());

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        editText_Budget.setVisibility(View.INVISIBLE);
        editText_Name.setVisibility(View.INVISIBLE);
        spinner_Name.setVisibility(View.INVISIBLE);
        spinner_Type.setVisibility(View.INVISIBLE);
        spinner_Proportion.setVisibility(View.INVISIBLE);

        String[] category = new String[]{"Category", "Income", "Expenses"};
        ArrayAdapter<String> catAdapter = new ArrayAdapter<String>(ModifyCategory.this,
                android.R.layout.simple_spinner_dropdown_item, category);
        spinner_Category.setAdapter(catAdapter);
        spinner_Category.setSelection(0);

        spinner_Category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    editText_Name.setVisibility(View.INVISIBLE);
                    editText_Budget.setVisibility(View.INVISIBLE);
                    spinner_Proportion.setVisibility(View.INVISIBLE);
                    spinner_Type.setVisibility(View.INVISIBLE);
                    spinner_Name.setVisibility(View.INVISIBLE);
                    button.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(),
                            "Please select Category", Toast.LENGTH_LONG).show();
                } else {
                    spinner_Name.setVisibility(View.VISIBLE);
                    if (position == 1) {
                        categories = db.getEarningCat();
                        name = new String[categories.size() + 1];
                        Iterator<Earning> i = categories.iterator();
                        name[0] = "Name";
                        int counter = 1;

                        while (i.hasNext()) {
                            Earning e = i.next();
                            name[counter] = e.getName();
                            counter++;
                        }
                    } else {
                        categories = db.getExpensesCat();
                        name = new String[categories.size() + 1];
                        Iterator<ExpensesCategory> i = categories.iterator();
                        name[0] = "Name";
                        int counter = 1;

                        while (i.hasNext()) {
                            ExpensesCategory e = i.next();
                            name[counter] = e.getName();
                            counter++;
                        }
                    }
                    ArrayAdapter<String> nameAdapter = new ArrayAdapter<String>(ModifyCategory.this,
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
                    editText_Name.setVisibility(View.INVISIBLE);
                    editText_Budget.setVisibility(View.INVISIBLE);
                    spinner_Proportion.setVisibility(View.INVISIBLE);
                    spinner_Type.setVisibility(View.INVISIBLE);
                    button.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(),
                            "Please select Name", Toast.LENGTH_LONG).show();
                } else {
                    if (spinner_Category.getSelectedItemPosition() == 1) {
                        Earning e = (Earning) categories.get(position - 1);
                        editText_Name.setVisibility(View.VISIBLE);
                        editText_Budget.setVisibility(View.INVISIBLE);
                        spinner_Proportion.setVisibility(View.INVISIBLE);
                        spinner_Type.setVisibility(View.INVISIBLE);
                        button.setVisibility(View.VISIBLE);

                        editText_Name.setText(e.getName());
                    } else {
                        ExpensesCategory e = (ExpensesCategory) categories.get(position - 1);

                        editText_Name.setVisibility(View.VISIBLE);
                        editText_Budget.setVisibility(View.VISIBLE);
                        spinner_Proportion.setVisibility(View.VISIBLE);
                        spinner_Type.setVisibility(View.VISIBLE);
                        button.setVisibility(View.VISIBLE);

                        editText_Name.setText(e.getName());
                        String[] type = new String[]{"Type", "Need", "Saving", "Want"};
                        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(ModifyCategory.this,
                                android.R.layout.simple_spinner_dropdown_item, type);
                        spinner_Type.setAdapter(typeAdapter);

                        String[] priority = new String[]{"Priority", "Top Priority", "Second Priority",
                                "Upper Middle Priority", "Lower Middle Priority", "Second Least Priority", "Least Priority"};
                        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<String>(ModifyCategory.this,
                                android.R.layout.simple_spinner_dropdown_item, priority);
                        spinner_Proportion.setAdapter(priorityAdapter);

                        spinner_Type.setSelection(e.getType());

                        if (e.getType() == 1) {
                            switch (e.getProportion()) {
                                case 1:
                                    spinner_Proportion.setSelection(6);
                                    break;
                                case 3:
                                    spinner_Proportion.setSelection(5);
                                    break;
                                case 5:
                                    spinner_Proportion.setSelection(4);
                                    break;
                                case 7:
                                    spinner_Proportion.setSelection(3);
                                    break;
                                case 9:
                                    spinner_Proportion.setSelection(2);
                                    break;
                                case 12:
                                    spinner_Proportion.setSelection(1);
                                    break;
                            }
                        } else {
                            switch (e.getProportion()) {
                                case 1:
                                    spinner_Proportion.setSelection(6);
                                    break;
                                case 2:
                                    spinner_Proportion.setSelection(5);
                                    break;
                                case 3:
                                    spinner_Proportion.setSelection(4);
                                    break;
                                case 4:
                                    spinner_Proportion.setSelection(3);
                                    break;
                                case 5:
                                    spinner_Proportion.setSelection(2);
                                    break;
                                case 6:
                                    spinner_Proportion.setSelection(1);
                                    break;
                            }
                        }

                        if (e.getBudget() > 0) {
                            editText_Budget.setText(String.valueOf(e.getBudget()));
                        } else {
                            editText_Budget.setText("");
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(editText_Name.getText().toString())){
                    editText_Name.setError("Name cannot be empty!");
                    return;
                }
                if (spinner_Category.getSelectedItemPosition() == 1) {
                    updateCategory(name[spinner_Name.getSelectedItemPosition()],
                            new Earning(editText_Name.getText().toString()));
                } else {
                    int proportion = 0;
                    if (spinner_Type.getSelectedItemPosition() == 1) {
                        switch (spinner_Proportion.getSelectedItemPosition()) {
                            case 0:
                                Toast.makeText(getApplicationContext(),
                                        "Please select Priority", Toast.LENGTH_LONG).show();
                                return;
                            case 1:
                                proportion = 12;
                                break;
                            case 2:
                                proportion = 9;
                                break;
                            case 3:
                                proportion = 7;
                                break;
                            case 4:
                                proportion = 5;
                                break;
                            case 5:
                                proportion = 3;
                                break;
                            case 6:
                                proportion = 1;
                                break;
                        }
                    } else if (spinner_Type.getSelectedItemPosition() == 2) {
                        switch (spinner_Proportion.getSelectedItemPosition()) {
                            case 0:
                                Toast.makeText(getApplicationContext(),
                                        "Please select Priority", Toast.LENGTH_LONG).show();
                                return;
                            case 1:
                                proportion = 6;
                                break;
                            case 2:
                                proportion = 5;
                                break;
                            case 3:
                                proportion = 4;
                                break;
                            case 4:
                                proportion = 3;
                                break;
                            case 5:
                                proportion = 2;
                                break;
                            case 6:
                                proportion = 1;
                                break;
                        }
                    } else {
                        switch (spinner_Proportion.getSelectedItemPosition()) {
                            case 0:
                                Toast.makeText(getApplicationContext(),
                                        "Please select Priority", Toast.LENGTH_LONG).show();
                                return;
                            case 1:
                                proportion = 6;
                                break;
                            case 2:
                                proportion = 5;
                                break;
                            case 3:
                                proportion = 4;
                                break;
                            case 4:
                                proportion = 3;
                                break;
                            case 5:
                                proportion = 2;
                                break;
                            case 6:
                                proportion = 1;
                                break;
                        }
                    }
                    double budget;
                    if (TextUtils.isEmpty(editText_Budget.getText().toString())) {
                        budget = 0;
                    } else {
                        budget = Double.parseDouble(editText_Budget.getText().toString());
                    }
                    updateCategory(name[spinner_Name.getSelectedItemPosition()],
                            new ExpensesCategory(editText_Name.getText().toString(),
                                    spinner_Type.getSelectedItemPosition(),
                                    proportion, budget));
                }
            }
        });
    }

    private void updateCategory(final String names, final Earning e) {
        String tag_string_req = "req_register";

        pDialog.setMessage("Modifying ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_EDITCAT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean result = jObj.getBoolean("result");
                    if (result) {
                        db.updateEarnignCat(names, e);
                        setResult(RESULT_OK, null);
                        finish();
                    } else {
                        Log.e(TAG, "Insert Earning Category Error: " + e.getName());
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
                params.put("new_name", e.getName());
                params.put("name", names);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void updateCategory(final String names, final ExpensesCategory e) {
        String tag_string_req = "req_register";

        pDialog.setMessage("Modifying ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_EDITCAT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean result = jObj.getBoolean("result");
                    if (result) {
                        db.updateExpensesCat(names, e);
                        setResult(RESULT_OK, null);
                        finish();

                    } else {
                        Log.e(TAG, "Insert Expenses Category Error: " + e.getName());
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
                params.put("new_name", e.getName());
                params.put("type", String.valueOf(e.getType()));
                params.put("proportion", String.valueOf(e.getProportion()));
                params.put("budget", String.valueOf(e.getBudget()));
                params.put("name", names);

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
