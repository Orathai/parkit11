package io.dynamicus;

import io.dynamicus.model.Zone;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.*;

public class CalculatePriceServiceTest {

    private CalculatePriceService calculatePrice = new CalculatePriceService();

    @Test
    public void testCalculatePriceZoneM1Goodday() {

        //60 mins, price 60
        assertEquals(60, calculatePrice.calculate(60, Zone.M1), 0);

        //30 mins, price 30
        assertEquals(30, calculatePrice.calculate(30, Zone.M1), 0);

        //1 hour 20 mins, price 120
        assertEquals(80, calculatePrice.calculate(80, Zone.M1), 0);
    }

    @Test
    public void testCalculatePriceZoneM1Badday() {
        assertEquals(0, calculatePrice.calculate(-2, Zone.M1), 0);
    }

    @Test
    public void testCalculatePriceZoneM2Goodday() {

        //60 mins, Monday, price 100
        LocalDateTime start = LocalDateTime.of(2017, 12, 4, 7, 00, 00);
        //LocalDateTime end = LocalDateTime.of(2017, 12, 4, 8, 00, 00);

        assertEquals(100, calculatePrice.calculate(60, Zone.M2, start), 0);

        //1 hour 20 mins, Monday, price 200
        assertEquals(200, calculatePrice.calculate(80, Zone.M2, start), 0);

        //60 mins, Saturday, double price to 200
        LocalDateTime saturday = LocalDateTime.of(2017, 12, 9, 7, 00, 00);
        assertEquals(200, calculatePrice.calculate(60, Zone.M2, saturday), 0);

        //1 hour 20 mins, Saturday, double price to 400
        assertEquals(400, calculatePrice.calculate(80, Zone.M2, saturday), 0);
    }

    @Test
    public void testCalculatePriceZoneM2FridayToWeekend() {
        //120 mins, From Friday night to Saturday : 1 hour Friday = 100, 1 hour Saturday = 200, TOTAL 300
        LocalDateTime fridayNight = LocalDateTime.of(2017, 12, 8, 23, 00, 00);

        assertEquals(100, calculatePrice.calculate(60, Zone.M2, fridayNight), 0);
        assertEquals(300, calculatePrice.calculate(120, Zone.M2, fridayNight), 0);
        assertEquals(300, calculatePrice.calculate(80, Zone.M2, fridayNight), 0);

    }

    @Test
    public void testCalculatePriceZoneM2Badday() {
        assertEquals(0, calculatePrice.calculate(-5, Zone.M2), 0);
    }

    @Test
    public void testCalculatePricZoneM3MondayInTimeRange() {

        //Free for first hour
        //Monday 1 hour = 0
        LocalDateTime start = LocalDateTime.of(2017, 12, 4, 8, 00, 00);
        assertEquals(0, calculatePrice.calculate(60, Zone.M3, start), 0);

        //Between 08:00 - 16:00, free 60 mins,  60 minutes * 2 = 120
        assertEquals(120, calculatePrice.calculate(120, Zone.M3, start), 0);

        //Free parking on Sunday
        LocalDateTime sunday = LocalDateTime.of(2017, 12, 10, 8, 00, 00);
        assertEquals(0, calculatePrice.calculate(120, Zone.M3, sunday), 0);
    }

    @Test
    public void testCalculatePricZoneM3MondayOutOfTimeRange() {

        //After 16:00, First hour is not free anymore (mins * 3)
        LocalDateTime start = LocalDateTime.of(2017, 12, 4, 17, 00, 00);
        assertEquals(180, calculatePrice.calculate(60, Zone.M3, start), 0);
        assertEquals(90, calculatePrice.calculate(30, Zone.M3, start), 0);

    }

    @Test
    public void testCalculatePricZoneM3BothTimeRange() {

        //First hour is free from 08:00 - 16:00, After 16:00 pay 60 mins * 3

        //15:00 - 16:00 pay 0, 16:00-17:00 pay 60 mins * 3 =  180
        LocalDateTime start = LocalDateTime.of(2017, 12, 4, 15, 00, 00);
        assertEquals(180, calculatePrice.calculate(120, Zone.M3, start), 0);
        assertEquals(360, calculatePrice.calculate(180, Zone.M3, start), 0);

    }

    @Test
    public void testCalculatePriceZoneM3() {

        //Tuesday 08:00
        LocalDateTime start = LocalDateTime.of(2017, 12, 5, 8, 00, 00);
        //For checking duration
        LocalDateTime end = LocalDateTime.of(2017, 12, 7, 8, 00, 00);
        int duration = end.compareTo(start);
        System.out.println(duration);

        //From Tuesday 08:00 - Thursday 00:00
        //48 hours, in minutes = 2880
        //Tuesday   08:00 - Tuesday   16:00, (8-1 hours), 420 mins * 2 = 840 kr
        //Tuesday   16:00 - Wednesday 08:00, 16 hours,    960 mins * 3 = 2880 kr
        //Wednesday 08:00 - Wednesday 16:00, (8 hours), 480 mins * 2 = 960 kr
        //Wednesday 16:00 - Thursday  08:00, 16 hours,    960 mins * 3 = 2880 kr
        //Total price = 7560 kr

        int minutes = (int) ChronoUnit.MINUTES.between(start, end);
        assertEquals(7560, calculatePrice.calculate(minutes, Zone.M3, start), 0);

    }

}