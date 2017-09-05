package com.example.ng.yourbudget101.category;

/**
 * Created by ng on 01/05/2017.
 */

public class Saving extends ExpensesCategory {

    public Saving(String name, int proportion, double budget) {
        super(name, 2, proportion, budget);
    }

    public Saving(String name, int proportion) {
        this(name, proportion, 0);
    }
}
