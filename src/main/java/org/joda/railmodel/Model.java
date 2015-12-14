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
import java.util.Iterator;
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

  static void appendDocs(List<String> output) {
    output.add("A selection of interesting journeys is listed, together with calculated route options." + NEWLINE);
    output.add("A key for station codes is at the end." + NEWLINE);
    output.add("The route options are sorted, with the fastest first." + NEWLINE);
    output.add("The excess over the fastest route option is listed in brackets." + NEWLINE);
    output.add("If the fastest route is direct, then only limited alternatives are shown." + NEWLINE);
    output.add("No alternative that takes over 15 minutes longer is shown." + NEWLINE);
    output.add("The number of trains per hour (tph) is also shown." + NEWLINE);
    output.add(NEWLINE);
    output.add("Station entry/exit times, buses and walking times are not included." + NEWLINE);
    output.add("Walking may affect the faster route on occasion, notably to Leicester Square, " + NEWLINE);
    output.add("which is easily accessed from the new proposed Shaftesbury Avenue exit of CR2." + NEWLINE);
    output.add(NEWLINE);
    output.add("The 'effective time' comparison adds some fudge factors to take into account low frequency " + NEWLINE);
    output.add("start service and an additional penalty per interchange. It is intended to be used as a rough metric " + NEWLINE);
    output.add("of what a more typical journey would be like (ie. a non-perfect one)." + NEWLINE);
    output.add("The 'total effective time' is the sum of all effective times modelled." + NEWLINE);
    output.add("It is a reasonable proxy for the total enhancement provided by the scheme." + NEWLINE);
    output.add(NEWLINE);
  }

  static void appendTotals(List<String> output, List<Station> starts, List<Station> ends, Model model) {
    appendSeparator(output);
    output.add("Total effective times" + NEWLINE);
    output.add("---------------------" + NEWLINE);
    int totalTotal = 0;
    int totalCurrentTotal = 0;
    for (Iterator<Station> it = starts.iterator(); it.hasNext();) {
      Station start = it.next();
      int totalPoints = 0;
      int totalCurrentPoints = 0;
      for (Station end : ends) {
        totalPoints += model.solve(start, end).points();
        totalCurrentPoints += CurrentSWLondonModel.MODEL.solve(start, end).points();
      }
      totalTotal += totalPoints;
      totalCurrentTotal += totalCurrentPoints;
      int pointsDiff = totalCurrentPoints - totalPoints;
      output.add("* From " + start.description() + ": " + totalPoints + "m - " + pointsDiff + "m better" + NEWLINE);
    }
    int totalPointsDiff = totalCurrentTotal - totalTotal;
    output.add("* TOTAL: " + totalTotal + "m - " + totalPointsDiff + "m better" + NEWLINE);
  }

  static void appendStations(List<String> output) {
    List<Station> stations = new ArrayList<>(Arrays.asList(Stations.values()));
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
    JourneyOptions solved = solve(start, end);
    Journey first = solved.first();
    StringBuilder buf = new StringBuilder();
    buf.append("From ").append(start.description()).append(" to ").append(end.description());
    int points = solved.points();
    if (this instanceof CurrentSWLondonModel) {
      buf.append(" (").append(points).append("m)").append(NEWLINE);
    } else {
      JourneyOptions currentOptions = CurrentSWLondonModel.MODEL.solve(start, end);
      int currentPoints = currentOptions.points();
      int timeChange = points - currentPoints;
      if (timeChange > 0) {
        buf.append(" (").append(timeChange).append("m worse)").append(NEWLINE);
      } else if (timeChange < 0) {
        buf.append(" (").append(-timeChange).append("m better)").append(NEWLINE);
      } else {
        buf.append(" (No change)").append(NEWLINE);
      }
    }
    buf.append(Strings.repeat("-", buf.length() - NEWLINE.length())).append(NEWLINE);

    int maxExcess = MAX_EXCESS;
    if (first.isDirect()) {
      maxExcess = MAX_EXCESS_WHEN_DIRECT;
    }
    for (Journey journey : solved.getJourneys()) {
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

  public JourneyOptions solve(Station start, Station end) {
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
    journeys.sort(Comparator.naturalOrder());
    return JourneyOptions.of(start, end, journeys);
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
