package com.example.calendardemo;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.calendardemo.component.CalendarAttr;
import com.example.calendardemo.component.CalendarViewAdapter;
import com.example.calendardemo.interf.OnSelectDateListener;
import com.example.calendardemo.model.CalendarDate;
import com.example.calendardemo.view.Calendar;
import com.example.calendardemo.view.CustomDayView;
import com.example.calendardemo.view.MonthPager;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.calendar_page)
    protected MonthPager monthPager; //日历
    @BindView(R.id.rv_list_data)
    protected RecyclerView rvToDoList; //日历数据
    @BindView(R.id.show_month_view)
    protected TextView tvMonth; //月
    @BindView(R.id.show_year_view)
    protected TextView tvYear; //年
    @BindView(R.id.cdl_content)
    protected CoordinatorLayout cdlContent; //父容器
    private CalendarDate currentDate; //当前日期
    private OnSelectDateListener onSelectDateListener;
    private CalendarViewAdapter calendarAdapter; //日期适配器
    protected int mCurrentPage; //当前日期页
    private ArrayList<Calendar> currentCalendars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initCurrentDate();
        initCalendarView();

    }

    /**
     * 初始化currentDate
     *
     * @return void
     */
    private void initCurrentDate() {
        mCurrentPage = MonthPager.CURRENT_DAY_INDEX;
        currentDate = new CalendarDate();
        tvYear.setText(currentDate.getYear() + "年");
        tvMonth.setText(currentDate.getMonth() + "");
        //此处强行setViewHeight，毕竟你知道你的日历牌的高度
        monthPager.setViewHeight(Utils.dpi2px(this, 270));
        rvToDoList.setHasFixedSize(true);
        rvToDoList.setLayoutManager(new LinearLayoutManager(this));
        rvToDoList.setAdapter(new ExampleAdapter(this));

    }

    /**
     * 初始化CustomDayView，并作为CalendarViewAdapter的参数传入
     */
    private void initCalendarView() {
        initListener();
        CustomDayView customDayView = new CustomDayView(this, R.layout.calendar_custom_day);
        calendarAdapter = new CalendarViewAdapter(
                this,
                onSelectDateListener,
                CalendarAttr.WeekArrayType.Sunday,
                customDayView);
        calendarAdapter.setOnCalendarTypeChangedListener(new CalendarViewAdapter.OnCalendarTypeChanged() {
            @Override
            public void onCalendarTypeChanged(CalendarAttr.CalendarType type) {
                //上下滑动
                rvToDoList.scrollToPosition(0);
            }
        });
        //初始化完成与未完成的数据
        initMarkData();
        initMonthPager();
    }

    private void initListener() {
        onSelectDateListener = new OnSelectDateListener() {
            @Override
            public void onSelectDate(CalendarDate date) {
                refreshClickDate(date);
            }

            @Override
            public void onSelectOtherMonth(int offset) {
                //偏移量 -1表示刷新成上一个月数据 ， 1表示刷新成下一个月数据
                monthPager.selectOtherMonth(offset);
            }
        };
    }


    @Override
    protected void onResume() {
        super.onResume();
        /*
         * 如果你想以周模式启动你的日历，请在onResume是调用
         * Utils.scrollTo(content, rvToDoList, monthPager.getCellHeight(), 200);
         * calendarAdapter.switchToWeek(monthPager.getRowIndex());
         * */
        Utils.scrollTo(cdlContent, rvToDoList, monthPager.getCellHeight(), 200);
        calendarAdapter.switchToWeek(monthPager.getRowIndex());
    }

    /**
     * 滑动时间实时改变
     *
     * @param date 时间
     */
    private void refreshClickDate(CalendarDate date) {
        currentDate = date;
        tvYear.setText(date.getYear() + "年");
        tvMonth.setText(date.getMonth() + "");
    }

    /**
     * 初始化标记数据，HashMap的形式，可自定义
     * 如果存在异步的话，在使用setMarkData之后调用 calendarAdapter.notifyDataChanged();
     * TODO：这里数据需要日期数据显示运动任务和完成状态，这里年月日格式要转换
     */
    private void initMarkData() {
        HashMap<String, String> markData = new HashMap<>();
        markData.put("2018-5-11", "跑步");
        markData.put("2018-5-12", "0");
        markData.put("2018-5-13", "游泳");
        markData.put("2018-5-14", "0");
        calendarAdapter.setMarkData(markData);
    }

    /**
     * 初始化monthPager，MonthPager继承自ViewPager
     *
     * @return void
     */
    private void initMonthPager() {
        monthPager.setAdapter(calendarAdapter);
        monthPager.setCurrentItem(MonthPager.CURRENT_DAY_INDEX);
        monthPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                position = (float) Math.sqrt(1 - Math.abs(position));
                page.setAlpha(position);
            }
        });
        monthPager.addOnPageChangeListener(new MonthPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //当前页
                mCurrentPage = position;
                //当前页的日期树量
                currentCalendars = calendarAdapter.getPagers();
                if (currentCalendars.get(position % currentCalendars.size()) != null) {
                    //需要调试
                    CalendarDate date = currentCalendars.get(position % currentCalendars.size()).getSeedDate();
                    currentDate = date;
                    tvYear.setText(date.getYear() + "年");
                    tvMonth.setText(date.getMonth() + "");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

}
