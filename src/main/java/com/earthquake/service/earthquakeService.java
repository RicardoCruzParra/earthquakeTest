package com.earthquake.service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class earthquakeService
{

    public static String getRestWSResponse(String url)
    {
        JSONObject earthquakeResponse = new JSONObject(url);
        return String.valueOf(earthquakeResponse);
    }
}
