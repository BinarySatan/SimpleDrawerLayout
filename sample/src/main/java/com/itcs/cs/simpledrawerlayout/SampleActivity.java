package com.itcs.cs.simpledrawerlayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.github.binarysatan.SimpleDrawerLayout;

/**
 * sample
 *
 * @author binarysatan
 *         blog    http://blog.csdn.net/xuezhe__
 */
public class SampleActivity extends AppCompatActivity {

    private SimpleDrawerLayout mDrawerLayout;
    private Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        mToolBar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(mToolBar);
        mDrawerLayout = (SimpleDrawerLayout) findViewById(R.id.drawerLayout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.open:
                mDrawerLayout.openDrawer();
                break;
            case R.id.close:
                mDrawerLayout.closeDrawer();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
