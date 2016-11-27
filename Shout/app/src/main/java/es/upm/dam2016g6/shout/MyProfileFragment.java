package es.upm.dam2016g6.shout;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyProfileFragment.OnProfileInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyProfileFragment extends android.support.v4.app.Fragment {
    /* Consult here for lifecycle information: {@link https://developer.android.com/guide/components/fragments.html#Creating} */
    private static final String TAG = "TAG_MPF";

    private View mView;

    private OnProfileInteractionListener mListener;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MyProfileFragment.
     */
    public static MyProfileFragment newInstance() {
        MyProfileFragment fragment = new MyProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_my_profile, container, false);

        Toolbar toolbar = (Toolbar) mView.findViewById(R.id.toolbar);
        toolbar.setTitle("My Profile");
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        loadUserData(mView);

        return mView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnProfileInteractionListener) {
            mListener = (OnProfileInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnProfileInteractionListener {
        void onProfileInteraction();
    }

    private void loadUserData(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String profileImgUrl = user.getPhotoUrl().toString();

        Glide.with(this)
                .load(profileImgUrl)
                .into((ImageView) mView.findViewById(R.id.img_profilePic));

        ((TextView) mView.findViewById(R.id.tv_welcome)).setText(user.getDisplayName());
        RecyclerView rv = (RecyclerView) MyProfileFragment.this.mView.findViewById(R.id.rv_mylikes);
        // Retrieve Facebook User Likes
        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);
        /* make the API call */
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Handle result
                        try {
//                            System.out.println(response);
                            JSONArray JSONFbLikes = object.getJSONObject("likes").getJSONArray("data");
//                            System.out.println(JSONFbLikes);

                            List<FacebookLike> fbLikes = new ArrayList<FacebookLike>(JSONFbLikes.length());

                            for (int i = 0; i < JSONFbLikes.length(); i++) {
                                JSONObject fbLike = JSONFbLikes.getJSONObject(i);
//                                System.out.println(fbLike.getString("name"));
                                fbLikes.add(new FacebookLike(fbLike.getString("name"), fbLike.getString("id")));
                            }

                            RecyclerView rv = (RecyclerView) MyProfileFragment.this.mView.findViewById(R.id.rv_mylikes);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(MyProfileFragment.this.getActivity());
                            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                            layoutManager.scrollToPosition(0);
                            rv.setLayoutManager(layoutManager);
                            MyLikesRecyclerViewAdapter adapter = new MyLikesRecyclerViewAdapter(fbLikes);
                            rv.setAdapter(adapter);
                        } catch (Throwable t) {
                            System.err.println(t);
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "likes");
        request.setParameters(parameters);
        request.executeAsync();
    }
}
