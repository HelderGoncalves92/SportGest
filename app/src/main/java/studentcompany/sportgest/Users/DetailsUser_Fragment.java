package studentcompany.sportgest.Users;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import studentcompany.sportgest.R;
import studentcompany.sportgest.domains.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsUser_Fragment extends Fragment {

    private static final String TAG = "DETAILS_USER_FRAGMENT";
    private TextView tv_username, tv_name, tv_email;
    private ImageView tv_photo;

    public DetailsUser_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        LayoutInflater lf = getActivity().getLayoutInflater();

        View view =  lf.inflate(R.layout.fragment_user_details, container, false);
        tv_username = (TextView) view.findViewById(R.id.username);
        tv_name = (TextView) view.findViewById(R.id.name);
        tv_email = (TextView) view.findViewById(R.id.email);
        tv_photo = (ImageView) view.findViewById(R.id.photo);

        return view;
    }

    public void showUser(User user){
        tv_username.setText(user.getUsername());
        tv_name.setText(user.getName());
        tv_email.setText(user.getEmail());
        tv_photo.setImageURI(Uri.parse(user.getPhoto()));
    }

    public void clearDetails(){
        tv_username.setText("");
        tv_name.setText("");
        tv_email.setText("");
        tv_photo.setImageURI(Uri.parse("defaulf"));
    }
}
