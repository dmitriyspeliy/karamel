package effective_mobile.com.utils;

import lombok.Getter;

public enum NameOfCity {

    CHEL("ЧЕЛЯБИНСК", "https://chelyabinsk.exkursiacaramel.ru", 8090),
    EKB("ЕКАТЕРИНБУРГ", "https://ekb.exkursiacaramel.ru", 8092),
    TYUMEN("ТЮМЕНЬ", "https://tyumen.exkursiacaramel.ru", 8093),
    IZH("ИЖЕВСК", "https://izh.exkursiacaramel.ru", 8083),
    KRAS("КРАСНОЯРСК", "https://kras.exkursiacaramel.ru", 8099),
    NSK("НОВОСИБИРСК", "https://nsk.exkursiacaramel.ru", 8098),
    SAMARA("САМАРА", "https://samara.exkursiacaramel.ru", 8097),
    VOLGOGRAD("ВОЛГОГРАД", "https://volgograd.exkursiacaramelramel.ru", 8096),
    LIPETSK("ЛИПЕЦК", "https://lipetsk.exkursiacaramel.ru", 8095),
    PERM("ПЕРМЬ", "https://perm.exkursiacaramel.ru", 8094),
    SPB("САНКТ-ПЕТЕРБУРГ", "https://spb.exkursiacaramel.ru", 8081),
    YAR("ЯРОСЛАВЛЬ", "https://yar.exkursiacaramel.ru", 8082),
    MSK("МОСКВА", "https://msk.exkursiacaramel.ru", 8091);

    @Getter
    final String name;
    final Integer port;
    final String hostName;

    NameOfCity(String name, String hostName, Integer port) {
        this.name = name;
        this.port = port;
        this.hostName = hostName;
    }


}
