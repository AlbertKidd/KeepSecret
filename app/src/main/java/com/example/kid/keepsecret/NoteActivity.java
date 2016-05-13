package com.example.kid.keepsecret;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.kid.keepsecret.DB.NoteDB;
import com.example.kid.keepsecret.model.Note;

/**
 * Created by niuwa on 2016/4/29.
 */
public class NoteActivity extends BaseActivity {

    public static final String UUID_TAG = "UUID_TAG";

    public static final String YELLOW = "#ffe7ad48";
    public static final String BLUE = "#ff4a95ce";
    public static final String GREEN = "#ff3db58b";
    public static final String RED = "#ffe0664b";

    private EditText mEditText;
    private RadioGroup mRadioGroup;

    private NoteDB mNoteDB;
    private Note mNote;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addActivit(this);
        setContentView(R.layout.activity_note);

        mNoteDB = NoteDB.getInstance(this);
        mEditText = (EditText)findViewById(R.id.edit_text);
        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);

        Intent i = getIntent();
        String uuid = i.getStringExtra(UUID_TAG);
        if (uuid != null){
            mNote = mNoteDB.loadNoteById(uuid);
            mEditText.setText(mNote.getContent());
            setTagChecked();
        }else {
            mNote = new Note();

            mEditText.setFocusable(true);
            mEditText.setFocusableInTouchMode(true);
            mEditText.requestFocus();
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mEditText, InputMethodManager.SHOW_FORCED);

        }

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNote.setContent(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton)findViewById(checkedId);
                String tagColor = radioButton.getText().toString();
                mNote.setTagColor(tagColor);
            }
        });
    }

    private void setTagChecked(){
        if (mNote.getTagColor() != null){
            switch (mNote.getTagColor()){
                case YELLOW:
                    mRadioGroup.check(R.id.radio_yellow_tag);
                    break;
                case BLUE:
                    mRadioGroup.check(R.id.radio_blue_tag);
                    break;
                case GREEN:
                    mRadioGroup.check(R.id.radio_green_tag);
                    break;
                case RED:
                    mRadioGroup.check(R.id.radio_red_tag);
                    break;
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();

    }

    @Override
    public void onPause(){
        super.onPause();
        if (mNote.getContent() != null){
            if (mNoteDB.loadNoteById(mNote.getId()) != null){
                mNoteDB.updateNote(mNote.getId(), mNote.getContent(), mNote.getTagColor());
            }else {
                mNoteDB.saveNote(mNote);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.note_delete:
                String uuid = mNote.getId();
                if (uuid != null){
                    mNoteDB.deleteNoteById(uuid);
                    mNote.setContent(null);
                    finish();
                }
                return true;
            case R.id.note_other:
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        finishAll();
    }
}
