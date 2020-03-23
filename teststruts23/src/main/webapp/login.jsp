<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Login</title>
</head>
<body>
	<form action="${pageContext.request.contextPath}/login/login" method="post">
		<div style="text-align: center; width: 300px;">
			<div>
				<label>username:</label><input name="loginUser.username" type="text">
			</div>
			<div>
				<label>password:</label><input name="loginUser.password" type="text">
			</div>
			<div style="text-align: center;">
				<input type="submit">
			</div>
		</div>
	</form>
</body>
</html>