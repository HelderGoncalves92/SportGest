package studentcompany.sportgest.Trainings;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import studentcompany.sportgest.Exercises.Exercise_Fragment_Details;
import studentcompany.sportgest.Exercises.Exercise_Fragment_List;
import studentcompany.sportgest.R;
import studentcompany.sportgest.daos.Attribute_Exercise_DAO;
import studentcompany.sportgest.daos.Exercise_DAO;
import studentcompany.sportgest.daos.Team_DAO;
import studentcompany.sportgest.daos.Training_DAO;
import studentcompany.sportgest.daos.Training_Exercise_DAO;
import studentcompany.sportgest.daos.exceptions.GenericDAOException;
import studentcompany.sportgest.domains.Attribute;
import studentcompany.sportgest.domains.Exercise;
import studentcompany.sportgest.domains.Training;
import studentcompany.sportgest.domains.TrainingExercise;

public class Training_Activity_Create_Exercises extends AppCompatActivity implements Exercise_Fragment_List.OnItemSelected  {
    private DialogFragment mDialog;
    private FragmentManager mFragmentManager;
    private Exercise_Fragment_Details mDetailsExercise = new Exercise_Fragment_Details();

    private Exercise_Fragment_List mListExercisesAvailable = new Exercise_Fragment_List();
    private Exercise_Fragment_List mListExercisesSelected = new Exercise_Fragment_List();

    private static final String TAG = "CREATE_TRAINING_ACTIVITY";
    private TextView tv_repetitions, tv_duration;
    private ImageButton b_repMore, b_repLess;

    //DAOs
    private Team_DAO team_dao;
    private Training_DAO training_dao;
    private Exercise_DAO exercise_dao;
    private Attribute_Exercise_DAO attribute_exercise_dao;
    private Training_Exercise_DAO training_exercise_dao;

    //Id of current training displayed
    //private Exercise exercise;
    private Training training;
    private TrainingExercise trainingExercise;
    private List<Exercise> exerciseList;
    private List<Attribute> exerciseAttributesList;
    private List<Exercise> availableExercises;
    private ArrayList<Exercise> trainingExercises;
    private Map<Long,Integer> repetitionsExercises; //Map para associar a cada exercídio o seu número de repetições

    private long trainingID;
    private String trainingName;
    private String trainingDescription;
    private int trainingDuration;
    private long trainingDateInMilis;
    private int trainingExerciseRepetitions;
    private Menu mOptionsMenu;
    private int positionaux;
    private int passing;

    private static final int AVAILABLE = 1;
    private static final int SELECTED = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_training_exercises);

        //initialize required DAOs
        team_dao = new Team_DAO(this);
        training_dao = new Training_DAO(this);
        exercise_dao = new Exercise_DAO(this);
        attribute_exercise_dao = new Attribute_Exercise_DAO(this);
        training_exercise_dao = new Training_Exercise_DAO(this);

        if(training_dao==null || exercise_dao==null || attribute_exercise_dao==null || training_exercise_dao==null){
            System.err.println(Training_Activity_Create.class.getName() + " [ERROR] DAOs not created");
            return;
        }

        //initialize views
        tv_repetitions = (TextView) findViewById(R.id.exercise_repetitions);
        b_repLess = (ImageButton) findViewById(R.id.selected_exercise_repetitions_less);
        b_repMore = (ImageButton) findViewById(R.id.selected_exercise_repetitions_more);
        tv_duration = (TextView) findViewById(R.id.training_total_duration);

        //initialize variables
        training = null;
        exerciseList = new ArrayList<>();
        exerciseAttributesList = new ArrayList<>();
        availableExercises = new ArrayList<>();
        trainingExercises = new ArrayList<>();
        repetitionsExercises=new HashMap<>();
        positionaux=-1;
        passing=-1;

        //tv_repetitions.setText("");
        tv_repetitions.setFocusable(false);
        tv_repetitions.setClickable(false);
        //tv_duration.setText("");
        tv_duration.setFocusable(false);
        tv_duration.setClickable(false);
        b_repMore.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentRep = tv_repetitions.getText().toString();
                int repetitions = 0;
                if (!currentRep.isEmpty()) {
                    repetitions = Integer.parseInt(currentRep);
                    if (repetitions < 0) repetitions = 0;
                }
                repetitions++;
                tv_repetitions.setText(Integer.toString(repetitions));
            }
        });
        b_repLess.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentRep = tv_repetitions.getText().toString();
                int repetitions = 0;
                if (!currentRep.isEmpty()) {
                    repetitions = Integer.parseInt(currentRep);
                    if (repetitions < 0) repetitions = 0;
                }
                if (repetitions > 0) {
                    repetitions -= 1;
                }
                tv_repetitions.setText(Integer.toString(repetitions));
            }
        });
        mListExercisesAvailable.setTag(AVAILABLE);
        mListExercisesSelected.setTag(SELECTED);
        mListExercisesAvailable.setExerciseList(availableExercises);
        mListExercisesSelected.setExerciseList(trainingExercises);

        //get a list of available exercises
        try {
            availableExercises = exercise_dao.getByCriteria(new Exercise(-1, null, null, -1, 0));
        } catch (GenericDAOException ex){
            System.err.println(Training_Activity_Create.class.getName() + " [WARNING] " + ex.toString());
            Logger.getLogger(Training_Activity_Create.class.getName()).log(Level.WARNING, null, ex);
        }

        //if update -> get object id
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            //get Exercise ID
            trainingID = extras.getLong(Training_DAO.TABLE_NAME + Training_DAO.COLUMN_ID);
            trainingName = extras.getString(Training_DAO.TABLE_NAME + Training_DAO.COLUMN_TITLE);
            trainingDescription = extras.getString(Training_DAO.TABLE_NAME + Training_DAO.COLUMN_DESCRIPTION);
            trainingDateInMilis = extras.getLong(Training_DAO.TABLE_NAME + Training_DAO.COLUMN_DATE);

            //validation
            if(trainingID > 0){
                //get the training information
                try {
                    training = training_dao.getById(trainingID);
                } catch (GenericDAOException ex){
                    System.err.println(Training_Activity_Create.class.getName() + " [WARNING] " + ex.toString());
                    Logger.getLogger(Training_Activity_Create.class.getName()).log(Level.WARNING, null, ex);
                }

                //validation
                if(training != null) {

                    //get list of exercises allocated to some trainingID
                    try {
                        ArrayList<TrainingExercise> auxTE = (ArrayList<TrainingExercise>) training_exercise_dao.getByCriteria(new TrainingExercise(-1, training, null, -1));
                        for(TrainingExercise te : auxTE){
                            if(te.getExercise()!=null) {
                                trainingExercises.add(te.getExercise());

                                repetitionsExercises.put(te.getExercise().getId(),te.getRepetitions());
                                updateDuration(te.getExercise().getDuration(),te.getRepetitions(),1);
                            }
                        }
                    } catch (GenericDAOException ex){
                        System.err.println(Training_Activity_Create.class.getName() + " [WARNING] " + ex.toString());
                        Logger.getLogger(Training_Activity_Create.class.getName()).log(Level.WARNING, null, ex);
                    }

                    //subtract from available already selected attributes
                    for(Exercise e: trainingExercises){
                        availableExercises.remove(e);
                    }

                    //set list in layout ListView
                    mListExercisesSelected.setExerciseList(trainingExercises);

                }//if(training != null)

            }//if(trainingID > 0)

        }//if(extras != null)

        //set available attributes ListView
        mListExercisesAvailable.setExerciseList(availableExercises);

        // Get a reference to the FragmentManager
        mFragmentManager = getSupportFragmentManager();

        // Start a new FragmentTransaction
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        // Add the TitleFragment to the layout
        fragmentTransaction.add(R.id.training_available_exercises_list_fragment_container , mListExercisesAvailable);
        fragmentTransaction.add(R.id.training_selected_exercises_list_fragment_container,   mListExercisesSelected);
        fragmentTransaction.add(R.id.exercise_detail_fragment_container, mDetailsExercise);

        fragmentTransaction.commit();
    }

    private List<String> exerciseNamesFromList(List<Exercise> listAttr){
        ArrayList<String> res = new ArrayList<>();
        for(Exercise e : listAttr){
            res.add(e.getTitle());
        }
        Collections.sort(res);
        return res;
    }

    /************************************
     ****     Listener Functions     ****
     ************************************/

    public void itemSelected(int position, int tag) {
        long id_To_Search;
        Exercise ex;

        switch (tag) {
            case AVAILABLE:
                tv_repetitions.setError(null);
                //Add the attribute to the selected ones
                System.err.println("#################Previous: " + positionaux + " new: " + position);
                if (positionaux!=position) {
                    System.err.println("Falta 1");
                    positionaux = position;
                    printExercise(position, 1);
                    tv_repetitions.setText("0");

                } else {
                    System.err.println("Passou");
                    id_To_Search = availableExercises.get(position).getId();
                    try {
                        ex = exercise_dao.getById(id_To_Search);
                        if (ex != null) {
                            int reps = Integer.parseInt(tv_repetitions.getText().toString());

                            if (reps < 1) {
                                tv_repetitions.setError("At least greater than 0");
                            } else {
                                availableExercises.remove(ex);
                                trainingExercises.add(ex);
                                repetitionsExercises.put(id_To_Search, reps);

                                Collections.sort(trainingExercises, new Comparator<Exercise>() {
                                    @Override
                                    public int compare(Exercise lhs, Exercise rhs) {
                                        return lhs.getTitle().compareTo(rhs.getTitle());
                                    }

                                });


                                //update ListViews
                                mListExercisesAvailable.updateList(availableExercises);
                                mListExercisesSelected.updateList(trainingExercises);

                                //update duration
                                updateDuration(ex.getDuration(), reps, 1);
                            }
                        }
                    } catch (GenericDAOException e) {
                        System.err.println(Training_Activity_Create.class.getName() + " [WARNING] " + e.toString());
                        Logger.getLogger(Training_Activity_Create.class.getName()).log(Level.WARNING, null, e);
                    }
                    positionaux=-1;
                    mDetailsExercise.clearDetails();
                    tv_repetitions.setText("0");

                }//onItemClick
                break;
            case SELECTED:
                tv_repetitions.setError(null);
                if (passing != position) {
                    passing=position;
                    printExercise(position,0);
                    id_To_Search = trainingExercises.get(position).getId();
                    int reps= repetitionsExercises.get(id_To_Search);
                    tv_repetitions.setText(Integer.toString(reps));
                } else {
                    id_To_Search = trainingExercises.get(position).getId();
                    try {
                        ex = exercise_dao.getById(id_To_Search);
                        if (ex != null) {
                            trainingExercises.remove(ex);
                            availableExercises.add(ex);

                            int reps= repetitionsExercises.get(id_To_Search);

                            Collections.sort(availableExercises, new Comparator<Exercise>() {
                                @Override
                                public int compare(Exercise lhs, Exercise rhs) {
                                    return lhs.getTitle().compareTo(rhs.getTitle());
                                }
                            });

                            //update ListViews
                            mListExercisesAvailable.updateList(availableExercises);
                            mListExercisesSelected.updateList(trainingExercises);

                            //update duration
                            updateDuration(ex.getDuration(), reps,0);

                            //remover do map o exercicio que foi eliminado
                            repetitionsExercises.remove(id_To_Search);
                        }
                    } catch (GenericDAOException e) {
                        System.err.println(Training_Activity_Create.class.getName() + " [WARNING] " + e.toString());
                        Logger.getLogger(Training_Activity_Create.class.getName()).log(Level.WARNING, null, e);
                    }
                    passing= -1;
                    mDetailsExercise.clearDetails();
                    tv_repetitions.setText("0");
                }//onItemClick
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar_crud, menu);
        mOptionsMenu = menu;
        //if its for updating an entry -> Save & Delete
        //for inserting a new entry -> Save
        if(training == null) {
            menu.findItem(R.id.Delete).setVisible(false);
        }
        menu.findItem(R.id.Add).setVisible(false);
        menu.findItem(R.id.Edit).setVisible(false);
        menu.findItem(R.id.Forward).setVisible(false);
        return true;
    }

    public List<String> getAttributesNamesList(List<Attribute> attributeList){
        ArrayList<String> list = new ArrayList<>();

        for(Attribute a: attributeList)
            list.add(a.getName());
        Collections.sort(list);
        return list;
    }

    public void updateDuration(int duration, int reps, int v){

        int newduration=Integer.parseInt(tv_duration.getText().toString());

        duration = duration*reps;

        if(v==1){
            newduration+=duration;
            tv_duration.setText("" + newduration);
            tv_duration.setFocusable(false);
            tv_duration.setClickable(false);
        }
        else{
            newduration-=duration;
            tv_duration.setText("" + newduration);
            tv_duration.setFocusable(false);
            tv_duration.setClickable(false);
        }
    }

    public void printExercise(int position, int v) {

        if (v == 1) {
            Exercise exercise = availableExercises.get(position);
            if(exercise!=null){
                try {
                    exerciseAttributesList = attribute_exercise_dao.getBySecondId(exercise.getId());
                } catch (GenericDAOException ex){
                    ex.printStackTrace();
                    exerciseAttributesList = new ArrayList<>();
                }
                mDetailsExercise.showExercise(exercise, getAttributesNamesList(exerciseAttributesList));
            }
        }
        else{

            Exercise exercise =trainingExercises.get(position);
            if(exercise!=null){
                try {
                    exerciseAttributesList = attribute_exercise_dao.getBySecondId(exercise.getId());
                } catch (GenericDAOException ex) {
                    ex.printStackTrace();
                    exerciseAttributesList = new ArrayList<>();
                }
                mDetailsExercise.showExercise(exercise, getAttributesNamesList(exerciseAttributesList));
            }
        }
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        boolean result = false;

        switch(item.getItemId())
        {
            //add action
            case R.id.Save:
                // validate inputs

                if(trainingName.isEmpty() || trainingDateInMilis <= 0){
                    return false;
                }
                trainingDuration = Integer.parseInt(tv_duration.getText().toString());
                trainingExerciseRepetitions = Integer.parseInt(tv_repetitions.getText().toString());

                //insert/update database
                if(training != null && training.getId() > 0) {
                    training.setTitle(trainingName);
                    training.setDescription(trainingDescription);
                    training.setTotalDuration(trainingDuration);
                    training.setDate(trainingDateInMilis);
                    //training.setTeam();
                    try {
                        result = training_dao.update(training);
                        if(result){
                            ArrayList<TrainingExercise> previousTrainingExercises = (ArrayList<TrainingExercise>) training_exercise_dao.getByCriteria(new TrainingExercise(-1, training, null, -1));
                            for(TrainingExercise te: previousTrainingExercises){
                                training_exercise_dao.delete(te);
                            }
                            for(Exercise e:trainingExercises){
                                //training_exercise_dao.insert(new TrainingExercise(-1, training, e, trainingExerciseRepetitions));
                                training_exercise_dao.insert(new TrainingExercise(-1, training, e, repetitionsExercises.get(e.getId())));

                            }
                        }
                    }catch (GenericDAOException ex){
                        System.err.println(Training_Activity_Create.class.getName() + " [WARNING] " + ex.toString());
                        Logger.getLogger(Training_Activity_Create.class.getName()).log(Level.WARNING, null, ex);
                    }

                    if(result){
                        Toast.makeText(getApplicationContext(), R.string.updated, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.not_updated, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        training = new Training(
                                -1,
                                trainingName,
                                trainingDescription,
                                trainingDateInMilis,
                                trainingDuration,
                                team_dao.getById(1),0); //TODO what team is selected???
                        training.setId(training_dao.insert(training));
                        result = training.getId() > 0;
                        if(result){
                            for(Exercise e:trainingExercises){
                                //training_exercise_dao.insert(new TrainingExercise(-1, training, e, Integer.parseInt(tv_repetitions.getText().toString())));
                                training_exercise_dao.insert(new TrainingExercise(-1, training, e, repetitionsExercises.get(e.getId())));

                            }
                        }
                    }catch (GenericDAOException ex){
                        System.err.println(Training_Activity_Create.class.getName() + " [WARNING] " + ex.toString());
                        Logger.getLogger(Training_Activity_Create.class.getName()).log(Level.WARNING, null, ex);
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
                                    ArrayList<TrainingExercise> auxTE =(ArrayList<TrainingExercise>) training_exercise_dao.getByCriteria(new TrainingExercise(-1, training, null, -1));
                                    for(TrainingExercise te : auxTE){
                                        training_exercise_dao.delete(te);
                                    }
                                    training_dao.delete(training);
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
