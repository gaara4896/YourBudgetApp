package com.example.ng.yourbudget101.network;

/**
 * Created by ng on 5/26/17.
 */

public class AppConfig {

    private static String LOCAL_IP = "172.18.65.153";
    private static String PUBLIC_IP = "115.133.215.238";
    private static String HOUSE_LOCAL = "192.168.1.7";

    private static String IP_ADDRESS = PUBLIC_IP;
    // Server user login url
    public static String URL_LOGIN = "http://" + IP_ADDRESS + "/login.php";

    // Server user register url
    public static String URL_REGISTER = "http://" + IP_ADDRESS + "/register.php";

    // Server user update prompt
    public static String URL_PROMPT = "http://" + IP_ADDRESS + "/prompt.php";

    // Server user insert category
    public static String URL_INSERTCAT = "http://" + IP_ADDRESS + "/insertCategory.php";

    // Server user get category
    public static String URL_GETCAT = "http://" + IP_ADDRESS + "/getCategory.php";

    // Server user edit category
    public static String URL_EDITCAT = "http://" + IP_ADDRESS + "/modifyCategory.php";

    // Server user delete category
    public static String URL_DELCAT = "http://" + IP_ADDRESS + "/deleteCategory.php";

    //Server user insert cashflow
    public static String URL_INSERTCASH = "http://" + IP_ADDRESS + "/insertCashflow.php";

    //Server user get cashflow
    public static String URL_GETCASH = "http://" + IP_ADDRESS + "/getCashflow.php";

    //Server insert bug
    public static String URL_REPORTBUG = "http://" + IP_ADDRESS + "/insertBugReport.php";
}
