package com.example.a15521.myweatherforecast.entity;

/**
 * Created by 15521 on 2018/5/19.
 */

//生活指数数据
public class LifeSuggestionDate {

    private String airCondition;    //空气状况
    private String sport;   //运动
    private String traffic; //交通
    private String travel;  //旅游
    private String umbrella;    //是否带伞
    private String uv;  //紫外线状况
    //以下是详细信息

    public String getAirConditionDetails() {
        return airConditionDetails;
    }

    public void setAirConditionDetails(String airConditionDetails) {
        this.airConditionDetails = airConditionDetails;
    }

    public String getSportDetails() {
        return sportDetails;
    }

    public void setSportDetails(String sportDetails) {
        this.sportDetails = sportDetails;
    }

    public String getTrafficDetails() {
        return trafficDetails;
    }

    public void setTrafficDetails(String trafficDetails) {
        this.trafficDetails = trafficDetails;
    }

    public String getTravelDetails() {
        return travelDetails;
    }

    public void setTravelDetails(String travelDetails) {
        this.travelDetails = travelDetails;
    }

    public String getUmbrellaDetails() {
        return umbrellaDetails;
    }

    public void setUmbrellaDetails(String umbrellaDetails) {
        this.umbrellaDetails = umbrellaDetails;
    }

    public String getUvDetails() {
        return uvDetails;
    }

    public void setUvDetails(String uvDetails) {
        this.uvDetails = uvDetails;
    }

    private String airConditionDetails;    //空气状况
    private String sportDetails;   //运动
    private String trafficDetails; //交通
    private String travelDetails;  //旅游
    private String umbrellaDetails;    //是否带伞
    private String uvDetails;  //紫外线状况

    public String getAirCondition() {
        return airCondition;
    }

    public void setAirCondition(String airCondition) {
        this.airCondition = airCondition;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getTraffic() {
        return traffic;
    }

    public void setTraffic(String traffic) {
        this.traffic = traffic;
    }

    public String getTravel() {
        return travel;
    }

    public void setTravel(String travel) {
        this.travel = travel;
    }

    public String getUmbrella() {
        return umbrella;
    }

    public void setUmbrella(String umbrella) {
        this.umbrella = umbrella;
    }

    public String getUv() {
        return uv;
    }

    public void setUv(String uv) {
        this.uv = uv;
    }
}
