package us.weather;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WeatherInfoManager {
	private static final Logger log = LoggerFactory.getLogger(WeatherInfoManager.class);
	private static Properties config;
	private String[] keySets;
	private double minLatitude;
	private double maxLatitude;
	private double minLongitude;
	private double maxLongitude;
	private double[] minLongitudeList;
	private double[] maxLongitudeList;
	private double gap;
	private boolean isLastLongitude;

	static {
		try {
			config = new Properties();
			FileInputStream fis = new FileInputStream(WeatherContants.CONFIG_PROPERTIES);
			config.load(new BufferedInputStream(fis));
			fis.close();
		} catch (IOException e) {
		}
	}

	public static void main(String[] args) {
		new WeatherInfoManager().init().gather();
	}

	private WeatherInfoManager init() {
		minLatitude = Double.parseDouble(config.getProperty(WeatherContants.WEATHER_GROUND_MIN_LATITUDE));
		maxLatitude = Double.parseDouble(config.getProperty(WeatherContants.WEATHER_GROUND_MAX_LATITUDE));

		String[] minLongitudeValues = config.getProperty(WeatherContants.WEATHER_GROUND_MIN_LONGITUDE).split(",");

		int i = 0;
		minLongitudeList = new double[minLongitudeValues.length];
		for (String longitude : minLongitudeValues) {
			minLongitudeList[i++] = Double.parseDouble(longitude);
		}

		String keyNumber = config.getProperty(WeatherContants.KEY_CHOICE_NUMBER);
		keySets = config.getProperty(WeatherContants.WEATHER_GROUND_API_KEY_SET + keyNumber).split(",");

		String strArrangeNum = config.getProperty(WeatherContants.WEATHER_GROUND_LONGITUDE_ARRANGE_NUMBER);
		int arangeNum = Integer.parseInt(strArrangeNum);
		if (5 == arangeNum) {
			String[] maxLastLongitudeValues = config.getProperty(WeatherContants.WEATHER_GROUND_MAX_LONGITUDE).split(
					",");

			int j = 0;
			maxLongitudeList = new double[maxLastLongitudeValues.length];
			for (String longitude : maxLastLongitudeValues) {
				maxLongitudeList[j++] = Double.parseDouble(longitude);
			}

			maxLongitude = maxLongitudeList[arangeNum];
			isLastLongitude = true;
		}
		minLongitude = minLongitudeList[arangeNum];

		gap = Double.parseDouble(config.getProperty(WeatherContants.WEATHER_GROUND_LONGITUDE_GAP));
		return this;
	}

	private void gather() {
		int threadCnt = keySets.length;

		double minLat = minLatitude;
		double maxLat = maxLatitude;
		double minLong = minLongitude;
		double maxLong = minLongitude + gap;

		ExecutorService ex = Executors.newFixedThreadPool(threadCnt);
		// for (int i = 0; i < 1; i++) {
		for (int i = 0; i < threadCnt; i++) {
			log.info("Regtangle space :: Min=>(" + minLat + "," + minLong + "), Max=>(" + maxLat + "," + maxLong + ")");
			ex.execute(new JsonParser(config, keySets[i], minLat, minLong, maxLat, maxLong));
			maxLong += gap;
			minLong += gap;

			if (isLastLongitude && i == 3) {
				maxLong = maxLongitude;
			}
		}

		ex.shutdown();
		while (!ex.isTerminated()) {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}
		}
	}
}
