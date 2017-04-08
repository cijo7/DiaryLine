package com.solidskulls.diaryline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.solidskulls.diaryline.adapters.ViewRecyclerAdapter;
import com.solidskulls.diaryline.data.DataBlockContainer;
import com.solidskulls.diaryline.data.DataBlockManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import timber.log.Timber;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.solidskulls.diaryline.DiaryTextPreview.OnContentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DiaryTextPreview#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiaryTextPreview extends Fragment implements ViewRecyclerAdapter.AdapterListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_POSITION = "position";
    private static Bitmap signBitMap=null;

    static final int NOTIFY_POPUP=5;

    /**
     * Listener for any interactions from cards.
     */
    private OnContentInteractionListener mListener;

    private ViewRecyclerAdapter viewRecyclerAdapter;
    private  LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;

    private SimpleDateFormat simpleDateFormat;
    private Calendar shownDate=Calendar.getInstance();

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
            long offset = getArguments().getInt(ARG_POSITION);
            shownDate.setTimeInMillis(shownDate.getTimeInMillis()+offset*24*60*60*1000);
        }/*
        dataBlockManager=new DataBlockManager(mOffsetDays=(DLMainActivity.COUNT-1-mPosition));*/
        /*dataBlockManager.readPackage();
        mText=dataBlockManager.getStringData();*/
        simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd", Locale.US);
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
            recyclerView = (RecyclerView) view.findViewById(R.id.content_recycler);
            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            viewRecyclerAdapter = new ViewRecyclerAdapter();
            viewRecyclerAdapter.setAdapterListener(this);
            viewRecyclerAdapter.setDataBlockContainers(DataBlockManager.readNotes(simpleDateFormat.format(shownDate.getTime()),getContext()));
            recyclerView.setAdapter(viewRecyclerAdapter);
            recyclerView.setNestedScrollingEnabled(true);
            /*
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recycler, int newState) {
                    super.onScrollStateChanged(recycler, newState);

                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if(layoutManager.findFirstVisibleItemPosition()==0&&t) {
                        t=false;
                        mListener.onNavigatorCollapse(true);
                    }

                }
            });*/
        }


       /* View view=getView();*/
       /* if(view!=null) {
            if(mText!=null) {
                ((TextView) view.findViewById(R.id.preview_Text)).setText(mText);
                if (signBitMap!=null)
                    ((ImageView)view.findViewById(R.id.imageView_sign)).setImageBitmap(signBitMap);
            }
            else {
                ( view.findViewById(R.id.blankIcon)).setVisibility(View.VISIBLE);
                (view.findViewById(R.id.blankIconText)).setVisibility(View.VISIBLE);
            }
        }*/
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnContentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void IsEmpty() {
        if(getView()!=null)
            (getView().findViewById(R.id.blankIconText)).setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(DataBlockContainer id) {
        mListener.onItemSelected(id);
    }

    /**
     * The interface to Activity
     */
    interface OnContentInteractionListener {
        /**
         * Called when Navigator View needs to be collapsed.
         * @param collapse true when view needs to be collapsed.
         */
        void onNavigatorCollapse(boolean collapse);

        /**
         * Called when an item is selected from the list.
         * @param id row id
         */
        void onItemSelected(DataBlockContainer id);
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
