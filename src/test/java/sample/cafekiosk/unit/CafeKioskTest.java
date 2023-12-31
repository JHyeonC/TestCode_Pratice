package sample.cafekiosk.unit;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import sample.cafekiosk.unit.beverage.Americano;
import sample.cafekiosk.unit.beverage.Latte;
import sample.cafekiosk.unit.order.Order;

class CafeKioskTest {

	@Test
	void add_mannual_test(){
		CafeKiosk cafeKiosk = new CafeKiosk();
		cafeKiosk.add(new Americano());

		System.out.println(">>> add beverages amount : " + cafeKiosk.getBeverages().size());
		System.out.println(">>> add beverage's Name : " + cafeKiosk.getBeverages().get(0).getName());
	}

	@DisplayName("음료 1개를 추가하면 주문 목록에 담긴다.")
	@Test
	void add() {
		CafeKiosk cafeKiosk = new CafeKiosk();
		cafeKiosk.add(new Americano());

		assertThat(cafeKiosk.getBeverages().size()).isEqualTo(1);
		assertThat(cafeKiosk.getBeverages().get(0).getName()).isEqualTo("Americano");
	}

	@Test
	void addSeveralBeverages() {
		CafeKiosk cafeKiosk = new CafeKiosk();
		Americano americano = new Americano();

		cafeKiosk.add(americano, 2);

		assertThat(cafeKiosk.getBeverages().get(0)).isEqualTo(americano);
		assertThat(cafeKiosk.getBeverages().get(1)).isEqualTo(americano);
	}

	@Test
	void addZeroBeverages() {
		CafeKiosk cafeKiosk = new CafeKiosk();
		Americano americano = new Americano();

		assertThatThrownBy(() -> cafeKiosk.add(americano, 0))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("beverage is must 1 over");
	}

	@Test
	void remove() {
		CafeKiosk cafeKiosk = new CafeKiosk();
		Americano americano = new Americano();
		cafeKiosk.add(americano);
		assertThat(cafeKiosk.getBeverages()).hasSize(1);

		cafeKiosk.remove(americano);
		assertThat(cafeKiosk.getBeverages()).hasSize(0);
	}

	@Test
	void clear(){
		CafeKiosk cafeKiosk = new CafeKiosk();
		Americano americano = new Americano();
		Latte latte = new Latte();

		cafeKiosk.add(americano);
		cafeKiosk.add(latte);
		assertThat(cafeKiosk.getBeverages()).hasSize(2);

		cafeKiosk.clear();
		assertThat(cafeKiosk.getBeverages()).hasSize(0);
	}

	@DisplayName("주문 목록에 담긴 상품들의 총 금액을 게산할 수 있다.")
	@Test
	void calculateTotalPrice(){
		// given
		CafeKiosk cafeKiosk = new CafeKiosk();
		Americano americano = new Americano();
		Latte latte = new Latte();

		cafeKiosk.add(americano);
		cafeKiosk.add(latte);
		// when
		int totalPrice = cafeKiosk.calculateTotalPrice();

		// then
		assertThat(totalPrice).isEqualTo(8500);
	}

	@Test
	void createOrder(){
		CafeKiosk cafeKiosk = new CafeKiosk();
		Americano americano = new Americano();

		cafeKiosk.add(americano);

		Order order = cafeKiosk.createOrder();
		assertThat(order.getBeverages()).hasSize(1);
		assertThat(order.getBeverages().get(0).getName()).isEqualTo("Americano");
	}

	@Test
	void createOrderWithCurrentTime(){
		CafeKiosk cafeKiosk = new CafeKiosk();
		Americano americano = new Americano();

		cafeKiosk.add(americano);

		Order order = cafeKiosk.createOrder(LocalDateTime.of(2024, 1, 02, 14, 0));

		assertThat(order.getBeverages()).hasSize(1);
		assertThat(order.getBeverages().get(0).getName()).isEqualTo("Americano");
	}

	@Test
	void createOrderWithOutsideOpenTime(){
		CafeKiosk cafeKiosk = new CafeKiosk();
		Americano americano = new Americano();

		cafeKiosk.add(americano);

		assertThatThrownBy(() -> cafeKiosk.createOrder(LocalDateTime.of(2024, 1, 02, 9, 59)))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("It is not order Time. please contact to Admin");
	}
}