package com.example.sumit.todo3;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
//import android.support.v7.app.AlertController;
import android.support.v4.util.Pair;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity implements ToDoListRecyclerAdapter.EntryLongClickListener {

    static boolean is_action_mode = false;
    TextView in_action_mode;
    Toolbar toolbar;
    ArrayList<Entry> selection_List;
    int counter = 0;
     NestedScrollView nestedScrollView;
    RecyclerView mRecyclerView;
    ArrayList<Entry> entryArrayList;
    ToDoListRecyclerAdapter toDoListRecyclerAdapter;
    public final static int NEW_ENTRY = 1;
    public final static int CHANGE_EXISTING_ENTRY = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        entryArrayList = new ArrayList<>();
        selection_List = new ArrayList<>();
        nestedScrollView = (NestedScrollView) findViewById(R.id.content_nested_scroll_view);

       in_action_mode = (TextView) findViewById(R.id.in_action_mode_text_view);
        in_action_mode.setVisibility(View.GONE);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        toDoListRecyclerAdapter = new ToDoListRecyclerAdapter(this, entryArrayList, new ToDoListRecyclerAdapter.EntryClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Intent i = new Intent(MainActivity.this,ToDoAppEditPageActivity.class);
                i.putExtra(IntentConstants.ENTRY_TITLE,entryArrayList.get(position).title);
                i.putExtra(IntentConstants.ENTRY_DESCRIPTION,entryArrayList.get(position).Description);
                i.putExtra(IntentConstants.REQUEST_CODE,CHANGE_EXISTING_ENTRY);
                i.putExtra(IntentConstants.ENTRY_ID, entryArrayList.get(position).id);
                i.putExtra(IntentConstants.ENTRY_DATE, entryArrayList.get(position).date);
                i.putExtra(IntentConstants.ENTRY_CATEGORY,entryArrayList.get(position).category);
                i.putExtra(IntentConstants.ENTRY_TIME,entryArrayList.get(position).time);
                startActivityForResult(i,CHANGE_EXISTING_ENTRY);
            }
        });


        toDoListRecyclerAdapter.setOnLongClickListener(this);
        mRecyclerView.setAdapter(toDoListRecyclerAdapter);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL|DividerItemDecoration.HORIZONTAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN|ItemTouchHelper.UP,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();
                Collections.swap(entryArrayList,from,to);
                toDoListRecyclerAdapter.notifyItemMoved(from,to);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                int id = entryArrayList.get(position).id;
                final Entry temp = new Entry();
                temp.time = entryArrayList.get(position).time;
                temp.category =  entryArrayList.get(position).category;
                temp.title = entryArrayList.get(position).title;
                temp.date = entryArrayList.get(position).date;
                temp.Description = entryArrayList.get(position).Description;
                temp.id = entryArrayList.get(position).id;
                entryArrayList.remove(position);
                toDoListRecyclerAdapter.notifyItemRemoved(position);

                ToDoAppOpenHelper toDoAppOpenHelper = ToDoAppOpenHelper.getToDoAppOpenHelper(MainActivity.this);
                final SQLiteDatabase database = toDoAppOpenHelper.getWritableDatabase();
                database.delete(ToDoAppOpenHelper.TABLE_NAME,ToDoAppOpenHelper.ENTRY_ID + " = " + id,null);

                Snackbar.make(findViewById(R.id.coordinatelayout),"Entry Deleted",Snackbar.LENGTH_SHORT).setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ContentValues cv = new ContentValues();
                        cv.put(ToDoAppOpenHelper.ENTRY_TITLE,temp.title);
                        cv.put(ToDoAppOpenHelper.ENTRY_DESCRIPTION,temp.Description);
                        cv.put(ToDoAppOpenHelper.ENTRY_CATEGORY,temp.category);
                        cv.put(ToDoAppOpenHelper.ENTRY_DATE,temp.date);
                        cv.put(ToDoAppOpenHelper.ENTRY_TIME,temp.time);
                        database.insert(ToDoAppOpenHelper.TABLE_NAME,null,cv);
                        entryArrayList.add(temp);
                        toDoListRecyclerAdapter.notifyDataSetChanged();
                    }
                }).show();
            }

        });
        itemTouchHelper.attachToRecyclerView(mRecyclerView);



         toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,ChooseCategoryActivity.class);
                startActivityForResult(i,NEW_ENTRY);
            }
        });
        updateList(null);
    }



    private void updateList(String Category)
    {
        ToDoAppOpenHelper toDoAppOpenHelper = ToDoAppOpenHelper.getToDoAppOpenHelper(this);
        entryArrayList.clear();
        SQLiteDatabase database = toDoAppOpenHelper.getReadableDatabase();
        Cursor cursor;
   if(Category == null)
   { cursor = database.query(toDoAppOpenHelper.TABLE_NAME,null,null,null,null,null,null);}
   else
   {
     cursor = database.query(toDoAppOpenHelper.TABLE_NAME,null, toDoAppOpenHelper.ENTRY_CATEGORY + " = ?",
             new String[] {Category},null,null,null);
   }

        while(cursor.moveToNext())
        {
            String Description = cursor.getString(cursor.getColumnIndex(toDoAppOpenHelper.ENTRY_DESCRIPTION));
            String Title = cursor.getString(cursor.getColumnIndex(toDoAppOpenHelper.ENTRY_TITLE));
            int id = cursor.getInt(cursor.getColumnIndex(toDoAppOpenHelper.ENTRY_ID));
            String Date = cursor.getString(cursor.getColumnIndex(toDoAppOpenHelper.ENTRY_DATE));
            String category = cursor.getString(cursor.getColumnIndex(toDoAppOpenHelper.ENTRY_CATEGORY));
            String Time = cursor.getString(cursor.getColumnIndex(toDoAppOpenHelper.ENTRY_TIME));
            Entry e = new Entry();
            e.Description = Description;
            e.title = Title;
            e.date = Date;
            e.id = id;
            e.category = category;
            e.time = Time;
            entryArrayList.add(e);
        }

       toDoListRecyclerAdapter.notifyDataSetChanged();
    }

    private void sortList(int menu_id)
    {
        ToDoAppOpenHelper toDoAppOpenHelper = ToDoAppOpenHelper.getToDoAppOpenHelper(this);
        entryArrayList.clear();
        SQLiteDatabase database = toDoAppOpenHelper.getReadableDatabase();
        Cursor cursor;
        if(menu_id == R.id.CategoriesSortBy)
       {
          cursor = database.query(ToDoAppOpenHelper.TABLE_NAME,null,null,null,null,null,ToDoAppOpenHelper.ENTRY_CATEGORY + " ASC ");
      }
      else if(menu_id == R.id.sortbytitle)
        {
            cursor = database.query(ToDoAppOpenHelper.TABLE_NAME,null,null,null,null,null,ToDoAppOpenHelper.ENTRY_TITLE + " ASC ");

        }
      else {
            cursor = database.query(ToDoAppOpenHelper.TABLE_NAME,null,null,null,null,null,ToDoAppOpenHelper.ENTRY_DATE + " ASC ");
        }

        while(cursor.moveToNext())
        {
            String Description = cursor.getString(cursor.getColumnIndex(toDoAppOpenHelper.ENTRY_DESCRIPTION));
            String Title = cursor.getString(cursor.getColumnIndex(toDoAppOpenHelper.ENTRY_TITLE));
            int id = cursor.getInt(cursor.getColumnIndex(toDoAppOpenHelper.ENTRY_ID));
            String Date = cursor.getString(cursor.getColumnIndex(toDoAppOpenHelper.ENTRY_DATE));
            String category = cursor.getString(cursor.getColumnIndex(toDoAppOpenHelper.ENTRY_CATEGORY));
            String Time = cursor.getString(cursor.getColumnIndex(toDoAppOpenHelper.ENTRY_TIME));
            Entry e = new Entry();
            e.Description = Description;
            e.title = Title;
            e.date = Date;
            e.id = id;
            e.time = Time;
            e.category = category;
            entryArrayList.add(e);
        }

        toDoListRecyclerAdapter.notifyDataSetChanged();


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == NEW_ENTRY || requestCode == CHANGE_EXISTING_ENTRY)
        {
            if(resultCode == RESULT_OK)
            {
                updateList(null);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);



        final MenuItem menuItem = menu.findItem(R.id.SearchView);
        final  SearchView searchView = (SearchView) menuItem.getActionView();
        final ArrayList<Entry> Temp = new ArrayList<>();
        Temp.addAll(entryArrayList);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
               return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                newText = newText.toLowerCase();

                ArrayList<Entry> newList = new ArrayList<Entry>();
                for(Entry e : Temp)
                {
                    String Title = e.title.toLowerCase();
                    String category = e.category.toLowerCase();
                    if(Title.contains(newText)) {
                        newList.add(e);
                    }
                    else if(category.contains(newText)) {
                        newList.add(e);
                    }
                }
                entryArrayList.clear();
                entryArrayList.addAll(newList);
               toDoListRecyclerAdapter.notifyDataSetChanged();
                return  true;
            }
        });


        return true;
    }




    public void updateAdapter(ArrayList<Entry> list)
    {
        for(Entry e: list)
        {   int id = e.id;
            entryArrayList.remove(e);
            ToDoAppOpenHelper toDoAppOpenHelper = ToDoAppOpenHelper.getToDoAppOpenHelper(MainActivity.this);
            final SQLiteDatabase database = toDoAppOpenHelper.getWritableDatabase();
            database.delete(ToDoAppOpenHelper.TABLE_NAME,ToDoAppOpenHelper.ENTRY_ID + " = " + id,null);
        }
        toDoListRecyclerAdapter.notifyDataSetChanged();
        Snackbar.make(findViewById(R.id.coordinatelayout)," Entries Deleted",Snackbar.LENGTH_SHORT).show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


      //SETTING DIFFERENT BACKGROUND FOR DIFFERENT CATEGORIES
        if(id == R.id.meetingtodos)
        {
              nestedScrollView.setBackgroundResource(R.drawable.meeting_wallpaper);
            updateList(CategoryConstants.CATEGORY_MEETING);

        }
        if (id == R.id.birthdaytodos)
        {  nestedScrollView.setBackgroundResource(R.drawable.happy_birthday_wallpaper);
            updateList(CategoryConstants.CATEGORY_BIRTHDAY);
        }

       else if (id == R.id.shoppingtodos)
        {  nestedScrollView.setBackgroundResource(R.drawable.shopping_wallpaper);
            updateList(CategoryConstants.CATEGORY_SHOPPING);
        }
        else if (id == R.id.studytodos)
        {  nestedScrollView.setBackgroundResource(R.drawable.study_wallpaper);
            updateList(CategoryConstants.CATEGORY_STUDY);
        }
        else if (id == R.id.gymtodos)
        {   nestedScrollView.setBackgroundResource(R.drawable.gym_wallpaper);
            updateList(CategoryConstants.CATEGORY_GYM);
        }
       else if (id == R.id.otherstodos)
        {   nestedScrollView.setBackgroundResource(R.drawable.others_wallpaper);
            updateList(CategoryConstants.CATEGORY_OTHERS );
        }else if(id == R.id.alltodos)
        {   nestedScrollView.setBackgroundResource(R.drawable.radialgradientback);
            updateList(null);
        }



       //IN ACTION MODE MENU ITEMS
        if(id == R.id.delete)
        {


            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("DELETE");
            builder.setMessage("Do You Want To Delete The Entries??");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    updateAdapter(selection_List);
                    clear_Action_Mode();

                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }

        else if(id == android.R.id.home)
        {
            clear_Action_Mode();
            toDoListRecyclerAdapter.notifyDataSetChanged();
        }


       //DELETE ALL TODOS
        else if(id == R.id.delete_all_to_dos)
        {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("DELETE");
            builder.setMessage("Do You Want To Delete The Entry??");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    final ArrayList<Entry> TempList = new ArrayList<Entry>();
                    for(Entry e: entryArrayList)
                    {
                        TempList.add(e);
                    }

                    ToDoAppOpenHelper toDoAppOpenHelper = ToDoAppOpenHelper.getToDoAppOpenHelper(MainActivity.this);

                    final SQLiteDatabase database = toDoAppOpenHelper.getWritableDatabase();

                    database.delete(ToDoAppOpenHelper.TABLE_NAME,null,null);
                    updateList(null);

                    Snackbar.make(findViewById(R.id.coordinatelayout),"Entry Deleted",Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            for(Entry e: TempList)
                            {
                                entryArrayList.add(e);
                            }
                            for(int i = 0;i < entryArrayList.size(); i++){
                            ContentValues cv = new ContentValues();
                            cv.put(ToDoAppOpenHelper.ENTRY_TITLE,entryArrayList.get(i).title);
                            cv.put(ToDoAppOpenHelper.ENTRY_DESCRIPTION,entryArrayList.get(i).Description);
                            cv.put(ToDoAppOpenHelper.ENTRY_CATEGORY,entryArrayList.get(i).category);
                            cv.put(ToDoAppOpenHelper.ENTRY_DATE,entryArrayList.get(i).date);
                            cv.put(ToDoAppOpenHelper.ENTRY_TIME,entryArrayList.get(i).time);
                            database.insert(ToDoAppOpenHelper.TABLE_NAME,null,cv);
                            toDoListRecyclerAdapter.notifyDataSetChanged();
                            }
                        }
                    }).show();
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }

        //SORT TO DO LIST
        else if(id == R.id.sortbydate)
        {
              sortList(id);
        }
        else if(id == R.id.CategoriesSortBy)
        {
            sortList(id);
        }
        else if(id == R.id.sortbytitle)
        {
            sortList(id);
        }
        return super.onOptionsItemSelected(item);
    }

    public void clear_Action_Mode()
    {
        is_action_mode = false;
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        in_action_mode.setVisibility(View.GONE);
        selection_List.clear();
        counter = 0;
    }


    @Override
    public void onBackPressed() {

        if (is_action_mode) {
            clear_Action_Mode();
            toDoListRecyclerAdapter.notifyDataSetChanged();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Do You Wish To exit The app");
            builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
    @Override
    public void onItemLongCLick(View view, int position) {
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_action_mode);
        in_action_mode.setVisibility(View.VISIBLE);
        is_action_mode = true;
        toDoListRecyclerAdapter.notifyDataSetChanged();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void prepareSelections(View view, int position) {

        if(((CheckBox)view).isChecked())
        {
           selection_List.add(entryArrayList.get(position));
            counter += 1;
            updateCounter();
        }
        else
        {
            selection_List.remove(entryArrayList.get(position));
            counter -= 1;
            updateCounter();
        }

    }


    public void updateCounter()
    {
        if(counter == 0)
        in_action_mode.setText("0 item selected");
        else
        in_action_mode.setText(counter + " item selected");
    }




}
