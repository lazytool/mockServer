var jeasyui = {
	Tabs : {}, // 选项卡
	Messager : {}, // 消息框
	Redirect : {}
// 重定向
};
// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*
 * 选项卡 id easyui标签的ID maxlength 设置选项卡最大个数
 */
jeasyui.Tabs = function(id, maxlength) {
	this.id = id;
	this.maxlength = maxlength;
	this.currTabCount = 1;
	// 自动关闭选项卡函数
	this.autoCloseTab = function() {
		$('#' + this.id).tabs('close', 1);
	};
	// 关闭指定选项卡函数
	this.CloseTab = function(title) {
		$('#' + this.id).tabs('close', title);
	};
	// 关闭除选中外所有选项卡函数
	this.CloseAllTabExceptThis = function(title) {
		var alltabs = $('#' + this.id).tabs('tabs');
		var currtab = $('#' + this.id).tabs("getTab", title);
		var titlelist = new Array();
		var listcount = 0;
		for (var i = 0; i < alltabs.length; i++) {
			if (alltabs[i] != currtab
					&& alltabs[i].panel('options').title != "首页") {
				titlelist[listcount] = alltabs[i].panel('options').title;
				listcount++;
			}
		}
		for (var j = 0; j < listcount; j++) {
			$('#' + this.id).tabs('close', titlelist[j]);
		}
	};
};
// 添加一个选项卡
jeasyui.Tabs.prototype.addTab = function(titleName, url) {
	if (!this.exists(titleName)) {
		var iframe = $('<iframe style="width:100%;height:100%;border:0"/>');
		$('#' + this.id).tabs('add', {
			title : titleName,
			content : iframe,
			// href:url,
			closable : true,
			cache : true,
			fit : true
		});
		iframe.attr('src', url);
	} else {
		this.selectTab(titleName);
	}
};
// 选中指定选项卡（参数titleName:选项卡标题名）
jeasyui.Tabs.prototype.selectTab = function(titleName) {
	$('#' + this.id).tabs('select', titleName);
};
// 获取当前选项卡
jeasyui.Tabs.prototype.getSelected = function() {
	return $('#' + this.id).tabs('getSelected');
};
// 刷新选项卡（参数tab:选项卡）
jeasyui.Tabs.prototype.refresh = function(tab) {
	var url = $(tab.panel('options').content).attr('src');
	// tab.panel('refresh', url);//本方法只能重加载body内的内容,不能加载整个html,改成下面方式刷新iframe
	self.parent.$('#tabs').tabs(
			'update',
			{
				tab : tab,
				options : {
					content : $('<iframe src="' + url
							+ '" style="width:100%;height:100%;border:0"/>')
				}
			});
};
// 验证选项卡是否存在（参数titleName:选项卡标题名）
jeasyui.Tabs.prototype.exists = function(titleName) {
	var tab = $('#' + this.id).tabs('exists', titleName);
	return tab;
};

// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
jeasyui.Messager = {};

// 弹出提示信息
jeasyui.Messager.Alert = function(title, msg, type) {
	$.messager.alert(title, msg, type);
};

// 弹出提示确认后重定向
jeasyui.Messager.ConfirmAndRedirect = function(title, msg, url) {
	$.messager.confirm(title, msg, function(r) {
		if (r) {
			location.href = url;
		}
	});
};

// 弹出提示确认后关闭窗口
jeasyui.Messager.ConfirmAndClose = function(title, msg) {
	$.messager.confirm(title, msg, function(r) {
		if (r) {
			window.close();
			parent.location.href = parent.location.href;
		}
	});
};

// 弹出提示信息后父窗体重定向
jeasyui.Messager.MRedirect = function(title, msg, url) {
	$.messager.alert(title, msg, 'info', function() {
		location.href = url;
	});
};

// 弹出提示信息后重定向
jeasyui.Messager.Redirect = function(title, msg, url) {
	$.messager.alert(title, msg, 'info', function() {
		var selectedTab = $('#tabs').tabs('getSelected');
		selectedTab.panel('refresh', url);
	});
};

// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// 重定向
jeasyui.Redirect = {};

jeasyui.Redirect.TabRedirect = function(url) {
	var selectedTab = $('#tabs').tabs('getSelected');
	selectedTab.panel('refresh', url);
};