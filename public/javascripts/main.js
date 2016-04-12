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
	});
	
	$(".faux-submit").submit(function(event) {
		var form = $(this);
		$.post(form.attr("action"), form.serialize());
		form.parents(".faux-submit-container").slideUp(50);
		return false;
	});
	
	
	
	$(".accept-temp").click(function() {
		var form = $(this).parents(".single-action-container").find("form");
		var siteContainer = $(this).parents(".indented-container");
		var input = form.find("input[name=suggestedUrl]");
		var inputSource = form.find("input[name=suggestedSource]");
		var value = siteContainer.find(".site-homepage").text();
		input.val(value);
		inputSource.val("Domain similarity to Redirect URL");
		form.addClass("faux-submit");
		form.submit()
	});
	$(".checkbox-container").click(function() {
		alert("here");
		var checkbox = $(this).find("input[type=checkbox]");
		alert("checkbox : " + checkbox);
		if(checkbox.prop('checked', true)){
			checkbox.prop('checked', false);
		}else {
			checkbox.prop('checked', true);
		}
	});
	
	/***************************   Task Reviewing ***********************/
	$(".approve-resolved-button").click(function() {
		var form = this.form
		$(form).find("input[name='action']").val("APPROVE_RESOLVED");
	});
	$(".manual-seed-button").click(function() {
		var form = this.form
		$(form).find("input[name='action']").val("MANUAL_SEED");
	});
	$(".shared-site-button").click(function() {
		var form = this.form;
		$(form).find("input[name='sharedSite']").val("true");
		$(form).find("input[name='action']").val("APPROVE_RESOLVED");
	});
	$(".more-button").click(function() {
		var form = this.form
		$(form).find("input[name='action']").val("MORE_WORK");
	});
	$(".mark-defunct-button").click(function() {
		var form = this.form
		$(form).find("input[name='action']").val("MARK_DEFUNCT");
	});
	$(".recheck-button").click(function() {
		var form = this.form
		$(form).find("input[name='action']").val("RECHECK");
	});
	
	/*******************  End Task Reviewing *********************/
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
			function addGroupUrl(button, siteId) {
				var url = encodeURI($(button).prev(".group-url-input").val());
				alert("url : " + url);
				$.get("/addGroupUrl?siteId=" + siteId + "&url=" + url);
			}