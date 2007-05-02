package org.joverseer.tools.orderCostCalculator;

import org.joverseer.domain.FortificationSizeEnum;
import org.joverseer.domain.NationEconomy;
import org.joverseer.domain.Order;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.domain.ProductEnum;
import org.joverseer.domain.ProductPrice;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.SNAEnum;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.metadata.domain.HexSideElementEnum;
import org.joverseer.metadata.domain.HexSideEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;


public class OrderCostCalculator {
    public int getOrderCost(Order o) {
        switch (o.getOrderNo()) {
            case 490:
                return buildBridgeCost(o);
            case 494:
                return fortifyPopCenterCost(o);
            case 552:
                return postCampCost(o);
            case 725:
                return nameNewCharCost(o);
            case 728:
                return nameNewCommanderCost(o);
            case 770:
                return hireArmyCost(o);
            case 950:
                return relocateCapitalCost(o);
            case 440:
                return makeWarMachinesCost(o);
            case 452:
                return makeWarshipsCost(o);
            case 456:
                return makeTransportsCost(o);
            case 731:
                return nameNewAgentCost(o);
            case 660:
                return offerRansomCost(o);
            case 505:
                return bribeCost(o);
            case 530:
                return improveHarborCost(o);
            case 535:
                return addHarborCost(o);
            case 550:
                return improvePopCenterCost(o);
            case 555:
                return createCampCost(o);
            case 734:
                return nameNewEmissaryCost(o);
            case 737:
                return nameNewMageCost(o);
            case 705:
                return researchSpellCost(o);
            case 310:
                return bidFromCaravanCost(o);
            case 315:
                return purchaseFromCaravanCost(o);
            case 320:
                return sellToCaravanCost(o);
            case 325:
                return natSellToCaravanCost(o);
        }
        return 0;
    }
    
    public int bidFromCaravanCost(Order o) {
        try {
            int no = Integer.parseInt(o.getParameter(1));
            int bid = Integer.parseInt(o.getParameter(2));
            return no * bid;
        }
        catch (Exception exc) {
            return -1;
        }
    }
    
    public int purchaseFromCaravanCost(Order o) 
    {
        ProductEnum pe = ProductEnum.getFromCode(o.getParameter(0));
        if (pe == null) return 0;
        try {
            int no = Integer.parseInt(o.getParameter(1));
            ProductPrice pp = (ProductPrice)GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.ProductPrice).findFirstByProperty("product", pe);
            return no * pp.getBuyPrice();
        }
        catch (Exception exc) {
            return -1;
        }
    }

    public int sellToCaravanCost(Order o) 
    {
        ProductEnum pe = ProductEnum.getFromCode(o.getParameter(0));
        if (pe == null) return 0;
        try {
            int no = Integer.parseInt(o.getParameter(1));
            ProductPrice pp = (ProductPrice)GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.ProductPrice).findFirstByProperty("product", pe);
            return - no * pp.getSellPrice();
        }
        catch (Exception exc) {
            return -1;
        }
    }

    public int natSellToCaravanCost(Order o) 
    {
        ProductEnum pe = ProductEnum.getFromCode(o.getParameter(0));
        if (pe == null) return 0;
        try {
            int pct = Integer.parseInt(o.getParameter(1));
            NationEconomy ne = (NationEconomy)GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.NationEconomy).findFirstByProperty("nationNo", o.getCharacter().getNationNo());
            ProductPrice pp = (ProductPrice)GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.ProductPrice).findFirstByProperty("product", pe);
            return - (ne.getStores().getProduct(pe) + ne.getProduction().getProduct(pe)) * pct * pp.getSellPrice() / 100;
        }
        catch (Exception exc) {
            return -1;
        }
    }

    public int researchSpellCost(Order o) {
        return 1000;
    }
    
    public int nameNewMageCost(Order o) {
        return 5000;
    }
    
    public int nameNewEmissaryCost(Order o) {
        return 5000;
    }
    
    public int createCampCost(Order o) {
        return 2000;
    }
    
    public int improvePopCenterCost(Order o) {
        PopulationCenter pc = (PopulationCenter)GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperty("hexNo", o.getCharacter().getHexNo());
        if (pc == null) return -1;
        if (pc.getSize() == PopulationCenterSizeEnum.camp) {
            return 4000;
        } else if (pc.getSize() == PopulationCenterSizeEnum.village) {
            return 6000;
        } else if (pc.getSize() == PopulationCenterSizeEnum.town) {
            return 8000;
        } else if (pc.getSize() == PopulationCenterSizeEnum.majorTown) {
            return 10000;
        } else if (pc.getSize() == PopulationCenterSizeEnum.city) {
            return -1;
        } 
        return 0;
    }
    
    public int addHarborCost(Order o) {
        return 2500;
    }
    
    public int improveHarborCost(Order o) {
        return 4000;
    }
    
    public int bribeCost(Order o) {
        try {
            int no = Integer.parseInt(o.getParameter(1));
            return no;
        }
        catch (Exception exc) {
            return -1;
        }
    }
    
    public int offerRansomCost(Order o) {
        try {
            int no = Integer.parseInt(o.getParameter(1));
            return no;
        }
        catch (Exception exc) {
            return -1;
        }
    }
    
    public int nameNewAgentCost(Order o) {
        return 5000;
    }

    public int makeTransportsCost(Order o) {
        try {
            int no = Integer.parseInt(o.getParameter(0));
            return no * 1000;
        }
        catch (Exception exc) {
            return -1;
        }
    }
    public int makeWarshipsCost(Order o) {
        try {
            int no = Integer.parseInt(o.getParameter(0));
            return no * 1000;
        }
        catch (Exception exc) {
            return -1;
        }
    }
    
    public int makeWarMachinesCost(Order o) {
        try {
            int no = Integer.parseInt(o.getParameter(0));
            return 0;
        }
        catch (Exception exc) {
            return -1;
        }
    }
    
    public int relocateCapitalCost(Order o) {
        return 30000;
    }
    
    public int hireArmyCost(Order o) {
        Nation n = GameHolder.instance().getGame().getMetadata().getNationByNum(o.getCharacter().getNationNo());
        if (n != null && n.hasSna(SNAEnum.FreeHire)) {
            return 0;
        }
        return 5000;
    }
    
    public int nameNewCommanderCost(Order o) {
        return 5000;
    }
    
    public int nameNewCharCost(Order o) {
        return 10000;
    }
    
    public int postCampCost(Order o) {
        return 4000;
    }
    
    public int fortifyPopCenterCost(Order o) {
        PopulationCenter pc = (PopulationCenter)GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperty("hexNo", o.getCharacter().getHexNo());
        if (pc == null) return -1;
        if (pc.getFortification() == FortificationSizeEnum.none) {
            return 1000;
        } else if (pc.getFortification() == FortificationSizeEnum.tower) {
            return 3000;
        } else if (pc.getFortification() == FortificationSizeEnum.fort) {
            return 5000;
        } else if (pc.getFortification() == FortificationSizeEnum.castle) {
            return 8000;
        } else if (pc.getFortification() == FortificationSizeEnum.keep) {
            return 10000;
        } else if (pc.getFortification() == FortificationSizeEnum.citadel) {
            return -1;
        }
        return 0;
    }
    
    public int buildBridgeCost(Order o) {
        Hex hex = GameHolder.instance().getGame().getMetadata().getHex(o.getCharacter().getHexNo());
        if (hex == null) {
            return -1;
        }
        
        String dir = o.getParameter(0);
        HexSideEnum side = null;
        if (dir.equals("ne")) {
            side = HexSideEnum.TopRight;
        } else if (dir.equals("nw")) {
            side = HexSideEnum.TopLeft;
        } else if (dir.equals("w")) {
            side = HexSideEnum.Left;
        } else if (dir.equals("sw")) {
            side = HexSideEnum.BottomLeft;
        } else if (dir.equals("se")) {
            side = HexSideEnum.BottomRight;
        } else if (dir.equals("e")) {
            side = HexSideEnum.Right;
        }  
        if (side == null) return -1;
        if (hex.getHexSideElements(side).contains(HexSideElementEnum.MajorRiver)) {
            return 5000;
        }
        if (hex.getHexSideElements(side).contains(HexSideElementEnum.MinorRiver)) {
            return 2500;
        }
        return 0;
    }
}
