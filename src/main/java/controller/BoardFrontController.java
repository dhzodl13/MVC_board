package controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import action.Action;
import action.BoardDeleteProAction;
import action.BoardDetailAction;
import action.BoardListAction;
import action.BoardModifyFormAction;
import action.BoardModifyProAction;
import action.BoardReplyFormAction;
import action.BoardReplyProAction;
import action.BoardWriteProAction;
import vo.ActionForward;

// 서블릿 주소가 xxx.bo 로 끝날 경우 BoardFrontController 클래스로 해당 요청이 전달됨
@WebServlet("*.bo")
public class BoardFrontController extends HttpServlet {
	// GET 방식 or POST 방식에 따른 처리를 별도로 구분하지 않고 공통으로 처리하기 위해
	// doProcess() 메서드를 정의하고 doGet(), doPost() 메서드에서 호출
	protected void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("BoardFrontController");
		
		// POST 방식 요청에 대한 한글 처리
		request.setCharacterEncoding("UTF-8");
		
		// 서블릿 주소 추출
		String command = request.getServletPath();
		System.out.println("command : " + command);
		
		// Action 클래스 인스턴스들을 공통으로 관리하는 Action 타입 변수 선언
		Action action = null;
		// 포워딩 정보를 관리하는 ActionForward 타입 변수 선언
		ActionForward forward = null;	
		
		// 추출된 서블릿 주소를 if문을 사용하여 판별하고 각 주소에 따른 액션(작업) 요청
		// ex) "/BoardWriteForm.bo" 일 경우 board 폴더 내의 qna_board_write.jsp 페이지로 이동
		if(command.equals("/BoardWriteForm.bo")) {
			// 글쓰기 폼 표시를 위한 View 페이지(*.jsp) 로 포워딩
			// 별도의 비즈니스 로직(= DB 작업)이 없이 뷰페이지로 바로 연결
			// => 이 때, JSP 페이지의 URL(qna_board_write.jsp)이 주소표시줄에 노출되지 않고
			//    이전의 요청 주소인 서블릿 주소("/BoardWriteForm.bo")를 그대로 유지해야하므로
			//    Dispatcher 방식으로 포워딩을 수행해야한다!
			// => 파라미터로 현재 위치(= Root)에서 하위 디렉토리의 qna_board_write.jsp 페이지 지정
//			RequestDispatcher dispatcher = request.getRequestDispatcher("board/qna_board_write.jsp");
//			dispatcher.forward(request, response);
			
			// 포워딩 정보를 관리하는 ActionForward 객체 생성 후 URL 및 포워딩 방식을 저장
			forward = new ActionForward();
			forward.setPath("board/qna_board_write.jsp");
			forward.setRedirect(false); // Dispatcher 방식(생략 가능)
		} else if(command.equals("/BoardWritePro.bo")) {
//			System.out.println("글쓰기 비즈니스 로직!");
			// 글쓰기 작업 완료했다고 가정
			// => 글목록 표시를 위한 BoardList.bo 서블릿 주소 요청하여 포워딩
			// => 새로운 요청에 의한 서블릿 주소를 변경해야하므로 Redirect 방식으로 포워딩
//			response.sendRedirect("BoardList.bo");
			
			// 포워딩 정보를 관리하는 ActionForward 객체 생성 후 URL 및 포워딩 방식을 저장
//			forward = new ActionForward();
//			forward.setPath("BoardList.bo");
//			forward.setRedirect(true); // Redirect 방식
			// --------------------------------------------------------------------------
			// 글쓰기 비즈니스 로직 수행을 위한 컨트롤러인 Action 클래스로 이동하여
			// Service -> DAO 클래스를 거쳐 비즈니스 로직을 수행한 후
			// 최종적으로 Action 클래스에서 포워딩 정보를 저장한 후 ActionForward 객체를 리턴

			try {
				// BoardWriteProAction 클래스 인스턴스 생성 후 execute() 메서드 호출
				action = new BoardWriteProAction();
				forward = action.execute(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if(command.equals("/BoardList.bo")) {
			// 글목록 조회 비즈니스 로직 수행을 위한 컨트롤러인 Action 클래스로 이동하여
			// Service -> DAO 클래스 비즈니스 로직을 수행한 후
			// 최종적으로 Action 클래스에서 포워딩 정보를 저장한 후 ActionForward 객체를 리턴
			try {
				// BoardListAction 클래스 인스턴스 생성 후 execute() 메서드 호출
				action = new BoardListAction();
				forward = action.execute(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if(command.equals("/BoardDetail.bo")) {
			try {
				action = new BoardDetailAction();
				forward = action.execute(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if(command.equals("/BoardDeleteForm.bo")) {
			forward = new ActionForward();
			forward.setPath("board/qna_board_delete.jsp");
			forward.setRedirect(false); // Dispatcher 방식(생략 가능)
		} else if(command.equals("/BoardDeletePro.bo")) {
			action = new BoardDeleteProAction();
			
			try {
				forward = action.execute(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if(command.equals("/BoardModifyForm.bo")) {
			// 글 수정에 필요한 게시물 조회 비즈니스 로직 처리를 위해
			// BoardModifyFormAction 클래스의 execute() 메서드 호출
			action = new BoardModifyFormAction();
			
			try {
				forward = action.execute(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if(command.equals("/BoardModifyPro.bo")) {
			action = new BoardModifyProAction();
			
			try {
				forward = action.execute(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if(command.equals("/BoardReplyForm.bo")) {
			// BoardReplyFormAction 을 통해 답글 달 원본 게시물 상세 정보 조회 후
			// qna_board_reply.jsp 페이지에서 표시(제목, 내용만 표시)
			action = new BoardReplyFormAction();
			
			try {
				forward = action.execute(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if(command.equals("/BoardReplyPro.bo")) {
			action = new BoardReplyProAction();
			
			try {
				forward = action.execute(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		// --------------------------------------------------------------------------------------
		// ActionForward 객체에 저장된 포워딩 정보에 따른 포워딩 작업 수행하기 위한 공통코드 작성
		if(forward != null) { // ActionForward 객체가 null 이 아닐 경우에만 포워딩 작업 수행
			// Redirect 방식 vs Dispatcher 방식 판별하여 각 방식에 대한 포워딩 작업 수행
			if(forward.isRedirect()) { // Redirect 방식
				response.sendRedirect(forward.getPath());
			} else { // Dispatcher 방식
				RequestDispatcher dispatcher = request.getRequestDispatcher(forward.getPath());
				dispatcher.forward(request, response);
			}
		}
		
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doProcess(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doProcess(request, response);
	}

}







