package com.beyt.jdq.jpa.presetation;

import com.beyt.jdq.jpa.BaseTestInstance;
import com.beyt.jdq.jpa.TestApplication;
import com.beyt.jdq.jpa.TestUtil;
import com.beyt.jdq.jpa.testenv.entity.school.Course;
import com.beyt.jdq.jpa.util.PresentationUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(classes = TestApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class S10_Argument_Resolvers extends BaseTestInstance {

    @Autowired
    private MockMvc mockMvc;

    private static final String COURSE_SEARCH_LIST_API_URL = "/test-api/course/as-list";
    private static final String COURSE_CRITERIA_API_URL = "/test-api/course";


    @Test
    public void argumentCriteriaListTests() throws Exception {
//        List.of(course1, course2, course3, course4, course5, course6, course7, course8, course9, course10);
        printRequestedResultAndAssert(COURSE_CRITERIA_API_URL,
                "key0=name&operation0=CONTAIN&values0=Calculus", Course[].class, List.of(course2, course3));
        printRequestedResultAndAssert(COURSE_CRITERIA_API_URL,
                "key0=name&operation0=DOES_NOT_CONTAIN&values0=I", Course[].class, List.of());
        printRequestedResultAndAssert(COURSE_CRITERIA_API_URL,
                "key0=name&operation0=END_WITH&values0=Science", Course[].class, List.of(course1));
        printRequestedResultAndAssert(COURSE_CRITERIA_API_URL,
                "key0=name&operation0=START_WITH&values0=Physics", Course[].class, List.of(course4, course5));
        printRequestedResultAndAssert(COURSE_CRITERIA_API_URL,
                "key0=active&operation0=SPECIFIED&values0=true", Course[].class, List.of(course1, course2, course8, course9, course10));
        printRequestedResultAndAssert(COURSE_CRITERIA_API_URL,
                "key0=active&operation0=SPECIFIED&values0=false", Course[].class, List.of(course3, course4, course5, course6, course7));
        printRequestedResultAndAssert(COURSE_CRITERIA_API_URL,
                "key0=name&operation0=EQUAL&values0=Calculus I", Course[].class, List.of(course2));
        printRequestedResultAndAssert(COURSE_CRITERIA_API_URL,
                "key0=startDate&operation0=EQUAL&values0=2015-06-18", Course[].class, List.of());
        printRequestedResultAndAssert(COURSE_CRITERIA_API_URL,
                "key0=maxStudentCount&operation0=EQUAL&values0=54", Course[].class, List.of(course9));
        printRequestedResultAndAssert(COURSE_CRITERIA_API_URL,
                "key0=name&operation0=NOT_EQUAL&values0=Introduction to Computer Science", Course[].class, List.of(course2, course3, course4, course5, course6, course7, course8, course9, course10));
        printRequestedResultAndAssert(COURSE_CRITERIA_API_URL,
                "key0=id&operation0=GREATER_THAN&values0=5", Course[].class, List.of(course6, course7, course8, course9, course10));
        printRequestedResultAndAssert(COURSE_CRITERIA_API_URL,
                "key0=startDate&operation0=GREATER_THAN&values0=2015-06-18", Course[].class, List.of(course1, course2, course3, course4, course5, course6, course7, course10));
        printRequestedResultAndAssert(COURSE_CRITERIA_API_URL,
                "key0=id&operation0=GREATER_THAN_OR_EQUAL&values0=8", Course[].class, List.of(course8, course9, course10));
        printRequestedResultAndAssert(COURSE_CRITERIA_API_URL,
                "key0=startDate&operation0=GREATER_THAN_OR_EQUAL&values0=2019-06-18", Course[].class, List.of(course5, course6, course7, course10));
        printRequestedResultAndAssert(COURSE_CRITERIA_API_URL,
                "key0=maxStudentCount&operation0=LESS_THAN&values0=40", Course[].class, List.of(course7, course8, course10));
        printRequestedResultAndAssert(COURSE_CRITERIA_API_URL,
                "key0=maxStudentCount&operation0=LESS_THAN_OR_EQUAL&values0=40", Course[].class, List.of(course6, course7, course8, course10));
    }


    @Test
    public void argumentSearchQueryTests() throws Exception {
        printRequestedResultAndAssert(COURSE_SEARCH_LIST_API_URL,
                "select0=id&select1=username&select2=roles.id&select3=roles.name&select4=roles.roleAuthorizations.authorization.id&select5=roles.roleAuthorizations.authorization.name&select6=roles.roleAuthorizations.authorization.menuIcon&" +
                "selectAs0=adminId&selectAs1=adminUsername&selectAs2=roleId&selectAs3=roleName&selectAs4=authorizationId&selectAs5=authorizationName&selectAs6=menuIcon&" +
                        "orderBy0=roles.id&orderByDirection0=desc&" +
                        "page=1&" +
                        "pageSize=2&" +
                        "key0=roles.roleAuthorizations.authorization.menuIcon&operation0=START_WITH&values0=icon", S9_Query_Builder.AuthorizationSummary[].class, List.of(new S9_Query_Builder.AuthorizationSummary(3L, "admin3", 3L, "role3", 3L, "auth3", "icon3"), new S9_Query_Builder.AuthorizationSummary(2L, "admin2", 2L, "role2", 2L, "auth2", "icon2")));
    }

    private <T> void printRequestedResultAndAssert(String apiUrl, String filter, Class<T[]> clazz, Object result) throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(apiUrl + "?" + filter))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        PresentationUtil.prettyPrint(TestUtil.getResultListValue(mvcResult.getResponse().getContentAsString(), clazz));

        assertEquals(result, TestUtil.getResultListValue(mvcResult.getResponse().getContentAsString(), clazz));
    }

}
