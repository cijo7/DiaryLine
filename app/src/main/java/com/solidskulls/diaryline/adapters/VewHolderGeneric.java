package com.solidskulls.diaryline.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by cijo-saju on 24/1/16.
 * A dummy class to be parent and provide common methods.
 */
public abstract class VewHolderGeneric extends RecyclerView.ViewHolder implements View.OnClickListener{
    private HolderInteraction listener;
    public VewHolderGeneric(View itemView) {
        super(itemView);
    }

    /**
     * <p>
     *     Add the date of creation of diary entry ,notes or lists.
     * </p>
     * @param milliSec Millise
     */
    public abstract void setDate(String date);
    /**
     * Set the card title
     * @param title Title of Item
     */
    public abstract void setTitle(String title);

    /**
     * Set the card text
     * @param text The text content of item.
     */
    public abstract void setText(String text);

    @Override
    public void onClick(View v) {
        if(listener!=null)
            listener.onItemClick(getAdapterPosition());
    }

    public void setListener(HolderInteraction listener){
        this.listener=listener;
    }
    public interface HolderInteraction{
        void onItemClick(int position);
    }
}
