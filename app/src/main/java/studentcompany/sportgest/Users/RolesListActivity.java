package studentcompany.sportgest.Users;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import studentcompany.sportgest.R;
import studentcompany.sportgest.daos.Role_DAO;
import studentcompany.sportgest.daos.exceptions.GenericDAOException;
import studentcompany.sportgest.domains.Role;

public class RolesListActivity extends AppCompatActivity {

    //DAOs
    private Role_DAO role_dao;
    private List<Role> roleList;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roles_activity_list);

        role_dao = new Role_DAO(this);

        try {
            roleList = role_dao.getAll();
        } catch (GenericDAOException ex) {
            Logger.getLogger(RolesListActivity.class.getName()).log(Level.WARNING, null, ex);
            roleList = null;
        }
        /*ArrayList<String> array_list = new ArrayList<>();
        for (Role ec : roleList) {
            array_list.add(ec.getName());
        }*/
        //ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, array_list);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, roleList);

        listView = (ListView) findViewById(R.id.role_ListView);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                //int id_To_Search = position + 1;

                Bundle dataBundle = new Bundle();
                dataBundle.putLong(Role_DAO.TABLE_NAME+Role_DAO.COLUMN_ID, roleList.get(position).getId());

                Intent intent = new Intent(getApplicationContext(), RoleDisplayActivity.class);

                intent.putExtras(dataBundle);
                startActivityForResult(intent, 112);
            }
        });

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.list_role_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onResume() {//update list
        super.onResume();  // Always call the superclass method first

        List<Role> roleList;
        try {
            roleList = role_dao.getAll();
        } catch (GenericDAOException ex) {
            Logger.getLogger(RolesListActivity.class.getName()).log(Level.WARNING, null, ex);
            roleList = null;
        }
        ArrayList<String> array_list = new ArrayList<>();
        for (Role ec : roleList) {
            array_list.add(ec.getName());
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, array_list);

        ListView listView = (ListView) findViewById(R.id.role_ListView);
        listView.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar_crud, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MenuItem addItem = menu.findItem(R.id.Add);
        addItem.setVisible(true);

        MenuItem editItem = menu.findItem(R.id.Edit);
        editItem.setVisible(false);
        MenuItem remItem = menu.findItem(R.id.Delete);
        remItem.setVisible(false);
        MenuItem saveItem = menu.findItem(R.id.Save);
        saveItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.Add:
                Bundle dataBundle = new Bundle();
                dataBundle.putInt(Role_DAO.TABLE_NAME+Role_DAO.COLUMN_ID, 0);

                Intent intent = new Intent(getApplicationContext(), CreateRole_Activity.class);
                intent.putExtras(dataBundle);

                startActivityForResult(intent, 112);
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 112) {
            try {
                roleList = role_dao.getAll();
                ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, roleList);
                listView.setAdapter(arrayAdapter);
            } catch (GenericDAOException e) {
                e.printStackTrace();
            }
        }

    }
}