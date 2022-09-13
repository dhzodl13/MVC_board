package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import svc.BoardDetailService;
import vo.ActionForward;
import vo.BoardDTO;

public class BoardDetailAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("BoardDetailAction");
		
		ActionForward forward = null;
		
		// request 객체를 통해 전달받은 파라미터(board_num) 가져오기
		int board_num = Integer.parseInt(request.getParameter("board_num"));
		
		// BoardDetailService 인스턴스 생성 후 increaseReadcount() 메서드 호출하여 조회수 증가 요청
		// => 파라미터 : 글번호(board_num)   리턴타입 : void
		BoardDetailService service = new BoardDetailService();
		service.increaseReadcount(board_num);
		
		// getBoard() 메서드 호출하여 글 상세정보 조회 요청
		// => 파라미터 : 글번호(board_num)   리턴타입 : BoardDTO(board)
		BoardDTO board = service.getBoard(board_num);
		
		// 조회 결과(1개 게시물 정보 = BoardDTO 객체)를 request 객체에 저장
		request.setAttribute("board", board);
		
		// ActionForward 객체를 활용하여 board 디렉토리의 qna_board_view.jsp 페이지 포워딩 설정
		// => Dispatcher 방식
		forward = new ActionForward();
		forward.setPath("board/qna_board_view.jsp");
		forward.setRedirect(false);
		
		return forward;
	}

}











