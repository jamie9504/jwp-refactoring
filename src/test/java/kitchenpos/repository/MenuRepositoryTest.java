package kitchenpos.repository;

import static kitchenpos.constants.Constants.TEST_MENU_NAME;
import static kitchenpos.constants.Constants.TEST_MENU_PRICE;
import static kitchenpos.constants.Constants.TEST_WRONG_MENU_ID;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import kitchenpos.domain.menu.Menu;
import kitchenpos.domain.menu.MenuProduct;
import kitchenpos.domain.menugroup.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MenuRepositoryTest extends KitchenPosRepositoryTest {

    @DisplayName("Menu 저장 - 성공")
    @Test
    void save_Success() {
        MenuGroup menuGroup = getCreatedMenuGroup();
        Menu menu = Menu.entityOf(TEST_MENU_NAME, TEST_MENU_PRICE, menuGroup,
            Collections.singletonList(MenuProduct.entityOf(getCreatedProduct(), 1)));
        Menu savedMenu = menuRepository.save(menu);

        assertThat(savedMenu.getId()).isNotNull();
        assertThat(savedMenu.getName()).isEqualTo(TEST_MENU_NAME);
        assertThat(savedMenu.getPrice()).isEqualByComparingTo(TEST_MENU_PRICE);
        assertThat(savedMenu.getMenuGroup()).isEqualTo(menuGroup);
        assertThat(savedMenu.getMenuProducts()).isNotNull();
    }

    @DisplayName("MenuID로 Menu 조회 - 조회됨, ID가 존재하는 경우")
    @Test
    void findById_ExistsId_ReturnMenu() {
        Menu menu = Menu.entityOf(TEST_MENU_NAME, TEST_MENU_PRICE, getCreatedMenuGroup(),
            Collections.singletonList(MenuProduct.entityOf(getCreatedProduct(), 1)));
        Menu savedMenu = menuRepository.save(menu);

        Menu foundMenu = menuRepository.findById(savedMenu.getId())
            .orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 Menu가 없습니다."));

        assertThat(foundMenu.getId()).isEqualTo(savedMenu.getId());
        assertThat(foundMenu.getName()).isEqualTo(savedMenu.getName());
        assertThat(foundMenu.getPrice()).isEqualByComparingTo(savedMenu.getPrice());
        assertThat(foundMenu.getMenuGroup()).isEqualTo(savedMenu.getMenuGroup());
        assertThat(foundMenu.getMenuProducts()).isEqualTo(savedMenu.getMenuProducts());
    }

    @DisplayName("MenuID로 Menu 조회 - 조회되지 않음, ID가 존재하지 않는 경우")
    @Test
    void findById_NotExistsId_ReturnEmpty() {
        Menu menu = Menu.entityOf(TEST_MENU_NAME, TEST_MENU_PRICE, getCreatedMenuGroup(),
            Collections.singletonList(MenuProduct.entityOf(getCreatedProduct(), 1)));
        Menu savedMenu = menuRepository.save(menu);

        Optional<Menu> foundMenu = menuRepository.findById(savedMenu.getId() + 1);

        assertThat(foundMenu.isPresent()).isFalse();
    }

    @DisplayName("전체 Menu 조회 - 성공")
    @Test
    void findAll_Success() {
        Menu menu = Menu.entityOf(TEST_MENU_NAME, TEST_MENU_PRICE, getCreatedMenuGroup(),
            Collections.singletonList(MenuProduct.entityOf(getCreatedProduct(), 1)));
        Menu savedMenu = menuRepository.save(menu);

        List<Menu> menus = menuRepository.findAll();

        assertThat(menus).isNotNull();
        assertThat(menus).isNotEmpty();

        List<Long> menuIds = menus.stream()
            .map(Menu::getId)
            .collect(Collectors.toList());

        assertThat(menuIds).contains(savedMenu.getId());
    }

    @DisplayName("ID에 해당하는 Menu 수 조회 - 성공")
    @Test
    void countByIdIn_Success() {
        Menu menu = Menu.entityOf(TEST_MENU_NAME, TEST_MENU_PRICE, getCreatedMenuGroup(),
            Collections.singletonList(MenuProduct.entityOf(getCreatedProduct(), 1)));
        Menu savedMenu = menuRepository.save(menu);

        List<Long> ids = Arrays.asList(savedMenu.getId(), TEST_WRONG_MENU_ID);

        long menuCount = menuRepository.countByIdIn(ids);

        assertThat(menuCount).isEqualTo(1);
    }
}
