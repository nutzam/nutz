package org.nutz.mvc.testapp.classes.action.adaptor;

import java.io.InputStream;
import java.io.Reader;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.nutz.ioc.annotation.InjectName;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.ViewModel;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.adaptor.PairAdaptor;
import org.nutz.mvc.adaptor.meta.Pet;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.impl.AdaptorErrorContext;
import org.nutz.mvc.testapp.BaseWebappTest;
import org.nutz.mvc.testapp.classes.bean.Issue1069;
import org.nutz.mvc.testapp.classes.bean.Issue1109;
import org.nutz.mvc.testapp.classes.bean.Issue1277;

import jakarta.servlet.http.HttpServletRequest;
import junit.framework.TestCase;

@InjectName
@IocBean
@At("/adaptor")
@Ok("raw")
@Fail("http:500")
public class AdaptorTestModule extends BaseWebappTest {

    @At("/github/issue/543")
    public long issue_543(@Param(value = "d", dfmt = "yyyyMMdd") Date d,
                          @Param("..") Issue543 o) {
        if (d.getTime() != o.d.getTime()) {
            throw Lang.impossible();
        }
        // new Throwable().printStackTrace();
        System.out.println("Hi, hotcode replace");
        return d.getTime();
    }

    /*
     * Githut : #352
     */
    @At("/reader")
    public String getInputStream(Reader reader) {
        return Streams.readAndClose(reader);
    }

    /*
     * Githut : #352
     */
    @At("/ins")
    public String getInputStream(InputStream ins) {
        return new String(Streams.readBytesAndClose(ins));
    }

    @At("/json/pet/array")
    @AdaptBy(type = JsonAdaptor.class)
    public String getJsonPetArray(@Param("pets") Pet[] pets) {
        return String.format("pets(%d) %s", pets.length, "array");
    }

    @At("/json/pet/list")
    @AdaptBy(type = JsonAdaptor.class)
    public String getJsonPetList(@Param("pets") List<Pet> lst) {
        StringBuilder sb = new StringBuilder();
        for (Pet pet : lst) {
            sb.append(',').append(pet.getName());
        }
        return String.format("pets(%d) %s", lst.size(), "list");
    }

    // 传入的id,会是一个非法的字符串!!
    @At({"/err/param", "/err/param/?"})
    public void errParam(@Param("id") long id, AdaptorErrorContext errCtx) {
        TestCase.assertNotNull(errCtx);
        TestCase.assertNotNull(errCtx.getErrors()[0]);
    }

    // 传入的id,会是一个非法的字符串!!
    @At({"/err/param/anywhere", "/err/param/anywhere/?"})
    public void errParamAnyWhere(AdaptorErrorContext errCtx, @Param("id") long id) {
        TestCase.assertNotNull(errCtx);
        TestCase.assertNotNull(errCtx.getErrors()[1]);
    }

    // 传入的id,会是一个非法的字符串!!
    @At({"/err/param/pathargs/?", "/err/param/pathargs/?/?"})
    public void errParamWithPathArgs(AdaptorErrorContext errCtx, String a, @Param("id") long id) {
        TestCase.assertNotNull(errCtx);
        TestCase.assertNotNull(errCtx.getErrors()[2]);
        TestCase.assertNotNull(a);
    }

    // 传入的id,会是一个非法的字符串!!
    @At({"/multi/err/ctxs/?", "/multi/err/ctxs/?/?"})
    public void multiErrCtxs(AdaptorErrorContext errCtx, String a, AdaptorErrorContext errCtx2, @Param("id") long id, AdaptorErrorContext errCtx3) {
        TestCase.assertNotNull(errCtx);
        TestCase.assertNotNull(errCtx.getErrors()[3]);
        TestCase.assertNotNull(a);
        TestCase.assertNull(errCtx2);
        TestCase.assertNull(errCtx3);
    }

    // 传入的id,会是一个非法的字符串!!
    @At({"/multi/err/ctxs2/?/?", "/multi/err/ctxs2/?/?/?"})
    public void multiErrCtxs(String a, AdaptorErrorContext errCtx, AdaptorErrorContext errCtx2, String b, @Param("id") long id, AdaptorErrorContext errCtx3) {
        TestCase.assertNotNull(errCtx);
        TestCase.assertNotNull(errCtx.getErrors()[4]);
        TestCase.assertNotNull(a);
        TestCase.assertNotNull(b);
        TestCase.assertNull(errCtx2);
        TestCase.assertNull(errCtx3);
    }

    @At("/json/type")
    @AdaptBy(type = JsonAdaptor.class)
    public void jsonMapType(Map<String, Double> map) {
        TestCase.assertNotNull(map);
        TestCase.assertEquals(1, map.size());
        TestCase.assertEquals(123456.0, map.get("abc").doubleValue());
        System.out.println(map.get("abc"));
    }

    @At("/default_value")
    @Ok("raw")
    public int default_value(@Param(value = "abc", df = "123456") int value) {
        return value;
    }

    @POST
    @AdaptBy(type = JsonAdaptor.class)
    @At("/err_ctx")
    @Ok("raw")
    public boolean err_ctx(@Param("..") Object obj, AdaptorErrorContext ctx) {
        return ctx == null;
    }

    // @GET
    @At("/sqldate")
    public String test_sql_date(@Param("checkDate") java.sql.Date checkDate) {
        return checkDate.toString();
    }

    @At("/param_without_param")
    @Ok("json:compact")
    public Object test_param_without_param(String uid, String[] uids, HttpServletRequest req) {
        return uids;
    }

    @At("/object_without_param")
    @Ok("json:compact")
    public Object test_object_without_param(Pet pet, HttpServletRequest req) {
        return pet;
    }

    @At("/path_args_and_object_without_param/?")
    @Ok("json:compact")
    public Object test_path_args_and_object_without_param(String name, Pet pet, HttpServletRequest req) {
        pet.setName(name);
        return pet;
    }

    @At("/issue1069")
    @Ok("raw")
    public Object test_issue1069(@Param("..") Issue1069 issue1069) {
        return issue1069.getShowAdd();
    }

    @At("/issue1109")
    @Ok("json")
    @AdaptBy(type = PairAdaptor.class)
    public Object issue1109(@Param("::issue") List<Issue1109> pojos) {
        return pojos;
    }

    @At("/issue1267")
    @Ok("raw")
    @AdaptBy(type = PairAdaptor.class)
    public long issue1267(@Param("..") Issue1267 issue) {
        return issue.getTime().getTime();
    }

    @At("/issue1277")
    @Ok("json")
    @AdaptBy(type = PairAdaptor.class)
    public Object issue1277(@Param("..") Issue1277 issue) {
        return issue;
    }

    @At("/issue1310")
    @Ok("json")
    @AdaptBy(type = PairAdaptor.class)
    public Object issue1310(@Param("::") Issue1277 issue) {
        return issue;
    }

    @At("/issue13xx")
    @Ok("re")
    public String re_view_with_NutMap(@Param("..") NutMap map, ViewModel viewModel) {
        viewModel.put("id", 1); // 如果正确, 应该会输出 {id:1}
        map.put("id", 2); // 如果走了NutMap的话,应该输出 {id:2}
        return "json";
    }

    @Ok("raw")
    @At("/jdk8/localdt")
    public String localdatetime(@Param("date") LocalDateTime localDateTime) {
        System.out.println(localDateTime);
        return localDateTime.toString();
    }
}
