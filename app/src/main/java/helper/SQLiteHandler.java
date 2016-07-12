package helper;

/**
 * Created by Administrator on 08/04/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

import model.RowCategory;
import model.RowSubItem;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();
    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 6;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Login table name
    private static final String TABLE_LOGIN = "login";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_FIRSTNAME = "firstname";
    private static final String KEY_SURNAME = "surname";
    private static final String KEY_USERID = "userID";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_UID = "uid";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_CREATE_ABILITY = "create_ability";

    // Products Table Columns names
    public static final String TABLE_REMINDERS = "reminders";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_REMINT = "rem_Int";
    public static final String COLUMN_CATID = "cat_ID";

    public static final String COLUMN_CATEGORY = "Category";
    public static final String COLUMN_CATEGORYARCHIVE = "category_Archive";
    public static final String COLUMN_CATDESC = "cat_Desc";
    public static final String COLUMN_ACTDATE = "act_Date";
    public static final String COLUMN_ACTDAYS = "act_Days";
    public static final String COLUMN_ACTREM = "act_Rem";
    public static final String COLUMN_ACTEXPIRY = "act_Expiry";
    public static final String COLUMN_ACTTITLE = "activation_Title";
    public static final String COLUMN_REMINDER = "Reminder";
    public static final String COLUMN_REMDESC = "rem_Desc";
    public static final String COLUMN_REMINDERARCHIVED = "rem_Archived";
    public static final String COLUMN_IMAGEA = "imageA";
    public static final String COLUMN_IMAGEB = "imageB";
    public static final String COLUMN_REMDATE = "rem_Date";
    public static final String COLUMN_REMEXPIRY = "rem_Expiry";
    public static final String COLUMN_REMNOTES = "rem_Notes";
    public static final String COLUMN_UPLOAD = "upload";
    public static final String COLUMN_USERID = "userID";
    public static final String COLUMN_CATUPLOADID = "cat_UploadID";
    public static final String COLUMN_REMUPLOADID = "rem_UploadID";
    public static final String COLUMN_UPLOADSUM = "uploadSum";

    public static final String TABLE_DOWNLOAD = "download";
    public static final String ID_COLUMN = "_id";
    public static final String REMINT_COLUMN_= "rem_Int";
    public static final String CATID_COLUMN = "cat_ID";

    public static final String CATEGORY_COLUMN = "Category";
    public static final String CATEGORYARCHIVE_COLUMN = "category_Archive";
    public static final String CATDESC_COLUMN = "cat_Desc";
    public static final String ACTDATE_COLUMN = "act_Date";
    public static final String ACTDAYS_COLUMN = "act_Days";
    public static final String ACTREM_COLUMN = "act_Rem";
    public static final String ACTEXPIRY_COLUMN = "act_Expiry";
    public static final String ACTTITLE_COLUMN = "activation_Title";
    public static final String REMINDER_COLUMN = "Reminder";
    public static final String REMINDERARCHIVED_COLUMN= "rem_Archived";
    public static final String REMDATE_COLUMN = "rem_Date";
    public static final String REMEXPIRY_COLUMN = "rem_Expiry";
    public static final String REMNOTES_COLUMN = "rem_Notes";
    public static final String UPLOAD_COLUMN = "upload";
    public static final String USERID_COLUMN = "userID";
    public static final String CATUPLOADID_COLUMN = "cat_UploadID";
    public static final String REMUPLOADID_COLUMN = "rem_UploadID";
    public static final String UPLOADSUM_COLUMN = "uploadSum";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("+
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_FIRSTNAME + " TEXT,"  +
                KEY_SURNAME + " TEXT," +
                KEY_USERID + " TEXT," +
                KEY_EMAIL + " TEXT UNIQUE," +
                KEY_COUNTRY + " TEXT," +
                KEY_UID + " TEXT," +
                KEY_CREATED_AT + " TEXT," +
                KEY_CREATE_ABILITY + " TEXT" +
                ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database tables created");

        String query = "CREATE TABLE " + TABLE_REMINDERS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_REMINT + " TEXT, " +
                COLUMN_CATID + " TEXT, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_CATEGORYARCHIVE + " TEXT, " +
                COLUMN_CATDESC + " TEXT, " +
                COLUMN_ACTDATE + " TEXT, " +
                COLUMN_ACTDAYS + " TEXT, " +
                COLUMN_ACTREM + " TEXT, " +
                COLUMN_ACTEXPIRY + " TEXT, " +
                COLUMN_ACTTITLE + " TEXT, " +
                COLUMN_REMINDER + " TEXT, " +
                COLUMN_REMINDERARCHIVED + " TEXT, " +
                COLUMN_IMAGEA + " TEXT, " +
                COLUMN_IMAGEB + " TEXT, " +
                COLUMN_REMDATE + " BIGINT, " +
                COLUMN_REMEXPIRY + " BIGINT, " +
                COLUMN_REMNOTES + " TEXT, " +
                COLUMN_UPLOAD + " TEXT, " +
                COLUMN_USERID + " TEXT, " +
                COLUMN_CATUPLOADID + " TEXT, " +
                COLUMN_REMUPLOADID + " TEXT, " +
                COLUMN_UPLOADSUM + " TEXT " +
                ")";
        db.execSQL(query);

        Log.d(TAG, "Products tables created");

        String downoadTable = "CREATE TABLE " + TABLE_DOWNLOAD + "(" +
                ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CATID_COLUMN + " TEXT, " +
                CATEGORY_COLUMN + " TEXT, " +
                CATEGORYARCHIVE_COLUMN + " TEXT, " +
                CATDESC_COLUMN+ " TEXT, " +
                UPLOAD_COLUMN + " TEXT, " +
                USERID_COLUMN + " TEXT, " +
                UPLOADSUM_COLUMN + " TEXT " +
                ")";
        db.execSQL(downoadTable);

        Log.d(TAG, "Download tables created");

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN + TABLE_REMINDERS + TABLE_DOWNLOAD);


        // Create tables again
        onCreate(db);
    }
    // Getting All Contacts

//    public List<Contact> getAllContacts() {
  //      List<Contact> contactList = new ArrayList<Contact>();
    //    // Select All Query
      //  String selectQuery = "SELECT  * FROM contacts ORDER BY DESC";
      //  SQLiteDatabase db = this.getWritableDatabase();
       // Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
      //  if (cursor.moveToFirst()) {
       // do {Contact contact = new Contact();
        //    contact.setID(Integer.parseInt(cursor.getString(0)));
         //   contact.setName(cursor.getString(1));
          //  contact.setImage(cursor.getBlob(2));
            // Adding contact to list
          //  contactList.add(contact);
           // } while (cursor.moveToNext());
        //}
        //// close inserting data from database
        //db.close();
        //// return contact list
       // return contactList;
   // }


    public Cursor fetchAllCategories(String archivedCategory) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCursor = db.query(true, TABLE_REMINDERS, new String[] {COLUMN_CATID,COLUMN_CATEGORY, COLUMN_CATEGORYARCHIVE, COLUMN_CATDESC,COLUMN_ACTDATE, COLUMN_USERID},
                COLUMN_CATEGORYARCHIVE+"=?",new String[]{archivedCategory}, null, null,COLUMN_CATEGORY+" asc",null);



        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor fetchUserCategories(String archivedCategory,String userID) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCursor = db.query(true, TABLE_REMINDERS, new String[]{COLUMN_CATID, COLUMN_CATEGORY, COLUMN_CATEGORYARCHIVE, COLUMN_CATDESC, COLUMN_ACTDATE, COLUMN_ACTDAYS, COLUMN_ACTTITLE, COLUMN_UPLOAD, COLUMN_USERID, COLUMN_UPLOADSUM,COLUMN_CATUPLOADID},
                COLUMN_CATEGORYARCHIVE + "=? AND " + COLUMN_USERID + "=? OR " + COLUMN_USERID + "=?", new String[]{archivedCategory, userID, "TaskiQ"}, null, null, COLUMN_CATEGORY + " asc", null);


        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    public Cursor fetchAllUserCategories(String archivedCategory,String userID) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCursor = db.query(true, TABLE_REMINDERS, new String[] {COLUMN_CATID,COLUMN_CATEGORY, COLUMN_CATEGORYARCHIVE, COLUMN_CATDESC,COLUMN_UPLOAD, COLUMN_USERID, COLUMN_UPLOADSUM},
                COLUMN_CATEGORYARCHIVE + "=? AND " + COLUMN_USERID + "=?",new String[]{archivedCategory,userID}, null, null,COLUMN_CATEGORY+" asc",null);



        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
// $email,$cat_ID,$act_Days,$userID,$catUploadID

    public Cursor fetchUpdateCat(String userID) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCursor = db.query(true, TABLE_REMINDERS, new String[] {COLUMN_CATID,COLUMN_ACTDAYS,COLUMN_USERID, COLUMN_CATUPLOADID},
                COLUMN_USERID + "!=?",new String[]{userID}, null, null,COLUMN_CATID+" asc",null);



        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }




    public Cursor fetchAllDownloads(String archivedCategory) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCursor = db.query(true, TABLE_REMINDERS, new String[] {COLUMN_CATID,COLUMN_CATEGORY, COLUMN_CATEGORYARCHIVE, COLUMN_CATDESC,COLUMN_UPLOAD, COLUMN_USERID, COLUMN_UPLOADSUM,COLUMN_CATUPLOADID},
                COLUMN_CATEGORYARCHIVE + "=?",new String[]{archivedCategory}, null, null,COLUMN_CATEGORY+" asc",null);



        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor fetchAllReminders(String catID,String archivedReminders) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCursor = db.query(TABLE_REMINDERS, new String[]{COLUMN_REMINT, COLUMN_CATID, COLUMN_CATEGORY, COLUMN_CATEGORYARCHIVE, COLUMN_CATDESC,
                        COLUMN_ACTDATE, COLUMN_ACTDAYS, COLUMN_ACTREM, COLUMN_ACTEXPIRY, COLUMN_ACTTITLE, COLUMN_REMINDER, COLUMN_REMINDERARCHIVED,COLUMN_IMAGEA,COLUMN_IMAGEB, COLUMN_REMDATE, COLUMN_REMEXPIRY,
                        COLUMN_REMNOTES, COLUMN_UPLOAD, COLUMN_USERID, COLUMN_CATUPLOADID, COLUMN_REMUPLOADID, COLUMN_UPLOADSUM},
                //     COLUMN_PRODUCTNAME + "=?", new String[]{title}, null, null, COLUMN_PRODUCTSUB + " asc", null);
                COLUMN_CATID + "=? AND " + COLUMN_REMINDERARCHIVED + "=?", new String[]{catID, archivedReminders}, null, null, COLUMN_REMINDER + " asc", null);

        //COLUMN_PRDNAMEHIDE + "=" + "0"
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor fetchAllRem(String catID, String archivedReminders) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCursor = db.query(TABLE_REMINDERS, new String[]{COLUMN_REMINT, COLUMN_CATID, COLUMN_CATEGORY, COLUMN_CATEGORYARCHIVE, COLUMN_CATDESC,
                        COLUMN_ACTDATE, COLUMN_ACTDAYS, COLUMN_ACTREM, COLUMN_ACTEXPIRY, COLUMN_ACTTITLE, COLUMN_REMINDER, COLUMN_REMINDERARCHIVED,COLUMN_IMAGEA,COLUMN_IMAGEB,  COLUMN_REMDATE, COLUMN_REMEXPIRY,
                        COLUMN_REMNOTES, COLUMN_UPLOAD, COLUMN_USERID, COLUMN_CATUPLOADID, COLUMN_REMUPLOADID, COLUMN_UPLOADSUM},
                //     COLUMN_PRODUCTNAME + "=?", new String[]{title}, null, null, COLUMN_PRODUCTSUB + " asc", null);
                COLUMN_CATID + "=? AND " + COLUMN_REMINDERARCHIVED + "=?", new String[]{catID, archivedReminders}, null, null, COLUMN_REMDATE + " asc", null);

        //COLUMN_PRDNAMEHIDE + "=" + "0"
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor fetchAllRemYes(String catID) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCursor = db.query(TABLE_REMINDERS, new String[]{COLUMN_REMINT, COLUMN_CATID, COLUMN_ACTREM, COLUMN_ACTEXPIRY, COLUMN_REMDATE, COLUMN_REMEXPIRY},

                COLUMN_CATID + "=?" , new String[]{catID}, null, null, COLUMN_REMINT + " asc", null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor fetchAllRemUpload(String catID, String userID, String archivedReminders) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCursor = db.query(TABLE_REMINDERS, new String[]{COLUMN_REMINT, COLUMN_CATID, COLUMN_CATEGORY, COLUMN_CATEGORYARCHIVE, COLUMN_CATDESC,
                        COLUMN_ACTDATE, COLUMN_ACTDAYS, COLUMN_ACTREM, COLUMN_ACTEXPIRY, COLUMN_ACTTITLE, COLUMN_REMINDER, COLUMN_REMINDERARCHIVED,COLUMN_IMAGEA,COLUMN_IMAGEB,  COLUMN_REMDATE, COLUMN_REMEXPIRY,
                        COLUMN_REMNOTES, COLUMN_UPLOAD, COLUMN_USERID, COLUMN_CATUPLOADID, COLUMN_REMUPLOADID, COLUMN_UPLOADSUM},
                //     COLUMN_PRODUCTNAME + "=?", new String[]{title}, null, null, COLUMN_PRODUCTSUB + " asc", null);
                COLUMN_CATID + "=? AND " + COLUMN_USERID + "=? AND " + COLUMN_REMINDERARCHIVED + "=?", new String[]{catID,userID, archivedReminders}, null, null, COLUMN_REMDATE + " asc", null);

        //COLUMN_PRDNAMEHIDE + "=" + "0"
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }


    /**
     * Edit Category
     * */
    public void editCategoryUpload(String cat_ID, String userID,String upload, String uploadSum)

    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_UPLOAD, upload);
        values.put(COLUMN_UPLOADSUM, uploadSum);

        // Inserting Row
        long id = db.update(TABLE_REMINDERS, values,COLUMN_CATID + "=" + cat_ID + " AND " + COLUMN_USERID + "= '"+ userID+"'", null);
        // long id = db.update(TABLE_PRODUCTS, values, COLUMN_PRODUCTID + "="+prd_int+ " AND "+COLUMN_PRODUCTNAME + "=" +prd_name , null);
        db.close(); // Closing database connection

        Log.d(TAG, "Category Updated in Sqlite: " + id);
    }




    public Cursor fetchAllDateReminders(String archived) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCursor = db.query(TABLE_REMINDERS, new String[]{ COLUMN_REMINT, COLUMN_CATID, COLUMN_CATEGORY, COLUMN_CATEGORYARCHIVE,COLUMN_CATDESC, COLUMN_REMINDER, COLUMN_REMINDERARCHIVED, COLUMN_REMDATE, COLUMN_USERID, COLUMN_UPLOAD},

                COLUMN_CATEGORYARCHIVE + "=? AND " + COLUMN_REMINDERARCHIVED + "=? AND " + COLUMN_ACTDATE + "!=" + "1", new String[]{ archived,archived}, null, null, COLUMN_REMDATE + " asc", null);


        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor fetchTodayReminders(String today, String archived) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCursor = db.query(TABLE_REMINDERS, new String[]{ COLUMN_CATEGORYARCHIVE, COLUMN_REMINDERARCHIVED, COLUMN_REMDATE},

                COLUMN_CATEGORYARCHIVE + "=? AND " + COLUMN_REMINDERARCHIVED + "=? AND " + COLUMN_REMDATE + "<=?", new String[]{ archived,archived,today}, null, null, COLUMN_REMDATE + " asc", null);


        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }



    public String fetchRemInt(String catID, String userID, String CatUploadID) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCursor = db.query(TABLE_REMINDERS, new String[]{"MAX("+COLUMN_REMINT+")",COLUMN_CATID,COLUMN_USERID,COLUMN_CATUPLOADID},
                COLUMN_CATID + "=? AND " + COLUMN_USERID+ "=? AND " +COLUMN_CATUPLOADID+ "=?", new String[]{catID,userID,CatUploadID}, null, null, COLUMN_REMDATE + " asc", null);

        if (mCursor != null)             mCursor.moveToFirst();

        String remInt1 = (String)  mCursor.getString(0);
        Log.d(TAG, "hideSubProduct cursor interger: " + remInt1);

        db.close();
        mCursor.close();
        return remInt1;
    }

    public Integer NewCategoryID() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCursor = db.query(TABLE_REMINDERS, new String[]{"MAX("+COLUMN_CATID+")"},
                null, new String[]{}, null, null, COLUMN_CATID + " asc", null);

        if (mCursor != null)             mCursor.moveToFirst();

        Integer remInt1 = (Integer)  mCursor.getInt(0);
        Log.d(TAG, "Max Cat interger: " + remInt1);

        db.close();
        mCursor.close();
        return remInt1;
    }

    public Integer NewRemID() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCursor = db.query(TABLE_REMINDERS, new String[]{"MAX("+COLUMN_REMINT+")"},
                null, new String[]{}, null, null, COLUMN_REMINT + " asc", null);

        if (mCursor != null)             mCursor.moveToFirst();

        Integer remInt1 = (Integer)  mCursor.getInt(0);
        Log.d(TAG, "Max Rem Int interger: " + remInt1);

        db.close();
        mCursor.close();
        return remInt1;
    }




    public RowSubItem fetchReminder(String remInt, String catID) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCursor = db.query(TABLE_REMINDERS, new String[]{COLUMN_REMINT,COLUMN_CATID,COLUMN_CATEGORY, COLUMN_CATEGORYARCHIVE, COLUMN_CATDESC,
                        COLUMN_ACTDATE,COLUMN_ACTDAYS,COLUMN_ACTREM,COLUMN_ACTEXPIRY,COLUMN_ACTTITLE,COLUMN_REMINDER,COLUMN_REMINDERARCHIVED,COLUMN_IMAGEA,COLUMN_IMAGEB,COLUMN_REMDATE,COLUMN_REMEXPIRY,
                        COLUMN_REMNOTES,COLUMN_UPLOAD,COLUMN_USERID,COLUMN_CATUPLOADID,COLUMN_REMUPLOADID,COLUMN_UPLOADSUM},
                //     COLUMN_PRODUCTNAME + "=?", new String[]{title}, null, null, COLUMN_PRODUCTSUB + " asc", null);
                COLUMN_REMINT + "=? AND " + COLUMN_CATID + "=?", new String[]{remInt,catID}, null, null, COLUMN_REMDATE + " asc", null);

        //COLUMN_PRDNAMEHIDE + "=" + "0"
        if (mCursor != null)             mCursor.moveToFirst();

        RowSubItem item = new RowSubItem((String)  mCursor.getString(0), mCursor.getString(1), mCursor.getString(2),mCursor.getString(3),mCursor.getString(4),
                mCursor.getString(5),mCursor.getString(6),mCursor.getString(7),mCursor.getString(8),mCursor.getString(9),mCursor.getString(10),mCursor.getString(11),
                mCursor.getString(12),mCursor.getString(13),mCursor.getString(14),mCursor.getString(15),mCursor.getString(16),mCursor.getString(17),mCursor.getString(18),mCursor.getString(19),mCursor.getString(20),mCursor.getString(21));

        Log.d(TAG, "hideSubProduct cursor interger: " + mCursor.getString(3));

        db.close();
        mCursor.close();
        return item;
    }


    public RowSubItem fetchCategory(String catID) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCursor = db.query(true, TABLE_REMINDERS, new String[]{COLUMN_REMINT,COLUMN_CATID,COLUMN_CATEGORY, COLUMN_CATEGORYARCHIVE, COLUMN_CATDESC,
                        COLUMN_ACTDATE,COLUMN_ACTDAYS,COLUMN_ACTREM,COLUMN_ACTEXPIRY,COLUMN_ACTTITLE,COLUMN_REMINDER,COLUMN_REMINDERARCHIVED,COLUMN_IMAGEA,COLUMN_IMAGEB,COLUMN_REMDATE,COLUMN_REMEXPIRY,
                        COLUMN_REMNOTES,COLUMN_UPLOAD,COLUMN_USERID,COLUMN_CATUPLOADID,COLUMN_REMUPLOADID,COLUMN_UPLOADSUM},
                //     COLUMN_PRODUCTNAME + "=?", new String[]{title}, null, null, COLUMN_PRODUCTSUB + " asc", null);
                COLUMN_CATID + "=?", new String[]{catID}, null, null, COLUMN_REMDATE + " asc", null);

        //COLUMN_PRDNAMEHIDE + "=" + "0"
        if (mCursor != null)             mCursor.moveToFirst();

        RowSubItem item = new RowSubItem((String)  mCursor.getString(0),mCursor.getString(1),mCursor.getString(2),mCursor.getString(3),mCursor.getString(4),
                mCursor.getString(5),mCursor.getString(6),mCursor.getString(7),mCursor.getString(8),mCursor.getString(9),mCursor.getString(10),mCursor.getString(11),
                mCursor.getString(12),mCursor.getString(13),mCursor.getString(14),mCursor.getString(15),mCursor.getString(16),mCursor.getString(17),mCursor.getString(18),mCursor.getString(19),mCursor.getString(20),mCursor.getString(21));

        Log.d(TAG, "hideSubProduct cursor interger: " + mCursor.getString(3));

        db.close();
        mCursor.close();
        return item;
    }







    public RowCategory fetchCatDesc(String catID, String archived) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCursor = db.query(true, TABLE_REMINDERS, new String[]{COLUMN_CATID,COLUMN_CATEGORY, COLUMN_CATEGORYARCHIVE, COLUMN_CATDESC, COLUMN_REMINDERARCHIVED, COLUMN_ACTDATE,COLUMN_ACTDAYS,COLUMN_ACTTITLE,COLUMN_UPLOAD, COLUMN_USERID, COLUMN_UPLOADSUM, COLUMN_CATUPLOADID},
                //     COLUMN_PRODUCTNAME + "=?", new String[]{title}, null, null, COLUMN_PRODUCTSUB + " asc", null);
                COLUMN_CATID + "=? AND " + COLUMN_CATEGORYARCHIVE + "=? AND "+ COLUMN_REMINDERARCHIVED + "=?", new String[]{catID, archived,archived}, null, null, null, null);

        //COLUMN_PRDNAMEHIDE + "=" + "0"
        if (mCursor != null)             mCursor.moveToFirst();

        RowCategory item = new RowCategory((String)mCursor.getString(0), mCursor.getString(1),mCursor.getString(2),mCursor.getString(3),mCursor.getString(5),mCursor.getString(6), mCursor.getString(7),mCursor.getString(8),mCursor.getString(9),mCursor.getString(10),mCursor.getString(11));
        Log.d(TAG, "fetchCatDesc cursor: " + mCursor.getString(0));

        db.close();
        mCursor.close();
        return item;
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String firstname, String surname,String userID, String email,String country, String uid, String created_at,String CreateAbility ) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FIRSTNAME, firstname); // First Name
        values.put(KEY_SURNAME, surname); // Surname
        values.put(KEY_EMAIL, email); // EmailKEY_USERID
        values.put(KEY_COUNTRY, country); // EmailKEY_USERID
        values.put(KEY_UID, uid); // Email // Email
        values.put(KEY_USERID, userID);
        values.put(KEY_CREATED_AT, created_at); // Created At
        values.put(KEY_CREATE_ABILITY, CreateAbility); // Created At

        // Inserting Row
        long id = db.insert(TABLE_LOGIN, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Storing downloads in database
     * */
    public void addDownloads(String cat_ID, String Category, String category_Archive, String cat_Desc, String upload,String userID,String uploadSum  ) {
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put(COLUMN_CATID, cat_ID);
        values.put(COLUMN_CATEGORY, Category);
        values.put(COLUMN_CATEGORYARCHIVE, category_Archive);
        values.put(COLUMN_CATDESC, cat_Desc);
        values.put(COLUMN_UPLOAD, upload);
        values.put(COLUMN_USERID, userID);
        values.put(COLUMN_UPLOADSUM, uploadSum);
        // Inserting Row
        long id = db.insert(TABLE_DOWNLOAD, null, values);
        db.close(); // Closing database connection

      //  Log.d(TAG, "Products inserted into sqlite: " + id);
    }


    /**
     * Storing user details in database
     * */
    public void addReminders(Integer rem_Int,Integer cat_ID, String Category, String category_Archive,  String act_Date, String act_Days, String act_Rem, String act_Expiry,
                             String act_Title,String Reminder, String rem_Archived, String imageA, String imageB, Long rem_Date,Long rem_Expiry,String rem_Notes,
                             String upload,String userID,Integer cat_UploadID,Integer rem_UploadID,String uploadSum  ) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_REMINT, rem_Int);
        values.put(COLUMN_CATID, cat_ID);
        values.put(COLUMN_CATEGORY, Category);
        values.put(COLUMN_CATEGORYARCHIVE, category_Archive);
        values.put(COLUMN_ACTDATE, act_Date);
        values.put(COLUMN_ACTDAYS, act_Days);
        values.put(COLUMN_ACTREM, act_Rem);
        values.put(COLUMN_ACTEXPIRY, act_Expiry);
        values.put(COLUMN_ACTTITLE, act_Title);
        values.put(COLUMN_REMINDER, Reminder);
        values.put(COLUMN_REMINDERARCHIVED, rem_Archived);
        values.put(COLUMN_IMAGEA, imageA);
        values.put(COLUMN_IMAGEB, imageB);
        values.put(COLUMN_REMDATE, rem_Date);
        values.put(COLUMN_REMEXPIRY, rem_Expiry);
        values.put(COLUMN_REMNOTES, rem_Notes);
        values.put(COLUMN_UPLOAD, upload);
        values.put(COLUMN_USERID, userID);
        values.put(COLUMN_CATUPLOADID, cat_UploadID);
        values.put(COLUMN_REMUPLOADID, rem_UploadID);
        values.put(COLUMN_UPLOADSUM, uploadSum);
        // Inserting Row
        long id = db.insert(TABLE_REMINDERS, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "Products inserted into sqlite: " + id);
    }



    /**
     * Replacing rows of products
     * */
    public void replaceCategory(String rem_Int,String cat_ID, String Category, String category_Archive, String cat_Desc, String act_Date, String act_Days,String act_Rem,String act_Expiry,
                                 String act_Title,String Reminder, String rem_Archived,  String imageA, String imageB,Integer rem_Date,Integer rem_Expiry,String rem_Notes,
                                 String upload,String userID,String cat_UploadID,String rem_UploadID,String uploadSum ){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_REMINT, rem_Int);
        values.put(COLUMN_CATID, cat_ID);
        values.put(COLUMN_CATEGORY, Category);
        values.put(COLUMN_CATEGORYARCHIVE, category_Archive);
        values.put(COLUMN_CATDESC, cat_Desc);
        values.put(COLUMN_ACTDATE, act_Date);
        values.put(COLUMN_ACTDAYS, act_Days);
        values.put(COLUMN_ACTREM, act_Rem);
        values.put(COLUMN_ACTEXPIRY, act_Expiry);
        values.put(COLUMN_ACTTITLE, act_Title);
        values.put(COLUMN_REMINDER, Reminder);
        values.put(COLUMN_REMINDERARCHIVED, rem_Archived);
        values.put(COLUMN_IMAGEA, imageA);
        values.put(COLUMN_IMAGEB, imageB);
        values.put(COLUMN_REMDATE, rem_Date);
        values.put(COLUMN_REMEXPIRY, rem_Expiry);
        values.put(COLUMN_REMNOTES, rem_Notes);
        values.put(COLUMN_UPLOAD, upload);
        values.put(COLUMN_USERID, userID);
        values.put(COLUMN_CATUPLOADID, cat_UploadID);
        values.put(COLUMN_REMUPLOADID, rem_UploadID);
        values.put(COLUMN_UPLOADSUM, uploadSum);
        // Inserting Row
        long id = db.update(TABLE_REMINDERS, values, COLUMN_REMINT + "=" + rem_Int + " AND " + COLUMN_CATID + "=" + cat_ID, null);
        // long id = db.update(TABLE_PRODUCTS, values, COLUMN_PRODUCTID + "="+prd_int+ " AND "+COLUMN_PRODUCTNAME + "=" +prd_name , null);
        db.close(); // Closing database connection

        Log.d(TAG, "Products inserted into sqlite: " + id);
    }

    /**
     * Edit Reminder
     * */
    public void editReminder(String rem_Int,String cat_ID, String Reminder, String imageA, String imageB,Long rem_Date,Long rem_Expiry,String rem_Notes
                                ){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_REMINDER, Reminder);
        values.put(COLUMN_IMAGEA, imageA);
        values.put(COLUMN_IMAGEB, imageB);
        values.put(COLUMN_REMDATE, rem_Date);
        values.put(COLUMN_REMEXPIRY, rem_Expiry);
        values.put(COLUMN_REMNOTES, rem_Notes);
        // Inserting Row
        long id = db.update(TABLE_REMINDERS, values, COLUMN_REMINT + "=" + rem_Int + " AND " + COLUMN_CATID + "=" + cat_ID, null);
        // long id = db.update(TABLE_PRODUCTS, values, COLUMN_PRODUCTID + "="+prd_int+ " AND "+COLUMN_PRODUCTNAME + "=" +prd_name , null);
        db.close(); // Closing database connection

        Log.d(TAG, "Products inserted into sqlite: " + id);
    }

    /**
     * Edit Yes Reminder
     * */
    public void editYesReminder(String rem_Int,String cat_ID, String act_Rem, String act_Expiry, String Reminder, String imageA, String imageB,Long rem_Date,Long rem_Expiry,String rem_Notes
    ){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_REMINDER, Reminder);
        values.put(COLUMN_ACTREM, act_Rem);
        values.put(COLUMN_ACTEXPIRY, act_Expiry);
        values.put(COLUMN_IMAGEA, imageA);
        values.put(COLUMN_IMAGEB, imageB);
        values.put(COLUMN_REMDATE, rem_Date);
        values.put(COLUMN_REMEXPIRY, rem_Expiry);
        values.put(COLUMN_REMNOTES, rem_Notes);
        // Inserting Row
        long id = db.update(TABLE_REMINDERS, values, COLUMN_REMINT + "=" + rem_Int + " AND " + COLUMN_CATID + "=" + cat_ID, null);
        // long id = db.update(TABLE_PRODUCTS, values, COLUMN_PRODUCTID + "="+prd_int+ " AND "+COLUMN_PRODUCTNAME + "=" +prd_name , null);
        db.close(); // Closing database connection

        Log.d(TAG, "Products inserted into sqlite: " + id);
    }


    /**
     * Edit Yes Days Reminder
     * */
    public void editYesDaysReminder(String rem_Int,String cat_ID,Long rem_Date,Long rem_Expiry){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_REMDATE, rem_Date);
        values.put(COLUMN_REMEXPIRY, rem_Expiry);
        // Inserting Row
        long id = db.update(TABLE_REMINDERS, values, COLUMN_REMINT + "=" + rem_Int + " AND " + COLUMN_CATID + "=" + cat_ID, null);
        // long id = db.update(TABLE_PRODUCTS, values, COLUMN_PRODUCTID + "="+prd_int+ " AND "+COLUMN_PRODUCTNAME + "=" +prd_name , null);
        db.close(); // Closing database connection

        Log.d(TAG, "Products inserted into sqlite: " + id);
    }


    /**
     * Edit Category
     * */
    public void editCategory(String cat_ID, String Category)

    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY, Category);

        // Inserting Row
        long id = db.update(TABLE_REMINDERS, values, COLUMN_CATID + "=" + cat_ID, null);
        // long id = db.update(TABLE_PRODUCTS, values, COLUMN_PRODUCTID + "="+prd_int+ " AND "+COLUMN_PRODUCTNAME + "=" +prd_name , null);
        db.close(); // Closing database connection

        Log.d(TAG, "Category Updated in Sqlite: " + id);
    }

    /**
     * Edit Category
     * */
    public void editCategoryYes(String cat_ID, String Category, String act_Title,String act_Days)

    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY, Category);
        values.put(COLUMN_ACTDAYS, act_Days);
        values.put(COLUMN_ACTTITLE, act_Title);

        // Inserting Row
        long id = db.update(TABLE_REMINDERS, values, COLUMN_CATID + "=" + cat_ID, null);
        // long id = db.update(TABLE_PRODUCTS, values, COLUMN_PRODUCTID + "="+prd_int+ " AND "+COLUMN_PRODUCTNAME + "=" +prd_name , null);
        db.close(); // Closing database connection

        Log.d(TAG, "Category Updated in Sqlite: " + id);
    }





    /**
     * Update Reminder Archive for selected row
     * */
    public void updateRemArchive(String rem_Int,String cat_ID, String remArchived) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_REMINDERARCHIVED, remArchived); // Archive Sub Product
        values.put(COLUMN_CATEGORYARCHIVE, remArchived);
        // Inserting Row
        long id = db.update(TABLE_REMINDERS, values, COLUMN_REMINT + "=" + rem_Int  + " AND " + COLUMN_CATID + "=" + cat_ID, null);
        // long id = db.update(TABLE_PRODUCTS, values, COLUMN_PRODUCTID + "="+prd_int+ " AND "+COLUMN_PRODUCTNAME + "=" +prd_name , null);
        db.close(); // Closing database connection

        Log.d(TAG, "Reminder Archive Updated into sqlite: " + id);
    }

    /**
     * Update Reminder Archive for selected row
     * */
    public void CatArchive(String cat_ID, String remArchived) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_REMINDERARCHIVED, remArchived); // Archive Sub Product
        values.put(COLUMN_CATEGORYARCHIVE, remArchived);
        // Inserting Row
        long id = db.update(TABLE_REMINDERS, values,COLUMN_CATID + "=" + cat_ID, null);
        // long id = db.update(TABLE_PRODUCTS, values, COLUMN_PRODUCTID + "="+prd_int+ " AND "+COLUMN_PRODUCTNAME + "=" +prd_name , null);
        db.close(); // Closing database connection

        Log.d(TAG, "Category Archive Updated into sqlite: " + id);
    }





    /**
     * delete selected row of products
     * */
    public boolean deleteReminder(String remInt, String catID) {

        SQLiteDatabase db = this.getWritableDatabase();

       return db.delete(TABLE_REMINDERS,COLUMN_REMINT + "=? AND " + COLUMN_CATID + "=?", new String[]{remInt,catID})> 0;


    }


    /**
     * delete selected row of products
     * */
    public boolean deleteCategory(String catID) {

        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(TABLE_REMINDERS, COLUMN_CATID + "=?", new String[]{catID})> 0;


    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("firstname", cursor.getString(1));
            user.put("surname", cursor.getString(2));
            user.put("userID", cursor.getString(3));
            user.put("email", cursor.getString(4));
            user.put("uid", cursor.getString(5));
            user.put("created_at", cursor.getString(7));
            user.put("CreateAbility", cursor.getString(8));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Getting user login status return true if rows are there in table
     * */
    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        // return row count
        return rowCount;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_LOGIN, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteTable_Products() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_REMINDERS, null, null);
        db.close();

        Log.d(TAG, "Deleted all product info from sqlite");
    }

}