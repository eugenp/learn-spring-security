<!DOCTYPE html>
<html>
<head>
<title>Layout</title>

<link type="text/css" href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/2.3.2/css/bootstrap.min.css" rel="stylesheet" />

</head>
<body>
    <div class="container">
        <div class="navbar">
            <div class="navbar-inner">
                <a class="brand" href="/"> Home </a>
                <ul class="nav">
                    <li><a href="${pageContext.request.contextPath}/user"> Users </a></li>
                </ul>
                <ul class="nav pull-right">
                    <li><a href="${pageContext.request.contextPath}/logout" class="menu-right" > Logout </a></li>
                </ul>
            </div>
        </div>
        <!-- Content goes here -->
    </div>
</body>
</html>
