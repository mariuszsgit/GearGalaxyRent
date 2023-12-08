
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>List</title>
</head>
<body>
<table>
  <tr>
    <td>id</td>
    <td>name</td>
  </tr>
<c:forEach items="${products}" var="product">
    <tr>
        <td>${product.id}</td>
        <td>${product.title}</td>
    </tr>
</c:forEach>

</table>
</body>
</html>
