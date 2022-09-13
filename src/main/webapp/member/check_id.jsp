<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script type="text/javascript">
	// 아이디 검색 후 결과를 다시 현재 페이지로 리다이렉트 했을 때
	// 검색 결과를 처리하기 위한 작업 수행
	// => 현재 페이지 로딩 완료 후 즉시 동작하기 위해 window.onload 이벤트 사용
	window.onload = function() { // body 영역 로딩 완료 후 실행할 익명 함수 정의
		// 1. URL 파라미터에 "id" 파라미터가 존재하는지 확인 => 존재할 경우에만 작업 수행
		var id = "<%=request.getParameter("id") %>"; // 존재하지 않는 아이디일 경우 null 값 전달
// 		alert(id);
		
		if(id != "null") { // id 파라미터가 존재할 경우(문자열 "null" 값 비교)
			// 2. 검색한 아이디를 아이디 입력창에 표시
			document.fr.id.value = id;
			
			var divCheckIdResult = document.getElementById("checkIdResult");
		
			// 3. 중복 검사 결과 판별
			var isDuplicate = "<%=request.getParameter("isDuplicate")%>";
			if(isDuplicate == "true") { // 중복일 경우(문자열 "true" 값 비교)
				divCheckIdResult.innerHTML = "이미 사용중인 아이디";
				divCheckIdResult.style.color = "RED";
			} else { // 중복이 아닐 경우
				divCheckIdResult.innerHTML = "사용 가능한 아이디<br>" 
								+ "<input type='button' value='아이디 사용' onclick='useId()'>";
				divCheckIdResult.style.color = "GREEN";
			}
		}
	}

	function checkId() { // onsubmit 이벤트 동작 시 호출
		// 정규표현식을 사용하여 아이디 입력 양식(영문자,숫자 조합 4~16자리) 검증
		// 아이디 입력창의 입력 내용 가져와서 변수에 저장
		var id = document.fr.id.value;
	
		var elemSpan = document.getElementById("checkIdResult");
	
		var regex = /^[A-Za-z0-9]{4,16}$/;
		// 만약, 아이디의 첫글자는 무조건 영문자여야 하는 경우
// 		var regex = /^[A-Za-z][A-Za-z0-9]{3,15}$/;
		
		// 정규표현식 패턴 객체(regex)의 exec() 메서드를 호출하여 검사할 문자열 전달 후 검증 수행
// 		if(regex.exec(id)) { // 정규표현식과 일치하는 경우
// 			elemSpan.innerHTML = "사용 가능한 아이디";
// 			elemSpan.style.color = "GREEN";
// 		} else {
// 			elemSpan.innerHTML = "영문 대소문자, 숫자, 특수문자 조합 4~16자리 필수!";
// 			elemSpan.style.color = "RED";
// 			document.fr.id.select(); // 입력항목 포커스 및 블럭
// 			return false;
// 		}
		
		// 규칙에 맞지 않을 경우에만 submit 동작 취소하고 오류메세지 표시
		if(!regex.exec(id)) { // 부적합한 아이디일 경우
			elemSpan.innerHTML = "영문 대소문자, 숫자, 특수문자 조합 4~16자리 필수!";
			elemSpan.style.color = "RED";
			document.fr.id.select(); // 입력항목 포커스 및 블럭
			return false; // submit 동작 취소
		} 
		
		return true; // submit 동작 수행
	}
	
	function useId() {
		// 아이디 사용 버튼 클릭 시
		// 부모창(member_join.jsp)의 폼 영역 내의 ID 입력창에 현재 입력된 아이디를 표시
		// => window.opener.document.폼이름.요소이름.value = 값; 형태로 표시 가능
		window.opener.document.fr.id.value = document.fr.id.value; // 부모창 ID 영역에 표시
		window.close(); // 창 닫기
	}
</script>
</head>
<body>
	<h1>ID 중복 체크</h1>
	<form action="CheckDuplicateId.me" name="fr" onsubmit="return checkId()">
		<input type="text" name="id" placeholder="대소문자,숫자,특수문자 4~16자리">
		<input type="submit" value="중복확인">
		<div id="checkIdResult"></div>
	</form>
</body>
</html>











    