package studentcompany.sportgest.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import studentcompany.sportgest.daos.db.MyDB;
import studentcompany.sportgest.daos.exceptions.GenericDAOException;
import studentcompany.sportgest.domains.Exercise;

public class Exercise_DAO extends GenericDAO<Exercise> implements IGenericDAO<Exercise>{
    //Database name
    private SQLiteDatabase db;

    //Table names
    public static final String TABLE_NAME         = "EXERCISE";

    //Table columns
    public static final String COLUMN_ID          = "ID";
    public static final String COLUMN_TITLE       = "TITLE";
    public static final String COLUMN_DESCRIPTION = "DESCRIPTION";
    public static final String COLUMN_DURATION    = "DURATION";

    //Create table
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID          + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, \n" +
            COLUMN_TITLE       + " TEXT NOT NULL, " +
            COLUMN_DESCRIPTION + " TEXT, " +
            COLUMN_DURATION    + " INTEGER);";

    //Drop table
    public static  final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + "; ";

    public Exercise_DAO(Context context) {
        this.db = MyDB.getInstance(context).db;
    }

    @Override
    public ArrayList<Exercise> getAll() throws GenericDAOException {
        //aux variables;
        ArrayList<Exercise> resExercise = new ArrayList<>();
        int id;
        String title;
        String description;
        int duration;

        //Query
        Cursor res = db.rawQuery( "SELECT * FROM "+TABLE_NAME, null );
        res.moveToFirst();

        //Parse data
        while(res.isAfterLast() == false) {
            id = res.getInt(res.getColumnIndexOrThrow(COLUMN_ID));
            title = res.getString(res.getColumnIndexOrThrow(COLUMN_TITLE));
            description = res.getString(res.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
            duration = res.getInt(res.getColumnIndexOrThrow(COLUMN_DURATION));
            resExercise.add(new Exercise(id, title, description, duration));
            res.moveToNext();
        }

        return resExercise;
    }

    @Override
    public Exercise getById(int id) throws GenericDAOException {
        //aux variables;
        Exercise resExercise;
        String title;
        String description;
        int duration;

        //Query
        Cursor res = db.rawQuery( "SELECT * FROM "+TABLE_NAME+" WHERE "+COLUMN_ID+"="+id, null );
        res.moveToFirst();

        //Parse data
        title = res.getString(res.getColumnIndexOrThrow(COLUMN_TITLE));
        description = res.getString(res.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
        duration = res.getInt(res.getColumnIndexOrThrow(COLUMN_DURATION));
        resExercise = new Exercise(id, title, description, duration);

        return resExercise;
    }

    @Override
    public boolean insert(Exercise object) throws GenericDAOException {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TITLE, object.getTitle());
        contentValues.put(COLUMN_DESCRIPTION, object.getDescription());
        contentValues.put(COLUMN_DURATION, object.getDuration());

        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    @Override
    public boolean delete(Exercise object) throws GenericDAOException {
        int deletedCount = db.delete(TABLE_NAME,
                COLUMN_ID + " = ? ",
                new String[] { Integer.toString(object.getId()) });
        return true;
    }

    public boolean deleteById(int id){
        int deletedCount = db.delete(TABLE_NAME,
                COLUMN_ID + " = ? ",
                new String[] { Integer.toString(id) });
        return true;
    }

    @Override
    public boolean update(Exercise object) throws GenericDAOException {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TITLE, object.getTitle());
        contentValues.put(COLUMN_DESCRIPTION, object.getDescription());
        contentValues.put(COLUMN_DURATION, object.getDuration());
        db.update(TABLE_NAME,
                contentValues,
                COLUMN_ID + " = ? ",
                new String[] { Integer.toString(object.getId()) } );
        return true;
    }

    public int numberOfRows(){
        return (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }

    @Override
    public boolean exists(Exercise object) throws GenericDAOException {
        //TODO implement exists
        return false;
    }

    @Override
    public List<Exercise> getByCriteria(Exercise object) throws GenericDAOException {
        //TODO implement getByCriteria
        return null;
    }
}
