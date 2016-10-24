<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="https://cdn.staticfile.org/twitter-bootstrap/3.3.6/css/bootstrap.min.css">
<title>登录</title>
</head>
<body>
	<div class="overlay"></div>
	<div class="center-wrapper">
		<div class="center-content">
			<div class="row no-m">
				<div class="col-xs-10 col-xs-offset-1 col-sm-6 col-sm-offset-3 col-md-4 col-md-offset-4">
					<section class="panel bg-white no-b fadeIn animated"> <header class="panel-heading no-b text-center" style="font-size:30px;"> Nutz Web Demo </header> <!-- END Language list    -->
					<div class="p15">
						<form id="login_form" action="#" data-parsley-validate="" novalidate="" role="form" method="post">
							<div class="form-group">
								<input type="text" id="username" name="username" value="admin" required class="form-control input-lg mb25" placeholder="用户名">
							</div>
							<div class="form-group">
								<input type="password" id="password" name="password" value="123456" required class="form-control input-lg mb25" placeholder="密码">
							</div>
							<p id="tip" class="bg-danger p15" style="display: none"></p>

							<div class="show">
								<button class="btn btn-primary btn-lg btn-block" type="submit" id="login_button" data-loading-text="登录...">登录</button>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
	<script type="text/javascript" src="https://cdn.staticfile.org/jquery/1.11.3/jquery.min.js"></script>
	<script type="text/javascript" src="https://cdn.staticfile.org/twitter-bootstrap/3.3.6/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="https://cdn.staticfile.org/layer/2.3/layer.js"></script>
	<script type="text/javascript">
		$(function() {
			$("#login_button").click(function() {
				$.ajax({
					url : "${base}/user/login",
					data : $("#login_form").serialize(),
					method : "POST",
					dataType : "json",
					success : function(resp) {
						if (resp) {
							if (resp.ok) {
								//layer.alert("登录成功,即将跳转");
								window.location = "${base}/user/index";
							} else {
								layer.alert("登录失败: " + resp.msg);
							}
						}
					}
				});
				return false;
			});

		});
	</script>
</html>