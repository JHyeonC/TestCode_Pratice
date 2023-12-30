package sample.cafekiosk.unit;

import sample.cafekiosk.unit.beverage.Americano;
import sample.cafekiosk.unit.beverage.Latte;

public class CafeKioskRunner {

	public static void main(String[] args){
		CafeKiosk cafeKiosk = new CafeKiosk();
		cafeKiosk.add(new Americano());
		System.out.println(">>> Add Americano");

		cafeKiosk.add(new Latte());
		System.out.println(">>> Add Latte");

		int totalPrice = cafeKiosk.calculateTotalPrice();
		System.out.println(">>> Total Price : " + totalPrice);
	}
}
