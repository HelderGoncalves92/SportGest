package studentcompany.sportgest.Players;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import studentcompany.sportgest.R;
import studentcompany.sportgest.daos.Player_DAO;
import studentcompany.sportgest.daos.exceptions.GenericDAOException;
import studentcompany.sportgest.domains.Player;
import studentcompany.sportgest.domains.Position;

public class EditPlayer_Activity extends AppCompatActivity {

    //DAOs
    private Player_DAO player_dao;

    Player player = null;
    int playerID = -1;

    private final String FILENAME_COUNTRIES = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_player);

        Bundle b = getIntent().getExtras();
        if(b!=null){
            playerID = (int) b.get("id");
        }


        EditText tv_nickname = (EditText) findViewById(R.id.nickname);
        EditText tv_name = (EditText) findViewById(R.id.name);
        Spinner tv_nationality = (Spinner) findViewById(R.id.nationality);
        Spinner tv_maritalStatus = (Spinner) findViewById(R.id.maritalstatus);
        //TODO: POR A DATA A VIR DO BOTAO
        EditText tv_height = (EditText) findViewById(R.id.height);
        EditText tv_weight = (EditText) findViewById(R.id.weight);
        EditText tv_address = (EditText) findViewById(R.id.address);
        Spinner tv_gender = (Spinner) findViewById(R.id.gender);
        EditText tv_email = (EditText) findViewById(R.id.email);
        Spinner tv_preferredFoot = (Spinner) findViewById(R.id.preferredfoot);
        EditText tv_number = (EditText) findViewById(R.id.number);
        ImageView tv_photo = (ImageView) findViewById(R.id.photo);
        Spinner tv_position = (Spinner) findViewById(R.id.position);

        Player playerFromDB=null;
        player_dao = new Player_DAO(this);

        try {
             playerFromDB = player_dao.getById(playerID);
        } catch (GenericDAOException e) {
            e.printStackTrace();
        }
        if (playerFromDB!=null){
            if(playerFromDB.getNickname()!=null)
                tv_nickname.setText(playerFromDB.getNickname());
            else
                tv_nickname.setText("");

            if(playerFromDB.getName()!=null)
                tv_name.setText(playerFromDB.getName());
            else
                tv_name.setText(playerFromDB.getName());

            ArrayList<String> gendersList = new ArrayList<>();
            gendersList.add("Male");
            gendersList.add("Female");

            Resources res = getResources();
            String[] countries_array = res.getStringArray(R.array.countries_array);
            String[] marital_array = res.getStringArray(R.array.marital_status);

            ArrayList<String> preferredList = new ArrayList<>();
            preferredList.add("Right");
            preferredList.add("Left");

            ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, gendersList);
            tv_gender.setAdapter(adapter1);
            ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, countries_array);
            tv_nationality.setAdapter(adapter2);
            ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, marital_array);
            tv_maritalStatus.setAdapter(adapter3);
            ArrayAdapter<String> adapter4 = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, preferredList);
            tv_preferredFoot.setAdapter(adapter4);

            int pos=-1;
            int counter=0;
            if (playerFromDB.getNationality()!=null)
                for(String s : countries_array){
                    if(playerFromDB.getNationality().equals(s))
                        pos=counter;
                    else
                        counter++;
                }

            if(pos!=-1)
                tv_nationality.setSelection(pos);
            else
                tv_nationality.setSelection(0);

            //TODO: por data direita
            //if(playerFromDB.getBirthDate()!=-1)
            //    tv_birthday.updateDate(playerFromDB.getBirthDate(), 0, 0);

            if(playerFromDB.getHeight()!=-1)
                tv_height.setText(Integer.toString(playerFromDB.getHeight()));
            else
                tv_height.setText(Integer.toString(0));

            if(playerFromDB.getWeight()!=-1)
                tv_weight.setText(Float.toString(playerFromDB.getWeight()));
            else
                tv_weight.setText(Float.toString(0f));

            if(playerFromDB.getAddress()!=null)
                tv_address.setText(playerFromDB.getAddress());
            else
                tv_address.setText("");

            pos=-1;
            counter=0;
            if (playerFromDB.getMarital_status()!=null)
                for(String s : marital_array){
                    if(playerFromDB.getMarital_status().equals(s))
                        pos=counter;
                    else
                        counter++;
                }

            if(pos!=-1)
                tv_maritalStatus.setSelection(pos);
            else
                tv_maritalStatus.setSelection(0);

            pos=-1;
            counter=0;
            if (playerFromDB.getGender()!=null)
                for(String s : gendersList){
                    if(playerFromDB.getGender().equals(s))
                        pos=counter;
                    else
                        counter++;
                }

            if(pos!=-1)
                tv_gender.setSelection(pos);
            else
                tv_gender.setSelection(0);

            if(playerFromDB.getEmail()!=null)
                tv_email.setText(playerFromDB.getEmail());
            else
                tv_email.setText("");

            pos=-1;
            counter=0;
            if (playerFromDB.getPreferredFoot()!=null)
                for(String s : preferredList){
                    if(playerFromDB.getPreferredFoot().equals(s))
                        pos=counter;
                    else
                        counter++;
                }

            if(pos!=-1)
                tv_preferredFoot.setSelection(pos);
            else
                tv_preferredFoot.setSelection(0);

            if(playerFromDB.getNumber()!=-1)
                tv_number.setText(Integer.toString(playerFromDB.getNumber()));
            else
                tv_number.setText("");

            if(playerFromDB.getPosition()!=null){
                //TODO: preencher com as positions
                ArrayList<String> positionSpinner = new ArrayList<>();

                ArrayAdapter<String> adapter5 = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item, positionSpinner);
                tv_maritalStatus.setAdapter(adapter5);

                pos=-1;
                counter=0;
                if (playerFromDB.getPosition()!=null)
                    for(String s : positionSpinner){
                        if(playerFromDB.getPosition().getName().equals(s))
                            pos=counter;
                        else
                            counter++;
                    }

                if(pos!=-1)
                    tv_position.setSelection(pos);
                else
                    tv_position.setSelection(0);
            }
            else
                tv_position.setSelection(0);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar_crud, menu);
        MenuItem editItem = menu.findItem(R.id.Edit);
        MenuItem delItem = menu.findItem(R.id.Delete);
        MenuItem addItem = menu.findItem(R.id.Add);
        editItem.setVisible(true);
        delItem.setVisible(false);
        addItem.setVisible(true);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);

        switch(item.getItemId())
        {
            //add action
            case R.id.Edit:
                EditText tv_nickname = (EditText) findViewById(R.id.nickname);
                EditText tv_name = (EditText) findViewById(R.id.name);
                Spinner tv_nationality = (Spinner) findViewById(R.id.nationality);
                Spinner tv_maritalStatus = (Spinner) findViewById(R.id.maritalstatus);
//TODO: por a data a vir do botão
                EditText tv_height = (EditText) findViewById(R.id.height);
                EditText tv_weight = (EditText) findViewById(R.id.weight);
                EditText tv_address = (EditText) findViewById(R.id.address);
                Spinner tv_gender = (Spinner) findViewById(R.id.gender);
                EditText tv_email = (EditText) findViewById(R.id.email);
                Spinner tv_preferredFoot = (Spinner) findViewById(R.id.preferredfoot);
                EditText tv_number = (EditText) findViewById(R.id.number);
                ImageView tv_photo = (ImageView) findViewById(R.id.photo);
                Spinner tv_position = (Spinner) findViewById(R.id.position);

                String nickname = tv_nickname.getText().toString();
                String name = tv_name.getText().toString();
                String nationality = tv_nationality.getSelectedItem()!=null ? tv_nationality.getSelectedItem().toString() : "TUGA";
                String maritalStatus = "";
                if(tv_maritalStatus.getSelectedItem()!=null)
                    maritalStatus = tv_maritalStatus.getSelectedItem().toString();
               //TODO: por a data
                //corrigir quando a data estiver direita
                //String birthday = year+"-"+month+"-"+day;
                int birthday = 1;
                int height = Integer.parseInt(tv_height.getText().toString());
                float weight = Float.parseFloat(tv_weight.getText().toString());
                String address = tv_address.getText().toString();
                String gender = "";
                if(tv_gender.getSelectedItem()!=null)
                    gender = tv_gender.getSelectedItem().toString();
                String email = tv_email.getText().toString();
                String preferredFoot = "";
                if(tv_preferredFoot.getSelectedItem()!=null)
                    preferredFoot = tv_preferredFoot.getSelectedItem().toString();
                int number = Integer.parseInt(tv_number.getText().toString());
                //ups vai estar a imagem em bitmap ou o path para ela?
                //String photo = tv_photo.get
                String photo="";
                Position position = (Position) tv_position.getSelectedItem();


                player=new Player(playerID,nickname,name,nationality,maritalStatus,birthday,height,weight,address,gender,photo,email,preferredFoot,number,null,position);

                //insert/update database
                try {
                    if(playerID > 0){
                        player_dao.update(player);
                    } else {
                        player_dao.insert(player);
                    }
                }catch (GenericDAOException ex){
                    System.err.println(CreatePlayer_Activity.class.getName() + " [WARNING] " + ex.toString());
                    Logger.getLogger(CreatePlayer_Activity.class.getName()).log(Level.WARNING, null, ex);
                }

                Intent intent = new Intent();
                setResult(1,intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStop(){
        super.onStop();

        setResult(0);
    }
}