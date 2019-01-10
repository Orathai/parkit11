package io.dynamicus;

import io.dynamicus.controller.ParkingController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ParkingController.class)
@AutoConfigureRestDocs(outputDir = "target/generated-snippets", uriPort = 9000)
public class ParkingRestDocsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParkingController controller;

    @Test
    public void getPriceByZone() throws Exception {

        this.mockMvc.perform(get("/api/prices?minutes=50&zone=M1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("prices/{method-name}", requestParameters(
                        parameterWithName("minutes").description("Number of minutes"),
                        parameterWithName("zone").description("e.g. M1 M2 M3")
                )));
    }
}
