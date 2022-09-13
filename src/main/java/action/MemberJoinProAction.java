package action;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import svc.MemberJoinProService;
import vo.ActionForward;
import vo.MemberDTO;

public class MemberJoinProAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("MemberJoinProAction");
		
		ActionForward forward = null;
		
		MemberDTO member = new MemberDTO();
		member.setName(request.getParameter("name"));
		member.setId(request.getParameter("id"));
		member.setPasswd(request.getParameter("passwd"));
		member.setEmail(request.getParameter("email1") + "@" + request.getParameter("email2"));
		member.setPost_code(request.getParameter("post_code"));
		member.setAddress1(request.getParameter("address1"));
		member.setAddress2(request.getParameter("address2"));
//		System.out.println(member);
		
		// MemberJoinProService - joinMember() 메서드 호출
		// => 파라미터 : MemberDTO 객체   리턴타입 : boolean(isJoinSuccess)
		MemberJoinProService service = new MemberJoinProService();
		boolean isJoinSuccess = service.joinMember(member);
		
		// isJoinSuccess 가 false 일 경우 자바스크립트를 통해 "가입 실패" 출력 후 이전페이지
		// 아니면, ActionForward 객체를 통해 메인페이지로 포워딩
		if(!isJoinSuccess) {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert('회원 가입 실패!')");
			out.println("history.back()");
			out.println("</script>");
		} else {
			forward = new ActionForward();
			forward.setPath("./"); // 메인페이지로 포워딩
			forward.setRedirect(true);
		}
		
		return forward;
	}

}











