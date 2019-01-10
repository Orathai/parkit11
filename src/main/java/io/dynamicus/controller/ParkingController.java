package io.dynamicus.controller;

import io.dynamicus.CalculatePriceService;
import io.dynamicus.model.Price;
import io.dynamicus.model.Zone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ParkingController {

    @Autowired
    private CalculatePriceService calculate;

    private static final Logger logger = LoggerFactory.getLogger(ParkingController.class);

    @RequestMapping(method = RequestMethod.GET, value = "/prices", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Price> getPriceByZone(@RequestParam(value = "minutes") int minutes,
                                                @RequestParam(value = "zone") String zone) {

        logger.debug("GET parking price service at ZONE: " + zone + " DURATIONS: " + minutes);

        Zone convertZone = Zone.valueOf(zone);

        double totalPrice = calculate.calculate(minutes, convertZone);
        Price price = new Price();
        price.price = totalPrice;

        logger.debug("Total price: " + price.price);

        return new ResponseEntity<>(price, HttpStatus.OK);
    }
}
