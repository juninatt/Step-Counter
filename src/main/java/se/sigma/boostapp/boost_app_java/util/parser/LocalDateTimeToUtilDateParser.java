package se.sigma.boostapp.boost_app_java.util.parser;

import org.springframework.stereotype.Component;
import se.sigma.boostapp.boost_app_java.converter.BoostAppConverter;

import java.time.*;
import java.util.Date;

@Component
public class LocalDateTimeToUtilDateParser implements BoostAppConverter<LocalDateTime, java.util.Date> {
    @Override
    public Date convert(LocalDateTime from) {
            try {
                ZonedDateTime zonedDateTime = from.atZone(ZoneId.systemDefault());
                return Date.from(zonedDateTime.toInstant());
            } catch (DateTimeException e) {
                return Date.from(Instant.now());
        }
    }
}
