package org.joverseer.engine;

import org.joverseer.domain.Order;
import org.joverseer.engine.orders.AnchorShipsOrder;
import org.joverseer.engine.orders.AttackOrder;
import org.joverseer.engine.orders.BuyOrder;
import org.joverseer.engine.orders.CastCombatSpellOrder;
import org.joverseer.engine.orders.CastConjuringSpellOrder;
import org.joverseer.engine.orders.CastLocationSpellOrder;
import org.joverseer.engine.orders.ChangeRelationsOrder;
import org.joverseer.engine.orders.ChangeTaxRateOrder;
import org.joverseer.engine.orders.CreateCampOrder;
import org.joverseer.engine.orders.CreateCompanyOrder;
import org.joverseer.engine.orders.DummyOrder;
import org.joverseer.engine.orders.FortifyPopOrder;
import org.joverseer.engine.orders.GuardPopOrder;
import org.joverseer.engine.orders.ImprovePopOrder;
import org.joverseer.engine.orders.InfYourOrder;
import org.joverseer.engine.orders.IssueChallengeOrder;
import org.joverseer.engine.orders.JoinArmyOrder;
import org.joverseer.engine.orders.JoinCompanyOrder;
import org.joverseer.engine.orders.MoveArmyOrder;
import org.joverseer.engine.orders.MoveCharacterOrder;
import org.joverseer.engine.orders.MoveCompanyOrder;
import org.joverseer.engine.orders.NameCharacterOrder;
import org.joverseer.engine.orders.NatSellOrder;
import org.joverseer.engine.orders.NatTranOrder;
import org.joverseer.engine.orders.PickupShipsOrder;
import org.joverseer.engine.orders.PutArmyOnManoeuversOrder;
import org.joverseer.engine.orders.ReconOrder;
import org.joverseer.engine.orders.RecruitOrder;
import org.joverseer.engine.orders.RefuseChallengesOrder;
import org.joverseer.engine.orders.SplitArmyOrder;
import org.joverseer.engine.orders.StealGoldOrder;
import org.joverseer.engine.orders.TransCarOrder;
import org.joverseer.engine.orders.TransferArtifactsOrder;
import org.joverseer.engine.orders.TransferCommandOrder;
import org.joverseer.engine.orders.TransferFoodFromArmyToPopOrder;
import org.joverseer.engine.orders.TransferFoodFromPopToArmyOrder;
import org.joverseer.engine.orders.TransferShipsOrder;
import org.joverseer.engine.orders.TransferTroopsOrder;
import org.joverseer.engine.orders.UseHidingArtifactOrder;

public class ExecutingOrderFactory {
	public ExecutingOrder createOrder(Order order) {
		int orderNo = order.getOrderNo();
		if (inList(orderNo, new int[]{180, 185})) {
			return new ChangeRelationsOrder(order);
		} else if (orderNo == 215) {
			return new RefuseChallengesOrder(order);
		} else if (orderNo == 210) {
			return new IssueChallengeOrder(order);
		} else if (orderNo == 225) {
			return new CastCombatSpellOrder(order);
		} else if (inList(orderNo, new int[]{230, 235, 250, 255})) {
			return new AttackOrder(order);
		} else if (orderNo == 300) {
			return new ChangeTaxRateOrder(order);
		} else if (inList(orderNo, new int[]{310, 315})) {
			return new BuyOrder(order);
		} else if (orderNo == 325) {
			return new NatSellOrder(order);
		} else if (orderNo == 330) {
			return new CastConjuringSpellOrder(order);
		} else if (orderNo == 340) {
			return new TransferFoodFromPopToArmyOrder(order);
		} else if (orderNo == 345) {
			return new TransferFoodFromArmyToPopOrder(order);
		} else if (orderNo == 355) {
			return new TransferTroopsOrder(order);
		} else if (orderNo == 357) {
			return new TransferShipsOrder(order);
		} else if (orderNo == 360) {
			return new TransferArtifactsOrder(order);
		} else if (inList(orderNo, new int[]{430, 435})) {
			return new PutArmyOnManoeuversOrder(order);
		} else if (orderNo == 494) {
			return new FortifyPopOrder(order);
		} else if (orderNo == 520) {
			return new InfYourOrder(order);
		} else if (orderNo == 550) {
			return new ImprovePopOrder(order);
		} else if (orderNo == 555) {
			return new CreateCampOrder(order);
		} else if (orderNo == 605) {
			return new GuardPopOrder(order);
		} else if (orderNo == 690) {
			return new StealGoldOrder(order);
		} else if (inList(orderNo, new int[]{785, 870})) {
			return new JoinArmyOrder(order);
		} else if (orderNo == 794) {
			return new AnchorShipsOrder(order);
		} else if (orderNo == 798) {
			return new PickupShipsOrder(order);
		} else if (orderNo == 810) {
			return new MoveCharacterOrder(order);
		} else if (inList(orderNo, new int[]{725,728, 731, 734, 737})) {
			return new NameCharacterOrder(order);
		} else if (inList(orderNo, new int[]{830, 850, 860})) {
			return new MoveArmyOrder(order);
		} else if (inList(orderNo, new int[]{400, 404, 408, 412, 416, 420, 770})) {
			return new RecruitOrder(order);
		} else if (orderNo == 820) {
			return new MoveCompanyOrder(order);
		} else if (orderNo == 745) {
			return new CreateCompanyOrder(order);
		} else if (orderNo == 755) {
			return new JoinCompanyOrder(order);
		} else if (orderNo == 765) {
			return new SplitArmyOrder(order);
		} else if (orderNo == 780) {
			return new TransferCommandOrder(order);
		} else if (orderNo == 925) {
			return new ReconOrder(order);
		} else if (orderNo == 940) {
			return new CastLocationSpellOrder(order);
		} else if (orderNo == 945) {
			return new UseHidingArtifactOrder(order);
		} else if (orderNo == 947) {
			return new NatTranOrder(order);
		} else if (orderNo == 948) {
			return new TransCarOrder(order);
		}
		
		return new DummyOrder(order);
	}
	
	protected boolean inList(int orderNo, int[] list) {
		for (int n : list) {
			if (n == orderNo) return true;
		}
		return false;
	}
}
