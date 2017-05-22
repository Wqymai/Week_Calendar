package com.beiing.weekcalendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.beiing.weekcalendar.adapter.CalendarPagerAdapter;
import com.beiing.weekcalendar.adapter.WeekAdapter;
import com.beiing.weekcalendar.listener.CustomPagerChandeListender;
import com.beiing.weekcalendar.listener.DateSelectListener;
import com.beiing.weekcalendar.listener.GetViewHelper;
import com.beiing.weekcalendar.listener.WeekChangeListener;

import org.joda.time.DateTime;

/**
 * Created by linechen on 2017/5/18.<br/>
 * 描述：周日历
 * </br>
 */

public class WeekCalendar extends LinearLayout {
    private static final String TAG = "WeekCalendar";

    public static final int DAYS_OF_WEEK = 7;

    private int maxCount = 1000;
    private int centerPosition = maxCount / 2;

    //-----日历属性
    private int headerHeight;

    private int headerBgColor;

    private int calendarHeight;

    /**
     * 日历星期
     */
    private GridView weekGrid;
    /**
     * 日历日期
     */
    private ViewPager viewPagerContent;

    private CalendarPagerAdapter calendarPagerAdapter;

    private GetViewHelper getViewHelper;

    private DateSelectListener dateSelectListener;

    private WeekChangeListener weekChangedListener;

    public WeekCalendar(Context context) {
        this(context, null);
    }

    public WeekCalendar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeekCalendar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WeekCalendar);
        try {
            headerHeight = (int) ta.getDimension(R.styleable.WeekCalendar_wc_headerHeight, getResources().getDimension(R.dimen.calender_header_height));
            headerBgColor = ta.getColor(R.styleable.WeekCalendar_wc_headerBgColor, Color.WHITE);
            calendarHeight = (int) ta.getDimension(R.styleable.WeekCalendar_wc_calendarHeight, getResources().getDimension(R.dimen.calender_content_height));
        } finally {
            ta.recycle();
        }
    }

    private void initView() {
        setOrientation(VERTICAL);
        addHeaderView();
        addWeekView();
        initWeekViewListenter();
    }

    private void addHeaderView() {
        View header =  LayoutInflater.from(getContext()).inflate(R.layout.layout_calender_header, this, false);
        header.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, headerHeight));
        header.setBackgroundColor(headerBgColor);
        weekGrid = (GridView) header.findViewById(R.id.grid_week);
        addView(header);
        weekGrid.setAdapter(new WeekAdapter(getViewHelper));
    }

    private void addWeekView() {
        View calendar = LayoutInflater.from(getContext()).inflate(R.layout.layout_calendar_content, this, false);
        calendar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, calendarHeight));
        viewPagerContent = (ViewPager) calendar.findViewById(R.id.viewpager_calendar);
        addView(calendar);
        DateTime startDay = new DateTime();
        startDay = startDay.minusDays(startDay.getDayOfWeek());
        calendarPagerAdapter = new CalendarPagerAdapter(getContext(), maxCount, startDay, getViewHelper);
        viewPagerContent.setAdapter(calendarPagerAdapter);
        viewPagerContent.setCurrentItem(centerPosition);
    }

    private void initWeekViewListenter() {
        viewPagerContent.addOnPageChangeListener(new CustomPagerChandeListender() {
            @Override
            public void onPageSelected(int position) {
                onWeekChange(position);
            }
        });

    }

    private void onWeekChange(int position) {
        int intervalWeeks = position - centerPosition;
        DateTime firstDayofWeek = calendarPagerAdapter.getStartDateTime().plusWeeks(intervalWeeks);
        if(weekChangedListener != null){
            weekChangedListener.onWeekChanged(firstDayofWeek);
        }
    }

    public void setGetViewHelper(GetViewHelper getViewHelper) {
        this.getViewHelper = getViewHelper;
        initView();
    }

    public void setDateSelectListener(DateSelectListener dateSelectListener) {
        this.dateSelectListener = dateSelectListener;
        calendarPagerAdapter.setDateSelectListener(dateSelectListener);
    }

    public void setWeekChangedListener(WeekChangeListener weekChangedListener) {
        this.weekChangedListener = weekChangedListener;
    }

    public DateTime getSelectDateTime() {
        return calendarPagerAdapter.getSelectDateTime();
    }

    public void refresh(){
        calendarPagerAdapter.notifyDataSetChanged();
    }

    public void gotoDate(DateTime dateTime){
        viewPagerContent.setCurrentItem(centerPosition);
        calendarPagerAdapter.setStartDateTime(dateTime.minusDays(dateTime.getDayOfWeek()));
        onWeekChange(centerPosition);
    }

}
