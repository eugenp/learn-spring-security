<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
<title>Users : View all</title>
</head>
<body>
    <jsp:include page="../layout.jsp" />
    <div class="container">
        <h1>Users : View all</h1>
    </div>
    
    <div class="container">
        <div class="pull-right">
            <a href="${pageContext.request.contextPath}/user?form">Create User</a>
        </div>
        <table class="table table-bordered table-striped">
            <thead>
                <tr>
                    <td>Email</td>
                    <td>Created</td>
                </tr>
            </thead>
            <tbody>
                <c:if test="${empty users}">
                <tr>
                    <td colspan="4">No users</td>
                </tr>
                </c:if>
                
                <c:forEach var="user" items="${users}">
                <tr>
                    <td><a href="${pageContext.request.contextPath}/user/${user.id}"> ${user.email}</a></td>
                    <td><fmt:formatDate type="both" dateStyle="long" timeStyle="long" value="${user.created.time}" /></td>
                </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</body>
</html>
