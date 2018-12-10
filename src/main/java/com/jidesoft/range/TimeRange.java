/*
 * @(#)TimeRange.java
 * 
 * 2002 - 2012 JIDE Software Incorporated. All rights reserved.
 * Copyright (c) 2005 - 2012 Catalysoft Limited. All rights reserved.
 */

package com.jidesoft.range;

import com.jidesoft.swing.JideSwingUtilities;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/**
 * Specifies upper and lower bounds for a range of values along a time line.
 *
 * @author Simon White (swhite@catalysoft.com)
 */
public class TimeRange extends AbstractRange<Date> {
    protected Date _min;
    protected Date _max;

    /**
     * The time zone used when formatting time/date strings for output For example, to see new Date(0) formatted as
     * 00:00:00 on Jan 1, 1970 (instead of 01:00 GMT for the UK), set the TimeZone to be TimeZone.getTimeZone("UTC");
     */
    private TimeZone timeZone = TimeZone.getDefault();

    /**
     * Sets a default time range of the whole of today
     */
    public TimeRange() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        long todayLong = today.getTimeInMillis();
        today.add(Calendar.HOUR, 24);
        long tomorrowLong = today.getTimeInMillis();
        _min = new Date(todayLong);
        _max = new Date(tomorrowLong);
    }

    public TimeRange(Calendar from, Calendar to) {
        _min = from.getTime();
        _max = to.getTime();
        assert (from.before(to));
    }

    public TimeRange(Date from, Date to) {
        _min = from;
        _max = to;
    }

    public TimeRange(long from, long to) {
        _min = new Date(from);
        _max = new Date(to);
    }

    /**
     * Constructs a copy of the supplied time range
     * @param timeRange the timeRange to copy
     */
    public TimeRange(TimeRange timeRange) {
        this((long) timeRange.minimum(), (long) timeRange.maximum());
    }

    @Override
    public Range<Date> copy() {
        return new TimeRange(this);
    }

    public void setMin(Date from) {
        Date oldValue = _min;
        _min = from;
        firePropertyChange(PROPERTY_MIN, oldValue, _min);
    }

    public void setMin(long from) {
        Date oldValue = _min;
        _min = new Date(from);
        firePropertyChange(PROPERTY_MIN, oldValue, _min);
    }

    public void adjust(Date lower, Date upper) {
        double size = size(); // save it
        if (lower != null) {
            setMin(lower);
        }
        else if (upper != null) {
            setMin(new Date(upper.getTime() - (long) size));
        }
        if (upper != null) {
            setMax(upper);
        }
        else if (lower != null) {
            setMax(new Date(lower.getTime() + (long) size));
        }
    }

    public void setMax(Date to) {
        Date oldValue = _max;
        _max = to;
        firePropertyChange(PROPERTY_MAX, oldValue, _max);
    }

    public void setMax(long to) {
        Date oldValue = _max;
        _max = new Date(to);
        firePropertyChange(PROPERTY_MAX, oldValue, _max);
    }

    public double minimum() {
        return _min == null ? Double.MIN_VALUE : _min.getTime();
    }

    public double maximum() {
        return _max == null ? Double.MAX_VALUE : _max.getTime();
    }

    public double size() {
        return maximum() - minimum();
    }

    public Date lower() {
        return _min;
    }

    public Date upper() {
        return _max;
    }

    public boolean contains(Date x) {
        if (x == null) {
            return false;
        }
        else {
            long value = x.getTime();
            return value >= minimum() && value <= maximum();
        }
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public Range<Date> createIntermediate(Range<Date> target, double position) {
        double sourceMin = this.minimum();
        double sourceMax = this.maximum();
        double targetMin = target.minimum();
        double targetMax = target.maximum();
        double min = sourceMin + position * (targetMin - sourceMin);
        double max= sourceMax + position * (targetMax - sourceMax);
        return new TimeRange((long) min, (long) max);
    }

    /**
     * Creates a new TimeRange as the union of two existing TimeRanges. The date format of the first time range is
     * retained in the returned result.
     *
     * @param r1 the first TimeRange
     * @param r2 the second TimeRange
     * @return the union of the supplied TimeRanges
     */
    public static TimeRange union(TimeRange r1, TimeRange r2) {
        if (r1 == null) {
            return r2;
        }
        else if (r2 == null) {
            return r1;
        }
        long r1Min = r1._min == null ? Long.MAX_VALUE : r1._min.getTime();
        long r2Min = r2._min == null ? Long.MAX_VALUE : r2._min.getTime();
        long r1Max = r1._max == null ? Long.MIN_VALUE : r1._max.getTime();
        long r2Max = r2._max == null ? Long.MIN_VALUE : r2._max.getTime();

        long min = Math.min(r1Min, r2Min);
        long max = Math.max(r1Max, r2Max);
        return new TimeRange(min, max);
    }


    @Override
    public boolean equals(Object other) {
        if (other instanceof TimeRange) {
            TimeRange otherRange = (TimeRange) other;
            return JideSwingUtilities.equals(_min, otherRange._min) && JideSwingUtilities.equals(_max, otherRange._max);
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (_max == null ? 0 : _max.hashCode()) * 29 + (_min == null ? 0 : _min.hashCode());
    }

    @Override
    public String toString() {
        DateFormat f = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        return String.format("[%s, %s] rounded to [%s, %s]", _min == null ? "null" : f.format(_min), _max == null ? "null" : f.format(_max), _min == null ? "null" : f.format(minimum()), _max == null ? "null" : f.format(maximum()));
    }
}
