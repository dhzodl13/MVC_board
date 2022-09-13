package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import svc.BoardDetailService;
import vo.ActionForward;
import vo.BoardDTO;

public class BoardModifyFormAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("BoardModifyFormAction");
		
		ActionForward forward = null;
		
		// 글 수정에 필요한 글번호 파라미터 가져오기
		int board_num = Integer.parseInt(request.getParameter("board_num"));
		
		// 글 수정에 필요한 게시물 상세 정보 조회를 위해
		// 기존에 정의된 BoardDetailService 클래스의 getBoard() 메서드를 호출하여
		// 게시물 상세 정보를 리턴받아 qna_board_modify.jsp 페이지로 포워딩
		// => 단, 조회수 증가 작업은 수행하지 않음
		BoardDetailService service = new BoardDetailService();
		BoardDTO board = service.getBoard(board_num);
		
		// 리턴받은 게시물 정보(BoardDTO 객체)를 request 객체에 저장
		request.setAttribute("board", board);
		
		// board 디렉토리의 qna_board_modify.jsp 페이지로 포워딩
		forward = new ActionForward();
		forward.setPath("board/qna_board_modify.jsp");
		forward.setRedirect(false);
		
		return forward;
	}

}











