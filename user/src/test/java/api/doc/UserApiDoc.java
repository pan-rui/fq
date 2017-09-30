package api.doc;


import api.DocApplicationTests;
import com.alibaba.fastjson.JSON;
import com.hy.core.Constants;
import com.hy.vo.ParamsVo;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.fileUpload;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserApiDoc extends DocApplicationTests {
    private String mediaType = "application/json;charset=UTF-8";
    private String multiPart = "multipart/form-data";
    private String contextPath = "/consumer";
    private String token="ICQmyuMxduw9Cku5X6YeecnzUfFWq0EU";



    @Test
    public void hyUserSms() throws Exception {
        ParamsVo paramsVo = new ParamsVo();
        paramsVo.getParams().addParams("phone", "18098986696").addParams("type","register");
        this.mockMvc.perform(
                post(contextPath+"/user/sms").contextPath(contextPath).contentType(mediaType).content(JSON.toJSONString(paramsVo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(mediaType))
                .andDo(document("hy-user-sms",
                        requestFields(
                                fieldWithPath("params.phone").description("手机号").type(JsonFieldType.STRING),
                                fieldWithPath("params.type").description("验证码类型").type(JsonFieldType.STRING)
                        )
                ));
    }
    @Test
    public void hyUserAdd() throws Exception {
        ParamsVo paramsVo = new ParamsVo();
        paramsVo.getParams().addParams("phone", "18098986696").addParams("smsCode","hy123").addParams("pwd","=32jlf+r349ofsd=fdst").addParams("clientSn","89f34ou8t436");
        this.mockMvc.perform(
                post(contextPath+"/user/add").contextPath(contextPath).contentType(mediaType).content(JSON.toJSONString(paramsVo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(mediaType))
                .andDo(document("hy-user-add",
                        requestFields(
                                fieldWithPath("params.phone").description("手机号").type(JsonFieldType.STRING),
                                fieldWithPath("params.smsCode").description("验证码").type(JsonFieldType.STRING),
                                fieldWithPath("params.clientSn").description("手机序列号").type(JsonFieldType.STRING),
                                fieldWithPath("params.pwd").description("MD5加密后的密文").type(JsonFieldType.STRING)
                        ),
                        responseFields(
                                subsectionWithPath("code").description("状态码"),
                                subsectionWithPath("msg").description("消息")
                        )
                ));
    }

//    @Test
    public void hyUserLogin() throws Exception {
//        hyUserAdd();
        ParamsVo paramsVo = new ParamsVo();
        paramsVo.getParams().addParams("phone", "13500000000").addParams("pwd","=32jlf+r349ofsd=fdst").addParams("clientSn","89f34ou8t43600000000");
       String result=this.mockMvc.perform(
                post(contextPath+"/user/login").contextPath(contextPath).contentType(mediaType).content(JSON.toJSONString(paramsVo)))
                .andExpect(status().isOk())
//                .andExpect(content().contentType(mediaType))
//                .andExpect(jsonPath("code").value(0))
                .andDo(document("hy-user-login",
                        requestFields(
                                fieldWithPath("params.phone").description("手机号").type(JsonFieldType.STRING),
                                fieldWithPath("params.clientSn").description("手机序列号").type(JsonFieldType.STRING),
                                fieldWithPath("params.pwd").description("MD5加密后的密文").type(JsonFieldType.STRING)
                        ),
                        responseFields(
                                subsectionWithPath("code").description("状态码"),
                                subsectionWithPath("data.token").description("访问停牌"),
                                subsectionWithPath("data.userInfo").description("用户信息")
                        )
                )).andReturn().getResponse().getContentAsString();
//       JSONObject jsonObject=JSON.parseObject(result);
//        token = (String) ((Map<String, Object>) jsonObject.get("data")).get("token");
    }

    @Test
    public void hyCompanyList() throws Exception {
        HttpHeaders headers = new HttpHeaders();headers.add(Constants.USER_PHONE,"18098986696");headers.add(Constants.CLIENT_SN,"89f34ou8t436");headers.add(Constants.USER_ID,"1035");
        headers.add(Constants.USER_TOKEN,token);
        this.mockMvc.perform(
                get(contextPath+"/company").contextPath(contextPath).contentType(mediaType).headers(headers))
                .andExpect(status().isOk())
//                .andExpect(content().contentType(mediaType))
//                .andExpect(jsonPath("code").value(0))
//                .andExpect(content().string(StringContains.containsString("\"code\":0")))
                .andDo(document("hy-company-list",
                        responseFields(
                                subsectionWithPath("code").description("状态码"),
                                subsectionWithPath("data").description("企业列表").type(JsonFieldType.ARRAY),
                                subsectionWithPath("data[0].companyName").description("企业名称")
                        )
                ));
    }

    @Test
    public void hyUserCertS1() throws Exception {
        ParamsVo paramsVo = new ParamsVo();
        paramsVo.getParams().addParams("phone", "18098986696").addParams("name", "张三").addParams("cardNo", "42397543524352343").addParams("smsCode", "hy123").addParams("companyId", 435).addParams("bizerId", 234);
        HttpHeaders headers = new HttpHeaders();headers.add("hyUP","18098986696");headers.add("hyCS","89f34ou8t436");headers.add("hyUI","1035");
        headers.add("hyUT",token);
        this.mockMvc.perform(
                post(contextPath+"/user/certS1").contextPath(contextPath).contentType(mediaType).headers(headers).content(JSON.toJSONString(paramsVo)))
                .andExpect(status().isOk())
//                .andExpect(content().contentType(mediaType))
//                .andExpect(jsonPath("code").value(0))
//                .andExpect(content().string(StringContains.containsString("\"code\":0")))
                .andDo(document("hy-user-certS1",
                        requestFields(
                                fieldWithPath("params.name").description("姓名").type(JsonFieldType.STRING),
                                fieldWithPath("params.cardNo").description("身份证号").type(JsonFieldType.STRING),
                                fieldWithPath("params.phone").description("手机号").type(JsonFieldType.STRING),
                                fieldWithPath("params.smsCode").description("验证码").type(JsonFieldType.STRING),
                                fieldWithPath("params.companyId").description("所属企业ID").type(JsonFieldType.NUMBER),
                                fieldWithPath("params.bizerId").description("销售员ID").type(JsonFieldType.NUMBER)
                        ),
                        responseFields(
                                subsectionWithPath("code").description("状态码"),
                                subsectionWithPath("msg").description("信息")
                        )
                ));
    }
    @Test
    public void hyUserCertS2() throws Exception {
        HttpHeaders headers = new HttpHeaders();headers.add("hyUP","18098986696");headers.add("hyCS","89f34ou8t436");headers.add("hyUI","1035");
        headers.add("hyUT",token);
        Map<String,String> multiValueMap=new HashMap<>();
        multiValueMap.put("cardUp", "fd");
        multiValueMap.put("cardDown", "fd");
        this.mockMvc.perform(
                fileUpload(contextPath+"/user/certS2").file(new MockMultipartFile("cardUp","0C4B65D2-6B19-4F3D-A6C0-8EAD8C40F0DF.jpg","image/jpeg",new FileInputStream(new File("D:\\0C4B65D2-6B19-4F3D-A6C0-8EAD8C40F0DF.jpg"))))
                        .file(new MockMultipartFile("cardDown","00E81345-81BF-454F-8339-49B6E82E302D.jpg","image/jpeg",new FileInputStream(new File("D:\\00E81345-81BF-454F-8339-49B6E82E302D.jpg")))).content(JSON.toJSONString(multiValueMap))
                .contextPath(contextPath).contentType(multiPart).headers(headers))
                .andExpect(status().isOk())
//                .andExpect(content().contentType(mediaType))
//                .andExpect(jsonPath("code").value(0))
//                .andExpect(content().string(StringContains.containsString("\"code\":0")))
                .andDo(document("hy-user-certS2",
                        requestFields(
                                fieldWithPath("cardUp").description("身份证正面").type("File"),
                                fieldWithPath("cardDown").description("身份证反面").type("FIle")
                        ),
                        responseFields(
                                subsectionWithPath("code").description("状态码"),
                                subsectionWithPath("msg").description("返回消息"),
                                subsectionWithPath("data.cardUp").description("正面路径"),
                                subsectionWithPath("data.cardDown").description("反面路径")
                        )
                ));
    }

    @Test
    public void hyUserCertS3() throws Exception {
        ParamsVo paramsVo = new ParamsVo();
        paramsVo.getParams().addParams("userName", "张三").addParams("bankCardNo", "42397543524352343");
        HttpHeaders headers = new HttpHeaders();headers.add("hyUP","18098986696");headers.add("hyCS","89f34ou8t436");headers.add("hyUI","1035");
        headers.add("hyUT",token);
        this.mockMvc.perform(
                post(contextPath+"/user/certS3").contextPath(contextPath).contentType(mediaType).headers(headers).content(JSON.toJSONString(paramsVo)))
                .andExpect(status().isOk())
//                .andExpect(content().contentType(mediaType))
//                .andExpect(jsonPath("code").value(0))
//                .andExpect(content().string(StringContains.containsString("\"code\":0")))
                .andDo(document("hy-user-certS3",
                        requestFields(
                                fieldWithPath("params.userName").description("持卡人").type(JsonFieldType.STRING),
                                fieldWithPath("params.bankCardNo").description("银行卡号").type(JsonFieldType.STRING)
                        ),
                        responseFields(
                                subsectionWithPath("code").description("状态码"),
                                subsectionWithPath("msg").description("信息")
                        )
                ));
    }

    @Test
    public void hyUserCertS4() throws Exception {
        HttpHeaders headers = new HttpHeaders();headers.add("hyUP","18098986696");headers.add("hyCS","89f34ou8t436");headers.add("hyUI","1035");
        headers.add("hyUT",token);
        this.mockMvc.perform(
                post(contextPath+"/user/certS4").contextPath(contextPath).contentType(mediaType).headers(headers))
                .andExpect(status().isOk())
//                .andExpect(content().contentType(mediaType))
//                .andExpect(jsonPath("code").value(0))
//                .andExpect(content().string(StringContains.containsString("\"code\":0")))
                .andDo(document("hy-user-certS4",
                        responseFields(
                                subsectionWithPath("code").description("状态码"),
                                subsectionWithPath("msg").description("信息")
                        )
                ));
    }

//    @Test
/*    public void restfulUserList() throws Exception {
        empty();
        add(3);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("page", "0");
        params.add("size", "2");
        this.mockMvc.perform(
                get("/restful/user").params(params))
                .andExpect(status().isOk())
.andDo(document("restful-user-list",
        requestParameters(
                parameterWithName("page").description("分页页码"),
                parameterWithName("size").description("分页页长")
        ),
        relaxedLinks(
                linkWithRel("self").description("The <<resources-restful-user-index,user resource>>"),
                linkWithRel("first").description("第一页"),
                linkWithRel("next").description("下一页"),
                linkWithRel("last").description("最后一页"),
                linkWithRel("profile").description("The ALPS profile for the service")),
        responseFields(
                subsectionWithPath("_links").description("<<resources-restful-user-index,Links>> user resources"),
                subsectionWithPath("_embedded.user").description("用户列表").type("User对象数组"),
                subsectionWithPath("page").description("分页信息").type("Object"))));
    }*/

/*    @Test
    public void restfulUserAdd() throws Exception {

        Map<String, Object> user = new HashMap<>();
        user.put("name", "testAdd");
        user.put("sex", 1);

        this.mockMvc.perform(
                post("/restful/user").contentType(mediaType).content(
                        this.objectMapper.writeValueAsString(user))).andExpect(
                status().isCreated())
                .andDo(document("restful-user-add",
                        requestFields(
                                fieldWithPath("name").description("用户姓名"),
                                fieldWithPath("sex").description("用户性别，0=女，1=男"))
                ));
    }*/

/*    @Test
    public void restfulUserUpdate() throws Exception {

        Map<String, Object> user = new HashMap<>();
        user.put("name", "testUpdate");
        user.put("sex", 1);

        String userLocation = this.mockMvc
                .perform(
                        post("/restful/user").contentType(mediaType).content(
                                this.objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated()).andReturn().getResponse()
                .getHeader("Location");

        Map<String, Object> userUpdate = new HashMap<>();
        userUpdate.put("name", "testUpdateComplete");

        this.mockMvc.perform(
                patch(userLocation).contentType(mediaType).content(
                        this.objectMapper.writeValueAsString(userUpdate)))
                .andExpect(status().isNoContent())
                .andDo(document("restful-user-update",
                        requestFields(
                                fieldWithPath("name").description("用户姓名").type(JsonFieldType.STRING).optional(),
                                fieldWithPath("sex").description("用户性别").type(JsonFieldType.STRING).optional())));
    }*/

/*    @Test
    public void restfulUserFindBySex() throws Exception {
        empty();
        add(100);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("sex", "1");
        params.add("page", "0");
        params.add("size", "2");

        this.mockMvc.perform(
                get("/restful/user/search/sex").params(params))
                .andExpect(status().isOk())
                .andDo(document("restful-user-find-sex",
                        requestParameters(
                                parameterWithName("sex").description("用户性别"),
                                parameterWithName("page").description("分页页码"),
                                parameterWithName("size").description("分页页长")
                        )));
    }*/

/*    @Test
    public void restfulUserFindByNameLike() throws Exception {
        empty();
        add(200);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("name", "%15%");
        params.add("page", "0");
        params.add("size", "2");

        this.mockMvc.perform(
                get("/restful/user/search/name").params(params))
                .andExpect(status().isOk())
                .andDo(document("restful-user-find-name",
                        requestParameters(
                                parameterWithName("name").description("用户姓名，如果需要模糊匹配，请在参数前后按需添加占位符如：%key%"),
                                parameterWithName("page").description("分页页码"),
                                parameterWithName("size").description("分页页长")
                        )));
    }*/

/*    @Test
    public void restfulUserDelete() throws Exception {
        empty();
        Map<String, Object> user = new HashMap<>();
        user.put("name", "testDelete");
        user.put("sex", 1);

        long count = userRepo.count();

        String userLocation = this.mockMvc
                .perform(
                        post("/restful/user").contentType(mediaType).content(
                                this.objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated()).andReturn().getResponse()
                .getHeader("Location");

        Assert.assertEquals(count + 1, userRepo.count());

        this.mockMvc.perform(
                delete(userLocation))
                .andExpect(status().isNoContent())
                .andDo(document("restful-user-delete"));

        Assert.assertEquals(count, userRepo.count());
    }*/


}
