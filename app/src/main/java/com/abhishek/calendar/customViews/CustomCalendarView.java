package com.abhishek.calendar.customViews;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;

import com.abhishek.calendar.R;
import com.google.android.material.color.MaterialColors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CustomCalendarView extends LinearLayout implements GestureDetector.OnGestureListener {

    private static final String DAY_OF_THE_WEEK_TEXT = "dayOfTheWeekText";
    private static final String DAY_OF_THE_WEEK_LAYOUT = "dayOfTheWeekLayout";
    private static final String DAY_OF_THE_MONTH_LAYOUT = "dayOfTheMonthLayout";
    private static final String DAY_OF_THE_MONTH_TEXT = "dayOfTheMonthText";
    private static final String DAY_OF_THE_MONTH_BACKGROUND = "dayOfTheMonthBackground";
    private static final String DAY_OF_THE_MONTH_CIRCLE_IMAGE_1 = "dayOfTheMonthCircleImage1";
    private static final String DAY_OF_THE_MONTH_CIRCLE_IMAGE_2 = "dayOfTheMonthCircleImage2";

    private TextView dateTitle;

    private ImageView leftButton;
    private ImageView rightButton;

    private ImageView showTaskOfDay;

    private View rootView;
    private ViewGroup customCalendarMonthLayout;
    private CustomCalendarListener customCalendarListener;
    @NotNull
    private Calendar currentCalendar = Calendar.getInstance();
    @Nullable
    private Calendar lastSelectedDayCalendar;
    private final OnClickListener onDayOfMonthClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {

            ViewGroup dayOfTheMonthContainer = (ViewGroup) view;
            String tagId = (String) dayOfTheMonthContainer.getTag();
            tagId = tagId.substring(DAY_OF_THE_MONTH_LAYOUT.length(), tagId.length());
            TextView dayOfTheMonthText = view.findViewWithTag(DAY_OF_THE_MONTH_TEXT + tagId);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, currentCalendar.get(Calendar.YEAR));
            calendar.set(Calendar.MONTH, currentCalendar.get(Calendar.MONTH));
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dayOfTheMonthText.getText().toString()));

            markDayAsSelectedDay(calendar.getTime());

            if (customCalendarListener == null) {
                throw new IllegalStateException("You must assign a valid CustomCalendarListener first!");
            } else {
                customCalendarListener.onDayClick(calendar.getTime());
            }
        }
    };
    private final OnLongClickListener onDayOfMonthLongClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {

            ViewGroup dayOfTheMonthContainer = (ViewGroup) view;
            String tagId = (String) dayOfTheMonthContainer.getTag();
            tagId = tagId.substring(DAY_OF_THE_MONTH_LAYOUT.length(), tagId.length());
            TextView dayOfTheMonthText = view.findViewWithTag(DAY_OF_THE_MONTH_TEXT + tagId);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, currentCalendar.get(Calendar.YEAR));
            calendar.set(Calendar.MONTH, currentCalendar.get(Calendar.MONTH));
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dayOfTheMonthText.getText().toString()));

            markDayAsSelectedDay(calendar.getTime());

            if (customCalendarListener == null) {
                throw new IllegalStateException("You must assign a valid CustomCalendarListener first!");
            } else {
                customCalendarListener.onDayLongClick(calendar.getTime());
            }
            return true;
        }
    };
    private boolean shortWeekDays = false;

    public CustomCalendarView(Context context) {
        super(context);
        init(null);
    }

    public CustomCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private static String checkSpecificLocales(String dayOfTheWeekString, int i) {
        if (i == 4 && "ES".equals(Locale.getDefault().getCountry())) {
            dayOfTheWeekString = "X";
        } else {
            dayOfTheWeekString = dayOfTheWeekString.substring(0, 1).toUpperCase();
        }
        return dayOfTheWeekString;
    }

    private static int getDayIndexByDate(Calendar currentCalendar) {
        int monthOffset = getMonthOffset(currentCalendar);
        int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
        return currentDay + monthOffset;
    }

    private static int getMonthOffset(Calendar currentCalendar) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentCalendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayWeekPosition = calendar.getFirstDayOfWeek();
        int dayPosition = calendar.get(Calendar.DAY_OF_WEEK);

        if (firstDayWeekPosition == 1) {
            return dayPosition - 1;
        } else {

            if (dayPosition == 1) {
                return 6;
            } else {
                return dayPosition - 2;
            }
        }
    }

    private static int getWeekIndex(int weekIndex, Calendar currentCalendar) {
        int firstDayWeekPosition = currentCalendar.getFirstDayOfWeek();

        if (firstDayWeekPosition == 1) {
            return weekIndex;
        } else {

            if (weekIndex == 1) {
                return 7;
            } else {
                return weekIndex - 1;
            }
        }
    }

    private static boolean areInTheSameDay(@NotNull Calendar calendarOne, @NotNull Calendar calendarTwo) {
        return calendarOne.get(Calendar.YEAR) == calendarTwo.get(Calendar.YEAR) && calendarOne.get(Calendar.DAY_OF_YEAR) == calendarTwo.get(Calendar.DAY_OF_YEAR);
    }

    private void init(@Nullable AttributeSet set) {

        if (isInEditMode()) {
            return;
        }

        LayoutInflater inflate = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflate.inflate(R.layout.calendar_view_layout, this, true);
        findViewsById(rootView);
        setUpEventListeners();

        currentCalendar = Calendar.getInstance();
        setDate(currentCalendar.getTime());
        gestureDetector = new GestureDetector(getContext(), this);

    }

    public void setDate(@NotNull Date date) {
        currentCalendar.setTime(date);
        updateView();
    }


    @NotNull
    public Date getDate() {
        return currentCalendar.getTime();
    }

    @Nullable
    public Date getSelectedDay() {
        if (lastSelectedDayCalendar == null || lastSelectedDayCalendar.getTime() == null) {
            return getDate();
        } else {
            return lastSelectedDayCalendar.getTime();

        }
    }

    public void markDayAsSelectedDay(@NotNull Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Clear previous current day mark
        clearSelectedDay();

        // Store current values as last values
        lastSelectedDayCalendar = calendar;

        // Mark current day as selected
        ViewGroup dayOfTheMonthBackground = getDayOfMonthBackground(calendar);
        dayOfTheMonthBackground.setBackgroundResource(R.drawable.circle);

        TextView dayOfTheMonth = getDayOfMonthText(calendar);
        dayOfTheMonth.setTextColor(getColorCompatFromAttribute(getContext(), R.attr.calendar_view_selected_day_text_color));

        ImageView circleImage1 = getCircleImage1(calendar);
        ImageView circleImage2 = getCircleImage2(calendar);
        int selectedDayColor = getColorCompatFromAttribute(getContext(), R.attr.calendar_view_selected_day_text_color);
        if (circleImage1.getVisibility() == VISIBLE) {
            DrawableCompat.setTint(circleImage1.getDrawable(), selectedDayColor);
        }

        if (circleImage2.getVisibility() == VISIBLE) {
            DrawableCompat.setTint(circleImage2.getDrawable(), selectedDayColor);
        }
    }

    public void clearSelectedDay() {
        if (lastSelectedDayCalendar != null) {
            ViewGroup dayOfTheMonthBackground = getDayOfMonthBackground(lastSelectedDayCalendar);

            // If it's today, keep the current day style
            Calendar nowCalendar = Calendar.getInstance();
            if (nowCalendar.get(Calendar.YEAR) == lastSelectedDayCalendar.get(Calendar.YEAR) && nowCalendar.get(Calendar.DAY_OF_YEAR) == lastSelectedDayCalendar.get(Calendar.DAY_OF_YEAR)) {
                dayOfTheMonthBackground.setBackgroundResource(R.drawable.ring);
            } else {
                dayOfTheMonthBackground.setBackgroundResource(android.R.color.transparent);
            }

            TextView dayOfTheMonth = getDayOfMonthText(lastSelectedDayCalendar);
            dayOfTheMonth.setTextColor(getColorCompatFromAttribute(getContext(), R.attr.calendar_view_day_of_the_month_text_color));

            ImageView circleImage1 = getCircleImage1(lastSelectedDayCalendar);
            ImageView circleImage2 = getCircleImage2(lastSelectedDayCalendar);
            if (circleImage1.getVisibility() == VISIBLE) {
                DrawableCompat.setTint(circleImage1.getDrawable(), getColorCompatFromAttribute(getContext(), R.attr.calendar_view_circle_1));
            }

            if (circleImage2.getVisibility() == VISIBLE) {
                DrawableCompat.setTint(circleImage2.getDrawable(), getColorCompatFromAttribute(getContext(), R.attr.calendar_view_circle_2));
            }
        }
    }

    public void setShortWeekDays(boolean shortWeekDays) {
        this.shortWeekDays = shortWeekDays;
    }

    public void showDateTitle(boolean show) {
        if (show) {
            customCalendarMonthLayout.setVisibility(VISIBLE);
        } else {
            customCalendarMonthLayout.setVisibility(GONE);
        }
    }

    public void setCustomCalendarListener(CustomCalendarListener customCalendarListener) {
        this.customCalendarListener = customCalendarListener;
    }

    private void findViewsById(View view) {

        customCalendarMonthLayout = view.findViewById(R.id.calendarDateTitleContainer);
        leftButton = view.findViewById(R.id.leftButton);
        rightButton = view.findViewById(R.id.rightButton);
        dateTitle = view.findViewById(R.id.monthText);
        showTaskOfDay = view.findViewById(R.id.showTaskOfDay);
        LayoutInflater inflate = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < 42; i++) {

            int weekIndex = (i % 7) + 1;
            ViewGroup dayOfTheWeekLayout = view.findViewWithTag(DAY_OF_THE_WEEK_LAYOUT + weekIndex);

            @SuppressLint("InflateParams")
            View dayOfTheMonthLayout = inflate.inflate(R.layout.calendar_day_of_the_month_layout, null);
            View dayOfTheMonthText = dayOfTheMonthLayout.findViewWithTag(DAY_OF_THE_MONTH_TEXT);
            View dayOfTheMonthBackground = dayOfTheMonthLayout.findViewWithTag(DAY_OF_THE_MONTH_BACKGROUND);
            View dayOfTheMonthCircleImage1 = dayOfTheMonthLayout.findViewWithTag(DAY_OF_THE_MONTH_CIRCLE_IMAGE_1);
            View dayOfTheMonthCircleImage2 = dayOfTheMonthLayout.findViewWithTag(DAY_OF_THE_MONTH_CIRCLE_IMAGE_2);

            int viewIndex = i + 1;
            dayOfTheMonthLayout.setTag(DAY_OF_THE_MONTH_LAYOUT + viewIndex);
            dayOfTheMonthText.setTag(DAY_OF_THE_MONTH_TEXT + viewIndex);
            dayOfTheMonthBackground.setTag(DAY_OF_THE_MONTH_BACKGROUND + viewIndex);
            dayOfTheMonthCircleImage1.setTag(DAY_OF_THE_MONTH_CIRCLE_IMAGE_1 + viewIndex);
            dayOfTheMonthCircleImage2.setTag(DAY_OF_THE_MONTH_CIRCLE_IMAGE_2 + viewIndex);

            dayOfTheWeekLayout.addView(dayOfTheMonthLayout);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
            return false;
        }
        return false;
    }

    private void setUpEventListeners() {

        leftButton.setOnClickListener(view -> {
            if (customCalendarListener == null) {
                throw new IllegalStateException("You must assign a valid CustomCalendarListener first!");
            }

            // Decrease month
            currentCalendar.add(Calendar.MONTH, -1);
            lastSelectedDayCalendar = null;
            updateView();
            customCalendarListener.onLeftButtonClick();
        });

        rightButton.setOnClickListener(view -> {
            if (customCalendarListener == null) {
                throw new IllegalStateException("You must assign a valid CustomCalendarListener first!");
            }

            // Increase month
            currentCalendar.add(Calendar.MONTH, 1);
            lastSelectedDayCalendar = null;
            updateView();
            customCalendarListener.onRightButtonClick();
        });

        dateTitle.setOnClickListener(view -> {
            if (customCalendarListener == null) {
                throw new IllegalStateException("You must assign a valid CustomCalendarListener first!");
            }
            customCalendarListener.onDateSelectorClick(getDate());
        });
        showTaskOfDay.setOnClickListener(view -> {
            customCalendarListener.onShowTaskOfDayClick(getSelectedDay());
        });
    }

    private void setUpMonthLayout() {
        String dateText = new DateFormatSymbols(Locale.getDefault()).getMonths()[currentCalendar.get(Calendar.MONTH)];
        dateText = dateText.substring(0, 1).toUpperCase() + dateText.subSequence(1, dateText.length());
        Calendar calendar = Calendar.getInstance();
        if (currentCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
            // dateTitle.setText(dateText);
            dateTitle.setText(String.format("%s %s", dateText, currentCalendar.get(Calendar.YEAR)));
        } else {
            dateTitle.setText(String.format("%s %s", dateText, currentCalendar.get(Calendar.YEAR)));
        }
    }

    private void setUpWeekDaysLayout() {
        TextView dayOfWeek;
        String dayOfTheWeekString;
        String[] weekDaysArray = new DateFormatSymbols(Locale.getDefault()).getWeekdays();
        int length = weekDaysArray.length;
        for (int i = 1; i < length; i++) {
            dayOfWeek = rootView.findViewWithTag(DAY_OF_THE_WEEK_TEXT + getWeekIndex(i, currentCalendar));
            dayOfTheWeekString = weekDaysArray[i];
            if (shortWeekDays) {
                dayOfTheWeekString = checkSpecificLocales(dayOfTheWeekString, i);
            } else {
                dayOfTheWeekString = dayOfTheWeekString.substring(0, 1).toUpperCase() + dayOfTheWeekString.substring(1, 3);
            }

            dayOfWeek.setText(dayOfTheWeekString);
        }
    }

    private void setUpDaysOfMonthLayout() {

        TextView dayOfTheMonthText;
        View circleImage1;
        View circleImage2;
        ViewGroup dayOfTheMonthContainer;
        ViewGroup dayOfTheMonthBackground;

        for (int i = 1; i < 43; i++) {

            dayOfTheMonthContainer = rootView.findViewWithTag(DAY_OF_THE_MONTH_LAYOUT + i);
            dayOfTheMonthBackground = rootView.findViewWithTag(DAY_OF_THE_MONTH_BACKGROUND + i);
            dayOfTheMonthText = rootView.findViewWithTag(DAY_OF_THE_MONTH_TEXT + i);
            circleImage1 = rootView.findViewWithTag(DAY_OF_THE_MONTH_CIRCLE_IMAGE_1 + i);
            circleImage2 = rootView.findViewWithTag(DAY_OF_THE_MONTH_CIRCLE_IMAGE_2 + i);

            dayOfTheMonthText.setVisibility(View.INVISIBLE);
            circleImage1.setVisibility(View.GONE);
            circleImage2.setVisibility(View.GONE);

            // Apply styles
            dayOfTheMonthText.setBackgroundResource(android.R.color.transparent);
            dayOfTheMonthText.setTypeface(null, Typeface.NORMAL);
            dayOfTheMonthText.setTextColor(getColorCompatFromAttribute(getContext(), R.attr.calendar_view_day_of_the_month_text_color));
            dayOfTheMonthContainer.setBackgroundResource(android.R.color.transparent);
            dayOfTheMonthContainer.setOnClickListener(null);
            dayOfTheMonthBackground.setBackgroundResource(android.R.color.transparent);
        }
    }

    private void setUpDaysInCalendar() {

        Calendar auxCalendar = Calendar.getInstance(Locale.getDefault());
        auxCalendar.setTime(currentCalendar.getTime());
        auxCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfMonth = auxCalendar.get(Calendar.DAY_OF_WEEK);
        TextView dayOfTheMonthText;
        ViewGroup dayOfTheMonthContainer;
        ViewGroup dayOfTheMonthLayout;

        // Calculate dayOfTheMonthIndex
        int dayOfTheMonthIndex = getWeekIndex(firstDayOfMonth, auxCalendar);

        for (int i = 1; i <= auxCalendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++, dayOfTheMonthIndex++) {
            dayOfTheMonthContainer = rootView.findViewWithTag(DAY_OF_THE_MONTH_LAYOUT + dayOfTheMonthIndex);
            dayOfTheMonthText = rootView.findViewWithTag(DAY_OF_THE_MONTH_TEXT + dayOfTheMonthIndex);
            if (dayOfTheMonthText == null) {
                break;
            }
            dayOfTheMonthContainer.setOnClickListener(onDayOfMonthClickListener);
            dayOfTheMonthContainer.setOnLongClickListener(onDayOfMonthLongClickListener);
            dayOfTheMonthText.setVisibility(View.VISIBLE);
            dayOfTheMonthText.setText(String.valueOf(i));
        }

        for (int i = 36; i < 43; i++) {
            dayOfTheMonthText = rootView.findViewWithTag(DAY_OF_THE_MONTH_TEXT + i);
            dayOfTheMonthLayout = rootView.findViewWithTag(DAY_OF_THE_MONTH_LAYOUT + i);
            if (dayOfTheMonthText.getVisibility() == INVISIBLE) {
                dayOfTheMonthLayout.setVisibility(GONE);
            } else {
                dayOfTheMonthLayout.setVisibility(VISIBLE);
            }
        }
    }

    private void markDayAsCurrentDay() {
        // If it's the current month, mark current day
        Calendar nowCalendar = Calendar.getInstance();
        if (nowCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) && nowCalendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)) {
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTime(nowCalendar.getTime());

            ViewGroup dayOfTheMonthBackground = getDayOfMonthBackground(currentCalendar);
            dayOfTheMonthBackground.setBackgroundResource(R.drawable.ring);
        }
    }

    private void updateView() {
        setUpMonthLayout();
        setUpWeekDaysLayout();
        setUpDaysOfMonthLayout();
        setUpDaysInCalendar();
        markDayAsCurrentDay();
    }

    private ViewGroup getDayOfMonthBackground(Calendar currentCalendar) {
        return (ViewGroup) getView(DAY_OF_THE_MONTH_BACKGROUND, currentCalendar);
    }

    private TextView getDayOfMonthText(Calendar currentCalendar) {
        return (TextView) getView(DAY_OF_THE_MONTH_TEXT, currentCalendar);
    }

    private ImageView getCircleImage1(Calendar currentCalendar) {
        return (ImageView) getView(DAY_OF_THE_MONTH_CIRCLE_IMAGE_1, currentCalendar);
    }

    private ImageView getCircleImage2(Calendar currentCalendar) {
        return (ImageView) getView(DAY_OF_THE_MONTH_CIRCLE_IMAGE_2, currentCalendar);
    }

    private View getView(String key, Calendar currentCalendar) {
        int index = getDayIndexByDate(currentCalendar);
        return rootView.findViewWithTag(key + index);
    }

    private int getColorCompatFromAttribute(Context context, int attributeId) {
        return MaterialColors.getColor(context, attributeId, Color.RED);
    }

    @Override
    public void onShowPress(@NonNull MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(@androidx.annotation.Nullable MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(@NonNull MotionEvent e) {

    }

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    private GestureDetector gestureDetector;

    // ... inside your init method or a suitable initialization point:
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        boolean result = false;
        try {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight();
                    } else {
                        onSwipeLeft();
                    }
                    result = true;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }

    public void onSwipeRight() {
        currentCalendar.add(Calendar.MONTH, -1);
        lastSelectedDayCalendar = null;
        //updateView();
        animateTransition(-1);
        if (customCalendarListener != null) {
            customCalendarListener.onLeftButtonClick();
        }
    }

    public void onSwipeLeft() {
        currentCalendar.add(Calendar.MONTH, 1);
        lastSelectedDayCalendar = null;
        animateTransition(1);
       // updateView();
        if (customCalendarListener != null) {
            customCalendarListener.onRightButtonClick();
        }
    }

    private void animateTransition(final int direction) {
        final float initialX = 0;
        final float finalX = direction * -getWidth();
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, View.TRANSLATION_X, initialX, finalX);
        animator.setDuration(150);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setTranslationX(0);
                updateView();
                if (customCalendarListener != null) {
                    if (direction == -1) {
                        customCalendarListener.onRightButtonClick();
                    } else {
                        customCalendarListener.onLeftButtonClick();
                    }
                }
            }
        });
    }


    public interface CustomCalendarListener {

        void onDayClick(Date date);

        void onDayLongClick(Date date);

        void onRightButtonClick();

        void onLeftButtonClick();

        void onDateSelectorClick(Date date);

        void onShowTaskOfDayClick(Date date);
    }

}