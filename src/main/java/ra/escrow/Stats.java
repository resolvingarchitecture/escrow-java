package ra.escrow;

import ra.common.content.JSON;
import ra.common.service.ServiceStatus;

import java.util.*;

public class Stats extends JSON {

    private Integer currentBalance = 0;
    private Integer totalAmountActive = 0;
    private Integer numberActive = 0;
    private Integer numberCompleted = 0;
    private Integer numberInactive = 0;
    private Integer percentInactive = 0;
    private Integer revenue = 0;
    private String status = ServiceStatus.SHUTDOWN.name();

    public void active(Integer amount, Integer feePercent) {

        synchronized (currentBalance) {
            currentBalance += amount;
            synchronized (totalAmountActive) {
                totalAmountActive += amount;
            }
        }
        synchronized (numberActive) {
            numberActive++;
        }
        advanceVersion();
    }

    public void completed(Integer amount) {
        synchronized (currentBalance) {
            currentBalance -= amount;
            synchronized (totalAmountActive) {
                totalAmountActive -= amount;
            }
        }
        synchronized (numberActive) {
            numberActive--;
        }
        synchronized (numberCompleted) {
            numberCompleted++;
        }
        advanceVersion();
    }

    public void inactive(Integer amount) {
        synchronized (totalAmountActive) {
            totalAmountActive -= amount;
        }
        synchronized (numberInactive) {
            numberInactive++;
        }
        synchronized (percentInactive) {
            percentInactive = (numberInactive / numberCompleted) * 100;
        }
        synchronized (revenue) {
            revenue += amount;
        }
        advanceVersion();
    }

    public void distribute(Integer amount) {
        synchronized (revenue) {
            revenue -= amount;
            synchronized (currentBalance) {
                currentBalance -= amount;
            }
        }
        advanceVersion();
    }

    public Integer getCurrentBalance() {
        return currentBalance;
    }

    public Integer getTotalAmountActive() {
        return totalAmountActive;
    }

    public Integer getNumberActive() {
        return numberActive;
    }

    public Integer getNumberCompleted() {
        return numberCompleted;
    }

    public Integer getNumberInactive() {
        return numberInactive;
    }

    public Integer getPercentInactive() {
        return percentInactive;
    }

    public Integer getRevenue() {
        return revenue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = super.toMap();
        m.put("currentBalance",currentBalance);
        m.put("totalAmountActive",totalAmountActive);
        m.put("numberActive",numberActive);
        m.put("numberCompleted",numberCompleted);
        m.put("numberInactive",numberInactive);
        m.put("percentInactive",percentInactive);
        m.put("revenue", revenue);
        m.put("status", status);
        return m;
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        super.fromMap(m);
        if(m.get("currentBalance")!=null) {currentBalance=(Integer)m.get("currentBalance");}
        if(m.get("totalAmountActive")!=null) {totalAmountActive=(Integer)m.get("totalAmountActive");}
        if(m.get("numberActive")!=null) {numberActive=(Integer)m.get("numberActive");}
        if(m.get("numberCompleted")!=null) {numberCompleted=(Integer)m.get("numberCompleted");}
        if(m.get("numberInactive")!=null) {numberInactive=(Integer)m.get("numberInactive");}
        if(m.get("percentInactive")!=null) {percentInactive=(Integer)m.get("percentInactive");}
        if(m.get("revenue")!=null) {revenue =(Integer)m.get("revenue");}
        if(m.get("status")!=null) {status =(String)m.get("status");}
    }

}
