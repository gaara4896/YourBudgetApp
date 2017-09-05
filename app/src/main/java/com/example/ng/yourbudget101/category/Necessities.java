package com.example.ng.yourbudget101.category;

/**
 * Created by ng on 01/05/2017.
 */

public class Necessities extends ExpensesCategory {

    public Necessities(String name, int proportion, double budget) {
        super(name, 1, proportion, budget);
    }

    public Necessities(String name, int proportion) {
        this(name, proportion, 0);
    }
}
