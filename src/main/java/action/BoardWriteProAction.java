package action;

import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import svc.BoardWriteProService;
import vo.ActionForward;
import vo.BoardDTO;

/*
 * XXXAction 클래스가 공통으로 갖는 execute() 메서드를 직접 정의하지 않고
 * Action 인터페이스에 구현한 후 Action 클래스가 상속받아 추상메서드를 구현하면 실수를 방지 가능
 * => 추상메서드 execute() 구현을 강제 => 코드의 통일성과 안정성 향상
 */
public class BoardWriteProAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("BoardWriteProAction");
		
		// 포워딩 정보를 저장하는 ActionForward 타입 변수 선언
		ActionForward forward = null;
		// -----------------------------------------------------------------------
		// 비즈니스 로직(데이터베이스 처리)을 위한 데이터 준비 작업 수행
		// => 글쓰기 폼에서 작성 후 글쓰기 버튼 클릭 시 현재 Action 객체로 이동
		// => 폼 파라미터 가져와서 준비 작업 수행(= 게시물 정보 저장)
		// => 주의! form 태그의 enctype 이 multipart/form-data 이므로
		//    파라미터에 접근 시 request 객체에서 바로 접근이 불가능하다!
//		String board_name = request.getParameter("board_name");
//		System.out.println(board_name); // null 값 출력됨
		
		// 파일 업로드 처리를 위해 MultipartRequest 객체 활용(cos.jar 라이브러리 필요)
		// 1. 업로드 파일 위치(이클립스 프로젝트 상의 경로) 저장
		String uploadPath = "upload"; // 가상의 폴더명
		
		// 2. 업로드 파일 크기를 제한하기 위한 정수 형태의 값 지정(10MB 제한)
		int fileSize = 1024 * 1024 * 10; // byte(1) -> KB(1024Byte) -> MB(1024KB) -> 10MB 단위 변환
		
		// 3. 현재 프로젝트(서블릿)를 처리하는 객체인 서블릿 컨텍스트 객체 얻어오기
		ServletContext context = request.getServletContext();
		
		// 4. 업로드 파일이 저장되는 실제 경로를 얻어오기
		// => ServletContext 객체의 getRealPath() 메서드 호출
		String realPath = context.getRealPath(uploadPath); // 가상의 업로드 폴더명을 파라미터로 전달
//		System.out.println(realPath);
		// E:\Shared\JSP\workspace_jsp2\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\MVC_Board\ upload
		// => 실제 업로드 될 폴더 위치(주의! 워크스페이스 내의 프로젝트 폴더에 있는 upload 폴더는 가상의 폴더)
		
		// 5. MultipartRequest 객체 생성
		// => 생성자 파라미터로 파일 업로드에 필요한 각종 파라미터를 전달
		MultipartRequest multi = new MultipartRequest(
			request, // 1) 실제 요청 정보가 포함된 request 객체
			realPath, // 2) 실제 업로드 폴더 경로
			fileSize, // 3) 업로드 파일 크기
			"UTF-8", // 4) 파일명에 대한 인코딩 방식(한글 처리 등이 필요하므로 UTF-8 지정)
			new DefaultFileRenamePolicy() // 5) 중복 파일명에 대한 처리를 담당하는 객체(파일명 뒤에 숫자 1 부터 차례대로 부여)
		);
		// => 객체 생성되는 시점에 이미 업로드 파일은 실제 폴더에 업로드 처리 완료됨
		// ---------------------
		// 6. MultipartRequest 객체의 getParameter() 메서드를 호출하여
		//    폼 파라미터 데이터 가져와서 BoardDTO 객체(board)에 저장
		BoardDTO board = new BoardDTO();
		board.setBoard_name(multi.getParameter("board_name"));
		board.setBoard_pass(multi.getParameter("board_pass"));
		board.setBoard_subject(multi.getParameter("board_subject"));
		board.setBoard_content(multi.getParameter("board_content"));
		// 주의! 파일 정보를 가져올 때 getParameter() 메서드 사용 불가
		board.setBoard_file(multi.getOriginalFileName("board_file")); // 원본 파일명
		board.setBoard_real_file(multi.getFilesystemName("board_file")); // 실제 업로드 파일명
		// 만약, DTO 클래스에 toString() 메서드가 오버라이딩 되어 있을 경우 변수명만으로 출력 가능
//		System.out.println(board);
		System.out.println(request.getRemoteAddr() + " : " + board.getBoard_name() + ", " + board.getBoard_subject());
		
		// -----------------------------------------------------------------------
		// 실제 비즈니스 작업 요청을 수행할 BoardWriteProService 클래스의 인스턴스 생성 후
		// registBoard() 메서드를 호출하여 글쓰기 작업 요청
		// => 파라미터 : BoardDTO 객체   리턴타입 : boolean(isWriteSuccess)
		BoardWriteProService service = new BoardWriteProService();
		boolean isWriteSuccess = service.registBoard(board);
		
		// Service 클래스로부터 글쓰기 작업 요청 처리 결과를 전달받아 성공/실패 여부 판별
		if(!isWriteSuccess) { // 글쓰기 실패 시
			// 자바스크립트를 통해 "글쓰기 실패!" 를 출력 후 이전페이지로 돌아가기
			// => jsp 페이지에서는 out.println() 메서드를 통해 HTML 코드 등을 출력하지만
			//    자바 클래스에서는 response 객체를 통해 문서 타입 설정 및 출력 객체를 가져와서
			//    웹브라우저에 HTML 코드를 출력해야한다!
			// 1) response 객체의 setContentType() 메서드를 호출하여 응답 문서 타입(ContentType) 지정
			response.setContentType("text/html; charset=UTF-8");
			// 2. response 객체의 getWriter() 메서드를 호출하여 출력 스트림(PrintWriter) 객체 리턴받기
			PrintWriter out = response.getWriter();
			// 3. PrintWriter(out) 객체의 println() 메서드를 호출하여 출력할 HTML 태그 작성
			out.println("<script>");
			out.println("alert('글 쓰기 실패!')");
			out.println("history.back()");
			out.println("</script>");
			// 주의! 응답 데이터에 HTML 코드를 저장한 채로 ActionForward 객체가 null 값이 리턴됨
			// => 따라서, FrontController 에서는 별도의 포워딩 작업을 수행하지 않으며
			//    대신 응답 객체(response)에 있는 HTML 태그(자바스크립트)가 실행됨
		} else { // 글쓰기 성공 시
			// 글목록 조회 비즈니스 로직을 수행하기 위한 BoardList.bo 서블릿 주소 요청
			// => 새로운 서비스에 대한 요청이므로 Redirect 방식 포워딩
			forward = new ActionForward();
			forward.setPath("BoardList.bo");
			forward.setRedirect(true);
		}
		
		return forward;
	}

}














