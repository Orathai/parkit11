package io.dynamicus;

import io.dynamicus.model.Zone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class CalculatePriceService {

    private static final Logger logger = LoggerFactory.getLogger(CalculatePriceService.class);

    public double calculate(int minutes,
                            Zone zone) {
        return calculate(minutes, zone, LocalDateTime.now());
    }

    protected double calculate(int minutes,
                               Zone zone,
                               LocalDateTime startedTime) {
        if (minutes < 0) {
            return 0;
        }
        double pricePerMinute = 1;
        double remainMinutes = minutes % 60;

        double totalPrice = 0;

        if (Zone.M1.equals(zone)) {
            totalPrice = pricePerMinute * minutes;
        }
        if (Zone.M2.equals(zone)) {
            totalPrice = getTotalPriceZoneM2(minutes, remainMinutes, startedTime);
        }
        if (Zone.M3.equals(zone)) {
            totalPrice = getTotalPriceZoneM3(minutes, startedTime);
        }
        logger.info("ZONE: " + zone
                + " DURATION: " + minutes + " mins"
                + " PRICE: " + totalPrice
                + "(" + startedTime.getDayOfWeek() + ")");
        return totalPrice;
    }

    private double getTotalPriceZoneM2(int minutes,
                                       double remainMinutes,
                                       LocalDateTime start) {
        double pricePerHour;
        double totalPrice = 0;
        int hours = Math.round(minutes / 60);
        if (remainMinutes > 0) {
            hours += 1;
        }
        for (int i = 0; i < hours; i++) {

            switch (start.getDayOfWeek()) {
                case SATURDAY:
                case SUNDAY:
                    pricePerHour = 200;
                    break;
                default:
                    pricePerHour = 100;

            }
            start = start.plus(1, ChronoUnit.HOURS);

            totalPrice += pricePerHour;
        }
        return totalPrice;
    }

    private double getTotalPriceZoneM3(int minutes,
                                       LocalDateTime start) {
        double totalPrice = 0;
        double pricePerMinute;
        // At least 1 hour
        int hours = Math.max(Math.round(minutes / 60), 1);

        if (minutes < 1) {
            return totalPrice;
        }

        boolean discounted = false;
        for (int i = 0; i < hours; i++) {

            switch (start.getDayOfWeek()) {
                case SUNDAY:
                    pricePerMinute = 0;
                    break;
                default:
                    if ((start.getHour() >= 8) && (start.getHour() < 16)) {
                        if (discounted) {
                            pricePerMinute = 2;
                        } else {
                            discounted = true;
                            pricePerMinute = 0;
                        }
                    } else {
                        pricePerMinute = 3;
                    }

            }
            start = start.plus(1, ChronoUnit.HOURS);
            totalPrice += Math.min( minutes, 60 ) * pricePerMinute;
            minutes = minutes - 60;
        }
        return totalPrice;
    }
}
