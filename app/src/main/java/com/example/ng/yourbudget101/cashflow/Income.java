package com.example.ng.yourbudget101.cashflow;

import com.example.ng.yourbudget101.category.Earning;
import com.example.ng.yourbudget101.date.Date;

/**
 * Created by ng on 5/27/17.
 */

public class Income extends Cashflow {

    public Income(double cashflow, Date date, String remark, String category) {
        super(cashflow, date, remark, category);
    }
}
