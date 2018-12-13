# VisitorManagementSystem
This is the android app for the vistor management system which can be used in any company
This will allow the vistors/employers to checkin/checkout when they visit the office

How it works:

This app provides two views
1. Admin view and 2. Visitor view

In visitor view, visitors/employers can either select checkin or checkout option
When the visit the office, they select checkin option and enter the basic details like
First name, Last name, phone, contact person, reason for visit, license plate and the
badge number which was provided by facility desk. In case of employees, it will ask
department as well

Before leaving the office, they have to select the checkout option. This will ask them
to enter the badge number.


Admin view provides more functionality like
1. Configuring the app
2. Viewing all the visitors
3. Viewing all the employees
4. Deleting the visitors/employees
5. Export the data from database as csv file or email the files
6. View the graphs for number of visitors/employees
7. Add new users who can login to the app


How to setup the app for using it:

You need to perform the following actions to properly use it

1. Import the app in the android studio and change the username and password mentioned in the file
   "DatabaseHandler.java". There are two login credentials. One for the admin view and the other for the
   visitor view. You need to change it according to your needs.
2. After changing the username/password, start the app. Once the app starts, it asks for user name and password.
   The default username/password are mentioned in the file "DatabaseHandler.java"
3. To get the admin view, enter the details mentioned for "adminUser" variable in the file "DatabaseHandler.java"
4. To get the visitor view, enter the details mentioned for "guestUser" variable in the file "DatabaseHandler.java"
5. Before users can use the app to checkin/checkout, you need to configure the database to which the app can connect to
   so that it can store the data in the database.
   All the user details are stored in the remote database. So even if the app crashes or if you install the app in different
   places, you can connect to central database and access all the data.
6. In the admin view click on "Configure database" and enter the IP addres of the machine where database is running.
7. You can select the location as "AMS01"
8. If you dont enter the ip address of the database machine, the app wont work. If the app can successfully connect to the database
   then you can use the app else it will display error message
9. After condiguring the database IP in the app, you need to setup the database in the remote machine before users can actually start
   using the app.
10. In the root directory of this project there is a directory called "php_backend_scripts" which can all the important php and sql
   scripts to setup the database.
11. First create a database of your choice and run the command mentioned in the file "create_tables" to setup the tables in the database
12. You also need to mention the same database name in the file "Config.java". If you make any changes to the php file names, make sure to
   change them in the file "Config.java" as well
13. Once the database and the tables are configured, copy all the php scripts in "php_backend_scripts" to /var/www/html/<DATABASE_PATH>/
   on the machine where the database is running. You can get he "DATABASE_PATH" value from "Config.java" file
14. Make sure to give the write access to the tables
15. Thats it. The initial setup is done and login to the app using "guestUser" credentails found in "DatabaseHandler.java"
16. Now the users can start using the app to checkin/checkout
17. There are several scripts present in "php_backend_script" to view the data in the web itself.
18. For example to see all the visitors checked in, we can use http://<IP address of database machine>/<Database path>/visitors.php
19. For example to see all the employers checked in, we can use http://<IP address of database machine>/<Database path>/employees.php

