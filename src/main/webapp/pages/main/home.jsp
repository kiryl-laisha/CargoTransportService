<%--
  Created by IntelliJ IDEA.
  User: Lenovo
  Date: 18.04.2022
  Time: 20:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<jsp:include page="header.jsp"/>
<form action="controller">
    <input type="hidden" name="command" value="go_to_sign_in">
    <input type="submit" name="sub" value="Authenticate"/>
</form>
    <jsp:include page="footer.jsp"/>
</body>
</html>
