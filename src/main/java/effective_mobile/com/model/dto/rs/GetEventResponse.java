package effective_mobile.com.model.dto.rs;

import java.util.List;

public record GetEventResponse(EventsField events) {
    public record EventsField(List<Event> events) {

    }

    public record Event (
            Long id,
            String name,
            String type,
            String time,
            Integer adultPrice,
            Integer kidPrice,
            Integer childAge,
            Integer capacity,
            Integer adultCapacity,
            Integer kidCapacity,
            Integer slotsLeft,
            Integer adultSlotsLeft,
            Integer kidSlotsLeft,
            String gatheringType,
            Boolean adultRequired,
            String city) {

    }
}
