$(function() {
	// 全局遮罩
	$(".overall_mask").on("click", function() {
		$.fn.jqLoading({
			height : 100,
			width : 240,
			text : "正在加载中,请耐心等待...."
		});
	});
	
	$('._delete').click(function() {
		return confirm("删除后不可恢复,是否确认删除?");
	});
})