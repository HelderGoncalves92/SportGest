package studentcompany.sportgest.Games;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import studentcompany.sportgest.R;
import studentcompany.sportgest.daos.Event_DAO;
import studentcompany.sportgest.daos.Game_DAO;
import studentcompany.sportgest.daos.Player_DAO;
import studentcompany.sportgest.daos.Squad_Call_DAO;
import studentcompany.sportgest.daos.exceptions.GenericDAOException;
import studentcompany.sportgest.domains.Event;
import studentcompany.sportgest.domains.Game;
import studentcompany.sportgest.domains.Player;
import studentcompany.sportgest.domains.PlayerPosition;
import studentcompany.sportgest.domains.Position;
import studentcompany.sportgest.domains.Team;

public class Game_Activity_GeneralView extends AppCompatActivity{


    //DAOs
    private Event_DAO eventDAO;
    private Game_DAO game_dao;
    private Squad_Call_DAO squad_call_dao;

    private List<Player> home_players, visitor_players;
    private List<Event> eventsList;
    private final int SQUAD_HOME = 100, SQUAD_VISITOR = 101, GAME_MODE = 102;

    private Game_Fragment_Squad mSquads;


    private long baseTeamID = 0;
    private long baseGameID = 0;
    private int current_tab = 0;

    //Id of current game displayed
    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_general_view);


        //Get Informations from the previous activity or rotation Layout
        if(savedInstanceState == null){
            Bundle extras = getIntent().getExtras();

            if (extras != null){
                baseTeamID = extras.getLong("TEAM");
                baseGameID = extras.getLong("GAME");

            } else {
                baseTeamID = 0;
                baseGameID = 0;
            }
        } else {
            baseGameID = savedInstanceState.getLong("baseGameID");
            baseTeamID = savedInstanceState.getLong("baseTeamID");
            current_tab = savedInstanceState.getInt("current_tab");
        }

        //Some verifications
        if(baseTeamID <=0 || baseGameID <= 0) {
            Toast.makeText(this, "Invalid call!!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        // Initialize the ViewPager and set an adapter
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);

        pager.setCurrentItem(0);

        tabs.setOnTabReselectedListener(new PagerSlidingTabStrip.OnTabReselectedListener() {
            @Override
            public void onTabReselected(int position) {
                Toast.makeText(Game_Activity_GeneralView.this, "Tab reselected: " + position, Toast.LENGTH_SHORT).show();
            }
        });


        //initialize required DAOs
        game_dao = new Game_DAO(getApplicationContext());
        squad_call_dao = new Squad_Call_DAO(getApplicationContext());
        eventDAO = new Event_DAO(getApplicationContext());

        //get all events and Players
        try {
            game = game_dao.getById(baseGameID);
            eventsList = eventDAO.getByGameID(baseGameID);
            visitor_players = squad_call_dao.getPlayersBy_GameID(baseGameID);

            if(visitor_players == null) visitor_players = new ArrayList<Player>();
            home_players = new ArrayList<Player>();

            for (Player p: visitor_players)
                if (p.getTeam().getId() == baseTeamID)
                    home_players.add(p);


            //Remove repeted players
            for (Player p: home_players)
                visitor_players.remove(p);

        } catch (GenericDAOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong("baseTeamID", baseTeamID);
        outState.putLong("baseGameID", baseGameID);
        outState.putInt("current_tab", current_tab);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {getString(R.string.overview), getString(R.string.Statistics), getString(R.string.teams)};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            if(position==0)
                return GameEvents_Fragment.newInstance(position, game, eventsList);
            else if(position==1)
                return GameStatistics_Fragment.newInstance(position);
            else{
                mSquads = Game_Fragment_Squad.newInstance(position, home_players, visitor_players);
                return mSquads;
            }
        }
    }


    /************************************
     ****       Menu Functions       ****
     ************************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Menu mOptionsMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_game_view, menu);

        //To restore state on Layout Rotation
        /*if(currentPos != -1 && players.size()>0) {

            MenuItem item = mOptionsMenu.findItem(R.id.action_del);
            item.setVisible(true);

            item = mOptionsMenu.findItem(R.id.action_edit);
            item.setVisible(true);

            mDetailsPlayer.showPlayer(players.get(currentPos));
            mListPlayer.select_Item(currentPos);
        }*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_home_CallSquad:
                intent = new Intent(this, Game_Activity_SquadCall.class);
                intent.putExtra("TEAM", game.getHome_team().getId());
                intent.putExtra("GAME", baseGameID);
                startActivityForResult(intent, SQUAD_HOME);
                return true;

            case R.id.action_visitor_CallSquad:
                intent = new Intent(this, Game_Activity_SquadCall.class);
                intent.putExtra("TEAM", game.getVisitor_team().getId());
                intent.putExtra("GAME", baseGameID);
                startActivityForResult(intent, SQUAD_VISITOR);
                return true;

            case R.id.action_gameMode:
                intent = new Intent(this, Game_Activity_GameMode.class);
                intent.putExtra("TEAM", baseTeamID);
                intent.putExtra("GAME", baseGameID);
                startActivityForResult(intent, GAME_MODE);
                return true;

            case R.id.action_del:
                intent = new Intent();
                setResult(1, intent);
                intent.putExtra("OPERATION", 1); //To delete
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onStop(){
        super.onStop();

        Intent returnIntent = new Intent();
        setResult(0, returnIntent);
    }

    private List<Player> getSquad_By_TeamID(long teamID){

        List<Player> list = null, squad = new ArrayList<Player>();

        try {

            list = squad_call_dao.getPlayersBy_GameID(baseGameID);
            if(list != null)
                for (Player p: list)
                    if (p.getTeam().getId() == teamID)
                        squad.add(p);

        } catch (GenericDAOException e) {
            e.printStackTrace();
        }

        return squad;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SQUAD_HOME) {
            if(resultCode == 1){
                home_players = getSquad_By_TeamID(game.getHome_team().getId());
                mSquads.update_HomeSquad(home_players);
            }

        } else if (requestCode == SQUAD_VISITOR) {
            if(resultCode == 1){
                visitor_players = getSquad_By_TeamID(game.getVisitor_team().getId());
                mSquads.update_VisitorSquad(visitor_players);
            }

        } else if (requestCode == GAME_MODE) {
            if(resultCode == 1){
                //Do Something
                baseGameID = baseGameID;
            }

        }
    }
}
