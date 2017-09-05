package com.example.ng.yourbudget101.activity.cashflow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.ng.yourbudget101.R;
import com.example.ng.yourbudget101.cashflow.Cashflow;


/**
 * Created by ng on 6/12/17.
 */

public class ListViewFragment extends ArrayAdapter<Cashflow> {

    ListViewFragment(Context context, Cashflow[] cashflow) {
        super(context, R.layout.listview_fragment, cashflow);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.listview_fragment, parent, false);

        Cashflow cashflow = getItem(position);
        TextView textView_Date = (TextView) customView.findViewById(R.id.textView_Date);
        TextView textView_Category = (TextView) customView.findViewById(R.id.textView_Category);
        TextView textView_Amount = (TextView)customView.findViewById(R.id.textView_Amount);

        textView_Date.setText(cashflow.getDate().showDate());
        textView_Category.setText(cashflow.getCategory());
        textView_Amount.setText("RM" + String.valueOf(cashflow.getCashflow()));
        return customView;
    }
}
