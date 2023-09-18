package de.tadris.fitness.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.tadris.fitness.R;
import de.tadris.fitness.data.WorkoutType;
import de.tadris.fitness.ui.fragment.DiscoveryBirdFragment;
import de.tadris.fitness.ui.fragment.FitnessTabFragment;
import de.tadris.fitness.ui.record.RecordWorkoutActivity;
import de.tadris.fitness.ui.settings.FitoTrackSettingsActivity;
import de.tadris.fitness.ui.statistics.StatisticsActivity;
import de.tadris.fitness.ui.workout.EnterWorkoutActivity;
import de.tadris.fitness.util.PermissionUtils;

public class MainActivity extends FitoTrackActivity{
    private static final int FILE_IMPORT_SELECT_CODE = 21;
    private static final int FOLDER_IMPORT_SELECT_CODE = 23;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FloatingActionButton birdDisBtn;
    private FloatingActionButton birdOfficeBtn;
    private FloatingActionButton birdSecretMissionBtn;
    private FloatingActionMenu floatButton;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        setupViewPager();
        tabLayout.setupWithViewPager(viewPager);
        floatButton = findViewById(R.id.workoutListMenu);

        // Set custom view for tabs
     /*   setupTabIcon(0, "Sports");
        setupTabIcon(1, "Bird Discovery");*/

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int tabCount = tabLayout.getTabCount();
        for (int i = 0; i < tabCount; i++) {
            TextView textView = new TextView(this);
            if (i ==0) {
                textView.setText("Fitness");
            } else {
                textView.setText("Bird Discovery");
            }
            textView.setWidth(screenWidth / tabCount);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(getResources().getColor(R.color.textBlack));
            textView.setTextSize(15);
            tabLayout.getTabAt(i).setCustomView(textView);
        }
        initButonClick();
    }

    private void initButonClick() {

        birdDisBtn = findViewById(R.id.bird_discovery);
        birdOfficeBtn = findViewById(R.id.bird_office);
        birdSecretMissionBtn = findViewById(R.id.secret_mission);

        Intent intent = new Intent();

        birdDisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WorkoutType type = new WorkoutType();
                type.id = "running";
                type.icon = "running";
                type.recordingType = "gps";
                type.title = "Run";
                Intent intent = new Intent(MainActivity.this, type.getRecordingType().recorderActivityClass);
                // intent.action = RecordWorkoutActivity.LAUNCH_ACTION;
                intent.putExtra(RecordWorkoutActivity.WORKOUT_TYPE_EXTRA, type);
                startActivity(intent);
                floatButton.close(true);
            }
        });

        birdOfficeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EnterWorkoutActivity.class);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                        floatButton.close(true);
                    }
                },300);
            }
        });

        birdSecretMissionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImportDialog();
                floatButton.close(true);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_workout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.actionOpenSettings) {
            startActivity(new Intent(this, FitoTrackSettingsActivity.class));
            return true;
        }
        if (id == R.id.actionOpenStatisticss) {
            //startActivity(Intent(this, AggregatedWorkoutStatisticsActivity::class.java))
            startActivity(new Intent(this, StatisticsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showImportDialog() {
        if (!hasPermission()) {
            requestPermissions();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.importWorkout)
                .setMessage(R.string.importWorkoutMultipleQuestion)
                .setPositiveButton(R.string.actionImport, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        importWorkout();
                    }
                })
                .setNeutralButton(R.string.actionImportMultiple, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showMassImportGpx();
                    }
                })
                .show();
    }

    private void showMassImportGpx() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.importMultipleGpxFiles);
        builder.setMessage(R.string.importMultipleMessageSelectFolder);
        builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openMassImportFolderSelector();
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    private void openMassImportFolderSelector(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, FOLDER_IMPORT_SELECT_CODE);
    }

    private void importWorkout() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, getString(R.string.importWorkout)),
                    FILE_IMPORT_SELECT_CODE
            );
        } catch (ActivityNotFoundException ignored) {
        }
    }

    private boolean hasPermission() {
        return PermissionUtils.checkStoragePermissions(this, false);
    }

    private void requestPermissions() {
        if (!hasPermission()) {
            if (!hasPermission()) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        10
                );
            }
        }
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new FitnessTabFragment(), "Fitness");
        adapter.addFragment(new DiscoveryBirdFragment(), "Bird Discovery");

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Return null since we're using a custom tab view
            return null;
        }

    }
}
