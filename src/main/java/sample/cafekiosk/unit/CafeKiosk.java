package sample.cafekiosk.unit;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import sample.cafekiosk.unit.beverage.Beverage;
import sample.cafekiosk.unit.order.Order;

@Getter
public class CafeKiosk {

	public static final LocalTime SHOP_OPEN_TIME = LocalTime.of(10, 0);
	public static final LocalTime SHOP_CLOSE_TIME = LocalTime.of(22, 0);

	private final List<Beverage> beverages = new ArrayList<>();

	public void add(Beverage beverage) {
		beverages.add(beverage);
	}

	public void add(Beverage beverage, int count) {
		if(count <= 0){
			throw new IllegalArgumentException("beverage is must 1 over");
		}

		for(int i=0; i<count; i++){
			beverages.add(beverage);
		}
	}

	public void remove(Beverage beverage){
		beverages.remove(beverage);
	}

	public void clear(){
		beverages.clear();
	}

	public int calculateTotalPrice() {
		// int totalPrice = 0;
		// for(Beverage beverage : beverages){
		// 	totalPrice = totalPrice + beverage.getPrice();
		// }
		// return totalPrice;

		return beverages.stream()
			.mapToInt(Beverage::getPrice)
			.sum();
	}


	public Order createOrder(){
		LocalDateTime currentDateTime = LocalDateTime.now();
		LocalTime currentTime = currentDateTime.toLocalTime();
		if(currentTime.isBefore(SHOP_OPEN_TIME) || currentTime.isAfter(SHOP_CLOSE_TIME)){
			throw new IllegalArgumentException("It is not order Time. please contact to Admin");
		}

		return new Order(currentDateTime, beverages);
	}

	public Order createOrder(LocalDateTime currentDateTime){
		LocalTime currentTime = currentDateTime.toLocalTime();
		if(currentTime.isBefore(SHOP_OPEN_TIME) || currentTime.isAfter(SHOP_CLOSE_TIME)){
			throw new IllegalArgumentException("It is not order Time. please contact to Admin");
		}

		return new Order(currentDateTime, beverages);
	}
}
