package studentcompany.sportgest.Exercises;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import studentcompany.sportgest.Attributes.Attribute_Fragment_List;
import studentcompany.sportgest.R;
import studentcompany.sportgest.daos.Attribute_DAO;
import studentcompany.sportgest.daos.Attribute_Exercise_DAO;
import studentcompany.sportgest.daos.Exercise_DAO;
import studentcompany.sportgest.daos.Pair;
import studentcompany.sportgest.daos.exceptions.GenericDAOException;
import studentcompany.sportgest.domains.Attribute;
import studentcompany.sportgest.domains.Exercise;


public class Exercise_Activity_Create extends AppCompatActivity implements Attribute_Fragment_List.OnItemSelected  {

    private static final String TAG = "CREATE_EXERCISE_ACTIVITY";
    private TextView tv_name, tv_duration;
    private EditText et_description;
    private ImageView iv_image;

    //DAOs
    private Exercise_DAO exercise_dao;
    private Attribute_DAO attribute_dao;
    private Attribute_Exercise_DAO attribute_exercise_dao;

    //Id of current exercise displayed
    private Exercise exercise;
    private List<Attribute> availableAttributes;
    private ArrayList<Attribute> exerciseAttributes;

    private FragmentManager mFragmentManager;
    private Attribute_Fragment_List mListAttributesAvailable = new Attribute_Fragment_List();
    private Attribute_Fragment_List mListAttributesSelected = new Attribute_Fragment_List();

    private static final int AVAILABLE = 0;
    private static final int SELECTED = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_exercise);

        //initialize required DAOs
        exercise_dao = new Exercise_DAO(this);
        attribute_dao = new Attribute_DAO(this);
        attribute_exercise_dao = new Attribute_Exercise_DAO(this);

        if(exercise_dao==null || attribute_dao==null || attribute_exercise_dao==null){
            System.err.println(Exercise_Activity_Create.class.getName() + " [ERROR] DAOs not created");
            return;
        }

        //initialize views
        tv_name = (TextView) findViewById(R.id.exercise_name);
        et_description = (EditText) findViewById(R.id.exercise_description);
        tv_duration = (TextView) findViewById(R.id.exercise_duration);
        iv_image = (ImageView) findViewById(R.id.exercise_image);

        //initialize variables
        exercise = null;
        availableAttributes = new ArrayList<>();
        exerciseAttributes = new ArrayList<>();

        tv_name.setText("");
        tv_name.setFocusable(true);
        tv_name.setClickable(true);
        et_description.setText("");
        et_description.setFocusable(true);
        et_description.setClickable(true);
        tv_duration.setText("");
        tv_duration.setFocusable(true);
        tv_duration.setClickable(true);
        iv_image.setImageResource(R.drawable.pii);
        iv_image.setFocusable(true);
        iv_image.setClickable(true);
        mListAttributesAvailable.setTag(AVAILABLE);
        mListAttributesSelected.setTag(SELECTED);
        mListAttributesAvailable.setList(availableAttributes);
        mListAttributesSelected.setList(exerciseAttributes);

        //get a list of available attributes
        try {
            availableAttributes = attribute_dao.getByCriteria(new Attribute(-1, null, null, 0));
        } catch (GenericDAOException ex){
            System.err.println(Exercise_Activity_Create.class.getName() + " [WARNING] " + ex.toString());
            Logger.getLogger(Exercise_Activity_Create.class.getName()).log(Level.WARNING, null, ex);
        }

        //if update -> get object id
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            //get Exercise ID
            long exerciseID = extras.getLong(Exercise_DAO.TABLE_NAME + Exercise_DAO.COLUMN_ID);

            //validation
            if(exerciseID > 0){
                //get the exercise information
                try {
                    exercise = exercise_dao.getById(exerciseID);
                } catch (GenericDAOException ex){
                    System.err.println(Exercise_Activity_Create.class.getName() + " [WARNING] " + ex.toString());
                    Logger.getLogger(Exercise_Activity_Create.class.getName()).log(Level.WARNING, null, ex);
                }

                //validation
                if(exercise != null) {
                    //set layout variables with information
                    tv_name.setText(exercise.getTitle());
                    tv_duration.setText(String.valueOf(exercise.getDuration()));
                    et_description.setText(exercise.getDescription());

                    //get list of attributes allocated to some exerciseID
                    try {
                        exerciseAttributes = (ArrayList) attribute_exercise_dao.getBySecondId(exerciseID);
                    } catch (GenericDAOException ex){
                        System.err.println(Exercise_Activity_Create.class.getName() + " [WARNING] " + ex.toString());
                        Logger.getLogger(Exercise_Activity_Create.class.getName()).log(Level.WARNING, null, ex);
                    }

                    //subtract from available already selected attributes
                    for(Attribute a: exerciseAttributes){
                        availableAttributes.remove(a);
                    }

                    //set list in layout ListView
                    mListAttributesSelected.setList(exerciseAttributes);

                }//if(exercise != null)

            }//if(exerciseID > 0)

        }//if(extras != null)

        //set available attributes ListView
        mListAttributesAvailable.setList(availableAttributes);

        // Get a reference to the FragmentManager
        mFragmentManager = getSupportFragmentManager();

        // Start a new FragmentTransaction
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        // Add the TitleFragment to the layout
        fragmentTransaction.add(R.id.available_attribute_list_fragment_container , mListAttributesAvailable);
        fragmentTransaction.add(R.id.selected_attribute_list_fragment_container, mListAttributesSelected);

        fragmentTransaction.commit();
    }

    private List<String> attributeNamesFromList(List<Attribute> listAttr){
        ArrayList<String> res = new ArrayList<>();
        for(Attribute a : listAttr){
            res.add(a.getName());
        }
        return res;
    }

    /************************************
     ****     Listener Functions     ****
     ************************************/

    public void itemSelected(int position, int tag) {
        long id_To_Search;
        Attribute attr;
        switch (tag) {
            case AVAILABLE:
                //Add the attribute to the selected ones
                id_To_Search = availableAttributes.get(position).getId();
                try {
                    attr = attribute_dao.getById(id_To_Search);
                    if(attr != null){
                        availableAttributes.remove(attr);
                        exerciseAttributes.add(attr);

                        Collections.sort(exerciseAttributes, new Comparator<Attribute>() {
                            @Override
                            public int compare(Attribute lhs, Attribute rhs) {
                                return lhs.getName().compareTo(rhs.getName());
                            }
                        });

                        //update ListViews
                        mListAttributesAvailable.updateList(availableAttributes);
                        mListAttributesSelected.updateList(exerciseAttributes);
                    }
                } catch (GenericDAOException ex){
                    System.err.println(Exercise_Activity_Create.class.getName() + " [WARNING] " + ex.toString());
                    Logger.getLogger(Exercise_Activity_Create.class.getName()).log(Level.WARNING, null, ex);
                }
                break;
            case SELECTED:
                id_To_Search = exerciseAttributes.get(position).getId();
                try {
                    attr = attribute_dao.getById(id_To_Search);
                    if(attr != null){
                        exerciseAttributes.remove(attr);
                        availableAttributes.add(attr);

                        Collections.sort(availableAttributes, new Comparator<Attribute>() {
                            @Override
                            public int compare(Attribute lhs, Attribute rhs) {
                                return lhs.getName().compareTo(rhs.getName());
                            }
                        });

                        //update ListViews
                        mListAttributesAvailable.updateList(availableAttributes);
                        mListAttributesSelected.updateList(exerciseAttributes);
                    }
                } catch (GenericDAOException ex) {
                    System.err.println(Exercise_Activity_Create.class.getName() + " [WARNING] " + ex.toString());
                    Logger.getLogger(Exercise_Activity_Create.class.getName()).log(Level.WARNING, null, ex);
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar_crud, menu);
        //if its for updating an entry -> Save & Delete
        if(exercise != null) {
            menu.findItem(R.id.Add).setVisible(false);
            menu.findItem(R.id.Edit).setVisible(false);
        } else {//for inserting a new entry -> Save
            menu.findItem(R.id.Add).setVisible(false);
            menu.findItem(R.id.Edit).setVisible(false);
            menu.findItem(R.id.Delete).setVisible(false);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        boolean result = false;


        switch(item.getItemId())
        {
            //add action
            case R.id.Save:
                // validate inputs
                if(tv_name.getText().toString().isEmpty()){
                    return false;
                }

                //insert/update database
                if(exercise != null) {
                    exercise.setTitle(tv_name.getText().toString());
                    exercise.setDescription(et_description.getText().toString());
                    exercise.setDuration(Integer.parseInt(tv_duration.getText().toString()));
                    try {
                        result = exercise_dao.update(exercise);
                        if(result){
                            ArrayList<Attribute> previousExerciseAttributes = (ArrayList) attribute_exercise_dao.getBySecondId(exercise.getId());
                            for(Attribute a: previousExerciseAttributes){
                                attribute_exercise_dao.delete(new Pair<>(a, exercise));
                            }
                            for(Attribute a:exerciseAttributes){
                                attribute_exercise_dao.insert(new Pair<>(a, exercise));
                            }
                        }
                    }catch (GenericDAOException ex){
                        System.err.println(Exercise_Activity_Create.class.getName() + " [WARNING] " + ex.toString());
                        Logger.getLogger(Exercise_Activity_Create.class.getName()).log(Level.WARNING, null, ex);
                    }

                    if(result){
                        Toast.makeText(getApplicationContext(), R.string.updated, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.not_updated, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    exercise = new Exercise(
                            -1,
                            tv_name.getText().toString(),
                            et_description.getText().toString(),
                            Integer.parseInt(tv_duration.getText().toString()), 0);
                    try {
                        exercise.setId(exercise_dao.insert(exercise));
                        result = exercise.getId() > 0;
                        if(result){
                            for(Attribute a:exerciseAttributes){
                                attribute_exercise_dao.insert(new Pair<>(a, exercise));
                            }
                        }
                    }catch (GenericDAOException ex){
                        System.err.println(Exercise_Activity_Create.class.getName() + " [WARNING] " + ex.toString());
                        Logger.getLogger(Exercise_Activity_Create.class.getName()).log(Level.WARNING, null, ex);
                    }

                    if(result){
                        Toast.makeText(getApplicationContext(), R.string.inserted, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.not_inserted, Toast.LENGTH_SHORT).show();
                    }
                }

                finish();

                return true;

            case R.id.Delete:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.delete_confirmation)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    exerciseAttributes = (ArrayList) attribute_exercise_dao.getBySecondId(exercise.getId());
                                    for(Attribute a: exerciseAttributes){
                                        attribute_exercise_dao.delete(new Pair<>(a, exercise));
                                    }
                                    exercise_dao.delete(exercise);
                                } catch (GenericDAOException ex){
                                    ex.printStackTrace();
                                }
                                Toast.makeText(getApplicationContext(), R.string.delete_successful, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                AlertDialog d = builder.create();
                d.setTitle(R.string.are_you_sure);
                d.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}