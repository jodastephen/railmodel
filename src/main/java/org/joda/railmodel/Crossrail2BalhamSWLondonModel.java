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
 * Calculates journey times for SW London with Crossrail 2 in place.
 */
public class Crossrail2BalhamSWLondonModel extends BaseLondonModel {

  public static void main(String[] args) throws Exception {
    Crossrail2BalhamSWLondonModel model = new Crossrail2BalhamSWLondonModel();
    ImmutableList<Station> starts = ImmutableList.of(
        CSS, LHD, EPS, SNL, WCP, MOT, SHP, FLW, KNG, HMC, SUR, NEM, RAY, WIM, EAD, UMD, USW, UTB, BAL);
    ImmutableList<Station> ends = ImmutableList.of(
        VIC, TCR, EUS, AGL, WAT, UGP, UOX, BDS, CHX, ULS, UGS, UWS, UBS, UWM, UTM, ZFD, UBH, LBG, UBK, MOG, UOS, UHL, UCL, USP, CWF);

    List<String> output = new ArrayList<>();
    output.add("Modelling for SW London with Crossrail 2 via Balham" + NEWLINE);
    output.add("===================================================" + NEWLINE);
    output.add("This uses CR2 via Balham, with best efforts guesses of interchange times." + NEWLINE);
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
    output.add("Walking may affect the faster route on occasion, notably to Leicester Square, " +
        "which is easily accessed from the new proposed Shaftesbury Avenue exit of CR2." + NEWLINE);
    output.add(NEWLINE);
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
    appendStations(output);
    output.add(NEWLINE);
    output.add("Feel free to send a pull request for errors and enhancments!." + NEWLINE);

    File file = new File("CR2-SWLondon.md");
    String result = Joiner.on("").join(output);
    Files.write(result, file, StandardCharsets.UTF_8);
    System.out.println(result);
  }

  Crossrail2BalhamSWLondonModel() {
    setup();
  }

  private void setup() {
    // WAT
    addRoute(SWML_TWI_WAT_4);
    addRoute(SWML_LHD_WAT_4);
    addRoute(SWML_SUR_WAT_FAST_6);
    addRoute(SWML_SUR_WAT_SLOW_4);
    // CR2
    // CLJ-CKR known as 3 mins
    // CKR-VIC and VIC-TCR are similar distances
    // BAL-CLJ set at 3mins although 4mins more likely
    // WIM-BAL set at 4mins
    Route wimagl = Route.of(
        "CR2",
        "CR2",
        30,
        stations(WIM, BAL, CLJ),
        times(4, 3),
        CR2_CLJ_AGL);
    Route rayagl = Route.of(
        "CR2",
        "CR2",
        20,
        stations(RAY, WIM),
        times(4),
        wimagl);
    Route nemagl = Route.of(
        "CR2",
        "CR2",
        10,
        stations(NEM, RAY),
        times(3),
        rayagl);
    Route motagl = Route.of(
        "CR2",
        "CR2",
        10,
        stations(MOT, RAY),
        times(3),
        rayagl);
    Route kngagl = Route.of(
        "CR2",
        "CR2",
        6,
        stations(KNG, NEM),
        times(7),
        nemagl);
    Route shpagl = Route.of(
        "CR2",
        "CR2",
        4,
        stations(SHP, FLW, KNG),
        times(13, 10),
        kngagl);
    Route hmcagl = Route.of(
        "CR2",
        "CR2",
        4,
        stations(HMC, SUR, NEM),
        times(8, 5),
        nemagl);
    Route cssagl = Route.of(
        "CR2",
        "CR2",
        4,
        stations(CSS, MOT),
        times(11),
        motagl);
    Route epsagl = Route.of(
        "CR2",
        "CR2",
        6,
        stations(EPS, SNL, WCP, MOT),
        times(5, 3, 3),
        motagl);
    addRoute(shpagl);
    addRoute(kngagl);
    addRoute(hmcagl);
    addRoute(cssagl);
    addRoute(epsagl);

    // Southern
    addRoute(SOUTHERN_BAL_VIC);
    addRoute(THAMESLINK_EPH_ZFD);

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

    // change CR2 to WAT at SUR, assume 6 tracks between Surbiton and New Malden
    // gaps between fast trains work out at 4-6 mins with 10min gap twice an hour
    // assume a sensible timetable minimises interchange time
    addChange(Change.of(SUR, hmcagl, SWML_SUR_WAT_FAST_6, 3, 5));
    addChange(Change.of(SUR, hmcagl, SWML_SUR_WAT_SLOW_4, 3, 5));

    // change CR2 to WAT at RAY, assume 8tph at gaps of 6 and 9 minutes
    Change xraycr2wat = Change.of(RAY, rayagl, SWML_RAY_WAT_8, 1, 9);
    addChange(xraycr2wat);
    // change WAT to CR2 at RAY, 20tph at gaps of 2 and 4 minutes
    Change xraywatcr2 = Change.of(RAY, SWML_RAY_WAT_8, rayagl, 1, 5);
    addChange(xraywatcr2);

    // change at Wimbledon
    Change xwimwatcr2 = Change.of(WIM, SWML_WIM_WAT_12, wimagl, 4, 6);
    Change xwimcr2wat = Change.of(WIM, wimagl, SWML_WIM_WAT_12, 4, 10);
    addChange(xwimwatcr2);
    addChange(xwimcr2wat);  // gaps of 3 to 6 mins

    // change at Balham
    addChange(Change.of(BAL, wimagl, NORTHERN_CITY_NB, 2, 4));
    addChange(Change.of(BAL, NORTHERN_CITY_NB, wimagl, 2, 4));

    // change at Clapham Junction
    Change xcljwatcr2 = Change.of(CLJ, SWML_CLJ_WAT_18, CR2_CLJ_AGL, 4, 6);
    addChange(xcljwatcr2);
    addChange(Change.of(CLJ, SWML_CLJ_WAT_18, SOUTHERN_BAL_VIC, 4, 6));
    addChange(Change.of(CLJ, SOUTHERN_BAL_VIC, CR2_CLJ_AGL, 4, 6));
    addChange(Change.of(CLJ, SOUTHERN_BAL_VIC, SWML_CLJ_WAT_18, 4, 6));

    // prefer change at RAY to WIM/CLJ if choice
    addPreferredChange(xraywatcr2, xwimwatcr2);
    addPreferredChange(xraywatcr2, xcljwatcr2);
    addPreferredChange(xraycr2wat, xwimcr2wat);

    // change at Victoria
    addChange(Change.of(VIC, CR2_VIC_AGL, DISTRICT_EB, 4, 6));
    addChange(Change.of(VIC, CR2_VIC_AGL, VICTORIA_NB, 4, 6));
    addChange(Change.of(VIC, SOUTHERN_BAL_VIC, DISTRICT_EB, 4, 6));
    addChange(Change.of(VIC, SOUTHERN_BAL_VIC, VICTORIA_NB, 4, 6));

    // change at TCR
    addChange(Change.of(TCR, CR2_VIC_AGL, CR1_EB, 3, 5));
    addChange(Change.of(TCR, CR2_VIC_AGL, CR1_WB, 3, 5));
    addChange(Change.of(TCR, CR2_VIC_AGL, CENTRAL_EB, 4, 6));
    addChange(Change.of(TCR, CR2_VIC_AGL, NORTHERN_WEST_NB, 3, 5));
    addChange(Change.of(TCR, CR2_VIC_AGL, NORTHERN_WEST_SB, 3, 5));

    // change at Euston (pointless, might as well change at Angel)
    // addChange(Change.of(EUS, wimagl, unortherncitysb, 4, 8));

    // change at Angel
    addChange(Change.of(AGL, CR2_VIC_AGL, NORTHERN_CITY_SB, 3, 6));

    // change at Kennington
    addChange(Change.of(UKN, NORTHERN_CITY_NB, NORTHERN_WEST_NB, 1, 2));

    // change at Stockwell
    addChange(Change.of(UST, NORTHERN_CITY_NB, VICTORIA_NB, 1, 2));

    // change at Elephant & Castle
    addChange(Change.of(EPH, NORTHERN_CITY_NB, BAKERLOO_NB, 2, 4));
    addChange(Change.of(EPH, NORTHERN_CITY_NB, THAMESLINK_EPH_ZFD, 8, 16));

    // change at London Bridge
    addChange(Change.of(LBG, NORTHERN_CITY_NB, JUBILEE_EB, 2, 4));
    addChange(Change.of(LBG, NORTHERN_CITY_NB, JUBILEE_NB, 2, 4));
    addChange(Change.of(LBG, JUBILEE_EB, NORTHERN_CITY_NB, 2, 4));
    addChange(Change.of(LBG, JUBILEE_EB, NORTHERN_CITY_SB, 2, 4));

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

    // change at Green Park
    addChange(Change.of(UGP, VICTORIA_NB, JUBILEE_NB, 4, 6));

    // change at Embankment (this is a fudge)
    addChange(Change.of(CHX, NORTHERN_WEST_NB, DISTRICT_EB, 3, 6));

    // change at Oxford Circus
    addChange(Change.of(UOX, VICTORIA_NB, BAKERLOO_NB, 1, 3));
    addChange(Change.of(UOX, BAKERLOO_NB, VICTORIA_NB, 1, 3));

  }

}
