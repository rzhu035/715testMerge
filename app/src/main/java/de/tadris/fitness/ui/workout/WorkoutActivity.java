/*
 * Copyright (c) 2021 Jannis Scheibe <jannis@tadris.de>
 *
 * This file is part of FitoTrack
 *
 * FitoTrack is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     FitoTrack is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.tadris.fitness.ui.workout;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.tadris.fitness.Instance;
import de.tadris.fitness.R;
import de.tadris.fitness.data.BaseSample;
import de.tadris.fitness.data.BaseWorkout;
import de.tadris.fitness.data.BaseWorkoutData;
import de.tadris.fitness.data.GpsSample;
import de.tadris.fitness.data.Interval;
import de.tadris.fitness.data.IntervalSet;
import de.tadris.fitness.data.StatsDataTypes;
import de.tadris.fitness.ui.workout.diagram.SampleConverter;
import de.tadris.fitness.util.WorkoutCalculator;
import de.tadris.fitness.util.charts.ChartStyles;
import de.tadris.fitness.util.charts.marker.DisplayValueMarker;
import de.tadris.fitness.util.unit.DistanceUnitUtils;
import de.tadris.fitness.util.unit.EnergyUnitUtils;

public abstract class WorkoutActivity extends InformationActivity {

    public static final int NUMBER_OF_SAMPLES_IN_DIAGRAM = 100;
    public static final String WORKOUT_ID_EXTRA = "de.tadris.fitness.WorkoutActivity.WORKOUT_ID_EXTRA";

    List<BaseSample> samples;
    private BaseWorkout workout;
    private Resources.Theme theme;
    protected final Handler mHandler = new Handler();
    protected IntervalSet usedIntervalSet;
    protected Interval[] intervals;

    protected DistanceUnitUtils distanceUnitUtils;
    protected EnergyUnitUtils energyUnitUtils;
    boolean diagramsInteractive = false;

    void initBeforeContent() {
        distanceUnitUtils = Instance.getInstance(this).distanceUnitUtils;
        energyUnitUtils = Instance.getInstance(this).energyUnitUtils;

        Intent intent = getIntent();
        long workoutId = intent.getLongExtra(WORKOUT_ID_EXTRA, 0);
        if (workoutId != 0) {
            workout = findWorkout(workoutId);
        }
        if (workout == null) {
            Toast.makeText(this, R.string.cannotFindWorkout, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        samples = findSamples(workoutId);
        if (workout.intervalSetUsedId != 0) {
            usedIntervalSet = Instance.getInstance(this).db.intervalDao().getSet(workout.intervalSetUsedId);
            intervals = Instance.getInstance(this).db.intervalDao().getAllIntervalsOfSet(usedIntervalSet.id);
        }
        setTheme(Instance.getInstance(this).themes.getWorkoutTypeTheme(workout.getWorkoutType(this)));
    }

    abstract BaseWorkout findWorkout(long id);

    abstract List<BaseSample> findSamples(long workoutId);

    void initAfterContent() {
        setupActionBar();
        setTitle(workout.getWorkoutType(this).title);

        theme = getTheme();
    }

    protected CombinedChart addDiagram(SampleConverter converter) {
        CombinedChart chart = getDiagram(converter);
        root.addView(chart, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, fullScreenItems ? ViewGroup.LayoutParams.MATCH_PARENT : getMapHeight() / 2));
        chart.getDescription().setEnabled(true); // I don't know where thes two get disabled... Internally for combined charts perhaps? Anyway!
        chart.getLegend().setEnabled(true);
        return chart;
    }

    protected int getMapHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels * 3 / 4;
    }

    private CombinedChart getDiagram(SampleConverter converter) {
        return getDiagram(Collections.singletonList(converter), converter.isIntervalSetVisible());
    }


    private CombinedChart getDiagram(List<SampleConverter> converters, boolean showIntervalSets) {
        CombinedChart chart = new CombinedChart(this);

        chart.setScaleXEnabled(diagramsInteractive);
        chart.setScaleYEnabled(false);
        if (diagramsInteractive) {
            chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, Highlight h) {
                    onChartSelectionChanged(findSample(e));
                }

                @Override
                public void onNothingSelected() {
                    onChartSelectionChanged(null);
                }
            });
        }

        chart.setHighlightPerDragEnabled(diagramsInteractive);
        chart.setHighlightPerTapEnabled(diagramsInteractive);

        updateChart(chart, converters, showIntervalSets);

        for (SampleConverter converter : converters) {
            converter.afterAdd(chart);
        }

        ChartStyles.defaultLineChart(chart, this);
        return chart;
    }

    protected void onChartSelectionChanged(BaseSample sample) {
    }

    abstract List<BaseSample> aggregatedSamples(int aggregationLength, StatsDataTypes.TimeSpan viewFieldSpan);

    protected void updateChart(CombinedChart chart, List<SampleConverter> converters, boolean showIntervalSets) {
        boolean hasMultipleConverters = converters.size() > 1;
        CombinedData combinedData = new CombinedData();

        String xLabel="", yLabel="";
        if (hasMultipleConverters) {
            xLabel = converters.get(0).getXAxisLabel();
        } else {
            xLabel = converters.get(0).getXAxisLabel();
            yLabel = converters.get(0).getYAxisLabel();
        }
        ChartStyles.setXAxisLabel(chart, xLabel, this);
        ChartStyles.setYAxisLabel(chart, yLabel, this);


        // Generate a time span from the view field of the chart
        StatsDataTypes.TimeSpan chartViewField = new StatsDataTypes.TimeSpan(Math.round(chart.getLowestVisibleX()*1000f*60f), Math.round(chart.getHighestVisibleX()*1000f*60f));

        // At the Before zooming/scrolling in the chart the chartViewField starts and ends at 0
        if (chartViewField.startTime == 0 && chartViewField.endTime == 0) {
            chartViewField.startTime = samples.get(0).relativeTime;
            chartViewField.endTime = samples.get(samples.size() - 1).relativeTime;
        }

        LineData lineData = new LineData();

        int converterIndex = 0;
        for (SampleConverter converter : converters) {
            converter.onCreate(getBaseWorkoutData());

            List<Entry> entries = new ArrayList<>();
            for (BaseSample sample : aggregatedSamples(NUMBER_OF_SAMPLES_IN_DIAGRAM, chartViewField)) {
                // turn data into Entry objects
                Entry e = new Entry((float) (sample.relativeTime) / 1000f / 60f, converter.getValue(sample), sample);
                entries.add(e);
            }
            chart.getXAxis().setValueFormatter(converter.getXValueFormatter());

            LineDataSet dataSet = new LineDataSet(entries, converter.getName()); // add entries to dataset
            int color = hasMultipleConverters ? getResources().getColor(converter.getColor()) : getThemePrimaryColor();
            dataSet.setColor(color);
            dataSet.setDrawValues(false);
            dataSet.setDrawCircles(false);
            dataSet.setLineWidth(2);
            dataSet.setHighlightLineWidth(2.5f);
            dataSet.setMode(LineDataSet.Mode.LINEAR);
            lineData.addDataSet(dataSet);

            if (converters.size() == 2) {
                YAxis.AxisDependency axisDependency = converterIndex == 0 ? YAxis.AxisDependency.LEFT : YAxis.AxisDependency.RIGHT;
                dataSet.setAxisDependency(axisDependency);
                chart.getAxis(axisDependency).setValueFormatter(converter.getYValueFormatter());
                chart.getAxisRight().setEnabled(true);
                chart.setMarker(new DisplayValueMarker(this, new DefaultValueFormatter(1), "", lineData));
                // TODO: Make marker for diagrams with plural datasets (eg Height+Speed) work better... -> Show Unit
            } else {
                chart.getAxisLeft().setValueFormatter(converter.getYValueFormatter());
                chart.getAxisRight().setValueFormatter(converter.getYValueFormatter());
                chart.getAxisRight().setEnabled(false);
                chart.setMarker(new DisplayValueMarker(this, converter.getYValueFormatter(), converter.getUnit(), lineData));
            }
            converterIndex++;
        }

        combinedData.setData(lineData);

        float yMax = lineData.getDataSetByIndex(0).getYMax() * 1.05f;
        if (showIntervalSets && intervals != null && intervals.length > 0) {
            List<BarEntry> barEntries = new ArrayList<>();

            for (long relativeTime : WorkoutCalculator.getIntervalSetTimesFromWorkout(getBaseWorkoutData())) {
                barEntries.add(new BarEntry((float) (relativeTime) / 1000f / 60f, yMax));
            }

            BarDataSet barDataSet = new BarDataSet(barEntries, getString(R.string.intervalSet));
            barDataSet.setBarBorderWidth(3);
            barDataSet.setBarBorderColor(getThemePrimaryColor());
            barDataSet.setColor(getThemePrimaryColor());

            BarData barData = new BarData(barDataSet);
            barData.setBarWidth(0.01f);
            barData.setDrawValues(false);

            combinedData.setData(barData);
        } else {
            combinedData.setData(new BarData()); // Empty bar data
        }

        chart.setData(combinedData);
        chart.invalidate();
    }

    public void updateChartSelection(CombinedChart chart, List<SampleConverter> converters) {
        // Fix Y-Axis scale
        chart.getAxisLeft().setAxisMaximum(converters.get(0).getMaxValue(workout)*1.05f);
        chart.getAxisLeft().setAxisMinimum(converters.get(0).getMinValue(workout)*0.95f);
        if (converters.size() > 1) {
            chart.getAxisRight().setAxisMaximum(converters.get(1).getMaxValue(workout)*1.05f);
            chart.getAxisRight().setAxisMinimum(converters.get(1).getMinValue(workout)*0.95f);
        }
    }

    private GpsSample findSample(Entry entry) {
        if (entry.getData() instanceof GpsSample) {
            return (GpsSample) entry.getData();
        } else {
            return null;
        }
    }

    protected boolean showPauses = false;
    boolean fullScreenItems = false;
    LinearLayout mapRoot;

    protected boolean hasSamples() {
        return samples.size() > 1;
    }

    protected BaseWorkoutData getBaseWorkoutData() {
        return new BaseWorkoutData(workout, samples);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public BaseWorkout getWorkout() {
        return workout;
    }

}
