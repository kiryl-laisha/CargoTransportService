<%--
  Created by IntelliJ IDEA.
  User: Lenovo
  Date: 18.04.2022
  Time: 22:12
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<form action="controller">
    <input type="hidden" name="command" value="sign_in"/>
    <input type="text" name="login" value="login"/>
    <br/>
    <input type="password" name="password" value="password"/>
    <br/>
    <input type="submit" name="sub" value="SignIn"/>
</form>
</body>
</html>
