package org.opentripplanner.routing.core;

import java.io.Serializable;

@Deprecated
public enum FareType implements Serializable {
  regular,
  student,
  senior,
  tram,
  special,
  youth,
  electronicRegular,
  electronicSenior,
  electronicSpecial,
  electronicYouth,
}
