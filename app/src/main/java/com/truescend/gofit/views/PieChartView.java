package com.truescend.gofit.views;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.IntDef;
import android.util.AttributeSet;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
//import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 作者:东芝(2019/6/1).
 * 功能:饼状图
 */
public class PieChartView extends PieChart implements OnChartValueSelectedListener {
    private static final int ANIMATE_DURATION_MILLIS = 1000;
    private List<PieEntry> entries = new ArrayList<>();
    private List<Integer> colors = new ArrayList<>();
    private  OnChartValueSelectedListener onChartValueSelectedListener;
    private List<PieDataEntry> tempChartData = new ArrayList<>();


    public void setOnPieSelectedListener(OnChartValueSelectedListener onChartValueSelectedListener) {
        this.onChartValueSelectedListener = onChartValueSelectedListener;
    }

    public PieChartView(Context context) {
        super(context);
        initChart();
    }

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initChart();
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initChart();
    }

    private void initChart() {

        setUsePercentValues(true);
        getDescription().setEnabled(false);
        setDragDecelerationFrictionCoef(0.95f);

        setCenterTextSize(18);
        setExtraOffsets(0.f, 25.f, 0.f, 25.f);
        setDrawHoleEnabled(true);
        setHoleColor(Color.WHITE);
        setTransparentCircleColor(Color.WHITE);
        setTransparentCircleAlpha(110);
        setHoleRadius(58f);
        setTransparentCircleRadius(61f);
        setDrawCenterText(true);
        setRotationAngle(0);
        setRotationEnabled(true);
        setHighlightPerTapEnabled(true);
        setOnChartValueSelectedListener(this);
        Legend l = getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(false);


    }

    public void setChartData(List<PieDataEntry> chartData) {
        if (chartData == null) {
            return;
        }
        float total = 0;
        int size = chartData.size();
        for (int i = 0; i < size; i++) {
            PieDataEntry chartDatum = chartData.get(i);
            total += chartDatum.value;
        }

        this.tempChartData.clear();
        tempChartData.addAll(chartData);
        entries.clear();
        colors.clear();

        for (int i = 0; i < size; i++) {
            PieDataEntry entry = chartData.get(i);
            entry.totalValue = total;
            float value = entry.value;

            if (value/total*100f<4) {
                value =  total*4/100f;
            }

            entries.add(new PieEntry(value, "", entry));
            colors.add(entry.color);
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(1f);
        dataSet.setColors(colors);
        dataSet.setSelectionShift(0f);
        dataSet.setValueLinePart1OffsetPercentage(105.0f);
        dataSet.setValueLinePart1Length(0.4f);
//      dataSet.setValueLinePart2Length(0.2f);
//      dataSet.setUsingSliceColorAsValueLineColor(true);
//      dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPieLabel(float value, PieEntry pieEntry) {
                PieDataEntry entry = (PieDataEntry) pieEntry.getData();
                if(entry.type==PieChartView.PieDataEntry.TYPE_EMPTY){
                    return entry.label;
                }
                return String.format(Locale.CHINESE, "%s %.1f%%", entry.label, entry.value/entry.totalValue*100f);
            }
        });
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);
        setData(data);
        //chart.highlightValues(null);
        animateY(ANIMATE_DURATION_MILLIS, Easing.EaseInOutQuad);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (onChartValueSelectedListener != null) {
            PieDataEntry data = (PieDataEntry) e.getData();
            int position  = (int) h.getX();
            if(!tempChartData.isEmpty()) {
                for (position = 0; position < tempChartData.size(); position++) {
                    PieDataEntry tempChartDatum = tempChartData.get(position);
                    if (tempChartDatum.equals(data)) {
                        break;
                    }
                }
            }
            onChartValueSelectedListener.onValueSelected(position, data);
        }
    }

    @Override
    public void onNothingSelected() {
        if (onChartValueSelectedListener != null) {
            onChartValueSelectedListener.onNothingSelected();
        }
    }

    public static class PieDataEntry {
        public static final int TYPE_EMPTY = 0;
        public static final int TYPE_SPORT_MODE_TYPE = 1;
        public static final int TYPE_DETAIL_WEEK = 2;
        private @PieType int type = TYPE_EMPTY;
        private String label;
        private float value;
        private float totalValue;
        private int color;
        private Object obj;

        @IntDef(flag = true, value = {
                TYPE_EMPTY,
                TYPE_SPORT_MODE_TYPE,
                TYPE_DETAIL_WEEK
        })
        @Retention(RetentionPolicy.SOURCE)
        public @interface PieType {}

        public PieDataEntry(@PieType int type, String label, float value, int color,Object obj) {
            this.type = type;
            this.label = label;
            this.value = value;
            this.color = color;
            this.obj = obj;
        }


        public Object getObj() {
            return obj;
        }

        public void setObj(Object obj) {
            this.obj = obj;
        }

        public @PieType int getType() {
            return type;
        }



        public void setType(@PieType int type) {
            this.type = type;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }
    }

    public interface OnChartValueSelectedListener {

        void onValueSelected(int position,PieDataEntry entry);

        /**
         * Called when nothing has been selected or an "un-select" has been made.
         */
        void onNothingSelected();
    }

}
