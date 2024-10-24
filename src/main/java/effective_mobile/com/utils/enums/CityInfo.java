package effective_mobile.com.utils.enums;

import lombok.Getter;

@Getter
public enum CityInfo {


    CHEL("ЧЕЛЯБИНСК", "https://chelyabinsk.exkursiacaramel.ru", 101),
    TYUMEN("ТЮМЕНЬ", "https://tyumen.exkursiacaramel.ru", 113),
    //MSKO("МОСКВА КРАСНОГОРСК ПАВШИНО", "https://exkursiacaramel.ru", 203),
    MSKM("МОСКВА", "https://msk.exkursiacaramel.ru", 97),
    MSKO("МОСКВА", "https://exkursiacaramel.ru", 97),
    SAMARA("САМАРА", "https://samara.exkursiacaramel.ru", 99),
    KSR("КРАСНОЯРСК", "https://ksr.exkursiacaramel.ru", 109);
    //SPB("CАНКТ-ПЕТЕРБУРГ", "https://spb.exkursiacaramel.ru", 201);

    final String name;
    final String hostName;
    final Integer slotCityNum;

    CityInfo(String name, String hostName, Integer slotCityNum) {
        this.name = name;
        this.hostName = hostName;
        this.slotCityNum = slotCityNum;
    }

    public static Integer getCityCode(String name) {
        for (var city : CityInfo.values()) {
            if (city.name.equals(name)) {
                return city.slotCityNum;
            }
        }
        return null;
    }

}
