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

import static org.joda.railmodel.Station.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;

/**
 * Calculates journey times for SW London without CR2.
 */
public class CurrentSWLondonModel extends BaseLondonModel {

  static final CurrentSWLondonModel MODEL = new CurrentSWLondonModel();

  public static void main(String[] args) throws Exception {
    CurrentSWLondonModel model = MODEL;
    ImmutableList<Station> starts = ImmutableList.of(
        CSS, LHD, EPS, SNL, WCP, MOT, SHP, FLW, KNG, HMC, SUR, NEM, RAY, WIM, EAD, UMD, USW, UTB, BAL, SRH, STE);
    ImmutableList<Station> ends = ImmutableList.of(
        VIC, TCR, EUS, AGL, WAT, UGP, UOX, BDS, CHX, ULS, UGS, UWS, UBS, UWM, UTM, ZFD, UBH, LBG, UBK, MOG, UOS, UHL, UCL, USP, CWF);

    List<String> output = new ArrayList<>();
    output.add("Modelling for SW London without Crossrail 2" + NEWLINE);
    output.add("===========================================" + NEWLINE);
    output.add("This uses best efforts guesses of interchange times." + NEWLINE);
    output.add(NEWLINE);
    output.add("A selection of interesting journeys is listed, together with calculated route options." + NEWLINE);
    output.add("A key for station codes is at the end." + NEWLINE);
    output.add("The route options are sorted, with the fastest first." + NEWLINE);
    output.add("The excess over the fastest route option is listed in brackets." + NEWLINE);
    output.add("If the fastest route is direct, then only limited alternatives are shown." + NEWLINE);
    output.add("No alternative that takes over 15 minutes longer is shown." + NEWLINE);
    output.add("The number of trains per hour (tph) is also shown." + NEWLINE);
    output.add(NEWLINE);
    output.add("Station entry/exit times, buses and walking times are not included." + NEWLINE);
    output.add("Walking may affect the faster route on occasion." + NEWLINE);
    output.add(NEWLINE);
    output.add("The 'effective time' adds some fudge factors to take into account low frequency " +
        "start service and an additional penalty per interchange. It is intended to be used as a rough metric " +
        "of what a more typical journey would be like (ie. a non-perfect one)." + NEWLINE);
    output.add("The 'total effective time' is the sum of all effective times modelled." + NEWLINE);
    output.add("It is a reasonable proxy for the total enhancement provided by the scheme." + NEWLINE);
    output.add(NEWLINE);
    output.add("Total effective times" + NEWLINE);
    output.add("---------------------" + NEWLINE);
    int totalTotal = 0;
    for (Iterator<Station> it = starts.iterator(); it.hasNext();) {
      Station start = it.next();
      int totalPoints = 0;
      for (Station end : ends) {
        totalPoints += model.solve(start, end).points();
      }
      totalTotal += totalPoints;
      output.add("From " + start.description() + ": " + totalPoints + NEWLINE);
    }
    output.add("TOTAL: " + totalTotal + NEWLINE);
    appendSeparator(output);
    for (Iterator<Station> it = starts.iterator(); it.hasNext();) {
      Station start = it.next();
      for (Station end : ends) {
        String explain = model.explain(start, end);
        output.add(explain);
        output.add(NEWLINE);
      }
      if (it.hasNext()) {
        appendSeparator(output);
      }
    }
    appendSeparator(output);
    appendStations(output);
    output.add(NEWLINE);
    output.add("Feel free to send a pull request for errors and enhancments!." + NEWLINE);

    File file = new File("SWLondon.md");
    String result = Joiner.on("").join(output);
    Files.write(result, file, StandardCharsets.UTF_8);
    System.out.println(result);
  }

  CurrentSWLondonModel() {
    setup();
  }

  private void setup() {
    // WAT
    Route wimwat = Route.of(
        "SWML",
        "WIM-WAT",
        16,
        stations(WIM, EAD, CLJ),
        times(4, 4),
        SWML_CLJ_WAT_18);
    Route raywat = Route.of(
        "SWML",
        "RAY-WAT",
        12,
        stations(RAY, WIM),
        times(4),
        wimwat);
    Route nemwat = Route.of(
        "SWML",
        "NEM-WAT",
        6,
        stations(NEM, RAY),
        times(3),
        raywat);
    Route motwat = Route.of(
        "SWML",
        "MOT-WAT",
        6,
        stations(MOT, RAY),
        times(3),
        raywat);
    // this ignores the 07:21 from RAY to WAT
    // 4tph from TWI or Teddington, 2tph off peak
    Route twiwat = Route.of(
        "SWML",
        "TWI-WAT",
        4,
        stations(TWI, KNG, NEM),
        times(14, 7),
        nemwat);
    // 2tph consistently
    Route shpwat = Route.of(
        "SWML",
        "SHP-WAT",
        2,
        stations(SHP, FLW, KNG, NEM),
        times(13, 10, 7),
        nemwat);
    // 2tph consistently
    Route hmcwat = Route.of(
        "SWML",
        "HMC-WAT",
        2,
        stations(HMC, SUR, NEM),
        times(8, 5),
        nemwat);
    // fast lines
    Route surwat1 = Route.of(
        "SWML",
        "Surbiton Express",
        6,
        stations(SUR, WAT),
        times(20));
    // slow lines
    Route surwat2 = Route.of(
        "SWML",
        "SUR-WAT",
        2,
        stations(SUR, NEM, WIM),
        times(4, 5),
        wimwat);
    // 2tph consistently
    Route csswat = Route.of(
        "SWML",
        "CSS-WAT",
        2,
        stations(CSS, MOT),
        times(11),
        motwat);
    // 2tph start at Guildford
    // 2tph peak extras from Epsom
    Route epswat = Route.of(
        "SWML",
        "EPS-WAT",
        4,
        stations(EPS, SNL, WCP, MOT),
        times(5, 3, 3),
        motwat);
    // 2tph from Dorking
    Route lhdwat = Route.of(
        "SWML",
        "LHD-WAT",
        2,
        stations(LHD, EPS, WCP, RAY),
        times(8, 7, 6),
        raywat);

    addRoute(twiwat);
    addRoute(shpwat);
    addRoute(hmcwat);
    addRoute(surwat1);
    addRoute(surwat2);
    addRoute(csswat);
    addRoute(epswat);
    addRoute(lhdwat);

    // Southern
    addRoute(SOUTHERN_BAL_VIC);
    addRoute(SOUTHERN_SRH_VIC);
    addRoute(THAMESLINK_EPH_ZFD);
    addRoute(THAMESLINK_STE_ZFD);
    addRoute(SOUTHERN_STE_LBG);
    addRoute(SOUTHEAST_HNH_VIC);

    // Tube lines
    addRoute(NORTHERN_CITY_NB);
    addRoute(NORTHERN_CITY_SB);
    addRoute(NORTHERN_WEST_NB);
    addRoute(NORTHERN_WEST_SB);
    addRoute(VICTORIA_NB);
    addRoute(BAKERLOO_NB);
    addRoute(JUBILEE_EB);
    addRoute(JUBILEE_NB);
    addRoute(WNC_NB);
    addRoute(DISTRICT_EB);
    addRoute(CENTRAL_EB);
    addRoute(CENTRAL_WB);
    addRoute(CR1_EB);
    addRoute(CR1_WB);

    // change at Balham
    addChange(Change.of(BAL, SOUTHERN_BAL_VIC, NORTHERN_CITY_NB, 4, 6));

    // change at Clapham Junction
    addChange(Change.of(CLJ, SWML_CLJ_WAT_18, SOUTHERN_BAL_VIC, 4, 6));
    addChange(Change.of(CLJ, SOUTHERN_BAL_VIC, SWML_CLJ_WAT_18, 4, 6));

    // change at Victoria
    addChange(Change.of(VIC, VICTORIA_NB, DISTRICT_EB, 4, 6));
    addChange(Change.of(VIC, SOUTHERN_BAL_VIC, DISTRICT_EB, 4, 6));
    addChange(Change.of(VIC, SOUTHERN_BAL_VIC, VICTORIA_NB, 4, 6));
    addChange(Change.of(VIC, SOUTHEAST_HNH_VIC, DISTRICT_EB, 4, 6));
    addChange(Change.of(VIC, SOUTHEAST_HNH_VIC, VICTORIA_NB, 4, 6));

    // change at TCR
    addChange(Change.of(TCR, NORTHERN_WEST_NB, CR1_EB, 3, 5));
    addChange(Change.of(TCR, NORTHERN_WEST_NB, CR1_WB, 3, 5));
    addChange(Change.of(TCR, NORTHERN_WEST_NB, CENTRAL_EB, 4, 6));

    // change at Kennington
    Change xkennington = Change.of(UKN, NORTHERN_CITY_NB, NORTHERN_WEST_NB, 1, 2);
    addChange(xkennington);

    // change at Stockwell
    addChange(Change.of(UST, NORTHERN_CITY_NB, VICTORIA_NB, 1, 2));

    // change at Elephant & Castle
    Change xephnorthernbakerloo = Change.of(EPH, NORTHERN_CITY_NB, BAKERLOO_NB, 2, 4);
    addChange(xephnorthernbakerloo);
    addChange(Change.of(EPH, NORTHERN_CITY_NB, THAMESLINK_EPH_ZFD, 8, 16));

    // change at Herne Hill
    addChange(Change.of(HNH, THAMESLINK_STE_ZFD, SOUTHEAST_HNH_VIC, 1, 15));

    // change at Brixton
    addChange(Change.of(BRX, SOUTHEAST_HNH_VIC, VICTORIA_NB, 4, 6));

    // change at London Bridge
    addChange(Change.of(LBG, NORTHERN_CITY_NB, JUBILEE_EB, 2, 4));
    addChange(Change.of(LBG, NORTHERN_CITY_NB, JUBILEE_NB, 2, 4));
    addChange(Change.of(LBG, JUBILEE_EB, NORTHERN_CITY_NB, 2, 4));
    addChange(Change.of(LBG, JUBILEE_EB, NORTHERN_CITY_SB, 2, 4));
    addChange(Change.of(LBG, SOUTHERN_STE_LBG, NORTHERN_CITY_NB, 4, 6));
    addChange(Change.of(LBG, SOUTHERN_STE_LBG, NORTHERN_CITY_SB, 4, 6));
    addChange(Change.of(LBG, SOUTHERN_STE_LBG, JUBILEE_EB, 4, 6));
    addChange(Change.of(LBG, SOUTHERN_STE_LBG, JUBILEE_NB, 4, 6));

    // change at Bank
    addChange(Change.of(UBK, CENTRAL_EB, NORTHERN_CITY_NB, 4, 8));
    addChange(Change.of(UBK, CENTRAL_EB, NORTHERN_CITY_SB, 4, 8));
    addChange(Change.of(UBK, NORTHERN_CITY_NB, CENTRAL_EB, 4, 8));
    addChange(Change.of(UBK, NORTHERN_CITY_NB, CENTRAL_WB, 4, 8));
    addChange(Change.of(UBK, WNC_NB, CENTRAL_WB, 4, 8));
    addChange(Change.of(UBK, WNC_NB, NORTHERN_CITY_NB, 5, 9));
    addChange(Change.of(UBK, WNC_NB, NORTHERN_CITY_SB, 5, 9));

    // change at Farringdon
    addChange(Change.of(ZFD, THAMESLINK_EPH_ZFD, CR1_EB, 3, 5));
    addChange(Change.of(ZFD, THAMESLINK_EPH_ZFD, CR1_WB, 3, 5));

    // change at Moorgate
    addChange(Change.of(MOG, NORTHERN_CITY_NB, CR1_EB, 3, 5));
    addChange(Change.of(MOG, NORTHERN_CITY_NB, CR1_WB, 3, 5));
    addChange(Change.of(MOG, CR1_EB, NORTHERN_CITY_NB, 3, 5));
    addChange(Change.of(MOG, CR1_EB, NORTHERN_CITY_SB, 3, 5));

    // change at Vauxhall
    addChange(Change.of(VXH, SWML_CLJ_WAT_18, VICTORIA_NB, 3, 6));

    // change at Waterloo
    addChange(Change.of(WAT, SWML_CLJ_WAT_18, JUBILEE_EB, 3, 6));
    addChange(Change.of(WAT, SWML_CLJ_WAT_18, JUBILEE_NB, 3, 6));
    addChange(Change.of(WAT, SWML_CLJ_WAT_18, NORTHERN_WEST_NB, 3, 6));
    addChange(Change.of(WAT, SWML_CLJ_WAT_18, BAKERLOO_NB, 3, 6));
    addChange(Change.of(WAT, SWML_CLJ_WAT_18, WNC_NB, 3, 10));  // includes queuing for W&C
    Change xwatjubileenorthern = Change.of(WAT, JUBILEE_NB, NORTHERN_WEST_NB, 3, 5);
    Change xwatjubileebakerloo = Change.of(WAT, JUBILEE_NB, BAKERLOO_NB, 3, 5);
    addChange(xwatjubileenorthern);
    addChange(xwatjubileebakerloo);
    addPreferredChange(xkennington, xwatjubileenorthern);
    addPreferredChange(xephnorthernbakerloo, xwatjubileebakerloo);

    // change at Green Park
    addChange(Change.of(UGP, VICTORIA_NB, JUBILEE_NB, 4, 6));

    // change at Embankment (this is a fudge)
    addChange(Change.of(CHX, NORTHERN_WEST_NB, DISTRICT_EB, 3, 6));

    // change at Oxford Circus
    addChange(Change.of(UOX, VICTORIA_NB, BAKERLOO_NB, 1, 3));
    addChange(Change.of(UOX, BAKERLOO_NB, VICTORIA_NB, 1, 3));

  }

}
