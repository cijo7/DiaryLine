package com.solidskulls.diaryline;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.solidskulls.diaryline.adapters.ListRecyclerAdapter;
import com.solidskulls.diaryline.data.AppConstants;
import com.solidskulls.diaryline.data.DataBlockContainer;
import com.solidskulls.diaryline.data.DataBlockManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class EditorList extends AppCompatActivity {
    public static final String CONTENT= "id";

    private static final int ADD = 89;
    private static final int UPDATE = 136;
    private RecyclerView recyclerView;
    private ListRecyclerAdapter listRecyclerAdapter;

    private EditText title,add;
    private DataBlockContainer container;
    private int MODE;
    private Calendar reminderDate;
    private SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle e=getIntent().getExtras();
        if(e!=null){
            MODE=UPDATE;
            container=e.getParcelable(CONTENT);
        }else {
            MODE=ADD;
        }

        setContentView(R.layout.activity_editor_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        title=(EditText)findViewById(R.id.editorList_title);
        add=(EditText)findViewById(R.id.editorList_addText);
        add.addTextChangedListener(new ListParser());
        ImageButton buttonAdd=(ImageButton)findViewById(R.id.editorList_addButton);
        if (buttonAdd != null) {
            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   pushContents();
                }
            });
        }
        reminderDate=Calendar.getInstance();
        final ImageButton reminder=(ImageButton)findViewById(R.id.editorList_reminder);
        final TextView reminderText=(TextView) findViewById(R.id.editorList_reminder_text);
        if(MODE==UPDATE&&container.getReminder()!=null){
            try {
                reminderDate.setTime(format.parse(container.getReminder()));
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, HH:mm a", Locale.getDefault());
                if (reminderText != null) {
                    reminderText.setText(simpleDateFormat.format(reminderDate.getTime()));
                }
            } catch (ParseException e) {
                Timber.d(e, "Unable to parse reminder.");
            }
        }
        if (reminder != null) {
            reminder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final DatePickerDialog date;
                    final TimePickerDialog time;

                    time=new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            reminderDate.set(Calendar.HOUR_OF_DAY,hourOfDay);
                            reminderDate.set(Calendar.MINUTE,minute);
                            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MMM dd, HH:mm a",Locale.getDefault());
                            if (reminderText != null) {
                                reminderText.setText(simpleDateFormat.format(reminderDate.getTime()));
                            }
                        }
                    },reminderDate.get(Calendar.HOUR_OF_DAY),reminderDate.get(Calendar.MINUTE),false);
                    date=new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            reminderDate.set(Calendar.YEAR,year);
                            reminderDate.set(Calendar.MONTH,monthOfYear);
                            reminderDate.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                            time.show();
                        }
                    }, reminderDate.get(Calendar.YEAR), reminderDate.get(Calendar.MONTH), reminderDate.get(Calendar.DAY_OF_MONTH));
                    date.show();
                }
            });
        }


        recyclerView=(RecyclerView)findViewById(R.id.editorList_recycler);
        // FIXME: 7/2/16 The contents must be retained even when orientation changes.
        if(MODE==UPDATE) {
            listRecyclerAdapter = new ListRecyclerAdapter(new DataParser().textToList(container.getText()));
            title.setText(container.getTitle());
        }
        else
            listRecyclerAdapter=new ListRecyclerAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(listRecyclerAdapter);
        //Hide if we don't have any contents.
        if(listRecyclerAdapter.getItemCount()==0)
            recyclerView.setVisibility(View.GONE);
        /*
         * Implement the recycler call backs.
         */
        listRecyclerAdapter.setInteractionListener(new ListRecyclerAdapter.RecyclerInteraction() {
            @Override
            public void showUndoNotification(final int index) {
                RelativeLayout rl=(RelativeLayout) findViewById(R.id.editorList_layout);
                if (rl != null) {
                    Snackbar.make(rl,getString(R.string.marked), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listRecyclerAdapter.undoRemoval(index);
                        }
                    }).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_done)
            save();
        return super.onOptionsItemSelected(item);
    }

    public Context getContext() {
        return this;
    }

    /**
     * Pushes the current content in add text to the array and truncates add text
     */
    private void pushContents(){
        if(add.getText().toString().length()>0) {//Push to recycler view.
            listRecyclerAdapter.addTextView(add.getText().toString());
            add.setText("");
            if(recyclerView.getVisibility()==View.GONE)
                recyclerView.setVisibility(View.VISIBLE);
            recyclerView.scrollToPosition(listRecyclerAdapter.getItemCount()-1);
        }
    }

    /**
     * Save the List and quit.
     */
    private void save(){
        DataParser parser=new DataParser();
        if(MODE==ADD) {
            container = new DataBlockContainer(title.getText().toString(),
                                                parser.listToText(listRecyclerAdapter.getData()),
                                                format.format(new Date().getTime()),
                                                format.format(reminderDate.getTime()),
                                                AppConstants.LISTS);
            if(container.getTitle().isEmpty())
                container.setTitle(getString(R.string.list_default_title));
            DataBlockManager.addNotes(container,this);
        }else if(MODE==UPDATE){
            container.setText(parser.listToText(listRecyclerAdapter.getData()));
            container.setTitle(title.getText().toString());
            container.setReminder(format.format(reminderDate.getTime()));
            DataBlockManager.updateNotes(container,this);
        }
        Toast.makeText(this, getString(R.string.save), Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp(){
        Toast.makeText(this,getString(R.string.discard),Toast.LENGTH_SHORT).show();
        return super.onSupportNavigateUp();
    }

    private class ListParser implements TextWatcher {
        boolean editable=true;
        int start=-1;
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if(s.length()>start)
                if(s.charAt(start)=='\n')
                    this.start=start;
        }

        @Override
        public void afterTextChanged(Editable s) {

            if(start>-1&&editable) {
                editable=false;
                s.replace(start, s.length(), "");
                pushContents();
                start=-1;
                editable=true;
            }
        }
    }
}
