package com.example.ng.yourbudget101.activity.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.example.ng.yourbudget101.cashflow.Expenses;
import com.example.ng.yourbudget101.cashflow.Income;
import com.example.ng.yourbudget101.category.Earning;
import com.example.ng.yourbudget101.category.ExpensesCategory;
import com.example.ng.yourbudget101.date.Date;
import com.example.ng.yourbudget101.network.SQLiteHandler;

import java.util.Vector;

/**
 * Created by ng on 5/28/17.
 */

public class SQLiteManager extends SQLiteOpenHelper {

    private static final String TAG = SQLiteManager.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "cashFlow";
    private static final String TABLE_NAME1 = "earningCategory";
    private static final String T_COL = "userUid";
    private static final String T_COL1 = "name";
    private static final String TABLE_NAME2 = "expensesCategory";
    private static final String T2_COL2 = "type";
    private static final String T2_COL3 = "proportion";
    private static final String T2_COL4 = "budget";
    private static final String TABLE_NAME3 = "income";
    private static final String TABLE_NAME4 = "expenses";
    private static final String T3_COL1 = "id";
    private static final String T3_COL2 = T_COL;
    private static final String T3_COL3 = "category";
    private static final String T3_COL4 = "time";
    private static final String T3_COL5 = "amount";
    private static final String T3_COL6 = "notes";

    private static final String DATABASE1_CREATE = "create table "
            + TABLE_NAME1 + "( " + T_COL
            + " text not null, " + T_COL1
            + " text primary key);";
    private static final String DATABASE2_CREATE = "create table "
            + TABLE_NAME2 + "( " + T_COL
            + " text not null, " + T_COL1
            + " text primary key not null, " + T2_COL2
            + " integer not null, " + T2_COL3
            + " integer not null, " + T2_COL4
            + " real);";
    private static final String DATABASE3_CREATE = "create table "
            + TABLE_NAME3 + "( " + T3_COL1
            + " integer primary key autoincrement, " + T3_COL2
            + " text not null, " + T3_COL3
            + " text not null, " + T3_COL4
            + " date not null, " + T3_COL5
            + " real not null, " + T3_COL6
            + " text);";
    private static final String DATABASE4_CREATE = "create table "
            + TABLE_NAME4 + "( " + T3_COL1
            + " integer primary key autoincrement, " + T3_COL2
            + " text not null, " + T3_COL3
            + " text not null, " + T3_COL4
            + " date not null, " + T3_COL5
            + " real not null, " + T3_COL6
            + " text);";

    public SQLiteManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE1_CREATE);
        db.execSQL(DATABASE2_CREATE);
        db.execSQL(DATABASE3_CREATE);
        db.execSQL(DATABASE4_CREATE);

        Log.d(TAG, "DATABASE CREATED");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public boolean insertEarningCat(Earning ea, Context context) {
        SQLiteHandler user = new SQLiteHandler(context);
        if (TextUtils.isEmpty(user.getUserId())) {
            return false;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(T_COL, user.getUserId());
        contentValues.put(T_COL1, ea.getName());
        long result = db.insert(TABLE_NAME1, null, contentValues);
        db.close();
        return !(result == -1);
    }

    public Vector<Earning> getEarningCat() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME1, null);
        Vector<Earning> vec = new Vector<Earning>();
        while (cursor.moveToNext()) {
            vec.add(new Earning(cursor.getString(1)));
        }
        cursor.close();
        db.close();
        return vec;
    }

/*    public Earning searchEarningCat(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME1, null);
        Earning e = null;
        while (cursor.moveToNext()) {
            if (cursor.getString(1).equals(name)) {
                e = new Earning(cursor.getString(1));
            }
        }
        cursor.close();
        db.close();
        return e;
    }*/

    public void updateEarnignCat(String name, Earning e) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(T_COL1, e.getName());
        db.update(TABLE_NAME1, contentValues, T_COL1 + " = ?", new String[]{name});
        db.close();
    }

    public void deleteEarningCat(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME1 + " WHERE " + T_COL1 + " = '" + name + "'; ");
        db.close();
    }

    public boolean insertExpensesCat(ExpensesCategory ex, Context context) {
        SQLiteHandler user = new SQLiteHandler(context);
        if (TextUtils.isEmpty(user.getUserId())) {
            return false;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(T_COL, user.getUserId());
        contentValues.put(T_COL1, ex.getName());
        contentValues.put(T2_COL2, ex.getType());
        contentValues.put(T2_COL3, ex.getProportion());
        contentValues.put(T2_COL4, ex.getBudget());
        long result = db.insert(TABLE_NAME2, null, contentValues);
        db.close();
        return !(result == -1);
    }

    public Vector<ExpensesCategory> getExpensesCat() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME2, null);
        Vector<ExpensesCategory> vec = new Vector<ExpensesCategory>();
        while (cursor.moveToNext()) {
            vec.add(new ExpensesCategory(cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getDouble(4)));
        }
        cursor.close();
        db.close();
        return vec;
    }

/*    public ExpensesCategory searchExpensesCat(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME2, null);
        ExpensesCategory e = null;
        while (cursor.moveToNext()) {
            if (cursor.getString(1).equals(name)) {
                e = new ExpensesCategory(cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getDouble(4));
            }
        }
        cursor.close();
        db.close();
        return e;
    }*/

    public void updateExpensesCat(String name, ExpensesCategory ex) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(T_COL1, ex.getName());
        contentValues.put(T2_COL2, ex.getType());
        contentValues.put(T2_COL3, ex.getProportion());
        contentValues.put(T2_COL4, ex.getBudget());
        db.update(TABLE_NAME2, contentValues, T_COL1 + " = ?", new String[]{name});
        db.close();
    }

    public void deleteExpensesCat(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME2 + " WHERE " + T_COL1 + " = '" + name + "'; ");
        db.close();
    }

    public boolean insertIncome(Income income, Context context) {
        SQLiteHandler user = new SQLiteHandler(context);
        if (TextUtils.isEmpty(user.getUserId())) {
            return false;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(T3_COL2, user.getUserId());
        contentValues.put(T3_COL3, income.getCategory());
        contentValues.put(T3_COL4, income.getDate().toString());
        contentValues.put(T3_COL5, income.getCashflow());
        contentValues.put(T3_COL6, income.getRemark());
        long result = db.insert(TABLE_NAME3, null, contentValues);
        db.close();
        return !(result == -1);
    }

    public Vector<Income> getIncome() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME3, null);
        Vector<Income> vec = new Vector<Income>();
        while (cursor.moveToNext()) {
            vec.add(new Income(cursor.getDouble(4), new Date(cursor.getString(3)), cursor.getString(5), cursor.getString(2)));
        }
        cursor.close();
        db.close();
        return vec;
    }

    public boolean insertExpenses(Expenses e, Context context) {
        SQLiteHandler user = new SQLiteHandler(context);
        if (TextUtils.isEmpty(user.getUserId())) {
            return false;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(T3_COL2, user.getUserId());
        contentValues.put(T3_COL3, e.getCategory());
        contentValues.put(T3_COL4, e.getDate().toString());
        contentValues.put(T3_COL5, e.getCashflow());
        contentValues.put(T3_COL6, e.getRemark());
        long result = db.insert(TABLE_NAME4, null, contentValues);
        db.close();
        return !(result == -1);
    }

    public Vector<Expenses> getExpenses() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME4, null);
        Vector<Expenses> vec = new Vector<Expenses>();
        while (cursor.moveToNext()) {
            vec.add(new Expenses(cursor.getDouble(4), new Date(cursor.getString(3)), cursor.getString(5), cursor.getString(2)));
        }
        cursor.close();
        db.close();
        return vec;
    }

    public void deleteData() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_NAME1, null, null);
        db.delete(TABLE_NAME2, null, null);
        db.delete(TABLE_NAME3, null, null);
        db.delete(TABLE_NAME4, null, null);
        db.close();

        Log.d(TAG, "Deleted all cashflow info from sqlite");
    }
}
