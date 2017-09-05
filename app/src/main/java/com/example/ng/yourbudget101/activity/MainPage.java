package com.example.ng.yourbudget101.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.ng.yourbudget101.R;
import com.example.ng.yourbudget101.activity.cashflow.ViewCashflow;
import com.example.ng.yourbudget101.activity.modifyCategory.AddCategory;
import com.example.ng.yourbudget101.activity.modifyCategory.DeleteCategory;
import com.example.ng.yourbudget101.activity.modifyCategory.ModifyCategory;
import com.example.ng.yourbudget101.activity.database.SQLiteManager;
import com.example.ng.yourbudget101.activity.report.BugReport;
import com.example.ng.yourbudget101.activity.validation.MainActivity;
import com.example.ng.yourbudget101.cashflow.Expenses;
import com.example.ng.yourbudget101.cashflow.Income;
import com.example.ng.yourbudget101.category.Earning;
import com.example.ng.yourbudget101.category.ExpensesCategory;
import com.example.ng.yourbudget101.category.Necessities;
import com.example.ng.yourbudget101.category.Saving;
import com.example.ng.yourbudget101.category.Want;
import com.example.ng.yourbudget101.date.Date;
import com.example.ng.yourbudget101.network.AppConfig;
import com.example.ng.yourbudget101.network.AppController;
import com.example.ng.yourbudget101.network.SQLiteHandler;
import com.example.ng.yourbudget101.network.SessionManager;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class MainPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static String TAG = "MainPage";

    PieChart pieChart;
    ImageButton imageButton_Income;
    ImageButton imageButton_Expenses;

    ArrayList<PieEntry> yEntry;
    ArrayList<Double> yEnt;
    ArrayList<String> xEntry;

    Vector<Necessities> necessities;
    Vector<Saving> saving;
    Vector<Want> want;
    double budget;
    double left;

    private ProgressDialog pDialog;
    SQLiteManager db1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        Log.d(TAG, "onCreate: Creating chart");

        double[] allocation = new double[3];

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Budget");
        getSupportActionBar().setIcon(R.drawable.icon);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        db1 = new SQLiteManager(getApplicationContext());

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        imageButton_Income = (ImageButton) findViewById(R.id.imageButton_Income);
        imageButton_Expenses = (ImageButton) findViewById(R.id.imageButton_Expenses);

        SQLiteHandler db = new SQLiteHandler(getApplicationContext());

        imageButton_Income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainPage.this, ManageCashFlow.class);
                i.putExtra("Category", 0);
                startActivityForResult(i, 1);
            }
        });

        imageButton_Expenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainPage.this, ManageCashFlow.class);
                i.putExtra("Category", 1);
                startActivityForResult(i, 1);
            }
        });

        pieChart = (PieChart) findViewById(R.id.pieChart);

        Description d = new Description();
        d.setText("");

        //get income
        budget = getBudget();
        left = budget;

        necessities = new Vector<Necessities>();
        saving = new Vector<Saving>();
        want = new Vector<Want>();

        try {
            addExpensesCategory();
        } catch (Exception e) {
            e.printStackTrace();
        }

        allocation = setAllocation();

        allocateBudget(allocation);

        pieChart.setContentDescription("Budget Allocation");
        pieChart.setRotationEnabled(false);
        //pieChart.setUsePercentValues(false);

        pieChart.setHoleColor(Color.parseColor("#E2FFE0"));
        pieChart.setCenterText("MYR " + new DecimalFormat("#.##").format(left));
        pieChart.setNoDataText("No Value Entered");
        //pieChart.setTransparentCircleAlpha(10);
        if (left > 0) {
            pieChart.setCenterTextColor(Color.parseColor("#1F8C12"));
        } else {
            pieChart.setCenterTextColor(Color.RED);
        }
        //pieChart.setHoleRadius(59);
        pieChart.setTransparentCircleAlpha(100);
        pieChart.setTransparentCircleRadius(55);
        pieChart.setCenterTextSize(20);
        pieChart.setDrawEntryLabels(false);
        pieChart.setEntryLabelTextSize(20);
        pieChart.setDescription(d);


        addDataSet(pieChart);

        pieChart.setDrawSliceText(true);

        /*pieChart.setContentDescription("Budget");
        pieChart.setRotationEnabled(false);
        //pieChart.setUsePercentValues(true);
        pieChart.setHoleColor(Color.parseColor("#E2FFE0"));
        pieChart.setCenterText("Budget");
        //pieChart.setCenterTextColor(Color.GREEN);
        //pieChart.setHoleRadius(59);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setCenterTextSize(20);
        pieChart.setDrawEntryLabels(true);
        pieChart.setEntryLabelTextSize(20);
        pieChart.setDescription(d);



        addDataSet(pieChart);*/

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.d(TAG, "onValueSelected: Value select from chart.");
                Log.d(TAG, "onValueSelected: " + e.toString());
                Log.d(TAG, "onValueSelected: " + h.toString());

                int pos2 = e.toString().indexOf("y: ");
                String available = e.toString().substring(pos2 + 3);

                //int pos1 = h.toString().indexOf("x: ");
                //String available = h.toString().substring(pos1 + 3);

                String[] part = h.toString().split(" ");
                String[] part1 = part[2].split(",");
                float temporary = Float.parseFloat(part1[0]);
                int pos1 = (int) temporary;

                /*for(int i = 0; i < part.length; i++){
                    Log.d(TAG,"DEBUG "+ part[i]);
                }*/

                Log.d(TAG, "onValueSelected: " + pos1);

                /*for (int i = 0; i < yEnt.size(); i++){
                    //Log.d(TAG, "Debug: " + yEnt.get(i));
                    if(yEnt.get(i) == Double.parseDouble(available)){
                        pos1 = i;
                        break;
                    }
                }*/

                Log.d(TAG, "onValueSelected: " + pos1);
                String cat = xEntry.get(pos1);
                Toast.makeText(MainPage.this, "Category: " + cat + "\n" + "Budget Left : RM " +
                        new DecimalFormat("##.##").format(Double.parseDouble(available)), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private void addExpensesCategory() throws Exception {
        SQLiteManager db = new SQLiteManager(getApplicationContext());
        Vector<ExpensesCategory> temp = db.getExpensesCat();
        Iterator<ExpensesCategory> i = temp.iterator();

        while (i.hasNext()) {
            ExpensesCategory temp1 = i.next();
            if (temp1.getType() == 1) {
                necessities.add(new Necessities(temp1.getName(), temp1.getProportion(), temp1.getBudget()));
            } else if (temp1.getType() == 2) {
                saving.add(new Saving(temp1.getName(), temp1.getProportion(), temp1.getBudget()));
            } else {
                want.add(new Want(temp1.getName(), temp1.getProportion(), temp1.getBudget()));
            }
        }
    }

    private double getBudget() {
        SQLiteManager db = new SQLiteManager(getApplicationContext());
        Vector<Income> temp = db.getIncome();
        Iterator<Income> i = temp.iterator();
        double total = 0;
        java.util.Calendar cal = java.util.Calendar.getInstance();
        int year = cal.get(java.util.Calendar.YEAR);
        int month = cal.get(java.util.Calendar.MONTH) + 1;

        while (i.hasNext()) {
            Income income = i.next();
            if (income.getDate().getMonth() == month && income.getDate().getYear() == year) {
                total += income.getCashflow();
            }
        }

        return total;
    }

    private void allocateBudget(double[] allocation) {
        double need = budget * allocation[0];
        double save = budget * allocation[1];
        double wan = budget * allocation[2];

        int total;

        Iterator i = necessities.iterator();
        need = leftOver(i, need);
        if (need > 0) {
            i = necessities.iterator();
            total = countProportion(i);
            i = necessities.iterator();
            necessities = setProportion(need, i, total);
        }
        i = necessities.iterator();
        necessities = minusExpenses(i);

        i = saving.iterator();
        save = leftOver(i, save);
        if (save > 0) {
            i = saving.iterator();
            total = countProportion(i);
            i = saving.iterator();
            saving = setProportion(save, i, total);
        }
        i = saving.iterator();
        saving = minusExpenses(i);

        i = want.iterator();
        wan = leftOver(i, wan);
        if (wan > 0) {
            i = want.iterator();
            total = countProportion(i);
            i = want.iterator();
            want = setProportion(wan, i, total);
        }
        i = want.iterator();
        want = minusExpenses(i);
    }

    private Vector minusExpenses(Iterator i) {
        SQLiteManager db = new SQLiteManager(getApplicationContext());
        Vector<Expenses> temp = db.getExpenses();
        Vector temp1 = new Vector();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        int year = cal.get(java.util.Calendar.YEAR);
        int month = cal.get(java.util.Calendar.MONTH) + 1;

        while (i.hasNext()) {
            ExpensesCategory e = (ExpensesCategory) i.next();
            Iterator j = temp.iterator();
            while (j.hasNext()) {
                Expenses ex = (Expenses) j.next();
                if(ex.getDate().getYear() == year && ex.getDate().getMonth() == month) {
                    if (ex.getCategory().equals(e.getName())) {
                        left -= ex.getCashflow();
                        if (e.getBudget() - ex.getCashflow() >= 0) {
                            e.setBudget(e.getBudget() - ex.getCashflow());
                        } else {
                            e.setBudget(0);
                        }
                    }
                }
            }
            temp1.add(e);
        }
        return temp1;
    }

    private double leftOver(Iterator i, double allocate) {
        while (i.hasNext()) {
            ExpensesCategory n = (ExpensesCategory) i.next();
            if (n.getBudget() != 0) {
                if (allocate - n.getBudget() > 0) {
                    allocate -= n.getBudget();
                } else {
                    allocate = 0;
                }
            }
        }
        return allocate;
    }

    private Vector setProportion(double allocate, Iterator i, int total) {
        Vector temp = new Vector();
        while (i.hasNext()) {
            ExpensesCategory n = (ExpensesCategory) i.next();
            if (n.getBudget() == 0) {
                n.setBudget(allocate * (double) n.getProportion() / (double) total);
            }
            temp.add(n);
        }
        return temp;
    }

    private int countProportion(Iterator i) {
        int total = 0;
        while (i.hasNext()) {
            ExpensesCategory n = (ExpensesCategory) i.next();
            if (n.getBudget() == 0) {
                total += n.getProportion();
            }
        }
        return total;
    }

    private double[] setAllocation() {
        double[] x = {0.75, 0.2, 0.05};
        double[] allocation = new double[3];

        for (int i = 1000; i < 7500; i += 375) {
            if (budget <= i) {
                for (int j = 0; j < 3; j++) {
                    allocation[j] = x[j];
                }
                return allocation;
            }
            x[0] -= 0.017;
            x[2] += 0.017;
            //Log.d(TAG, String.valueOf(i) + " - " + String.valueOf(x[0]) + " - " + String.valueOf(x[2]));
        }

        for (int j = 0; j < 3; j++) {
            allocation[j] = x[j];
        }
        return allocation;
    }

    private void addDataSet(PieChart pieChart) {
        Log.d(TAG, "addDataSet: ");
        yEntry = new ArrayList<>();
        yEnt = new ArrayList<>();
        xEntry = new ArrayList<>();

        int index = 0;
        Iterator i = necessities.iterator();
        insertPieData(index, i);
        i = saving.iterator();
        insertPieData(index, i);
        i = want.iterator();
        insertPieData(index, i);

        i = necessities.iterator();
        insertPieName(i);
        i = saving.iterator();
        insertPieName(i);
        i = want.iterator();
        insertPieName(i);


        /*for(int i = 0; i < score.length; i++ ){
            yEntry.add(new PieEntry(score[i],i));
        }

        for(int i = 0; i < score.length; i++ ){
            xEntry.add(name[i]);
        }*/

        PieDataSet pieDataSet = new PieDataSet(yEntry, "Budget");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);
        //pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        //pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setDrawValues(false);

        ArrayList<Integer> colors = new ArrayList<>();

        for (int k = 0; k < yEntry.size(); k++) {
            if (k % 7 == 0) {
                colors.add(Color.GRAY);
            } else if (k % 7 == 1) {
                colors.add(Color.parseColor("#5E6CBE"));
            } else if (k % 7 == 2) {
                colors.add(Color.RED);
            } else if (k % 7 == 3) {
                colors.add(Color.GREEN);
            } else if (k % 7 == 4) {
                colors.add(Color.CYAN);
            } else if (k % 7 == 5) {
                colors.add(Color.YELLOW);
            } else {
                colors.add(Color.MAGENTA);
            }
        }


        pieDataSet.setColors(colors);

        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);
        PieData pieData;
        pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    private void insertPieData(int index, Iterator i) {
        while (i.hasNext()) {
            ExpensesCategory c = (ExpensesCategory) i.next();
            DecimalFormat ds = new DecimalFormat("#.##");
            double entry = Double.parseDouble(ds.format(c.getBudget()));
            //double entry = c.getBudget();
            yEntry.add(new PieEntry(Float.parseFloat(Double.toString(entry)), index));
            yEnt.add(new Double(entry));
            index++;
        }

    }

    private void insertPieName(Iterator i) {
        while (i.hasNext()) {
            ExpensesCategory c = (ExpensesCategory) i.next();
            xEntry.add(c.getName());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_report) {
            startActivity(new Intent(MainPage.this, BugReport.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_add) {
            startActivityForResult(new Intent(MainPage.this, AddCategory.class), 1);
        } else if (id == R.id.nav_edit) {
            startActivityForResult(new Intent(MainPage.this, ModifyCategory.class), 1);
        } else if (id == R.id.nav_delete) {
            startActivityForResult(new Intent(MainPage.this, DeleteCategory.class), 1);
        } else if (id == R.id.nav_refresh) {
            SQLiteManager db1 = new SQLiteManager(getApplicationContext());
            db1.deleteData();
            getCategory();
        } else if (id == R.id.nav_logout) {
            SQLiteHandler db = new SQLiteHandler(getApplicationContext());
            db.deleteUsers();
            SQLiteManager db1 = new SQLiteManager(getApplicationContext());
            db1.deleteData();
            startActivity(new Intent(MainPage.this, MainActivity.class));
            SessionManager sm = new SessionManager(getApplicationContext());
            sm.setLogin(false);
            finish();
        } else if (id == R.id.nav_income) {
            startActivity(new Intent(MainPage.this, ViewCashflow.class).putExtra("cashflow", 1));
        } else if (id == R.id.nav_expenses) {
            startActivity(new Intent(MainPage.this, ViewCashflow.class).putExtra("cashflow", 2));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Intent refresh = new Intent(this, MainPage.class);
            startActivity(refresh);
            this.finish();
        }
    }

    private void getCategory() {
        String tag_string_req = "req_register";

        pDialog.setMessage("Refreshing...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GETCAT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean result = jObj.getBoolean("result");
                    if (result) {
                        JSONArray jsonArray = jObj.getJSONArray("category");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject json = jsonArray.getJSONObject(i);
                            db1.insertEarningCat(new Earning(json.getString("name")), getApplicationContext());
                        }
                        getExpensesCategory();
                    } else {
                        Log.e(TAG, "Error: " + jObj.getString("error_msg"));
                        hideDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    hideDialog();
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
                params.put("option", String.valueOf(1));

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void getExpensesCategory() {
        String tag_string_req = "req_register";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GETCAT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean result = jObj.getBoolean("result");
                    if (result) {
                        JSONArray jsonArray = jObj.getJSONArray("category");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject json = jsonArray.getJSONObject(i);
                            db1.insertExpensesCat(
                                    new ExpensesCategory(json.getString("name"), json.getInt("type")
                                            , json.getInt("proportion"), json.getDouble("budget")
                                    ), getApplicationContext());
                        }
                        getCashflow();
                    } else {
                        Log.e(TAG, "Error: " + jObj.getString("error_msg"));
                        hideDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    hideDialog();
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
                params.put("option", String.valueOf(2));

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void getCashflow() {
        String tag_string_req = "req_register";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GETCASH, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean result = jObj.getBoolean("result");
                    if (result) {
                        JSONArray jsonArray = jObj.getJSONArray("cashflow");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject json = jsonArray.getJSONObject(i);
                            db1.insertIncome(
                                    new Income(
                                            json.getDouble("amount"), Date.mysqlToDate(json.getString("time"))
                                            , json.getString("notes"), json.getString("category"))
                                    , getApplicationContext());
                        }
                        getExpenses();
                    } else {
                        Log.e(TAG, "Error: " + jObj.getString("error_msg"));
                        hideDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    hideDialog();
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
                params.put("option", String.valueOf(1));

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void getExpenses() {
        String tag_string_req = "req_register";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GETCASH, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean result = jObj.getBoolean("result");
                    if (result) {
                        JSONArray jsonArray = jObj.getJSONArray("cashflow");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject json = jsonArray.getJSONObject(i);
                            db1.insertExpenses(
                                    new Expenses(
                                            json.getDouble("amount"), Date.mysqlToDate(json.getString("time"))
                                            , json.getString("notes"), json.getString("category"))
                                    , getApplicationContext());
                        }
                        hideDialog();
                        startActivity(new Intent(MainPage.this, MainPage.class));
                        finish();
                    } else {
                        Log.e(TAG, "Error: " + jObj.getString("error_msg"));
                        hideDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    hideDialog();
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
                params.put("option", String.valueOf(2));

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
