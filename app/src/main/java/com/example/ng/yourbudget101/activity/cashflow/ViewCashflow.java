package com.example.ng.yourbudget101.activity.cashflow;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ng.yourbudget101.R;
import com.example.ng.yourbudget101.activity.database.SQLiteManager;
import com.example.ng.yourbudget101.cashflow.Cashflow;

import java.util.Iterator;
import java.util.Vector;

public class ViewCashflow extends AppCompatActivity {

    Toolbar toolbar;
    ListView listView_Cashflow;
    SQLiteManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cashflow);

        toolbar = (Toolbar) findViewById(R.id.include);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Budget");
        getSupportActionBar().setIcon(R.drawable.icon);
        toolbar.setTitleTextColor(Color.WHITE);

        listView_Cashflow = (ListView)findViewById(R.id.listView_Cashflow);
        db = new SQLiteManager(getApplicationContext());
        Vector v;

        final int cashflow = getIntent().getExtras().getInt("cashflow");
        if(cashflow == 1){
            v = db.getIncome();
        } else {
            v = db.getExpenses();
        }

        final Cashflow[] show = new Cashflow[v.size()];

        int counter = 0;
        Iterator i = v.iterator();
        while(i.hasNext()){
            show[counter] = (Cashflow)i.next();
            counter++;
        }

        ListAdapter adapter = new ListViewFragment(this, show);
        listView_Cashflow.setAdapter(adapter);

        listView_Cashflow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cashflow temp = show[position];
                Toast.makeText(ViewCashflow.this,
                        "Date = " + temp.getDate().showDate() +
                                "\n" + " Category = " + temp.getCategory() +
                                "\n" + " Amount = " + temp.getCashflow() +
                                "\n" + " Notes = " + temp.getRemark(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
