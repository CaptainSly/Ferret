package io.azraein.ferret.system.calendar;

public class Holiday {

	private String holidayName;
	private int holidayDay, holidayMonth;

	public Holiday(String holidayName, int holidayDay, int holidayMonth) {
		this.holidayName = holidayName;
		this.holidayDay = holidayDay;
		this.holidayMonth = holidayMonth;
	}

	public String getHolidayName() {
		return holidayName;
	}

	public int getHolidayDay() {
		return holidayDay;
	}

	public int getHolidayMonth() {
		return holidayMonth;
	}

}
