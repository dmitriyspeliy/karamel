package effective_mobile.com.utils.enums;

import lombok.Getter;

@Getter
public enum CityInfo {


    CHEL("ЧЕЛЯБИНСК", "https://chelyabinsk.exkursiacaramel.ru", 101, "C3:EXECUTING"),
    TYUMEN("ТЮМЕНЬ", "https://tyumen.exkursiacaramel.ru", 113, "C9:EXECUTING"),
    MSKM("МОСКВА", "https://msk.exkursiacaramel.ru", 97, "FINAL_INVOICE"),
    MSKO("МОСКВА КРАСНОГОРСК", "https://exkursiacaramel.ru", 203, "C23:EXECUTING"),
    SAMARA("САМАРА", "https://samara.exkursiacaramel.ru", 99, "C1:EXECUTING"),
    KSR("КРАСНОЯРСК", "https://ksr.exkursiacaramel.ru", 109, "C13:EXECUTING"),
    YAR("ЯРОСЛАВЛЬ", "https://yar.exkursiacaramel.ru", 103, "C5:EXECUTING"),
    KZN("КАЗАНЬ", "https://kzn.exkursiacaramel.ru", 105, "C7:EXECUTING");
    //SPB("CАНКТ-ПЕТЕРБУРГ", "https://spb.exkursiacaramel.ru", 201, "C25:EXECUTING");

    final String name;
    final String hostName;
    final Integer slotCityNum;
    final String successSection;

    CityInfo(String name, String hostName, Integer slotCityNum, String successSection) {
        this.name = name;
        this.hostName = hostName;
        this.slotCityNum = slotCityNum;
        this.successSection = successSection;
    }

    public static Integer getCityCode(String name) {
        for (var city : CityInfo.values()) {
            if (city.name.equals(name)) {
                return city.slotCityNum;
            }
        }
        return null;
    }

    public static String getCitySection(String name) {
        for (var city : CityInfo.values()) {
            if (city.name.equals(name)) {
                return city.successSection;
            }
        }
        return null;
    }

}
