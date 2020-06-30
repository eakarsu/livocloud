<%@include file="include.jsp"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<!DOCTYPE html>
<html lang="en">

<head>

<title>Livo Mobile Cloud</title>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<META NAME="ROBOTS" CONTENT="NOINDEX, NOFOLLOW">

<!-- BEGIN GLOBAL MANDATORY STYLES -->
<!-- <link href='http://fonts.googleapis.com/css?family=Hind:400,500,300,600,700' rel='stylesheet' type='text/css'> -->
<link
	href="<c:url value="/resources/onepage/css/font-awesome.min.css"/>"
	rel="stylesheet" type="text/css" />
<link
	href="<c:url value="/resources/onepage/css/simple-line-icons.min.css"/>"
	rel="stylesheet" type="text/css" />
<link
	href="<c:url value="/resources/onepage/bootstrap/css/bootstrap.min.css"/>"
	rel="stylesheet" type="text/css" />
<!-- END GLOBAL MANDATORY STYLES -->
<!-- BEGIN PAGE LEVEL PLUGIN STYLES -->
<link href="<c:url value="/resources/onepage/css/owl.carousel.css"/>"
	rel="stylesheet" type="text/css" />
<link href="<c:url value="/resources/onepage/css/settings.css"/>"
	rel="stylesheet" type="text/css" />
<link
	href="<c:url value="/resources/onepage/css/cubeportfolio.min.css"/>"
	rel="stylesheet" type="text/css" />
<!-- END PAGE LEVEL PLUGIN STYLES -->
<!-- BEGIN THEME STYLES -->
<link href="<c:url value="/resources/onepage/css/layout.css"/>"
	rel="stylesheet" type="text/css" />
<!-- END THEME STYLES -->
<!-- <link rel="shortcut icon" href="favicon.ico" /> -->
</head>
<!-- END HEAD -->
<!-- BEGIN BODY -->
<!-- DOC: Apply "page-on-scroll" class to the body element to set fixed header layout -->
<body class="page-header-fixed">

	<!-- BEGIN MAIN LAYOUT -->
	<!-- Header BEGIN -->

	<header class="page-header">
		<div id="megamenu">
			<div class="toptopbar">
				<ul>
					<li><a href="#" class="signup">SIGN UP</a></li>
					<li><a href="/LivoCloud/login" class="login">Login</a></li>
					<li><a href="#" class="toplink">Company</a></li>
					<li><a href="#" class="toplink">Dev Center</a></li>
					<li><a href="#" class="toplink">Blog</a></li>
				</ul>
			</div>
		</div>
		<nav class="navbar navbar-fixed-top" style="top: 40px;"
			role="navigation">
			<div class="container">
				<!-- Brand and toggle get grouped for better mobile display -->
				<div class="navbar-header page-scroll">
					<button type="button" class="navbar-toggle" data-toggle="collapse"
						data-target=".navbar-responsive-collapse">
						<span class="sr-only">Toggle navigation</span> <span
							class="toggle-icon"> <span class="icon-bar"></span> <span
							class="icon-bar"></span> <span class="icon-bar"></span>
						</span>
					</button>
					<a class="navbar-brand" href="#intro"> <img
						class="logo-default"
						src="<c:url value="/resources/onepage/img/logo_default.png"/>"
						alt="Logo"> <img class="logo-scroll"
						src="<c:url value="/resources/onepage/img/logo_scroll.png"/>"
						alt="Logo">
					</a>
				</div>

				<!-- Collect the nav links, forms, and other content for toggling -->
				<div class="collapse navbar-collapse navbar-responsive-collapse">
					<ul class="nav navbar-nav">
						<li class="page-scroll active"><a href="#intro">HOME</a></li>
						<li class="page-scroll"><a href="#about">WHAT IS LIVO</a></li>
						<li class="page-scroll"><a href="#features">FEATURES</a></li>
						<li class="page-scroll"><a href="#pricing">PRICING</a></li>
						<li class="page-scroll"><a href="#clients">CLIENTS</a></li>
						<li class="page-scroll"><a href="#contact">Contact</a></li>
						<!-- <li class="page-scroll"><a href="#team">Team</a></li> -->
						<!-- <li class="page-scroll"><a href="#portfolio">Portfolio</a></li> -->
					</ul>
				</div>
				<!-- End Navbar Collapse -->
			</div>
			<!--/container-->
		</nav>
	</header>
	<!-- Header END -->

	<!-- BEGIN INTRO SECTION -->
	<section id="intro">
		<!-- Slider BEGIN -->
		<div class="page-slider">
			<div class="fullwidthbanner-container revolution-slider">
				<div class="banner">
					<ul id="revolutionul">

						<li data-transition="fade" data-slotamount="8"
							data-masterspeed="700" data-delay="6000" data-thumb="">
							<!-- THE MAIN IMAGE IN THE FIRST SLIDE --> <img
							src="<c:url value="/resources/onepage/img/bg/bg_slider2.jpg"/>"
							alt="">

							<div class="caption lft tp-resizeme" data-x="center"
								data-y="center" data-hoffset="-322" data-voffset="-30"
								data-speed="900" data-start="1000" data-easing="easeOutExpo">
								<h3 class="title-v2">
									MOBILIZE YOUR <br> BUSINESS DATA
								</h3>
							</div>
							<div class="caption lft tp-resizeme" data-x="center"
								data-y="center" data-hoffset="-490" data-voffset="110"
								data-speed="900" data-start="1500" data-easing="easeOutExpo">
								<p class="subtitle-v2">Available in:</p>
							</div> <a href="#"
							class="caption lft tp-resizeme slide_thumb_img slide_border"
							data-x="center" data-y="center" data-hoffset="-370"
							data-voffset="102" data-speed="900" data-start="1500"
							data-easing="easeOutExpo"> <img
								src="<c:url value="/resources/onepage/img/widgets/icon_android.png"/>"
								alt="Image 1"> |
						</a> <a href="#" class="caption lft tp-resizeme slide_thumb_img"
							data-x="center" data-y="center" data-hoffset="-318"
							data-voffset="102" data-speed="900" data-start="1500"
							data-easing="easeOutExpo"> <img
								src="<c:url value="/resources/onepage/img/widgets/icon_ios.png"/>"
								alt="Image 2">
						</a>
							<div class="caption lfb tp-resizeme" data-x="right"
								data-y="bottom" data-hoffset="100" data-speed="900"
								data-start="2000" data-easing="easeOutExpo">
								<img
									src="<c:url value="/resources/onepage/img/widgets/device.png"/>"
									alt="Image 3">
							</div>
							<div class="caption lft tp-resizeme" data-x="center"
								data-y="center" data-hoffset="-280" data-voffset="220"
								data-speed="900" data-start="1500" data-easing="easeOutExpo">


								<form:form id="signUp-form" class="form-wrap input-field"
									action="signUp" method="post" modelAttribute="signUpBean">
									<div class="form-wrap-group">
										<form:input type="text" class="form-control" id="name"
											name="name" placeholder="Name Surname" path="" />
									</div>
									<div class="form-wrap-group">
										<form:input type="email" class="form-control" id="email"
											name="email" placeholder="Your Email" path="" />
									</div>
									<!-- 									<div class="form-wrap-group border-left-transparent"> -->
									<%-- 										<form:input type="password" class="form-control" id="password" --%>
									<%-- 											name="password" placeholder="Password" path="" /> --%>
									<!-- 									</div> -->
									<div class="form-wrap-group">
										<button id="signUpButton" type="submit"
											class="btn-danger btn-md btn-block">Signup</button>

									</div>

								</form:form>

								<div class="col-md-12">
									<c:if test="${isRegisteredEmail}">
										<div id="signUpResponse" class="alert alert-warning">${userControlResponse}</div>
									</c:if>
								</div>

							</div>


						</li>

					</ul>
				</div>
			</div>
		</div>
		<!-- Slider END -->
	</section>
	<!-- END INTRO SECTION -->

	<!-- BEGIN MAIN LAYOUT -->
	<div class="page-content">
		<!-- 		<!-- SUBSCRIBE BEGIN -->
		<!-- 		<div class="subscribe"> -->
		<!-- 			<div class="container"> -->
		<!-- 				<div class="subscribe-wrap"> -->
		<!-- 					<div class="subscribe-body subscribe-desc md-margin-bottom-30"> -->
		<!-- 						<h1>Signup for free</h1> -->
		<!-- 						<p>To try the most advanced business platform for mobile and -->
		<!-- 							desktop</p> -->
		<!-- 					</div> -->
		<!-- 					<div class="subscribe-body"> -->
		<!-- 						<form id="signUpForm" class="form-wrap input-field"> -->
		<!-- 							<div class="form-wrap-group"> -->
		<!-- 								<input type="name" class="form-control" id="name" -->
		<!-- 									placeholder="Name"> -->
		<!-- 							</div> -->
		<!-- 							<div class="form-wrap-group border-left-transparent"> -->
		<!-- 								<input type="email" class="form-control" id="email" -->
		<!-- 									placeholder="Your Email"> -->
		<!-- 							</div> -->
		<!-- 							<div class="form-wrap-group"> -->
		<!-- 								<button type="submit" class="btn-danger btn-md btn-block">Signup</button> -->
		<!-- 							</div> -->
		<!-- 						</form> -->
		<!-- 					</div> -->
		<!-- 				</div> -->
		<!-- 			</div> -->
		<!-- 		</div> -->
		<!-- 		<!-- SUBSCRIBE END -->

		<!-- BEGIN ABOUT SECTION -->
		<section id="about">
			<!-- Services BEGIN -->
			<div class="container service-bg">
				<div class="row">
					<div class="col-sm-4">
						<div class="services sm-margin-bottom-100">
							<div class="services-wrap">
								<div class="service-body">
									<img
										src="<c:url value="/resources/onepage/img/widgets/icon1.png"/>"
										alt="">
								</div>
							</div>
							<h2>Rapid App Development</h2>
							<p>
								Build your mobile app using <br> the HTML5 in just a day
							</p>
						</div>
					</div>
					<div class="col-sm-4">
						<div class="services sm-margin-bottom-100">
							<div class="services-wrap">
								<div class="service-body">
									<img
										src="<c:url value="/resources/onepage/img/widgets/icon2.png"/>"
										alt="">
								</div>
							</div>
							<h2>Enterprise Middleware on Cloud</h2>
							<p>
								Mobilize your business data <br> with service integration
							</p>
						</div>
					</div>
					<div class="col-sm-4">
						<div class="services">
							<div class="services-wrap">
								<div class="service-body">
									<img
										src="<c:url value="/resources/onepage/img/widgets/icon3.png"/>"
										alt="">
								</div>
							</div>
							<h2>Deploy and Manage</h2>
							<p>
								Deploy and update app from the dashboard, <br> autherize
								your clients
							</p>
						</div>
					</div>
				</div>
			</div>
			<!-- Services END -->
		</section>
		<!-- END ABOUT SECTION -->

		<!-- BEGIN FEATURES SECTION -->
		<section id="features">
			<!-- Features BEGIN -->
			<div class="features-bg">
				<div class="container">
					<!-- 					<div class="heading"> -->
					<!-- 						<h2> -->
					<!-- 							<strong>Livo Mobile</strong> Main Features -->
					<!-- 						</h2> -->

					<!-- 					</div> -->
					<!-- //end heading -->

					<!-- Features -->
					<div class="row margin-bottom-70">
						<div class="col-md-6 md-margin-bottom-70">
							<div class="features">
								<img
									src="<c:url value="/resources/onepage/img/widgets/screen1.png"/>"
									alt="">
								<div class="features-in">
									<h3>
										<a href="#">MOBILE CONTAINER</a>
									</h3>

									<p>
										<strong>CROSS-PLATFORM (Android-iOS)</strong><br> NATIVE
										DEVICE FUNCTIONS<br>-Camera<br>-Geolocation<br>-Vibration<br>-Contacts<br>more..
									</p>
								</div>
							</div>
						</div>
						<div class="col-md-6">
							<div class="features">
								<img
									src="<c:url value="/resources/onepage/img/widgets/screen2.png"/>"
									alt="">
								<div class="features-in">
									<h3>
										<a href="#">IDE</a>
									</h3>
									<p>
										Jquery Toolbar <br>Pre-built templates<br> Free
										style coding <br> Live Preview and Test <br>
										Collaboration with Github <br>
									</p>
								</div>
							</div>
						</div>
					</div>
					<!-- //end row -->
					<div class="row margin-bottom-80">
						<div class="col-md-6 md-margin-bottom-70">
							<div class="features">
								<img
									src="<c:url value="/resources/onepage/img/widgets/screen3.png"/>"
									alt="">
								<div class="features-in">
									<h3>
										<a href="#">PLATFORM</a>
									</h3>
									<p>
										AD Auth <br> Push Notification<br> REST/SOAP
										Integration Wizard <br> SAP Connector and JavaScript
										Plugin <br> Central Deployment <br> Easy app
										distribution and update
									</p>
								</div>
							</div>
						</div>
						<div class="col-md-6">
							<div class="features">
								<img
									src="<c:url value="/resources/onepage/img/widgets/screen4.png"/>"
									alt="">
								<div class="features-in">
									<h3>
										<a href="#">AngularJS support</a>
									</h3>
									<p>Lorem niam ipsum dolor sit ammet adipiscing et suitem
										elit et nonuy nibh elit niam dolor</p>
								</div>
							</div>
						</div>
					</div>
					<!-- //end row -->
					<!-- End Features -->
				</div>
			</div>
			<!-- Features END -->
		</section>
		<!-- END FEATURES SECTION -->

		<!-- BEGIN PRICING SECTION -->
		<section id="pricing">
			<div class="pricing-bg">
				<div class="container">
					<!-- Pricing -->
					<div class="row no-space-row">
						<div class="col-md-4">
							<div class="pricing no-right-brd">
								<img
									src="<c:url value="/resources/onepage/img/widgets/icon4.png"/>"
									alt="">
								<h4>Developer Plan</h4>
								<span>Free</span>
								<ul class="pricing-features">
									<li>IDE</li>
									<li>Mobile Container</li>
									<li>Up to 1 App</li>
									<li>100 MB Space</li>
								</ul>
								<button type="button" class="btn-brd-primary">Purchase</button>
							</div>
						</div>
						<div class="col-md-4">
							<div class="pricing pricing-red">
								<img
									src="<c:url value="/resources/onepage/img/widgets/icon5.png"/>"
									alt="">
								<h4>Professional Plan</h4>
								<span>$65 / Month</span>
								<ul class="pricing-features">
									<li>Basic Auth</li>
									<li>Up to 3 App</li>
									<li>1000 MB Space</li>
									<li>Up to 1 Service Integration</li>
									<li>Standart Support</li>
									<li>Weekly Backup</li>
								</ul>
								<button type="button" id="professionalPlanButton"
									class="btn-brd-white">Purchase</button>
							</div>
						</div>
						<div class="col-md-4">
							<div class="pricing no-left-brd">
								<img
									src="<c:url value="/resources/onepage/img/widgets/icon6.png"/>"
									alt="">
								<h4>Enterprise Plan</h4>
								<span>$125 / Month</span>
								<ul class="pricing-features">
									<li>AD Auth</li>
									<li>Up to 10 App</li>
									<li>10.000 MB Space</li>
									<li>Unlimited Service Integration</li>
									<li>Premium Support</li>
									<li>Daily Backup</li>
								</ul>
								<button type="button" class="btn-brd-primary">Purchase</button>
							</div>
						</div>
					</div>
					<!-- //end row -->
					<!-- End Pricing -->
				</div>
			</div>
		</section>

		<!-- BEGIN CLIENTS SECTION -->
		<section id="clients">
			<div class="clients">
				<div class="clients-bg">
					<div class="container">
						<div class="heading-blue">
							<h2>
								Over <strong>30.000</strong> Customers
							</h2>
							<p>and let's see what are they saying</p>
						</div>
						<!-- //end heading -->

						<!-- Owl Carousel -->
						<div class="owl-carousel">
							<div class="item" data-quote="#client-quote-1">
								<img
									src="<c:url value="/resources/onepage/img/clients/logo1.png"/>"
									alt="">
							</div>
							<div class="item" data-quote="#client-quote-2">
								<img
									src="<c:url value="/resources/onepage/img/clients/logo2.png"/>"
									alt="">
							</div>
							<div class="item" data-quote="#client-quote-3">
								<img
									src="<c:url value="/resources/onepage/img/clients/logo3.png"/>"
									alt="">
							</div>
							<div class="item" data-quote="#client-quote-4">
								<img
									src="<c:url value="/resources/onepage/img/clients/logo4.png"/>"
									alt="">
							</div>
							<div class="item" data-quote="#client-quote-5">
								<img
									src="<c:url value="/resources/onepage/img/clients/logo5.png"/>"
									alt="">
							</div>
							<div class="item" data-quote="#client-quote-6">
								<img
									src="<c:url value="/resources/onepage/img/clients/logo6.png"/>"
									alt="">
							</div>
							<div class="item" data-quote="#client-quote-7">
								<img
									src="<c:url value="/resources/onepage/img/clients/logo7.png"/>"
									alt="">
							</div>
							<div class="item" data-quote="#client-quote-8">
								<img
									src="<c:url value="/resources/onepage/img/clients/logo8.png"/>"
									alt="">
							</div>
							<div class="item" data-quote="#client-quote-9">
								<img
									src="<c:url value="/resources/onepage/img/clients/logo9.png"/>"
									alt="">
							</div>
							<div class="item" data-quote="#client-quote-10">
								<img
									src="<c:url value="/resources/onepage/img/clients/logo10.png"/>"
									alt="">
							</div>
							<div class="item" data-quote="#client-quote-11">
								<img
									src="<c:url value="/resources/onepage/img/clients/logo11.png"/>"
									alt="">
							</div>
							<div class="item" data-quote="#client-quote-12">
								<img
									src="<c:url value="/resources/onepage/img/clients/logo12.png"/>"
									alt="">
							</div>
							<div class="item" data-quote="#client-quote-13">
								<img
									src="<c:url value="/resources/onepage/img/clients/logo13.png"/>"
									alt="">
							</div>
							<div class="item" data-quote="#client-quote-14">
								<img
									src="<c:url value="/resources/onepage/img/clients/logo14.png"/>"
									alt="">
							</div>
						</div>
						<!-- End Owl Carousel -->
					</div>
				</div>

				<!-- Clients Quotes -->
				<div class="clients-quotes">
					<div class="container">
						<div class="client-quote" id="client-quote-1">
							<p>Lorem ipsum dolor sit amet consectetuer adipiscing elit
								euismod tincidunt ut laoreet dolore magna aliquam dolor sit amet
								consectetuer elit</p>
							<h4>Mark Nilson</h4>
							<span>Director</span>
						</div>
						<div class="client-quote" id="client-quote-2">
							<p>Lorem ipsum dolor sit amet consectetuer adipiscing elit
								euismod tincidunt aliquam dolor sit amet consectetuer elit</p>
							<h4>Lisa Wong</h4>
							<span>Artist</span>
						</div>
						<div class="client-quote" id="client-quote-3">
							<p>Lorem ipsum dolor sit amet consectetuer elit euismod
								tincidunt aliquam dolor sit amet elit</p>
							<h4>Nick Dalton</h4>
							<span>Developer</span>
						</div>
						<div class="client-quote" id="client-quote-4">
							<p>Fusce mattis vestibulum felis, vel semper mi interdum
								quis. Vestibulum ligula turpis, aliquam a molestie quis, gravida
								eu libero.</p>
							<h4>Alex Janmaat</h4>
							<span>Co-Founder</span>
						</div>
						<div class="client-quote" id="client-quote-5">
							<p>Vestibulum sodales imperdiet euismod.</p>
							<h4>Jeffrey Veen</h4>
							<span>Designer</span>
						</div>
						<div class="client-quote" id="client-quote-6">
							<p>Praesent sed sollicitudin mauris. Praesent eu metus
								laoreet, sodales orci nec, rutrum dui.</p>
							<h4>Inna Rose</h4>
							<span>Google</span>
						</div>
						<div class="client-quote" id="client-quote-7">
							<p>Sed ornare enim ligula, id imperdiet urna laoreet eu.
								Praesent eu metus laoreet, sodales orci nec, rutrum dui.</p>
							<h4>Jacob Nelson</h4>
							<span>Support</span>
						</div>
						<div class="client-quote" id="client-quote-8">
							<p>Adipiscing elit euismod tincidunt ut laoreet dolore magna
								aliquam dolor sit amet consectetuer elit</p>
							<h4>John Doe</h4>
							<span>Marketing</span>
						</div>
						<div class="client-quote" id="client-quote-9">
							<p>Nam euismod fringilla turpis vitae tincidunt, adipiscing
								elit euismod tincidunt aliquam dolor sit amet consectetuer elit</p>
							<h4>Michael Stawson</h4>
							<span>Graphic Designer</span>
						</div>
						<div class="client-quote" id="client-quote-10">
							<p>Quisque eget mi non enim efficitur fermentum id at purus.</p>
							<h4>Liam Nelsson</h4>
							<span>Actor</span>
						</div>
						<div class="client-quote" id="client-quote-11">
							<p>Integer et ante dictum, hendrerit metus eget, ornare
								massa.</p>
							<h4>Madison Klarsson</h4>
							<span>Director</span>
						</div>
						<div class="client-quote" id="client-quote-12">
							<p>Vestibulum sodales imperdiet euismod.</p>
							<h4>Ava Veen</h4>
							<span>Writer</span>
						</div>
						<div class="client-quote" id="client-quote-13">
							<p>Ut sit amet nisl nec dui lobortis gravida ut et neque.
								Praesent eu metus laoreet, sodales orci nec, rutrum dui.</p>
							<h4>Sophia Williams</h4>
							<span>Apple</span>
						</div>
						<div class="client-quote" id="client-quote-14">
							<p>Nam non vulputate orci. Duis sed mi nec ligula tristique
								semper vitae pretium nisi. Pellentesque nec enim vel magna
								pulvinar vulputate.</p>
							<h4>Melissa Korn</h4>
							<span>Reporter</span>
						</div>
					</div>
				</div>
				<!-- End Clients Quotes -->
			</div>
		</section>
		<!-- END CLIENTS SECTION -->


		<!-- BEGIN CONTACT SECTION -->
		<section id="contact">
			<!-- Footer -->
			<div class="footer">
				<div class="container">
					<div class="row">
						<div class="col-sm-6">
							<div class="heading-left-light">
								<h2>Say hello to Metronic</h2>
								<p>
									To try the most advanced business platform <br> for mobile
									and desktop
								</p>
							</div>
						</div>
						<div class="col-sm-6">
							<div class="form">
								<div class="form-wrap">
									<div class="form-wrap-group">
										<input type="text" placeholder="Your Name"
											class="form-control"> <input type="text"
											placeholder="Subject"
											class="border-top-transparent form-control">
									</div>
									<div class="form-wrap-group border-left-transparent">
										<input type="text" placeholder="Your Email"
											class="form-control"> <input type="text"
											placeholder="Contact Phone"
											class="border-top-transparent form-control">
									</div>
								</div>
							</div>
							<textarea rows="8" name="message"
								placeholder="Write comment here ..."
								class="border-top-transparent form-control"></textarea>
							<button type="submit" class="btn-danger btn-md btn-block">Send
								it</button>
						</div>
					</div>
					<!-- //end row -->
				</div>
			</div>
			<!-- End Footer -->

			<!-- Footer Coypright -->
			<div class="footer-copyright">
				<div class="container">
					<h3>Metronic</h3>
					<ul class="copyright-socials">
						<li><a href="#"><i class="fa fa-twitter"></i></a></li>
						<li><a href="#"><i class="fa fa-facebook"></i></a></li>
						<li><a href="#"><i class="fa fa-dribbble"></i></a></li>
						<li><a href="#"><i class="fa fa-pinterest"></i></a></li>
						<li><a href="#"><i class="fa fa-linkedin"></i></a></li>
					</ul>
					<P>Designed with love by KeenThemes</P>
				</div>
			</div>
			<!-- End Footer Coypright -->
		</section>
		<!-- END CONTACT SECTION -->
	</div>
	<!-- END MAIN LAYOUT -->
	<a href="#intro" class="go2top"><i class="fa fa-arrow-up"></i></a>

	<!-- BEGIN JAVASCRIPTS(Load javascripts at bottom, this will reduce page load time) -->
	<!-- BEGIN CORE PLUGINS -->

	<script src="<c:url value="/resources/onepage/js/jquery.min.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/onepage/js/jquery-migrate.min.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/onepage/bootstrap/js/bootstrap.min.js"/>"
		type="text/javascript"></script>
	<!-- END CORE PLUGINS -->

	<!-- BEGIN PAGE LEVEL PLUGINS -->
	<script src="<c:url value="/resources/onepage/js/jquery.easing.js"/>"
		type="text/javascript"></script>
	<script src="<c:url value="/resources/onepage/js/jquery.parallax.js"/>"
		type="text/javascript"></script>
	<script src="<c:url value="/resources/onepage/js/smooth-scroll.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/onepage/js/owl.carousel.min.js"/>"
		type="text/javascript"></script>

	<!-- BEGIN Cubeportfolio -->
	<script
		src="<c:url value="/resources/onepage/js/jquery.cubeportfolio.min.js"/>"
		type="text/javascript"></script>
	<script src="<c:url value="/resources/onepage/js/portfolio.js"/>"
		type="text/javascript"></script>
	<!-- END Cubeportfolio -->

	<!-- BEGIN RevolutionSlider -->
	<script
		src="<c:url value="/resources/onepage/js/jquery.themepunch.revolution.min.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/onepage/js/jquery.themepunch.tools.min.js"/>"
		type="text/javascript"></script>
	<script src="<c:url value="/resources/onepage/js/revo-ini.js"/>"
		type="text/javascript"></script>
	<!-- END RevolutionSlider -->
	<!-- END PAGE LEVEL PLUGINS -->
	<!-- BEGIN PAGE LEVEL SCRIPTS -->
	<script src="<c:url value="/resources/onepage/js/layout.js"/>"
		type="text/javascript"></script>
	<script src="<c:url value="/resources/onepage/js/custom.js"/>"
		type="text/javascript"></script>


	<!-- END PAGE LEVEL SCRIPTS -->

	<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>