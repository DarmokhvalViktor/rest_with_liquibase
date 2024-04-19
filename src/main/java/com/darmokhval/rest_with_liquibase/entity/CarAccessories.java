package com.darmokhval.rest_with_liquibase.entity;


public enum CarAccessories {
    WINDOW_TINTING("window tinting"),
    FLOOR_MATS("floor mats"),
    SEAT_COVERS("seat covers"),
    DASH_CAM("dash cam"),
    STEERING_WHEEL_COWER("steering wheel cover"),
    USB_CAR_CHARGER("USB car charger"),
    EMERGENCY_KIT("emergency kit"),
    BACKUP_CAMERAS("backup cameras"),
    AIR_COMPRESSOR("air compressor"),
    GPS_NAVIGATOR("GPS navigator"),
    CAR_ALARM("car alarm"),
    REAR_SEAT_ENTERTAINING_SYSTEM("rear seat entertaining system");

    private final String accessory;
    CarAccessories(String accessory) {
        this.accessory = accessory;
    }

    public String getAccessory() {
        return accessory;
    }
}
