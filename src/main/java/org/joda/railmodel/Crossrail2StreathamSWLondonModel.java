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
public class Crossrail2StreathamSWLondonModel extends BaseLondonModel {

  public static void main(String[] args) throws Exception {
    Crossrail2StreathamSWLondonModel model = new Crossrail2StreathamSWLondonModel();

    model.explain(STE, WAT);

    ImmutableList<Station> starts = ImmutableList.of(
        CSS, LHD, EPS, SNL, WCP, MOT, SHP, FLW, KNG, HMC, SUR, NEM, RAY, WIM, EAD, UMD, USW, UTB, BAL, SRH, STE);
    ImmutableList<Station> ends = ImmutableList.of(
        VIC, TCR, EUS, AGL, WAT, UGP, UOX, BDS, CHX, ULS, UGS, UWS, UBS, UWM, UTM, ZFD, UBH, LBG, UBK, MOG, UOS, UHL, UCL, USP, CWF);

    List<String> output = new ArrayList<>();
    output.add("Modelling for SW London with Crossrail 2 via Streatham" + NEWLINE);
    output.add("======================================================" + NEWLINE);
    output.add("This uses CR2 via Tooting (mainline) and Streatham, with best efforts guesses of interchange times." + NEWLINE);
    output.add("This route is promoted by various groups in Streatham." + NEWLINE);
    output.add("It adds at least 6 minutes to all journeys on Crossrail 2 between Wimbledon and Clapham Junction." + NEWLINE);
    output.add("As such, no matter what benefits it gives Streatham, it simply will not happen." + NEWLINE);
    output.add(NEWLINE);
    appendDocs(output);
    appendTotals(output, starts, ends, model);
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

    File file = new File("CR2-Streatham-SWLondon.md");
    String result = Joiner.on("").join(output);
    Files.write(result, file, StandardCharsets.UTF_8);
    System.out.println(result);
  }

  Crossrail2StreathamSWLondonModel() {
    setup();
  }

  private void setup() {
    // WAT
    Route cljwat = SWML_CLJ_WAT_18;
    Route wimwat = SWML_WIM_WAT_12;
    Route raywat = SWML_RAY_WAT_8;
    Route twiwat = SWML_TWI_WAT_4;
    Route lhdwat = SWML_LHD_WAT_4;
    Route surwat1 = SWML_SUR_WAT_FAST_6;
    Route surwat2 = SWML_SUR_WAT_SLOW_4;
    addRoute(twiwat);
    addRoute(lhdwat);
    addRoute(surwat1);
    addRoute(surwat2);
    // CR2
    // CLJ-CKR known as 3 mins
    // CKR-VIC and VIC-TCR are similar distances
    Route wimagl = Route.of(
        "CR2",
        "CR2",
        30,
        stations(WIM, TOO, STE, CLJ),
        times(4, 3, 6),
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

    // change CR2 to WAT at SUR, assume 6 tracks between Surbiton and New Malden
    // gaps between fast trains work out at 4-6 mins with 10min gap twice an hour
    // assume a sensible timetable minimises interchange time
    addChange(Change.of(SUR, hmcagl, surwat1, 3, 5));
    addChange(Change.of(SUR, hmcagl, surwat2, 3, 5));

    // change CR2 to WAT at RAY, assume 8tph at gaps of 6 and 9 minutes
    Change xraycr2wat = Change.of(RAY, rayagl, raywat, 1, 9);
    addChange(xraycr2wat);
    // change WAT to CR2 at RAY, 20tph at gaps of 2 and 4 minutes
    Change xraywatcr2 = Change.of(RAY, raywat, rayagl, 1, 5);
    addChange(xraywatcr2);

    // change at Wimbledon
    Change xwimwatcr2 = Change.of(WIM, wimwat, wimagl, 4, 6);
    Change xwimcr2wat = Change.of(WIM, wimagl, wimwat, 4, 10);
    addChange(xwimwatcr2);
    addChange(xwimcr2wat);  // gaps of 3 to 6 mins

    // change at Balham
    addChange(Change.of(BAL, SOUTHERN_BAL_VIC, NORTHERN_CITY_NB, 4, 6));

    // change at Streatham
    addChange(Change.of(STE, wimagl, THAMESLINK_STE_ZFD, 3, 5));

    // change at Clapham Junction
    Change xcljwatcr2 = Change.of(CLJ, cljwat, CR2_CLJ_AGL, 4, 6);
    addChange(xcljwatcr2);
    addChange(Change.of(CLJ, cljwat, SOUTHERN_BAL_VIC, 4, 6));
    addChange(Change.of(CLJ, SOUTHERN_BAL_VIC, CR2_CLJ_AGL, 4, 6));
    addChange(Change.of(CLJ, SOUTHERN_BAL_VIC, cljwat, 4, 6));
    addChange(Change.of(CLJ, CR2_CLJ_AGL, cljwat, 4, 6));

    // prefer change at RAY to WIM/CLJ if choice
    addPreferredChange(xraywatcr2, xwimwatcr2);
//    addPreferredChange(xraywatcr2, xcljwatcr2);
    addPreferredChange(xraycr2wat, xwimcr2wat);

    // change at Victoria
    addChange(Change.of(VIC, CR2_VIC_AGL, DISTRICT_EB, 4, 6));
    addChange(Change.of(VIC, CR2_VIC_AGL, VICTORIA_NB, 4, 6));
    addChange(Change.of(VIC, SOUTHERN_BAL_VIC, DISTRICT_EB, 4, 6));
    addChange(Change.of(VIC, SOUTHERN_BAL_VIC, VICTORIA_NB, 4, 6));
    addChange(Change.of(VIC, SOUTHEAST_HNH_VIC, DISTRICT_EB, 4, 6));
    addChange(Change.of(VIC, SOUTHEAST_HNH_VIC, VICTORIA_NB, 4, 6));

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

    // change at Herne Hill
    addChange(Change.of(HNH, THAMESLINK_STE_ZFD, SOUTHEAST_HNH_VIC, 1, 15));

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
    addChange(Change.of(VXH, cljwat, VICTORIA_NB, 3, 6));

    // change at Waterloo
    addChange(Change.of(WAT, cljwat, JUBILEE_EB, 3, 6));
    addChange(Change.of(WAT, cljwat, JUBILEE_NB, 3, 6));
    addChange(Change.of(WAT, cljwat, NORTHERN_WEST_NB, 3, 6));
    addChange(Change.of(WAT, cljwat, BAKERLOO_NB, 3, 6));
    addChange(Change.of(WAT, cljwat, WNC_NB, 3, 10));  // includes queuing for W&C
    addChange(Change.of(WAT, JUBILEE_NB, NORTHERN_WEST_NB, 3, 5));
    addChange(Change.of(WAT, JUBILEE_NB, BAKERLOO_NB, 3, 5));

    // change at Brixton
    addChange(Change.of(BRX, SOUTHEAST_HNH_VIC, VICTORIA_NB, 4, 6));

    // change at Brixton
    addChange(Change.of(BRX, SOUTHEAST_HNH_VIC, VICTORIA_NB, 4, 6));

    // change at Green Park
    addChange(Change.of(UGP, VICTORIA_NB, JUBILEE_NB, 4, 6));

    // change at Embankment (this is a fudge)
    addChange(Change.of(CHX, NORTHERN_WEST_NB, DISTRICT_EB, 3, 6));

    // change at Oxford Circus
    addChange(Change.of(UOX, VICTORIA_NB, BAKERLOO_NB, 1, 3));
    addChange(Change.of(UOX, BAKERLOO_NB, VICTORIA_NB, 1, 3));

  }

}
