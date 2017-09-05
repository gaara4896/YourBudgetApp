package com.example.ng.yourbudget101.activity.modifyCategory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.ng.yourbudget101.R;
import com.example.ng.yourbudget101.activity.MainPage;
import com.example.ng.yourbudget101.activity.database.SQLiteManager;
import com.example.ng.yourbudget101.category.Earning;
import com.example.ng.yourbudget101.category.ExpensesCategory;
import com.example.ng.yourbudget101.network.AppConfig;
import com.example.ng.yourbudget101.network.AppController;
import com.example.ng.yourbudget101.network.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class PromptDetail extends AppCompatActivity {

    private static final String TAG = PromptDetail.class.getSimpleName();
    Toolbar toolbar;
    RadioButton radioButton_HouseNo;
    RadioButton radioButton_HouseYes;
    RadioButton radioButton_HouseConsidering;
    RadioButton radioButton_CarNo;
    RadioButton radioButton_CarYes;
    RadioButton radioButton_CarConsidering;
    EditText editText_House;
    EditText editText_Car;
    Button button;
    private ProgressDialog pDialog;
    SQLiteManager db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prompt_detail);

        toolbar = (Toolbar) findViewById(R.id.include);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Budget");
        getSupportActionBar().setIcon(R.drawable.icon);
        toolbar.setTitleTextColor(Color.WHITE);

        radioButton_HouseNo = (RadioButton) findViewById(R.id.radioButton_HouseNo);
        radioButton_HouseYes = (RadioButton) findViewById(R.id.radioButton_HouseYes);
        radioButton_HouseConsidering = (RadioButton) findViewById(R.id.radioButton_HouseConsidering);
        radioButton_CarNo = (RadioButton) findViewById(R.id.radioButton_CarNo);
        radioButton_CarYes = (RadioButton) findViewById(R.id.radioButton_CarYes);
        radioButton_CarConsidering = (RadioButton) findViewById(R.id.radioButton_CarConsidering);
        editText_House = (EditText) findViewById(R.id.editText_House);
        editText_Car = (EditText) findViewById(R.id.editText_Car);
        button = (Button) findViewById(R.id.button);
        db = new SQLiteManager(getApplicationContext());

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        SQLiteHandler user = new SQLiteHandler(getApplicationContext());
        if (user.getIsPrompt()) {
            startActivity(new Intent(PromptDetail.this, MainPage.class));
            finish();
        }

        editText_House.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                radioButton_HouseYes.setChecked(true);
            }
        });

        radioButton_HouseNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText_House.setText("");
                radioButton_HouseNo.setChecked(true);
            }
        });

        radioButton_HouseConsidering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText_House.setText("");
                radioButton_HouseConsidering.setChecked(true);
            }
        });

        radioButton_HouseYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButton_HouseYes.setChecked(true);
            }
        });

        editText_Car.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                radioButton_CarYes.setChecked(true);
            }
        });

        radioButton_CarNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText_Car.setText("");
                radioButton_CarNo.setChecked(true);

            }
        });

        radioButton_CarConsidering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText_Car.setText("");
                radioButton_CarConsidering.setChecked(true);
            }
        });

        radioButton_CarYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButton_CarYes.setChecked(true);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.deleteData();

                int house = -1;
                int car = -1;

                if (radioButton_HouseNo.isChecked()) {
                    house = 0;
                } else if (radioButton_HouseConsidering.isChecked()) {
                    house = 1;
                } else if (radioButton_HouseYes.isChecked()) {
                    house = 2;
                } else {

                }

                if (radioButton_CarNo.isChecked()) {
                    car = 0;
                } else if (radioButton_CarConsidering.isChecked()) {
                    car = 1;
                } else if (radioButton_CarYes.isChecked()) {
                    car = 2;
                } else {

                }

                if (car == 2 || house == 2) {
                    String emptyHouse = editText_House.getText().toString();
                    String emptyCar = editText_Car.getText().toString();
                    if (car == 2 && house == 2) {
                        if (TextUtils.isEmpty(emptyHouse)) {
                            editText_House.setError("Monthly Mortgage cannot be empty");
                            if (TextUtils.isEmpty(emptyCar)) {
                                editText_Car.setError("Monthly Loan cannot be empty");
                            }
                            return;
                        } else if (TextUtils.isEmpty(emptyCar)) {
                            editText_Car.setError("Monthly Loan cannot be empty");
                            return;
                        }
                    } else {
                        if (car == 2) {
                            if (TextUtils.isEmpty(emptyCar)) {
                                editText_Car.setError("Monthly Loan cannot be empty");
                                return;
                            }
                        } else {
                            if (TextUtils.isEmpty(emptyHouse)) {
                                editText_House.setError("Monthly Mortgage cannot be empty");
                                return;
                            }
                        }
                    }
                }

                Vector<ExpensesCategory> expCat;
                Vector<Earning> earCat;
                try {
                    expCat = ExpensesCategory.getDefaultExpCat(getApplicationContext());
                    earCat = Earning.getDefaultEarCat(getApplicationContext());
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                Iterator i = expCat.iterator();
                while (i.hasNext()) {
                    ExpensesCategory temp = (ExpensesCategory) i.next();
                    if (temp.getName().equals("House")) {
                        if (house == 1) {
                            insertCategory(temp);
                            db.insertExpensesCat(temp, getApplicationContext());
                        } else if (house == 2) {
                            temp.setBudget(Double.parseDouble(editText_House.getText().toString()));
                            insertCategory(temp);
                            db.insertExpensesCat(temp, getApplicationContext());
                        }
                    } else if (temp.getName().equals("Car")) {
                        if (car == 1) {
                            insertCategory(temp);
                            db.insertExpensesCat(temp, getApplicationContext());
                        } else if (car == 2) {
                            temp.setBudget(Double.parseDouble(editText_Car.getText().toString()));
                            insertCategory(temp);
                            db.insertExpensesCat(temp, getApplicationContext());
                        }
                    } else {
                        insertCategory(temp);
                        db.insertExpensesCat(temp, getApplicationContext());
                    }
                }
                i = earCat.iterator();
                while (i.hasNext()) {
                    Earning temp = (Earning) i.next();
                    insertCategory(temp);
                    db.insertEarningCat(temp, getApplicationContext());
                }

                setPrompt();
            }
        });
    }

    private void insertCategory(final Earning e) {
        String tag_string_req = "req_register";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_INSERTCAT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean result = jObj.getBoolean("result");
                    if (result) {

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
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                SQLiteHandler user = new SQLiteHandler(getApplicationContext());
                params.put("unique_id", user.getUserId());
                params.put("name", e.getName());

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void insertCategory(final ExpensesCategory e) {
        String tag_string_req = "req_register";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_INSERTCAT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean result = jObj.getBoolean("result");
                    if (result) {

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
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                SQLiteHandler user = new SQLiteHandler(getApplicationContext());
                params.put("unique_id", user.getUserId());
                params.put("name", e.getName());
                params.put("type", String.valueOf(e.getType()));
                params.put("proportion", String.valueOf(e.getProportion()));
                params.put("budget", String.valueOf(e.getBudget()));

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void setPrompt() {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Processing ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_PROMPT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean result = jObj.getBoolean("result");
                    if (result) {
                        //Changed successfully
                        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
                        // Inserting row in users table
                        db.updatePrompt(db.getUserId());

                        Toast.makeText(getApplicationContext(), "Process success", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        startActivity(new Intent(PromptDetail.this, MainPage.class));
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
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
