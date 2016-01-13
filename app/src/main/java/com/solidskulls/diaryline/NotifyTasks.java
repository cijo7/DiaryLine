package com.solidskulls.diaryline;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
    public static final String ACTION_CLOSE ="Close";
    public static final String ACTION_EDITOR ="Editor";
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
     * @param msg The message to display on notify.
     * @param act The action to take place when clicked ok.
     * @return A new instance of fragment NotifyTasks.
     */

    public static NotifyTasks newInstance(String msg, String act) {
        NotifyTasks fragment = new NotifyTasks();
        Bundle args = new Bundle();
        args.putString(ARG_NOTIFY_MSG, msg);
        args.putString(ARG_ACTION, act);
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

        View v=getView();
        if(v!=null) {
            TextView msg = (TextView) v.findViewById(R.id.notify_message);
            msg.setText(mNotify_Msg);
        }
        if(getView()!=null) {
            Button later = (Button) getView().findViewById(R.id.button_later);
            Button action = (Button) getView().findViewById(R.id.button_action);

            later.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onButtonPressed(ACTION_CLOSE);
                }
            });
            switch (mAction) {
                case ACTION_EDITOR:
                    action.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onButtonPressed(ACTION_EDITOR);
                        }
                    });
                    break;
            }
        }else
            Log.d("Notify", "Unable to set Events Listeners");
    }
    public void onButtonPressed(String action) {
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
        void onNotifyInteraction(String action);
    }

}
