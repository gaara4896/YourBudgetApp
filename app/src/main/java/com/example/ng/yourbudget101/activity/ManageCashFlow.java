package com.example.ng.yourbudget101.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.ng.yourbudget101.R;
import com.example.ng.yourbudget101.activity.database.SQLiteManager;
import com.example.ng.yourbudget101.cashflow.Expenses;
import com.example.ng.yourbudget101.cashflow.Income;
import com.example.ng.yourbudget101.category.Category;
import com.example.ng.yourbudget101.category.Earning;
import com.example.ng.yourbudget101.category.ExpensesCategory;
import com.example.ng.yourbudget101.date.Date;
import com.example.ng.yourbudget101.network.AppConfig;
import com.example.ng.yourbudget101.network.AppController;
import com.example.ng.yourbudget101.network.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class ManageCashFlow extends AppCompatActivity {

    Toolbar toolbar;
    EditText editText_Date;
    EditText editText_Amount;
    EditText editText_Notes;
    Spinner spinner_Category;
    Button button_Record;
    Vector categories;
    SQLiteManager db;
    private ProgressDialog pDialog;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private static final String TAG = "ManageCashFlow";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_cash_flow);

        toolbar = (Toolbar) findViewById(R.id.include);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Budget");
        getSupportActionBar().setIcon(R.drawable.icon);
        toolbar.setTitleTextColor(Color.WHITE);

        editText_Date = (EditText) findViewById(R.id.editText_Date);
        editText_Date.setFocusable(false);
        editText_Date.setClickable(true);
        editText_Amount = (EditText) findViewById(R.id.editText_Amount);
        editText_Notes = (EditText) findViewById(R.id.editText_Notes);
        spinner_Category = (Spinner) findViewById(R.id.spinner_Category);
        button_Record = (Button) findViewById(R.id.button_Record);
        db = new SQLiteManager(getApplicationContext());

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        java.util.Calendar cal = java.util.Calendar.getInstance();
        int year = cal.get(java.util.Calendar.YEAR);
        int month = cal.get(java.util.Calendar.MONTH) + 1;
        int day = cal.get(java.util.Calendar.DAY_OF_MONTH);
        editText_Date.setText(day + "/" + month + "/" + year);

        final int category = getIntent().getExtras().getInt("Category");
        categories = new Vector();
        String[] showlist;

        if (category == 0) {
            try {
                addEarningCategory();
            } catch (Exception e) {
                e.printStackTrace();
            }

            showlist = new String[categories.size()];
            Iterator i = categories.iterator();
            int counter = 0;

            while (i.hasNext()) {
                Earning e = (Earning) i.next();
                showlist[counter] = e.getName();
                counter++;
            }
        } else {
            try {
                addExpensesCategory();
            } catch (Exception e) {
                e.printStackTrace();
            }

            showlist = new String[categories.size()];
            Iterator i = categories.iterator();
            int counter = 0;

            while (i.hasNext()) {
                ExpensesCategory e = (ExpensesCategory) i.next();
                showlist[counter] = e.getName();
                counter++;
            }
        }

        editText_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = editText_Date.getText().toString();
                String temp[] = date.split("/");

                DatePickerDialog datePicker = new DatePickerDialog(ManageCashFlow.this,
                        dateSetListener,
                        Integer.parseInt(temp[2]), Integer.parseInt(temp[1]) - 1, Integer.parseInt(temp[0]));
                datePicker.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month++;
                Log.d(TAG, "onDateSet: dd/mm/yyyy" + day + "/" + month + "/" + year);
                String date = day + "/" + month + "/" + year;
                editText_Date.setText(date);
            }
        };

        spinner_Category.setAdapter(new ArrayAdapter<String>(ManageCashFlow.this, android.R.layout.simple_spinner_dropdown_item, showlist));

        button_Record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nullAmount = editText_Amount.getText().toString();

                if (TextUtils.isEmpty(nullAmount)) {
                    editText_Amount.setError("Amount cannot be empty");
                    return;
                }

                String temp = editText_Date.getText().toString();
                Date date = Date.editToDate(temp);
                double amount = Double.parseDouble(editText_Amount.getText().toString());
                int position = spinner_Category.getSelectedItemPosition();
                String remark = editText_Notes.getText().toString();

                insertCashflow(category, ((Category) categories.get(position)).getName(), date, amount, remark);
            }
        });
    }

    private void addExpensesCategory() throws Exception {
        SQLiteManager db = new SQLiteManager(getApplicationContext());
        categories = db.getExpensesCat();
    }

    private void addEarningCategory() throws Exception {
        SQLiteManager db = new SQLiteManager(getApplicationContext());
        categories = db.getEarningCat();
    }

    private void insertCashflow(final int cashflow, final String category, final Date date
            , final double amount, final String remark) {
        String tag_string_req = "req_register";

        pDialog.setMessage("Recording ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_INSERTCASH, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean result = jObj.getBoolean("result");
                    if (result) {
                        boolean success;
                        if (cashflow == 0) {
                            Income income = new Income(amount, date, remark, category);
                            success = db.insertIncome(income, getApplicationContext());
                        } else {
                            Expenses expenses = new Expenses(amount, date, remark, category);
                            success = db.insertExpenses(expenses, getApplicationContext());
                        }
                        if (success) {
                            Toast.makeText(getApplicationContext(), "Record successfully recorded",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Not success",
                                    Toast.LENGTH_LONG).show();
                        }
                        setResult(RESULT_OK, null);
                        finish();
                    } else {
                        Log.e(TAG, "Error Message: " + jObj.getString("error_msg"));
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
                params.put("cashflow", String.valueOf(cashflow));
                params.put("userUid", user.getUserId());
                params.put("category", category);
                params.put("time", date.toString());
                params.put("amount", String.valueOf(amount));
                params.put("notes", remark);

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
