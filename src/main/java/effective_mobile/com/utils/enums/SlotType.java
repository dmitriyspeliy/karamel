package effective_mobile.com.utils.enums;

public enum SlotType {
    SCHOOL(117, "Школьный"),
    MIXED(119, "Сборная");

    private final int code;
    private final String description;

    SlotType(int value, String description) {
        this.code = value;
        this.description = description;
    }

    public static String typeFromValue(int value) {
        for (var slotType : SlotType.values()) {
            if (slotType.code == value) {
                return slotType.description;
            }
        }
        return null;
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
