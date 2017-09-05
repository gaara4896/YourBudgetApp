package com.example.ng.yourbudget101.activity.report;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.ng.yourbudget101.R;
import com.example.ng.yourbudget101.network.AppConfig;
import com.example.ng.yourbudget101.network.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BugReport extends AppCompatActivity {

    Toolbar toolbar;
    Button button_Report;
    EditText editText_report;
    private ProgressDialog pDialog;
    private static final String TAG = BugReport.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bug_report);

        toolbar = (Toolbar) findViewById(R.id.include);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Budget");
        getSupportActionBar().setIcon(R.drawable.icon);
        toolbar.setTitleTextColor(Color.WHITE);

        button_Report = (Button) findViewById(R.id.button_Report);
        editText_report = (EditText) findViewById(R.id.editText_Report);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        button_Report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editText_report.getText().toString())) {
                    editText_report.setError("Description cannot be empty");
                    return;
                }

                reportBug(editText_report.getText().toString());
            }
        });
    }

    private void reportBug(final String report) {
        String tag_string_req = "req_register";

        pDialog.setMessage("Reporting ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REPORTBUG, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean result = jObj.getBoolean("result");
                    if (result) {
                        Toast.makeText(getApplicationContext(),
                                "Report successfully", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                jObj.getString("error_msg"), Toast.LENGTH_LONG).show();
                        finish();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),
                            "Unable to report bug", Toast.LENGTH_LONG).show();
                    finish();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                finish();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("report", report);

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
