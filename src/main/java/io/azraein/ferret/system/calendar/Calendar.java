package io.azraein.ferret.system.calendar;

import java.util.ArrayList;
import java.util.List;

import org.tinylog.Logger;

import io.azraein.ferret.system.utilities.Utils;

public class Calendar {

	private final String[] months = { "Month_1", "Month_2", "Month_3", "Month_4" };
	private final String[] days = { "Day_1", "Day_2", "Day_3", "Day_4" };

	private int hours, minutes;
	private int day, week, month, year;

	private int weeksInMonth = 5;

	private int minutesInHour = 60;
	private int hoursInDay = 24;

	private boolean timePaused = false;

	private List<Holiday> holidays;

	private Holiday currentHoliday;
	private boolean todayHoliday = false;

	public Calendar() {
		this.holidays = new ArrayList<>();
		this.hours = 12;
		this.minutes = 0;
		this.day = 1;
		this.week = 1;
		this.month = 1;
		this.year = 0;
	}

	public void update() {
		if (!timePaused) {
			minutes++;
			if (minutes >= minutesInHour) {
				minutes = 0;
				hours++;
				if (hours >= hoursInDay) {
					hours = 0;
					day++;

					// Check to see if there is a holiday today
					for (Holiday holiday : holidays) {
						if (holiday.getHolidayDay() == day && holiday.getHolidayMonth() == month) {
							currentHoliday = holiday;
							todayHoliday = true;
							break;
						} else {
							currentHoliday = null;
							todayHoliday = false;
						}
					}

					if (day > days.length) {
						day = 1;
						week++;
						if (week > weeksInMonth) {
							week = 1;
							month++;
							if (month > months.length) {
								month = 1;
								year++;
							}
						}
					}
				}
			}
		}

	}

	public void pauseTime() {
		timePaused = true;
	}

	public void unpauseTime() {
		timePaused = false;
	}

	/**
	 * Returns the time as a string in either military or regular format.
	 * 
	 * @param militaryTime If true, returns time in military format; otherwise, in
	 *                     regular format.
	 * @return The time as a string.
	 */
	public String getTimeAsString(boolean militaryTime) {
		if (militaryTime) {
			// Military time format
			return String.format("%02d:%02d", hours, minutes);
		} else {
			// Regular time format
			int displayHour = hours % 12;
			displayHour = (displayHour == 0 ? 12 : displayHour); // Convert 0 to 12 for AM/PM format
			String amPm = getAmOrPm();
			return String.format("%02d:%02d %s", displayHour, minutes, amPm);
		}
	}

	public String getDateAsString() {
		String date = String.format("The %s of %s, %s, %d", Utils.getSuffix(day), months[month - 1],
				days[day - 1], year);
		return date;
	}

	public float getNormalizedHourValue() {
		return (float) hours / hoursInDay;
	}

	public String getAmOrPm() {
		return hours < 12 ? "AM" : "PM";
	}

	public int getYear() {
		return year;
	}


	public String getDayString() {
		return days[day - 1];
	}

	public String getMonthString() {
		return months[month - 1];
	}

	public boolean isTodayHoliday() {
		return todayHoliday;
	}

	public int getHours() {
		return hours;
	}

	public int getDay() {
		return day;
	}

	public int getMonth() {
		return month;
	}

	public void setTime(int hours, int minutes, int day, int week, int month) {
		if (isValidTime(hours, minutes) && isValidDate(day, week, month)) {
			this.hours = hours;
			this.minutes = minutes;
			this.day = day;
			this.week = week;
			this.month = month;
		} else {
			Logger.error("Invalid time or date values provided.");
		}
	}

	public void setTime(int hours, int minutes) {
		if (isValidTime(hours, minutes)) {
			this.hours = hours;
			this.minutes = minutes;
		} else
			Logger.error("Invalid time value provided");
	}

	public void setDate(int day, int week, int month) {
		if (isValidDate(day, week, month)) {
			this.day = day;
			this.week = week;
			this.month = month;
		} else
			Logger.error("Invalid date value provided");
	}

	private boolean isValidTime(int hours, int minutes) {
		return (hours >= 0 && hours < 12) && (minutes >= 0 && minutes < 60);
	}

	private boolean isValidDate(int day, int week, int month) {
		return (day >= 1 && day <= 20) && (week >= 1 && week <= 5) && (month >= 1 && month <= 4);
	}

	public List<Holiday> getHolidays() {
		return holidays;
	}

	public Holiday getCurrentHoliday() {
		return currentHoliday;
	}

}