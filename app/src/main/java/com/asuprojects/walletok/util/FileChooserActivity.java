package com.asuprojects.walletok.util;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.asuprojects.walletok.R;

import net.rdrei.android.dirchooser.DirectoryChooserConfig;
import net.rdrei.android.dirchooser.DirectoryChooserFragment;

public class FileChooserActivity extends AppCompatActivity
            implements DirectoryChooserFragment.OnFragmentInteractionListener{

    public static final String EXTRA_CONFIG = "config";
    public static final String RESULT_SELECTED_FILE = "selected_file";
    public static final int RESULT_CODE_FILE_SELECTED = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupActionBar();
        setContentView(R.layout.directory_chooser_activity);

        final DirectoryChooserConfig config = getIntent().getParcelableExtra(EXTRA_CONFIG);

        if (config == null) {
            throw new IllegalArgumentException(
                    "You must provide EXTRA_CONFIG when starting the DirectoryChooserActivity.");
        }

        if (savedInstanceState == null) {
            final FragmentManager fragmentManager = getFragmentManager();
            final DirectoryChooserFragment fragment = DirectoryChooserFragment.newInstance(config);
            fragmentManager.beginTransaction()
                    .add(R.id.main, fragment)
                    .commit();
        }
    }

    /* package */void setupActionBar() {
        // there might not be an ActionBar, for example when started in Theme.Holo.Dialog.NoActionBar theme
        @SuppressLint("AppCompatMethod") final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSelectDirectory(@NonNull String path) {
        final Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/octet-stream");
        intent.putExtra(RESULT_SELECTED_FILE, path);
        setResult(RESULT_CODE_FILE_SELECTED, intent);

        Log.i("FILE", "onSelectDirectory: passando em FileChooser");
        finish();
    }

    @Override
    public void onCancelChooser() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
