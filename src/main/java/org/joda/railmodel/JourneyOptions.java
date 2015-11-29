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

import java.util.List;

/**
 * A set of journey options.
 */
public final class JourneyOptions {

  private final Station start;
  private final Station end;
  private final List<Journey> options;

  //-------------------------------------------------------------------------
  public static JourneyOptions of(Station start, Station end, List<Journey> options) {
    return new JourneyOptions(start, end, options);
  }

  //-------------------------------------------------------------------------
  public JourneyOptions(Station start, Station end, List<Journey> options) {
    this.start = start;
    this.end = end;
    this.options = options;
  }

  //-------------------------------------------------------------------------
  public Station getStart() {
    return start;
  }

  public Station getEnd() {
    return end;
  }

  public List<Journey> getJourneys() {
    return options;
  }

  //-------------------------------------------------------------------------
  public Journey first() {
    if (options.isEmpty()) {
      throw new IllegalStateException("No options found: " + start + "-" + end);
    }
    return options.get(0);
  }

  public int points() {
    int base = first().points();
    if (options.size() >= 2) {
      return Math.min(base, options.get(1).points());
    }
    return base;
  }

}
