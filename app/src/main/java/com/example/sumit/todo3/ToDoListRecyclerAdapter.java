package com.example.sumit.todo3;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Sumit on 07/13/2017.
 */

public class ToDoListRecyclerAdapter extends RecyclerView.Adapter<ToDoListRecyclerAdapter.EntryViewHolder> {

    private Context mContext;
    private ArrayList<Entry> mEntryList;
    private EntryClickListener mListener;
    private EntryLongClickListener mLongClickListener;

    public interface EntryClickListener {
        void onItemClick(View view, int position);
    }

    public interface  EntryLongClickListener{

        void onItemLongCLick(View view,int position);
        void prepareSelections(View view,int position);
    }

    public void setOnLongClickListener(EntryLongClickListener mLongClickListener)
    {
        this.mLongClickListener = mLongClickListener;
    }



    public ToDoListRecyclerAdapter(Context mContext, ArrayList<Entry> mEntryList, EntryClickListener mListener) {
        this.mContext = mContext;
        this.mEntryList = mEntryList;
        this.mListener = mListener;
    }

    @Override
    public EntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.list_item_view, parent, false);
        return new EntryViewHolder(itemView, mListener,mLongClickListener);
    }

    @Override
    public void onBindViewHolder(ToDoListRecyclerAdapter.EntryViewHolder holder, int position) {
        Entry entry = mEntryList.get(position);
        holder.titleTextView.setText(entry.title);
        holder.dateTextView.setText(entry.date);
        holder.categoryTextView.setText(entry.category);
        holder.timeTextView.setText(entry.time);

        if(entry.category.equals(CategoryConstants.CATEGORY_BIRTHDAY))
        {
            holder.imageView.setBackgroundResource(R.drawable.happy_birthday_wallpaper);
        }
        else if(entry.category.equals(CategoryConstants.CATEGORY_MEETING))
        {
            holder.imageView.setBackgroundResource(R.drawable.meeting_wallpaper);
        }
        else if(entry.category.equals(CategoryConstants.CATEGORY_STUDY))
        {
            holder.imageView.setBackgroundResource(R.drawable.study_wallpaper);
        }

        else if(entry.category.equals(CategoryConstants.CATEGORY_SHOPPING))
        {
            holder.imageView.setBackgroundResource(R.drawable.shopping_wallpaper);
        }

        else if(entry.category.equals(CategoryConstants.CATEGORY_GYM))
        {
            holder.imageView.setBackgroundResource(R.drawable.gym_wallpaper);
        }

        else if(entry.category.equals(CategoryConstants.CATEGORY_OTHERS))
        {
            holder.imageView.setBackgroundResource(R.drawable.others_wallpaper);
        }

        if(!MainActivity.is_action_mode)
            holder.checkBox.setVisibility(View.GONE);
        else {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {
        return mEntryList.size();
    }


    public static class EntryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView titleTextView;
        TextView dateTextView;
        TextView categoryTextView;
        //LinearLayout linearLayout;
        ImageView imageView;
        EntryClickListener mEntryClickListener;
        EntryLongClickListener mEntryLongClickListener;
        CheckBox checkBox;
        TextView timeTextView;

        public EntryViewHolder(View itemView, EntryClickListener listener, final EntryLongClickListener mEntryLongClickListener) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mEntryClickListener = listener;
            this.mEntryLongClickListener = mEntryLongClickListener;
            //linearLayout = itemView.findViewById(R.id.list_item_linear_layout);
            imageView = itemView.findViewById(R.id.list_item_image_view);
            timeTextView = itemView.findViewById(R.id.todolisttimetextview);
            titleTextView = itemView.findViewById(R.id.todolisttitletextview);
            dateTextView = itemView.findViewById(R.id.todolistdatetextview);
            categoryTextView = itemView.findViewById(R.id.todolistcategorytextview);
            checkBox = itemView.findViewById(R.id.checkbox);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int id = view.getId();
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION)
                    {  if(id == R.id.checkbox) {
                        mEntryLongClickListener.prepareSelections(view, position);
                    }
                    }
                }
            });
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                if (id == R.id.entry_layout) {
                    mEntryClickListener.onItemClick(view, position);
                }
            }
        }

        @Override
        public boolean onLongClick(View view) {
            int id= view.getId();
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION)
            {
                if(id == R.id.entry_layout)
                {
                    mEntryLongClickListener.onItemLongCLick(view,position);
                }
            }
            return true;
        }
    }

}
