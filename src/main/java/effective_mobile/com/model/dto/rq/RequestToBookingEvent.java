package effective_mobile.com.model.dto.rq;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class RequestToBookingEvent implements Serializable {
    private String name;
    private String source;
    private Integer childrenCount;
    private Integer paidAdultCount;
    private Integer freeChild;
    private Integer freeAdultCount;
    private Integer totalCount;
    private String childrenAge;
    private Integer date;
    private String school;
    private String address;
    private String departureAddress;
    private String city;
    private String eventName;
    private String number;
    private String contactName;

    @Override
    public String toString() {
        return "RequestToBookingEvent{" +
                "name='" + name + '\'' +
                ", source='" + source + '\'' +
                ", childrenCount=" + childrenCount +
                ", paidAdultCount=" + paidAdultCount +
                ", freeChild=" + freeChild +
                ", freeAdultCount=" + freeAdultCount +
                ", totalCount=" + totalCount +
                ", childrenAge='" + childrenAge + '\'' +
                ", date=" + date +
                ", school='" + school + '\'' +
                ", address='" + address + '\'' +
                ", departureAddress='" + departureAddress + '\'' +
                ", city='" + city + '\'' +
                ", eventName='" + eventName + '\'' +
                ", number='" + number + '\'' +
                ", contactName='" + contactName + '\'' +
                '}';
    }
}