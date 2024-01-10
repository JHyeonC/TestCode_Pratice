package sample.cafekiosk.spring.api.service.order;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import sample.cafekiosk.spring.api.controller.order.request.OrderCreateRequest;
import sample.cafekiosk.spring.api.service.order.response.OrderResponse;
import sample.cafekiosk.spring.domain.order.Order;
import sample.cafekiosk.spring.domain.order.OrderRepository;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductType;
import sample.cafekiosk.spring.domain.stock.Stock;
import sample.cafekiosk.spring.domain.stock.StockRepository;

@Transactional
@RequiredArgsConstructor
@Service
public class OrderService {

	private final ProductRepository productRepository;
	private final OrderRepository orderRepository;
	private final StockRepository stockRepository;

	/**
	 * 재고 감소 -> 동시성 고민
	 * optimistic lock / pessimistic lock / ...
	 * 데이터의 락을 잡고 순차적인 처리
	 * 키오스크의 동시 접수 수 등등
	 */
	public OrderResponse createOrder(OrderCreateRequest request, LocalDateTime registeredDateTime) {
		List<String> productNumbers = request.getProductNumbers();
		List<Product> products = findProductsBy(productNumbers);

		deductStockQuantity(products);

		Order order = Order.create(products, registeredDateTime);
		Order saveOrder = orderRepository.save(order);

		return OrderResponse.of(saveOrder);
	}

	private void deductStockQuantity(List<Product> products) {
		List<String> stockProductNumbers = extractStockProductNumbers(products);

		Map<String, Stock> stockMap = createStockMapBy(stockProductNumbers);

		Map<String, Long> productCountingMap = createCountingMapBy(stockProductNumbers);

		for(String stockProductNumber : new HashSet<>(stockProductNumbers)){
			Stock stock = stockMap.get(stockProductNumber);
			int quantity = productCountingMap.get(stockProductNumber).intValue();

			if (stock.isQuantityLessThan(quantity)){
				throw new IllegalArgumentException("재고가 부족한 상품이 있습니다.");
			}
			stock.deductQuantity(quantity);
		}
	}

	private static List<String> extractStockProductNumbers(List<Product> products) {
		return products.stream()
			.filter(product -> ProductType.containsStockType(product.getType()))
			.map(Product::getProductNumber)
			.collect(Collectors.toList());
	}

	private Map<String, Stock> createStockMapBy(List<String> stockProductNumbers) {
		List<Stock> stocks = stockRepository.findAllByProductNumberIn(stockProductNumbers);
		Map<String, Stock> stockMap = stocks.stream()
			.collect(Collectors.toMap(Stock::getProductNumber, s -> s));
		return stockMap;
	}

	private static Map<String, Long> createCountingMapBy(List<String> stockProductNumbers) {
		Map<String, Long> productCountingMap = stockProductNumbers.stream()
			.collect(Collectors.groupingBy(p -> p, Collectors.counting()));
		return productCountingMap;
	}

	private List<Product> findProductsBy(List<String> productNumbers) {
		List<Product> products = productRepository.findAllByProductNumberIn(productNumbers);

		Map<String, Product> productMap = products.stream()
			.collect(Collectors.toMap(Product::getProductNumber, p -> p));

		List<Product> duplicateProducts = productNumbers.stream()
			.map(productMap::get)
			.collect(Collectors.toList());
		return duplicateProducts;
	}
}
