package org.joverseer.metadata;

import java.io.IOException;
import java.util.ArrayList;

import org.joverseer.metadata.domain.Nation;
import org.joverseer.metadata.domain.NationAllegianceEnum;

/**
 * Reads nation information for the game metadata
 * 
 * TODO: Move data from the in-code arrays that it currently resides in to
 * files.
 * 
 * @author Marios Skounakis
 * 
 */
public class NationReader implements MetadataReader {
	Object[][] nations_2950 = new Object[][] { { "Unknown", "Un", new Object[] {} }, { "Woodmen", "Woo", new Object[] { SNAEnum.StealthBonus, SNAEnum.ChallengeBonus, SNAEnum.ScoutReconAtDouble, SNAEnum.BetterMoraleAtNoFood, SNAEnum.NoMoraleLossOnFM } }, { "Northmen", "Nor", new Object[] { SNAEnum.ShipsWith750Timber, SNAEnum.BuySellBonus, SNAEnum.TroopsAt20Training, SNAEnum.EmmisariesAt40, SNAEnum.WarshipsAtStr5 } }, { "Riders of Rohan", "RoR", new Object[] { SNAEnum.TroopsAt20Training, SNAEnum.CommandersAt40, SNAEnum.NoMoraleLossOnFM, SNAEnum.AccessToCjrMts } }, { "Dúnadan Rangers", "DRa", new Object[] { SNAEnum.TroopsAt20Training, SNAEnum.BetterMoraleAtNoFood, SNAEnum.FortificationsWithHalfTimber, SNAEnum.MagesAt40, SNAEnum.WarshipsAtStr4 } }, { "Silvan Elves", "Sil", new Object[] { SNAEnum.ShipsWith500Timber, SNAEnum.TroopsAt25Training, SNAEnum.StealthBonus, SNAEnum.BetterMoraleAtNoFood, SNAEnum.WarshipsAtStr5 } },
			{ "Northern Gondor", "NGo", new Object[] { SNAEnum.FortificationsWithHalfTimber, SNAEnum.CommandersAt40, SNAEnum.BetterMoraleAtNoFood, SNAEnum.TroopsAt20Training, SNAEnum.WarshipsAtStr4 } }, { "Southern Gondor", "SGo", new Object[] { SNAEnum.FortificationsWithHalfTimber, SNAEnum.MagesAt40, SNAEnum.TroopsAt20Training, SNAEnum.BetterMoraleAtNoFood, SNAEnum.WarshipsAtStr5 } }, { "Dwarves", "Dwa", new Object[] { SNAEnum.HIAt30Training, SNAEnum.NoMoraleLossOnFM, SNAEnum.FortificationsWithHalfTimber, SNAEnum.ScoutReconAt50, SNAEnum.WarshipsAtStr2 } }, { "Sinda Elves", "Sin", new Object[] { SNAEnum.ShipsWith500Timber, SNAEnum.TroopsAt25Training, SNAEnum.StealthBonus, SNAEnum.BetterMoraleAtNoFood, SNAEnum.WarshipsAtStr5 } }, { "Noldo Elves", "Nol", new Object[] { SNAEnum.TroopsAt25Training, SNAEnum.NoMoraleLossOnFM, SNAEnum.UncoverSecretsAt40, SNAEnum.StealthBonus, SNAEnum.WarshipsAtStr5 } },
			{ "Witch-king", "WiK", new Object[] { SNAEnum.CommandersAt40, SNAEnum.BetterMoraleAtNoFood, SNAEnum.AccessToFrflHrts, SNAEnum.AccessToCjrHds } }, { "Dragon Lord", "DrL", new Object[] { SNAEnum.StealthBonus, SNAEnum.AccessToTeleport, SNAEnum.ScoutReconAtDouble, SNAEnum.BetterMoraleAtNoFood, SNAEnum.WarshipsAtStr2 } }, { "Dog Lord", "DoL", new Object[] { SNAEnum.TroopsAt20Training, SNAEnum.BetterMoraleAtNoFood, SNAEnum.AccessToCjrMts, SNAEnum.StealthBonus } }, { "Cloud Lord", "ClL", new Object[] { SNAEnum.KindapAssassAtPlus20, SNAEnum.StealthBonus, SNAEnum.UncoverSecretsAt40, SNAEnum.AgentsAt40 } }, { "Blind Sorcerer", "BlS", new Object[] { SNAEnum.OpenSeasMvmnt, SNAEnum.AccessToSmnStrms, SNAEnum.AccessToCjrHds, SNAEnum.MagesAt40, SNAEnum.WarshipsAtStr4 } }, { "Ice King", "IcK", new Object[] { SNAEnum.StealthBonus, SNAEnum.AgentsAt40, SNAEnum.AccessToSmnStrms, SNAEnum.BetterMoraleAtNoFood } },
			{ "Quiet Avenger", "QAv", new Object[] { SNAEnum.ScoutReconAtDouble, SNAEnum.UncoverSecretsAt40, SNAEnum.EmmisariesAt40, SNAEnum.CommandersAt40, SNAEnum.WarshipsAtStr4 } }, { "Fire King", "FiK", new Object[] { SNAEnum.FreeHire, SNAEnum.ArmiesAt40Morale, SNAEnum.BetterMoraleAtNoFood, SNAEnum.AccessToFntcsm, SNAEnum.AccessToCjrHds } }, { "Long Rider", "LoR", new Object[] { SNAEnum.CommandersAt40, SNAEnum.AccessToCjrMts, SNAEnum.TroopsAt20Training, SNAEnum.NoMoraleLossOnFM, SNAEnum.WarshipsAtStr4 } }, { "Dark Lieutenants", "DkL", new Object[] { SNAEnum.CommandersAt40, SNAEnum.AccessToFrflHrts, SNAEnum.AccessToCjrHds, SNAEnum.BetterMoraleAtNoFood } }, { "Corsairs", "Cor", new Object[] { SNAEnum.ShipsWith750Timber, SNAEnum.OpenSeasMvmnt, SNAEnum.ChallengeBonus, SNAEnum.WarshipsAtStr5 } }, { "Rhûn Easterlings", "Eas", new Object[] { SNAEnum.CommandersAt40, SNAEnum.ChallengeBonus, SNAEnum.BetterMoraleAtNoFood } },
			{ "Dunlendings", "Dun", new Object[] { SNAEnum.ChallengeBonus, SNAEnum.ScoutReconAtDouble, SNAEnum.AgentsAt40 } }, { "White Wizard", "WWi", new Object[] { SNAEnum.FreeHire, SNAEnum.TroopsAt25Training, SNAEnum.CommandersAt40, SNAEnum.ChallengeBonus } }, { "Khand Easterlings", "KhE", new Object[] { SNAEnum.CommandersAt40, SNAEnum.ChallengeBonus, SNAEnum.BetterMoraleAtNoFood } }, };

	Object[][] nations_1650 = new Object[][] { { "Unknown", "Un", new Object[] {} }, { "Woodmen", "Woo", new Object[] { SNAEnum.StealthBonus, SNAEnum.ChallengeBonus, SNAEnum.ScoutReconAtDouble, SNAEnum.BetterMoraleAtNoFood, SNAEnum.NoMoraleLossOnFM } }, { "Northmen", "Nor", new Object[] { SNAEnum.ShipsWith750Timber, SNAEnum.EmmisariesAt40, SNAEnum.TroopsAt20Training, SNAEnum.ShipsWith750Timber, SNAEnum.WarshipsAtStr5 } }, { "Éothraim", "Eot", new Object[] { SNAEnum.TroopsAt20Training, SNAEnum.CommandersAt40, SNAEnum.NoMoraleLossOnFM, SNAEnum.AccessToCjrMts } }, { "Arthedain", "Art", new Object[] { SNAEnum.TroopsAt20Training, SNAEnum.BetterMoraleAtNoFood, SNAEnum.FortificationsWithHalfTimber, SNAEnum.MagesAt40, SNAEnum.WarshipsAtStr4 } }, { "Cardolan", "Car", new Object[] { SNAEnum.FreeHire, SNAEnum.ArmiesAt40Morale, SNAEnum.CommandersAt40, SNAEnum.MAAt25Training, SNAEnum.ScoutReconPlus20, SNAEnum.WarshipsAtStr4 } },
			{ "Northern Gondor", "NGo", new Object[] { SNAEnum.FortificationsWithHalfTimber, SNAEnum.CommandersAt40, SNAEnum.TroopsAt20Training, SNAEnum.BetterMoraleAtNoFood, SNAEnum.WarshipsAtStr4 } }, { "Southern Gondor", "SGo", new Object[] { SNAEnum.FortificationsWithHalfTimber, SNAEnum.TroopsAt20Training, SNAEnum.BetterMoraleAtNoFood, SNAEnum.MagesAt40, SNAEnum.WarshipsAtStr5 } }, { "Dwarves", "Dwa", new Object[] { SNAEnum.HIAt30Training, SNAEnum.NoMoraleLossOnFM, SNAEnum.FortificationsWithHalfTimber, SNAEnum.ScoutReconAt50, SNAEnum.WarshipsAtStr2 } }, { "Sinda Elves", "Sin", new Object[] { SNAEnum.ShipsWith500Timber, SNAEnum.TroopsAt25Training, SNAEnum.StealthBonus, SNAEnum.BetterMoraleAtNoFood, SNAEnum.WarshipsAtStr5 } }, { "Noldo Elves", "Nol", new Object[] { SNAEnum.TroopsAt25Training, SNAEnum.StealthBonus, SNAEnum.UncoverSecretsAt40, SNAEnum.NoMoraleLossOnFM, SNAEnum.WarshipsAtStr5 } },
			{ "Witch-king", "WiK", new Object[] { SNAEnum.CommandersAt40, SNAEnum.BetterMoraleAtNoFood, SNAEnum.NoMoraleLossOnFM, SNAEnum.AccessToFrflHrts, SNAEnum.AccessToCjrHds } }, { "Dragon Lord", "DrL", new Object[] { SNAEnum.StealthBonus, SNAEnum.AccessToTeleport, SNAEnum.ScoutReconAtDouble, SNAEnum.BetterMoraleAtNoFood, SNAEnum.NoMoraleLossOnFM, SNAEnum.WarshipsAtStr2 } }, { "Dog Lord", "DoL", new Object[] { SNAEnum.BetterMoraleAtNoFood, SNAEnum.NoMoraleLossOnFM, SNAEnum.TroopsAt20Training, SNAEnum.AccessToCjrMts, SNAEnum.StealthBonus } }, { "Cloud Lord", "ClL", new Object[] { SNAEnum.KindapAssassAtPlus20, SNAEnum.AgentsAt40, SNAEnum.StealthBonus, SNAEnum.UncoverSecretsAt40 } }, { "Blind Sorcerer", "BlS", new Object[] { SNAEnum.OpenSeasMvmnt, SNAEnum.AccessToSmnStrms, SNAEnum.AccessToCjrHds, SNAEnum.MagesAt40, SNAEnum.WarshipsAtStr4 } },
			{ "Ice King", "IcK", new Object[] { SNAEnum.StealthBonus, SNAEnum.AgentsAt40, SNAEnum.AccessToSmnStrms, SNAEnum.BetterMoraleAtNoFood, SNAEnum.NoMoraleLossOnFM } }, { "Quiet Avenger", "QAv", new Object[] { SNAEnum.ScoutReconAtDouble, SNAEnum.UncoverSecretsAt40, SNAEnum.CommandersAt40, SNAEnum.EmmisariesAt40, SNAEnum.WarshipsAtStr4 } }, { "Fire King", "FiK", new Object[] { SNAEnum.FreeHire, SNAEnum.ArmiesAt40Morale, SNAEnum.BetterMoraleAtNoFood, SNAEnum.NoMoraleLossOnFM, SNAEnum.AccessToFntcsm, SNAEnum.AccessToCjrHds } }, { "Long Rider", "LoR", new Object[] { SNAEnum.CommandersAt40, SNAEnum.AccessToCjrMts, SNAEnum.AccessToCjrMts, SNAEnum.NoMoraleLossOnFM, SNAEnum.WarshipsAtStr4 } }, { "Dark Lieutenants", "DkL", new Object[] { SNAEnum.BetterMoraleAtNoFood, SNAEnum.CommandersAt40, SNAEnum.AccessToFrflHrts, SNAEnum.AccessToCjrHds } }, { "Corsairs", "Cor", new Object[] { SNAEnum.ShipsWith750Timber, SNAEnum.OpenSeasMvmnt, SNAEnum.ChallengeBonus, SNAEnum.WarshipsAtStr5 } },
			{ "Haradwaith", "Har", new Object[] { SNAEnum.FreeHire, SNAEnum.MAAt25Training, SNAEnum.UncoverSecretsAt40, SNAEnum.ChallengeBonus, SNAEnum.WarshipsAtStr5 } }, { "Dunlendings", "Dun", new Object[] { SNAEnum.ChallengeBonus, SNAEnum.ScoutReconAtDouble, SNAEnum.AgentsAt40 } }, { "Rhudaur", "Rhu", new Object[] { SNAEnum.FreeHire, SNAEnum.MAAt25Training, SNAEnum.CommandersAt40, SNAEnum.ChallengeBonus } }, { "Easterlings", "Eas", new Object[] { SNAEnum.CommandersAt40, SNAEnum.ChallengeBonus, SNAEnum.BetterMoraleAtNoFood, SNAEnum.NoMoraleLossOnFM } }, };

	Object[][] nations_BOFA = new Object[][] {
			{ "Unknown", "Un", new Object[] {} },
			{ "Goblins", "Gob", new Object[] { SNAEnum.FreeHire, SNAEnum.TroopsAt20Training, SNAEnum.NoMoraleLossOnFM, SNAEnum.ArmiesAt40Morale } },
			{ "Warg Riders", "WaR", new Object[] { SNAEnum.FreeHire, SNAEnum.NoMoraleLossOnFM, SNAEnum.BetterMoraleAtNoFood } },
			{ "Elves", "Elv", new Object[] { SNAEnum.FreeHire, SNAEnum.NoMoraleLossOnFM, SNAEnum.TroopsAt25Training } },
			{ "Dwarves", "Dwa", new Object[] { SNAEnum.FreeHire, SNAEnum.ScoutReconAt50, SNAEnum.HIAt30Training, SNAEnum.NoMoraleLossOnFM } },
			{ "Northmen", "Nor", new Object[] { SNAEnum.FreeHire, SNAEnum.ScoutReconPlus20, SNAEnum.NoMoraleLossOnFM, SNAEnum.BuySellBonus, SNAEnum.FortificationsWithHalfTimber } },
			{ "Unplayed VI", "UN6", new Object[] {} },
			{ "Unplayed VII", "UN7", new Object[] {} },
			{ "Unplayed VIII", "UN8", new Object[] {} },
			{ "Unplayed X", "UN9", new Object[] {} },
			{ "Unplayed IX", "UN10", new Object[] {} },
			{ "Unplayed XI", "UN11", new Object[] {} },
			{ "Unplayed XII", "UN12", new Object[] {} },
			{ "Unplayed XIII", "UN13", new Object[] {} },
			{ "Unplayed XIV", "UN14", new Object[] {} },
			{ "Unplayed XV", "UN15", new Object[] {} },
			{ "Unplayed XVI", "UN16", new Object[] {} },
			{ "Unplayed XVII", "UN17", new Object[] {} },
			{ "Unplayed XVIII", "UN18", new Object[] {} },
			{ "Unplayed XIX", "UN19", new Object[] {} },
			{ "Unplayed XX", "UN20", new Object[] {} },
			{ "Unplayed XXI", "UN21", new Object[] {} },
			{ "Unplayed XXII", "UN22", new Object[] {} },
			{ "Unplayed XXIII", "UN23", new Object[] {} },
			{ "Unplayed XXIV", "UN24", new Object[] {} },
			{ "Unplayed XXV", "UN25", new Object[] {} } };

	Object[][] nations_CME = new Object[][] {
			{ "Unknown", "Un", new Object[] {} },
			{ "Unnamed I", "UN1", new Object[] {  } },
			{ "Unnamed II", "UN2", new Object[] {  } },
			{ "Unplayed III", "UN3", new Object[] {  } },
			{ "Unplayed IV", "UN4", new Object[] {  } },
			{ "Unplayed V", "UN5", new Object[] {} },
			{ "Unplayed VI", "UN6", new Object[] {} },
			{ "Unplayed VII", "UN7", new Object[] {} },
			{ "Unplayed VIII", "UN8", new Object[] {} },
			{ "Unplayed X", "UN9", new Object[] {} },
			{ "Unplayed IX", "UN10", new Object[] {} },
			{ "Unplayed XI", "UN11", new Object[] {} },
			{ "Unplayed XII", "UN12", new Object[] {} },
			{ "Unplayed XIII", "UN13", new Object[] {} },
			{ "Unplayed XIV", "UN14", new Object[] {} },
			{ "Unplayed XV", "UN15", new Object[] {} },
			{ "Unplayed XVI", "UN16", new Object[] {} },
			{ "Unplayed XVII", "UN17", new Object[] {} },
			{ "Unplayed XVIII", "UN18", new Object[] {} },
			{ "Unplayed XIX", "UN19", new Object[] {} },
			{ "Unplayed XX", "UN20", new Object[] {} },
			{ "Unplayed XXI", "UN21", new Object[] {} },
			{ "Unplayed XXII", "UN22", new Object[] {} },
			{ "Unplayed XXIII", "UN23", new Object[] {} },
			{ "Unplayed XXIV", "UN24", new Object[] {} },
			{ "Unplayed XXV", "UN25", new Object[] {} } };

// old BOFA:
//	Object[][] nations_BOFA = new Object[][] { { "Unknown", "Un", new Object[] {} }, { "North Kingdom", "NKi", new Object[] {} }, { "South Kingdom", "SKi", new Object[] {} }, { "Unplayed Nat III", "UN3", new Object[] {} }, { "Unplayed Nat IV", "UN4", new Object[] {} }, { "Unplayed V", "UN5", new Object[] {} }, { "Unplayed VI", "UN6", new Object[] {} }, { "Unplayed VII", "UN7", new Object[] {} }, { "Unplayed VIII", "UN8", new Object[] {} }, { "Unplayed IX", "UN8", new Object[] {} }, { "Goblins", "Gob", new Object[] { SNAEnum.FreeHire, SNAEnum.TroopsAt20Training, SNAEnum.NoMoraleLossOnFM, SNAEnum.ArmiesAt40Morale } }, { "Warg Riders", "WaR", new Object[] { SNAEnum.FreeHire, SNAEnum.NoMoraleLossOnFM, SNAEnum.BetterMoraleAtNoFood } }, { "Elves", "Elv", new Object[] { SNAEnum.FreeHire, SNAEnum.NoMoraleLossOnFM, SNAEnum.TroopsAt25Training } }, { "Dwarves", "Dwa", new Object[] { SNAEnum.FreeHire, SNAEnum.ScoutReconAt50, SNAEnum.HIAt30Training, SNAEnum.NoMoraleLossOnFM } },
//			{ "Northmen", "Nor", new Object[] { SNAEnum.FreeHire, SNAEnum.ScoutReconPlus20, SNAEnum.NoMoraleLossOnFM, SNAEnum.BuySellBonus, SNAEnum.FortificationsWithHalfTimber } }, { "Unplayed XV", "UN15", new Object[] {} }, { "Unplayed XVI", "UN16", new Object[] {} }, { "Unplayed XVII", "UN17", new Object[] {} }, { "Unplayed XVIII", "UN18", new Object[] {} }, { "Unplayed XIX", "UN19", new Object[] {} }, { "Unplayed XX", "UN20", new Object[] {} }, { "Unplayed XXI", "UN21", new Object[] {} }, { "Unplayed XXII", "UN22", new Object[] {} }, { "Unplayed XXIII", "UN23", new Object[] {} }, { "Unplayed XXIV", "UN24", new Object[] {} }, { "Unplayed XXV", "UN25", new Object[] {} } };

	Object[][] nations_1000 = new Object[][] { { "Unknown", "Un", new Object[] {} }, { "North Kingdom", "NKi", new Object[] {} }, { "South Kingdom", "SKi", new Object[] {} },   { "Nation III", "N03", new Object[] {} }, { "Nation IV", "N04", new Object[] {} }, { "Nation V", "N05", new Object[] {} }, { "Nation VI", "N06", new Object[] {} }, { "Nation VII", "N07", new Object[] {} }, { "Nation VIII", "N08", new Object[] {} }, { "Nation IX", "N09", new Object[] {} }, { "Nation X", "N10", new Object[] {} }, { "Nation XI", "N11", new Object[] {} }, { "Nation XII", "N12", new Object[] {} }, { "Nation XIII", "N13", new Object[] {} }, { "Nation XIV", "N14", new Object[] {} }, { "Nation XV", "N15", new Object[] {} }, { "Nation XVI", "N16", new Object[] {} }, { "Nation XVII", "N17", new Object[] {} }, { "Nation XVIII", "N18", new Object[] {} }, { "Nation XIX", "N19", new Object[] {} },
			{ "Nation XX", "N20", new Object[] {} }, { "Nation XXI", "N21", new Object[] {} }, { "Nation XXII", "N22", new Object[] {} }, { "Nation XXIII", "N23", new Object[] {} }, { "Nation XXIV", "N24", new Object[] {} }, { "Nation XXV", "N25", new Object[] {} }, };

	Object[][] nations_UW = new Object[][] { { "Unknown", "Un", new Object[] {} }, { "Woodmen", "Woo", new Object[] { SNAEnum.StealthBonus, SNAEnum.ChallengeBonus, SNAEnum.ScoutReconAtDouble, SNAEnum.BetterMoraleAtNoFood, SNAEnum.NoMoraleLossOnFM } }, { "Northmen", "Nor", new Object[] { SNAEnum.ShipsWith750Timber, SNAEnum.BuySellBonus, SNAEnum.TroopsAt20Training, SNAEnum.EmmisariesAt40, SNAEnum.WarshipsAtStr5 } }, { "Riders of Rohan", "RoR", new Object[] { SNAEnum.TroopsAt20Training, SNAEnum.CommandersAt40, SNAEnum.NoMoraleLossOnFM, SNAEnum.AccessToCjrMts } }, { "Dúnadan Rangers", "DRa", new Object[] { SNAEnum.TroopsAt20Training, SNAEnum.BetterMoraleAtNoFood, SNAEnum.FortificationsWithHalfTimber, SNAEnum.MagesAt40, SNAEnum.WarshipsAtStr4 } }, { "Silvan Elves", "Sil", new Object[] { SNAEnum.ShipsWith500Timber, SNAEnum.TroopsAt25Training, SNAEnum.StealthBonus, SNAEnum.BetterMoraleAtNoFood, SNAEnum.WarshipsAtStr5 } },
			{ "Northern Gondor", "NGo", new Object[] { SNAEnum.FortificationsWithHalfTimber, SNAEnum.CommandersAt40, SNAEnum.BetterMoraleAtNoFood, SNAEnum.TroopsAt20Training, SNAEnum.WarshipsAtStr4 } }, { "Southern Gondor", "SGo", new Object[] { SNAEnum.FortificationsWithHalfTimber, SNAEnum.MagesAt40, SNAEnum.TroopsAt20Training, SNAEnum.BetterMoraleAtNoFood, SNAEnum.WarshipsAtStr5 } }, { "Dwarves", "Dwa", new Object[] { SNAEnum.HIAt30Training, SNAEnum.NoMoraleLossOnFM, SNAEnum.FortificationsWithHalfTimber, SNAEnum.ScoutReconAt50, SNAEnum.WarshipsAtStr2 } }, { "Sinda Elves", "Sin", new Object[] { SNAEnum.ShipsWith500Timber, SNAEnum.TroopsAt25Training, SNAEnum.StealthBonus, SNAEnum.BetterMoraleAtNoFood, SNAEnum.WarshipsAtStr5 } }, { "Noldo Elves", "Nol", new Object[] { SNAEnum.TroopsAt25Training, SNAEnum.NoMoraleLossOnFM, SNAEnum.UncoverSecretsAt40, SNAEnum.StealthBonus, SNAEnum.WarshipsAtStr5 } },
			{ "Witch-king", "WiK", new Object[] { SNAEnum.CommandersAt40, SNAEnum.BetterMoraleAtNoFood, SNAEnum.AccessToFrflHrts, SNAEnum.AccessToCjrHds } }, { "Dragon Lord", "DrL", new Object[] { SNAEnum.StealthBonus, SNAEnum.AccessToTeleport, SNAEnum.ScoutReconAtDouble, SNAEnum.BetterMoraleAtNoFood, SNAEnum.WarshipsAtStr2 } }, { "Dog Lord", "DoL", new Object[] { SNAEnum.TroopsAt20Training, SNAEnum.BetterMoraleAtNoFood, SNAEnum.AccessToCjrMts, SNAEnum.StealthBonus } }, { "Cloud Lord", "ClL", new Object[] { SNAEnum.KindapAssassAtPlus20, SNAEnum.StealthBonus, SNAEnum.UncoverSecretsAt40, SNAEnum.AgentsAt40 } }, { "Blind Sorcerer", "BlS", new Object[] { SNAEnum.OpenSeasMvmnt, SNAEnum.AccessToSmnStrms, SNAEnum.AccessToCjrHds, SNAEnum.MagesAt40, SNAEnum.WarshipsAtStr4 } }, { "Ice King", "IcK", new Object[] { SNAEnum.StealthBonus, SNAEnum.AgentsAt40, SNAEnum.AccessToSmnStrms, SNAEnum.BetterMoraleAtNoFood } },
			{ "Quiet Avenger", "QAv", new Object[] { SNAEnum.ScoutReconAtDouble, SNAEnum.UncoverSecretsAt40, SNAEnum.EmmisariesAt40, SNAEnum.CommandersAt40, SNAEnum.WarshipsAtStr4 } }, { "Fire King", "FiK", new Object[] { SNAEnum.FreeHire, SNAEnum.ArmiesAt40Morale, SNAEnum.BetterMoraleAtNoFood, SNAEnum.AccessToFntcsm, SNAEnum.AccessToCjrHds } }, { "Long Rider", "LoR", new Object[] { SNAEnum.CommandersAt40, SNAEnum.AccessToCjrMts, SNAEnum.TroopsAt20Training, SNAEnum.NoMoraleLossOnFM, SNAEnum.WarshipsAtStr4 } }, { "Dark Lieutenants", "DkL", new Object[] { SNAEnum.CommandersAt40, SNAEnum.AccessToFrflHrts, SNAEnum.AccessToCjrHds, SNAEnum.BetterMoraleAtNoFood } }, { "Corsairs", "Cor", new Object[] { SNAEnum.ShipsWith750Timber, SNAEnum.OpenSeasMvmnt, SNAEnum.ChallengeBonus, SNAEnum.WarshipsAtStr5 } }, { "Rhûn Easterlings", "RhE", new Object[] { SNAEnum.CommandersAt40, SNAEnum.ChallengeBonus, SNAEnum.BetterMoraleAtNoFood } },
			{ "Dunlendings", "Dun", new Object[] { SNAEnum.ChallengeBonus, SNAEnum.ScoutReconAtDouble, SNAEnum.AgentsAt40 } }, { "White Wizard", "WWi", new Object[] { SNAEnum.FreeHire, SNAEnum.TroopsAt25Training, SNAEnum.CommandersAt40, SNAEnum.ChallengeBonus } }, { "Khand Easterlings", "KhE", new Object[] { SNAEnum.CommandersAt40, SNAEnum.ChallengeBonus, SNAEnum.BetterMoraleAtNoFood } }, };

	Object[][] nations_KS = new Object[][] { { "Unknown", "Un", new Object[] {} }, { "Line of Eldacar", "Eld", new Object[] {} }, { "Rebels of Ered Lithui", "Reb", new Object[] {} }, { "Rhovanion", "Rho", new Object[] {} }, { "Horselords of Rhovanion", "HoR", new Object[] {} }, { "Kingdom of Arnor", "KoA", new Object[] {} }, { "Quendi", "Que", new Object[] {} }, { "Nation VII", "N07", new Object[] {} }, { "Nation VIII", "N08", new Object[] {} }, { "Nation IX", "N09", new Object[] {} }, { "Nation X", "N10", new Object[] {} }, { "Line of Castamir", "Cas", new Object[] {} }, { "Line of Morlaen", "Mor", new Object[] {} }, { "Line of Elendin", "Ele", new Object[] {} }, { "Southron Kingdoms", "SKi", new Object[] {} }, { "Hithlum", "Hit", new Object[] {} }, { "Witch-realm of Angmar", "WiA", new Object[] {} }, { "NationXVII", "N17", new Object[] {} }, { "NationXVIII", "N18", new Object[] {} }, { "NationXIX", "N19", new Object[] {} }, { "NationXX", "N20", new Object[] {} },
			{ "Line of Tirkhor", "LoT", new Object[] {} }, { "Khazad", "Kha", new Object[] {} }, { "NationXXIII", "N23", new Object[] {} }, { "NationXXVI", "N24", new Object[] {} }, { "NationXXV", "N25", new Object[] {} }, };

	private void addSNAs(Nation n, Object[] snas) {
		for (Object sna : snas) {
			n.getSnas().add((SNAEnum) sna);
		}
	}

	@Override
	public void load(GameMetadata gm) throws IOException, MetadataReaderException {
		ArrayList<Nation> nations = new ArrayList<Nation>();
		int number;
		if (gm.getGameType() == GameTypeEnum.game2950) {
			for (int i = 0; i < 26; i++) {
				String shortName = (String) this.nations_2950[i][1];
				String name = (String) this.nations_2950[i][0];
				Nation n = new Nation(i, name, shortName);
				number = n.getNumber().intValue();
				if (number <= 10) {
					n.setAllegiance(NationAllegianceEnum.FreePeople);
				} else if (number <= 20) {
					n.setAllegiance(NationAllegianceEnum.DarkServants);
				} else {
					n.setAllegiance(NationAllegianceEnum.Neutral);
				}
				addSNAs(n, (Object[]) this.nations_2950[i][2]);
				nations.add(n);
			}
		} else if (gm.getGameType() == GameTypeEnum.game1650) {
			for (int i = 0; i < 26; i++) {
				String shortName = (String) this.nations_1650[i][1];
				String name = (String) this.nations_1650[i][0];
				Nation n = new Nation(i, name, shortName);
				number = n.getNumber().intValue();
				if (number <= 10) {
					n.setAllegiance(NationAllegianceEnum.FreePeople);
				} else if (number <= 20) {
					n.setAllegiance(NationAllegianceEnum.DarkServants);
				} else {
					n.setAllegiance(NationAllegianceEnum.Neutral);
				}
				addSNAs(n, (Object[]) this.nations_1650[i][2]);
				nations.add(n);
			}
		} else if (gm.getGameType() == GameTypeEnum.gameFA) {
			// TODO fix
			for (int i = 0; i < 26; i++) {
				String shortName = (String) this.nations_1000[i][1];
				String name = (String) this.nations_1000[i][0];
				Nation n = new Nation(i, name, shortName);
				number = n.getNumber().intValue();
				if (number <= 10) {
					n.setAllegiance(NationAllegianceEnum.FreePeople);
				} else if (number <= 20) {
					n.setAllegiance(NationAllegianceEnum.DarkServants);
				} else {
					n.setAllegiance(NationAllegianceEnum.Neutral);
				}
				addSNAs(n, (Object[]) this.nations_1000[i][2]);
				nations.add(n);
			}
		} else if (gm.getGameType() == GameTypeEnum.gameBOFA) {
			for (int i = 0; i < 26; i++) {
				String shortName = (String) this.nations_BOFA[i][1];
				String name = (String) this.nations_BOFA[i][0];
				Nation n = new Nation(i, name, shortName);
				number = n.getNumber().intValue();
/* old BOFA
 				if (number >= 10 && number <= 11) {
 					n.setAllegiance(NationAllegianceEnum.DarkServants);
				} else if (number >= 12 && number <= 14) {
					n.setAllegiance(NationAllegianceEnum.FreePeople);
				} else {
					n.setRemoved(true);
					n.setAllegiance(NationAllegianceEnum.Neutral);
				}
*/
 				if ((number == 1) || (number == 2)) {
 					n.setAllegiance(NationAllegianceEnum.DarkServants);
				} else if (number >= 3 && number <= 5) {
					n.setAllegiance(NationAllegianceEnum.FreePeople);
				} else {
					n.setRemoved(true);
					n.setAllegiance(NationAllegianceEnum.Neutral);
				}

 				addSNAs(n, (Object[]) this.nations_BOFA[i][2]);
				nations.add(n);
/* old BOFA
				if (number < 10 || number > 14) {
					n.setRemoved(true);
				}
*/				
			}
		} else if (gm.getGameType() == GameTypeEnum.gameUW) {
			for (int i = 0; i < 26; i++) {
				String shortName = (String) this.nations_UW[i][1];
				String name = (String) this.nations_UW[i][0];
				Nation n = new Nation(i, name, shortName);
				number = n.getNumber().intValue();
				if (number <= 10) {
					n.setAllegiance(NationAllegianceEnum.FreePeople);
				} else if (number <= 20 || number == 24) {
					n.setAllegiance(NationAllegianceEnum.DarkServants);
				} else {
					n.setAllegiance(NationAllegianceEnum.Neutral);
				}
				addSNAs(n, (Object[]) this.nations_UW[i][2]);
				nations.add(n);
				switch (number) {
				case  1: case 4: case 6: case 7: case 9:
				case 14: case 15: case 16: case 17: case 19:
				case 20: case 21: case 22: case 23: case 25:
					n.setRemoved(true);
				}
			}
		} else if (gm.getGameType() == GameTypeEnum.gameKS) {
			for (int i = 0; i < 26; i++) {
				String shortName = (String) this.nations_KS[i][1];
				String name = (String) this.nations_KS[i][0];
				Nation n = new Nation(i, name, shortName);
				number = n.getNumber().intValue();
				if (number <= 10) {
					n.setAllegiance(NationAllegianceEnum.FreePeople);
				} else if (number <= 20) {
					n.setAllegiance(NationAllegianceEnum.DarkServants);
				} else {
					n.setAllegiance(NationAllegianceEnum.Neutral);
				}
				addSNAs(n, (Object[]) this.nations_KS[i][2]);
				nations.add(n);
				boolean removed = false;
				if (number > 6 && number < 11)
					removed = true;
				if (number > 16 && number < 21)
					removed = true;
				if (number > 22)
					removed = true;
				n.setRemoved(removed);
			}
		} else if (gm.getGameType() == GameTypeEnum.gameCME) {
			for (int i = 0; i < 26; i++) {
				String shortName = (String) this.nations_CME[i][1];
				String name = (String) this.nations_CME[i][0];
				Nation n = new Nation(i, name, shortName);
				number = n.getNumber().intValue();
				n.setAllegiance(NationAllegianceEnum.Neutral);
				addSNAs(n, (Object[]) this.nations_CME[i][2]);
				nations.add(n);
				boolean removed = false;
				if (number > 2)
					removed = true;
				n.setRemoved(removed);
			}
		}
		gm.setNations(nations);

	}
}
