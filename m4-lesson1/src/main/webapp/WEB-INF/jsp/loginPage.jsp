<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>

<head>
<title>Login Page</title>

<link type="text/css" href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.6/css/bootstrap.min.css" rel="stylesheet" />

</head>

<body>
    <div class="container">
        <h1>Login page</h1>

        <c:if test="${param.error != null}">
        <div class="alert alert-danger">Invalid username and password.</div>
        </c:if>

        <c:if test="${param.logout != null}">
        <div class="alert alert-success">You have been logged out.</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/doLogin" method="post" class="form-horizontal">
            <div class="form-group">
                <label class="control-label col-xs-2" for="username"> Email:</label>
                <div class="col-xs-10">
                    <input id="username" type="text" name="username" />
                </div>
            </div>
            <div class="form-group">
                <label class="control-label col-xs-2" for="password"> Password: </label>
                <div class="col-xs-10">
                    <input id="password" type="password" name="password" />
                </div>
            </div>
            <div class="form-actions col-xs-offset-2 col-xs-10">
                <input type="submit" class="btn btn-primary" value="Sign In" />
            </div>
        </form>
    </div>

</body>
</html>