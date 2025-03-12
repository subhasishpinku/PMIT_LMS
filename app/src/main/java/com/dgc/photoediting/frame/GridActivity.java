package com.dgc.photoediting.frame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.dgc.photoediting.R;


public class GridActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grid);
		
		Intent i = new Intent(this,SelectedImageActivity.class);
		startActivity(i);
		
		
	}

}
