package com.example.ng.yourbudget101.category;

/**
 * Created by ng on 01/05/2017.
 */

public class Want extends ExpensesCategory {

    public Want(String name, int proportion, double budget) {
        super(name, 3, proportion, budget);
    }

    public Want(String name, int proportion) {
        this(name, proportion, 0);
    }
}
