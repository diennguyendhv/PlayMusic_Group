package context.app_sql;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import context.app_data.AppLogin;
import context.app_data.Customer;
import context.app_default.AppDefault;
import context.app_default.DbDefault;

public class CustomerHelper extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "Customer";
    private static final String TAG = "CustomerHelper";
    public CustomerHelper(@Nullable Context context) {
        super(context, DbDefault.DATABASE_NAME, null, DbDefault.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.i(TAG, "Create table");
        String queryCreateTable = "CREATE TABLE " + TABLE_NAME + " ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name VARCHAR (255) NOT NULL, " +
                "username VARCHAR (255) NOT NULL, " +
                "password VARCHAR (255) NOT NULL," +
                "statusid INTEGER NOT NULL" +
                ")";

        db.execSQL(queryCreateTable);
        InsertCustomerDefault();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    public List<Customer> getAllCustomers() {

        List<Customer> items = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, name, username, password, statusid from " +  TABLE_NAME
                + " Where statusid > 0", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            items.add(new Customer(
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4)));
            cursor.moveToNext();
        }

        cursor.close();
        return items;
    }


    public Customer getCustomerByID(int ID) {
        Customer item = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, name, username, password, statusid from "+ TABLE_NAME +" where id = ?",
                new String[]{ID + ""});

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            item = new Customer(
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4));
        }
        cursor.close();
        return item;
    }

    public Customer getCustomerByUsername(String username) {
        username = username.toLowerCase();
        Customer item = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, name, username, password, statusid from "+ TABLE_NAME +" where username = ?",
                new String[]{username });

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            item = new Customer(
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4));
        }
        cursor.close();
        return item;
    }

    public Customer getCustomerForLogin(AppLogin appLogin) {
        Customer item = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, name, username, password, statusid from "+ TABLE_NAME
                        +" where username = ? and password = ?",
                new String[]{appLogin.Username,appLogin.Password });

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            item = new Customer(
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4));;
        }
        cursor.close();
        return item;
    }

    public void updateCustomer(Customer entity) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_NAME + " SET name=?, password = ?, stautusid = ? where id = ?",
                new String[]{entity.getName(), entity.getUserName(), entity.getStatusId() + "", entity.getId() + "",});
    }


    public void insertCustomer(Customer entity) {
        entity.setUserName(entity.getUserName().toLowerCase()) ;
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO " + TABLE_NAME +" (name, username, password, statusid ) VALUES (?,?,?,1)",
                new String[]{entity.getName(), entity.getUserName(), entity.getPassword()});
    }


    public void InsertCustomerDefault(){
        Customer customer = getCustomerByUsername(AppDefault.CustomerDefaultUsername);
        if(customer == null){
            Customer customer2 = new Customer("Mặc định",AppDefault.CustomerDefaultUsername,AppDefault.CustomerDefaultPassswor,1);
            insertCustomer(customer2);
        }
    }


    public  void deleteCustomerByID(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME +" where id = ?", new String[]{String.valueOf(id)});
    }



}
