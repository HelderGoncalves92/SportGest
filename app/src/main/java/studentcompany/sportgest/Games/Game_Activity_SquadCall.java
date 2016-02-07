package studentcompany.sportgest.Games;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.SynchronousQueue;

import studentcompany.sportgest.Players.Player_Fragment_Details;
import studentcompany.sportgest.Players.Player_Fragment_List;
import studentcompany.sportgest.R;
import studentcompany.sportgest.daos.Event_Category_DAO;
import studentcompany.sportgest.daos.Event_DAO;
import studentcompany.sportgest.daos.Game_DAO;
import studentcompany.sportgest.daos.GenericDAO;
import studentcompany.sportgest.daos.Pair;
import studentcompany.sportgest.daos.Player_DAO;
import studentcompany.sportgest.daos.Squad_Call_DAO;
import studentcompany.sportgest.daos.Team_DAO;
import studentcompany.sportgest.daos.exceptions.GenericDAOException;
import studentcompany.sportgest.domains.Event;
import studentcompany.sportgest.domains.EventCategory;
import studentcompany.sportgest.domains.Game;
import studentcompany.sportgest.domains.Player;
import studentcompany.sportgest.domains.Team;

public class Game_Activity_SquadCall extends AppCompatActivity implements Player_Fragment_List.OnItemSelected {

    private List<Player> inGame, onBench, all_players;
    private Player_DAO playerDao;
    private Squad_Call_DAO squadCallDao;
    private Game_DAO game_dao;
    private int currentPos = 0;
    private Menu mOptionsMenu;


    private long baseGameID;
    private long baseTeamID;

    private FragmentManager mFragmentManager;
    private Player_Fragment_List mList_inGame = new Player_Fragment_List();
    private Player_Fragment_List mList_onBench = new Player_Fragment_List();
    private Player_Fragment_Details mDetailsPlayer = new Player_Fragment_Details();
    private static final String TAG = "GAME_GAME_MODE_ACTIVITY";
    private static final int ON_BENCH = 1, IN_GAME = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_squal_call);


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            if (extras != null) {
                baseGameID = extras.getLong("GAME");
                baseTeamID = extras.getLong("TEAM");

            } else {
                baseGameID = 0;
                baseTeamID = 0;
            }
        } else {
            baseGameID = savedInstanceState.getLong("baseGameID");
            baseTeamID = savedInstanceState.getLong("baseTeamID");
            currentPos = savedInstanceState.getInt("currentPos");
        }

        //Some verifications
        if(baseTeamID <=0 || baseGameID <= 0) {
            Toast.makeText(this, "Invalid call!!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        try {

            //Initializations
            squadCallDao = new Squad_Call_DAO(getApplicationContext());
            playerDao = new Player_DAO(getApplicationContext());

            onBench = playerDao.getByCriteria(new Player(new Team(baseTeamID)));

            if (onBench.size() == 0) {
                noElems();
                inGame = new ArrayList<Player>();


            } else{
                Long id;
                all_players = squadCallDao.getPlayersBy_GameID(baseGameID);
                for (Player p: onBench){
                    id = p.getId();
                    for (Player pa: all_players) {
                        if (id == pa.getId()) {
                            inGame.add(p);
                            break;
                        }
                    }
                }

                //Remove repeted players
                for (Player p: inGame)
                    onBench.remove(p);
            }

            mList_inGame.setList(inGame);
            mList_onBench.setList(onBench);

            mList_inGame.setTag(IN_GAME);
            mList_onBench.setTag(ON_BENCH);

        } catch (GenericDAOException e) {
            e.printStackTrace();
        }


        // Get a reference to the FragmentManager
        mFragmentManager = getSupportFragmentManager();

        // Start a new FragmentTransaction
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        // Add the TitleFragment to the layout
        fragmentTransaction.add(R.id.game_InGame_list, mList_inGame);
        fragmentTransaction.add(R.id.game_OnBench_list, mList_onBench);
        fragmentTransaction.add(R.id.detail_fragment_container, mDetailsPlayer);

        fragmentTransaction.commit();


    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong("baseGameID", baseGameID);
        outState.putLong("baseTeamID", baseTeamID);
        outState.putInt("currentPos", currentPos);
    }

    public void noElems(){

        LinearLayoutCompat l = (LinearLayoutCompat)findViewById(R.id.linear);
        l.setVisibility(View.GONE);

        AppCompatTextView t= (AppCompatTextView)findViewById(R.id.without_elems);
        t.setVisibility(View.VISIBLE);
    }

    public void itemSelected(int position, int tag) {
        Player player = null;
        if(tag==ON_BENCH) {
            //istcurrenton[position]=1;
            player = onBench.get(position);
        }
        else if(tag == IN_GAME) {
            //listcurrentin[position]=1;
            player = inGame.get(position);
        }

        if(player != null){
            if(currentPos == -1) {
                mDetailsPlayer.showFirstElem();
            }

            currentPos = position;
            mDetailsPlayer.showPlayer(player);
        }
    }


    public void swapP(View v) {
        Player p, p2;

        int selected_InGame, selectedOnBench;

        selected_InGame = mList_inGame.has_Selection();
        selectedOnBench = mList_onBench.has_Selection();

        MenuItem item = mOptionsMenu.findItem(R.id.action_save);
        item.setVisible(true);

        //Swap
        if (selected_InGame != -1 && selectedOnBench != -1) {
            mList_inGame.unselect_Item(selected_InGame);
            mList_onBench.unselect_Item(selectedOnBench);

            p = mList_inGame.removeItem(selected_InGame);
            p2 = mList_onBench.removeItem(selectedOnBench);

            mList_inGame.insert_Item(p2);
            mList_onBench.insert_Item(p);


            //Move to onBench
        } else if (selected_InGame != -1) {
            mList_inGame.unselect_Item(selected_InGame);
            p = mList_inGame.removeItem(selected_InGame);
            mList_onBench.insert_Item(p);


            //Move to inGame
        } else if (selectedOnBench != -1) {
            mList_onBench.unselect_Item(selectedOnBench);
            p = mList_onBench.removeItem(selectedOnBench);
            mList_inGame.insert_Item(p);

        }
    }

    /************************************
     ****       Menu Functions       ****
     ************************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mOptionsMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_users_view, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MenuItem item;

        item = menu.findItem(R.id.action_edit);
        item.setVisible(false);
        item = menu.findItem(R.id.action_del);
        item.setVisible(false);
        item = menu.findItem(R.id.action_add);
        item.setVisible(false);
        item = menu.findItem(R.id.action_save);
        item.setVisible(false);
        item = menu.findItem(R.id.action_settings);
        item.setVisible(false);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.action_save:


                try {
                    squadCallDao.insert(new Pair<>(onBench.get(0), game_dao.getById(baseGameID)));
                    //testei este codigo para adicionar apenas o primeiro elemento do onBench mas ele adiciona todos os elementos


                } catch (GenericDAOException e) {
                    e.printStackTrace();
                }

                finish();
                return true;

            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void insertTest() {
        Team_DAO team_dao = new Team_DAO(getApplicationContext());
        Player_DAO player_dao = new Player_DAO(getApplicationContext());
        Game_DAO game_dao = new Game_DAO(getApplicationContext());
        Squad_Call_DAO squad_call_dao = new Squad_Call_DAO(getApplicationContext());
        long id;
        Player p;

        try {
            Team team = new Team("Santa Maria", "Uma equipa fantástica!!", "default.jpg", 2015, 0);
            long teamID = team_dao.insert(team);
            team.setId(teamID);

            Game game = new Game(team, team, new Date().getTime(), "", -1, -1, 40.0f);
            long gameID = game_dao.insert(game);
            game.setId(gameID);


            p = new Player("Jocka", "João Alberto", "Portuguesa", "Solteiro", "1222-1-23", 176, 70.4f, "Travessa do Morro", "Masculino", "default.jpg", "player1@email.com", "Direito", 2, team, null);
            id = player_dao.insert(p);
            p.setId(id);

            p = new Player("Fabinho", "Fábio Gomes", "Portuguesa", "Solteiro", "1222-1-23", 170, 83, "Travessa do Morro", "Masculino", "default.jpg", "player1@email.com", "Direito", 4, team, null);
            id = player_dao.insert(p);
            p.setId(id);

            p = new Player("Jorge D.", "Jorge Duarte", "Portuguesa", "Solteiro", "1231-2-3", 180, 73.6f, "Travessa do Morro", "Masculino", "default.jpg", "player1@email.com", "Esquerdo", 3, team, null);
            id = player_dao.insert(p);
            p.setId(id);

            p = new Player("Nel", "Manuel Arouca", "Portuguesa", "Solteiro", "1231-2-3", 194, 69.69f, "Travessa do Morro", "Masculino", "default.jpg", "player1@email.com", "Direito", 1, team, null);
            id = player_dao.insert(p);
            p.setId(id);


        } catch (GenericDAOException e) {
            e.printStackTrace();
        }

    }

}