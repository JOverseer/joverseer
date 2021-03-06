package org.joverseer.ui.listviews;

import java.util.ArrayList;

import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.NationEconomy;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.ProductEnum;
import org.joverseer.domain.ProductPrice;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.ui.domain.NationEconomyPotential;

/**
 * List view for NationEconomyPotential objects.
 * 
 * @author Marios Skounakis
 */
public class NationEconomyPotentialListView extends BaseItemListView {

    public NationEconomyPotentialListView() {
        super(NationEconomyPotentialTableModel.class);
    }

    @Override
	protected int[] columnWidths() {
        return new int[]{100, 64, 64, 64, 64, 48, 64, 48, 64};
    }

    @Override
	protected void setItems() {
    	Turn t = this.getTurn();
        ArrayList<NationEconomyPotential> potentials = new ArrayList<NationEconomyPotential>();
        if (t != null) {
            for (NationEconomy ne : (ArrayList<NationEconomy>)t.getContainer(TurnElementsEnum.NationEconomy).getItems()) {
                //TODO move NationEconomyPotential creation to TableModel or other place out of here
                NationEconomyPotential nep = ((NationEconomyPotentialTableModel)this.tableModel).getNewPotential();
                nep.setNationNo(ne.getNationNo());
                nep.setSurplus(ne.getSurplus());
                nep.setTotal(ne.getReserve()+ ne.getSurplus());
                nep.setReserve(ne.getReserve());
                
                
                int bestNatSell = 0;
                ProductEnum bestNatSellProd = null;
                ProductEnum secondBestNatSellProd = null;
                int secondBestNatSell = 0;
                for (ProductEnum pe : ProductEnum.values()) {
                    if (pe == ProductEnum.Gold) continue;
                    ProductPrice pp = (ProductPrice)t.getContainer(TurnElementsEnum.ProductPrice).findFirstByProperty("product", pe);
                    int amt = pp.getSellPrice() * (ne.getStores(pe) + ne.getProduction(pe));
                    if (amt > bestNatSell) {
                        secondBestNatSell = bestNatSell;
                        bestNatSell = amt;
                        secondBestNatSellProd = bestNatSellProd;
                        bestNatSellProd = pe;
                        
                    } else if (amt > secondBestNatSell) {
                        secondBestNatSell = amt;
                        secondBestNatSellProd = pe;
                    }
                }
                
                nep.setOneNatSellProduct(bestNatSellProd);
                nep.setOneNatSell(bestNatSell);
                nep.setTwoNatSells(secondBestNatSell);
                nep.setTwoNatSellProduct(secondBestNatSellProd);
                
                PopulationCenter capital = (PopulationCenter)t.getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperties(new String[]{"nationNo", "capital"}, new Object[]{ne.getNationNo(), true});
                if (capital != null) {
                    nep.setCharsInCapital(t.getContainer(TurnElementsEnum.Character).findAllByProperties(new String[]{"nationNo", "hexNo", "deathReason"}, new Object[]{ne.getNationNo(), capital.getHexNo(), CharacterDeathReasonEnum.NotDead}).size());
                    nep.setHexNo(capital.getHexNo());
                }
                
                potentials.add(nep);
                
            }
        }
        this.tableModel.setRows(potentials);
    }

}
