package com.example.wallit_app;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.levitnudi.legacytableview.LegacyTableView;

public class StatsActivity extends ToolBarActivity {

    private Resources res = getResources();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

    /*
    If deposit row: change font color to #6699CC
    If withdraw row: change font color to #676767
    Date, (Wit. / Dep.), Amount, Balance.


    int id = res.getIdentifier("titleText", "id", getContext().getPackageName());

    TODO: Add page system, so user can see entire history of movements. (9 entries per page)
     */

}
