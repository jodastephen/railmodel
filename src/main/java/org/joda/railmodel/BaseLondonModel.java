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
 * Common code for current London.
 */
public class BaseLondonModel extends Model {

  static final Route CR2_VIC_AGL = Route.of(
      "CR2",
      "CR2",
      30,
      stations(VIC, TCR, EUS, STP, AGL),
      times(3, 2, 0, 3));
  static final Route CR2_CLJ_AGL = Route.of(
      "CR2",
      "CR2",
      30,
      stations(CLJ, CKR, VIC),
      times(3, 3),
      CR2_VIC_AGL);

  static final Route SWML_CLJ_WAT_18 = Route.of(
      "SWML",
      "CLJ-WAT",
      18,
      stations(CLJ, VXH, WAT),
      times(5, 6));
  static final Route SWML_WIM_WAT_12 = Route.of(
      "SWML",
      "WIM-WAT",
      12,
      stations(WIM, EAD, CLJ),
      times(4, 4),
      SWML_CLJ_WAT_18);
  static final Route SWML_RAY_WAT_8 = Route.of(
      "SWML",
      "RAY-WAT",
      8,
      stations(RAY, WIM),
      times(4),
      SWML_WIM_WAT_12);
  // 4tph from Twickenham/Loop
  static final Route SWML_TWI_WAT_4 = Route.of(
      "SWML",
      "TWI-WAT",
      4,
      stations(TWI, KNG, NEM, RAY),
      times(14, 7, 3),
      SWML_RAY_WAT_8);
  // 4tph from Dorking/Guildford
  static final Route SWML_LHD_WAT_4 = Route.of(
      "SWML",
      "LHD-WAT",
      4,
      stations(LHD, EPS, WCP, RAY),
      times(8, 7, 6),
      SWML_RAY_WAT_8);
  // 6tph faster Surbiton, guess -2mins for not stopping WIM/EAD
  static final Route SWML_SUR_WAT_FAST_6 = Route.of(
      "SWML fast",
      "SUR-WAT (SUR-CLJ-VXH-WAT)",
      6,
      stations(SUR, CLJ),
      times(13),
      SWML_CLJ_WAT_18);
  // 4tph slower Surbiton, guess -2mins for not stopping NEM
  static final Route SWML_SUR_WAT_SLOW_4 = Route.of(
      "SWML",
      "SUR-WAT (SUR-WIM-EAD-CLJ-VXH-WAT)",
      4,
      stations(SUR, WIM),
      times(7),
      SWML_WIM_WAT_12);

  static final Route NORTHERN_CITY_NB = Route.of(
      "Northern (City)",
      "Northern (City)",
      30,
      stations(UMD, USW, UTB, BAL, UCS, UST, UKN, EPH, UBH, LBG, UBK, MOG, UOS, AGL, STP, EUS),
      times(2, 4, 4, 2, 5, 5, 2, 1, 2, 2, 2, 1, 3, 2, 2));
  static final Route NORTHERN_CITY_SB = Route.of(
      "Northern (City)",
      "Northern (City) Southbound",
      30,
      stations(EUS, STP, AGL, UOS, MOG, UBK, LBG, UBH),
      times(2, 2, 3, 1, 2, 2, 2));
  static final Route NORTHERN_WEST_NB = Route.of(
      "Northern (West End)",
      "Northern (West End)",
      30,
      stations(BPW, UKN, WAT, CHX, ULS, TCR, UGS, UWS, EUS),
      times(4, 2, 3, 2, 1, 1, 2, 1));
  static final Route NORTHERN_WEST_SB = Route.of(
      "Northern (West End)",
      "Northern (West End) Southbound",
      30,
      stations(TCR, ULS, CHX),
      times(1, 2));
  static final Route VICTORIA_NB = Route.of(
      "Victoria",
      "Victoria",
      36,
      stations(BRX, UST, VXH, VIC, UGP, UOX, UWS, EUS),
      times(2, 2, 3, 2, 2, 2, 1));
  static final Route BAKERLOO_NB = Route.of(
      "Bakerloo",
      "Bakerloo",
      27,
      stations(EPH, WAT, CHX, UOX, UBS),
      times(4, 2, 4, 4));
  static final Route JUBILEE_EB = Route.of(
      "Jubilee",
      "Jubilee",
      36,
      stations(UBS, BDS, UGP, UWM, WAT, LBG, CWF),
      times(2, 2, 2, 2, 3, 7));
  static final Route JUBILEE_NB = Route.of(
      "Jubilee",
      "Jubilee (Northbound)",
      36,
      stations(LBG, WAT, UWM, UGP, BDS, UBS),
      times(3, 2, 2, 2, 2));
  static final Route WNC_NB = Route.of(
      "W&C",
      "W&C",
      24,
      stations(WAT, UBK),
      times(4));
  // district not included WIM-VIC (29mins), complicates model and causes errors for fairly slow journey
  static final Route DISTRICT_EB = Route.of(
      "District",
      "District",
      27,  // only 9tph from WIM-VIC, but terrible journey time so does not matter
      stations(VIC, UWM, CHX, UTM, UBK),   // fudge Embankment as Charing Cross, Monument as Bank
      times(4, 2, 2, 6));
  static final Route CENTRAL_EB = Route.of(
      "Central",
      "Central",
      36,
      stations(BDS, UOX, TCR, UHL, UCL, USP, UBK, LST),
      times(1, 1, 2, 2, 2, 2, 2));
  static final Route CENTRAL_WB = Route.of(
      "Central",
      "Central (Westbound)",
      36,
      stations(UBK, USP, UCL, UHL),
      times(2, 2, 2));

  static final Route SOUTHERN_BAL_VIC = Route.of(
      "Southern",
      "BAL-VIC",
      12,
      stations(BAL, CLJ, VIC),
      times(6, 10));
  static final Route SOUTHERN_SRH_VIC = Route.of(
      "Southern",
      "SRH-VIC",
      4,
      stations(SRH, BAL),
      times(3),
      SOUTHERN_BAL_VIC);
  static final Route SOUTHERN_STE_LBG = Route.of(
      "Southern",
      "STE-LBG",
      4,  // 2 now, but assume 4 once LBG works complete
      stations(STE, LBG),
      times(23));
  static final Route SOUTHEAST_HNH_VIC = Route.of(
      "SouthEast",
      "HNH-VIC",
      4,
      stations(HNH, BRX, VIC),
      times(2, 10));

  static final Route THAMESLINK_EPH_ZFD = Route.of(
      "Thameslink",
      "EPH-ZFD",
      8,
      stations(EPH, ZFD),
      times(10));
  static final Route THAMESLINK_STE_ZFD = Route.of(
      "Thameslink",
      "STE-ZFD",
      4,
      stations(STE, HNH, EPH),
      times(8, 8),
      THAMESLINK_EPH_ZFD);
  static final Route CR1_EB = Route.of(
      "CR1",
      "CR1",
      30,
      stations(PAD, BDS, TCR, ZFD, MOG, LST, CWF),
      times(3, 2, 3, 2, 0, 7));
  static final Route CR1_WB = Route.of(
      "CR1",
      "CR1 (Westbound)",
      30,
      stations(MOG, ZFD, TCR, BDS, PAD),
      times(2, 3, 2, 3));

  BaseLondonModel() {
  }

}
