<%@include file="include.jsp"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<!DOCTYPE html>
<!-- 
Template Name: Metronic - Responsive Admin Dashboard Template build with Twitter Bootstrap 3.3.4
Version: 3.8.1
Author: KeenThemes
Website: http://www.keenthemes.com/
Contact: support@keenthemes.com
Follow: www.twitter.com/keenthemes
Like: www.facebook.com/keenthemes
Purchase: http://themeforest.net/item/metronic-responsive-admin-dashboard-template/4021469?ref=keenthemes
License: You must have a valid license purchased only from themeforest(the above link) in order to legally use the theme for your project.
-->
<!--[if IE 8]> <html lang="en" class="ie8 no-js"> <![endif]-->
<!--[if IE 9]> <html lang="en" class="ie9 no-js"> <![endif]-->
<!--[if !IE]><!-->
<html lang="en" class="no-js">
<!--<![endif]-->
<!-- BEGIN HEAD -->
<head>
<meta charset="utf-8" />
<title>Livo Mobile | Admin Dashboard</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta content="width=device-width, initial-scale=1" name="viewport" />
<meta content="" name="description" />
<meta content="" name="author" />
<!-- BEGIN GLOBAL MANDATORY STYLES -->
<link
	href="http://fonts.googleapis.com/css?family=Open+Sans:400,300,600,700&subset=all"
	rel="stylesheet" type="text/css" />
<link
	href="<c:url value="/resources/adminpanel/global/plugins/font-awesome/css/font-awesome.min.css"/>"
	rel="stylesheet" type="text/css" />
<link
	href="<c:url value="/resources/adminpanel/global/plugins/simple-line-icons/simple-line-icons.min.css"/>"
	rel="stylesheet" type="text/css" />
<link
	href="<c:url value="/resources/adminpanel/global/plugins/bootstrap/css/bootstrap.min.css"/>"
	rel="stylesheet" type="text/css" />
<link
	href="<c:url value="/resources/adminpanel/global/plugins/uniform/css/uniform.default.css"/>"
	rel="stylesheet" type="text/css" />
<link
	href="<c:url value="/resources/adminpanel/global/plugins/bootstrap-switch/css/bootstrap-switch.min.css"/>"
	rel="stylesheet" type="text/css" />
<!-- END GLOBAL MANDATORY STYLES -->
<!-- BEGIN PAGE LEVEL PLUGIN STYLES -->
<link
	href="<c:url value="/resources/adminpanel/global/plugins/bootstrap-daterangepicker/daterangepicker-bs3.css"/>"
	rel="stylesheet" type="text/css" />
<link
	href="<c:url value="/resources/adminpanel/global/plugins/fullcalendar/fullcalendar.min.css"/>"
	rel="stylesheet" type="text/css" />
<link
	href="<c:url value="/resources/adminpanel/global/plugins/jqvmap/jqvmap/jqvmap.css"/>"
	rel="stylesheet" type="text/css" />
<link
	href="<c:url value="/resources/adminpanel/global/plugins/morris/morris.css"/>"
	rel="stylesheet" type="text/css" />
<!-- END PAGE LEVEL PLUGIN STYLES -->
<!-- BEGIN PAGE STYLES -->
<link
	href="<c:url value="/resources/adminpanel/admin/pages/css/tasks.css"/>"
	rel="stylesheet" type="text/css" />

<link
	href="<c:url value="/resources/adminpanel/admin/pages/css/pricing-table.css"/>"
	rel="stylesheet" type="text/css" />
<!-- END PAGE STYLES -->
<!-- BEGIN THEME STYLES -->
<!-- DOC: To use 'rounded corners' style just load 'components-rounded.css' stylesheet instead of 'components.css' in the below style tag -->
<link
	href="<c:url value="/resources/adminpanel/global/css/components-rounded.css"/>"
	rel="stylesheet" type="text/css" />
<link
	href="<c:url value="/resources/adminpanel/global/css/plugins.css"/>"
	rel="stylesheet" type="text/css" />
<link
	href="<c:url value="/resources/adminpanel/admin/layout4/css/layout.css"/>"
	rel="stylesheet" type="text/css" />
<link
	href="<c:url value="/resources/adminpanel/admin/layout4/css/themes/light.css"/>"
	rel="stylesheet" type="text/css" id="style_color" />
<link
	href="<c:url value="/resources/adminpanel/admin/layout4/css/custom.css"/>"
	rel="stylesheet" type="text/css" />
<!-- END THEME STYLES -->
</head>
<!-- END HEAD -->
<!-- BEGIN BODY -->
<!-- DOC: Apply "page-header-fixed-mobile" and "page-footer-fixed-mobile" class to body element to force fixed header or footer in mobile devices  -->
<!-- DOC: Apply "page-sidebar-closed" class to the body and "page-sidebar-menu-closed" class to the sidebar menu element to hide the sidebar by default -->
<!-- DOC: Apply "page-sidebar-hide" class to the body to make the sidebar completely hidden on toggle -->
<!-- DOC: Apply "page-sidebar-closed-hide-logo" class to the body element to make the logo hidden on sidebar toggle -->
<!-- DOC: Apply "page-sidebar-hide" class to body element to completely hide the sidebar on sidebar toggle -->
<!-- DOC: Apply "page-sidebar-fixed" class to have fixed sidebar -->
<!-- DOC: Apply "page-footer-fixed" class to the body element to have fixed footer -->
<!-- DOC: Apply "page-sidebar-reversed" class to put the sidebar on the right side -->
<!-- DOC: Apply "page-full-width" class to the body element to have full width page without the sidebar menu -->
<body class="page-header-fixed">
	<!-- BEGIN HEADER -->
	<div class="page-header navbar navbar-fixed-top">
		<!-- BEGIN HEADER INNER -->
		<div class="page-header-inner">
			<!-- BEGIN LOGO -->
			<div class="page-logo">
				<a href="/LivoCloud/"> <img
					src="<c:url value="/resources/adminpanel/admin/layout4/img/LivoLogo.png"/>"
					alt="logo" class="logo-default" />
				</a>

			</div>
			<!-- END LOGO -->

		</div>
		<!-- END HEADER INNER -->
	</div>
	<!-- END HEADER -->
	<div class="clearfix"></div>
	<!-- BEGIN CONTAINER -->
	<div class="page-container">
		<!-- BEGIN CONTENT -->
		<!-- 		<div class="page-content-wrapper"> -->
		<div class="page-content">
			<!-- BEGIN PAGE HEAD -->
			<div class="page-head">
				<!-- BEGIN PAGE TITLE -->
				<div class="page-title">
					<h1></h1>
				</div>
				<!-- END PAGE TITLE -->
			</div>
			<!-- END PAGE HEAD -->
			<c:choose>



				<c:when test="${payPaypal != null}">

					<!-- BEGIN PAGE BREADCRUMB -->
					<!-- BEGIN PAGE CONTENT-->
					<div class="row">
						<div class="col-md-12">

							<c:if test="${payPaypal== true}">

								<div class="alert alert-success">
									<h3>
										<strong>Payment complete.</strong>
									</h3>
									<p>
										<strong>Thank you for your order. </strong>
									</p>
									<p>You will see the invoice in Billing menu. Your payment
										reference is ${payerID}</p>
									<p>It is now processing. We will shortly initialize your
										Livo platform and send you and email which includes details to
										access the platform.</p>
									<%-- 									Payment Completed for Payer ID : <strong>${payerID} </strong> --%>
									<!-- 									<p> -->
									<!-- 										Payment Status: <strong>Approved.</strong> -->
									<!-- 									<p> -->
									<!-- 									<h3> -->
									<!-- 										Platform Plan :<strong> Enterprise</strong> -->
									<!-- 									</h3> -->
									<!-- 									<p> -->
									<!-- 										Application Limit :<strong> 10</strong> -->
									<!-- 									<p> -->
									<!-- 										Developer Limit : <strong>10</strong> -->
									<!-- 									<p> -->
									<!-- 										Client Limit: <strong>10</strong> -->
									<!-- 									<p> -->
								</div>


								<div class="alert alert-success">

									<%-- 									<form:form method="post" action="createInstance"> --%>

									<!-- 										<div class="form-actions"> -->
									<a href="/LivoCloud/createInstance"><button type="button"
											id="createInstanceButton" name="createInstanceButton"
											class="btn blue pull-right">
											Create Instance Now <i class="m-icon-swapright m-icon-white"></i>
										</button></a>
									<!-- 										</div> -->
									<!-- 										<input type="button" name="createInstanceButton" -->
									<!-- 											alt="Create Instance" value="Create Instance Now" /> -->
									<%-- 									</form:form> --%>
									<h3>
										Instance Id :<strong> <span id="instanceIp">Creating
												instance..</span></strong>
									</h3>
									<p>
									<h3>
										<!-- 										<strong>We always are dreaming high for you. Lets -->
										<!-- 											create amazing apps together.</strong> -->
									</h3>
									<p>
								</div>



							</c:if>

							<c:if test="${payPaypal== false}">
								<div class="alert alert-danger">

									<strong>The payment failure.</strong>

								</div>
							</c:if>
						</div>

					</div>
					<!-- END PAGE CONTENT-->

					<!-- END PAGE BREADCRUMB -->
					<!-- BEGIN PAGE CONTENT INNER -->
					<!-- END PAGE CONTENT INNER -->




				</c:when>
				<c:otherwise>
					<!-- BEGIN PAGE BREADCRUMB -->
					<!-- BEGIN PAGE CONTENT-->
					<div class="row">
						<div class="col-md-12">
							<!-- BEGIN INLINE NOTIFICATIONS PORTLET-->
							<div class="portlet light">
								<div class="portlet-title">
									<div class="caption">
										<i class="fa fa-cogs"></i>PRICING
									</div>
									<!-- 								<div class="tools"> -->
									<!-- 									<a href="javascript:;" class="collapse"> </a> <a -->
									<!-- 										href="#portlet-config" data-toggle="modal" class="config"> -->
									<!-- 									</a> <a href="javascript:;" class="reload"> </a> <a -->
									<!-- 										href="javascript:;" class="remove"> </a> -->
									<!-- 								</div> -->
								</div>
								<div class="portlet-body">
									<div class="row margin-bottom-40">
										<!-- Pricing -->
										<div class="col-md-4">
											<div class="pricing hover-effect">
												<div class="pricing-head pricing-head-active">
													<h3>Developer Plan</h3>
													<h4>
														<i>Free</i> <span> A Month </span>
													</h4>
												</div>
												<ul class="pricing-content list-unstyled">
													<li><i class="fa fa-tags"></i> IDE</li>
													<li><i class="fa fa-asterisk"></i>Mobile Container</li>
													<li><i class="fa fa-heart"></i> Up To 1 App</li>
													<li><i class="fa fa-star"></i> 100 MB Space</li>

												</ul>
												<div class="pricing-footer">
													<p></p>

													<a href='#'><input type="button"
														class="btn btn-primary" name="devPlanButton"
														alt="Developer Plan" value="Start Now"></a>
												</div>
											</div>
										</div>
										<div class="col-md-4">
											<div class="pricing hover-effect">
												<div class="pricing-head">
													<h3>Professional Plan</h3>
													<h4>
														<i>$</i>65<span> Per Month </span>
													</h4>
												</div>
												<ul class="pricing-content list-unstyled">
													<li><i class="fa fa-tags"></i> Basic Auth</li>
													<li><i class="fa fa-asterisk"></i> Up To 3 App</li>
													<li><i class="fa fa-heart"></i> 1000 MB Space</li>
													<li><i class="fa fa-star"></i> Up To 1 Service
														Integration</li>
													<li><i class="fa fa-shopping-cart"></i> Standart
														Support</li>
													<li><i class="fa fa-shopping-cart"></i> Weekly Backup</li>
												</ul>
												<div class="pricing-footer">
													<p></p>
													<!-- 													<form -->
													<!-- 														action="https://www.sandbox.paypal.com/cgi-bin/webscr" -->
													<!-- 														method="post" target="_blank"> -->
													<!-- 														<input type="hidden" name="cmd" value="_s-xclick"> -->
													<!-- 														<input type="hidden" name="encrypted" -->
													<!-- 															value="-----BEGIN PKCS7-----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-----END PKCS7-----"> -->
													<!-- 														<input type="image" -->
													<!-- 															src="https://www.sandbox.paypal.com/en_US/i/btn/btn_buynowCC_LG.gif" -->
													<!-- 															border="0" name="submit" -->
													<!-- 															alt="PayPal - The safer, easier way to pay online!"> -->
													<!-- 														<img alt="" border="0" -->
													<!-- 															src="https://www.sandbox.paypal.com/en_US/i/scr/pixel.gif" -->
													<!-- 															width="1" height="1"> -->
													<!-- 													</form> -->
													<a href='${redirectPaypalUrlforProfessional}'> <input
														type="button" class="btn btn-primary" name="proPlanButton"
														alt="Professional Plan" value="Buy Now">
													</a>


												</div>
											</div>
										</div>
										<div class="col-md-4">
											<div class="pricing hover-effect">
												<div class="pricing-head">
													<h3>Enterprise Plan</h3>
													<h4>
														<i>$</i>125<span> Per Month </span>
													</h4>
												</div>
												<ul class="pricing-content list-unstyled">
													<li><i class="fa fa-tags"></i> AD Auth</li>
													<li><i class="fa fa-asterisk"></i> Up To 10 App</li>
													<li><i class="fa fa-heart"></i> 10.000 MB Space</li>
													<li><i class="fa fa-star"></i> Unlimited Service
														Integration</li>
													<li><i class="fa fa-shopping-cart"></i> Premium
														Support</li>
													<li><i class="fa fa-heart"></i> Daily Backup</li>
												</ul>
												<div class="pricing-footer">
													<p></p>

													<a href='${redirectPaypalUrlforEnterprice}'> <input
														type="button" class="btn btn-primary"
														name="enterprisePlanButton" alt="Enterprise Plan"
														value="Buy Now">

													</a>
												</div>
											</div>

										</div>
										<a href="/LivoCloud/dashboard" class="pull-right"
											style="margin-right: 25px;"> <i class="icon-home "></i> <span
											class="title">Go to Dashboard</span>
										</a>
										<!--//End Pricing -->
									</div>
								</div>
							</div>
							<!-- END INLINE NOTIFICATIONS PORTLET-->
						</div>


					</div>
					<!-- END PAGE CONTENT-->

					<!-- END PAGE BREADCRUMB -->
					<!-- BEGIN PAGE CONTENT INNER -->
					<!-- END PAGE CONTENT INNER -->

				</c:otherwise>

			</c:choose>

		</div>
		<!-- END CONTENT -->
		<!-- 		</div> -->
	</div>
	<!-- END CONTAINER -->


	<!-- BEGIN FOOTER -->
	<!-- 	<div class="page-footer-fixed"> -->
	<!-- 		<div class="page-footer-inner"> -->
	<!-- 			2014 &copy; Metronic by keenthemes. <a -->
	<!-- 				href="http://themeforest.net/item/metronic-responsive-admin-dashboard-template/4021469?ref=keenthemes" -->
	<!-- 				title="Purchase Metronic just for 27$ and get lifetime updates for free" -->
	<!-- 				target="_blank">Purchase Metronic!</a> -->
	<!-- 		</div> -->
	<!-- 		<div class="scroll-to-top"> -->
	<!-- 			<i class="icon-arrow-up"></i> -->
	<!-- 		</div> -->
	<!-- 	</div> -->
	<!-- END FOOTER -->


	<!-- BEGIN JAVASCRIPTS(Load javascripts at bottom, this will reduce page load time) -->
	<!-- BEGIN CORE PLUGINS -->
	<!--[if lt IE 9]>
<script src="<c:url value="/resources/adminpanel/global/plugins/respond.min.js"/>" ></script>
<script src="<c:url value="/resources/adminpanel/global/plugins/excanvas.min.js"/>" ></script> 
<![endif]-->
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/jquery.min.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/jquery-migrate.min.js"/>"
		type="text/javascript"></script>
	<!-- IMPORTANT! Load jquery-ui.min.js before bootstrap.min.js to fix bootstrap tooltip conflict with jquery ui tooltip -->
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/jquery-ui/jquery-ui.min.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/bootstrap/js/bootstrap.min.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/bootstrap-hover-dropdown/bootstrap-hover-dropdown.min.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/jquery-slimscroll/jquery.slimscroll.min.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/jquery.blockui.min.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/jquery.cokie.min.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/uniform/jquery.uniform.min.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/bootstrap-switch/js/bootstrap-switch.min.js"/>"
		type="text/javascript"></script>
	<!-- END CORE PLUGINS -->
	<!-- BEGIN PAGE LEVEL PLUGINS -->
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/jqvmap/jqvmap/jquery.vmap.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/jqvmap/jqvmap/maps/jquery.vmap.russia.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/jqvmap/jqvmap/maps/jquery.vmap.world.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/jqvmap/jqvmap/maps/jquery.vmap.europe.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/jqvmap/jqvmap/maps/jquery.vmap.germany.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/jqvmap/jqvmap/maps/jquery.vmap.usa.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/jqvmap/jqvmap/data/jquery.vmap.sampledata.js"/>"
		type="text/javascript"></script>
	<!-- IMPORTANT! fullcalendar depends on jquery-ui.min.js for drag & drop support -->
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/morris/morris.min.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/morris/raphael-min.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/global/plugins/jquery.sparkline.min.js"/>"
		type="text/javascript"></script>
	<!-- END PAGE LEVEL PLUGINS -->
	<!-- BEGIN PAGE LEVEL SCRIPTS -->
	<script
		src="<c:url value="/resources/adminpanel/global/scripts/metronic.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/admin/layout4/scripts/layout.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/admin/layout4/scripts/demo.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/admin/pages/scripts/index3.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/admin/pages/scripts/tasks.js"/>"
		type="text/javascript"></script>
	<script
		src="<c:url value="/resources/adminpanel/admin/layout4/scripts/custom.js"/>"
		type="text/javascript"></script>

	<!-- END PAGE LEVEL SCRIPTS -->
	<script>
		jQuery(document).ready(function() {
			Metronic.init(); // init metronic core componets
			Layout.init(); // init layout
			Demo.init(); // init demo features 
			Index.init(); // init index page
			Tasks.initDashboardWidget(); // init tash dashboard widget  
		});
	</script>
	<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>


