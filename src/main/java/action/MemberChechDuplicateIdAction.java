package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import svc.MemberCheckDuplicateIdService;
import vo.ActionForward;

public class MemberChechDuplicateIdAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("MemberChechDuplicateIdAction");
		
		ActionForward forward = null;
		
		String id = request.getParameter("id");

		// MemberCheckDuplicateIdService 의 checkDuplicateId() 메서드 호출하여 아이디 검색 요청
		// => 파라미터 : 아이디(id)   리턴타입 : boolean(isDuplicate)
		MemberCheckDuplicateIdService service = new MemberCheckDuplicateIdService();
		boolean isDuplicate = service.checkDuplicateId(id);
		
		// ActionForward 객체를 사용하여 CheckDuplicateId.me 서블릿 주소 요청
		// => 파라미터로 아이디와 검색결과 전달
		forward = new ActionForward();
		forward.setPath("MemberCheckIdForm.me?id=" + id + "&isDuplicate=" + isDuplicate);
		forward.setRedirect(true);
		
		return forward;
	}

}










