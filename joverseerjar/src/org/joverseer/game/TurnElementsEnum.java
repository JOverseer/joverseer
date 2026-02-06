package org.joverseer.game;

import java.io.Serializable;

/**
 * Enumeration of item types that can be stored within a turn. 
 * 
 * @author Marios Skounakis
 *
 */
public enum TurnElementsEnum implements Serializable {
    Army,
    PopulationCenter,
    Character,
    NationMessage,
    NationEconomy,
    ProductPrice,
    HexInfo,
    Combat,
    NationRelation,
    Artifact,
    Company,
    MapItem,
    Encounter,
    Challenge,
    EconomyCalucatorData,
    PdfText,
    PlayerInfo,
    Notes,
    ArmyEstimate,
    CombatCalcCombats,
    ChallengeFights,
    Diplo,
    ArtifactUser,
    NotepadInfo,
    OrderResults,
}
