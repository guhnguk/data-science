package us.weather;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.fail;

public class WeatherInfoTest {

	private WeatherInfoManager info;

	@Before
	public void setUp() throws Exception {
		info = new WeatherInfoManager();
	}

	@Test
	public void testGetJsonData() throws Exception {
		int maxCnt = 500;
		int cnt = 0;
		for (int i = 0; i < 20; i++) {
			String url = "http://api.wunderground.com/api/470d8cd8aeb8dbae/conditions/history_20121001/q/37.8,-122.4.json";
			BufferedReader br = null;

			try {
				br = new BufferedReader(new InputStreamReader(new URL(url).openConnection().getInputStream()));
				String readLine = null;
				StringBuffer sb = new StringBuffer(1024);
				while ((readLine = br.readLine()) != null) {
					sb.append(readLine);
				}
				System.out.println(sb.toString());
			} catch (Exception e) {
				fail(e.getMessage());
			} finally {
				try {
					br.close();
					br = null;
				} catch (IOException e) {
				}
			}

			TimeUnit.MILLISECONDS.sleep(170);
			cnt++;
			if (cnt > 500) {
				TimeUnit.DAYS.sleep(1);
			}
		}
	}

	@Test
	public void testName() throws Exception {
		WeatherInfoManager.main(new String[0]);

		// String buildUrl = info.buildUrl();
		// String json = info.getJson(buildUrl);

		// Var var = Djson.parse(json);
		//
		// // observation
		// Var observationCity = var.find("current_observation.observation_location.city");
		// Var observationState = var.find("current_observation.observation_location.state");
		// Var observationCountry = var.find("current_observation.observation_location.country");
		// Var observationLatitude = var.find("current_observation.observation_location.latitude");
		// Var observationLongitude = var.find("current_observation.observation_location.longitude");
		// Var observationElvation = var.find("current_observation.observation_location.elevation");
		//
		// // history date
		// int dateYear = var.find("history.date.year").toInt();
		// int dateMon = var.find("history.date.mon").toInt();
		// int dateMday = var.find("history.date.mday").toInt();
		// int dateHour = var.find("history.date.hour").toInt();
		// int dateMin = var.find("history.date.min").toInt();
		// DateTime dt = new DateTime(dateYear, dateMon, dateMday, dateHour, dateMin,
		// DateTimeZone.forID("America/Los_Angeles"));
		// DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
		// String date = dt.toString(fmt);
		//
		// //
		// Var observations = var.find("history.observations");
		// if (observations.size() >= 1) {
		// for (int i = 0, size = observations.size(); i < size; i++) {
		// System.out.print(observations.get(i).get("date").find("year"));
		// System.out.print(":" + observations.get(i).get("date").find("mon"));
		// System.out.print(":" + observations.get(i).get("date").find("mday"));
		// System.out.print(":" + observations.get(i).get("date").find("hour"));
		// System.out.println(":" + observations.get(i).get("date").find("min"));
		//
		// System.out.println(observations.get(i).get("tempm"));
		// }
		// } else {
		// // logging
		// }
		//
		// Var dailySummaryDateHour = var.find("history.dailysummary");
		// System.out.println(dailySummaryDateHour.size());
		// System.out.println(dailySummaryDateHour.get(0).get("date").get("hour"));
		// System.out.println(dailySummaryDateHour.get(0).get("meanpressurei"));
	}

}
