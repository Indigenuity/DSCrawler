$(document).ready(function() {
				$(".accordion").accordion({
			      collapsible: true,
			      active: false
			    });
				
				$(".single-action-container button").click(function() {
					$(this).parents(".single-action-container").slideUp(50);
				})
				
				$(".site-brief button").click(function() {
					$(this).parents(".site-brief").slideUp(50);
				});
				
				$(".site-crawls button").click(function() {
					$(this).parents("tr").slideUp(50);
				})
				
				$(".faux-submit").submit(function(event) {
					var form = $(this);
					$.post(form.attr("action"), form.serialize());
					form.slideUp(50);
					return false;
				});
				
				$(".hide-from-matt-button").click(function() {
					$(this).parents(".site-group-url-wrapper").slideUp(50);
				})
				$(".hide-from-travis-button").click(function() {
					$(this).parents(".site-group-url-wrapper").slideUp(50);
					var count = 0;
					while(true){
						$("html").animate({"background-color":"black"}, 5);
						$("html").animate({"background-color":"white"}, 5);
					}
				})
				//$("#jobFormSubmit").click(function() {
				//	var serialized = $("#jobForm").serializeArray();
					//alert("serialized : " + serialized);
					//var stringified = JSON.stringify(serialized);
				//	$.ajax ("/crawlSetWork", {
				//		data : serialized,
				//		type: 'POST'
				//	});
				//});
			});

			function combineOnDomain(siteId){
				var endpoint = "/combineOnDomain?siteId=" + siteId;
				
				$.get(endpoint);
			}
			
			function continueCrawlSet(button, crawlSetId){
				var numToProcess = $(button).parent().find(".continue-crawl-set-input").val();
				var endpoint = "/continueCrawlSet?numToProcess=" + numToProcess + "&crawlSetId=" + crawlSetId;
				$.get(endpoint);
			}
			function setWebProvider(siteCrawlId, webProviderId) {
				$.get("/setWebProvider?siteCrawlId=" + siteCrawlId + "&webProviderId=" + webProviderId);
			}
			function makeSite(button, dealerId) {
				var url = encodeURI($(button).prev(".site-input").val());
				$(button).next(".make-site-results").load("/makeSite?dealerId=" + dealerId + "&url=" + url, function(responseText, textStatus, jqXHR) {
					$(button).next(".make-site-results").html(responseText);
				})
			}
			function hideFromMatt(siteId) {
				$.get("/hideFromMatt?siteId=" + siteId)
			}
			function addGroupUrl(button, siteId) {
				var url = encodeURI($(button).prev(".group-url-input").val());
				alert("url : " + url);
				$.get("/addGroupUrl?siteId=" + siteId + "&url=" + url);
			}