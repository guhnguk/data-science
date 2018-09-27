package us.city;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import net.indf.djbox.json.Djson;
import net.indf.djbox.json.Var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class Collector {

    private static final Logger log = LoggerFactory.getLogger(Collector.class);

    private static final String filePath = "D:/us-state-county-city.csv.text";
    private static String wsUrl = "http://api.sba.gov/geodata/city_county_links_for_state_of/";

    public static void main(String[] args) {
        Collector collector = new Collector();

        int length = States.states.length;
        for (int i = 0; i < length; i++) {
            String taregtUrl = wsUrl + States.states[i].toLowerCase() + ".json";
            log.info("Invoked url: " + taregtUrl);

            String json = collector.getData(taregtUrl);
            if ("continue".equals(json)) {
                continue;
            }
            collector.parseData(json);
        }
    }

    private void parseData(String json) {
        Var cities = Djson.parse(json);
        StringBuffer sb = new StringBuffer(1024);

        for (int i = 0, size = cities.size(); i < size; i++) {
            String countyName = cities.get(i).get("county_name").toString().trim();
            sb.append(countyName).append(",");
            String featClass = cities.get(i).get("feat_class").toString().trim();
            sb.append(featClass).append(",");
            String featureId = cities.get(i).get("feature_id").toString().trim();
            sb.append(featureId).append(",");
            String fipsClass = cities.get(i).get("fips_class").toString().trim();
            sb.append(fipsClass).append(",");
            String fipsCountyCd = cities.get(i).get("fips_county_cd").toString().trim();
            sb.append(fipsCountyCd).append(",");
            String fullCountyName = cities.get(i).get("full_county_name").toString().trim();
            sb.append(fullCountyName).append(",");
            String cityName = cities.get(i).get("name").toString().trim();
            sb.append(cityName).append(",");
            String latitude = cities.get(i).get("primary_latitude").toString().trim();
            sb.append(latitude).append(",");
            String longitude = cities.get(i).get("primary_longitude").toString().trim();
            sb.append(longitude).append(",");
            String statAbbr = cities.get(i).get("state_abbreviation").toString().trim();
            sb.append(statAbbr).append(",");
            String stateName = cities.get(i).get("state_name").toString().trim();
            sb.append(stateName).append(",");
            String url = cities.get(i).get("url").toString().trim();
            sb.append(url).append("\n");
        }

        makeCsv(sb.toString());
    }

    private void makeCsv(String csvString) {
        try {
            File newFile = new File(filePath);
            if (newFile.exists()) {
                Files.append(csvString, newFile, Charsets.UTF_8);
                return;
            }

            Files.write(csvString.getBytes(), new File(filePath));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

    }

    private String getData(String url) {
        BufferedReader br = null;
        String skipString = null;
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
            if (e.getMessage().indexOf("400 for URL") > 0) {
                skipString = "continue";
            }
        } finally {
            try {
                if (br != null) {
                    br.close();
                    br = null;
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }

            if ("continue".equals(skipString)) {
                return "continue";
            }
        }

        return null;
    }
}
