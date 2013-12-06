package org.joverseer.orders.me;

import java.util.ArrayList;

import org.joverseer.domain.NationEconomy;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.ProductEnum;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.orders.AbstractTurnPhaseProcessor;
import org.joverseer.orders.OrderUtils;


public class ProductionRevenuePhaseProcessor extends AbstractTurnPhaseProcessor {
    public ProductionRevenuePhaseProcessor(String name) {
        super(name);
    }

    @Override
	public void processPhase(Turn t) {
        Game g = OrderUtils.getGame();
        for (int i=1; i<=g.getMetadata().getNationNo(); i++) {
            NationEconomy ne = (NationEconomy)t.getContainer(TurnElementsEnum.NationEconomy).findFirstByProperty("nationNo", i);
            int nationGoldProduction = 0;
            int nationTaxRevenue = 0;
            int taxBase = 0;
            for (PopulationCenter pc : (ArrayList<PopulationCenter>)t.getContainer(TurnElementsEnum.PopulationCenter).findAllByProperty("nationNo", i)) {
                for (ProductEnum p : ProductEnum.values()) {
                    if (p == ProductEnum.Gold) {
                        Integer prod = pc.getProduction(p);
                        if (prod == null) prod = 0;
                        nationGoldProduction += prod;
                    } else {
                        Integer stores = pc.getStores(p);
                        Integer prod = pc.getProduction(p);
                        if (stores == null) stores = 0;
                        if (prod == null) prod = 0;
                        Integer total = stores + prod;
                        if (total == 0) total = null;
                        pc.setStores(p, total);
                    }
                }
                taxBase += pc.getSize().getCode() - 1;
                nationTaxRevenue += (pc.getSize().getCode() - 1) * 2500 * ne.getTaxRate() / 100;
            }
            ne.setTaxBase(taxBase);
            ne.setRevenue(nationGoldProduction + nationTaxRevenue);
            ne.setReserve(ne.getReserve() + ne.getRevenue());

        }
    }
}
