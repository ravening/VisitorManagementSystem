package com.leaseweb.global.visitormanagement.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.leaseweb.global.visitormanagement.model.Employee;
import com.leaseweb.global.visitormanagement.model.User;
import com.leaseweb.global.visitormanagement.model.Visitor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by rvenkatesh on 4/2/17.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    // All Static variables
    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "visitor_management";

    // Table names
    private static final String TABLE_EMPLOYEE = "employee";
    private static final String TABLE_VISITOR = "visitor";
    private static final String TABLE_USER = "user";

    // User Table Columns names
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USER_NAME = "user_name";
    private static final String COLUMN_USER_EMAIL = "user_email";
    private static final String COLUMN_USER_PASSWORD = "user_password";
    private static final String COLUMN_USER_ADMIN = "user_admin";

    // Common Table Column names
    private static final String KEY_ID = "id";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_PHONE_NUMBER = "phone";
    private static final String KEY_CHECKIN_TIME = "checkin_time";
    private static final String KEY_CHECKOUT_TIME = "checkout_time";
    private static final String KEY_BADGE_NUMBER = "badge_number";

    // Columns unique to visitor table
    private static final String KEY_REASON_FOR_VISIT = "reason_for_visit";
    private static final String KEY_CONTACT_PERSON = "contact_person";
    //private static final String KEY_COMPANY_FROM = "company";
    private static final String KEY_LICENSE_PLATE = "license_plate";

    // Columns unique to employee table
    private static final String KEY_DEPARTMENT = "department";

    // Visitor table create statement
    private static final String CREATE_TABLE_VISITOR = "CREATE TABLE IF NOT EXISTS " +
            TABLE_VISITOR + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_FIRST_NAME + " TEXT, " + KEY_LAST_NAME + " TEXT, " + KEY_PHONE_NUMBER +
            " TEXT, " + KEY_REASON_FOR_VISIT + " TEXT, " + KEY_CONTACT_PERSON +
            " TEXT, " + KEY_LICENSE_PLATE + " TEXT, " + KEY_BADGE_NUMBER + " INTEGER, "
            + KEY_CHECKIN_TIME + " DATETIME, " + KEY_CHECKOUT_TIME + " DATETIME" + ")";

    // Employee table create statement
    private static final String CREATE_TABLE_EMPLOYEE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_EMPLOYEE + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_FIRST_NAME + " TEXT, " + KEY_LAST_NAME + " TEXT, " + KEY_DEPARTMENT +
            " TEXT, " + KEY_PHONE_NUMBER + " TEXT, " + KEY_BADGE_NUMBER + " INTEGER, " +
            KEY_CHECKIN_TIME + " DATETIME, " + KEY_CHECKOUT_TIME + " DATETIME" + ")";


    // create table sql query
    private static final String  CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_USER_NAME + " TEXT, "
            + COLUMN_USER_EMAIL + " TEXT, " + COLUMN_USER_PASSWORD + " TEXT, " + COLUMN_USER_ADMIN + " INTEGER" + ")";

    // drop table sql query
    private static final String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;

    private static final int USER_ADMIN = 1;
    private static final int USER_GUEST = 0;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_VISITOR);
        db.execSQL(CREATE_TABLE_EMPLOYEE);
        db.execSQL(CREATE_USER_TABLE);

        // create default admin user
        User adminUser = new User("admin", "admin@leaseweb.com", "LeasewebAdmin", USER_ADMIN);
        User guestUser = new User("visitor", "visitor@leaseweb.com", "LeasewebAdmin", USER_GUEST);
        addUser(adminUser, db);
        addUser(guestUser,db);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VISITOR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMPLOYEE);
        db.execSQL(DROP_USER_TABLE);
        // Create tables again
        onCreate(db);
    }

    public String getCheckinTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String checkinTime = df.format(c.getTime());
        return checkinTime;
    }
    // Checkin new visitor
    public void checkinVisitor(Visitor visitor) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FIRST_NAME, visitor.getFirstName());
        values.put(KEY_LAST_NAME, visitor.getLastName());
        values.put(KEY_PHONE_NUMBER, visitor.getPhone());
        values.put(KEY_REASON_FOR_VISIT, visitor.getReasonForVisit());
        values.put(KEY_CONTACT_PERSON, visitor.getContactPerson());
        values.put(KEY_LICENSE_PLATE, visitor.getLicensePlate());
        values.put(KEY_BADGE_NUMBER, visitor.getBadgeNumber());
        values.put(KEY_CHECKOUT_TIME, "null");
        String checkinTime = getCheckinTime();
        values.put(KEY_CHECKIN_TIME, checkinTime);

        db.insert(TABLE_VISITOR, null, values);
    }

    // Checkin new Employee
    public void checkinEmployee(Employee employee) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FIRST_NAME, employee.getFirstName());
        values.put(KEY_LAST_NAME, employee.getLastName());
        values.put(KEY_BADGE_NUMBER, employee.getBadgeNumber());
        values.put(KEY_DEPARTMENT, employee.getDepartment());
        values.put(KEY_PHONE_NUMBER, employee.getPhoneNumber());
        values.put(KEY_CHECKOUT_TIME, "null");

        String checkinTime = getCheckinTime();
        values.put(KEY_CHECKIN_TIME, checkinTime);
        //Calendar c = Calendar.getInstance();
        //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //String checkinTime = df.format(c.getTime());

        db.insert(TABLE_EMPLOYEE, null, values);
        getAllEmployees();
    }

    public int checkoutVisitor(Long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_VISITOR + " WHERE " +
                                KEY_ID + "=" + id;

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null) {
            cursor.moveToLast();
        } else {
            return 0;
        }

        if (!cursor.getString(cursor.getColumnIndex(KEY_CHECKOUT_TIME)).equalsIgnoreCase("null")) {
            return -1;
        }

        Visitor visitor = new Visitor();
        visitor.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
        visitor.setFirstName(cursor.getString(cursor.getColumnIndex(KEY_FIRST_NAME)));
        visitor.setLastName(cursor.getString(cursor.getColumnIndex(KEY_LAST_NAME)));
        visitor.setPhone(cursor.getString(cursor.getColumnIndex(KEY_PHONE_NUMBER)));
        visitor.setReasonForVisit(cursor.getString(cursor.getColumnIndex(KEY_REASON_FOR_VISIT)));
        visitor.setContactPerson(cursor.getString(cursor.getColumnIndex(KEY_CONTACT_PERSON)));
        visitor.setLicensePlate(cursor.getString(cursor.getColumnIndex(KEY_LICENSE_PLATE)));
        visitor.setBadgeNumber(cursor.getInt(cursor.getColumnIndex(KEY_BADGE_NUMBER)));
        visitor.setCheckinTime(cursor.getString(cursor.getColumnIndex(KEY_CHECKIN_TIME)));

        /*Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String checkoutTime = df.format(cal.getTime());*/
        String checkoutTime = getCheckinTime();
        visitor.setCheckoutTime(checkoutTime);

        updateVisitor(visitor);
        cursor.close();
        return 1;
    }

    public int checkoutEmployee(Long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_EMPLOYEE + " WHERE " +
                                KEY_ID + "=" + id;

        Log.e("ID is ", String.valueOf(id));
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null) {
            cursor.moveToLast();
        } else {
            return 0;
        }
        Log.e("checkout time is ", cursor.getString(cursor.getColumnIndex(KEY_CHECKOUT_TIME)));
        if (!cursor.getString(cursor.getColumnIndex(KEY_CHECKOUT_TIME)).equalsIgnoreCase("null")) {
            return -1;
        }

        String checkoutTime = getCheckinTime();

        Employee employee = new Employee();
        employee.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
        employee.setFirstName(cursor.getString(cursor.getColumnIndex(KEY_FIRST_NAME)));
        employee.setLastName(cursor.getString(cursor.getColumnIndex(KEY_LAST_NAME)));
        employee.setBadgeNumber(cursor.getInt(cursor.getColumnIndex(KEY_BADGE_NUMBER)));
        employee.setDepartment(cursor.getString(cursor.getColumnIndex(KEY_DEPARTMENT)));
        employee.setPhoneNumber(cursor.getString(cursor.getColumnIndex(KEY_PHONE_NUMBER)));
        employee.setCheckinTime(cursor.getString(cursor.getColumnIndex(KEY_CHECKIN_TIME)));
        employee.setCheckoutTime(checkoutTime);

        updateEmployee(employee);
        cursor.close();
        return 1;
    }


    public int updateVisitor(Visitor visitor) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_FIRST_NAME, visitor.getFirstName());
        values.put(KEY_LAST_NAME, visitor.getLastName());
        values.put(KEY_PHONE_NUMBER, visitor.getPhone());
        values.put(KEY_REASON_FOR_VISIT, visitor.getReasonForVisit());
        values.put(KEY_CONTACT_PERSON, visitor.getContactPerson());
        values.put(KEY_LICENSE_PLATE, visitor.getLicensePlate());
        values.put(KEY_BADGE_NUMBER, visitor.getBadgeNumber());
        values.put(KEY_CHECKIN_TIME, visitor.getCheckinTime());
        values.put(KEY_CHECKOUT_TIME, visitor.getCheckoutTime());

        return db.update(TABLE_VISITOR, values, KEY_ID + " = ?",
                new String[] {String.valueOf(visitor.getId())});

    }

    public int updateEmployee(Employee employee) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_FIRST_NAME, employee.getFirstName());
        values.put(KEY_LAST_NAME, employee.getLastName());
        values.put(KEY_BADGE_NUMBER, employee.getBadgeNumber());
        values.put(KEY_DEPARTMENT, employee.getDepartment());
        values.put(KEY_PHONE_NUMBER, employee.getPhoneNumber());
        values.put(KEY_CHECKIN_TIME, employee.getCheckinTime());
        values.put(KEY_CHECKOUT_TIME, employee.getCheckoutTime());

        return db.update(TABLE_EMPLOYEE, values, KEY_ID + " = ?",
                new String[] {String.valueOf(employee.getId())});
    }

    public List<Visitor> getAllVisitors() {
        List<Visitor> visitorList = new ArrayList<Visitor>();

        // Select all query
        String selectQuery = "SELECT * FROM " + TABLE_VISITOR;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Visitor visitor = new Visitor();
                visitor.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
                visitor.setFirstName(cursor.getString(cursor.getColumnIndex(KEY_FIRST_NAME)));
                visitor.setLastName(cursor.getString(cursor.getColumnIndex(KEY_LAST_NAME)));
                visitor.setPhone(cursor.getString(cursor.getColumnIndex(KEY_PHONE_NUMBER)));
                visitor.setReasonForVisit(cursor.getString(cursor.getColumnIndex(KEY_REASON_FOR_VISIT)));
                visitor.setContactPerson(cursor.getString(cursor.getColumnIndex(KEY_CONTACT_PERSON)));
                visitor.setLicensePlate(cursor.getString(cursor.getColumnIndex(KEY_LICENSE_PLATE)));
                visitor.setBadgeNumber(cursor.getInt(cursor.getColumnIndex(KEY_BADGE_NUMBER)));
                visitor.setCheckinTime(cursor.getString(cursor.getColumnIndex(KEY_CHECKIN_TIME)));
                visitor.setCheckoutTime(cursor.getString(cursor.getColumnIndex(KEY_CHECKOUT_TIME)));
                visitorList.add(visitor);

                Log.d("Log is ", visitor.getFirstName() + " " + visitor.getLastName() + " " +
                visitor.getId() + " " + visitor.getPhone() + " " + visitor.getReasonForVisit() + " " +
                visitor.getContactPerson() + " " + visitor.getCheckinTime() + " " + visitor.getCheckoutTime());
            } while (cursor.moveToNext());
        }
        cursor.close();

        return visitorList;
    }

    public Visitor getVisitor(int badgeNumber) {
        Visitor visitor = new Visitor();
        String selectQuery = " SELECT * FROM " + TABLE_VISITOR + " WHERE " + KEY_BADGE_NUMBER + "=" + badgeNumber;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToLast()) {
            visitor.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
            visitor.setFirstName(cursor.getString(cursor.getColumnIndex(KEY_FIRST_NAME)));
            visitor.setLastName(cursor.getString(cursor.getColumnIndex(KEY_LAST_NAME)));
            visitor.setPhone(cursor.getString(cursor.getColumnIndex(KEY_PHONE_NUMBER)));
            visitor.setReasonForVisit(cursor.getString(cursor.getColumnIndex(KEY_REASON_FOR_VISIT)));
            visitor.setContactPerson(cursor.getString(cursor.getColumnIndex(KEY_CONTACT_PERSON)));
            visitor.setLicensePlate(cursor.getString(cursor.getColumnIndex(KEY_LICENSE_PLATE)));
            visitor.setBadgeNumber(cursor.getInt(cursor.getColumnIndex(KEY_BADGE_NUMBER)));
            visitor.setCheckinTime(cursor.getString(cursor.getColumnIndex(KEY_CHECKIN_TIME)));
            visitor.setCheckoutTime(cursor.getString(cursor.getColumnIndex(KEY_CHECKOUT_TIME)));
        }

        cursor.close();
        return visitor;
    }

    // TODO result can be many. so return list
    public Employee getEmployee(int badgeNumber) {
        Employee employee = new Employee();
        String selectQuery = "SELECT * FROM " + TABLE_EMPLOYEE + " WHERE " + KEY_BADGE_NUMBER + "=" + badgeNumber;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToLast()) {
            employee.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
            employee.setFirstName(cursor.getString(cursor.getColumnIndex(KEY_FIRST_NAME)));
            employee.setLastName(cursor.getString(cursor.getColumnIndex(KEY_LAST_NAME)));
            employee.setBadgeNumber(cursor.getInt(cursor.getColumnIndex(KEY_BADGE_NUMBER)));
            employee.setDepartment(cursor.getString(cursor.getColumnIndex(KEY_DEPARTMENT)));
            employee.setPhoneNumber(cursor.getString(cursor.getColumnIndex(KEY_PHONE_NUMBER)));
            employee.setCheckinTime(cursor.getString(cursor.getColumnIndex(KEY_CHECKIN_TIME)));
            employee.setCheckinTime(cursor.getString(cursor.getColumnIndex(KEY_CHECKOUT_TIME)));
        }

        cursor.close();
        return employee;
    }

    public List<Employee> getAllEmployees() {
        List<Employee> employeeList = new ArrayList<Employee>();

        // Select all query
        String selectQuery = "SELECT * FROM " + TABLE_EMPLOYEE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Employee employee = new Employee();
                employee.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
                employee.setFirstName(cursor.getString(cursor.getColumnIndex(KEY_FIRST_NAME)));
                employee.setLastName(cursor.getString(cursor.getColumnIndex(KEY_LAST_NAME)));
                employee.setBadgeNumber(cursor.getInt(cursor.getColumnIndex(KEY_BADGE_NUMBER)));
                employee.setDepartment(cursor.getString(cursor.getColumnIndex(KEY_DEPARTMENT)));
                employee.setPhoneNumber(cursor.getString(cursor.getColumnIndex(KEY_PHONE_NUMBER)));
                employee.setCheckinTime(cursor.getString(cursor.getColumnIndex(KEY_CHECKIN_TIME)));
                employee.setCheckoutTime(cursor.getString(cursor.getColumnIndex(KEY_CHECKOUT_TIME)));
                employeeList.add(employee);
                Log.d("Log is ", employee.getFirstName() + " " + employee.getLastName() + " " + " id is " +
                        employee.getId() + " " + employee.getBadgeNumber() + " " + employee.getDepartment() + " " + employee.getPhoneNumber()
                         + " " + employee.getCheckinTime() + " " + employee.getCheckoutTime());
            } while (cursor.moveToNext());
        }

        cursor.close();
        return employeeList;
    }

    public int getVisitorCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select * from " + TABLE_VISITOR;
        Cursor cursor = db.rawQuery(query, null);

        return cursor.getCount();
    }

    public int getEmployeeCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select * from " + TABLE_EMPLOYEE;
        Cursor cursor = db.rawQuery(query, null);

        return cursor.getCount();
    }

    public int getTotalVisitorCount() {
        return getVisitorCount() + getEmployeeCount();
    }

    public void deleteAllVisitor() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_VISITOR, null, null);
        db.close();
    }

    public void deleteAllEmployee() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EMPLOYEE, null, null);
    }

    // called while initializing the database to create the default admin account
    public void addUser(User user, SQLiteDatabase db) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());
        values.put(COLUMN_USER_ADMIN, user.isAdmin());

        // Inserting Row
        db.insert(TABLE_USER, null, values);
        //db.close();
    }

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());
        values.put(COLUMN_USER_ADMIN, user.isAdmin());

        // Inserting Row
        db.insert(TABLE_USER, null, values);
    }

    /**
     * This method is to fetch all user and return the list of user records
     *
     * @return list
     */
    public List<User> getAllUser() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID,
                COLUMN_USER_EMAIL,
                COLUMN_USER_NAME,
                COLUMN_USER_PASSWORD,
                COLUMN_USER_ADMIN
        };
        // sorting orders
        String sortOrder =
                COLUMN_USER_NAME + " ASC";
        List<User> userList = new ArrayList<User>();

        SQLiteDatabase db = this.getReadableDatabase();

        // query the user table
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID))));
                user.setName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(COLUMN_USER_PASSWORD)));
                user.setAdmin((cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ADMIN))));
                // Adding user record to list
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // return user list
        return userList;
    }


    public void updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());
        values.put(COLUMN_USER_ADMIN, user.isAdmin());

        // updating row
        db.update(TABLE_USER, values, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
    }


    public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete user record by id
        db.delete(TABLE_USER, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
    }


    public boolean checkUser(String email) {

        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = COLUMN_USER_EMAIL + " = ?";

        // selection argument
        String[] selectionArgs = {email};

        // query user table with condition
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com';
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();

        return (cursorCount > 0);
    }


    public int checkUser(String email, String password) {

        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ADMIN
        };
        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_USER_EMAIL + " = ?" + " AND " + COLUMN_USER_PASSWORD + " = ?";

        // selection arguments
        String[] selectionArgs = {email, password};

        // query user table with conditions
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com' AND user_password = 'qwerty';
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);                      //The sort order

        int cursorCount = cursor.getCount();

        if (cursorCount > 0) {
            Log.e("Cursor count is ", String.valueOf(cursor.getCount()));
            if (cursor.moveToFirst()) {
                if (cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ADMIN)) == 1) {
                    Log.e("User is ", "admin");
                    return 1;
                } else {
                    Log.e("User is ", "NOOOOOT admin");
                    return 2;
                }
            }
        } else {
            Log.e("Cursor count", " is 0");
        }

        return 0;
    }

    private Long getEpochValue(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateTimeToEpoch = new Date();
        try {
            dateTimeToEpoch = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateTimeToEpoch.getTime();
    }

    public void deleteEmployeesOlderThanThreeMonths()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_EMPLOYEE;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String checkinTime = cursor.getString(cursor.getColumnIndex(KEY_CHECKIN_TIME));
                Long checkinTimeEpoch = getEpochValue(checkinTime);
                Date today = new Date();
                Long threeMonthOldEpoch = today.getTime() - 7862400;
                if (checkinTimeEpoch < threeMonthOldEpoch) {
                    long id = cursor.getLong(cursor.getColumnIndex(KEY_ID));
                    db.delete(TABLE_EMPLOYEE, KEY_ID + " = ?",
                            new String[]{String.valueOf(id)});
                }
            } while (cursor.moveToNext());
        }

    }

    public void deleteVisitorsOlderThanThreeMonths()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_VISITOR;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String checkinTime = cursor.getString(cursor.getColumnIndex(KEY_CHECKIN_TIME));
                Long checkinTimeEpoch = getEpochValue(checkinTime);
                Date today = new Date();
                Long threeMonthOldEpoch = today.getTime() - 7862400;
                if (checkinTimeEpoch < threeMonthOldEpoch) {
                    long id = cursor.getLong(cursor.getColumnIndex(KEY_ID));
                    db.delete(TABLE_VISITOR, KEY_ID + " = ?",
                            new String[]{String.valueOf(id)});
                }
            } while (cursor.moveToNext());
        }

    }

    public int deleteVisitor(String lastName, String phone) {

        // array of columns to fetch
        String[] columns = {
                KEY_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = KEY_LAST_NAME + " = ?" + " AND " + KEY_PHONE_NUMBER + " = ?";

        // selection arguments
        String[] selectionArgs = {lastName, phone};

        Cursor cursor = db.query(TABLE_VISITOR, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);                      //The sort order

        int cursorCount = cursor.getCount();

        if (cursorCount > 0) {
            Log.e("Cursor count is ", String.valueOf(cursor.getCount()));
            if (cursor.moveToFirst()) {
                do {
                    Long id = cursor.getLong(cursor.getColumnIndex(KEY_ID));
                    Log.e("ID is ", id + " ");
                    deleteGuest(id, TABLE_VISITOR);
                } while (cursor.moveToNext());
            }
            return 1;
        } else {
            Log.e("Cursor count", " is 0");
            return 0;
        }
    }

    public int deleteEmployee(String lastName, String phone) {

        // array of columns to fetch
        String[] columns = {
                KEY_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = KEY_LAST_NAME + " = ?" + " AND " + KEY_PHONE_NUMBER + " = ?";

        // selection arguments
        String[] selectionArgs = {lastName, phone};

        Cursor cursor = db.query(TABLE_EMPLOYEE, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);                      //The sort order

        int cursorCount = cursor.getCount();

        if (cursorCount > 0) {
            Log.e("Cursor count is ", String.valueOf(cursor.getCount()));

            if (cursor.moveToFirst()) {
                do {
                    Long id = cursor.getLong(cursor.getColumnIndex(KEY_ID));
                    Log.e("ID is ", id + " ");
                    deleteGuest(id, TABLE_EMPLOYEE);
                } while (cursor.moveToNext());
            }
            return 1;
        } else {
            Log.e("Cursor count", " is 0");
        }

        return 0;
    }

    public void deleteGuest(Long id, String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete user record by id
        db.delete(tableName, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
    }
}

