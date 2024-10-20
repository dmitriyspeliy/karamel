package effective_mobile.com.utils.enums;

public enum SlotType {
    SCHOOL(117, "Школьные"),
    MIXED(119, "Сборные");

    private final int code;
    private final String description;

    SlotType(int value, String description) {
        this.code = value;
        this.description = description;
    }

    public static Integer getCodeOfType(String type) {
        for (var slotType : SlotType.values()) {
            if (slotType.description.equalsIgnoreCase(type)) {
                return slotType.code;
            }
        }
        return null;
    }
}
