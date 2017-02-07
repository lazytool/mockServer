var jtab = new jeasyui.Tabs('tabs', 10);
function AddTab(obj) {
	jtab.addTab(obj.title, $(obj).attr("url"));
}
function CloseCurrTab() {
	var title = $('#hidCurrTab').val();
	jtab.CloseTab(title);
}
function ReloadTab() {
	var selectedTab = jtab.getSelected();
	jtab.refresh(selectedTab);
}
function CloseAllTabExceptThis() {
	var title = $('#hidCurrTab').val();
	jtab.CloseAllTabExceptThis(title);
}
$(document).ready(function() {
	// 构建手风琴
	var aaOptions = {
		fit : true,
		border : false
	};
	$('#aa').accordion(aaOptions);
	// 构建选项卡
	var tabsOptions = {
		fit : true,
		tools : '#tab-tools',
		onContextMenu : function(e, title) {
			e.preventDefault();
			if (title == "首页") {
				$('#mm-closeone').attr('style', 'display:none');
			} else {
				$('#mm-closeone').attr('style', '');
			}
			$('#mm').menu('show', {
				left : e.pageX,
				top : e.pageY
			})
			$('#hidCurrTab').val(title);
		},
		onAdd : function(title) {
			jtab.currTabCount++;
			if (jtab.currTabCount > jtab.maxlength) {
				jtab.autoCloseTab();
			}
		},
		onClose : function(title) {
			jtab.currTabCount--;
		}
	};
	$('#tabs').tabs(tabsOptions);
	$('.nav').click(function() {
		AddTab(this);
	});
	// 加载说明页到首页标签
	$('#main').load("man.html");
});