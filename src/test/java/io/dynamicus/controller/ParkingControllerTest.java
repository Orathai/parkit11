package io.dynamicus.controller;

import io.dynamicus.config.OpenIdConnectFilter;
import io.dynamicus.model.Price;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ParkingControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Mock
    private OpenIdConnectFilter filter;

    @Autowired
    private ParkingController controller;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity(filter))
                .build();
    }

    @Test
    public void getPriceByZone_200() throws Exception {

        ResponseEntity<Price> price = controller.getPriceByZone(30, "M2");
        assertEquals(100.0, price.getBody().price, 0);
    }


    @Test
    public void getPriceByZone_400() throws Exception {

        ResponseEntity<Price> price = controller.getPriceByZone(30, "m2");
        assertEquals(HttpStatus.BAD_REQUEST, price.getStatusCode());
    }
}