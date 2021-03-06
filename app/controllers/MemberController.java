package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Member;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Authentication;
import utils.OJException;

public class MemberController extends Controller {
    public static Result registerPage() {
        return ok(views.html.member.register.render());
    }

    public static Result loginPage() {
        return ok(views.html.member.login.render());
    }

    public static Result register() {
        ObjectNode out = Json.newObject();
        DynamicForm in = Form.form().bindFromRequest();
        String username = in.get("username").trim();
        try {
            Member member = new Member();
            member.setUsername(username);
            try {
                member.setEmail(in.get("email"));
                member.setPassword(in.get("password"));
                member.gender = in.get("gender").equals("1");
            } catch (NullPointerException ne) {
                throw new OJException(1005, "information ,not complete.");
            }
            member.save();
            out.put("error", 0);
            out.put("message", "member is registered.");
        } catch (OJException e) {
            out.put("error", e.getCode());
            out.put("message", e.getMessage());
        }
        return ok(out);
    }

    public static Result login() {
        ObjectNode out = Json.newObject();
        DynamicForm in = Form.form().bindFromRequest();
        try {
            Member member = Member.find.where("username = '" + in.get("username") + "'").findUnique();
            if (member == null) {
                throw new OJException(1003, "username not registered");
            }
            if (!member.verifyPassword(in.get("password"))) {
                throw new OJException(1004, "password incorrect");
            }
            session("mid", "" + member.id);
            out.put("error", 0);
            out.put("message", "logged in success");
        } catch (OJException e) {
            out.put("error", e.getCode());
            out.put("message", e.getMessage());
        }
        return ok(out);
    }

    public static Result logout() {
        session().remove("mid");
        return redirect("/");
    }

    @Authentication
    public static Result status() {
        ObjectNode out = Json.newObject();
        out.put("error", 0);
        out.put("member", Json.toJson(ctx().args.get("member")));
        return ok(out);
    }

    @Authentication(json = false)
    public static Result profilePage() {
        return ok(views.html.member.profile.render());
    }
}
