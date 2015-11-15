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
 * A user journey.
 */
public final class Journey implements Comparable<Journey> {

  private final Station start;
  private final Station end;
  private final Route route1;
  private final Change change1;
  private final Change change2;

  //-------------------------------------------------------------------------
  public static Journey of(Station start, Station end, Route directRoute) {
    return new Journey(start, end, directRoute, null, null);
  }

  public static Journey of(Station start, Station end, Route directRoute, Change change1) {
    return new Journey(start, end, directRoute, change1, null);
  }

  public static Journey of(Station start, Station end, Route directRoute, Change change1, Change change2) {
    return new Journey(start, end, directRoute, change1, change2);
  }

  //-------------------------------------------------------------------------
  public Journey(Station start, Station end, Route route1, Change change1, Change change2) {
    this.start = start;
    this.end = end;
    this.route1 = route1;
    this.change1 = change1;
    this.change2 = change2;
  }

  //-------------------------------------------------------------------------
  public int frequency() {
    if (change1 == null) {
      return route1.getFrequency();
    }
    int min = Math.min(route1.getFrequency(), change1.getRoute2().getFrequency());
    if (change2 == null) {
      return min;
    }
    return Math.min(min, change2.getRoute2().getFrequency());
  }

  public int timeMin() {
    if (change1 == null) {
      return route1.time(start, end);
    }
    if (change2 == null) {
      return route1.time(start, change1.getStation()) +
          change1.getTimeMin() +
          change1.getRoute2().time(change1.getStation(), end);
    }
    return route1.time(start, change1.getStation()) +
        change1.getTimeMin() +
        change1.getRoute2().time(change1.getStation(), change2.getStation()) +
        change2.getTimeMin() +
        change2.getRoute2().time(change2.getStation(), end);
  }

  public int timeMax() {
    if (change1 == null) {
      return route1.time(start, end);
    }
    if (change2 == null) {
      return route1.time(start, change1.getStation()) +
          change1.getTimeMax() +
          change1.getRoute2().time(change1.getStation(), end);
    }
    return route1.time(start, change1.getStation()) +
        change1.getTimeMax() +
        change1.getRoute2().time(change1.getStation(), change2.getStation()) +
        change2.getTimeMax() +
        change2.getRoute2().time(change2.getStation(), end);
  }

  //-------------------------------------------------------------------------
  @Override
  public int compareTo(Journey other) {
    int thisTime = ((this.timeMax() + this.timeMin()) * 2 + 1) / 2;
    int otherTime = ((other.timeMax() + other.timeMin()) * 2 + 1) / 2;
    return thisTime - otherTime;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(64);
    if (change1 == null) {
      buf.append(route1.getName() + " direct in " + route1.time(start, end) + "m at " + route1.getFrequency() + "tph");

    } else if (change2 == null) {
      int timeMin = timeMin();
      int timeMax = timeMax();
      int freq = frequency();
      buf.append(route1.getName() + " changing to " + change1.getRoute2() + " at " + change1.getStation() +
          " in " + timeMin + "-" + timeMax + "m at " + freq + "tph");
    } else {
      int timeMin = timeMin();
      int timeMax = timeMax();
      int freq = frequency();
      buf.append(route1.getName() + " changing to " + change1.getRoute2() + " at " + change1.getStation() +
          " changing to " + change2.getRoute2() + " at " + change2.getStation() +
          " in " + timeMin + "-" + timeMax + "m at " + freq + "tph");
    }
    return buf.toString();
  }

}
