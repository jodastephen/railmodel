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
public class Crossrail2TootingSWLondonModel extends Model {

  public static void main(String[] args) throws Exception {
    Crossrail2TootingSWLondonModel model = new Crossrail2TootingSWLondonModel();
    ImmutableList<Station> starts = ImmutableList.of(
        CSS, LHD, EPS, SNL, WCP, MOT, SHP, FLW, KNG, HMC, SUR, NEM, RAY, WIM, UMD, USW, UTB);
    ImmutableList<Station> ends = ImmutableList.of(
        VIC, TCR, EUS, AGL, WAT, UGP, UOX, CHX, ULS, UGS, UWS, UBS, UWM, UTM, ZFD, UBH, LBG, UBK, MOG, UOS, UCL, USP);

    List<String> output = new ArrayList<>();
    output.add("Modelling for SW London with Crossrail 2" + NEWLINE);
    output.add("========================================" + NEWLINE);
    output.add("This uses CR2 via Tooting Broadway, with best efforts guesses of interchange times." + NEWLINE);
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

    File file = new File("CR2-Tooting-SWLondon.md");
    String result = Joiner.on("").join(output);
    Files.write(result, file, StandardCharsets.UTF_8);
    System.out.println(result);
  }

  Crossrail2TootingSWLondonModel() {
    setup();
  }

  private void setup() {
    // WAT
    Route cljwat = Route.of(
        "SWML",
        "CLJ-WAT",
        18,
        stations(CLJ, VXH, WAT),
        times(5, 6));
    Route wimwat = Route.of(
        "SWML",
        "WIM-WAT",
        12,
        stations(WIM, EAD, CLJ),
        times(4, 4),
        cljwat);
    Route raywat = Route.of(
        "SWML",
        "RAY-WAT",
        8,
        stations(RAY, WIM),
        times(4),
        wimwat);
    // 4tph from Twickenham/Loop
    Route twiwat = Route.of(
        "SWML",
        "TWI-WAT",
        4,
        stations(TWI, KNG, NEM, RAY),
        times(14, 7, 3),
        raywat);
    // 4tph from Dorking/Guildford
    Route lhdwat = Route.of(
        "SWML",
        "LHD-WAT",
        4,
        stations(LHD, EPS, WCP, RAY),
        times(8, 7, 6),
        raywat);
    // 6tph faster Surbiton, guess -2mins for not stopping WIM/EAD
    Route surwat1 = Route.of(
        "SWML fast",
        "SUR-WAT (fast)",
        6,
        stations(SUR, CLJ),
        times(13),
        cljwat);
    // 4tph slower Surbiton, guess -2mins for not stopping NEM
    Route surwat2 = Route.of(
        "SWML",
        "SUR-WAT (slow)",
        4,
        stations(SUR, WIM),
        times(7),
        wimwat);
    addRoute(twiwat);
    addRoute(lhdwat);
    addRoute(surwat1);
    addRoute(surwat2);
    // CR2
    // CLJ-CKR known as 3 mins
    // CKR-VIC and VIC-TCR are similar distances
    // UTB-CLJ set at 4mins
    // WIM-UTB set at 3mins
    Route wimagl = Route.of(
        "CR2",
        "CR2",
        30,
        stations(WIM, UTB, CLJ, CKR, VIC, TCR, EUS, STP, AGL),
        times(3, 4, 3, 3, 3, 2, 0, 3));
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
    Route balvic = Route.of(
        "Southern",
        "BAL-VIC",
        12,
        stations(BAL, CLJ, VIC),
        times(6, 10));
    addRoute(balvic);

    // Tube lines
    Route unortherncity = Route.of(
        "Northern (City)",
        "Northern (City)",
        30,
        stations(UMD, USW, UTB, BAL, UCS, UST, UKN, EPH, UBH, LBG, UBK, MOG, UOS, AGL, STP, EUS),
        times(2, 4, 4, 2, 5, 5, 2, 1, 2, 2, 2, 1, 3, 2, 2));
    Route unortherncitysb = Route.of(
        "Northern (City)",
        "Northern (City) Southbound",
        30,
        stations(EUS, STP, AGL, UOS, MOG, UBK, LBG, UBH),
        times(2, 2, 3, 1, 2, 2, 2));
    Route unorthernwest = Route.of(
        "Northern (West End)",
        "Northern (West End)",
        30,
        stations(UKN, WAT, CHX, ULS, TCR, UGS, UWS, EUS),
        times(2, 3, 2, 1, 1, 2, 1));
    Route uvictoria = Route.of(
        "Victoria",
        "Victoria",
        36,
        stations(UST, VXH, VIC, UGP, UOX, UWS, EUS),
        times(2, 3, 2, 2, 2, 1));
    Route ubakerloo = Route.of(
        "Bakerloo",
        "Bakerloo",
        27,
        stations(EPH, WAT, CHX, UOX, UBS),
        times(4, 2, 4, 4));
    Route ujubilee = Route.of(
        "Jubilee",
        "Jubilee",
        36,
        stations(UBS, BDS, UGP, UWM, WAT, LBG, CWF),
        times(2, 2, 2, 2, 3, 7));
    Route ujubileenb = Route.of(
        "Jubilee",
        "Jubilee (Northbound)",
        36,
        stations(LBG, WAT, UWM, UGP, BDS, UBS),
        times(3, 2, 2, 2, 2));
    Route uwandc = Route.of(
        "W&C",
        "W&C",
        24,
        stations(WAT, UBK),
        times(4));
    // district not included WIM-VIC (29mins), complicates model and causes errors for fairly slow journey
    Route udistrict = Route.of(
        "District",
        "District",
        27,  // only 9tph from WIM-VIC, but terrible journey time so does not matter
        stations(VIC, UWM, CHX, UTM, UBK),   // fudge Embankment as Charing Cross, Monument as Bank
        times(4, 2, 2, 6));
    Route ucentral = Route.of(
        "Central",
        "Central",
        36,
        stations(BDS, UOX, TCR, UHL, UCL, USP, UBK, LST),
        times(1, 1, 2, 2, 2, 2, 2));
    Route ucentralwb = Route.of(
        "Central",
        "Central (Westbound)",
        36,
        stations(UBK, USP, UCL, UHL),
        times(2, 2, 2));
    Route cr1 = Route.of(
        "CR1",
        "CR1",
        30,
        stations(BDS, TCR, ZFD, MOG, LST, CWF),
        times(2, 3, 2, 0, 7));
    addRoute(unortherncity);
    addRoute(unortherncitysb);
    addRoute(unorthernwest);
    addRoute(uvictoria);
    addRoute(ubakerloo);
    addRoute(ujubilee);
    addRoute(ujubileenb);
    addRoute(uwandc);
    addRoute(udistrict);
    addRoute(ucentral);
    addRoute(ucentralwb);
    addRoute(cr1);

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

    // change at Tooting Broadway
    addChange(Change.of(UTB, wimagl, unortherncity, 2, 4));
    addChange(Change.of(UTB, unortherncity, wimagl, 2, 4));

    // change at Balham
    addChange(Change.of(BAL, unortherncity, balvic, 5, 7));

    // change at Clapham Junction
    Change xcljwatcr2 = Change.of(CLJ, cljwat, wimagl, 4, 6);
    addChange(xcljwatcr2);
    addChange(Change.of(CLJ, cljwat, balvic, 4, 6));
    addChange(Change.of(CLJ, balvic, wimagl, 4, 6));
    addChange(Change.of(CLJ, balvic, cljwat, 4, 6));

    // prefer change at RAY to WIM/CLJ if choice
    addPreferredChange(xraywatcr2, xwimwatcr2);
    addPreferredChange(xraywatcr2, xcljwatcr2);
    addPreferredChange(xraycr2wat, xwimcr2wat);

    // change at Victoria
    addChange(Change.of(VIC, wimagl, udistrict, 4, 6));
    addChange(Change.of(VIC, wimagl, uvictoria, 4, 6));
    addChange(Change.of(VIC, balvic, udistrict, 4, 6));
    addChange(Change.of(VIC, balvic, uvictoria, 4, 6));

    // change at TCR
    addChange(Change.of(TCR, wimagl, cr1, 3, 5));
    addChange(Change.of(TCR, wimagl, ucentral, 4, 6));

    // change at Euston (pointless, might as well change at Angel)
    // addChange(Change.of(EUS, wimagl, unortherncitysb, 4, 8));

    // change at Angel
    addChange(Change.of(AGL, wimagl, unortherncitysb, 3, 6));

    // change at Kennington
    addChange(Change.of(UKN, unortherncity, unorthernwest, 1, 2));

    // change at Stockwell
    addChange(Change.of(UST, unortherncity, uvictoria, 1, 2));

    // change at Elephant & Castle
    addChange(Change.of(EPH, unortherncity, ubakerloo, 2, 4));

    // change at London Bridge
    addChange(Change.of(LBG, unortherncity, ujubilee, 2, 4));
    addChange(Change.of(LBG, unortherncity, ujubileenb, 2, 4));
    addChange(Change.of(LBG, ujubilee, unortherncity, 2, 4));
    addChange(Change.of(LBG, ujubilee, unortherncitysb, 2, 4));

    // change at Bank
    addChange(Change.of(UBK, ucentral, unortherncity, 4, 8));
    addChange(Change.of(UBK, ucentral, unortherncitysb, 4, 8));
    addChange(Change.of(UBK, unortherncity, ucentral, 4, 8));
    addChange(Change.of(UBK, unortherncity, ucentralwb, 4, 8));
    addChange(Change.of(UBK, uwandc, ucentralwb, 4, 8));
    addChange(Change.of(UBK, uwandc, unortherncity, 5, 9));
    addChange(Change.of(UBK, uwandc, unortherncitysb, 5, 9));

    // change at Moorgate
    addChange(Change.of(MOG, unortherncity, cr1, 3, 5));
    addChange(Change.of(MOG, cr1, unortherncity, 3, 5));
    addChange(Change.of(MOG, cr1, unortherncitysb, 3, 5));

    // change at Vauxhall
    addChange(Change.of(VXH, cljwat, uvictoria, 3, 6));

    // change at Waterloo
    addChange(Change.of(WAT, cljwat, ujubilee, 3, 6));
    addChange(Change.of(WAT, cljwat, ujubileenb, 3, 6));
    addChange(Change.of(WAT, cljwat, unorthernwest, 3, 6));
    addChange(Change.of(WAT, cljwat, ubakerloo, 3, 6));
    addChange(Change.of(WAT, cljwat, uwandc, 3, 10));  // includes queuing for W&C

    // change at Green Park
    addChange(Change.of(UGP, uvictoria, ujubileenb, 4, 6));

    // change at Oxford Circus
    addChange(Change.of(UOX, uvictoria, ubakerloo, 1, 3));
    addChange(Change.of(UOX, ubakerloo, uvictoria, 1, 3));

  }

}
