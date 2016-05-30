<%--
  Created by IntelliJ IDEA.
  User: plato2000
  Date: 5/27/16
  Time: 8:54 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.nexttech.spanishreview.database.MCPSStudent" %>
<%@ page import="static com.googlecode.objectify.ObjectifyService.ofy" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="org.codehaus.plexus.util.StringUtils" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
    <meta charset="utf-8">
    <title>Spanish Review - Teacher</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <!-- Stylesheets -->
    <link rel="stylesheet" href="/css/bootstrap.min.css" media="screen">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.6.1/css/bootstrap-datepicker.min.css">

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
    <!-- jQuery -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.2.2/jquery.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>

    <script src="/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.13.0/moment.min.js"></script>
    <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.6.1/js/bootstrap-datepicker.min.js"></script>
    <script type="text/javascript" src="/js/jquery-csv.min.js"></script>
    <script type="text/javascript"
            src="https://cdnjs.cloudflare.com/ajax/libs/jquery-json/2.5.1/jquery.json.min.js"></script>


    <script src="/js/teacher.js"></script>

    <!-- Google sign in stuff -->
    <script src="https://apis.google.com/js/client.js?onload=checkAuth"></script>
</head>
<body>
    <div class="navbar navbar-default navbar-fixed-top top">
        <div class="container">
            <div class="navbar-header">
                <a href="/" class="navbar-brand">Spanish Review</a>
                <button class="navbar-toggle" type="button" data-toggle="collapse" data-target="#navbar-main">
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
            </div>
            <div class="navbar-collapse collapse" id="navbar-main">
                <ul class="nav navbar-nav">
                    <li>
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Classes <span class="caret"></span></a>
                        <ul id="dropdown" class="dropdown-menu">

                        </ul>
                    </li>

                </ul>

                <ul class="nav navbar-nav navbar-right">
                    <%
                        // Gets the authentication handler from the Users API
                        UserService userService = UserServiceFactory.getUserService();
                        User user = userService.getCurrentUser();
//                        System.out.println(user.getUserId());
    //                        System.out.println(user.getEmail().substring(0, user.getEmail().indexOf("@")));
    //                        System.out.println(StringUtils.isNumeric(user.getEmail().substring(0, user.getEmail().indexOf("@"))));
    //                        System.out.println(user.getEmail().substring(user.getEmail().indexOf("@") + 1));

                        // Allows functions using the ${} syntax to use variable
                        pageContext.setAttribute("user", user);
                    %>
                    <li><a id="sign-in" href="javascript:handleAuthClick()">Sign in</a></li>
                    <li><a href="javascript:signOut()">Sign out</a></li>

                    <!--<li><a href="http://builtwithbootstrap.com/" target="_blank">Built With Bootstrap</a></li>-->
                    <!--<li><a href="https://wrapbootstrap.com/?ref=bsw" target="_blank">WrapBootstrap</a></li>-->
                </ul>

            </div>
        </div>
    </div>
    <br />
    <br />
    <br />
    <br />
    <div class="container">
        <div class="row">
            <h1 id="class-name">No class selected.</h1>
            <div class="form-inline" id="file-picker-div">
                <label for="file-picker" id="mer-file-label">Select .MER file: </label>
                <input type="file" class="form-control input-sm" id="file-picker">
            </div>
            <div class="form-inline" id="deadline-span" style="display:none;">
                <label for="deadline">Deadline: </label>
                <input type="text" class="form-control input-sm" id="deadline" data-provide="datepicker">
            </div>
            <div class="form-inline" id="requirements-span" style="display: none;">

                    <label for="total">Total WS Required: </label>
                    <input type="number" class="form-control input-sm" id="total">

                    <label for="blank">Blank WS Required: </label>
                    <input type="number" class="form-control input-sm" id="blank">

                    <label for="scoredOver">WS Over 30 Required: </label>
                    <input type="number" class="form-control input-sm" id="scoredOver">
            </div>
            <div class="btn-group btn-group-justified" id="button-bar" style="display:none;">
                <a href="javascript:refreshInfoFromServer()" class="btn btn-success"><span class="glyphicon glyphicon-refresh"></span>&nbsp;Refresh Data</a>
                <a href="javascript:updateRequirements()" class="btn btn-primary"><span class="glyphicon glyphicon-floppy-disk"></span>&nbsp;Submit Requirements</a>
                <a data-toggle="modal" data-target="#reset-users" class="btn btn-danger"><span class="glyphicon glyphicon-remove"></span>&nbsp;Reset Student Progress</a>
            </div>
            <h2>Students:</h2>
            <table class="table table-bordered table-responsive" id="students">
                <tr>
                    <th>Name</th>
                    <th>Status</th>
                </tr>
            </table>
        </div>
    </div>
    <div class="modal fade" id="reset-users" tabindex="-1" role="dialog" aria-labelledby="reset-users-label">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title" id="reset-users-label">Are you sure?</h4>
                </div>
                <div class="modal-body">
                    Are you sure you want to reset user progress? This means that every worksheet completed by each of the users in the table to this point will no longer count towards the total. This cannot be undone.
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                    <button type="button" class="btn btn-danger" onclick="resetUsers()">Reset progress</button>
                </div>
            </div>
        </div>
    </div>


</body>
</html>
