package org.joverseer.orders;

import java.util.ArrayList;
import java.util.Arrays;

import org.joverseer.domain.Character;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;

public class TurnOrderPhaseProcessor extends AbstractTurnPhaseProcessor {

    BaseOrderScheduler orderScheduler;
    Integer[] phaseOrderNumbers;
    
    public TurnOrderPhaseProcessor(String name) {
        super(name);
    }

    public TurnOrderPhaseProcessor(String name, String orderNumbers) {
        super(name);
        String[] ons = orderNumbers.split(",");
        ArrayList<Integer> onList = new ArrayList<Integer>();
        for (String on : ons) {
            onList.add(Integer.parseInt(on));
        }
        setPhaseOrderNumbers(onList.toArray(new Integer[]{}));
    }

    public Integer[] getPhaseOrderNumbers() {
        return phaseOrderNumbers;
    }
    
    public void setPhaseOrderNumbers(Integer[] orderNumbers) {
        phaseOrderNumbers = orderNumbers;
    }

    public ArrayList<OrderExecutionWrapper> getOrdersForPhase(Turn t) {
        ArrayList<OrderExecutionWrapper> orders = new ArrayList<OrderExecutionWrapper>();
        Integer[] orderNumbers = getPhaseOrderNumbers();
        Arrays.sort(orderNumbers);
        for (Character c : (ArrayList<Character>)t.getContainer(TurnElementsEnum.Character).getItems()) {
            for (int i=0; i<2; i++) {
                if (Arrays.binarySearch(orderNumbers, c.getOrders()[i].getOrderNo()) > 0) {
                    orders.add(new OrderExecutionWrapper(c, i));
                }
            }
        }
        return orders;
    }
    
    public void processPhase(Turn t) {
        getOrderScheduler().scheduleOrders(getOrdersForPhase(t));
        ArrayList<OrderExecutionWrapper> scheduledOrders = getOrderScheduler().getScheduledOrders();
        for (OrderExecutionWrapper oew : scheduledOrders) {
            for (AbstractOrderProcessor processor : AbstractOrderProcessor.processorRegistry) {
                if (processor.appliesTo(oew.getCharacter(), oew.getOrderNo())) {
                    processor.processOrder(t, oew.getCharacter(), oew.getOrderNo());
                }
            }
        }
    }

    
    public BaseOrderScheduler getOrderScheduler() {
        return orderScheduler;
    }

    
    public void setOrderScheduler(BaseOrderScheduler orderScheduler) {
        this.orderScheduler = orderScheduler;
    }
    
    
}
