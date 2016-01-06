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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import studentcompany.sportgest.R;
import studentcompany.sportgest.daos.Player_DAO;
import studentcompany.sportgest.daos.Position_DAO;
import studentcompany.sportgest.daos.exceptions.GenericDAOException;
import studentcompany.sportgest.domains.Player;
import studentcompany.sportgest.domains.Position;

public class Player_Activity_Create extends AppCompatActivity implements View.OnClickListener {

    //DAOs
    private Player_DAO player_dao;
    private Position_DAO position_dao;

    Player player = null;
    long playerID = -1;

    private final int CREATE_TAG = 20;
    private final String FILENAME_COUNTRIES = "";
    private final String FILENAME_GENDERS = "";
    private final String FILENAME_MARITALSTATUS = "";
    private final String FILENAME_POSITIONS = "";

    private ImageButton btnCalendar;
    private TextView txtDate;
    private int mYear, mMonth, mDay;
    private int selectedYear, selectedMonth, selectedDay;

    private EditText tv_nickname,tv_name,tv_height,tv_weight,tv_address,tv_email,tv_number;
    private TextInputLayout inputLayoutNickname,inputLayoutName,inputLayoutHeight,inputLayoutWeight,inputLayoutAddress,inputLayoutEmail,inputLayoutNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_activity_create);

        player_dao = new Player_DAO(this);
        position_dao = new Position_DAO(this);

        btnCalendar = (ImageButton) findViewById(R.id.birthday);
        txtDate = (TextView) findViewById(R.id.txtDate);

        btnCalendar.setOnClickListener(this);

        tv_nickname = (EditText) findViewById(R.id.nickname);
        tv_name = (EditText) findViewById(R.id.name);
        Spinner tv_nationality = (Spinner) findViewById(R.id.nationality);
        Spinner tv_maritalStatus = (Spinner) findViewById(R.id.maritalstatus);
        tv_height = (EditText) findViewById(R.id.height);
        tv_weight = (EditText) findViewById(R.id.weight);
        tv_address = (EditText) findViewById(R.id.address);
        Spinner tv_gender = (Spinner) findViewById(R.id.gender);
        tv_email = (EditText) findViewById(R.id.email);
        Spinner tv_preferredFoot = (Spinner) findViewById(R.id.preferredfoot);
        tv_number = (EditText) findViewById(R.id.number);
        ImageView tv_photo = (ImageView) findViewById(R.id.photo);
        //Spinner tv_position = (Spinner) findViewById(R.id.position);


        ArrayList<String> gendersList = new ArrayList<>();
        gendersList.add("Male");
        gendersList.add("Female");

        Resources res = getResources();
        String[] countries_array = res.getStringArray(R.array.countries_array);
        String[] marital_array = res.getStringArray(R.array.marital_status);

        ArrayList<String> preferredList = new ArrayList<>();
        preferredList.add("Right");
        preferredList.add("Left");

        ArrayList<String> positionsList = new ArrayList<>();
        try {
            List<Position> positions = position_dao.getAll();
            for(Position p : positions)
                positionsList.add(p.getName());
        } catch (GenericDAOException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, gendersList);
        tv_gender.setAdapter(adapter1);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, countries_array);
        tv_nationality.setAdapter(adapter2);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, marital_array);
        tv_maritalStatus.setAdapter(adapter3);
        ArrayAdapter<String> adapter4 = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, preferredList);
        tv_preferredFoot.setAdapter(adapter4);
        ArrayAdapter<String> adapter5 = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, positionsList);

        /*if(positionsList.size()>0)
            tv_position.setAdapter(adapter5);
        else {
            String[] positions_array = res.getStringArray(R.array.positions_array);
            ArrayAdapter<String> adapter6 = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, positions_array);
            tv_position.setAdapter(adapter6);
        }*/

        tv_nationality.setAdapter(adapter2);

        tv_nickname.addTextChangedListener(new MyTextWatcher(tv_nickname));
        tv_name.addTextChangedListener(new MyTextWatcher(tv_name));
        tv_height.addTextChangedListener(new MyTextWatcher(tv_height));
        tv_weight.addTextChangedListener(new MyTextWatcher(tv_weight));
        tv_address.addTextChangedListener(new MyTextWatcher(tv_address));
        tv_email.addTextChangedListener(new MyTextWatcher(tv_email));
        tv_number.addTextChangedListener(new MyTextWatcher(tv_number));

        inputLayoutNickname = (TextInputLayout) findViewById(R.id.inputLayoutNickname);
        inputLayoutName = (TextInputLayout) findViewById(R.id.inputLayout_create_player_Name);
        inputLayoutHeight = (TextInputLayout) findViewById(R.id.inputLayoutHeight);
        inputLayoutWeight = (TextInputLayout) findViewById(R.id.inputLayoutWeight);
        inputLayoutAddress = (TextInputLayout) findViewById(R.id.inputLayoutAddress);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.inputLayoutEmail);
        inputLayoutNumber = (TextInputLayout) findViewById(R.id.inputLayoutNumber);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar_crud_add, menu);
        MenuItem addItem = menu.findItem(R.id.Add);
        addItem.setVisible(true);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);

        switch(item.getItemId())
        {
            //add action
            case R.id.Add:
                boolean okUntilNow = true;
                 tv_nickname = (EditText) findViewById(R.id.nickname);
                 tv_name = (EditText) findViewById(R.id.name);
                Spinner tv_nationality = (Spinner) findViewById(R.id.nationality);
                Spinner tv_maritalStatus = (Spinner) findViewById(R.id.maritalstatus);
                 tv_height = (EditText) findViewById(R.id.height);
                 tv_weight = (EditText) findViewById(R.id.weight);
                 tv_address = (EditText) findViewById(R.id.address);
                Spinner tv_gender = (Spinner) findViewById(R.id.gender);
                 tv_email = (EditText) findViewById(R.id.email);
                Spinner tv_preferredFoot = (Spinner) findViewById(R.id.preferredfoot);
                 tv_number = (EditText) findViewById(R.id.number);
                ImageView tv_photo = (ImageView) findViewById(R.id.photo);
                //Spinner tv_position = (Spinner) findViewById(R.id.position);

                String nickname = tv_nickname.getText().toString();
                String name = tv_name.getText().toString();
                String nationality = tv_nationality.getSelectedItem()!=null ? tv_nationality.getSelectedItem().toString() : "TUGA";
                String maritalStatus = "";
                if(tv_maritalStatus.getSelectedItem()!=null)
                    maritalStatus=tv_maritalStatus.getSelectedItem().toString();
                String birthday = selectedYear+"-"+selectedMonth+"-"+selectedDay;
                int height = -1;
                try {
                    height = Integer.parseInt(tv_height.getText().toString());
                } catch (NumberFormatException ex){
                    okUntilNow=false;
                }
                float weight = -1;
                try {
                     weight = Float.parseFloat(tv_weight.getText().toString());
                } catch (NumberFormatException ex){
                    okUntilNow=false;
                }
                String address = tv_address.getText().toString();
                String gender = "";
                if(tv_gender.getSelectedItem()!=null)
                    gender = tv_gender.getSelectedItem().toString();
                String email = tv_email.getText().toString();
                String preferredFoot = "";
                if(tv_preferredFoot.getSelectedItem()!=null)
                    preferredFoot = tv_preferredFoot.getSelectedItem().toString();
                int number = -1;
                try {
                    number = Integer.parseInt(tv_number.getText().toString());
                } catch (NumberFormatException ex) {
                    okUntilNow=false;
                }
                //ups vai estar a imagem em bitmap ou o path para ela?
                //String photo = tv_photo.get
                String photo="";
                /*String positionStr = null;
                if(tv_position.getSelectedItem()!=null)
                    positionStr = (String) tv_position.getSelectedItem();*/

                boolean ok = false;
                if (validateName())
                    if (validateNickname())
                        if (validateEmail())
                            if (validateHeight())
                                if (validateWeight())
                                    if (validateAddress())
                                        if (validateNumber())
                                            ok = true;

                /*if(!okUntilNow){
                    Intent intent = new Intent();
                    setResult(2, intent);
                    finish();
                    return false;
                }*/

                if (!ok) {
                    //Intent intent = new Intent();
                    //setResult(2, intent);
                    //finish();
                    return false;
                }

                /*
                Position toSearch = new Position(positionStr);
                List<Position> positionsList = null;
                Position position = null;
                try {
                    positionsList = position_dao.getByCriteria(toSearch);
                    if(positionsList.size()>0){
                        position = positionsList.get(0);
                    } else {
                        position_dao.insert(new Position(positionStr));
                        positionsList = position_dao.getByCriteria(toSearch);
                        if (positionsList.size() > 0) {
                            position = positionsList.get(0);
                        }
                    }
                } catch (GenericDAOException e) {
                    e.printStackTrace();
                }
*/
                //TODO: isto

                player = new Player(nickname, name, nationality, maritalStatus, birthday, height, weight, address, gender, photo, email, preferredFoot, number,null,null);
                boolean corrected = false;

                //insert
                try {
                    playerID = player_dao.insert(player);
                    if(playerID>0)
                        corrected = true;
                } catch (GenericDAOException ex) {
                    System.err.println(studentcompany.sportgest.Players.Player_Activity_Create.class.getName() + " [WARNING] " + ex.toString());
                    Logger.getLogger(studentcompany.sportgest.Players.Player_Activity_Create.class.getName()).log(Level.WARNING, null, ex);
                }

                Intent intent = new Intent();
                intent.putExtra("id", playerID);
                setResult(corrected?1:2, intent);
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
        if(inputLayoutNickname==null)
            inputLayoutNickname = (TextInputLayout) findViewById(R.id.inputLayoutNickname);
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
        if(inputLayoutName==null)
            inputLayoutName = (TextInputLayout) findViewById(R.id.inputLayoutName);
        String pw = tv_name.getText().toString().trim();
        if (pw.isEmpty() || pw.length() < 6) {
            inputLayoutName.setErrorEnabled(true);
            inputLayoutName.setError(getString(R.string.err_name_short));
            //requestFocus(inputLayoutPassword);
            return false;
        }
        inputLayoutName.setErrorEnabled(false);
        return true;
    }

    private boolean validateHeight() {
        if(inputLayoutHeight==null)
            inputLayoutHeight = (TextInputLayout) findViewById(R.id.inputLayoutHeight);
        String pw = tv_height.getText().toString().trim();
        if(!pw.matches("\\d+(\\.\\d+)?")){
            inputLayoutNumber.setError(getString(R.string.err_height_invalid));
            return false;
        }
        if (pw.isEmpty() || (pw.length() >= 1 && pw.length()<4)) {
            int hg = -1;
            if(isNumericInt(pw)){
                hg = Integer.parseInt(pw);
            } else {
                inputLayoutHeight.setError(getString(R.string.err_height_invalid));
                return false;
            }
            if(!(hg<200 && hg>0)){
                inputLayoutHeight.setError(getString(R.string.err_height_invalid));
                return false;
            }
        }
        inputLayoutHeight.setErrorEnabled(false);
        return true;
    }

    private boolean validateWeight() {
        if(inputLayoutWeight==null)
            inputLayoutWeight = (TextInputLayout) findViewById(R.id.inputLayoutWeight);
        String pw = tv_weight.getText().toString().trim();
        if(!pw.matches("\\d+(\\.\\d+)?")){
            inputLayoutNumber.setError(getString(R.string.err_weight_invalid));
            return false;
        }
        if (pw.isEmpty() || (pw.length() > 1 && pw.length()<5)) {
            float wg = -1f;
            if(isNumericFloat(pw)){
                wg = Float.parseFloat(pw);
            } else {
                inputLayoutWeight.setError(getString(R.string.err_weight_invalid));
                return false;
            }
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
        if(inputLayoutAddress==null)
            inputLayoutAddress = (TextInputLayout) findViewById(R.id.inputLayoutAddress);
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
        if(inputLayoutEmail==null)
            inputLayoutEmail = (TextInputLayout) findViewById(R.id.inputLayoutEmail);
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
        if(inputLayoutNumber==null)
            inputLayoutNumber = (TextInputLayout) findViewById(R.id.inputLayoutNumber);
        String pw = tv_number.getText().toString().trim();
        if(!pw.matches("\\d+(\\.\\d+)?")){
            inputLayoutNumber.setError(getString(R.string.err_number));
            return false;
        }
        if (pw.isEmpty() || (pw.length() > 1 && pw.length()<4)) {
            int nb = -1;
            if(isNumericInt(pw)){
                nb = Integer.parseInt(pw);
            } else {
                inputLayoutNumber.setError(getString(R.string.err_number));
                return false;
            }
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
                            txtDate.setText(Integer.toString(year)+"-"+Integer.toString(monthOfYear+1)+"-"+Integer.toString(dayOfMonth));
                            selectedDay = dayOfMonth;
                            selectedMonth = monthOfYear+1;
                            selectedYear = year;

                        }
                    }, mYear, mMonth, mDay);
            dpd.show();
        }
    }

    public static boolean isNumericInt(String str)
    {
        try
        {
            int d = Integer.parseInt(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    public static boolean isNumericFloat(String str)
    {
        try
        {
            float d = Float.parseFloat(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }
}