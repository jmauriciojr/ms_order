package com.ambev.ms_order.resource;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ambev.ms_order.commons.enums.OrderStatus;
import com.ambev.ms_order.exception.OrderBadRequestException;
import com.ambev.ms_order.mock.MockUtil;
import com.ambev.ms_order.model.dto.OrderDTO;
import com.ambev.ms_order.model.dto.OrderRequestDTO;
import com.ambev.ms_order.service.OrderService;


@WebMvcTest(OrderController.class)
class OrderControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean 
    private OrderService orderService;

	
	@Test
	void calculateOrder_withValidRequest_returnsAccepted() throws Exception {
	    OrderRequestDTO orderRequestDTO = MockUtil.createMockOrderRequestDTO();

	    mockMvc.perform(post("/order/calculate")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(MockUtil.asJsonString(orderRequestDTO)))
	            .andExpect(status().isAccepted())
	            .andExpect(content().string("Order processing initiated."));
	}

	@Test
	void calculateOrder_withInvalidRequest_returnsBadRequest() throws Exception {
	    OrderRequestDTO orderRequestDTO = new OrderRequestDTO(); // Invalid request

	    mockMvc.perform(post("/order/calculate")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(MockUtil.asJsonString(orderRequestDTO)))
	            .andExpect(status().isBadRequest());
	}

	@Test
	void getOrders_returnsOrdersList() throws Exception {
	    List<OrderDTO> orders = List.of(MockUtil.createMockOrderDTO(OrderStatus.RECEIVED));

	    when(orderService.getAllOrders()).thenReturn(orders);

	    mockMvc.perform(get("/order")
	            .contentType(MediaType.APPLICATION_JSON))
	            .andExpect(status().isOk())
	            .andExpect(content().json("[{\"externalId\":\"123\"}]"));
	}

	@Test
	void getOrders_whenNoOrders_returnsEmptyList() throws Exception {
	    when(orderService.getAllOrders()).thenReturn(List.of());

	    mockMvc.perform(get("/order")
	            .contentType(MediaType.APPLICATION_JSON))
	            .andExpect(status().isOk())
	            .andExpect(content().json("[]"));
	}

    @Test
    void getOrderByExternalId_withValidId_returnsOrder() throws Exception {
        String orderExternalId = "validId";
        OrderDTO orderDTO = MockUtil.createMockOrderDTO(OrderStatus.RECEIVED);
        
       when(orderService.getOrderByExternalId(orderExternalId)).thenReturn(orderDTO);

        mockMvc.perform(get("/order/{orderExternalId}", orderExternalId))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"externalId\":\"123\"}"));
    }
    

    @Test
    void getOrderByExternalId_withEmptyId_returnsBadRequest() throws Exception {
        String orderExternalId = " ";

        doThrow(new OrderBadRequestException("Order External ID is Required"))
        	.when(orderService).getOrderByExternalId(orderExternalId);
        
        mockMvc.perform(get("/order/{orderExternalId}", orderExternalId))
                .andExpect(status().isBadRequest());
    }


}