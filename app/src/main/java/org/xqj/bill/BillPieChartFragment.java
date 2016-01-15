package org.xqj.bill;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Chaos
 *         2016/01/13.
 */
public class BillPieChartFragment extends DataFragment implements
        OnChartValueSelectedListener, RadioGroup.OnCheckedChangeListener {

    @Bind(R.id.pie_chart) PieChart mPieChart;
    @Bind(R.id.tips) TextView mTips;
    @Bind(R.id.type_group) RadioGroup mTypeGroup;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bill_pie_chart, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mTypeGroup.setOnCheckedChangeListener(this);
        initPieChart();
        onUpgrade();
    }

    private void initPieChart() {
        mPieChart.setUsePercentValues(true);
        mPieChart.setDescription("");
        mPieChart.setExtraOffsets(15, 10, 15, 45);
        mPieChart.setDragDecelerationFrictionCoef(0.95f);
        mPieChart.setDrawHoleEnabled(true);
        mPieChart.setHoleColorTransparent(true);
        mPieChart.setTransparentCircleColor(Color.WHITE);
        mPieChart.setTransparentCircleAlpha(110);
        mPieChart.setHoleRadius(45f);
        mPieChart.setTransparentCircleRadius(52f);
        mPieChart.setDrawCenterText(true);
        mPieChart.setRotationAngle(0);
        mPieChart.setDrawSliceText(true);
        // enable rotation of the chart by touch
        mPieChart.setRotationEnabled(true);
        mPieChart.setHighlightPerTapEnabled(true);
        mPieChart.setOnChartValueSelectedListener(this);
        mPieChart.setRotationEnabled(false);

        Legend l = mPieChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setYEntrySpace(5f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
    }

    @Override
    public void onUpgrade() {
        boolean isExpense = mTypeGroup.getCheckedRadioButtonId() == R.id.expense_btn;

        List<String> types = getAllConsumptionType();
        List<String> actualTypes = new ArrayList<>();
        List<Entry> entries = new ArrayList<>();
        int i = 0;
        for (String type : types) {
            float num = getCurrentSumByTwoType(!isExpense, type);
            if (num != 0) {
                actualTypes.add(type);
                entries.add(new Entry(num, i++));
            }
        }

        if (entries.isEmpty()) {
            mPieChart.setVisibility(View.GONE);
            mTips.setVisibility(View.VISIBLE);
        } else {
            PieDataSet dataSet = new PieDataSet(entries, isExpense ? "支出数据" : "收入数据");
            dataSet.setSliceSpace(2f);
            dataSet.setSelectionShift(15f);

            ArrayList<Integer> colors = new ArrayList<Integer>();

            for (int c : ColorTemplate.VORDIPLOM_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.JOYFUL_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.COLORFUL_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.LIBERTY_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.PASTEL_COLORS)
                colors.add(c);

            colors.add(ColorTemplate.getHoloBlue());

            dataSet.setColors(colors);

            PieData data = new PieData(actualTypes, dataSet);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextSize(13f);
            mPieChart.setData(data);
            mPieChart.highlightValues(null);

            mPieChart.setCenterText(generateCenterSpannableText(actualTypes.get(0), entries.get(0).getVal()));

            mPieChart.invalidate();

            mTips.setVisibility(View.GONE);
            mPieChart.setVisibility(View.VISIBLE);

            mPieChart.animateY(800, Easing.EasingOption.EaseInOutQuad);
        }
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        if (e == null) {
            return;
        }
        float num = e.getVal();
        String type = mPieChart.getData().getXVals().get(e.getXIndex());
        mPieChart.setCenterText(generateCenterSpannableText(type, num));
    }

    @Override
    public void onNothingSelected() {
        /* do nothing */
    }

    private SpannableString generateCenterSpannableText(String type, float num) {
        String centerText = String.format("%s\n%s元", type, num);
        SpannableString s = new SpannableString(centerText);
        s.setSpan(new RelativeSizeSpan(1.2f), 0, type.length(), 0);
        s.setSpan(new RelativeSizeSpan(1.4f), type.length() + 1, centerText.length(), 0);
        return s;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        onUpgrade();
    }
}