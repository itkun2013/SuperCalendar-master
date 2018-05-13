package com.example.calendardemo.view;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.calendardemo.R;
import com.example.calendardemo.Utils;
import com.example.calendardemo.component.State;
import com.example.calendardemo.interf.IDayRenderer;
import com.example.calendardemo.model.CalendarDate;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 作者：Shinelon on 2018/5/13 16:16
 * 作用：日期view
 */
public class CustomDayView extends DayView {
    @BindView(R.id.today_background)
    protected View viewToadayBg; //今日的背景
    @BindView(R.id.selected_background)
    protected View viewSelectBg; //选中的背景
    @BindView(R.id.tv_date)
    protected TextView tvDate; //日期
    @BindView(R.id.iv_maker)
    protected ImageView ivMark; //任务是否完成的标记
    @BindView(R.id.tv_maker)
    protected TextView tvMark; //任务没完成的标记
    //当天日期
    private final CalendarDate today = new CalendarDate();

    /**
     * 构造器
     *
     * @param context        上下文
     * @param layoutResource 自定义DayView的layout资源
     */
    public CustomDayView(Context context, int layoutResource) {
        super(context, layoutResource);
        ButterKnife.bind(this);
    }

    @Override
    public IDayRenderer copy() {
        return new CustomDayView(context, layoutResource);
    }

    @Override
    public void refreshContent() {
        //你的代码 你可以在这里定义你的显示规则
        renderToday(day.getDate());
        renderSelect(day.getState());
        renderMarker(day.getDate(), day.getState());
        super.refreshContent();
        super.refreshContent();
    }

    /**
     * 刷新今日
     *
     * @param date 日期时间
     */
    private void renderToday(CalendarDate date) {
        if (date != null) {
            if (date.equals(today)) {
//                tvDate.setText("今");
                tvDate.setText(date.day + "");
                viewToadayBg.setVisibility(VISIBLE);
            } else {
                tvDate.setText(date.day + "");
                viewToadayBg.setVisibility(GONE);
            }
        }
    }

    /**
     * 刷新选中状态
     *
     * @param state 状态
     */
    private void renderSelect(State state) {
        if (state == State.SELECT) {
            viewSelectBg.setVisibility(VISIBLE);
            //当月显示
            tvDate.setVisibility(VISIBLE);
            tvDate.setTextColor(Color.WHITE);
        } else if (state == State.NEXT_MONTH || state == State.PAST_MONTH) {
            //上月和下月的不显示
            viewSelectBg.setVisibility(GONE);
            tvDate.setVisibility(INVISIBLE);
            tvDate.setTextColor(Color.parseColor("#d5d5d5"));
        } else {
            //当月显示
            tvDate.setVisibility(VISIBLE);
            viewSelectBg.setVisibility(GONE);
            tvDate.setTextColor(Color.parseColor("#111111"));
        }
    }

    /**
     * 完成标志
     *
     * @param date  日期
     * @param state 状态
     */
    private void renderMarker(CalendarDate date, State state) {
        if (Utils.loadMarkData().containsKey(date.toString())) {

            if (Utils.loadMarkData().get(date.toString()).equals("0")) {
                //0代表完成
                ivMark.setVisibility(VISIBLE);
                tvMark.setVisibility(GONE);
                ivMark.setEnabled(true);
            } else {
                //1代表没完成
                ivMark.setVisibility(GONE);
                tvMark.setVisibility(VISIBLE);
                Log.e("3333333","数据 "+Utils.loadMarkData().get(date.toString()));
                tvMark.setText(Utils.loadMarkData().get(date.toString()));
            }
        } else {
            ivMark.setVisibility(GONE);
        }
    }
}
