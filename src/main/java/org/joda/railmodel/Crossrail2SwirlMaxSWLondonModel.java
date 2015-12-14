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

import static org.joda.railmodel.Stations.*;

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
public class Crossrail2SwirlMaxSWLondonModel extends BaseLondonModel {

  public static void main(String[] args) throws Exception {
    Crossrail2SwirlMaxSWLondonModel model = new Crossrail2SwirlMaxSWLondonModel();
    ImmutableList<Station> starts = ImmutableList.of(
        CSS, LHD, EPS, SNL, WCP, MOT, SHP, FLW, KNG, HMC, SUR, NEM, RAY, WIM, EAD, UMD, USW, UTB, BAL, SRH, STE);
    ImmutableList<Station> ends = ImmutableList.of(
        VIC, TCR, EUS, AGL, WAT, UGP, UOX, BDS, CHX, ULS, UGS, UWS, UBS, UWM, UTM, ZFD, UBH, LBG, UBK, MOG, UOS, UHL, UCL, USP, CWF);

    List<String> output = new ArrayList<>();
    output.add("Modelling for SW London with Crossrail 2 Swirl-Max" + NEWLINE);
    output.add("==================================================" + NEWLINE);
    output.add("This uses CR2 via Earlsfield based on the [Swirl plan](http://ukrail.blogspot.co.uk/2015/11/crossrail-2-swirl.html), " + NEWLINE);
    output.add("with a separate branch to Balham, Streatham and on to Wimbledon via Haydons Road." + NEWLINE);
    output.add("It uses best efforts guesses for interchange times." + NEWLINE);
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
    output.add("Feel free to send a pull request for errors and enhancments!" + NEWLINE);

    File file = new File("CR2-SwirlMax-SWLondon.md");
    String result = Joiner.on("").join(output);
    Files.write(result, file, StandardCharsets.UTF_8);
    System.out.println(result);
  }

  Crossrail2SwirlMaxSWLondonModel() {
    setup();
  }

  private void setup() {
    // WAT
    Route wimwat = Route.of(
        "SWML",
        "WIM-WAT",
        18,
        stations(WIM, CLJ),
        times(6),
        SWML_CLJ_WAT_18);
    // 4tph from Twickenham/Loop
    Route twiwat = Route.of(
        "SWML",
        "TWI-WAT",
        4,
        stations(TWI, KNG, NEM, WIM),
        times(14, 7, 7),
        wimwat);
    // 4tph from Dorking/Guildford
    Route lhdwat = Route.of(
        "SWML",
        "LHD-WAT",
        4,
        stations(LHD, EPS, WCP, WIM),
        times(8, 7, 10),
        wimwat);
    // 10tph Surbiton
    Route surwat = Route.of(
        "SWML",
        "SUR-WAT (SUR-WIM-CLJ-VXH-WAT)",
        10,
        stations(SUR, WIM),
        times(7),
        wimwat);
    addRoute(twiwat);
    addRoute(lhdwat);
    addRoute(surwat);
    // CR2
    // CLJ-CKR known as 3 mins
    // CKR-VIC and VIC-TCR are similar distances
    Route wimsteagl = Route.of(
        "CR2",
        "CR2",
        10,
        stations(WIM, TOO, STE, BAL, CLJ),
        times(5, 3, 3, 3),
        CR2_CLJ_AGL);
    Route wimagl = Route.of(
        "CR2",
        "CR2",
        20,
        stations(WIM, EAD, CLJ),
        times(3, 3),
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
    addRoute(wimsteagl);

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
    addChange(Change.of(SUR, hmcagl, surwat, 3, 5));

    // change at Wimbledon
    Change xwimwatcr2 = Change.of(WIM, wimwat, wimagl, 1, 4);
    Change xwimcr2wat = Change.of(WIM, wimagl, wimwat, 1, 4);
    addChange(xwimwatcr2);
    addChange(xwimcr2wat);

    // change at Balham
    addChange(Change.of(BAL, NORTHERN_CITY_NB, wimsteagl, 2, 4));
    addChange(Change.of(BAL, wimsteagl, NORTHERN_CITY_NB, 2, 4));
    addChange(Change.of(BAL, NORTHERN_CITY_NB, SOUTHERN_BAL_VIC, 5, 7));
    addChange(Change.of(BAL, SOUTHERN_BAL_VIC, NORTHERN_CITY_NB, 4, 6));

    // change at Clapham Junction
    Change xcljwatcr2 = Change.of(CLJ, SWML_CLJ_WAT_18, CR2_CLJ_AGL, 4, 6);
    Change xcljcr2wat = Change.of(CLJ, CR2_CLJ_AGL, SWML_CLJ_WAT_18, 4, 6);
    addChange(xcljwatcr2);
    addChange(xcljcr2wat);
    addChange(Change.of(CLJ, SWML_CLJ_WAT_18, SOUTHERN_BAL_VIC, 4, 6));
    addChange(Change.of(CLJ, SOUTHERN_BAL_VIC, CR2_CLJ_AGL, 4, 6));
    addChange(Change.of(CLJ, SOUTHERN_BAL_VIC, SWML_CLJ_WAT_18, 4, 6));

    // prefer change at RAY to WIM/CLJ if choice
    addPreferredChange(xwimwatcr2, xcljwatcr2);
    addPreferredChange(xwimcr2wat, xcljcr2wat);

    // change at Victoria
    addChange(Change.of(VIC, CR2_VIC_AGL, DISTRICT_EB, 4, 6));
    addChange(Change.of(VIC, CR2_VIC_AGL, VICTORIA_NB, 4, 6));
    addChange(Change.of(VIC, VICTORIA_NB, DISTRICT_EB, 4, 6));
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

    // change at Streatham
    addChange(Change.of(STE, wimsteagl, THAMESLINK_STE_ZFD, 2, 4));
    addChange(Change.of(STE, wimsteagl, SOUTHERN_STE_LBG, 2, 4));

    commonChanges(NORTHERN_CITY_NB);
  }

}
