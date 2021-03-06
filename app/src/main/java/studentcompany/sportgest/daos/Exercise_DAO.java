package studentcompany.sportgest.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    public static final String COLUMN_DELETED     = "DELETED";

    //Create table
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID          + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, \n" +
            COLUMN_TITLE       + " TEXT NOT NULL, " +
            COLUMN_DESCRIPTION + " TEXT, " +
            COLUMN_DURATION    + " INTEGER, " +
            COLUMN_DELETED     + " INTEGER NOT NULL);";

    //Drop table
    public static  final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + "; ";

    public Exercise_DAO(Context context) {
        this.db = MyDB.getInstance(context).db;
    }

    @Override
    public ArrayList<Exercise> getAll() throws GenericDAOException {
        //aux variables;
        ArrayList<Exercise> resExercise = new ArrayList<>();
        long id;
        String title;
        String description;
        int duration;
        int deleted;

        //Query
        Cursor res = db.rawQuery( "SELECT * FROM "+TABLE_NAME, null );
        res.moveToFirst();

        //Parse data
        while(res.isAfterLast() == false) {
            id = res.getLong(res.getColumnIndexOrThrow(COLUMN_ID));
            title = res.getString(res.getColumnIndexOrThrow(COLUMN_TITLE));
            description = res.getString(res.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
            duration = res.getInt(res.getColumnIndexOrThrow(COLUMN_DURATION));
            deleted = res.getInt(res.getColumnIndexOrThrow(COLUMN_DELETED));
            resExercise.add(new Exercise(id, title, description, duration, deleted));
            res.moveToNext();
        }

        //Sorting
        Collections.sort(resExercise, new Comparator<Exercise>() {
            @Override
            public int compare(Exercise ex1, Exercise ex2) {

                return ex1.getTitle().compareTo(ex2.getTitle());
            }
        });

        return resExercise;
    }

    @Override
    public Exercise getById(long id) throws GenericDAOException {
        //aux variables;
        Exercise resExercise;
        String title;
        String description;
        int duration;
        int deleted;

        //Query
        Cursor res = db.rawQuery( "SELECT * FROM "+TABLE_NAME+" WHERE "+COLUMN_ID+"="+id, null );
        res.moveToFirst();

        //Parse data
        title = res.getString(res.getColumnIndexOrThrow(COLUMN_TITLE));
        description = res.getString(res.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
        duration = res.getInt(res.getColumnIndexOrThrow(COLUMN_DURATION));
        deleted = res.getInt(res.getColumnIndexOrThrow(COLUMN_DELETED));
        resExercise = new Exercise(id, title, description, duration, deleted);

        return resExercise;
    }

    @Override
    public long insert(Exercise object) throws GenericDAOException {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TITLE, object.getTitle());
        contentValues.put(COLUMN_DESCRIPTION, object.getDescription());
        contentValues.put(COLUMN_DURATION, object.getDuration());
        contentValues.put(COLUMN_DELETED, object.getDeleted());

        return db.insert(TABLE_NAME, null, contentValues);
    }

    @Override
    public boolean delete(Exercise object) throws GenericDAOException {
        int deletedCount = db.delete(TABLE_NAME,
                COLUMN_ID + " = ? ",
                new String[] { Long.toString(object.getId()) });
        return true;
    }

    @Override
    public boolean deleteById(long id){
        int deletedCount = db.delete(TABLE_NAME,
                COLUMN_ID + " = ? ",
                new String[] { Long.toString(id) });
        return true;
    }

    @Override
    public boolean update(Exercise object) throws GenericDAOException {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TITLE, object.getTitle());
        contentValues.put(COLUMN_DESCRIPTION, object.getDescription());
        contentValues.put(COLUMN_DURATION, object.getDuration());
        contentValues.put(COLUMN_DELETED, object.getDeleted());
        db.update(TABLE_NAME,
                contentValues,
                COLUMN_ID + " = ? ",
                new String[] { Long.toString(object.getId()) } );
        return true;
    }

    @Override
    public int numberOfRows(){
        return (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }

    @Override
    public boolean exists(Exercise object) throws GenericDAOException {

        if(object==null)
            return false;

        int fields = 0;
        String tmpString;
        int tmpInt;
        long tmpLong;

        StringBuilder statement = new StringBuilder("SELECT * FROM "+ TABLE_NAME +" where ");
        if ((tmpLong = object.getId()) >= 0) {
            statement.append(COLUMN_ID + "=" + tmpLong);
            fields++;
        }
        if ((tmpString = object.getTitle()) != null) {
            statement.append(((fields != 0) ? " AND " : "") + COLUMN_TITLE + " = '" + tmpString + "'");
            fields++;
        }
        if ((tmpString = object.getDescription()) != null) {
            statement.append(((fields != 0) ? " AND " : "") + COLUMN_DESCRIPTION + " = '" + tmpString + "'");
            fields++;
        }
        if ((tmpInt = object.getDuration()) >= 0) {
            statement.append(((fields != 0) ? " AND " : "") + COLUMN_DURATION + " = " + tmpInt );
            fields++;
        }
        if ((tmpInt = object.getDeleted()) >= 0) {
            statement.append(((fields != 0) ? " AND " : "") + COLUMN_DELETED + " = " + tmpInt);
            fields++;
        }

        if (fields > 0) {
            Cursor res = db.rawQuery(statement.toString(), null);
            return res.moveToFirst();
        }
        else
            return false;
    }

    @Override
    public List<Exercise> getByCriteria(Exercise object) throws GenericDAOException {

        if(object==null)
            return null;

        List<Exercise> resExercise = new ArrayList<>();
        int fields = 0;
        String tmpString;
        int tmpInt;
        long tmpLong;

        StringBuilder statement = new StringBuilder("SELECT * FROM "+ TABLE_NAME +" where ");
        if ((tmpLong = object.getId()) >= 0) {
            statement.append(COLUMN_ID + "=" + tmpLong);
            fields++;
        }
        if ((tmpString = object.getTitle()) != null) {
            statement.append(((fields != 0) ? " AND " : "") + COLUMN_TITLE + " LIKE '%" + tmpString + "%'");
            fields++;
        }
        if ((tmpString = object.getDescription()) != null) {
            statement.append(((fields != 0) ? " AND " : "") + COLUMN_DESCRIPTION + " LIKE '%" + tmpString + "%'");
            fields++;
        }
        if ((tmpInt = object.getDuration()) >= 0) {
            statement.append(((fields != 0) ? " AND " : "") + COLUMN_DURATION + " = " + tmpInt );
            fields++;
        }
        if ((tmpInt = object.getDeleted()) >= 0) {
            statement.append(((fields != 0) ? " AND " : "") + COLUMN_DELETED + " = " + tmpInt);
            fields++;
        }

        if (fields > 0) {

            long id;
            String title;
            String description;
            int duration;
            int deleted;

            Cursor res = db.rawQuery( statement.toString(), null );
            if(res.moveToFirst())

                while (res.isAfterLast() == false) {
                    id = res.getLong(res.getColumnIndexOrThrow(COLUMN_ID));
                    title = res.getString(res.getColumnIndexOrThrow(COLUMN_TITLE));
                    description = res.getString(res.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                    duration = res.getInt(res.getColumnIndexOrThrow(COLUMN_DURATION));
                    deleted = res.getInt(res.getColumnIndexOrThrow(COLUMN_DELETED));
                    resExercise.add(new Exercise(id, title, description, duration, deleted));
                    res.moveToNext();
                }
        }


        return resExercise;
    }
}
