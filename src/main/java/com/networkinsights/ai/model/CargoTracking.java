package com.networkinsights.ai.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 货物追踪模型
 * 用于Smart Tracking功能，存储货物、船舶、路线和ETA信息
 */
public class CargoTracking {
    
    private String shipmentId;           // 货物编号 (如 #342)
    private String cargoType;            // 货物类型
    private String vesselName;           // 船舶名称
    private String vesselCode;           // 船舶代码
    private String currentLocation;      // 当前位置
    private String currentPort;          // 当前港口
    private String currentPortCode;      // 当前港口代码
    private String destinationPort;      // 目的港口
    private String destinationPortCode;  // 目的港口代码
    private String route;                // 路线描述
    private LocalDateTime estimatedArrival; // 预计到达时间
    private LocalDateTime actualArrival;    // 实际到达时间
    private String status;               // 状态 (In Transit, At Port, Delivered, etc.)
    private String trackingStatus;      // 追踪状态 (On Time, Delayed, Early)
    private Map<String, String> additionalInfo; // 额外信息
    
    // 构造函数
    public CargoTracking() {
        this.additionalInfo = new HashMap<>();
    }
    
    public CargoTracking(String shipmentId, String cargoType, String vesselName) {
        this();
        this.shipmentId = shipmentId;
        this.cargoType = cargoType;
        this.vesselName = vesselName;
    }
    
    // Getters and Setters
    public String getShipmentId() {
        return shipmentId;
    }
    
    public void setShipmentId(String shipmentId) {
        this.shipmentId = shipmentId;
    }
    
    public String getCargoType() {
        return cargoType;
    }
    
    public void setCargoType(String cargoType) {
        this.cargoType = cargoType;
    }
    
    public String getVesselName() {
        return vesselName;
    }
    
    public void setVesselName(String vesselName) {
        this.vesselName = vesselName;
    }
    
    public String getVesselCode() {
        return vesselCode;
    }
    
    public void setVesselCode(String vesselCode) {
        this.vesselCode = vesselCode;
    }
    
    public String getCurrentLocation() {
        return currentLocation;
    }
    
    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }
    
    public String getCurrentPort() {
        return currentPort;
    }
    
    public void setCurrentPort(String currentPort) {
        this.currentPort = currentPort;
    }
    
    public String getCurrentPortCode() {
        return currentPortCode;
    }
    
    public void setCurrentPortCode(String currentPortCode) {
        this.currentPortCode = currentPortCode;
    }
    
    public String getDestinationPort() {
        return destinationPort;
    }
    
    public void setDestinationPort(String destinationPort) {
        this.destinationPort = destinationPort;
    }
    
    public String getDestinationPortCode() {
        return destinationPortCode;
    }
    
    public void setDestinationPortCode(String destinationPortCode) {
        this.destinationPortCode = destinationPortCode;
    }
    
    public String getRoute() {
        return route;
    }
    
    public void setRoute(String route) {
        this.route = route;
    }
    
    public LocalDateTime getEstimatedArrival() {
        return estimatedArrival;
    }
    
    public void setEstimatedArrival(LocalDateTime estimatedArrival) {
        this.estimatedArrival = estimatedArrival;
    }
    
    public LocalDateTime getActualArrival() {
        return actualArrival;
    }
    
    public void setActualArrival(LocalDateTime actualArrival) {
        this.actualArrival = actualArrival;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getTrackingStatus() {
        return trackingStatus;
    }
    
    public void setTrackingStatus(String trackingStatus) {
        this.trackingStatus = trackingStatus;
    }
    
    public Map<String, String> getAdditionalInfo() {
        return additionalInfo;
    }
    
    public void setAdditionalInfo(Map<String, String> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
    
    @Override
    public String toString() {
        return "CargoTracking{" +
                "shipmentId='" + shipmentId + '\'' +
                ", cargoType='" + cargoType + '\'' +
                ", vesselName='" + vesselName + '\'' +
                ", currentLocation='" + currentLocation + '\'' +
                ", destinationPort='" + destinationPort + '\'' +
                ", status='" + status + '\'' +
                ", estimatedArrival=" + estimatedArrival +
                '}';
    }
}
