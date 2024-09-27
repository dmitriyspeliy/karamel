package effective_mobile.com.model.dto;

import java.net.URL;

public record BookingCreationResult(String id, String hash, URL invoiceLink) {

}