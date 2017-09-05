package com.example.ng.yourbudget101.category;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.Vector;

/**
 * Created by ng on 5/27/17.
 */

public class Earning extends Category {

    public Earning(String name) {
        super(name);
    }

    public static Vector<Earning> getDefaultEarCat(Context context) throws IOException {
        InputStream is = context.getAssets().open("IncomeCategory.txt");
        Scanner sc = new Scanner(is);
        Vector<Earning> categories = new Vector<Earning>();

        while (sc.hasNext()) {
            String name = sc.nextLine();
            Earning e = new Earning(name);
            categories.add(e);
        }
        return categories;
    }
}
