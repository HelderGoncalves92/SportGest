package studentcompany.sportgest.Exercises;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import studentcompany.sportgest.R;
import studentcompany.sportgest.daos.Attribute_Exercise_DAO;
import studentcompany.sportgest.daos.Exercise_DAO;
import studentcompany.sportgest.daos.Pair;
import studentcompany.sportgest.daos.exceptions.GenericDAOException;
import studentcompany.sportgest.domains.Attribute;
import studentcompany.sportgest.domains.Exercise;

public class Exercise_Activity_ListView extends AppCompatActivity implements Exercise_Fragment_List.OnItemSelected  {

    private Exercise_DAO exercise_dao;
    private Attribute_Exercise_DAO attribute_exercise_dao;
    private List<Exercise> exerciseList;
    private List<Attribute> exerciseAttributesList;
    private int currentPos = 0;
    private Menu mOptionsMenu;

    private DialogFragment mDialog;
    private FragmentManager mFragmentManager;
    private Exercise_Fragment_List mListExercises = new Exercise_Fragment_List();
    private Exercise_Fragment_Details mDetailsExercise = new Exercise_Fragment_Details();
    private static final String TAG = "EXERCISE_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list);

        if(savedInstanceState != null)
            currentPos = savedInstanceState.getInt("currentPos");

        try {
            exercise_dao = new Exercise_DAO(getApplicationContext());
            attribute_exercise_dao = new Attribute_Exercise_DAO(getApplicationContext());

            exerciseList = exercise_dao.getByCriteria(new Exercise(-1, null, null, -1, 0));

            //Check if it is empty
            if(exerciseList == null)
                exerciseList = new ArrayList<Exercise>();

            mListExercises.setExerciseList(exerciseList);

        } catch (GenericDAOException e) {
            e.printStackTrace();
        }

        // Get a reference to the FragmentManager
        mFragmentManager = getSupportFragmentManager();

        // Start a new FragmentTransaction
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        // Add the TitleFragment to the layout
        fragmentTransaction.add(R.id.exercise_list_fragment_container , mListExercises);
        fragmentTransaction.add(R.id.exercise_detail_fragment_container, mDetailsExercise);

        fragmentTransaction.commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("currentPos", currentPos);
    }


    public List<String> getNamesList(List<Exercise> exerciseList){
        ArrayList<String> list = new ArrayList<>();

        for(Exercise e: exerciseList)
            list.add(e.getTitle());
        Collections.sort(list);
        return list;
    }

    public List<String> getAttributesNamesList(List<Attribute> attributeList){
        ArrayList<String> list = new ArrayList<>();

        for(Attribute a: attributeList)
            list.add(a.getName());
        Collections.sort(list);
        return list;
    }

    public void removeExercise(){

        try {
            Exercise exercise = mListExercises.removeItem(currentPos);
            mDetailsExercise.clearDetails();

            if(exercise != null) {

                //remove exercise
                exercise.setDeleted(1);
                exercise_dao.update(exercise);
            }
        }catch (GenericDAOException ex){
            ex.printStackTrace();
        }


        currentPos = -1;
        mOptionsMenu.findItem(R.id.Delete).setVisible(false);
        mOptionsMenu.findItem(R.id.Edit).setVisible(false);
    }
    /************************************
     ****     Listener Functions     ****
     ************************************/

    @Override
     public void itemSelected(int position, int tag) {
        Exercise exercise = exerciseList.get(position);

        if(exercise != null){
            if(currentPos == -1) {
                mOptionsMenu.findItem(R.id.Delete).setVisible(true);
                mOptionsMenu.findItem(R.id.Edit).setVisible(true);

                mDetailsExercise.showFirstElem();
            }

            currentPos = position;
            try {
                exerciseAttributesList = attribute_exercise_dao.getBySecondId(exercise.getId());
            } catch (GenericDAOException ex){
                ex.printStackTrace();
                exerciseAttributesList = new ArrayList<>();
            }
            mDetailsExercise.showExercise(exercise, getAttributesNamesList(exerciseAttributesList));
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
                                    Exercise_Activity_ListView activity = (Exercise_Activity_ListView) getActivity();
                                    activity.DialogDismiss();
                                }
                            })
                    .setPositiveButton(res.getString(R.string.positive_answer),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Exercise_Activity_ListView activity = (Exercise_Activity_ListView) getActivity();
                                    activity.DialogDismiss();
                                    activity.removeExercise();
                                }
                            }).create();
        }
    }

    /************************************
     ****       Menu Functions       ****
     ************************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_crud, menu);
        mOptionsMenu = menu;
        menu.findItem(R.id.Edit).setVisible(false);
        menu.findItem(R.id.Delete).setVisible(false);
        menu.findItem(R.id.Save).setVisible(false);

        //To restore state on Layout Rotation
        if(currentPos != -1 && exerciseList.size()>0) {
            MenuItem item = mOptionsMenu.findItem(R.id.Delete);
            item.setVisible(true);

            item = mOptionsMenu.findItem(R.id.Edit);
            item.setVisible(true);

            try {

                Exercise exercise = exerciseList.get(currentPos);
                exerciseAttributesList = attribute_exercise_dao.getBySecondId(exercise.getId());

                mDetailsExercise.showFirstElem();
                mDetailsExercise.showExercise(exercise, getAttributesNamesList(exerciseAttributesList));
                mListExercises.select_Item(currentPos);

            } catch (GenericDAOException ex){
                ex.printStackTrace();
                exerciseAttributesList = new ArrayList<>();
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.Add:
                intent = new Intent(this, Exercise_Activity_Create.class);
                startActivityForResult(intent, 0);
                return true;

            case R.id.Edit:
                intent = new Intent(this, Exercise_Activity_Create.class);
                //put current exercise ID in extras
                Bundle dataBundle = new Bundle();
                dataBundle.putLong(Exercise_DAO.TABLE_NAME + Exercise_DAO.COLUMN_ID, exerciseList.get(currentPos).getId());
                //add data
                intent.putExtras(dataBundle);
                //start activity
                startActivityForResult(intent, 0);
                return true;

            case R.id.Delete:
                mDialog = AlertToDelete_DialogFragment.newInstance();
                mDialog.show(mFragmentManager, "Alert");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 0) {
            try {
                exerciseList = exercise_dao.getByCriteria(new Exercise(-1, null, null, -1, 0));
                mListExercises.setExerciseList(exerciseList);
                mListExercises.updateList();

            } catch (GenericDAOException e) {
                e.printStackTrace();
            }
            mDetailsExercise.clearDetails();
            currentPos = -1;
            mOptionsMenu.findItem(R.id.Delete).setVisible(false);
            mOptionsMenu.findItem(R.id.Edit).setVisible(false);
        }
    }
}
