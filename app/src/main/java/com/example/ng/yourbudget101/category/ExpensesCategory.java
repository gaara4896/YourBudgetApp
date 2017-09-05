package com.example.ng.yourbudget101.category;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.Vector;

/**
 * Created by ng on 5/27/17.
 */

public class ExpensesCategory extends Category {


    private int type;
    private int proportion;
    private double budget;

    public ExpensesCategory(String name, int type, int proportion, double budget) {
        super(name);
        this.type = type;
        this.proportion = proportion;
        this.budget = budget;
    }

    public ExpensesCategory(String name, int type, int proportion) {
        this(name, type, proportion, 0);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getProportion() {
        return proportion;
    }

    public void setProportion(int proportion) {
        this.proportion = proportion;
    }

    public double getBudget() {
        return this.budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public static Vector<ExpensesCategory> getDefaultExpCat(Context context) throws IOException {
        InputStream is = context.getAssets().open("ExpensesCategory.txt");
        Scanner sc = new Scanner(is);
        Vector<ExpensesCategory> categories = new Vector<ExpensesCategory>();

        while (sc.hasNext()) {
            String name = sc.next();
            String[] temp = name.split("-");
            StringBuilder sb = new StringBuilder(temp[0]);
            for (int i = 1; i < temp.length; i++) {
                sb.append(" " + temp[i]);
            }
            name = sb.toString();
            int type = sc.nextInt();
            int proportion = sc.nextInt();
            ExpensesCategory e = new ExpensesCategory(name, type, proportion);
            categories.add(e);
        }
        return categories;
    }
}
