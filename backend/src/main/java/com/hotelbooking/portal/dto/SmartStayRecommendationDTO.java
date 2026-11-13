package com.hotelbooking.portal.dto;

/**
 * DTO representing the "Smart Stay Recommendation" result,
 * featuring the top-value hotel with score explanation.
 */
public class SmartStayRecommendationDTO {

    private HotelSummaryDTO hotel;
    private double valueScore;
    private String recommendationBadge;
    private String reason;
    private int availableRooms;
    private int amenitiesCount;

    public SmartStayRecommendationDTO() {
    }

    public SmartStayRecommendationDTO(HotelSummaryDTO hotel, double valueScore,
                                      String recommendationBadge, String reason,
                                      int availableRooms, int amenitiesCount) {
        this.hotel = hotel;
        this.valueScore = valueScore;
        this.recommendationBadge = recommendationBadge;
        this.reason = reason;
        this.availableRooms = availableRooms;
        this.amenitiesCount = amenitiesCount;
    }

    public HotelSummaryDTO getHotel() {
        return hotel;
    }

    public void setHotel(HotelSummaryDTO hotel) {
        this.hotel = hotel;
    }

    public double getValueScore() {
        return valueScore;
    }

    public void setValueScore(double valueScore) {
        this.valueScore = valueScore;
    }

    public String getRecommendationBadge() {
        return recommendationBadge;
    }

    public void setRecommendationBadge(String recommendationBadge) {
        this.recommendationBadge = recommendationBadge;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getAvailableRooms() {
        return availableRooms;
    }

    public void setAvailableRooms(int availableRooms) {
        this.availableRooms = availableRooms;
    }

    public int getAmenitiesCount() {
        return amenitiesCount;
    }

    public void setAmenitiesCount(int amenitiesCount) {
        this.amenitiesCount = amenitiesCount;
    }
}
