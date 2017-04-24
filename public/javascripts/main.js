$(document).ready(function() {
	
	
	
	localEventBindings(document);
	
	/************************  Salesforce Sync *************************/
	
	$("div[data-syncType]").each(function(){
		$(this).load("/syncList?syncType=" + $(this).attr("data-syncType"), function(){
			localEventBindings(this);
		});
	});
	
	$(".load-dashboard").each(function() {
		$(this).load($(this).attr("data-dashboard-href"), function() {
			localEventBindings(this);
		});
	})
	
	$(".relevant-salesforce-accounts").each(function() {
		$(this).load("/salesforce/siteMismatchForms?siteId=" + $(this).attr("data-siteId"), function() {
			localEventBindings(this);
		});
	})
	
});

	function localEventBindings(scope) {
		$(scope).find("*").unbind();
		
		/************************* General *****************************/
		$(scope).find("#overlay").click(function() {
			closeLoadingOverlay().fadeOut();
		});
		$(scope).find(".accordion").accordion({
	      collapsible: true,
	      active: false
	    });
		
		//**********************  Faux Forms *******************************/
		$(scope).find(".single-action-container button").click(function() {
			$(this).parents(".single-action-container").slideUp(50);
		})
		
		$(scope).find(".site-brief button").click(function() {
			$(this).parents(".site-brief").slideUp(50);
		});
		
		$(scope).find(".site-crawls button").click(function() {
			$(this).parents("tr").slideUp(50);
		});
		
		$(scope).find(".faux-submit").submit(function(event) {
			var form = $(this);
			$.post(form.attr("action"), form.serialize());
			form.parents(".faux-submit-container").slideUp(50);
			return false;
		});
		
		$(scope).find(".faux-submit-form").submit(function(event) {
			var form = $(this);
			openLoadingOverlay();
			$.post(form.attr("action"), form.serialize()).done( function(jqXHR, textStatus, response) {
				successfulOverlay(response.responseText);
			}).fail(function(jqXHR, textStatus, response) {
				failureOverlay("When visiting url (" + url + "), received response : " + response);
			});
			return false;
		});
		
		$(scope).find(".accept-temp").click(function() {
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
		
		$(scope).find(".checkbox-container").click(function(e) {
			var checkbox = $(this).find("input[type=checkbox]");
			if(checkbox.prop('checked') == true){
				checkbox.prop('checked', false);
			}else {
				checkbox.prop('checked', true);
			}
		});
		
		$(scope).find(".checkbox-container input[type=checkbox]").click(function(e) {
			e.stopPropagation();
		});
		
		$(scope).find(".mini-link-form").submit(function(e) {
			var link = $(this).attr("data-link");
			var input = $(this).find(".mini-link-append").val();
			link += input;
//			alert(link);
//			
//			sleep(5);
//			e.preventDefault();
			document.location.href = link;
			return false;
		});
		
		//********************** Ajax Utilities *******************
		$(scope).find("a.in-page-link").click(function(event) {
			event.preventDefault();
			var url = $(this).attr("href");
//			alert("url : " + url);
			openLoadingOverlay();
			$.ajax(url).done( function(jqXHR, textStatus, response) {
				successfulOverlay(response.responseText);
			}).fail(function(jqXHR, textStatus, response) {
				failureOverlay("When visiting url (" + url + "), received response : " + response);
			});
			return false;
		});
		
		/***************************   Task Reviewing ***********************/
		/*
		$(scope).find(".approve-resolved-button").click(function() {
			var form = this.form
			$(form).find("input[name='action']").val("APPROVE_RESOLVED");
			$(form).attr("action", "/review/submit/approveResolved");
		});
		$(scope).find(".manual-seed-button").click(function() {
			var form = this.form
			$(form).find("input[name='action']").val("MANUAL_SEED");
			$(form).attr("action", "/review/submit/manuallySeedSalesforceAccount");
			
		});
		$(scope).find(".manual-redirect-button").click(function() {
			var form = this.form
			$(form).attr("action", "/review/submit/manuallyRedirectSalesforceAccount");
			
		});
		$(scope).find(".shared-site-button").click(function() {
			var form = this.form;
			$(form).find("input[name='sharedSite']").val("true");
			$(form).find("input[name='action']").val("APPROVE_SHARED");
			$(form).attr("action", "/review/submit/approveShared");
		});
		$(scope).find(".more-button").click(function() {
			var form = this.form
			$(form).find("input[name='action']").val("MORE_WORK");
		});
		$(scope).find(".mark-defunct-button").click(function() {
			var form = this.form
			$(form).find("input[name='action']").val("MARK_DEFUNCT");
			$(form).attr("action", "/review/submit/markDefunct");
		});
		$(scope).find(".recheck-button").click(function() {
			var form = this.form
			$(form).find("input[name='action']").val("RECHECK");
			$(form).attr("action", "/review/submit/recheck");
		});
		$(scope).find(".other-issue-button").click(function() {
			var form = this.form
			$(form).find("input[name='action']").val("OTHER_ISSUE");
			$(form).attr("action", "/review/submit/otherIssue");
		});
		
		*/
	}

	function openLoadingOverlay() {
		$("#overlay .message").html("");
		$("#overlay .loading").show();
		$("#overlay .success").hide();
		$("#overlay .failure").hide();
		
		$("#overlay").show();
	}
	
	function successfulOverlay(message) {
		$("#overlay .message").html("Success : " + message);
		$("#overlay .loading").fadeOut();
		$("#overlay .failure").fadeOut();
		$("#overlay .success").fadeIn();
	}
	function failureOverlay(message) {
		$("#overlay .message").html("Request failed : " + message);
		$("#overlay .loading").fadeOut();
		$("#overlay .success").fadeOut();
		$("#overlay .failure").fadeIn();
	}
	
	function closeLoadingOverlay() {
		$("#overlay").hide();
	}
	
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