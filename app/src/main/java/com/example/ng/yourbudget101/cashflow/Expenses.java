package com.example.ng.yourbudget101.cashflow;

import com.example.ng.yourbudget101.category.ExpensesCategory;
import com.example.ng.yourbudget101.date.Date;

/**
 * Created by ng on 5/27/17.
 */

public class Expenses extends Cashflow {

    public Expenses(double cashflow, Date date, String remark, String category) {
        super(cashflow, date, remark, category);
    }
}
