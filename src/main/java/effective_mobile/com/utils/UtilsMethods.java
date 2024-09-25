package effective_mobile.com.utils;

import effective_mobile.com.utils.exception.BadRequestException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class UtilsMethods {

    public static void checkVar(List<String> vars) throws BadRequestException {
        for (String var : vars) {
            if (var == null || var.equals("")) {
                throw new BadRequestException("Var mustn't null or empty");
            }
        }
    }

    public static void checkLong(List<Long> vars) throws BadRequestException {
        for (Long var : vars) {
            if (var == null) {
                throw new BadRequestException("Var mustn't null or empty");
            }
        }
    }

    public static LocalDateTime parseLocalDataTimeFromInstant(Integer time) {
        return Instant.ofEpochSecond(time).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

}
