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
    private static final String ARG_POSITION = "position";
    private static Bitmap signBitMap=null;

    static final int NOTIFY_POPUP=5;

    private DataBlockManager dataBlockManager;
    private int mOffsetDays,mPosition;
    private String mText=null;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param offset The number of days offset. .
     * @return A new instance of fragment DiaryTextPreview.
     */
    public static DiaryTextPreview newInstance(int offset) {
        DiaryTextPreview fragment = new DiaryTextPreview();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, offset);
        fragment.setArguments(args);
        return fragment;
    }

    public DiaryTextPreview() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPosition = getArguments().getInt(ARG_POSITION);
        }
        dataBlockManager=new DataBlockManager(mOffsetDays=(DLMainActivity.COUNT-1-mPosition));
        dataBlockManager.readPackage();
        mText=dataBlockManager.getStringData();
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
            if(mText!=null) {
                ((TextView) view.findViewById(R.id.preview_Text)).setText(mText);
                if (signBitMap!=null)
                    ((ImageView)view.findViewById(R.id.imageView_sign)).setImageBitmap(signBitMap);
            }
            else {
                ( view.findViewById(R.id.blankIcon)).setVisibility(View.VISIBLE);
                (view.findViewById(R.id.blankIconText)).setVisibility(View.VISIBLE);
            }
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
     * The interface to Activity
     */
    public interface OnFragmentInteractionListener {

    }


    public static boolean updateBitmap(Context context){
        FileInputStream fileInputStream=null;
        try {
            fileInputStream = context.openFileInput("sign.png");
            signBitMap= BitmapFactory.decodeStream(fileInputStream);
        }catch (FileNotFoundException e){
            Timber.d(e,"Bit map File not found");
            signBitMap=null;
        }

        try {
            if(fileInputStream!=null)
                fileInputStream.close();
        } catch (IOException e) {
            Timber.d(e,"Bitmap File close failed");
        }
        return signBitMap!=null;
    }
    public static void recycleBitmap(){
        if(signBitMap!=null)
            signBitMap.recycle();
    }
}
