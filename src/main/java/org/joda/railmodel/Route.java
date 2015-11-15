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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;

/**
 * A train route.
 */
public final class Route {

  private final String service;
  private final String name;
  private final int frequency;
  private final ImmutableList<Station> stations;
  private final ImmutableList<Integer> times;
  private final Optional<Route> nextSegment;
  private final Route flat;

  //-------------------------------------------------------------------------
  public static Route of(String service, String name, int frequency, List<Station> stations, List<Integer> times) {
    return of(service, name, frequency, stations, times, null);
  }

  public static Route of(String service, String name, int frequency, List<Station> stations, List<Integer> times, Route nextSegment) {
    return new Route(service, name, frequency, stations, times, nextSegment);
  }

  //-------------------------------------------------------------------------
  private Route(String service, String name, int frequency, List<Station> stations, List<Integer> times, Route nextSegment) {
    if (stations.size() - 1 != times.size()) {
      throw new IllegalArgumentException("Times must be one less than stations");
    }
    if (nextSegment != null && !stations.get(stations.size() - 1).equals(nextSegment.getStations().get(0))) {
      throw new IllegalArgumentException("Invalid next segment");
    }
    this.service = service;
    this.name = name;
    this.frequency = frequency;
    this.stations = ImmutableList.copyOf(stations);
    this.times = ImmutableList.copyOf(times);
    this.nextSegment = Optional.ofNullable(nextSegment);
    this.flat = flatten();
  }

  private Route flatten() {
    if (nextSegment.isPresent()) {
      Route flatNext = nextSegment.get().flatten();
      List<Station> flatStations = new ArrayList<>(stations);
      List<Integer> flatTimes = new ArrayList<>(times);
      flatStations.addAll(flatNext.getStations().subList(1, flatNext.getStations().size()));
      flatTimes.addAll(flatNext.getTimes());
      return new Route(service, name, frequency, flatStations, flatTimes, null);
    }
    return this;
  }

  //-------------------------------------------------------------------------
  public String getService() {
    return service;
  }

  public String getName() {
    return name;
  }

  public int getFrequency() {
    return frequency;
  }

  public ImmutableList<Station> getStations() {
    return stations;
  }

  public ImmutableList<Integer> getTimes() {
    return times;
  }

  public Route getFlat() {
    return flat;
  }

  public Optional<Route> getNextSegment() {
    return nextSegment;
  }

  //-------------------------------------------------------------------------
  public int time(Station start, Station end) {
    int startIndex = flat.stations.indexOf(start);
    int endIndex = flat.stations.indexOf(end);
    if (startIndex < 0) {
      throw new IllegalArgumentException("Start station not in route: " + start);
    }
    if (endIndex < 0) {
      throw new IllegalArgumentException("End station not in route: " + end);
    }
    if (startIndex > endIndex) {
      throw new IllegalArgumentException("Start staton after End station in route: " + start + "," + end);
    }
    return flat.times.subList(startIndex, endIndex).stream().mapToInt(Integer::intValue).sum();
  }

  public Optional<Route> matches(Station station) {
    Optional<Route> match = nextSegment.flatMap(n -> n.matches(station));
    if (match.isPresent()) {
      return match;
    }
    if (stations.contains(station)) {
      return Optional.of(this);
    }
    return Optional.empty();
  }

  public boolean contains(Station station) {
    return flat.stations.contains(station);
  }

  public boolean containsAfter(Station station, Station after) {
    int index = Math.max(flat.stations.indexOf(after), 0);
    return flat.stations.subList(index, flat.stations.size()).contains(station);
  }

  public boolean includes(Route other) {
    return this.equals(other) || (nextSegment.isPresent() && nextSegment.get().includes(other));
  }

  //-------------------------------------------------------------------------
  @Override
  public String toString() {
    return name;
  }

}
