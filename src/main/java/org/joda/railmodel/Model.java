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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;

/**
 * A train routing model.
 */
public class Model {

  private final List<Route> routes = new ArrayList<>();
  private final List<Change> changes = new ArrayList<>();

  //-------------------------------------------------------------------------
  public void addRoute(Route route) {
    routes.add(route);
  }

  public void addChange(Change change) {
    changes.add(change);
  }

  //-------------------------------------------------------------------------
  public String explain(Station start, Station end) {
    StringBuilder buf = new StringBuilder();
    buf.append("From ").append(start).append(" to ").append(end).append("\n");
    List<Journey> solved = solve(start, end);
    solved.sort(Comparator.naturalOrder());
    buf.append(Joiner.on("\n").join(solved)).append("\n");
    return buf.toString();
  }

  public List<Journey> solve(Station start, Station end) {
    List<Route> base = routes.stream()
        .flatMap(r -> r.matches(start).map(Stream::of).orElse(Stream.empty()))
        .distinct()
        .collect(Collectors.toList());
    if (base.isEmpty()) {
      throw new IllegalArgumentException("Station not found: " + start);
    }
    // direct
    List<Journey> journeys = new ArrayList<>();
    for (Route route : base) {
      if (route.contains(end)) {
        journeys.add(Journey.of(start, end, route));
      }
    }
    // one change
    for (Route baseRoute : base) {
      List<Station> effectiveStations = new ArrayList<>(baseRoute.getFlat().getStations());
      int startIndex = effectiveStations.indexOf(start);
      effectiveStations = effectiveStations.subList(startIndex + 1, effectiveStations.size());
      for (Station possibleChange : effectiveStations) {
        List<Change> changes = findChanges(possibleChange, baseRoute, end);
        for (Change change : changes) {
          if (change.getRoute2().containsAfter(end, change.getStation())) {
            journeys.add(Journey.of(start, end, baseRoute, change));
          } else {
            // two changes
            List<Station> effectiveStations2 = new ArrayList<>(change.getRoute2().getFlat().getStations());
            int startIndex2 = effectiveStations2.indexOf(change.getStation());
            effectiveStations2 = effectiveStations2.subList(startIndex2 + 1, effectiveStations2.size());
            for (Station possibleChange2 : effectiveStations2) {
              List<Change> changes2 = findChanges(possibleChange2, change.getRoute2(), end);
              for (Change change2 : changes2) {
                if (change2.getRoute2().containsAfter(end, change2.getStation())) {
                  if (!(baseRoute.getName().contains("CR2") && change2.getRoute2().getName().contains("CR2"))) {
                    journeys.add(Journey.of(start, end, baseRoute, change, change2));
                  }
                }
              }
            }
          }
        }
      }
    }
    return journeys;
  }

  private List<Change> findChanges(Station possibleChange, Route baseRoute, Station dest) {
    return changes.stream()
        .filter(c -> c.getStation().equals(possibleChange))
        .filter(c -> baseRoute.includes(c.getRoute1()))
        .filter(c -> !c.getStation().equals(dest))
        .collect(Collectors.toList());
  }

  //-------------------------------------------------------------------------
  static List<Station> stations(Station... stations) {
    return ImmutableList.copyOf(stations);
  }

  static List<Integer> times(int... times) {
    return ImmutableList.copyOf(Ints.asList(times));
  }

}
