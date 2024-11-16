package com.heartsave.todaktodak_api.diary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heartsave.todaktodak_api.diary.service.DiaryService;
import com.heartsave.todaktodak_api.diary.service.MySharedDiaryService;
import com.heartsave.todaktodak_api.diary.service.PublicDiaryService;
import com.heartsave.todaktodak_api.member.controller.MemberController;
import com.heartsave.todaktodak_api.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(
    controllers = {
      DiaryController.class,
      MySharedDiaryController.class,
      PublicDiaryController.class,
      MemberController.class
    })
@AutoConfigureMockMvc(addFilters = false)
public class BaseControllerTest {
  @Autowired protected MockMvc mockMvc;
  @Autowired protected ObjectMapper objectMapper;
  @Autowired WebApplicationContext context;

  @MockBean protected DiaryService diaryService;
  @MockBean protected MySharedDiaryService mockMySharedDiaryService;
  @MockBean protected PublicDiaryService publicDiaryService;
  @MockBean protected MemberService memberService;

  //  @PostConstruct
  //  public void setup() {
  //    mockMvc =
  //        MockMvcBuilders.webAppContextSetup(context)
  //            .defaultRequest(
  //                patch("/**").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
  //            .alwaysDo(print())
  //            .build();
  //  }

  //  @Mock
  //  public TodakUser mockUser;
}
