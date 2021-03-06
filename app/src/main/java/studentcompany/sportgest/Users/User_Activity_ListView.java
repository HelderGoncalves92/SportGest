package studentcompany.sportgest.Users;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import studentcompany.sportgest.R;
import studentcompany.sportgest.daos.User_DAO;
import studentcompany.sportgest.daos.User_Team_DAO;
import studentcompany.sportgest.daos.exceptions.GenericDAOException;
import studentcompany.sportgest.domains.Team;
import studentcompany.sportgest.domains.User;

public class User_Activity_ListView extends AppCompatActivity implements User_Fragment_List.OnItemSelected {


    private User_DAO userDao;
    private User_Team_DAO user_team_dao;
    private List<User> users;
    private int currentPos = 0;
    private long user_id;
    private Menu mOptionsMenu;

    private DialogFragment mDialog;
    private FragmentManager mFragmentManager;
    private User_Fragment_List mListUsers = new User_Fragment_List();
    private User_Fragment_Details mDetailsUser = new User_Fragment_Details();
    private static final String TAG = "USERS_ACTIVITY";

    private final int EDIT_TAG = 19;
    private final int CREATE_TAG = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_list_view);

        if(savedInstanceState != null)
            currentPos = savedInstanceState.getInt("currentPos");


        try {
            userDao = new User_DAO(getApplicationContext());
            user_team_dao = new User_Team_DAO(getApplicationContext());
            users = userDao.getAll();
            for(User u : users)
            {List<Team> t = user_team_dao.getByFirstId(u.getId());
             if(t!=null)
                 if(t.size()>0)
                     u.setTeam(t.get(0)); // Get the Team
            }
            if(users.isEmpty()) {
                noElems();
                //insertUserTest(userDao);
                //users = userDao.getAll();
            }
            mListUsers.setList(users);

        } catch (GenericDAOException e) {
            e.printStackTrace();
        }


        // Get a reference to the FragmentManager
        mFragmentManager = getSupportFragmentManager();

        // Start a new FragmentTransaction
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        // Add the TitleFragment to the layout
        fragmentTransaction.add(R.id.title_fragment_container , mListUsers);
        fragmentTransaction.add(R.id.detail_fragment_container, mDetailsUser);

        fragmentTransaction.commit();
        /*if(users.size()>0) {
            currentPos=0;
            if(users.get(currentPos) != null)
                user_id = users.get(currentPos).getId();
            //mDetailsUser.startActivity();
            //mDetailsUser.showUser(users.get(currentPos));
        }*/
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("currentPos", currentPos);
    }

    public List<String> getNamesList(List<User> usersList){

        ArrayList<String> list = new ArrayList<String>();

        for(User u: usersList)
            list.add(u.getName());

        return list;
    }

    public void noElems(){

        LinearLayoutCompat l = (LinearLayoutCompat)findViewById(R.id.linear);
        l.setVisibility(View.GONE);

        AppCompatTextView t= (AppCompatTextView)findViewById(R.id.without_elems);
        t.setVisibility(View.VISIBLE);
    }

    private void withElems(){

        LinearLayoutCompat l = (LinearLayoutCompat)findViewById(R.id.linear);
        l.setVisibility(View.VISIBLE);

        AppCompatTextView t= (AppCompatTextView)findViewById(R.id.without_elems);
        t.setVisibility(View.GONE);
    }

    public void removeUser(){
        mDetailsUser.clearDetails();

        userDao.deleteById(users.get(currentPos).getId());
        mListUsers.removeItem(currentPos);
        currentPos = -1;

        MenuItem item = mOptionsMenu.findItem(R.id.action_del);
        item.setVisible(false);

        item = mOptionsMenu.findItem(R.id.action_edit);
        item.setVisible(false);

        if(users.isEmpty()){
            noElems();
        }
    }

    /************************************
     ****     Listener Functions     ****
     ************************************/

    public void itemSelected(int position, int tag) {
        User user = users.get(position);

        if(user != null){
            if(currentPos == -1) {
                MenuItem item = mOptionsMenu.findItem(R.id.action_del);
                item.setVisible(true);

                item = mOptionsMenu.findItem(R.id.action_edit);
                item.setVisible(true);
            }

            currentPos = position;
            mDetailsUser.showUser(user);
            user_id = user.getId();
        }
    }

    /************************************
     ****      Dialog Functions      ****
     ************************************/

    public void DialogDismiss(){
        mDialog.dismiss();
    }

    public static class AlertToDelete_DialogFragment extends DialogFragment {

        public static AlertToDelete_DialogFragment newInstance(){
            return new AlertToDelete_DialogFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            Resources res = getResources();

            return new AlertDialog.Builder(getActivity())
                    .setMessage(res.getString(R.string.are_you_sure))
                    .setCancelable(false)
                    .setNegativeButton(res.getString(R.string.negative_answer),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    User_Activity_ListView activity = (User_Activity_ListView) getActivity();
                                    activity.DialogDismiss();
                                }
                            })
                    .setPositiveButton(res.getString(R.string.positive_answer),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    User_Activity_ListView activity = (User_Activity_ListView) getActivity();
                                    activity.DialogDismiss();
                                    activity.removeUser();
                                }
                            }).create();
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

        //To restore state on Layout Rotation
        if(currentPos != -1 && users.size()>0) {

            MenuItem item = mOptionsMenu.findItem(R.id.action_del);
            item.setVisible(true);

            item = mOptionsMenu.findItem(R.id.action_edit);
            item.setVisible(true);

            User u = users.get(currentPos);
            user_id = u.getId();

            mDetailsUser.showUser(u);
            mListUsers.select_Item(currentPos);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_add:

                intent = new Intent(this, User_Activity_Create.class);
                startActivityForResult(intent,CREATE_TAG);
                return true;

            case R.id.action_edit:
                intent = new Intent(this, User_Activity_Create.class);
                user_id = users.get(currentPos).getId();
                intent.putExtra("ID",user_id);
                startActivityForResult(intent,EDIT_TAG);
                return true;

            case R.id.action_del:
                mDialog = AlertToDelete_DialogFragment.newInstance();
                mDialog.show(mFragmentManager, "Alert");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CREATE_TAG) {
            try {
                Bundle extras = data.getExtras();
                if(extras != null) {
                    //get User ID
                    long id = extras.getLong("ID");

                    if(id>0){
                        User user = userDao.getById(id);

                        if(users.size() == 0){
                            users.add(user);
                            mListUsers.updateList(users);
                            withElems();
                        }else{
                            mListUsers.insertList(user);
                        }

                        mDetailsUser.showUser(user);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (requestCode == EDIT_TAG) {
            try {
                User user1 = userDao.getById(user_id);

                if(user1!=null)
                    {List<Team> t = user_team_dao.getByFirstId(user1.getId());
                        if(t!=null)
                            if(t.size()>0)
                                user1.setTeam(t.get(0)); // Get the Team

                    mDetailsUser.showUser(user1);}
                    mListUsers.updatePosition(user1, currentPos);
            } catch (GenericDAOException e) {
                e.printStackTrace();
            }
        }
    }
}
