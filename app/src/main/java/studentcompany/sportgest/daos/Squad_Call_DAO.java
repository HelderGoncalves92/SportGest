package studentcompany.sportgest.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import studentcompany.sportgest.daos.db.MyDB;
import studentcompany.sportgest.daos.exceptions.GenericDAOException;
import studentcompany.sportgest.domains.Game;
import studentcompany.sportgest.domains.Player;
import studentcompany.sportgest.domains.Team;

public  class Squad_Call_DAO extends GenericPairDAO<Player,Game> implements IGenericPairDAO<Player,Game> {
    //Database name
    private SQLiteDatabase db;

    //Dependencies DAOs
    private Player_DAO player_dao;
    private Game_DAO game_dao;

    //Table names
    public static final String TABLE_NAME         = "SQUAD_CALL";

    //Table columns
    public static final String COLUMN_GAME_ID = "GAME_ID";
    public static final String COLUMN_PLAYER_ID = "PLAYER_ID";

    //Create table
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_GAME_ID + " INTEGER NOT NULL, " +
            COLUMN_PLAYER_ID + " INTEGER NOT NULL, " +
            "PRIMARY KEY (" + COLUMN_GAME_ID + ", " + COLUMN_PLAYER_ID + "), " +
            "FOREIGN KEY(" + COLUMN_GAME_ID + ") REFERENCES " + Game_DAO.TABLE_NAME + "(" + Game_DAO.COLUMN_ID + "), " +
            "FOREIGN KEY(" + COLUMN_PLAYER_ID + ") REFERENCES " + Player_DAO.TABLE_NAME + "(" + Player_DAO.COLUMN_ID + "));";

    //Drop table
    public static  final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + "; ";

    public Squad_Call_DAO(Context context) {
        this.db = MyDB.getInstance(context).db;
        this.game_dao = new Game_DAO(context);
        this.player_dao = new Player_DAO(context);
    }

    @Override
    public List<Pair<Player, Game>> getAll() throws GenericDAOException {
        //aux variables;
        ArrayList<Pair<Player, Game>> resList = new ArrayList<>();
        int gameId;
        int playerId;

        //Query
        Cursor res = db.rawQuery( "SELECT * FROM " + TABLE_NAME, null );
        res.moveToFirst();

        //Parse data
        while(res.isAfterLast() == false) {
            gameId = res.getInt(res.getColumnIndex(COLUMN_GAME_ID));
            playerId = res.getInt(res.getColumnIndex(COLUMN_PLAYER_ID));
            resList.add(
                    new Pair<>(
                            player_dao.getById(playerId),
                            game_dao.getById(gameId)));
            res.moveToNext();
        }
        return resList;
    }

    @Override
    public List<Pair<Player, Game>> getByFirstId(int id) throws GenericDAOException {
        //aux variables;
        ArrayList<Pair<Player, Game>> resList = new ArrayList<>();
        Player player;
        int gameId;

        //Query
        Cursor res = db.rawQuery( "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_PLAYER_ID + "=" + id, null );
        res.moveToFirst();

        //Parse data
        player = player_dao.getById(id);
        while(res.isAfterLast() == false) {
            gameId = res.getInt(res.getColumnIndex(COLUMN_GAME_ID));
            resList.add(
                    new Pair<>(
                            player,
                            game_dao.getById(gameId)));
            res.moveToNext();
        }

        return resList;
    }

    @Override
    public List<Pair<Player, Game>> getBySecondId(int id) throws GenericDAOException {
        //aux variables;
        ArrayList<Pair<Player, Game>> resList = new ArrayList<>();
        Game game;
        int playerId;

        //Query
        Cursor res = db.rawQuery( "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_GAME_ID + "=" + id, null );
        res.moveToFirst();

        //Parse data
        game = game_dao.getById(id);
        while(res.isAfterLast() == false) {
            playerId = res.getInt(res.getColumnIndex(COLUMN_PLAYER_ID));
            resList.add(
                    new Pair<>(
                            player_dao.getById(playerId),
                            game));
            res.moveToNext();
        }

        return resList;

    }

    @Override
    public long insert(Pair<Player, Game> object) throws GenericDAOException {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PLAYER_ID, object.getFirst().getId());
        contentValues.put(COLUMN_GAME_ID, object.getSecond().getId());

        return db.insert(TABLE_NAME, null, contentValues);
    }

    @Override
    public boolean delete(Pair<Player, Game> object) throws GenericDAOException {
        int deletedCount = db.delete(TABLE_NAME,
                COLUMN_PLAYER_ID + " = ? , " + COLUMN_GAME_ID + " = ? ",
                new String[] { Integer.toString(object.getFirst().getId()), Integer.toString(object.getSecond().getId()) });
        return true;
    }

    @Override
    public boolean exists(Pair<Player, Game> object) throws GenericDAOException {
        return false;
    }
}
