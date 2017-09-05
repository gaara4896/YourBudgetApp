package com.example.ng.yourbudget101.cashflow;

import com.example.ng.yourbudget101.category.Category;
import com.example.ng.yourbudget101.date.Date;

public class Cashflow {

    public static final String INCOME = "income";
    public static final String EXPENSES = "expenses";

    private double cashflow;
    private Date date;
    private String remark;
    private String category;

    protected Cashflow(double cashflow, Date date, String remark, String category) {
        this.cashflow = cashflow;
        this.date = date;
        this.remark = remark;
        this.category = category;
    }

    public double getCashflow() {
        return cashflow;
    }

    public void setCashflow(double cashflow) {
        this.cashflow = cashflow;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
