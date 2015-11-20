package studentcompany.sportgest.daos.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import studentcompany.sportgest.daos.Attribute_DAO;
import studentcompany.sportgest.daos.Event_Category_DAO;
import studentcompany.sportgest.daos.Event_DAO;
import studentcompany.sportgest.daos.Exercise_DAO;
import studentcompany.sportgest.daos.Game_DAO;
import studentcompany.sportgest.daos.Obs_Category_DAO;
import studentcompany.sportgest.daos.Observation_DAO;

/**
 * Created by MrFabio on 18/11/2015.
 */
public class MyDB extends SQLiteOpenHelper {

    public SQLiteDatabase db;
    public static final String DATABASE_NAME = "SportGest.db";
    public static final int DATABASE_VERSION = 1;
    public static MyDB _Instance;

    public MyDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        db = this.getWritableDatabase();
    }


    public static MyDB getInstance (Context context) {
        if (_Instance == null) {
            _Instance = new MyDB(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        return _Instance;
    }

    void setInstance(MyDB value) {
        _Instance = value;
        value.Init();
    }

    void Init() {

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Drop tables
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_NAME);

        //Create tables
        db.execSQL(Event_Category_DAO.CREATE_TABLE);
        db.execSQL(Obs_Category_DAO.CREATE_TABLE);
        db.execSQL(Event_DAO.CREATE_TABLE);
        db.execSQL(Observation_DAO.CREATE_TABLE);
        db.execSQL(Game_DAO.CREATE_TABLE);
        db.execSQL(Attribute_DAO.CREATE_TABLE);
        db.execSQL(Exercise_DAO.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // No upgrade available
        onCreate(db);
    }
}
