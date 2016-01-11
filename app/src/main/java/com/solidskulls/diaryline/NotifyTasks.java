package com.solidskulls.diaryline;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NotifyTasks.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NotifyTasks#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotifyTasks extends Fragment {
    public static final int CLOSE=0;
    public static final int EDITOR=1;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_NOTIFY_MSG = "Message";
    private static final String ARG_ACTION = "Event";


    private String mNotify_Msg;
    private String mAction;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotifyTasks.
     */

    public static NotifyTasks newInstance(String param1, String param2) {
        NotifyTasks fragment = new NotifyTasks();
        Bundle args = new Bundle();
        args.putString(ARG_NOTIFY_MSG, param1);
        args.putString(ARG_ACTION, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public NotifyTasks() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNotify_Msg = getArguments().getString(ARG_NOTIFY_MSG);
            mAction = getArguments().getString(ARG_ACTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notify_tasks, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();


        TextView msg=(TextView)getView().findViewById(R.id.notify_message);
        msg.setText(mNotify_Msg);
        Button later=(Button)getView().findViewById(R.id.button_later);
        Button action=(Button)getView().findViewById(R.id.button_action);
        later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(CLOSE);
            }
        });
        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(EDITOR);
            }
        });
    }
    public void onButtonPressed(int action) {
        if (mListener != null) {
            mListener.onNotifyInteraction(action);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onNotifyInteraction(int action);
    }

}
