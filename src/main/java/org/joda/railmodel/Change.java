/*
 *  Copyright 2015-present Stephen Colebourne
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.joda.railmodel;

/**
 * A change of trains.
 */
public final class Change {

  private final Station station;
  private final Route route1;
  private final Route route2;
  private final int timeMin;
  private final int timeMax;

  //-------------------------------------------------------------------------
  public static Change of(Station station, Route route1, Route route2, int timeMin, int timeMax) {
    return new Change(station, route1, route2, timeMin, timeMax);
  }

  //-------------------------------------------------------------------------
  private Change(Station station, Route route1, Route route2, int timeMin, int timeMax) {
    if (!route1.getStations().contains(station)) {
      throw new IllegalArgumentException("Route1 does not contain station");
    }
    if (!route2.getStations().contains(station)) {
      throw new IllegalArgumentException("Route2 does not contain station");
    }
    this.station = station;
    this.route1 = route1;
    this.route2 = route2;
    this.timeMin = timeMin;
    this.timeMax = timeMax;
  }

  //-------------------------------------------------------------------------
  public Station getStation() {
    return station;
  }

  public Route getRoute1() {
    return route1;
  }

  public Route getRoute2() {
    return route2;
  }

  public int getTimeMin() {
    return timeMin;
  }

  public int getTimeMax() {
    return timeMax;
  }

  //-------------------------------------------------------------------------
  @Override
  public String toString() {
    return "Change at " + station + " from " + route1 + " to " + route2;
  }

}
