package io.dynamicus.springrestdocs;

import io.dynamicus.config.OpenIdConnectFilter;
import io.dynamicus.controller.IndexController;
import io.dynamicus.model.OpenIdConnectUserDetails;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(IndexController.class)
@AutoConfigureRestDocs(outputDir = "target/generated-snippets", uriPort = 9000)
public class IndexControllerTest {

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Mock
    private OpenIdConnectUserDetails userDetails;

    @Mock
    private OpenIdConnectFilter filter;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity(filter))
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    public void index() throws Exception {

        this.mockMvc.perform(get("/index"))
                .andDo(print())
                .andExpect(status().isOk())
                ;
    }

    @Test
    public void getUser() throws Exception {

        Mockito.when(userDetails.getName()).thenReturn("test user");

        this.mockMvc.perform(get("/user"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user/{method-name}"))
        ;
    }
}