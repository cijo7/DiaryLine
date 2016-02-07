package com.solidskulls.diaryline.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.solidskulls.diaryline.R;
import com.solidskulls.diaryline.data.AppConstants;
import com.solidskulls.diaryline.data.DataBlockContainer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by cijo-saju on 24/1/16.
 * This class implements The recycler adapter. The entire data is kept here.
 */
public class ViewRecyclerAdapter extends RecyclerView.Adapter<VewHolderGeneric> {
    private AdapterListener adapterListener=null;
    private List<DataBlockContainer> dataBlockContainers=null;
    private SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
    private SimpleDateFormat displayDate=new SimpleDateFormat("dd MMM yyyy",Locale.US);
    public ViewRecyclerAdapter(){

    }

    /**
     * Adding/Replacing the data block completely.
     * @param dataBlockContainers the List of DataBlocks
     */
    public void setDataBlockContainers(List<DataBlockContainer> dataBlockContainers){
        this.dataBlockContainers=dataBlockContainers;
        if(this.dataBlockContainers.isEmpty()&&adapterListener!=null)
            adapterListener.IsEmpty();
    }

    @Override
    public VewHolderGeneric onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case 0:
                view= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_lists,parent,false);
                ViewHolderGenericLists lists= new ViewHolderGenericLists(view);
                lists.setListener(new VewHolderGeneric.HolderInteraction() {
                    @Override
                    public void onItemClick(int position) {
                        try{
                            adapterListener.onItemClick(dataBlockContainers.get(position));
                        }catch (NullPointerException e){
                            Timber.d(e,"Null pointer exception on Listener");
                        }
                    }
                });
                return lists;
            case 1:
                view= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_notes,parent,false);
                ViewHolderGenericNotes notes=new ViewHolderGenericNotes(view);
                notes.setListener(new VewHolderGeneric.HolderInteraction() {
                    @Override
                    public void onItemClick(int position) {
                        try{
                            adapterListener.onItemClick(dataBlockContainers.get(position));
                        }catch (NullPointerException e){
                            Timber.d(e,"Null pointer exception on Listener");
                        }
                    }
                });
                return notes;
            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_diary, parent, false);
                ViewHolderGenericDiary diary = new ViewHolderGenericDiary(view);
                diary.setListener(new VewHolderGeneric.HolderInteraction() {
                    @Override
                    public void onItemClick(int position) {
                        try {
                            adapterListener.onItemClick(dataBlockContainers.get(position));
                        } catch (NullPointerException e) {
                            Timber.d(e, "Null pointer exception on Listener");
                        }
                    }
                });
                return diary;
            default:
                view=LayoutInflater.from(parent.getContext()).inflate(R.layout.card_notes,parent,false);
                ViewHolderGenericLists lists1= new ViewHolderGenericLists(view);
                lists1.setListener(new VewHolderGeneric.HolderInteraction() {
                    @Override
                    public void onItemClick(int position) {
                        try{
                            adapterListener.onItemClick(dataBlockContainers.get(position));
                        }catch (NullPointerException e){
                            Timber.d(e,"Null pointer exception on Listener");
                        }
                    }
                });
                return lists1;
        }
    }

    @Override
    public void onBindViewHolder(VewHolderGeneric holder, int position) {
        holder.setTitle(dataBlockContainers.get(position).getTitle());
        holder.setText(dataBlockContainers.get(position).getText());
        try {
            holder.setDate(displayDate.format(format.parse(dataBlockContainers.get(position).getDate())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemViewType(int position) {
        String tag=dataBlockContainers.get(position).getTag();
        switch (tag) {
            case AppConstants.LISTS:
                return 0;
            case AppConstants.NOTES:
                return 1;
            case AppConstants.DIARY:
                return 2;
            default:
                return 1;
        }
    }


    @Override
    public int getItemCount() {
        return dataBlockContainers.size();
    }

    /**
     * Sets the adapters listener.
     * @param listener the interaction listener.
     */
    public void setAdapterListener(AdapterListener listener){
        adapterListener=listener;
    }
    public interface AdapterListener{
        void IsEmpty();
        void onItemClick(DataBlockContainer id);
    }
}
