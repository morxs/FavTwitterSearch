package com.rudsu.favtwittersearch;

import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;


public class FavTwitterSeachActivity extends Activity {
	private SharedPreferences savedSearches; // user's fav searches
	private TableLayout queryTableLayout; // show search button
	private EditText queryEditText; // where user enter queries
	private EditText tagEditText; // where user enter a query tag
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // get the SharedPreferences that contains the user's saved searches
        savedSearches = getSharedPreferences("searches", MODE_PRIVATE);
        
        // get a reference to the queryTableLayout
        queryTableLayout = (TableLayout) findViewById(R.id.queryTableLayout);
        
        // get references to the two EditTexts and save button
        queryEditText = (EditText) findViewById(R.id.queryEditText);
        tagEditText = (EditText) findViewById(R.id.tagEditText);
        
        // register listeners for the save and clear tag buttons
        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(saveButtonListener);
        Button clearTagsButton = (Button) findViewById(R.id.clearTagsButton);
        clearTagsButton.setOnClickListener(clearTagsButtonListener);
        
        refreshButtons(null); // add previously saved searches to gui
    }
    
    // recreate search tag and edit buttons for all saved searches;
    // pass null to create all the tag and edit buttons
    private void refreshButtons(String newTag) {
    	// store saved tags in the tags array
    	String[] tags = savedSearches.getAll().keySet().toArray(new String[0]);
    	Arrays.sort(tags, String.CASE_INSENSITIVE_ORDER);
    	
    	if (newTag != null) {
    		makeTagGUI(newTag, Arrays.binarySearch(tags, newTag));
    	}
    	else {
    		// display all saved tags
    		for (int index = 0; index < tags.length; index++) {
    			makeTagGUI(tags[index], index);
    		}
    	}
    }
    
    // add new search to the save file, then refresh all buttons
    private void makeTag(String query, String tag) {
    	// originalQuery will be null if we're modifying an existing search
    	String originalQuery = savedSearches.getString(tag, null);
    	
    	// get a SharedPreferences.Editor to store new tag/query pair
    	SharedPreferences.Editor preferencesEditor = savedSearches.edit();
    	preferencesEditor.putString(tag, query); // store current search
    	preferencesEditor.apply();
    	
    	// if this is new query, add its gui
    	if (originalQuery == null)
    		refreshButtons(tag); // add a new button for this tag
    }
    
    private void makeTagGUI(String tag, int index) {
    	// get a reference to the LayoutInflater service
    	LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	
    	// inflate new_tag_view.xml to create new tag and edit buttons
    	View newTagView = inflater.inflate(R.layout.new_tag_view, null);
    	
    	// get newTagButton, set its text and register its listener
    	Button newTagButton = (Button) newTagView.findViewById(R.id.newTagButton);
    	newTagButton.setText(tag);
    	newTagButton.setOnClickListener(queryButtonListener);
    	
    	// get newEditButton and register its listener
    	Button newEditButton = (Button) newTagView.findViewById(R.id.newEditButton);
    	newEditButton.setOnClickListener(editButtonListener);
    	
    	// add new tag and edit buttons to queryTableLayout
    	queryTableLayout.addView(newTagView, index);
    }
    
    private void clearButtons() {
    	// remove all saved search buttons
    	queryTableLayout.removeAllViews();
    }
}