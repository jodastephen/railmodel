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
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Ints;

/**
 * A train routing model.
 */
public class Model {

  /**
   * The maximum a journey can be over the shortest to still be printed.
   */
  private static final int MAX_EXCESS_WHEN_DIRECT = 8;
  /**
   * The maximum a journey can be over the shortest to still be printed.
   */
  private static final int MAX_EXCESS = 15;
  /**
   * The new line character.
   */
  static final String NEWLINE = System.lineSeparator();

  private final List<Route> routes = new ArrayList<>();
  private final List<Change> changes = new ArrayList<>();
  private final Multimap<Change, Change> preferredChanges = ArrayListMultimap.create();

  Model() {
  }

  static void appendSeparator(List<String> output) {
    output.add(NEWLINE);
    output.add("----" + NEWLINE);
    output.add(NEWLINE);
  }

  static void appendStations(List<String> output) {
    List<Station> stations = new ArrayList<>(Arrays.asList(Station.values()));
    stations.sort(Comparator.comparing(Station::name));
    appendSeparator(output);
    for (Station station : stations) {
      output.add("* " + station.name() + " " + station.description() + NEWLINE);
    }
  }

  //-------------------------------------------------------------------------
  public void addRoute(Route route) {
    routes.add(route);
  }

  public void addChange(Change change) {
    changes.add(change);
  }

  public void addPreferredChange(Change preferredChange, Change rejectedChange) {
    preferredChanges.put(preferredChange, rejectedChange);
  }

  //-------------------------------------------------------------------------
  public String explain(Station start, Station end) {
    StringBuilder buf = new StringBuilder();
    buf.append("From ").append(start.description()).append(" to ").append(end.description()).append(NEWLINE);
    buf.append(Strings.repeat("-", buf.length() - NEWLINE.length())).append(NEWLINE);
    List<Journey> solved = solve(start, end);
    if (solved.isEmpty()) {
      throw new IllegalStateException("No routes found: " + start + " - " + end);
    }
    solved.sort(Comparator.naturalOrder());
    Journey first = solved.get(0);
    int maxExcess = MAX_EXCESS;
    if (first.isDirect()) {
      maxExcess = MAX_EXCESS_WHEN_DIRECT;
    }
    for (Journey journey : solved) {
      if (journey != first) {
        int diff = first.differenceTo(journey);
        if (diff <= maxExcess) {
          buf.append("* ").append(journey).append(" (+").append(diff).append("m)").append(NEWLINE);
        }
      } else {
        buf.append("* ").append(journey).append(NEWLINE);
      }
    }
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
      Set<Change> allChanges = new HashSet<>();
      for (Station possibleChange : effectiveStations) {
        List<Change> changes = findChanges(possibleChange, allChanges, base, baseRoute, end);
        //System.out.println(possibleChange + " " + changes);
        allChanges.addAll(changes);
        for (Change change : changes) {
          if (change.getRoute2().containsAfter(end, change.getStation())) {
            journeys.add(Journey.of(start, end, baseRoute, change));
          } else {
            // two changes
            List<Station> effectiveStations2 = new ArrayList<>(change.getRoute2().getFlat().getStations());
            int startIndex2 = effectiveStations2.indexOf(change.getStation());
            effectiveStations2 = effectiveStations2.subList(startIndex2 + 1, effectiveStations2.size());
            for (Station possibleChange2 : effectiveStations2) {
              List<Change> changes2 = findChanges(possibleChange2, allChanges, base, change.getRoute2(), end);
              allChanges.addAll(changes2);
              for (Change change2 : changes2) {
                if (change2.getRoute2().containsAfter(end, change2.getStation())) {
                  if (!(baseRoute.getService().equals(change2.getRoute2().getService()))) {
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

  private List<Change> findChanges(Station possibleChange, Set<Change> allChanges, List<Route> allBase, Route baseRoute, Station dest) {
    List<Change> result = changes.stream()
        .filter(c -> c.getStation().equals(possibleChange))
        .filter(c -> baseRoute.includes(c.getRoute1()))
        .filter(c -> !c.getStation().equals(dest))
        .filter(c -> !isStupidChangeToRouteAvailableAtStartPoint(allBase, c))
        .collect(Collectors.toList());
    Set<Change> rejected = new HashSet<>();
    for (Change change : allChanges) {
      Collection<Change> reject = preferredChanges.get(change);
      rejected.addAll(reject);
    }
    result.removeAll(rejected);
    return result;
  }

  private boolean isStupidChangeToRouteAvailableAtStartPoint(List<Route> allBase, Change change) {
    for (Route route : allBase) {
      if (route.includes(change.getRoute2())) {
        return true;
      }
    }
    return false;
  }

  //-------------------------------------------------------------------------
  static List<Station> stations(Station... stations) {
    return ImmutableList.copyOf(stations);
  }

  static List<Integer> times(int... times) {
    return ImmutableList.copyOf(Ints.asList(times));
  }

}
