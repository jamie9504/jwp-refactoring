package kitchenpos.dao;

import static kitchenpos.constants.Constants.TEST_ORDER_ORDERED_TIME;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.Table;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderRepositoryTest extends KitchenPosDaoTest {

    @DisplayName("Order 저장 - 성공")
    @Test
    void save_Success() {
        Table table = getCreatedTable();
        Order order = Order
            .entityOf(table, OrderStatus.COOKING.name(), TEST_ORDER_ORDERED_TIME, null);

        Order savedOrder = orderRepository.save(order);

        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getTable()).isEqualTo(table);
        assertThat(savedOrder.getOrderStatus()).isEqualTo(OrderStatus.COOKING.name());
        assertThat(savedOrder.getOrderedTime()).isEqualTo(TEST_ORDER_ORDERED_TIME);
        assertThat(savedOrder.getOrderLineItems()).isNull();
    }

    @DisplayName("Order ID로 Order 조회 - 조회됨, ID가 존재하는 경우")
    @Test
    void findById_ExistsId_ReturnOrder() {
        Table table = getCreatedTable();
        Order order = Order
            .entityOf(table, OrderStatus.COOKING.name(), TEST_ORDER_ORDERED_TIME, null);

        Order savedOrder = orderRepository.save(order);

        Order foundOrder = orderRepository.findById(savedOrder.getId())
            .orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 order가 없습니다."));

        assertThat(foundOrder.getId()).isEqualTo(savedOrder.getId());
        assertThat(foundOrder.getTable()).isEqualTo(savedOrder.getTable());
        assertThat(foundOrder.getOrderStatus()).isEqualTo(savedOrder.getOrderStatus());
        assertThat(foundOrder.getOrderedTime()).isEqualTo(savedOrder.getOrderedTime());
        assertThat(foundOrder.getOrderLineItems()).isEqualTo(savedOrder.getOrderLineItems());
    }

    @DisplayName("Order ID로 Order 조회 - 조회되지 않음, ID가 존재하지 않는 경우")
    @Test
    void findById_NotExistsId_ReturnEmpty() {
        Order order = Order
            .entityOf(getCreatedTable(), OrderStatus.COOKING.name(), TEST_ORDER_ORDERED_TIME, null);
        Order savedOrder = orderRepository.save(order);

        Optional<Order> foundOrder = orderRepository.findById(savedOrder.getId() + 1);

        assertThat(foundOrder.isPresent()).isFalse();
    }

    @DisplayName("전체 Order 조회 - 성공")
    @Test
    void findAll_Success() {
        Order order = Order
            .entityOf(getCreatedTable(), OrderStatus.COOKING.name(), TEST_ORDER_ORDERED_TIME, null);
        Order savedOrder = orderRepository.save(order);

        List<Order> orders = orderRepository.findAll();

        assertThat(orders).isNotNull();
        assertThat(orders).isNotEmpty();
        assertThat(orders).contains(savedOrder);
    }

    @DisplayName("OrderTableId와 OrderStatus들과 매치되는 Order가 있는지 확인 - True, 모두 매치")
    @Test
    void existsByOrderTableIdAndOrderStatusIn_MatchedOrderTableIdAndOrderStatus_ReturnTrue() {
        Table table = getCreatedTable();
        List<String> orderStatuses
            = Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name());

        for (String orderStatus : orderStatuses) {
            Order order = Order
                .entityOf(table, orderStatus, TEST_ORDER_ORDERED_TIME, null);
            orderRepository.save(order);
        }

        boolean existsOrder = orderRepository
            .existsByTable_IdAndOrderStatusIn(table.getId(), orderStatuses);

        assertThat(existsOrder).isTrue();
    }

    @DisplayName("OrderTableId와 OrderStatus들과 매치되는 Order가 있는지 확인 - False, OrderStatus만 매치")
    @Test
    void existsByOrderTableIdAndOrderStatusIn_MatchedOnlyOrderStatus_ReturnFalse() {
        Table table = getCreatedTable();
        Table otherTable = getCreatedTable();
        List<String> orderStatuses
            = Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name());

        for (String orderStatus : orderStatuses) {
            Order order = Order
                .entityOf(table, orderStatus, TEST_ORDER_ORDERED_TIME, null);
            orderRepository.save(order);
        }

        boolean existsOrder = orderRepository
            .existsByTable_IdAndOrderStatusIn(otherTable.getId(), orderStatuses);

        assertThat(existsOrder).isFalse();
    }

    @DisplayName("OrderTableId와 OrderStatus들과 매치되는 Order가 있는지 확인 - False, OrderTableId만 매치")
    @Test
    void existsByOrderTableIdAndOrderStatusIn_MatchedOnlyOrderTableId_ReturnFalse() {
        Table table = getCreatedTable();
        List<String> orderStatuses
            = Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name());

        Order order = Order
            .entityOf(table, OrderStatus.COMPLETION.name(), TEST_ORDER_ORDERED_TIME, null);
        orderRepository.save(order);

        boolean existsOrder = orderRepository
            .existsByTable_IdAndOrderStatusIn(table.getId(), orderStatuses);

        assertThat(existsOrder).isFalse();
    }

    @DisplayName("OrderTableId와 OrderStatus들과 매치되는 Order가 있는지 확인 - False, 매치되는 것 없음")
    @Test
    void existsByOrderTableIdAndOrderStatusIn_MatchedNothing_ReturnFalse() {
        Table table = getCreatedTable();
        Table orderTable = getCreatedTable();
        List<String> orderStatuses
            = Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name());

        Order order = Order
            .entityOf(table, OrderStatus.COMPLETION.name(), TEST_ORDER_ORDERED_TIME, null);
        orderRepository.save(order);

        boolean existsOrder = orderRepository
            .existsByTable_IdAndOrderStatusIn(orderTable.getId(), orderStatuses);

        assertThat(existsOrder).isFalse();
    }

    @DisplayName("OrderTableId들과 OrderStatus들과 매치되는 Order가 있는지 확인 - True, 모두 매치")
    @Test
    void existsByTableInAndOrderStatusIn_MatchedOrderTableIdsAndOrderStatus_ReturnTrue() {
        List<Table> tables = Arrays.asList(getCreatedTable(), getCreatedTable());
        List<String> orderStatuses
            = Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name());

        Order order = Order
            .entityOf(tables.get(0), orderStatuses.get(0), TEST_ORDER_ORDERED_TIME, null);
        orderRepository.save(order);

        boolean existsOrder = orderRepository
            .existsByTableInAndOrderStatusIn(tables, orderStatuses);

        assertThat(existsOrder).isTrue();
    }

    @DisplayName("OrderTableId들과 OrderStatus들과 매치되는 Order가 있는지 확인 - False, OrderTableIds만 매치")
    @Test
    void existsByTableInAndOrderStatusIn_MatchedOrderTableIds_ReturnFalse() {
        List<Table> tables = Arrays.asList(getCreatedTable(), getCreatedTable());
        List<String> orderStatuses
            = Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name());

        Order order = Order
            .entityOf(tables.get(0), OrderStatus.COMPLETION.name(), TEST_ORDER_ORDERED_TIME, null);
        orderRepository.save(order);

        boolean existsOrder = orderRepository
            .existsByTableInAndOrderStatusIn(tables, orderStatuses);

        assertThat(existsOrder).isFalse();
    }

    @DisplayName("OrderTableId들과 OrderStatus들과 매치되는 Order가 있는지 확인 - False, OrderStatuses만 매치")
    @Test
    void existsByTableInAndOrderStatusIn_MatchedOrderStatuses_ReturnFalse() {
        List<Table> tables = Arrays.asList(getCreatedTable(), getCreatedTable());
        List<String> orderStatuses
            = Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name());

        Order order = Order
            .entityOf(getCreatedTable(), orderStatuses.get(0), TEST_ORDER_ORDERED_TIME, null);
        orderRepository.save(order);

        boolean existsOrder = orderRepository
            .existsByTableInAndOrderStatusIn(tables, orderStatuses);

        assertThat(existsOrder).isFalse();
    }

    @DisplayName("OrderTableId들과 OrderStatus들과 매치되는 Order가 있는지 확인 - False, 매치되는 것 없음")
    @Test
    void existsByTableInAndOrderStatusIn_MatchedNothing_ReturnFalse() {
        List<Table> tables = Arrays.asList(getCreatedTable(), getCreatedTable());
        List<String> orderStatuses
            = Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name());

        Order order = Order
            .entityOf(getCreatedTable(), OrderStatus.COMPLETION.name(), TEST_ORDER_ORDERED_TIME,
                null);
        orderRepository.save(order);

        boolean existsOrder = orderRepository
            .existsByTableInAndOrderStatusIn(tables, orderStatuses);

        assertThat(existsOrder).isFalse();
    }
}
