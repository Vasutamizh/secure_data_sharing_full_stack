package com.antigravity.securedata.dto;

public class ShareRecordRequest {
    private String recordId;
    private String targetDoctorId;

    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }
    public String getTargetDoctorId() { return targetDoctorId; }
    public void setTargetDoctorId(String targetDoctorId) { this.targetDoctorId = targetDoctorId; }
}
