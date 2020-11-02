package kitchenpos.ui;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroup;
import kitchenpos.ui.dto.MenuGroupRequest;
import kitchenpos.ui.dto.MenuGroupResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(MenuGroupRestController.class)
class MenuGroupRestControllerTest {

    private static final Long MENU_GROUP_ID = 1L;
    private static final String MENU_GROUP_NAME = "추천메뉴";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MenuGroupService menuGroupService;

    @DisplayName("메뉴 그룹 추가")
    @Test
    void create() throws Exception {
        MenuGroup menuGroup = MenuGroup.of(MENU_GROUP_ID, MENU_GROUP_NAME);

        String requestBody = "{\n"
            + "  \"name\": \"" + menuGroup.getName() + "\"\n"
            + "}";

        given(menuGroupService.create(any(MenuGroupRequest.class)))
            .willReturn(MenuGroupResponse.of(menuGroup));

        final ResultActions resultActions = mockMvc.perform(post("/api/menu-groups")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andDo(print());

        resultActions
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(menuGroup.getId().intValue())))
            .andExpect(jsonPath("$.name", is(menuGroup.getName())))
            .andExpect(header().exists(HttpHeaders.LOCATION))
            .andDo(print());
    }

    @DisplayName("메뉴 그룹 전체 조회")
    @Test
    void list() throws Exception {
        MenuGroup menuGroup = MenuGroup.of(MENU_GROUP_ID, MENU_GROUP_NAME);

        given(menuGroupService.list())
            .willReturn(MenuGroupResponse.listOf(Collections.singletonList(menuGroup)));

        final ResultActions resultActions = mockMvc.perform(get("/api/menu-groups")
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print());

        resultActions
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", is(menuGroup.getId().intValue())))
            .andDo(print());
    }
}
