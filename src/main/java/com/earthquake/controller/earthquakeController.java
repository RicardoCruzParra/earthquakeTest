package com.earthquake.controller;

import com.earthquake.model.h2Connection;
import com.earthquake.service.earthquakeService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@RestController
@RequestMapping("/earthquakes/")
public class earthquakeController
{
    @PostMapping("/getEarthquakesByDate")
    @ResponseBody
    public ResponseEntity<String> getEarthquakesByDate(@RequestBody String data)
    {
        JSONObject json = new JSONObject(data);
        System.out.println(json.toString(5));

        String url = "https://earthquake.usgs.gov/fdsnws/event/1/query?" +
                "format=geojson&" +
                "starttime=" + json.get("starttime").toString() + "&" +
                "endtime=" + json.get("endtime").toString();

        System.out.println(url);

        String earthquakeData = earthquakeService.getRestWSResponse(url);

        System.out.println(json.get("starttime"));

        return ResponseEntity.ok(earthquakeData);
    }

    @PostMapping("/getEarthquakesByMagnitude")
    @ResponseBody
    public ResponseEntity<String> getEarthquakesByMagnitude(@RequestBody String data)
    {
        JSONObject json = new JSONObject(data);
        System.out.println(json.toString(5));

        String url = "https://earthquake.usgs.gov/fdsnws/event/1/query?" +
                "format=geojson&" +
                "minmagnitude=" + json.get("minMagnitude").toString() + "&" +
                "maxmagnitude=" + json.get("maxMagnitude").toString();

        System.out.println(url);

        String earthquakeData = earthquakeService.getRestWSResponse(url);

        return ResponseEntity.ok(earthquakeData);
    }

    @GetMapping("/storeTodayEarthquakes")
    @ResponseBody
    public ResponseEntity<String> storeTodayEarthquakesInH2(@RequestBody String data) {
        try
        {
            JSONObject json = new JSONObject(data);
            System.out.println(json.toString(5));
            Date date = Calendar.getInstance().getTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
            String actualDate = dateFormat.format(date);

            String url = "https://earthquake.usgs.gov/fdsnws/event/1/query?" +
                    "format=geojson&" +
                    "starttime=" + actualDate;

            System.out.println(url);

            JSONArray jsonArray = (JSONArray) json.get("features");
            Connection con = h2Connection.getConnection();
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO Earthquake VALUES (?, ?, ?, ?, ? )");
            for (Object object : jsonArray) {
                JSONObject record = (JSONObject) object;
                String id = (String) record.get("id");
                String ubicacion = (String) record.get("place");
                String status = (String) record.get("status");
                String tipo = (String) record.get("type");
                String titulo = (String) record.get("title");
                pstmt.setString(1, id);
                pstmt.setString(2, ubicacion);
                pstmt.setString(3, status);
                pstmt.setString(4, tipo);
                pstmt.setString(5, titulo);
                pstmt.executeUpdate();
            }
            System.out.println("Records saved..");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return ResponseEntity.ok("OK.");
    }
}
