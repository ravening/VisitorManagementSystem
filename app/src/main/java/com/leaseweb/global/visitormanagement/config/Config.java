package com.leaseweb.global.visitormanagement.config;

import android.app.Activity;

public class Config extends Activity {
    public String DATABASE_IP = "10.11.60.100";
    public static final String sharedPreferenceName = "leaseweb_visitor_management";
    public static final String DB_HOST = "database_ip";
    public static final String LOCATION = "current_location";
    public static final String DATABASE_PATH = "leaseweb_visitor_management";
    public static final String DISPLAY_VISITORS = "get_all_visitors.php";
    public static final String DISPLAY_EMPLOYEES = "get_all_employees.php";
    public static final String DISPLAY_SUPPLIERS = "get_all_suppliers.php";
    public static final String CHECKIN_EMPLOYEE = "checkin_employee.php";
    public static final String CHECKIN_VISITOR = "checkin_visitor.php";
    public static final String CHECKOUT_EMPLOYEE = "checkout_employee.php";
    public static final String CHECKOUT_VISITOR = "checkout_visitor.php";
    public static final String DELETE_EMPLOYEE = "delete_employees.php";
    public static final String DELETE_VISITOR = "delete_visitors.php";
    public static final String DELETE_SUPPLIER = "delete_suppliers.php";
    public static final String DELETE_GUEST = "delete_guest.php";
    public static final String CHECKIN_SUPPLIER = "checkin_supplier.php";
    public static final String CHECKOUT_SUPPLIER = "checkout_supplier.php";
}
