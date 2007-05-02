package org.joverseer.ui.domain;

import org.joverseer.domain.IHasMapLocation;
import org.joverseer.domain.IHasTurnNumber;

    public class LocateArtifactResult implements IHasMapLocation, IHasTurnNumber {
        int hexNo;
        String spellName;
        String caster;
        int artifactNo;
        String artifactName;
        String artifactPowers;
        int turnNo;
        String owner;
        
        public String getArtifactName() {
            return artifactName;
        }
        
        public void setArtifactName(String artifactName) {
            this.artifactName = artifactName;
        }
        
        public int getArtifactNo() {
            return artifactNo;
        }
        
        public void setArtifactNo(int artifactNo) {
            this.artifactNo = artifactNo;
        }
        
        public String getArtifactPowers() {
            return artifactPowers;
        }
        
        public void setArtifactPowers(String artifactPowers) {
            this.artifactPowers = artifactPowers;
        }
        
        public String getCaster() {
            return caster;
        }
        
        public void setCaster(String caster) {
            this.caster = caster;
        }
        
        public int getHexNo() {
            return hexNo;
        }
        
        public void setHexNo(int hexNo) {
            this.hexNo = hexNo;
        }
        
        public String getSpellName() {
            return spellName;
        }
        
        public void setSpellName(String spellName) {
            this.spellName = spellName;
        }
        
        public int getTurnNo() {
            return turnNo;
        }
        
        public void setTurnNo(int turnNo) {
            this.turnNo = turnNo;
        }

        public int getX() {
            return getHexNo() / 100;
        }

        public int getY() {
            return getHexNo() % 100;
        }

        
        public String getOwner() {
            return owner;
        }

        
        public void setOwner(String owner) {
            this.owner = owner;
        }
        

        
    }
