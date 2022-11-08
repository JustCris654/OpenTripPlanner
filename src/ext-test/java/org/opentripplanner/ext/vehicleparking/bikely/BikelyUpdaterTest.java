package org.opentripplanner.ext.vehicleparking.bikely;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.opentripplanner.routing.vehicle_parking.VehicleParking;
import org.opentripplanner.routing.vehicle_parking.VehicleParkingState;
import org.opentripplanner.transit.model.basic.Locales;
import org.opentripplanner.transit.model.framework.FeedScopedId;

public class BikelyUpdaterTest {

  @Test
  void parseBikeBoxes() {
    var url = "file:src/ext-test/resources/vehicleparking/bikely/bikely.json";
    var timeZone = ZoneId.of("Europe/Oslo");
    var parameters = new BikelyUpdaterParameters("", url, "bikely", 30, Map.of(), timeZone);
    var updater = new BikelyUpdater(parameters);

    assertTrue(updater.update());
    var parkingLots = updater.getUpdates();

    assertEquals(100, parkingLots.size());

    var first = parkingLots.get(0);
    assertEquals(new FeedScopedId("bikely", "164"), first.getId());
    assertEquals("Husebybadet", first.getName().toString());
    assertFalse(first.hasAnyCarPlaces());
    assertTrue(first.hasBicyclePlaces());
    assertNull(first.getCapacity());
    assertEquals(
      "First 4 hour(s) is NOK0.00, afterwards NOK10.00 per 1 hour(s)",
      first.getNote().toString(Locale.ENGLISH)
    );
    assertEquals(
      "First 4 hour(s) is kr 0,00, afterwards kr 10,00 per 1 hour(s)",
      first.getNote().toString(Locales.NORWEGIAN_BOKMAL)
    );
    assertEquals(
      "First 4 hour(s) is 0.00 kr, afterwards 10.00 kr per 1 hour(s)",
      first.getNote().toString(Locales.NORWEGIAN_NYNORSK)
    );

    var freeParkingLots = parkingLots.get(2);
    assertEquals("Free of charge", freeParkingLots.getNote().toString(Locale.ENGLISH));

    var last = parkingLots.get(99);
    assertEquals("Hamar Stasjon", last.getName().toString());
    assertNull(last.getOpeningHours());
    assertFalse(last.hasAnyCarPlaces());
    assertFalse(last.hasWheelchairAccessibleCarPlaces());

    var closed = parkingLots
      .stream()
      .map(VehicleParking::getState)
      .filter(x -> x == VehicleParkingState.TEMPORARILY_CLOSED)
      .count();
    assertEquals(2, closed);
  }
}
