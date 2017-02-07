$(document)
		.ready(
				function() {
					// ip注册过提示更新
					var status = $('#status').val();
					var deviceInDB = $('#deviceInDB').val();

					if (status == 1) {
						alert("该设备 " + deviceInDB + " 已被注册过,不能重复注册");
					} else if (status == 2) {
						var r = confirm("该设备 " + deviceInDB + " 已被注册过,是否更新ip->"
								+ $('#ip').val());
						if (r == true) {
							var f = document.getElementById("showRegister");
							f.action = "updateDevice.do";
							f.submit();
						}
					}
					// 获取设备型号
					$("#ip").blur(function() {
										var ip = $("#ip").val();
										var ipformat = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/; // IP地址格式正则
										if (ip.match(ipformat)) {
											$("#deviceModelTip").show();
											$("#deviceModel").hide();
											var getDeviceModel=$.ajax({type : "POST",
												    url : "getDeviceModelByIp.do",
												    timeout : 20000, //超时时间设置,单位毫秒
													data : "ip=" + ip,
													dataType:"json",
													success : function(res) {//成功返回
														    $("#deviceModelTip").hide();
														    $("#deviceModel").show();
															if(res.status == "1"){//查询成功,填充设备型号
																$("#deviceModel").val(res.msg);
															}else{//查询失败,清空input
																alert(res.msg+";请检查ip或重新连接");
																$("#deviceModel").val("");
															}
													},
													error: function(xmlHttpRequest,status,e){
														//alert("status="+status+";errorMsg="+e);
														if(status=='timeout'){//请求超时
															getDeviceModel.abort();
															$("#deviceModelTip").hide();
															$("#deviceModel").show();
															$("#deviceModel").val("");
															alert("获取设备型号请求超时!请检查ip或自定义设备型号");
														}else{//其他错误:status=null/error/notmodified/parsererror
															$("#deviceModelTip").hide();
															$("#deviceModel").show();
															$("#deviceModel").val("");
															alert("获取设备型号失败!请检查ip或自定义设备型号");
														}
													}
													
											});
										}
									});

				})
	/** 去除字符串空格(设置第二个参数is_global为g就可以去除所有空格) */
	function Trim(str, is_global) {
		var result;
		result = str.replace(/(^\s+)|(\s+$)/g, "");
		if (is_global.toLowerCase() == "g")
			result = result.replace(/\s/g, "");
		return result;
	}
