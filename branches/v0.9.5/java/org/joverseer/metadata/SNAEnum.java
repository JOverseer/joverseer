package org.joverseer.metadata;

import java.util.HashMap;


public enum SNAEnum {
    ScoutReconAtDouble(1, "Scout/Recon at Double"),
    ScoutReconPlus20(2, "Scout/Recon at +20"),
    ScoutReconAt50(3, "Scout/Recon at 50"),
    ChallengeBonus(4, "Challenge Bonus"),
    StealthBonus(5, "Stealth Bonus"),
    NoMoraleLossOnFM(6, "No Morale Loss on Force March"),
    BetterMoraleAtNoFood(7, "Better Morale With No Food"),
    BuySellBonus(8, "Market Bonus"),
    EmmisariesAt40(9, "New Emmisaries up to 40"),
    MagesAt40(10, "New Mages up to 40"),
    AgentsAt40(11, "New Agents up to 40"),
    CommandersAt40(12, "New Commanders up to 40"),
    TroopsAt20Training(13, "New Troops at 20 Training"),
    TroopsAt25Training(14, "New Troops at 25 Training"),
    HIAt30Training(15, "New HI at 30 Training"),
    MAAt25Training(16, "New MA at 25 Training"),
    ShipsWith750Timber(17, "Ships at 1/2 Timber"),
    ShipsWith500Timber(18, "Ships at 1/3 Timber"),
    FortificationsWithHalfTimber(19, "Forts at Half Timber"),
    ArmiesAt40Morale(20, "New Armies at 40 Morale"),
    FreeHire(21, "Free Hire"),
    UncoverSecretsAt40(22, "Uncover Secrets at 40"),
    AccessToWeakness(23, "Access to Weakness"),
    AccessToCjrMts(24, "Access to Conjure Mounts"),
    AccessToCjrFood(25, "Access to Conjure Food"),
    AccessToCjrHds(26, "Access to Conjure Hordes"),
    AccessToFrflHrts(27, "Access to Fearful Hearts"),
    AccessToFntcsm(28, "Access to Fanaticism"),
    AccessToSmnStrms(29, "Access to Summon Storms"),
    AccessToTeleport(30, "Access to Teleport"),
    KindapAssassAtPlus20(31, "Kindap/Assass. at Plus 20"),
    OpenSeasMvmnt(32, "Open Seas Movement"),
    WarshipsAtStr4(33, "Warships at Str 4"),
    WarshipsAtStr5(34, "Warships at Str 5");
    
	int number;
    String description;

    SNAEnum(int number, String description) {
    	this.number = number;
    	this.description = description;
    }

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
    
    public static SNAEnum getSnaFromNumber(int number) {
    	for (SNAEnum e : SNAEnum.values()) {
    		if (e.getNumber() == number) return e;
    	}
    	return null;
    }
}
