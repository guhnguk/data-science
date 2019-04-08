package us.weather;

public class CsvInfo {
	public final static String hourlyFix = "hourly";
	public final static String summaryFix = "summary";

	private boolean isSummary;
	private String csvContent;
	private String dateTime;
	private String queryLatLon;

	public static class Get {
		private boolean isSummary;
		private String csvContent;
		private String dateTime;
		private String queryLatLon;

		public Get isSummary(boolean isSummary) {
			this.isSummary = isSummary;
			return this;
		}

		public Get csvContent(String csvContent) {
			this.csvContent = csvContent;
			return this;
		}

		public Get dateTime(String dateTime) {
			this.dateTime = dateTime;
			return this;
		}

		public Get queryLatLon(String queryLatLon) {
			this.queryLatLon = queryLatLon;
			return this;
		}

		public CsvInfo build() {
			return new CsvInfo(this);
		}
	}

	private CsvInfo(Get build) {
		this.isSummary = build.isSummary;
		this.csvContent = build.csvContent;
		this.dateTime = build.dateTime;
		this.queryLatLon = build.queryLatLon;
	}

	@Override
	public String toString() {
		// example file name
		// 2012_10_23_houry.csv
		String fileName = null;
		if (!isSummary) {
			fileName = dateTime + "_" + hourlyFix + ".csv";
			return fileName;
		}
		fileName = dateTime + "_" + summaryFix + ".csv";
		return fileName;
	}

	public static String getHourlyfix() {
		return hourlyFix;
	}

	public static String getSummaryfix() {
		return summaryFix;
	}

	public boolean isSummary() {
		return isSummary;
	}

	public String getCsvContent() {
		return csvContent;
	}

	public String getDateTime() {
		return dateTime;
	}

	public String getQueryLatLon() {
		return queryLatLon;
	}
}
