package com.solidskulls.diaryline;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import timber.log.Timber;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DiaryTextPreview.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DiaryTextPreview#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiaryTextPreview extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_DATE = "date";
    private static final String ARG_TEXT="text";
    private static Bitmap signBitMap=null;

    static final int NOTIFY_POPUP=5;

    private String mDate;
    private String mText;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param text The text to display.
     * @param date The display date string .
     * @return A new instance of fragment DiaryTextPreview.
     */
    public static DiaryTextPreview newInstance(String text,String date) {
        DiaryTextPreview fragment = new DiaryTextPreview();
        Bundle args = new Bundle();
        args.putString(ARG_DATE, date);
        args.putString(ARG_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    public DiaryTextPreview() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDate = getArguments().getString(ARG_DATE);
            mText=getArguments().getString(ARG_TEXT);
        }

    }

    public static boolean updateBitmap(Context context){
        FileInputStream fileInputStream=null;
        try {
            fileInputStream = context.openFileInput("sign.png");
            signBitMap= BitmapFactory.decodeStream(fileInputStream);
        }catch (FileNotFoundException e){
            Timber.d(e,"Bit map File not found");
        }

        try {
            fileInputStream.close();
        } catch (IOException e) {
            Timber.d(e,"Bitmap File close failed");
        }
        return signBitMap!=null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_diary_text, container, false);

    }
    @Override
    public void onStart(){
        super.onStart();
        View view=getView();
        if(view!=null) {
            ((TextView) view.findViewById(R.id.preview_Text)).setText(mText);
            ((TextView) view.findViewById(R.id.preview_date)).setText(mDate);
            if (signBitMap!=null)
                ((ImageView)view.findViewById(R.id.imageView_sign)).setImageBitmap(signBitMap);
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
    }

}
