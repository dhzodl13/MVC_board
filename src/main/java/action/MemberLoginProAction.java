package action;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import svc.MemberLoginProService;
import vo.ActionForward;
import vo.MemberDTO;

public class MemberLoginProAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("MemberLoginProAction");
		
		ActionForward forward = null;
		
//		String id = request.getParameter("id");
//		String passwd = request.getParameter("passwd");
		MemberDTO member = new MemberDTO();
		member.setId(request.getParameter("id"));
		member.setPasswd(request.getParameter("passwd"));
		
		MemberLoginProService service = new MemberLoginProService();
		boolean isLoginSuccess = service.loginMember(member);
		
		// 로그인 실패 시 자바스크립트를 통해 "로그인 실패" 출력 후 이전페이지로 돌아가기
		// 아니면, 세션에 로그인 아이디를 "sId" 속성으로 저장 후 메인페이지 포워딩
		if(!isLoginSuccess) {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert('로그인 실패!')");
			out.println("history.back()");
			out.println("</script>");
		} else {
			// request 객체로부터 HttpSession 객체(세션) 가져와서 아이디 저장
			HttpSession session = request.getSession();
			session.setAttribute("sId", member.getId());
			
			// 메인페이지로 포워딩
			forward = new ActionForward();
			forward.setPath("./");
			forward.setRedirect(true);
		}
		
		return forward;
	}

}















