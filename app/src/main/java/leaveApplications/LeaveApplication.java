package leaveApplications;

public class LeaveApplication {
    String name,cnic,fromDate,toDate,leaveType,reason,datePosted;
    Boolean approved;



    public LeaveApplication(String name, String cnic, String fromDate,
                            String toDate, String leaveType, String reason, String datePosted) {
        this.name = name;
        this.cnic = cnic;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.leaveType = leaveType;
        this.reason = reason;
        this.approved=false;
        this.datePosted=datePosted;
    }

    public String getDatePosted() {
        return datePosted;
    }

    public LeaveApplication() {
    }

    public String getName() {
        return name;
    }

    public String getCnic() {
        return cnic;
    }

    public String getFromDate() {
        return fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public String getReason() {
        return reason;
    }
    public Boolean isApproved() {
        return approved;
    }



}
