package studentcompany.sportgest.daos;
//TODO all

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import studentcompany.sportgest.daos.exceptions.GenericDAOException;
import studentcompany.sportgest.domains.User;

public class User_DAO extends GenericDAO<User> implements IGenericDAO<User>{
    //Database name
    private SQLiteDatabase db;

    //Table names
    public static final String TABLE_NAME         = "";

    //Table columns
    public static final String COLUMN_ID          = "";

    //Create table
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            "";

    //Drop table
    public static  final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + "; ";

    @Override
    public List<User> getAll() throws GenericDAOException {
        return null;
    }

    @Override
    public User getById(int id) throws GenericDAOException {
        return null;
    }

    @Override
    public boolean insert(User object) throws GenericDAOException {
        return false;
    }

    @Override
    public boolean delete(User object) throws GenericDAOException {
        return false;
    }

    @Override
    public boolean update(User object) throws GenericDAOException {
        return false;
    }

    @Override
    public boolean exists(User object) throws GenericDAOException {
        return false;
    }

    @Override
    public List<User> getByCriteria(User object) throws GenericDAOException {
        return null;
    }
}
