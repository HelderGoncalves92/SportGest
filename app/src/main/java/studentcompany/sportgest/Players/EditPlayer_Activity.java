package studentcompany.sportgest.Players;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import studentcompany.sportgest.R;
import studentcompany.sportgest.daos.Player_DAO;
import studentcompany.sportgest.daos.exceptions.GenericDAOException;
import studentcompany.sportgest.domains.Player;
import studentcompany.sportgest.domains.Position;

public class EditPlayer_Activity extends AppCompatActivity implements View.OnClickListener {

    //DAOs
    private Player_DAO player_dao;

    Player player = null;
    int playerID = -1;

    private EditText tv_nickname,tv_name,tv_height,tv_weight,tv_address,tv_email,tv_number;
    private TextInputLayout inputLayoutNickname,inputLayoutName,inputLayoutHeight,inputLayoutWeight,inputLayoutAddress,inputLayoutEmail,inputLayoutNumber;

    private TextView focusView;
    private final String FILENAME_COUNTRIES = "";

    private ImageButton btnCalendar;
    private TextView txtDate;
    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_player);

        Bundle b = getIntent().getExtras();
        if(b!=null){
            playerID = (int) (b.getLong("id")+0);
        }

        btnCalendar = (ImageButton) findViewById(R.id.birthday);
        txtDate = (TextView) findViewById(R.id.txtDate);

        btnCalendar.setOnClickListener(this);


         tv_nickname = (EditText) findViewById(R.id.nickname);
         tv_name = (EditText) findViewById(R.id.name);
        Spinner tv_nationality = (Spinner) findViewById(R.id.nationality);
        Spinner tv_maritalStatus = (Spinner) findViewById(R.id.maritalstatus);
        //TODO: POR A DATA A VIR DO BOTAO
         tv_height = (EditText) findViewById(R.id.height);
         tv_weight = (EditText) findViewById(R.id.weight);
         tv_address = (EditText) findViewById(R.id.address);
        Spinner tv_gender = (Spinner) findViewById(R.id.gender);
         tv_email = (EditText) findViewById(R.id.email);
        Spinner tv_preferredFoot = (Spinner) findViewById(R.id.preferredfoot);
         tv_number = (EditText) findViewById(R.id.number);
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

            //TODO: alterar a data
            if(playerFromDB.getBirthDate()!=-1)
                txtDate.setText(Integer.toString(playerFromDB.getBirthDate()));

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

        tv_nickname.addTextChangedListener(new MyTextWatcher(tv_nickname));
        tv_name.addTextChangedListener(new MyTextWatcher(tv_name));
        tv_height.addTextChangedListener(new MyTextWatcher(tv_height));
        tv_weight.addTextChangedListener(new MyTextWatcher(tv_weight));
        tv_address.addTextChangedListener(new MyTextWatcher(tv_address));
        tv_email.addTextChangedListener(new MyTextWatcher(tv_email));
        tv_number.addTextChangedListener(new MyTextWatcher(tv_number));

        inputLayoutNickname = (TextInputLayout) findViewById(R.id.inputLayoutNickname);
        inputLayoutName = (TextInputLayout) findViewById(R.id.inputLayoutName);
        inputLayoutHeight = (TextInputLayout) findViewById(R.id.inputLayoutHeight);
        inputLayoutWeight = (TextInputLayout) findViewById(R.id.inputLayoutWeight);
        inputLayoutAddress = (TextInputLayout) findViewById(R.id.inputLayoutAddress);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.inputLayoutEmail);
        inputLayoutNumber = (TextInputLayout) findViewById(R.id.inputLayoutNumber);

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

                tv_nickname = (EditText) findViewById(R.id.nickname);
                tv_name = (EditText) findViewById(R.id.name);
                Spinner tv_nationality = (Spinner) findViewById(R.id.nationality);
                Spinner tv_maritalStatus = (Spinner) findViewById(R.id.maritalstatus);
//TODO: por a data a vir do botão
                 tv_height = (EditText) findViewById(R.id.height);
                 tv_weight = (EditText) findViewById(R.id.weight);
                 tv_address = (EditText) findViewById(R.id.address);
                Spinner tv_gender = (Spinner) findViewById(R.id.gender);
                 tv_email = (EditText) findViewById(R.id.email);
                Spinner tv_preferredFoot = (Spinner) findViewById(R.id.preferredfoot);
                 tv_number = (EditText) findViewById(R.id.number);
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

                boolean ok = false;
                if (validateName())
                    if (validateNickname())
                        if (validateEmail())
                            if (validateHeight())
                                if (validateWeight())
                                    if (validateAddress())
                                        if (validateNumber())
                                            ok = true;

                if (!ok)
                    return false;

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

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.inputLayoutNickname:
                    validateNickname();
                    break;
                case R.id.inputLayoutName:
                    validateName();
                    break;
                case R.id.inputLayoutHeight:
                    validateHeight();
                    break;
                case R.id.inputLayoutWeight:
                    validateWeight();
                    break;
                case R.id.inputLayoutAddress:
                    validateAddress();
                    break;
                case R.id.inputLayoutEmail:
                    validateEmail();
                    break;
                case R.id.inputLayoutNumber:
                    validateNumber();
                    break;
            }
        }
    }

    private boolean validateNickname() {
        String pw = tv_nickname.getText().toString().trim();
        if (pw.isEmpty() || pw.length() < 5) {
            inputLayoutNickname.setError(getString(R.string.err_nickname_short));
            //requestFocus(inputLayoutPassword);
            return false;
        }
        inputLayoutNickname.setErrorEnabled(false);
        return true;
    }

    private boolean validateName() {
        String pw = tv_name.getText().toString().trim();
        if (pw.isEmpty() || pw.length() < 6) {
            inputLayoutName.setError(getString(R.string.err_name_short));
            //requestFocus(inputLayoutPassword);
            return false;
        }
        inputLayoutName.setErrorEnabled(false);
        return true;
    }

    private boolean validateHeight() {
        String pw = tv_height.getText().toString().trim();
        if (pw.isEmpty() || (pw.length() > 1 && pw.length()<4)) {
            int hg = Integer.parseInt(pw);
            if(!(hg<200 && hg>0)){
                inputLayoutHeight.setError(getString(R.string.err_height_invalid));
                //requestFocus(inputLayoutPassword);
                return false;
            }
        }
        inputLayoutHeight.setErrorEnabled(false);
        return true;
    }

    private boolean validateWeight() {
        String pw = tv_weight.getText().toString().trim();
        if (pw.isEmpty() || (pw.length() > 1 && pw.length()<5)) {
            float wg = Float.parseFloat(pw);
            if(!(wg>0)){
                inputLayoutWeight.setError(getString(R.string.err_weight_invalid));
                //requestFocus(inputLayoutPassword);
                return false;
            }
        }
        inputLayoutWeight.setErrorEnabled(false);
        return true;
    }

    private boolean validateAddress() {
        String pw = tv_address.getText().toString().trim();
        if (pw.isEmpty() || pw.length() < 5) {
            inputLayoutAddress.setError(getString(R.string.err_address_short));
            //requestFocus(inputLayoutPassword);
            return false;
        }
        inputLayoutAddress.setErrorEnabled(false);
        return true;
    }

    private boolean validateEmail() {

        String pw = tv_email.getText().toString().trim();
        if (pw.isEmpty() || pw.length() < 5) {
            Pattern VALID_EMAIL_ADDRESS_REGEX =
                    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
            Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(pw);
            if(!matcher.find()){
                inputLayoutEmail.setError(getString(R.string.error_invalid_email));
                return false;
            }
        }
        inputLayoutEmail.setErrorEnabled(false);
        return true;
    }

    private boolean validateNumber() {
        String pw = tv_number.getText().toString().trim();
        if (pw.isEmpty() || (pw.length() > 1 && pw.length()<4)) {
            int nb = Integer.parseInt(pw);
            if(!(nb<100 && nb>0)){
                inputLayoutNumber.setError(getString(R.string.err_number));
                //requestFocus(inputLayoutPassword);
                return false;
            }
        }
        inputLayoutNumber.setErrorEnabled(false);
        return true;
    }

    @Override
    public void onStop(){
        super.onStop();

        setResult(0);
    }

    @Override
    public void onClick(View v) {
        if (v == btnCalendar) {

            // Process to get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            // Launch Date Picker Dialog
            DatePickerDialog dpd = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            // Display Selected date in textbox
                            //txtDate.setText(dayOfMonth + "-"
                            //        + (monthOfYear + 1) + "-" + year);
                            txtDate.setText(Integer.toString(year));

                        }
                    }, mYear, mMonth, mDay);
            dpd.show();
        }
    }
}