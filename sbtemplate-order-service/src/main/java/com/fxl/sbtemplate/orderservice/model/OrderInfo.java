package com.fxl.sbtemplate.orderservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@ToString
public class OrderInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String unitId;

    private String orderNo;

    private String userId;

    private String phone;

    private String memberId;

    private String trueName;

    private String doctorId;

    private String doctorName;

    private String depId;

    private String depName;

    private Double exameFee;

    private Double guahaoAmt;

    private String toDate;

    private String beginTime;

    private String endTime;

    private String hisTakeNo;

    private String payState;

    @JsonIgnore
    private Date createTime;

    private Byte reservationStatus;

    private Byte signStatus;

    private Byte examinePayStatus;

    private Byte examineStatus;

    private Byte reportStatus;

    private Byte outpatientPayStatus;

    private Byte medicineStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId == null ? null : memberId.trim();
    }

    public String getTrueName() {
        return trueName;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName == null ? null : trueName.trim();
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId == null ? null : doctorId.trim();
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName == null ? null : doctorName.trim();
    }

    public String getDepId() {
        return depId;
    }

    public void setDepId(String depId) {
        this.depId = depId == null ? null : depId.trim();
    }

    public String getDepName() {
        return depName;
    }

    public void setDepName(String depName) {
        this.depName = depName == null ? null : depName.trim();
    }

    public Double getExameFee() {
        return exameFee;
    }

    public void setExameFee(Double exameFee) {
        this.exameFee = exameFee;
    }

    public Double getGuahaoAmt() {
        return guahaoAmt;
    }

    public void setGuahaoAmt(Double guahaoAmt) {
        this.guahaoAmt = guahaoAmt;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate == null ? null : toDate.trim();
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime == null ? null : beginTime.trim();
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime == null ? null : endTime.trim();
    }

    public String getHisTakeNo() {
        return hisTakeNo;
    }

    public void setHisTakeNo(String hisTakeNo) {
        this.hisTakeNo = hisTakeNo == null ? null : hisTakeNo.trim();
    }

    public String getPayState() {
        return payState;
    }

    public void setPayState(String payState) {
        this.payState = payState == null ? null : payState.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Byte getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(Byte reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public Byte getSignStatus() {
        return signStatus;
    }

    public void setSignStatus(Byte signStatus) {
        this.signStatus = signStatus;
    }

    public Byte getExaminePayStatus() {
        return examinePayStatus;
    }

    public void setExaminePayStatus(Byte examinePayStatus) {
        this.examinePayStatus = examinePayStatus;
    }

    public Byte getExamineStatus() {
        return examineStatus;
    }

    public void setExamineStatus(Byte examineStatus) {
        this.examineStatus = examineStatus;
    }

    public Byte getReportStatus() {
        return reportStatus;
    }

    public void setReportStatus(Byte reportStatus) {
        this.reportStatus = reportStatus;
    }

    public Byte getOutpatientPayStatus() {
        return outpatientPayStatus;
    }

    public void setOutpatientPayStatus(Byte outpatientPayStatus) {
        this.outpatientPayStatus = outpatientPayStatus;
    }

    public Byte getMedicineStatus() {
        return medicineStatus;
    }

    public void setMedicineStatus(Byte medicineStatus) {
        this.medicineStatus = medicineStatus;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }
}