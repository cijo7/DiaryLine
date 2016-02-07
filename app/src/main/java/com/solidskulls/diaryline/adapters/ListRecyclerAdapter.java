package com.solidskulls.diaryline.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.solidskulls.diaryline.R;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by cijo-saju on 28/1/16.
 *
 */
public class ListRecyclerAdapter extends RecyclerView.Adapter<ListRecyclerAdapter.ListViewHolder> {
    private List<String> arrayList=new ArrayList<>();
    private String undoString;
    private RecyclerInteraction listener;

    public class ListViewHolder extends RecyclerView.ViewHolder{

        private CheckBox text;
        public ListViewHolder(View itemView) {
            super(itemView);
            text=(CheckBox)itemView.findViewById(R.id.editorList_checkbox);
        }
    }

    public ListRecyclerAdapter(){

    }

    public ListRecyclerAdapter(List<String> list){
        arrayList=list;
    }
    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ListViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.editor_list_items,parent,false));
    }

    @Override
    public void onBindViewHolder(final ListViewHolder holder, final int position) {
        holder.text.setText(arrayList.get(position));
        holder.text.setChecked(false);
        holder.text.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    removeTextView(holder.getLayoutPosition());
            }
        });
    }

    public void addTextView(String s){
        arrayList.add(s);
        notifyItemInserted(arrayList.size());
    }

    public void removeTextView(int index){
        undoString=arrayList.get(index);
        arrayList.remove(index);
        notifyItemRemoved(index);

        listener.showUndoNotification(index);
    }

    public void undoRemoval(int index){
        arrayList.add(index,undoString);
        notifyItemInserted(index);
    }

    public List<String> getData(){
        return arrayList;
    }

    @Override
    public int getItemCount() {
        try {
            return arrayList.size();
        }catch (NullPointerException e){
            Timber.d(e,"No array in list");
        }
        return 0;
    }
    public void setInteractionListener(RecyclerInteraction interactionListener){
        listener=interactionListener;
    }

    public interface RecyclerInteraction{
        void showUndoNotification(int index);
    }
}
