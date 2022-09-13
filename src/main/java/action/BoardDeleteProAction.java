package action;

import java.io.File;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import svc.BoardDeleteProService;
import vo.ActionForward;

public class BoardDeleteProAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("BoardDeleteProAction");
		ActionForward forward = null;
		
		// 전달받은 파라미터 가져오기(페이지번호 제외)
		int board_num = Integer.parseInt(request.getParameter("board_num"));
		String board_pass = request.getParameter("board_pass");
		
		// BoardDeleteProService - isBoardWriter() 메서드를 호출하여 삭제 권한 판별 요청
		// => 파라미터 : 글번호, 패스워드    리턴타입 : boolean(isBoardWriter)
		BoardDeleteProService service = new BoardDeleteProService();
		boolean isBoardWriter = service.isBoardWriter(board_num, board_pass);
		
		// 삭제 권한 판별 결과에 따른 작업 수행
		// 패스워드가 일치하지 않을 경우(= 권한 없을 경우)
		// 자바스크립트를 사용하여 "삭제 권한이 없습니다!" 출력 후 이전페이지로 돌아가기
		if(!isBoardWriter) {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert('삭제 권한이 없습니다!')");
			out.println("history.back()");
			out.println("</script>");
		} else { // 아니면(패스워드 일치할 경우 = 권한 있을 경우)
			// -------------------- 파일 삭제 기능 추가 - 1 ---------------------
			// 게시물 삭제 전 삭제할 대상 게시물의 실제 업로드 된 파일명 리턴받기
			// Service 객체의 getRealFile() 메서드 호출
			// => 파라미터 : 글번호   리턴타입 : String
			String realFile = service.getRealFile(board_num);
			
			// 삭제할 대상 파일의 경로 알아내기
			String realPath = request.getServletContext().getRealPath("/upload");
			// ------------------------------------------------------------------
			
			// BoardDeleteProService - removeBoard() 메서드를 호출하여 삭제 요청
			// => 파라미터 : 글번호    리턴타입 : boolean(isDeleteSuccess)
			// (BoardDAO - deleteBoard())
			boolean isDeleteSuccess = service.removeBoard(board_num);
			
			// 삭제 결과 판별
			// 삭제 실패 시 자바스크립트로 "삭제 실패!" 출력 후 이전페이지로 돌아가기
			if(!isDeleteSuccess) {
				response.setContentType("text/html; charset=UTF-8");
				PrintWriter out = response.getWriter();
				out.println("<script>");
				out.println("alert('삭제 실패!')");
				out.println("history.back()");
				out.println("</script>");
			} else { // 게시물 삭제 성공 시
				// -------------------- 파일 삭제 기능 추가 - 2 ---------------------
				// 파일 삭제 기능 추가-1 에서 생성한 파일명과 경로를 사용하여 File 객체 생성
				File f = new File(realPath, realFile);
				// 해당 파일이 존재할 경우 삭제 작업 수행
				if(f.exists()) { // 파일 존재 여부 판별
					f.delete(); // 파일 삭제
				}
				// ------------------------------------------------------------------
				// 글목록(BoardList.bo) 페이지 요청 => 페이지번호 전달
				forward = new ActionForward();
				forward.setPath("BoardList.bo?pageNum=" + request.getParameter("pageNum"));
				forward.setRedirect(true);
			}
		}
		
		return forward;
	}

}









