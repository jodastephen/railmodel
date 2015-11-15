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

/**
 * Calculates journey times for SW London with Crossrail 2 in place.
 */
public class Crossrail2SWLondonModel extends Model {

  public static void main(String[] args) {
    Crossrail2SWLondonModel model = new Crossrail2SWLondonModel();
    System.out.println("Setup complete");
    System.out.println(model.explain(NEM, VIC));
    System.out.println(model.explain(RAY, TCR));

    System.out.println(model.explain(WIM, VIC));
    System.out.println(model.explain(WIM, TCR));
    System.out.println(model.explain(WIM, EUS));
    System.out.println(model.explain(WIM, AGL));
    System.out.println(model.explain(WIM, MOG));
    System.out.println(model.explain(WIM, UBK));
    System.out.println(model.explain(WIM, LBG));
    System.out.println(model.explain(WIM, CWF));

    System.out.println(model.explain(BAL, VIC));
    System.out.println(model.explain(BAL, TCR));
    System.out.println(model.explain(BAL, EUS));
    System.out.println(model.explain(BAL, AGL));
    System.out.println(model.explain(BAL, MOG));
    System.out.println(model.explain(BAL, UBK));
    System.out.println(model.explain(BAL, LBG));
    System.out.println(model.explain(BAL, CWF));

    System.out.println(model.explain(KNG, WAT));
    System.out.println(model.explain(KNG, UBK));
    System.out.println(model.explain(KNG, LBG));
    System.out.println(model.explain(KNG, UGP));
    System.out.println(model.explain(KNG, CWF));

    System.out.println(model.explain(CSS, WAT));
    System.out.println(model.explain(CSS, UBK));
    System.out.println(model.explain(CSS, LBG));
    System.out.println(model.explain(CSS, UGP));
    System.out.println(model.explain(CSS, CWF));

    System.out.println(model.explain(UMD, CWF));
    System.out.println(model.explain(UMD, VIC));
    System.out.println(model.explain(UMD, TCR));
    System.out.println(model.explain(UMD, EUS));
  }

  Crossrail2SWLondonModel() {
    setup();
  }

  private void setup() {
    // WAT
    Route cljwat = Route.of(
        "CLJ-WAT",
        18,
        stations(CLJ, VXH, WAT),
        times(5, 6));
    Route wimwat = Route.of(
        "WIM-WAT",
        12,
        stations(WIM, EAD, CLJ),
        times(4, 4),
        cljwat);
    Route raywat = Route.of(
        "RAY-WAT",
        8,
        stations(RAY, WIM),
        times(4),
        wimwat);
    // 4tph from Twickenham/Loop
    Route twiwat = Route.of(
        "TWI-WAT",
        4,
        stations(TWI, KNG, NEM, RAY),
        times(14, 7, 3),
        raywat);
    // 4tph from Dorking/Guildford
    Route lhdwat = Route.of(
        "LHD-WAT",
        4,
        stations(LHD, EPS, WCP, RAY),
        times(8, 7, 6),
        raywat);
    // 6tph faster Surbiton, guess -2mins for not stopping WIM/EAD
    Route surwat1 = Route.of(
        "SUR-WAT (fast)",
        6,
        stations(SUR, CLJ),
        times(13),
        cljwat);
    // 4tph slower Surbiton, guess -2mins for not stopping NEM
    Route surwat2 = Route.of(
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
    Route wimagl = Route.of(
        "CR2 (from WIM)",
        30,
        stations(WIM, BAL, CLJ, CKR, VIC, TCR, EUS, AGL),
        times(4, 4, 3, 3, 3, 2, 3));
    Route rayagl = Route.of(
        "CR2 (from RAY)",
        20,
        stations(RAY, WIM),
        times(4),
        wimagl);
    Route nemagl = Route.of(
        "CR2 (from NEM)",
        10,
        stations(NEM, RAY),
        times(3),
        rayagl);
    Route motagl = Route.of(
        "CR2 (from MOT)",
        10,
        stations(MOT, RAY),
        times(3),
        rayagl);
    Route kngagl = Route.of(
        "CR2 (from KNG)",
        6,
        stations(KNG, NEM),
        times(7),
        nemagl);
    Route shpagl = Route.of(
        "CR2 (from SHP)",
        4,
        stations(SHP, FLW, KNG),
        times(13, 10),
        kngagl);
    Route hmcagl = Route.of(
        "CR2 (from HMC)",
        4,
        stations(HMC, SUR, NEM),
        times(8, 5),
        nemagl);
    Route cssagl = Route.of(
        "CR2 (from CSS)",
        4,
        stations(CSS, MOT),
        times(11),
        motagl);
    Route epsagl = Route.of(
        "CR2 (from EPS)",
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
        "BAL-VIC",
        12,
        stations(BAL, CLJ, VIC),
        times(6, 10));
    addRoute(balvic);

    // Tube lines
    Route unortherncity = Route.of(
        "Northern (City)",
        30,
        stations(UMD, USW, UTB, BAL, UCS, UST, UKN, UEC, LBG, UBK, MOG, AGL, EUS),
        times(2, 4, 4, 2, 5, 5, 2, 3, 2, 2, 4, 4));
    Route unortherncitysb = Route.of(
        "Northern (City) Southbound",
        30,
        stations(EUS, AGL, MOG, UBK, LBG),
        times(4, 4, 2, 2));
    Route unorthernwest = Route.of(
        "Northern (West End)",
        30,
        stations(UKN, WAT, CHX, ULS, TCR, EUS),
        times(2, 3, 2, 1, 4));
    Route uvictoria = Route.of(
        "Victoria",
        36,
        stations(UST, VXH, VIC, UGP, UOX, EUS),
        times(2, 3, 2, 2, 3));
    Route ubakerloo = Route.of(
        "Bakerloo",
        27,
        stations(UEC, WAT, CHX, UOX, UBS),
        times(4, 2, 4, 4));
    Route ujubilee = Route.of(
        "Jubilee",
        36,
        stations(UBS, BDS, UGP, WAT, LBG, CWF),
        times(2, 2, 4, 3, 7));
    Route ujubileenb = Route.of(
        "Jubilee (Northbound)",
        36,
        stations(LBG, WAT, UGP, BDS),
        times(3, 4, 2));
    Route uwandc = Route.of(
        "W&C",
        24,
        stations(WAT, UBK),
        times(4));
    Route udistrict = Route.of(  // TODO: extend district to Wimbledon
        "District",
        27,
        stations(VIC, CHX, UBK),   // fudge Embankment as Charing Cross, Monument as Bank
        times(6, 8));
    Route ucentral = Route.of(
        "Central",
        36,
        stations(BDS, UOX, TCR, USP, UBK, LST),
        times(1, 1, 6, 2, 2));
    Route cr1 = Route.of(
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
    addRoute(cr1);

    // change CR2 to WAT at SUR, assume 6 tracks between Surbiton and New Malden
    // gaps between fast trains work out at 4-6 mins with 10min gap twice an hour
    // assume a sensible timetable minimises interchange time
    addChange(Change.of(SUR, hmcagl, surwat1, 3, 5));
    addChange(Change.of(SUR, hmcagl, surwat2, 3, 5));

    // change CR2 to WAT at RAY, assume 8tph at gaps of 6 and 9 minutes
    addChange(Change.of(RAY, rayagl, raywat, 1, 9));
    // change WAT to CR2 at RAY, 20tph at gaps of 2 and 4 minutes
    addChange(Change.of(RAY, raywat, rayagl, 1, 4));

    // change at Wimbledon
    addChange(Change.of(WIM, wimagl, wimwat, 4, 6));
    addChange(Change.of(WIM, wimwat, wimagl, 4, 6));

    // change at Balham
    addChange(Change.of(BAL, wimagl, unortherncity, 2, 4));
    addChange(Change.of(BAL, unortherncity, wimagl, 2, 4));

    // change at Clapham Junction
    addChange(Change.of(CLJ, cljwat, wimagl, 4, 6));
    addChange(Change.of(CLJ, cljwat, balvic, 4, 6));
    addChange(Change.of(CLJ, balvic, wimagl, 4, 6));
    addChange(Change.of(CLJ, balvic, cljwat, 4, 6));

    // change at Victoria
    addChange(Change.of(VIC, wimagl, udistrict, 4, 6));
    addChange(Change.of(VIC, wimagl, uvictoria, 4, 6));
    addChange(Change.of(VIC, balvic, udistrict, 4, 6));
    addChange(Change.of(VIC, balvic, uvictoria, 4, 6));

    // change at TCR
    addChange(Change.of(TCR, wimagl, cr1, 2, 6));
    addChange(Change.of(TCR, wimagl, ucentral, 3, 6));

    // change at Euston (pointless, might as well change at Angel)
    // addChange(Change.of(EUS, wimagl, unortherncitysb, 4, 8));

    // change at Angel
    addChange(Change.of(AGL, wimagl, unortherncitysb, 3, 6));

    // change at Kennington
    addChange(Change.of(UKN, unortherncity, unorthernwest, 1, 2));

    // change at Stockwell
    addChange(Change.of(UST, unortherncity, uvictoria, 1, 2));

    // change at Elephant & Castle
    addChange(Change.of(UEC, unortherncity, ubakerloo, 2, 4));

    // change at London Bridge
    addChange(Change.of(LBG, unortherncity, ujubilee, 2, 4));
    addChange(Change.of(LBG, unortherncity, ujubileenb, 2, 4));

    // change at Moorgate
    addChange(Change.of(MOG, unortherncity, cr1, 2, 4));

    // change at Vauxhall
    addChange(Change.of(VXH, cljwat, uvictoria, 3, 6));

    // change at Waterloo
    addChange(Change.of(WAT, cljwat, ujubilee, 3, 6));
    addChange(Change.of(WAT, cljwat, ujubileenb, 3, 6));
    addChange(Change.of(WAT, cljwat, unorthernwest, 3, 6));
    addChange(Change.of(WAT, cljwat, ubakerloo, 3, 6));
    addChange(Change.of(WAT, cljwat, uwandc, 2, 12));  // includes queuing for W&C

    // change at Green Park
    addChange(Change.of(UGP, uvictoria, ujubileenb, 4, 6));

  }

}
