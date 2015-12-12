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

/**
 * Stations.
 */
public enum Stations implements Station{

  HMC("Hampton Court"),
  SUR("Surbiton"),
  TWI("Twickenham"),
  SHP("Shepperton"),
  FLW("Fulwell"),
  KNG("Kingston"),
  NEM("New Malden"),
  CSS("Chessington South"),
  LHD("Leatherhead"),
  EPS("Epsom"),
  SNL("Stoneleigh"),
  WCP("Worcester Park"),
  MOT("Motspur Park"),
  RAY("Raynes Park"),
  WIM("Wimbledon"),
  EAD("Earlsfield"),
  BAL("Balham"),
  CLJ("Clapham Junction"),
  VXH("Vauxhall"),
  WAT("Waterloo"),
  HNH("Herne Hill"),
  STE("Streatham"),
  SRH("Streatham Hill"),
  TOO("Tooting (mainline)"),
  CKR("Chelsea Kings Road"),
  BPW("Battersea Power"),
  VIC("Victoria"),
  TCR("Tottenham Court Road"),
  EUS("Euston"),
  STP("St Pancras"),
  AGL("Angel"),
  PAD("Paddington"),
  BDS("Bond Street"),
  ZFD("Farringdon"),
  MOG("Moorgate"),
  LST("Liverpool Street"),
  CWF("Canary Wharf"),
  CHX("Charing Cross"),
  LBG("London Bridge"),
  BRX("Brixton"),
  UMD("Morden"),
  USW("South Wimbledon"),
  UTB("Tooting Broadway"),
  UCS("Clapham South"),
  UST("Stockwell"),
  UKN("Kennington"),
  EPH("Elephant & Castle"),
  UBH("Borough"),
  UBK("Bank/Monument"),
  UOS("Old Street"),
  ULS("Leicester Square"),
  UGS("Goodge Street"),
  UWS("Warren Street"),
  UGP("Green Park"),
  UOX("Oxford Circus"),
  UBS("Baker Street"),
  UHL("Holborn"),
  UCL("Chancery Lane"),
  USP("St Pauls"),
  UWM("Westminster"),
  UTM("Temple");

  private final String description;

  private Stations(String description) {
    this.description = description;
  }

  @Override
  public String description() {
    return description;
  }

}
