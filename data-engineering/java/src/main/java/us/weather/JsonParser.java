package us.weather;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import date.time.TimeWatch;
import net.indf.djbox.json.Djson;
import net.indf.djbox.json.Var;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


public class JsonParser implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(JsonParser.class);

	private String urlRoot;
	private String apiKey;
	private String urlQuery;
	private String urlCondition1;
	private String urlCondition2;
	private String startDate;
	private String endDate;
	private List<String> dateList;

	private double minLatitude;
	private double minLongitude;
	private double maxLatitude;
	private double maxLongitude;

	private String targetFilePath;
	private String postFix = ".json";

	private double initMinLaitude;
	private double initMinLongitude;
	private double initMaxLaitude;
	private double initMaxLongitude;

	public JsonParser(Properties config, String apiKey, double minLat, double minLong, double maxLat, double maxLong) {
		this.urlRoot = config.getProperty(WeatherContants.WEATHER_GROUND_URL_ROOT);
		this.urlQuery = config.getProperty(WeatherContants.WEATHER_GROUND_URL_QUERY);
		this.urlCondition1 = config.getProperty(WeatherContants.WEATHER_GROUND_URL_QUERY_CONDITION_1);
		this.urlCondition2 = config.getProperty(WeatherContants.WEATHER_GROUND_URL_QUERY_CONDITION_2);

		this.startDate = config.getProperty(WeatherContants.WEATHER_GROUND_URL_QUERY_START_DATE);
		this.endDate = config.getProperty(WeatherContants.WEATHER_GROUND_URL_QUERY_END_DATE);
		this.dateList = getDateList(startDate, endDate);

		this.minLatitude = minLat;
		this.minLongitude = minLong;
		this.maxLatitude = maxLat;
		this.maxLongitude = maxLong;

		this.initMinLaitude = minLat;
		this.initMinLongitude = minLong;
		this.initMaxLaitude = maxLat;
		this.initMaxLongitude = maxLong;

		this.targetFilePath = config.getProperty(WeatherContants.TARGET_FILE_ENTRY_PATH);
		this.apiKey = "/" + apiKey;
	}

	public JsonParser() {
	}

	private List<String> getDateList(String startDate, String endDate) {
		List<String> dateList = new ArrayList<String>();
		Integer startNumDay = Integer.valueOf(getDay(startDate));
		Integer startNumMonth = Integer.valueOf(getMonth(startDate));
		Integer startNumYear = Integer.valueOf(getYear(startDate));
		Calendar calStartDate = Calendar.getInstance();
		calStartDate.set(startNumYear, startNumMonth - 1, startNumDay);

		Integer endNumDay = Integer.valueOf(getDay(endDate));
		Integer endNumMonth = Integer.valueOf(getMonth(endDate));
		Integer endNumYear = Integer.valueOf(getYear(endDate));
		Calendar calEndDate = Calendar.getInstance();
		calEndDate.set(endNumYear, endNumMonth - 1, endNumDay);

		long dayMills = 86400000;
		for (long startMills = calStartDate.getTimeInMillis(), endMills = calEndDate.getTimeInMillis(); startMills <= endMills;) {
			String strYear = String.valueOf(calStartDate.get(Calendar.YEAR));

			int monthNum = (calStartDate.get(Calendar.MONTH) + 1);
			String strMonth = null;
			if (monthNum < 10) {
				strMonth = "0" + String.valueOf(monthNum);
			} else {
				strMonth = String.valueOf(monthNum);
			}

			int dateNum = calStartDate.get(Calendar.DATE);
			String strDate = null;
			if (dateNum < 10) {
				strDate = "0" + String.valueOf(dateNum);
			} else {
				strDate = String.valueOf(dateNum);
			}

			dateList.add(strYear + strMonth + strDate);
			startMills += dayMills;
			calStartDate.setTimeInMillis(startMills);
		}
		return dateList;
	}

	private String getDay(String startDate) {
		return startDate.substring(6, startDate.length());
	}

	private String getMonth(String startDate) {
		return startDate.substring(4, 6);
	}

	private String getYear(String startDate) {
		return startDate.substring(0, 4);
	}

	@Override
	public void run() {
		parseJson();
		log.info("Json parsing is complete.!!!!");
	}

	private String targetDate;

	private void parseJson() {
		double initLog = minLongitude;

		try {
			for (int i = 0, size = dateList.size(); i < size; i++) {
				targetDate = dateList.get(i);
				TimeWatch tw = TimeWatch.start();
				log.info("###### {} day's Min=>({},{}), Max=({},{}) :: gathering is stated !!", dateList.get(i),
						minLatitude, minLongitude, maxLatitude, maxLongitude);
				while (true) {
					String url = buildUrl(targetDate, minLatitude, minLongitude);
					log.info(url);
					String json = getJson(url);
					if (!Strings.isNullOrEmpty(json)) {
						CsvInfo[] parses = parse(json);
						// System.out.println("=============> " + taretDate);
						// CsvInfo[] parses = parse();
						if (parses != null) {
							makeCsv(parses[0]);
							makeCsv(parses[1]);
						}
					}

					minLongitude += 0.1;
					if (minLongitude > maxLongitude) {
						minLatitude += 0.1;
						minLongitude = initLog;
					}
					if (minLatitude > maxLatitude) {
						break;
					}

					// minLongitude += 1;
					// if (minLongitude > maxLongitude) {
					// minLatitude += 1;
					// minLongitude = initLog;
					// }
					// if (minLatitude > maxLatitude) {
					// break;
					// }
				}
				log.info("###### {} day => {} hours is elapsed!!", targetDate, tw.time(TimeUnit.HOURS));
				this.minLatitude = initMinLaitude;
				this.minLongitude = initMinLongitude;
				this.maxLatitude = initMaxLaitude;
				this.maxLongitude = initMaxLongitude;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private String strMinLat = null;
	private String strMinLong = null;

	private String buildUrl(String date, double minLat, double minLong) {
		DecimalFormat format = new DecimalFormat(".#");
		strMinLat = format.format(minLat);
		strMinLong = format.format(minLong);

		String latLon = strMinLat + "," + strMinLong + postFix;
		String buildUrl = urlRoot + apiKey + urlCondition1 + urlCondition2 + date + urlQuery + latLon;
		return buildUrl;
	}

	private String getJson(String url) {
		BufferedReader br = null;

		try {
			br = new BufferedReader(new InputStreamReader(new URL(url).openConnection().getInputStream()));
			String readLine = null;
			StringBuffer json = new StringBuffer(1024);
			while ((readLine = br.readLine()) != null) {
				json.append(readLine);
			}

			return json.toString();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				br.close();
				br = null;
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}

		return null;
	}

	private CsvInfo[] parse(String json) {
		// private CsvInfo[] parse() {
		String fileNameDate = null;
		String queryLatLon = null;
		StringBuffer dailySummary = null;

		StringBuffer sb = new StringBuffer(1024);
		sb.append(strMinLat).append(",").append(strMinLong).append(",");

		// Daily Summary
		CsvInfo hourlyInfo = null;
		CsvInfo summaryInfo = null;

		Var var = null;
		try {
			// var = Djson.parse(new File("d:/37.8,-122.4.json"));
			var = Djson.parse(json);

			// display_location
			String disCountry = var.find("current_observation.display_location.country").toString().trim();
			disCountry = Strings.isNullOrEmpty(disCountry) ? "empty" : disCountry;
			if (!disCountry.equalsIgnoreCase("US")) {
				log.info("This counry => " + disCountry + ", Just skip...");
				return null;
			}

			Var disState = var.find("current_observation.display_location.state_name");
			Var disStateAbb = var.find("current_observation.display_location.state");

			String disCity = var.find("current_observation.display_location.city").toString();
			if (disCity.contains(",")) {
				disCity = disCity.replace(",", " ");
			}

			Var disLatitude = var.find("current_observation.display_location.latitude");
			Var disLongitude = var.find("current_observation.display_location.longitude");
			Var disElvation = var.find("current_observation.display_location.elevation");
			Var disZip = var.find("current_observation.display_location.zip");

			sb.append(disCountry).append(",").append(disState).append(",").append(disStateAbb).append(",")
					.append(disCity).append(",").append(disZip).append(",").append(disLatitude).append(",")
					.append(disLongitude).append(",").append(disElvation).append(",");

			// observation_location
			String observCountry = var.find("current_observation.observation_location.country").toString().trim();
			observCountry = Strings.isNullOrEmpty(observCountry) ? "empty" : observCountry;
			if (!observCountry.equalsIgnoreCase("US")) {
				log.info("This counry => " + observCountry + ", Just skip...");
				return null;
			}

			Var observState = var.find("current_observation.observation_location.state");

			String observCity = var.find("current_observation.observation_location.city").toString();
			if (observCity.contains(",")) {
				observCity = observCity.replace(",", "");
			}
			Var observLatitude = var.find("current_observation.observation_location.latitude");
			Var observLongitude = var.find("current_observation.observation_location.longitude");
			Var observElvation = var.find("current_observation.observation_location.elevation");
			Var stationId = var.find("current_observation.station_id");

			sb.append(observCountry).append(",").append(observState).append(",").append(observCity).append(",")
					.append(observLatitude).append(",").append(observLongitude).append(",").append(observElvation)
					.append(",").append(stationId).append(",");

			String mainInfo = sb.toString();
			StringBuffer hourly = new StringBuffer(1024);

			String fileNameYear = var.find("history.observations").get(0).get("date").find("year").toString();
			String fileNameMon = var.find("history.observations").get(0).get("date").find("mon").toString();
			String fileNameDay = var.find("history.observations").get(0).get("date").find("mday").toString();
			fileNameDate = fileNameYear + fileNameMon + fileNameDay;
			queryLatLon = strMinLat + "," + strMinLong;// 37.8,-122.4";

			// history(hourly)
			DateTime dateTime = null;
			DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
			Var observations = var.find("history.observations");
			if (observations.size() >= 1) {
				for (int i = 0, size = observations.size(); i < size; i++) {
					hourly.append(mainInfo);

					int dateYear = observations.get(i).get("date").find("year").toInt();
					int dateMon = observations.get(i).get("date").find("mon").toInt();
					int dateMday = observations.get(i).get("date").find("mday").toInt();
					int dateHour = observations.get(i).get("date").find("hour").toInt();
					int dateMin = observations.get(i).get("date").find("min").toInt();

					String dateTzname = observations.get(i).get("date").find("tzname").toString();

					dateTime = new DateTime(dateYear, dateMon, dateMday, dateHour, dateMin,
							DateTimeZone.forID(dateTzname));
					String strDateTime = dateTime.toString(fmt);
					hourly.append(strDateTime).append(",").append(dateTzname).append(",");

					int utcDateYear = observations.get(i).get("utcdate").find("year").toInt();
					int utcDateMon = observations.get(i).get("utcdate").find("mon").toInt();
					int utcDateMday = observations.get(i).get("utcdate").find("mday").toInt();
					int utcDateHour = observations.get(i).get("utcdate").find("hour").toInt();
					int utcDateMin = observations.get(i).get("utcdate").find("min").toInt();
					String utcDateTzname = observations.get(i).get("utcdate").find("tzname").toString();

					dateTime = new DateTime(utcDateYear, utcDateMon, utcDateMday, utcDateHour, utcDateMin,
							DateTimeZone.forID(utcDateTzname));
					String strUtcDateTime = dateTime.toString(fmt);
					hourly.append(strUtcDateTime).append(",").append(utcDateTzname).append(",");

					Var tempm = observations.get(i).get("tempm");
					hourly.append(tempm).append(",");

					Var tempi = observations.get(i).get("tempi");
					hourly.append(tempi).append(",");

					Var dewptm = observations.get(i).get("dewptm");
					hourly.append(dewptm).append(",");

					Var dewpti = observations.get(i).get("dewpti");
					hourly.append(dewpti).append(",");

					Var hum = observations.get(i).get("hum");
					hourly.append(hum).append(",");

					Var wspdm = observations.get(i).get("wspdm");
					hourly.append(wspdm).append(",");

					Var wspdi = observations.get(i).get("wspdi");
					hourly.append(wspdi).append(",");

					Var wgustm = observations.get(i).get("wgustm");
					hourly.append(wgustm).append(",");

					Var wgusti = observations.get(i).get("wgusti");
					hourly.append(wgusti).append(",");

					Var wdird = observations.get(i).get("wdird");
					hourly.append(wdird).append(",");

					Var wdire = observations.get(i).get("wdire");
					hourly.append(wdire).append(",");

					Var vism = observations.get(i).get("vism");
					hourly.append(vism).append(",");

					Var visi = observations.get(i).get("visi");
					hourly.append(visi).append(",");

					Var pressurem = observations.get(i).get("pressurem");
					hourly.append(pressurem).append(",");

					Var pressurei = observations.get(i).get("pressurei");
					hourly.append(pressurei).append(",");

					Var windchillm = observations.get(i).get("windchillm");
					hourly.append(windchillm).append(",");

					Var windchilli = observations.get(i).get("windchilli");
					hourly.append(windchilli).append(",");

					Var heatindexm = observations.get(i).get("heatindexm");
					hourly.append(heatindexm).append(",");

					Var heatindexi = observations.get(i).get("heatindexi");
					hourly.append(heatindexi).append(",");

					Var precipm = observations.get(i).get("precipm");
					hourly.append(precipm).append(",");

					Var precipi = observations.get(i).get("precipi");
					hourly.append(precipi).append(",");

					Var conds = observations.get(i).get("conds");
					hourly.append(conds).append(",");

					Var fog = observations.get(i).get("fog");
					hourly.append(fog).append(",");

					Var rain = observations.get(i).get("rain");
					hourly.append(rain).append(",");

					Var snow = observations.get(i).get("snow");
					hourly.append(snow).append(",");

					Var hail = observations.get(i).get("hail");
					hourly.append(hail).append(",");

					Var thunder = observations.get(i).get("thunder");
					hourly.append(thunder).append(",");

					Var tornado = observations.get(i).get("tornado");
					hourly.append(tornado).append("\n");
				}
			}

			hourlyInfo = new CsvInfo.Get().csvContent(hourly.toString()).dateTime(fileNameDate)
					.queryLatLon(queryLatLon).build();

			dailySummary = new StringBuffer(1024);
			Var dailySummaryDateHour = var.find("history.dailysummary");
			for (int i = 0, size = dailySummaryDateHour.size(); i < size; i++) {
				dailySummary.append(mainInfo);
				int summaryYear = dailySummaryDateHour.get(i).get("date").get("year").toInt();
				int summaryMon = dailySummaryDateHour.get(i).get("date").get("mon").toInt();
				int summaryMday = dailySummaryDateHour.get(i).get("date").get("mday").toInt();
				int summaryHour = dailySummaryDateHour.get(i).get("date").get("hour").toInt();
				int summaryMin = dailySummaryDateHour.get(i).get("date").get("min").toInt();
				String summaryDateTzname = observations.get(i).get("date").find("tzname").toString();

				dateTime = new DateTime(summaryYear, summaryMon, summaryMday, summaryHour, summaryMin,
						DateTimeZone.forID(summaryDateTzname));
				String summaryDateTime = dateTime.toString(fmt);
				dailySummary.append(summaryDateTime).append(",").append(summaryDateTzname).append(",");

				Var fog = dailySummaryDateHour.get(i).get("fog");
				dailySummary.append(fog).append(",");

				Var rain = dailySummaryDateHour.get(i).get("rain");
				dailySummary.append(rain).append(",");

				Var snow = dailySummaryDateHour.get(i).get("snow");
				dailySummary.append(snow).append(",");

				Var snowfallm = dailySummaryDateHour.get(i).get("snowfallm");
				dailySummary.append(snowfallm).append(",");

				Var snowfalli = dailySummaryDateHour.get(i).get("snowfalli");
				dailySummary.append(snowfalli).append(",");

				Var monthtodatesnowfallm = dailySummaryDateHour.get(i).get("monthtodatesnowfallm");
				dailySummary.append(monthtodatesnowfallm).append(",");

				Var monthtodatesnowfalli = dailySummaryDateHour.get(i).get("monthtodatesnowfalli");
				dailySummary.append(monthtodatesnowfalli).append(",");

				Var since1julsnowfallm = dailySummaryDateHour.get(i).get("since1julsnowfallm");
				dailySummary.append(since1julsnowfallm).append(",");

				Var since1julsnowfalli = dailySummaryDateHour.get(i).get("since1julsnowfalli");
				dailySummary.append(since1julsnowfalli).append(",");

				Var snowdepthm = dailySummaryDateHour.get(i).get("snowdepthm");
				dailySummary.append(snowdepthm).append(",");

				Var snowdepthi = dailySummaryDateHour.get(i).get("snowdepthi");
				dailySummary.append(snowdepthi).append(",");

				Var hail = dailySummaryDateHour.get(i).get("hail");
				dailySummary.append(hail).append(",");

				Var thunder = dailySummaryDateHour.get(i).get("thunder");
				dailySummary.append(thunder).append(",");

				Var tornado = dailySummaryDateHour.get(i).get("tornado");
				dailySummary.append(tornado).append(",");

				Var meantempm = dailySummaryDateHour.get(i).get("meantempm");
				dailySummary.append(meantempm).append(",");

				Var meantempi = dailySummaryDateHour.get(i).get("meantempi");
				dailySummary.append(meantempi).append(",");

				Var meandewptm = dailySummaryDateHour.get(i).get("meandewptm");
				dailySummary.append(meandewptm).append(",");

				Var meandewpti = dailySummaryDateHour.get(i).get("meandewpti");
				dailySummary.append(meandewpti).append(",");

				Var meanpressurem = dailySummaryDateHour.get(i).get("meanpressurem");
				dailySummary.append(meanpressurem).append(",");

				Var meanpressurei = dailySummaryDateHour.get(i).get("meanpressurei");
				dailySummary.append(meanpressurei).append(",");

				Var meanwindspdm = dailySummaryDateHour.get(i).get("meanwindspdm");
				dailySummary.append(meanwindspdm).append(",");

				Var meanwindspdi = dailySummaryDateHour.get(i).get("meanwindspdi");
				dailySummary.append(meanwindspdi).append(",");

				Var meanwdire = dailySummaryDateHour.get(i).get("meanwdire");
				dailySummary.append(meanwdire).append(",");

				Var meanwdird = dailySummaryDateHour.get(i).get("meanwdird");
				dailySummary.append(meanwdird).append(",");

				Var meanvism = dailySummaryDateHour.get(i).get("meanvism");
				dailySummary.append(meanvism).append(",");

				Var meanvisi = dailySummaryDateHour.get(i).get("meanvisi");
				dailySummary.append(meanvisi).append(",");

				Var humidity = dailySummaryDateHour.get(i).get("humidity");
				dailySummary.append(humidity).append(",");

				Var maxtempm = dailySummaryDateHour.get(i).get("maxtempm");
				dailySummary.append(maxtempm).append(",");

				Var maxtempi = dailySummaryDateHour.get(i).get("maxtempi");
				dailySummary.append(maxtempi).append(",");

				Var mintempm = dailySummaryDateHour.get(i).get("mintempm");
				dailySummary.append(mintempm).append(",");

				Var mintempi = dailySummaryDateHour.get(i).get("mintempi");
				dailySummary.append(mintempi).append(",");

				Var maxhumidity = dailySummaryDateHour.get(i).get("maxhumidity");
				dailySummary.append(maxhumidity).append(",");

				Var minhumidity = dailySummaryDateHour.get(i).get("minhumidity");
				dailySummary.append(minhumidity).append(",");

				Var maxdewptm = dailySummaryDateHour.get(i).get("maxdewptm");
				dailySummary.append(maxdewptm).append(",");

				Var maxdewpti = dailySummaryDateHour.get(i).get("maxdewpti");
				dailySummary.append(maxdewpti).append(",");

				Var mindewptm = dailySummaryDateHour.get(i).get("mindewptm");
				dailySummary.append(mindewptm).append(",");

				Var mindewpti = dailySummaryDateHour.get(i).get("mindewpti");
				dailySummary.append(mindewpti).append(",");

				Var maxpressurem = dailySummaryDateHour.get(i).get("maxpressurem");
				dailySummary.append(maxpressurem).append(",");

				Var maxpressurei = dailySummaryDateHour.get(i).get("maxpressurei");
				dailySummary.append(maxpressurei).append(",");

				Var minpressurem = dailySummaryDateHour.get(i).get("minpressurem");
				dailySummary.append(minpressurem).append(",");

				Var minpressurei = dailySummaryDateHour.get(i).get("minpressurei");
				dailySummary.append(minpressurei).append(",");

				Var maxwspdm = dailySummaryDateHour.get(i).get("maxwspdm");
				dailySummary.append(maxwspdm).append(",");

				Var maxwspdi = dailySummaryDateHour.get(i).get("maxwspdi");
				dailySummary.append(maxwspdi).append(",");

				Var minwspdm = dailySummaryDateHour.get(i).get("minwspdm");
				dailySummary.append(minwspdm).append(",");

				Var minwspdi = dailySummaryDateHour.get(i).get("minwspdi");
				dailySummary.append(minwspdi).append(",");

				Var maxvism = dailySummaryDateHour.get(i).get("maxvism");
				dailySummary.append(maxvism).append(",");

				Var maxvisi = dailySummaryDateHour.get(i).get("maxvisi");
				dailySummary.append(maxvisi).append(",");

				Var minvism = dailySummaryDateHour.get(i).get("minvism");
				dailySummary.append(minvism).append(",");

				Var minvisi = dailySummaryDateHour.get(i).get("minvisi");
				dailySummary.append(minvisi).append(",");

				Var gdegreedays = dailySummaryDateHour.get(i).get("gdegreedays");
				dailySummary.append(gdegreedays).append(",");

				Var heatingdegreedays = dailySummaryDateHour.get(i).get("heatingdegreedays");
				dailySummary.append(heatingdegreedays).append(",");

				Var coolingdegreedays = dailySummaryDateHour.get(i).get("coolingdegreedays");
				dailySummary.append(coolingdegreedays).append(",");

				Var precipm = dailySummaryDateHour.get(i).get("precipm");
				dailySummary.append(precipm).append(",");

				Var precipi = dailySummaryDateHour.get(i).get("precipi");
				dailySummary.append(precipi).append(",");

				Var precipsource = dailySummaryDateHour.get(i).get("precipsource");
				dailySummary.append(precipsource).append(",");

				Var heatingdegreedaysnormal = dailySummaryDateHour.get(i).get("heatingdegreedaysnormal");
				dailySummary.append(heatingdegreedaysnormal).append(",");

				Var monthtodateheatingdegreedays = dailySummaryDateHour.get(i).get("monthtodateheatingdegreedays");
				dailySummary.append(monthtodateheatingdegreedays).append(",");

				Var monthtodateheatingdegreedaysnormal = dailySummaryDateHour.get(i).get(
						"monthtodateheatingdegreedaysnormal");

				dailySummary.append(monthtodateheatingdegreedaysnormal).append(",");
				Var since1sepheatingdegreedays = dailySummaryDateHour.get(i).get("since1sepheatingdegreedays");
				dailySummary.append(since1sepheatingdegreedays).append(",");

				Var since1sepheatingdegreedaysnormal = dailySummaryDateHour.get(i).get(
						"since1sepheatingdegreedaysnormal");
				dailySummary.append(since1sepheatingdegreedaysnormal).append(",");

				Var since1julheatingdegreedays = dailySummaryDateHour.get(i).get("since1julheatingdegreedays");
				dailySummary.append(since1julheatingdegreedays).append(",");

				Var since1julheatingdegreedaysnormal = dailySummaryDateHour.get(i).get(
						"since1julheatingdegreedaysnormal");
				dailySummary.append(since1julheatingdegreedaysnormal).append(",");

				Var coolingdegreedaysnormal = dailySummaryDateHour.get(i).get("coolingdegreedaysnormal");
				dailySummary.append(coolingdegreedaysnormal).append(",");

				Var monthtodatecoolingdegreedays = dailySummaryDateHour.get(i).get("monthtodatecoolingdegreedays");
				dailySummary.append(monthtodatecoolingdegreedays).append(",");

				Var monthtodatecoolingdegreedaysnormal = dailySummaryDateHour.get(i).get(
						"monthtodatecoolingdegreedaysnormal");

				dailySummary.append(monthtodatecoolingdegreedaysnormal).append(",");
				Var since1sepcoolingdegreedays = dailySummaryDateHour.get(i).get("since1sepcoolingdegreedays");
				dailySummary.append(since1sepcoolingdegreedays).append(",");

				Var since1sepcoolingdegreedaysnormal = dailySummaryDateHour.get(i).get(
						"since1sepcoolingdegreedaysnormal");
				dailySummary.append(since1sepcoolingdegreedaysnormal).append(",");

				Var since1jancoolingdegreedays = dailySummaryDateHour.get(i).get("since1jancoolingdegreedays");
				dailySummary.append(since1jancoolingdegreedays).append(",");

				Var since1jancoolingdegreedaysnormal = dailySummaryDateHour.get(i).get(
						"since1jancoolingdegreedaysnormal");
				dailySummary.append(since1jancoolingdegreedaysnormal).append("\n");
			}
			summaryInfo = new CsvInfo.Get().csvContent(dailySummary.toString()).dateTime(fileNameDate)
					.queryLatLon(queryLatLon).isSummary(true).build();
		} catch (Exception e) {
			String errorType;
			String desc;
			try {
				errorType = var.find("response.error.type").toString();
				desc = var.find("response.error.description").toString();
				log.info("==> (" + strMinLat + "," + strMinLong + ") :: " + e.getMessage() + " :: " + desc);
				if ("invalidkey".equalsIgnoreCase(errorType)) {
					log.info("==> (" + strMinLat + "," + strMinLong + ") :: " + e.getMessage() + " :: " + desc);
					TimeUnit.HOURS.sleep(1);
				}
			} catch (Exception e1) {
			}
			return null;
		}

		return new CsvInfo[] { hourlyInfo, summaryInfo };
	}

	private void makeCsv(CsvInfo parse) {
		String dateTime = targetDate;
		// String dateTime = parse.getDateTime();
		String year = dateTime.substring(0, 4);
		String month = dateTime.substring(4, 6);
		String day = dateTime.substring(6, dateTime.length());

		String dir = targetFilePath + year + "/" + month + "/" + day + "/";

		log.info("Target directory => {}", dir);
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
		}

		String csvContent = parse.getCsvContent();
		String fileName = dir + parse.toString();
		log.info("Target file name => {}", fileName);
		try {
			File newFile = new File(fileName);
			if (newFile.exists()) {
				log.info("File append => {}", fileName);
				Files.append(csvContent, newFile, Charsets.UTF_8);
				return;
			}

			Files.write(csvContent.getBytes(), new File(fileName));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void main(String[] args) {
		// new JsonParser().parse();
	}

}
