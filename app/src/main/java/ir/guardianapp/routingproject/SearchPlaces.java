package ir.guardianapp.routingproject;

public class SearchPlaces {
    private String searchPlaceName;
    private String city;
    private String address;
    private double latitude;
    private double longitude;

    public SearchPlaces(String searchPlaceName, String city, String address, double latitude, double longitude) {
        this.searchPlaceName = searchPlaceName;
        this.city = city;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getSearchPlaceName() {
        return this.searchPlaceName;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
