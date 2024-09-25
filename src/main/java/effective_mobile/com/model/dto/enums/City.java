package effective_mobile.com.model.dto.enums;

public enum City {
    MOSCOW(97, "Москва"),
    SAMARA(99, "Самара"),
    CHELYABINSK(101, "Челябинск"),
    YAROSLAVL(103, "Ярославль"),
    KAZAN(105, "Казань"),
    YEKATERINBURG(107, "Екатеринбург"),
    KRASNOYARSK(109, "Красноярск"),
    ALMATY(111, "Алматы"),
    TYUMEN(113, "Тюмень"),
    UFA(115, "Уфа");

    private final int code;
    private final String description;

    City(int value, String description) {
        this.code = value;
        this.description = description;
    }

    public static String cityFromValue(int value) {
        for (var city : City.values()) {
            if (city.code == value) {
                return city.description;
            }
        }
        return null;
    }

    public static Integer getCodeOfCity(String name) {
        for (var city : City.values()) {
            if (city.description.equalsIgnoreCase(name)) {
                return city.code;
            }
        }
        return null;
    }
}
